// URL para búsqueda de pacientes
const API_BASE_URL = 'http://localhost:8080/api/pacientes/public';

// URL base para gestión de archivos 
const API_ARCHIVOS_URL = 'http://localhost:8080/api/archivos';

// URL base para gestión de estudiantes
const API_ESTUDIANTES_URL = 'http://localhost:8080/api/estudiantes';

document.addEventListener('DOMContentLoaded', function() {
    // Elementos de búsqueda de pacientes
    const nombreBusqueda = document.getElementById('nombreBusqueda');
    const ciBusqueda = document.getElementById('ciBusqueda');
    const btnBuscarPaciente = document.getElementById('btnBuscarPaciente');
    const resultadosBusquedaPaciente = document.getElementById('resultadosBusquedaPaciente');
    const listaResultadosPaciente = document.getElementById('listaResultadosPaciente');
    
    const codigoEstudianteBusqueda = document.getElementById('codigoEstudianteBusqueda');
    const btnBuscarEstudiante = document.getElementById('btnBuscarEstudiante');
    const resultadosBusquedaEstudiante = document.getElementById('resultadosBusquedaEstudiante');
    const listaResultadosEstudiante = document.getElementById('listaResultadosEstudiante');

    // Elementos del formulario para llenar con datos del paciente
    const numHistoriaClinica = document.getElementById('numHistoriaClinica');
    const ciPaciente = document.getElementById('ciPaciente');
    const numExpediente = document.getElementById('numExpediente');
    const personaInformacion = document.getElementById('personaInformacion');
    const edadPaciente = document.getElementById('edadPaciente');
    
    // Elementos de datos personales
    const apellidoPaterno = document.getElementById('apellidoPaterno');
    const apellidoMaterno = document.getElementById('apellidoMaterno');
    const nombres = document.getElementById('nombres');
    const lugarNacimiento = document.getElementById('lugarNacimiento');
    const ocupacion = document.getElementById('ocupacion');
    const estadoCivil = document.getElementById('estadoCivil');
    const direccion = document.getElementById('direccion');
    const telefono = document.getElementById('telefono');
    const gradoInstruccion = document.getElementById('gradoInstruccion');
    
    // Elementos del header
    const headerExpediente = document.getElementById('headerExpediente');
    const headerHistoria = document.getElementById('headerHistoria');
    const headerCI = document.getElementById('headerCI');
    
    // Variables para resultados
    const resultadosBusqueda = document.getElementById('resultadosBusqueda') || { style: { display: 'none' } };
    const listaResultados = document.getElementById('listaResultados') || { innerHTML: '' };
    
    // ===== BÚSQUEDA DE PACIENTES =====
    async function buscarPacientes() {
        const nombre = nombreBusqueda.value.trim();
        const ci = ciBusqueda.value.trim();

        if (!nombre && !ci) {
            showNotification('warning', 'Por favor, ingrese un nombre o CI para buscar.');
            return;
        }

        listaResultadosPaciente.innerHTML = '<div class="loading">Buscando pacientes...</div>';
        resultadosBusquedaPaciente.style.display = 'block';
        btnBuscarPaciente.disabled = true;
        btnBuscarPaciente.textContent = 'BUSCANDO...';

        try {
            let url;
            if (ci) {
                url = `${API_BASE_URL}/buscar-por-ci?ci=${encodeURIComponent(ci)}`;
            } else {
                url = `${API_BASE_URL}/buscar?term=${encodeURIComponent(nombre)}`;
            }

            console.log('Buscando pacientes con URL:', url);

            const response = await fetch(url);
            if (!response.ok) throw new Error(`Error en la búsqueda: ${response.status}`);

            const pacientes = await response.json();
            console.log('Pacientes encontrados:', pacientes);
            mostrarResultadosPacientes(pacientes);

        } catch (error) {
            console.error('Error:', error);
            listaResultadosPaciente.innerHTML = '<div class="no-results">Error al buscar pacientes.</div>';
            showNotification('error', 'Error al buscar pacientes.');
        } finally {
            btnBuscarPaciente.disabled = false;
            btnBuscarPaciente.textContent = 'BUSCAR';
        }
    }
    
    function mostrarResultadosPacientes(pacientes) {
        listaResultadosPaciente.innerHTML = '';
        
        if (!pacientes || pacientes.length === 0) {
            listaResultadosPaciente.innerHTML = '<div class="no-results">No se encontraron pacientes.</div>';
        } else {
            pacientes.forEach(paciente => {
                const item = document.createElement('div');
                item.className = 'result-item';
                const nombreCompleto = `${paciente.persona?.nombre || ''} ${paciente.persona?.apellidoPaterno || ''} ${paciente.persona?.apellidoMaterno || ''}`.trim();
                item.innerHTML = `<strong>${nombreCompleto}</strong> - CI: ${paciente.ci || 'N/A'} - Historial: ${paciente.historialClinico || 'N/A'}`;
                item.addEventListener('click', () => seleccionarPaciente(paciente));
                listaResultadosPaciente.appendChild(item);
            });
        }
        resultadosBusquedaPaciente.style.display = 'block';
    }
    
    async function seleccionarPaciente(paciente) {
        console.log('=== SELECCIONANDO PACIENTE ===');
        console.log('Paciente:', paciente);
        
        numHistoriaClinica.value = paciente.historialClinico || '';
        ciPaciente.value = paciente.ci || '';
        numExpediente.value = paciente.idPaciente || paciente.id || '';
        const nombreCompleto = `${paciente.persona?.nombre || ''} ${paciente.persona?.apellidoPaterno || ''} ${paciente.persona?.apellidoMaterno || ''}`.trim();
        personaInformacion.value = nombreCompleto;
        edadPaciente.value = paciente.persona?.edad || '';
        apellidoPaterno.value = paciente.persona?.apellidoPaterno || '';
        apellidoMaterno.value = paciente.persona?.apellidoMaterno || '';
        nombres.value = paciente.persona?.nombre || '';
        lugarNacimiento.value = paciente.lugarNacimiento || '';
        ocupacion.value = paciente.ocupacion || '';
        estadoCivil.value = paciente.estadoCivil || '';
        direccion.value = paciente.direccion || '';
        telefono.value = paciente.telefono || '';
        gradoInstruccion.value = paciente.gradoInstruccion || '';

        if (paciente.persona?.sexo) {
            const sexo = String(paciente.persona.sexo).toUpperCase();
            document.getElementById('masculino').checked = sexo === 'M';
            document.getElementById('femenino').checked = sexo === 'F';
        }

        headerExpediente.textContent = paciente.idPaciente || '-';
        headerHistoria.textContent = paciente.historialClinico || '-';
        headerCI.textContent = paciente.ci || '-';

        resultadosBusquedaPaciente.style.display = 'none';
        nombreBusqueda.value = '';
        ciBusqueda.value = '';

        await obtenerIdArchivoDelPaciente(paciente);
        document.querySelector('.form-section').scrollIntoView({ behavior: 'smooth' });
    }
    
    // ===== FUNCIÓN PARA OBTENER ID DEL ARCHIVO (VERSIÓN CORREGIDA) =====
    async function obtenerIdArchivoDelPaciente(paciente) {
        try {
            const ci = paciente.ci;
            const idPaciente = paciente.idPaciente || paciente.id;
            
            console.log('=== BUSCANDO ARCHIVO ===');
            console.log('CI del paciente:', ci);
            console.log('ID del paciente:', idPaciente);
            
            if (!ci && !idPaciente) {
                console.error('No se tiene CI ni ID del paciente');
                showNotification('error', 'No se pudo obtener información del paciente', 'Error');
                return null;
            }
            
            // Opción 1: Buscar por CI usando el endpoint id-por-paciente
            if (ci) {
                const url = `${API_ARCHIVOS_URL}/id-por-paciente?ci=${ci}`;
                console.log('Buscando por CI - URL:', url);
                
                const response = await fetch(url);
                console.log('Respuesta status:', response.status);
                
                if (response.ok) {
                    const result = await response.json();
                    console.log('Respuesta del servidor:', result);
                    
                    if (result.success && result.found && result.idArchivo) {
                        const idArchivo = result.idArchivo;
                        document.getElementById('idArchivo').value = idArchivo;
                        showNotification('success', `✅ Archivo encontrado<br><small>ID: ${idArchivo}</small>`, 'Archivo asignado');
                        return idArchivo;
                    } else {
                        console.log('No se encontró archivo por CI. Resultado:', result);
                    }
                } else {
                    console.log('Error en la petición por CI:', response.status);
                }
            }
            
            // Opción 2: Buscar por ID de paciente
            if (idPaciente) {
                const url = `${API_ARCHIVOS_URL}/paciente/${idPaciente}`;
                console.log('Buscando por ID - URL:', url);
                
                const response = await fetch(url);
                console.log('Respuesta status:', response.status);
                
                if (response.ok) {
                    const archivo = await response.json();
                    console.log('Archivo encontrado por ID:', archivo);
                    
                    if (archivo && (archivo.idArchivo || archivo.id)) {
                        const idArchivo = archivo.idArchivo || archivo.id;
                        document.getElementById('idArchivo').value = idArchivo;
                        showNotification('success', `✅ Archivo encontrado por ID<br><small>ID: ${idArchivo}</small>`, 'Archivo asignado');
                        return idArchivo;
                    }
                }
            }
            
            // Si llegamos aquí, no se encontró archivo
            console.log('❌ Archivo NO encontrado');
            showNotification('warning', 
                `⚠️ No se encontró archivo para este paciente<br>
                <small>CI: ${ci || 'N/A'}<br>
                ID Paciente: ${idPaciente || 'N/A'}</small>`, 
                'Archivo no encontrado'
            );
            document.getElementById('idArchivo').value = '';
            mostrarOpcionCrearArchivo(paciente);
            return null;
            
        } catch (error) {
            console.error('Error al buscar archivo:', error);
            showNotification('error', `❌ Error al buscar archivo: ${error.message}`, 'Error');
            document.getElementById('idArchivo').value = '';
            return null;
        }
    }
    
    function mostrarOpcionCrearArchivo(paciente) {
        if (document.getElementById('mensajeCrearArchivo')) return;
        
        const mensajeDiv = document.createElement('div');
        mensajeDiv.id = 'mensajeCrearArchivo';
        mensajeDiv.className = 'alert alert-warning';
        mensajeDiv.style.cssText = 'margin-top: 10px; padding: 10px; border-radius: 5px; border: 1px solid #ffc107; background: #fff3cd;';
        mensajeDiv.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <div>
                    <strong><i class="fas fa-exclamation-triangle"></i> Paciente sin archivo</strong>
                    <p style="margin: 5px 0 0 0; font-size: 14px;">El paciente seleccionado no tiene un archivo creado.</p>
                </div>
                <div style="display: flex; gap: 10px;">
                    <button id="btnCrearArchivo" class="btn btn-sm btn-primary" style="padding: 8px 15px;">
                        <i class="fas fa-plus"></i> Crear Archivo
                    </button>
                    <button id="btnIgnorarArchivo" class="btn btn-sm btn-secondary" style="padding: 8px 15px;">
                        <i class="fas fa-times"></i> Ignorar
                    </button>
                </div>
            </div>
        `;
        
        const pacienteSection = document.querySelector('.form-section');
        if (pacienteSection) pacienteSection.insertBefore(mensajeDiv, pacienteSection.firstChild);
        
        document.getElementById('btnCrearArchivo').addEventListener('click', async () => {
            await crearArchivoParaPaciente(paciente);
            mensajeDiv.remove();
        });
        document.getElementById('btnIgnorarArchivo').addEventListener('click', () => {
            mensajeDiv.remove();
            showNotification('info', 'Continuando sin archivo.', 'Aviso');
        });
    }
    
    async function crearArchivoParaPaciente(paciente) {
        try {
            showNotification('info', 'Creando archivo...', 'Creando');
            
            const archivoData = {
                paciente: { idPaciente: paciente.idPaciente || paciente.id },
                codigoArchivo: `ARCH-${paciente.ci || 'NOC'}-${Date.now().toString().slice(-6)}`,
                estado: 'ACTIVO'
            };
            
            console.log('Creando archivo con datos:', archivoData);
            
            const response = await fetch(API_ARCHIVOS_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(archivoData)
            });
            
            if (response.ok) {
                const archivoCreado = await response.json();
                const idArchivo = archivoCreado.idArchivo || archivoCreado.id;
                document.getElementById('idArchivo').value = idArchivo;
                showNotification('success', `✅ Archivo creado exitosamente<br><small>ID: ${idArchivo}</small>`, 'Archivo creado');
            } else {
                const errorText = await response.text();
                throw new Error(`Error ${response.status}: ${errorText}`);
            }
        } catch (error) {
            console.error('Error al crear archivo:', error);
            showNotification('error', `❌ Error al crear archivo: ${error.message}`, 'Error');
        }
    }
    
    // ===== BÚSQUEDA DE ESTUDIANTES =====
    async function buscarEstudiante() {
        const codigo = codigoEstudianteBusqueda.value.trim();
        
        if (!codigo) {
            showNotification('warning', 'Ingrese el código del estudiante.');
            return;
        }
        
        if (isNaN(codigo)) {
            showNotification('error', 'El código debe ser numérico.');
            return;
        }
        
        listaResultadosEstudiante.innerHTML = '<div class="loading">Buscando estudiante...</div>';
        resultadosBusquedaEstudiante.style.display = 'block';
        btnBuscarEstudiante.disabled = true;
        btnBuscarEstudiante.textContent = 'BUSCANDO...';
        
        try {
            const url = `${API_ESTUDIANTES_URL}/buscar-estudiante/${codigo}`;
            console.log('Buscando estudiante con URL:', url);
            
            const response = await fetch(url);
            
            if (!response.ok) {
                if (response.status === 404) {
                    listaResultadosEstudiante.innerHTML = '<div class="no-results">No se encontró ningún estudiante con el código ingresado.</div>';
                    showNotification('info', 'No se encontró estudiante con ese código.');
                } else {
                    throw new Error(`Error en la búsqueda: ${response.status}`);
                }
                return;
            }
            
            const estudiante = await response.json();
            console.log('Estudiante encontrado (DTO):', estudiante);
            mostrarResultadosEstudiantes([estudiante]);
            
        } catch (error) {
            console.error('Error al buscar estudiante:', error);
            listaResultadosEstudiante.innerHTML = '<div class="no-results">Error al realizar la búsqueda. Verifique la conexión.</div>';
            showNotification('error', 'Error al buscar estudiante.');
        } finally {
            btnBuscarEstudiante.disabled = false;
            btnBuscarEstudiante.textContent = 'BUSCAR ESTUDIANTE';
        }
    }
    
    function mostrarResultadosEstudiantes(estudiantes) {
        listaResultadosEstudiante.innerHTML = '';
        
        estudiantes.forEach(estudiante => {
            const item = document.createElement('div');
            item.className = 'result-item';
            
            const idEstudiante = estudiante.idEstudiante;
            const codigoEstudiante = estudiante.codigoEstudiante;
            const nombreCompleto = estudiante.nombreCompleto || 'No especificado';
            const email = estudiante.email || 'No disponible';
            const bloqueado = estudiante.bloqueado || false;
            
            item.innerHTML = `
                <div class="estudiante-resultado">
                    <div class="estudiante-header">
                        <strong>🎓 ${nombreCompleto}</strong>
                        <span class="estudiante-codigo">RU: ${codigoEstudiante}</span>
                    </div>
                    <div class="estudiante-info">
                        <div><small><strong>📧 Email:</strong> ${email}</small></div>
                        <div><small><strong>📊 Estado:</strong> ${bloqueado ? '❌ Bloqueado' : '✅ Activo'}</small></div>
                    </div>
                    <div class="estudiante-acciones">
                        <button class="btn-seleccionar-estudiante" data-id="${idEstudiante}">
                            <i class="fas fa-check"></i> Seleccionar Estudiante
                        </button>
                    </div>
                </div>
            `;
            
            const btnSeleccionar = item.querySelector('.btn-seleccionar-estudiante');
            btnSeleccionar.addEventListener('click', (e) => {
                e.stopPropagation();
                seleccionarEstudiante(estudiante);
            });
            
            item.addEventListener('click', () => seleccionarEstudiante(estudiante));
            listaResultadosEstudiante.appendChild(item);
        });
        
        resultadosBusquedaEstudiante.style.display = 'block';
    }
    
    function seleccionarEstudiante(estudiante) {
        console.log('=== DTO RECIBIDO ===');
        console.log('estudiante.idEstudiante:', estudiante.idEstudiante);
        console.log('estudiante.codigoEstudiante:', estudiante.codigoEstudiante);
        console.log('estudiante.nombreCompleto:', estudiante.nombreCompleto);

        if (!estudiante.idEstudiante) {
        console.error('ERROR: idEstudiante es undefined o null');
        showNotification('error', 'Error: El estudiante no tiene ID válido', 'Error');
        return;
    }
        const estudianteId = estudiante.idEstudiante;
        document.getElementById('idEstudiante').value = estudianteId;
        
        if (document.getElementById('codigoEstudianteBusqueda')) {
            document.getElementById('codigoEstudianteBusqueda').value = estudiante.codigoEstudiante || '';
        }
        
        resultadosBusquedaEstudiante.style.display = 'none';
        
        showNotification('success', 
            `✅ Estudiante "${estudiante.nombreCompleto}" seleccionado.<br>
             📚 RU: ${estudiante.codigoEstudiante}<br>
             📧 Email: ${estudiante.email}`, 
            'Estudiante seleccionado'
        );
        
        document.querySelector('.form-section').scrollIntoView({ behavior: 'smooth' });
    }
    
    // ===== EVENT LISTENERS =====
    btnBuscarPaciente.addEventListener('click', buscarPacientes);
    nombreBusqueda.addEventListener('keypress', e => e.key === 'Enter' && buscarPacientes());
    ciBusqueda.addEventListener('keypress', e => e.key === 'Enter' && buscarPacientes());
    
    if (btnBuscarEstudiante) {
        btnBuscarEstudiante.addEventListener('click', buscarEstudiante);
        codigoEstudianteBusqueda.addEventListener('keypress', e => e.key === 'Enter' && buscarEstudiante());
    }
    
    // ===== FUNCIÓN DE ENVÍO PARA GUARDAR PRÉSTAMO =====
    document.getElementById('enviarFormulario').addEventListener('click', async function() {
        if (!validarFormularioPrestamo()) return;

        const idEstudiante = document.getElementById('idEstudiante').value;
        const idArchivo = document.getElementById('idArchivo').value;
        const fechaLimitePrestamo = document.getElementById('fechaLimitePrestamo').value;
        const tipoPrestamo = document.getElementById('tipoPrestamo').value;
        const encargadoPrestamo = document.getElementById('encargadoPrestamo').value.trim();
        const motivoPrestamo = document.getElementById('motivoPrestamo')?.value.trim() || '';

        try {
            const fechaActual = new Date();
            const año = fechaActual.getFullYear();
            const mes = String(fechaActual.getMonth() + 1).padStart(2, '0');
            const dia = String(fechaActual.getDate()).padStart(2, '0');
            const fechaPrestamoFormateada = `${año}-${mes}-${dia}`;

            if (fechaLimitePrestamo < fechaPrestamoFormateada) {
                showNotification('warning', 'La fecha límite no puede ser menor que la fecha actual', 'Validación');
                return;
            }

            const prestamoData = {
                idArchivo: parseInt(idArchivo),
                idEstudiante: parseInt(idEstudiante),
                fechaLimitePrestamo: fechaLimitePrestamo,
                tipoPrestamo: tipoPrestamo,
                encargadoPrestamo: encargadoPrestamo,
                motivoPrestamo: motivoPrestamo || null
            };

            console.log('Enviando préstamo:', prestamoData);

            const response = await fetch('http://localhost:8080/api/prestamos-actuales', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(prestamoData)
            });

            if (response.ok) {
                const prestamoGuardado = await response.json();
                showNotification('success', 
                    `✅ Préstamo registrado<br>
                    <small>ID: ${prestamoGuardado.idPrestamo}</small><br>
                    <small>Fecha límite: ${prestamoGuardado.fechaLimitePrestamo}</small>`,
                    'Éxito'
                );
                limpiarFormularioPrestamo();
            } else {
                const errorText = await response.text();
                showNotification('error', `Error: ${errorText || 'Error desconocido'}`, 'Error');
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification('error', `Error: ${error.message}`, 'Error');
        }
    });

    function validarFormularioPrestamo() {
        if (!document.getElementById('numExpediente').value) {
            showNotification('error', 'Debe seleccionar un paciente');
            return false;
        }
        if (!document.getElementById('idEstudiante').value) {
            showNotification('error', 'Debe seleccionar un estudiante');
            return false;
        }
        if (!document.getElementById('idArchivo').value) {
            showNotification('error', 'El paciente no tiene archivo', 'Archivo requerido');
            return false;
        }
        if (!document.getElementById('fechaLimitePrestamo').value) {
            showNotification('error', 'La fecha límite es obligatoria');
            return false;
        }
        if (!document.getElementById('tipoPrestamo').value) {
            showNotification('error', 'El tipo de préstamo es obligatorio');
            return false;
        }
        if (!document.getElementById('encargadoPrestamo').value.trim()) {
            showNotification('error', 'El encargado es obligatorio');
            return false;
        }
        return true;
    }

    function limpiarFormularioPrestamo() {
        document.getElementById('fechaLimitePrestamo').value = '';
        document.getElementById('tipoPrestamo').value = '';
        document.getElementById('encargadoPrestamo').value = '';
        document.getElementById('motivoPrestamo').value = '';
        document.getElementById('idEstudiante').value = '';
        document.getElementById('codigoEstudianteBusqueda').value = '';
    }

    // ===== NOTIFICACIONES =====
    function showNotification(type, message, title = '') {
        const existingNotifications = document.querySelectorAll('.notification');
        existingNotifications.forEach(n => n.remove());

        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            padding: 15px 20px;
            border-radius: 8px;
            background: white;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            display: flex;
            align-items: center;
            gap: 12px;
            min-width: 300px;
            max-width: 400px;
            animation: slideIn 0.3s ease-out;
        `;
        
        const colors = {
            success: '#10b981',
            error: '#ef4444',
            warning: '#f59e0b',
            info: '#3b82f6'
        };
        
        notification.style.borderLeft = `4px solid ${colors[type]}`;
        
        const icons = { 
            success: 'fas fa-check-circle', 
            error: 'fas fa-exclamation-triangle', 
            warning: 'fas fa-exclamation-circle', 
            info: 'fas fa-info-circle' 
        };
        
        notification.innerHTML = `
            <i class="${icons[type]}" style="color: ${colors[type]}; font-size: 20px;"></i>
            <div style="flex: 1;">
                ${title ? `<strong>${title}</strong><br>` : ''}
                <span style="font-size: 14px;">${message}</span>
            </div>
            <button class="notification-close" style="background: none; border: none; cursor: pointer; font-size: 16px;">
                <i class="fas fa-times"></i>
            </button>
        `;
        
        document.body.appendChild(notification);
        
        const closeBtn = notification.querySelector('.notification-close');
        closeBtn.addEventListener('click', () => notification.remove());
        setTimeout(() => notification.remove(), 5000);
    }
    
    // Agregar estilos de animación
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
    `;
    document.head.appendChild(style);
});