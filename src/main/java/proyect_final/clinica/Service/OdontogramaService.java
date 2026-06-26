package proyect_final.clinica.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proyect_final.clinica.Model.Entity.*;
import proyect_final.clinica.Model.Dao.*;

import java.time.LocalDate;
import java.util.*;

@Service
public class OdontogramaService {

    @Autowired
    private RevisionRepository revisionRepository;

    @Autowired
    private DetalleRevisionRepository detalleRevisionRepository;

    @Autowired
    private DiagnosticoRepository diagnosticoRepository;

    @Autowired
    private DienteRepository dienteRepository;

    @Autowired
    private CaraDienteRepository caraDienteRepository;

    @Autowired
    private CaraDetReviRepository caraDetReviRepository;

    @Autowired
    private TipoPacienteRepository tipoPacienteRepository;

    // Mapeo de diagnósticos a estados
    private static final Map<String, String> DIAGNOSTICO_TO_ESTADO = new HashMap<>();
    private static final Map<String, Integer> SEVERIDAD = new HashMap<>();

    static {
        DIAGNOSTICO_TO_ESTADO.put("sano", "Sano");
        DIAGNOSTICO_TO_ESTADO.put("cariado", "Cariado");
        DIAGNOSTICO_TO_ESTADO.put("obturado", "Obturado");
        DIAGNOSTICO_TO_ESTADO.put("obturado_cariado", "Obturado");
        DIAGNOSTICO_TO_ESTADO.put("extraccion", "Extracción indicada");
        DIAGNOSTICO_TO_ESTADO.put("extraido", "Extraído");
        DIAGNOSTICO_TO_ESTADO.put("perdido_otra", "Extraído");
        DIAGNOSTICO_TO_ESTADO.put("endodoncia", "Endodoncia");
        DIAGNOSTICO_TO_ESTADO.put("corona", "Corona");

        SEVERIDAD.put("sano", 0);
        SEVERIDAD.put("cariado", 1);
        SEVERIDAD.put("obturado", 2);
        SEVERIDAD.put("obturado_cariado", 2);
        SEVERIDAD.put("corona", 2);
        SEVERIDAD.put("endodoncia", 2);
        SEVERIDAD.put("extraccion", 3);
        SEVERIDAD.put("extraido", 3);
        SEVERIDAD.put("perdido_otra", 3);
    }

    // ===== MAPEO DE ÁREAS DEL FRONTEND A CARAS DE LA BD =====
    private String obtenerNombreCaraBD(String area, Integer numeroDiente) {
        boolean esTemporal = (numeroDiente >= 51 && numeroDiente <= 85);
        boolean esPosterior = esTemporal ? (numeroDiente % 10 >= 4) : ((numeroDiente % 10) >= 4);
        boolean esSuperior = (numeroDiente >= 11 && numeroDiente <= 28) || (numeroDiente >= 51 && numeroDiente <= 65);
        
        switch (area) {
            case "center":
                return "vestibular";
            case "top":
                if (esPosterior) return "oclusal";
                return "incisal";
            case "bottom":
                return esSuperior ? "palatino" : "lingual";
            case "left":
                return "mesial";
            case "right":
                return "distal";
            default:
                return "vestibular";
        }
    }

