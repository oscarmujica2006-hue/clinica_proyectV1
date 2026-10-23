-- ============================================================
-- FUNCIÓN: fn_descontar_cupo_y_crear_recibo (CORREGIDA)
-- Correcciones aplicadas:
--   BUG 1: El paso 9 (totales) estaba FUERA del IF NOT EXISTS
--          del paso 8 -> doble conteo si el detalle ya existía.
--          Ahora los totales se actualizan solo al insertar detalle.
--   BUG 2: Si no hay consentimiento, se creaba un recibo huérfano
--          con id_consentimiento = NULL invisible para la app.
--          Ahora: búsqueda e inserción por id_evolucion_clinica
--          cuando el consentimiento no existe.
-- ============================================================
CREATE OR REPLACE FUNCTION public.fn_descontar_cupo_y_crear_recibo()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
    v_id_tratamiento INTEGER;
    v_cupos_disponibles INTEGER;
    v_id_cupo INTEGER;
    v_cupo_total INTEGER;
    v_id_materia INTEGER;
    v_nombre_tratamiento VARCHAR(100);
    v_usuario_actual INTEGER;
    v_id_estudiante INTEGER;
    v_nombre_estudiante VARCHAR(200);
    v_precio_tratamiento DECIMAL(10,2);
    v_id_recibo BIGINT;
    v_id_detalle_recibo BIGINT;
    v_id_paciente INTEGER;
    v_id_evolucion_clinica INTEGER;
    v_id_consentimiento INTEGER;
    v_nombre_paciente VARCHAR(200);
    v_cupo_realizado INTEGER;
    v_cupo_pendiente INTEGER;
    v_nuevo_estado VARCHAR(20);