    // ===== MAPEO INVERSO DE CARAS DE LA BD A ÁREAS DEL FRONTEND =====
    private String obtenerAreaFrontend(String nombreCara) {
        switch (nombreCara) {
            case "vestibular":
                return "center";
            case "oclusal":
            case "incisal":
                return "top";
            case "palatino":
            case "lingual":
                return "bottom";
            case "mesial":
                return "left";
            case "distal":
                return "right";
            default:
                return "center";
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public Revision guardarRevision(Long idConsulta, Map<String, Object> revisionData) {
        System.out.println("=== GUARDANDO REVISIÓN ===");
        System.out.println("ID Consulta: " + idConsulta);
        
        // 1. Crear la revisión
        Revision revision = new Revision();
        Consulta consulta = new Consulta();
        consulta.setIdConsulta(idConsulta);
        revision.setConsulta(consulta);
        revision.setFechaRevision(LocalDate.now());
        revision.setObservacionesGenerales((String) revisionData.get("observaciones_generales"));

        revision = revisionRepository.save(revision);
        System.out.println("✅ Revisión creada con ID: " + revision.getIdRevision());

        // ===== 2. Guardar UN SOLO diagnóstico con TODOS los campos (CPO + CEO) =====
        Map<String, Object> diagnosticoData = (Map<String, Object>) revisionData.get("diagnostico");
        if (diagnosticoData != null && !diagnosticoData.isEmpty()) {
            System.out.println("📊 Diagnosticos recibidos: " + diagnosticoData.keySet());
            guardarDiagnosticoCompleto(revision, consulta, diagnosticoData);
        }

        // ===== 3. Guardar dientes permanentes =====
        List<Map<String, Object>> dientesPermanentes = (List<Map<String, Object>>) revisionData.get("dientesPermanentes");
        System.out.println("🦷 Dientes permanentes recibidos: " + (dientesPermanentes != null ? dientesPermanentes.size() : 0));
        
        if (dientesPermanentes != null && !dientesPermanentes.isEmpty()) {
            for (Map<String, Object> dienteInfo : dientesPermanentes) {
                guardarDiente(revision, dienteInfo);
            }
        }
        
        // ===== 4. Guardar dientes temporales =====
        List<Map<String, Object>> dientesTemporales = (List<Map<String, Object>>) revisionData.get("dientesTemporales");
        System.out.println("🦷 Dientes temporales recibidos: " + (dientesTemporales != null ? dientesTemporales.size() : 0));
        
        if (dientesTemporales != null && !dientesTemporales.isEmpty()) {
            for (Map<String, Object> dienteInfo : dientesTemporales) {
                guardarDiente(revision, dienteInfo);
            }
        }

        return revision;
    }

    // ===== NUEVO: Guardar diagnóstico completo (CPO + CEO) =====
    private void guardarDiagnosticoCompleto(Revision revision, Consulta consulta, Map<String, Object> diagnosticoData) {
        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setRevision(revision);
        diagnostico.getRevision().getConsulta();
        
        // ===== CPO-D (Permanentes) =====
        diagnostico.setCpoCariados(getIntValue(diagnosticoData, "cpo_cariados"));
        diagnostico.setCpoPerdidos(getIntValue(diagnosticoData, "cpo_perdidos"));
        diagnostico.setCpoObturados(getIntValue(diagnosticoData, "cpo_obturados"));
        diagnostico.setCpoTotal(getIntValue(diagnosticoData, "cpo_total"));
        diagnostico.setTotalPiezasPermanentes(getIntValue(diagnosticoData, "total_piezas_permanentes", 32));
        diagnostico.setPiezasSanash4Permanentes(getIntValue(diagnosticoData, "piezas_sanas_permanentes", 0));
        
        // ===== CEO (Temporales) =====
        diagnostico.setCeoCariados(getIntValue(diagnosticoData, "ceo_cariados"));
        diagnostico.setCeoExtraidos(getIntValue(diagnosticoData, "ceo_extraidos"));
        diagnostico.setCeoObturados(getIntValue(diagnosticoData, "ceo_obturados"));
        diagnostico.setCeoTotal(getIntValue(diagnosticoData, "ceo_total"));
        diagnostico.setTotalPiezasTemporales(getIntValue(diagnosticoData, "total_piezas_temporales", 20));
        diagnostico.setPiezasSanash4Temporales(getIntValue(diagnosticoData, "piezas_sanas_temporales", 0));
        
        // ===== Descripción combinada =====
        String descripcionCPO = (String) diagnosticoData.getOrDefault("descripcionCPO", "");
        String descripcionCEO = (String) diagnosticoData.getOrDefault("descripcionCEO", "");
        diagnostico.setDescripcion(
            "CPO-D: " + descripcionCPO + " | CEO: " + descripcionCEO
        );
        
        diagnosticoRepository.save(diagnostico);
        System.out.println("✅ Diagnóstico completo guardado con ID: " + diagnostico.getIdDiagnostico());
        System.out.println("   - CPO: C:" + diagnostico.getCpoCariados() + 
                          " P:" + diagnostico.getCpoPerdidos() + 
                          " O:" + diagnostico.getCpoObturados());
        System.out.println("   - CEO: C:" + diagnostico.getCeoCariados() + 
                          " E:" + diagnostico.getCeoExtraidos() + 
                          " O:" + diagnostico.getCeoObturados());
    }

    // ===== NUEVO: Guardar un diente individual =====
    private void guardarDiente(Revision revision, Map<String, Object> dienteInfo) {
        Integer numeroDiente = (Integer) dienteInfo.get("numero_diente");
        List<Map<String, String>> caras = (List<Map<String, String>>) dienteInfo.get("caras");

        if (caras == null || caras.isEmpty()) {
            System.out.println("  ⚠️ Diente " + numeroDiente + " sin caras, saltando...");
            return;
        }

        // Verificar si hay diagnósticos no sanos
        boolean tieneProblema = false;
        for (Map<String, String> caraInfo : caras) {
            if (!"sano".equals(caraInfo.get("diagnostico"))) {
                tieneProblema = true;
                break;
            }
        }
        
        if (!tieneProblema) {
            System.out.println("  ⚠️ Diente " + numeroDiente + " sin patologías, saltando...");
            return;
        }

        // Obtener o crear el diente
        Diente diente = obtenerOCrearDiente(numeroDiente);
        System.out.println("  ✅ Diente " + numeroDiente + " encontrado/creado con ID: " + diente.getIdDiente());

        // Determinar estado general
        String estadoGeneral = determinarEstadoGeneral(caras);
        String observaciones = generarObservaciones(numeroDiente, caras);
        System.out.println("  Estado general: " + estadoGeneral);
        System.out.println("  Observaciones: " + observaciones);

        // Crear detalle de revisión
        DetalleRevision detalle = new DetalleRevision();
        detalle.setRevision(revision);
        detalle.setDiente(diente);
        detalle.setEstado(estadoGeneral);
        detalle.setObservaciones(observaciones);

        detalle = detalleRevisionRepository.save(detalle);
        System.out.println("  ✅ DetalleRevision creado con ID: " + detalle.getIdDetalleRev());

        // Asociar caras
        int carasGuardadas = 0;
        for (Map<String, String> caraInfo : caras) {
            String area = caraInfo.get("cara");
            String diagnosticoCara = caraInfo.get("diagnostico");

            if (area == null || diagnosticoCara == null || "sano".equals(diagnosticoCara)) {
                continue;
            }

            String nombreCaraBD = obtenerNombreCaraBD(area, numeroDiente);
            System.out.println("    Mapeando área '" + area + "' -> cara BD '" + nombreCaraBD + "' para diente " + numeroDiente);
            
            CaraDiente caraDiente = obtenerOCrearCara(nombreCaraBD);
            
            if (caraDiente != null) {
                CaraDetRevi caraDetRevi = new CaraDetRevi();
                caraDetRevi.setDetalleRevision(detalle);
                caraDetRevi.setCaraDiente(caraDiente);
                caraDetReviRepository.save(caraDetRevi);
                carasGuardadas++;
                System.out.println("    ✅ Relación cara-detalle guardada para: " + nombreCaraBD);
            } else {
                System.out.println("    ❌ No se pudo obtener/crear la cara: " + nombreCaraBD);
            }
        }
        System.out.println("  ✅ Total caras guardadas para diente " + numeroDiente + ": " + carasGuardadas);
    }

    @Transactional
    public Revision cargarRevisionPorConsulta(Long idConsulta) {
        Optional<Revision> revisionOpt = revisionRepository.findByConsulta_IdConsulta(idConsulta);
        return revisionOpt.orElse(null);
    }

    @Transactional
    public Map<String, Object> obtenerDatosRevision(Long idRevision) {
        Optional<Revision> revisionOpt = revisionRepository.findById(idRevision);
        if (revisionOpt.isEmpty()) {
            return null;
        }

        Revision revision = revisionOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);

        // Datos de la revisión
        Map<String, Object> revisionData = new HashMap<>();
        revisionData.put("id_revision", revision.getIdRevision());
        revisionData.put("id_consulta", revision.getConsulta().getIdConsulta());
        revisionData.put("fecha_revision", revision.getFechaRevision().toString());
        revisionData.put("observaciones_generales", revision.getObservacionesGenerales());
        response.put("revision", revisionData);

        // Diagnóstico - Ahora con TODOS los campos
        Optional<Diagnostico> diagnosticoOpt = diagnosticoRepository.findByRevision_IdRevision(idRevision);
        if (diagnosticoOpt.isPresent()) {
            Diagnostico diagnostico = diagnosticoOpt.get();
            Map<String, Object> diagnosticoData = new HashMap<>();
            
            // CPO-D (Permanentes)
            diagnosticoData.put("cpo_cariados", diagnostico.getCpoCariados());
            diagnosticoData.put("cpo_perdidos", diagnostico.getCpoPerdidos());
            diagnosticoData.put("cpo_obturados", diagnostico.getCpoObturados());
            diagnosticoData.put("cpo_total", diagnostico.getCpoTotal());
            diagnosticoData.put("total_piezas_permanentes", diagnostico.getTotalPiezasPermanentes());
            diagnosticoData.put("piezas_sanas_permanentes", diagnostico.getPiezasSanash4Permanentes());
            
            // CEO (Temporales)
            diagnosticoData.put("ceo_cariados", diagnostico.getCeoCariados());
            diagnosticoData.put("ceo_extraidos", diagnostico.getCeoExtraidos());
            diagnosticoData.put("ceo_obturados", diagnostico.getCeoObturados());
            diagnosticoData.put("ceo_total", diagnostico.getCeoTotal());
            diagnosticoData.put("total_piezas_temporales", diagnostico.getTotalPiezasTemporales());
            diagnosticoData.put("piezas_sanas_temporales", diagnostico.getPiezasSanash4Temporales());
            
            diagnosticoData.put("descripcion", diagnostico.getDescripcion());
            response.put("diagnostico", diagnosticoData);
        }

        // Dientes y caras
        List<DetalleRevision> detalles = detalleRevisionRepository.findByRevision_IdRevision(idRevision);
        List<Map<String, Object>> dientesList = new ArrayList<>();

        for (DetalleRevision detalle : detalles) {
            Diente diente = detalle.getDiente();
            Map<String, Object> dienteData = new HashMap<>();
            dienteData.put("numero_diente", diente.getNumeroDiente());
            dienteData.put("nombre_diente", diente.getNombreDiente());
            dienteData.put("estado", detalle.getEstado());
            dienteData.put("observaciones", detalle.getObservaciones());

            List<CaraDetRevi> carasRel = caraDetReviRepository.findByDetalleRevision_IdDetalleRev(detalle.getIdDetalleRev());
            List<Map<String, String>> carasData = new ArrayList<>();

            for (CaraDetRevi caraRel : carasRel) {
                CaraDiente cara = caraRel.getCaraDiente();
                String areaFrontend = obtenerAreaFrontend(cara.getNombreCara());
                if (areaFrontend != null) {
                    Map<String, String> caraMap = new HashMap<>();
                    caraMap.put("cara", areaFrontend);
                    caraMap.put("diagnostico", obtenerDiagnosticoFromEstado(detalle.getEstado()));
                    carasData.add(caraMap);
                }
            }

            if (carasData.isEmpty() && !"Sano".equals(detalle.getEstado())) {
                Map<String, String> caraMap = new HashMap<>();
                caraMap.put("cara", "center");
                caraMap.put("diagnostico", obtenerDiagnosticoFromEstado(detalle.getEstado()));
                carasData.add(caraMap);
            }

            dienteData.put("caras", carasData);
            dientesList.add(dienteData);
        }

        response.put("dientes", dientesList);
        return response;
    }

    // ===== MÉTODOS AUXILIARES =====
    
    private Diente obtenerOCrearDiente(Integer numeroDiente) {
        Optional<Diente> dienteOpt = dienteRepository.findByNumeroDiente(numeroDiente);
        if (dienteOpt.isPresent()) {
            return dienteOpt.get();
        }

        Diente diente = new Diente();
        diente.setNumeroDiente(numeroDiente);
        diente.setNombreDiente(obtenerNombreDiente(numeroDiente));
        return dienteRepository.save(diente);
    }

    private CaraDiente obtenerOCrearCara(String nombreCara) {
        Optional<CaraDiente> caraOpt = caraDienteRepository.findByNombreCara(nombreCara);
        if (caraOpt.isPresent()) {
            return caraOpt.get();
        }

        CaraDiente cara = new CaraDiente();
        cara.setNombreCara(nombreCara);
        return caraDienteRepository.save(cara);
    }

    private String determinarEstadoGeneral(List<Map<String, String>> caras) {
        int maxSeveridad = 0;
        String estadoGeneral = "Sano";

        for (Map<String, String> caraInfo : caras) {
            String diagnostico = caraInfo.get("diagnostico");
            if (diagnostico != null && SEVERIDAD.containsKey(diagnostico)) {
                int severidad = SEVERIDAD.get(diagnostico);
                if (severidad > maxSeveridad) {
                    maxSeveridad = severidad;
                    estadoGeneral = DIAGNOSTICO_TO_ESTADO.getOrDefault(diagnostico, "Sano");
                }
            }
        }

        return estadoGeneral;
    }

    private String generarObservaciones(Integer numeroDiente, List<Map<String, String>> caras) {
        List<String> observaciones = new ArrayList<>();
        for (Map<String, String> caraInfo : caras) {
            String area = caraInfo.get("cara");
            String diagnostico = caraInfo.get("diagnostico");
            if (diagnostico != null && !"sano".equals(diagnostico)) {
                String nombreDiagnostico = DIAGNOSTICO_TO_ESTADO.getOrDefault(diagnostico, diagnostico);
                String nombreArea = obtenerNombreCaraBD(area, numeroDiente);
                observaciones.add(nombreDiagnostico + " en cara " + nombreArea);
            }
        }

        if (!observaciones.isEmpty()) {
            return "Diente " + numeroDiente + ": " + String.join(", ", observaciones);
        }
        return "Diente " + numeroDiente + ": Sin patologías";
    }

    private String obtenerNombreDiente(Integer numeroDiente) {
        Map<Integer, String> nombres = new HashMap<>();
        // Permanentes
        nombres.put(11, "Incisivo central superior derecho");
        nombres.put(12, "Incisivo lateral superior derecho");
        nombres.put(13, "Canino superior derecho");
        nombres.put(14, "Primer premolar superior derecho");
        nombres.put(15, "Segundo premolar superior derecho");
        nombres.put(16, "Primer molar superior derecho");
        nombres.put(17, "Segundo molar superior derecho");
        nombres.put(18, "Tercer molar superior derecho");
        nombres.put(21, "Incisivo central superior izquierdo");
        nombres.put(22, "Incisivo lateral superior izquierdo");
        nombres.put(23, "Canino superior izquierdo");
        nombres.put(24, "Primer premolar superior izquierdo");
        nombres.put(25, "Segundo premolar superior izquierdo");
        nombres.put(26, "Primer molar superior izquierdo");
        nombres.put(27, "Segundo molar superior izquierdo");
        nombres.put(28, "Tercer molar superior izquierdo");
        nombres.put(31, "Incisivo central inferior izquierdo");
        nombres.put(32, "Incisivo lateral inferior izquierdo");
        nombres.put(33, "Canino inferior izquierdo");
        nombres.put(34, "Primer premolar inferior izquierdo");
        nombres.put(35, "Segundo premolar inferior izquierdo");
        nombres.put(36, "Primer molar inferior izquierdo");
        nombres.put(37, "Segundo molar inferior izquierdo");
        nombres.put(38, "Tercer molar inferior izquierdo");
        nombres.put(41, "Incisivo central inferior derecho");
        nombres.put(42, "Incisivo lateral inferior derecho");
        nombres.put(43, "Canino inferior derecho");
        nombres.put(44, "Primer premolar inferior derecho");
        nombres.put(45, "Segundo premolar inferior derecho");
        nombres.put(46, "Primer molar inferior derecho");
        nombres.put(47, "Segundo molar inferior derecho");
        nombres.put(48, "Tercer molar inferior derecho");
        
        // Temporales
        nombres.put(51, "Incisivo central superior derecho temporal");
        nombres.put(52, "Incisivo lateral superior derecho temporal");
        nombres.put(53, "Canino superior derecho temporal");
        nombres.put(54, "Primer molar superior derecho temporal");
        nombres.put(55, "Segundo molar superior derecho temporal");
        nombres.put(61, "Incisivo central superior izquierdo temporal");
        nombres.put(62, "Incisivo lateral superior izquierdo temporal");
        nombres.put(63, "Canino superior izquierdo temporal");
        nombres.put(64, "Primer molar superior izquierdo temporal");
        nombres.put(65, "Segundo molar superior izquierdo temporal");
        nombres.put(71, "Incisivo central inferior izquierdo temporal");
        nombres.put(72, "Incisivo lateral inferior izquierdo temporal");
        nombres.put(73, "Canino inferior izquierdo temporal");
        nombres.put(74, "Primer molar inferior izquierdo temporal");
        nombres.put(75, "Segundo molar inferior izquierdo temporal");
        nombres.put(81, "Incisivo central inferior derecho temporal");
        nombres.put(82, "Incisivo lateral inferior derecho temporal");
        nombres.put(83, "Canino inferior derecho temporal");
        nombres.put(84, "Primer molar inferior derecho temporal");
        nombres.put(85, "Segundo molar inferior derecho temporal");
        
        return nombres.getOrDefault(numeroDiente, "Diente " + numeroDiente);
    }

    private String obtenerDiagnosticoFromEstado(String estado) {
        for (Map.Entry<String, String> entry : DIAGNOSTICO_TO_ESTADO.entrySet()) {
            if (entry.getValue().equals(estado)) {
                return entry.getKey();
            }
        }
        return "sano";
    }

    private Integer getIntValue(Map<String, Object> data, String key) {
        return getIntValue(data, key, 0);
    }

    private Integer getIntValue(Map<String, Object> data, String key, Integer defaultValue) {
        Object value = data.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Transactional
    public void eliminarRevision(Long idRevision) {
        List<DetalleRevision> detalles = detalleRevisionRepository.findByRevision_IdRevision(idRevision);
        for (DetalleRevision detalle : detalles) {
            caraDetReviRepository.deleteByDetalleRevision_IdDetalleRev(detalle.getIdDetalleRev());
        }
        
        detalleRevisionRepository.deleteAll(detalles);
        
        Optional<Diagnostico> diagnosticoOpt = diagnosticoRepository.findByRevision_IdRevision(idRevision);
        diagnosticoOpt.ifPresent(diagnostico -> diagnosticoRepository.delete(diagnostico));
        
        revisionRepository.deleteById(idRevision);
        System.out.println("✅ Revisión " + idRevision + " eliminada");
    }

    public String determinarTipoDenticionPorEdad(Integer edad) {
        if (edad == null) {
            return "permanente";
        }

        Optional<TipoPaciente> tipoOpt = tipoPacienteRepository.findTipoByEdad(edad);
        if (tipoOpt.isPresent()) {
            TipoPaciente tipo = tipoOpt.get();
            String nombreTipo = tipo.getNombreTipo().toLowerCase();
            if (nombreTipo.contains("niño") || nombreTipo.contains("infantil")) {
                return "temporal";
            }
        }
        
        return edad < 12 ? "temporal" : "permanente";
    }
}