BEGIN
    IF NEW.estado IN ('APROBADO', 'COMPLETADO')
       AND OLD.estado IN ('PENDIENTE', 'PENDIENTE_APROBACION', 'REALIZADO') THEN

        -- 1. Datos del tratamiento
        SELECT dt.id_tratamiento, t.nombre_tratamiento, t.precio_tratamiento, dt.id_evolucion_clinica
        INTO v_id_tratamiento, v_nombre_tratamiento, v_precio_tratamiento, v_id_evolucion_clinica
        FROM diagnostico_tratamiento dt
        JOIN tratamiento t ON dt.id_tratamiento = t.id_tratamiento
        WHERE dt.id_diag_trat = NEW.id_diagnostico_tratamiento;

        -- 2. Estudiante y paciente
        SELECT c.id_estudiante,
               CONCAT(per.nombre, ' ', per.apellido_paterno),
               c.id_paciente,
               CONCAT(per2.nombre, ' ', per2.apellido_paterno)
        INTO v_id_estudiante, v_nombre_estudiante, v_id_paciente, v_nombre_paciente
        FROM diagnostico_tratamiento dt
        JOIN evolucion_clinica ec ON dt.id_evolucion_clinica = ec.id_evolucion_clinica
        JOIN diagnostico d ON ec.id_diagnostico = d.id_diagnostico
        JOIN revision r ON d.id_revision = r.id_revision
        JOIN consulta c ON r.id_consulta = c.id_consulta
        JOIN usuario_estudiante ue ON c.id_estudiante = ue.id_estudiante
        JOIN usuario u ON ue.id_usuario = u.id_usuario
        JOIN persona per ON u.id_persona = per.id_persona
        JOIN paciente pac ON c.id_paciente = pac.id_paciente
        JOIN persona per2 ON pac.id_persona = per2.id_persona
        WHERE dt.id_diag_trat = NEW.id_diagnostico_tratamiento;

        -- 3. Consentimiento
        BEGIN
            SELECT co.id_consentimiento INTO v_id_consentimiento
            FROM consentimiento co
            WHERE co.id_diag_trat = NEW.id_diagnostico_tratamiento
            LIMIT 1;
        EXCEPTION WHEN OTHERS THEN
            v_id_consentimiento := NULL;
        END;

        -- 4. Usuario actual
        BEGIN
            SELECT id_usuario INTO v_usuario_actual FROM usuario WHERE user_name = CURRENT_USER;
        EXCEPTION WHEN OTHERS THEN
            v_usuario_actual := 1;
        END;

        -- 5. Descontar cupo de la clinica (logica sin cambios)
        UPDATE cupo c
        SET cupos_disponibles = cupos_disponibles - 1,
            fech_mod_cup = CURRENT_TIMESTAMP,
            usu_mod_cup = COALESCE(v_usuario_actual, 1)
        WHERE c.id_tratamiento = v_id_tratamiento
          AND c.cupos_disponibles > 0
        RETURNING c.cupos_disponibles, c.id_cupo
        INTO v_cupos_disponibles, v_id_cupo;

        IF NOT FOUND THEN
            RAISE EXCEPTION 'No hay cupos disponibles para el tratamiento: %', v_nombre_tratamiento;
        END IF;

        -- 5b. Meta fija y materia del tratamiento del estudiante
        SELECT c.cupos_totales, c.id_materia
        INTO v_cupo_total, v_id_materia
        FROM cupo c
        WHERE c.id_tratamiento = v_id_tratamiento
        LIMIT 1;

        IF v_id_materia IS NULL THEN
            RAISE EXCEPTION 'No hay materia configurada para el tratamiento: %', v_nombre_tratamiento;
        END IF;

        -- 6. UPSERT del progreso del estudiante
        INSERT INTO progreso_cupo_estudiante (
            id_estudiante, id_tratamiento, id_materia,
            cupo_total, cupo_realizado, cupo_pendiente,
            estado, fecha_inicio, fecha_completado
        ) VALUES (
            v_id_estudiante, v_id_tratamiento, v_id_materia,
            COALESCE(v_cupo_total, 0),
            1,
            GREATEST(COALESCE(v_cupo_total, 0) - 1, 0),
            CASE WHEN COALESCE(v_cupo_total, 0) - 1 <= 0 THEN 'COMPLETADO' ELSE 'EN_PROGRESO' END,
            CURRENT_TIMESTAMP,
            CASE WHEN COALESCE(v_cupo_total, 0) - 1 <= 0 THEN CURRENT_TIMESTAMP ELSE NULL END
        )
        ON CONFLICT (id_estudiante, id_tratamiento) DO UPDATE
        SET cupo_total     = EXCLUDED.cupo_total,
            cupo_realizado = progreso_cupo_estudiante.cupo_realizado + 1,
            cupo_pendiente = GREATEST(EXCLUDED.cupo_total - (progreso_cupo_estudiante.cupo_realizado + 1), 0),
            estado = CASE
                        WHEN EXCLUDED.cupo_total - (progreso_cupo_estudiante.cupo_realizado + 1) <= 0
                        THEN 'COMPLETADO' ELSE 'EN_PROGRESO'
                     END,
            fecha_completado = CASE
                        WHEN EXCLUDED.cupo_total - (progreso_cupo_estudiante.cupo_realizado + 1) <= 0
                        THEN CURRENT_TIMESTAMP ELSE progreso_cupo_estudiante.fecha_completado
                     END,
            id_materia = EXCLUDED.id_materia
        RETURNING cupo_realizado, cupo_pendiente, estado
        INTO v_cupo_realizado, v_cupo_pendiente, v_nuevo_estado;

        -- ============================================================
        -- 7. Crear o actualizar recibo  [CORREGIDO]
        --    Antes: solo buscaba/creaba por id_consentimiento (si era
        --    NULL quedaba un recibo invisible para la app).
        --    Ahora: sin consentimiento, se busca/crea por
        --    id_evolucion_clinica.
        -- ============================================================
        IF v_id_consentimiento IS NOT NULL THEN
            SELECT id_recibo INTO v_id_recibo
            FROM recibo
            WHERE id_consentimiento = v_id_consentimiento;
        ELSIF v_id_evolucion_clinica IS NOT NULL THEN
            SELECT id_recibo INTO v_id_recibo
            FROM recibo
            WHERE id_evolucion_clinica = v_id_evolucion_clinica;
        END IF;

        IF v_id_recibo IS NULL THEN
            INSERT INTO recibo (id_consentimiento, id_evolucion_clinica, estado_pago,
                subtotal_tratamientos, subtotal_radiografias, monto_total,
                monto_pagado, saldo_pendiente, usu_reg_rec, fech_reg_rec)
            VALUES (v_id_consentimiento,
                CASE WHEN v_id_consentimiento IS NULL THEN v_id_evolucion_clinica ELSE NULL END,
                'PENDIENTE', 0, 0, 0, 0, 0,
                COALESCE(v_usuario_actual, 1), CURRENT_TIMESTAMP)
            RETURNING id_recibo INTO v_id_recibo;
        END IF;

        -- ============================================================
        -- 8. Detalle del recibo + 9. Totales  [CORREGIDO]
        --    El paso 9 estaba fuera del IF NOT EXISTS: si el detalle
        --    ya existía se sumaba el precio dos veces.
        --    Ahora totales solo se actualizan al insertar el detalle.
        -- ============================================================
        IF NOT EXISTS (
            SELECT 1 FROM detalle_recibo
            WHERE id_diagnostico_tratamiento_diente = NEW.id_diagnostico_tratamiento_diente
        ) THEN
            INSERT INTO detalle_recibo (id_recibo, id_diagnostico_tratamiento_diente,
                tipo_item, cantidad, precio_unitario, subtotal)
            VALUES (v_id_recibo, NEW.id_diagnostico_tratamiento_diente, 'TRATAMIENTO',
                1, COALESCE(v_precio_tratamiento, 0), COALESCE(v_precio_tratamiento, 0));

            -- 9. Totales del recibo (solo si se creó el detalle)
            UPDATE recibo
            SET subtotal_tratamientos = COALESCE(subtotal_tratamientos, 0) + COALESCE(v_precio_tratamiento, 0),
                monto_total = COALESCE(monto_total, 0) + COALESCE(v_precio_tratamiento, 0),
                saldo_pendiente = COALESCE(saldo_pendiente, 0) + COALESCE(v_precio_tratamiento, 0),
                usu_mod_rec = COALESCE(v_usuario_actual, 1),
                fech_mod_rec = CURRENT_TIMESTAMP
            WHERE id_recibo = v_id_recibo;
        END IF;

        -- 10. Auditoria
        INSERT INTO auditoria_cupos (id_cupo, accion, cupos_anteriores, cupos_nuevos,
            id_diag_trat_diente, id_estudiante, id_usuario, fecha_cambio, usuario, nombre_estudiante)
        VALUES (v_id_cupo, 'DESCUENTO_APROBACION', v_cupos_disponibles + 1, v_cupos_disponibles,
            NEW.id_diagnostico_tratamiento_diente, v_id_estudiante, v_usuario_actual,
            CURRENT_TIMESTAMP, v_usuario_actual::text, v_nombre_estudiante);

        RAISE NOTICE 'TRATAMIENTO APROBADO - ID: %, Estudiante: %', v_id_tratamiento, v_nombre_estudiante;
        RAISE NOTICE '   Cupo restante: %, Progreso: %/%', v_cupos_disponibles, v_cupo_realizado, v_cupo_total;
        RAISE NOTICE '   Recibo ID: % (consentimiento: %)', v_id_recibo, v_id_consentimiento;

        RETURN NEW;
    END IF;

    RETURN NEW;
EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Error en trigger: %', SQLERRM;
END;
$function$;
