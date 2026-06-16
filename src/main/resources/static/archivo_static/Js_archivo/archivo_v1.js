// URL para búsqueda de pacientes
const API_BASE_URL = 'http://localhost:8080/api/pacientes/public';

// URL base para gestión de archivos 
const API_ARCHIVOS_URL = 'http://localhost:8080/api/archivos';
document.addEventListener('DOMContentLoaded', function() {

 
    // Elementos de búsqueda
    const nombreBusqueda = document.getElementById('nombreBusqueda');
    const ciBusqueda = document.getElementById('ciBusqueda');
    const btnBuscar = document.getElementById('btnBuscar');
    const resultadosBusqueda = document.getElementById('resultadosBusqueda');
    const listaResultados = document.getElementById('listaResultados');
    
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
    
    // Función para buscar pacientes en el backend
    async function buscarPacientes() {
        const nombre = nombreBusqueda.value.trim();
        const ci = ciBusqueda.value.trim();

        // Validar que al menos un campo tenga datos
        if (!nombre && !ci) {
            alert('Por favor, ingrese un nombre o CI para buscar.');
            return;
        }

        listaResultados.innerHTML = '<div class="loading">Buscando pacientes...</div>';
        resultadosBusqueda.style.display = 'block';
        btnBuscar.disabled = true;
        btnBuscar.textContent = 'BUSCANDO...';

        try {
            let url;
            
            // ✅ BÚSQUEDA SEPARADA: Si hay CI, buscar solo por CI
            if (ci) {
                url = `${API_BASE_URL}/buscar-por-ci?ci=${encodeURIComponent(ci)}`;
            } else {
                // Si solo hay nombre, buscar por nombre
                url = `${API_BASE_URL}/buscar?term=${encodeURIComponent(nombre)}`;
            }

            console.log('Buscando pacientes con URL:', url);

            const response = await fetch(url);

            if (!response.ok) {
                throw new Error(`Error en la búsqueda: ${response.status}`);
            }

            const pacientes = await response.json();
            console.log('Pacientes encontrados:', pacientes);
            mostrarResultados(pacientes);

        } catch (error) {
            console.error('Error al buscar pacientes:', error);
            listaResultados.innerHTML = '<div class="no-results">Error al realizar la búsqueda. Verifique la conexión.</div>';
        } finally {
            btnBuscar.disabled = false;
            btnBuscar.textContent = 'BUSCAR';
        }
    }
    
    // Función para mostrar resultados de búsqueda
    function mostrarResultados(pacientes) {
        listaResultados.innerHTML = '';
        
        if (!pacientes || pacientes.length === 0) {
            listaResultados.innerHTML = '<div class="no-results">No se encontraron pacientes con los criterios de búsqueda.</div>';
        } else {
            pacientes.forEach(paciente => {
                const item = document.createElement('div');
                item.className = 'result-item';

                // Formatear nombre completo usando los campos reales
                const nombreCompleto = `${paciente.persona?.nombre || ''} ${paciente.persona?.apellidoPaterno || ''} ${paciente.persona?.apellidoMaterno || ''}`.trim();

                // CI está en la entidad Paciente (campo ci)
                const ciMostrado = paciente.ci !== null && paciente.ci !== undefined ? paciente.ci : (paciente.persona?.ci || 'N/A');

                item.innerHTML = `
                    <strong>${nombreCompleto}</strong> - CI: ${ciMostrado} - Historial: ${paciente.historialClinico || 'N/A'}
                `;
                item.addEventListener('click', () => seleccionarPaciente(paciente));
                listaResultados.appendChild(item);
            });
        }
        
        resultadosBusqueda.style.display = 'block';
    }
    
    // Función para seleccionar un paciente y llenar el formulario
    function seleccionarPaciente(paciente) {
        // Llenar campos del formulario con datos del paciente
        numHistoriaClinica.value = paciente.historialClinico || '';
        ciPaciente.value = paciente.ci !== null && paciente.ci !== undefined ? paciente.ci : '';
        numExpediente.value = paciente.idPaciente || paciente.id || '';
        const nombreCompleto = `${paciente.persona?.nombre || ''} ${paciente.persona?.apellidoPaterno || ''} ${paciente.persona?.apellidoMaterno || ''}`.trim();
        personaInformacion.value = nombreCompleto;
        edadPaciente.value = paciente.persona?.edad || '';

        // Llenar datos personales con campos correctos
        apellidoPaterno.value = paciente.persona?.apellidoPaterno || '';
        apellidoMaterno.value = paciente.persona?.apellidoMaterno || '';
        nombres.value = paciente.persona?.nombre || '';
        lugarNacimiento.value = paciente.lugarNacimiento || '';
        ocupacion.value = paciente.ocupacion || '';
        estadoCivil.value = paciente.estadoCivil || '';
        direccion.value = paciente.direccion || '';
        telefono.value = paciente.telefono || '';
        gradoInstruccion.value = paciente.gradoInstruccion || '';

        // Establecer el sexo
        if (paciente.persona?.sexo) {
            const sexo = String(paciente.persona.sexo).toUpperCase();
            if (sexo === 'M') {
                document.getElementById('masculino').checked = true;
            } else if (sexo === 'F') {
                document.getElementById('femenino').checked = true;
            }
        }

        // Actualizar header
        headerExpediente.textContent = paciente.idPaciente || paciente.id || '-';
        headerHistoria.textContent = paciente.historialClinico || '-';
        headerCI.textContent = paciente.ci !== null && paciente.ci !== undefined ? paciente.ci : '-';

        // Ocultar resultados
        resultadosBusqueda.style.display = 'none';

        // Limpiar campos de búsqueda
        nombreBusqueda.value = '';
        ciBusqueda.value = '';

        // Desplazar al formulario principal
        document.querySelector('.form-section').scrollIntoView({ behavior: 'smooth' });
    }
    
    // Event listeners para búsqueda
    btnBuscar.addEventListener('click', buscarPacientes);
    
    // Permitir búsqueda con Enter
    nombreBusqueda.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            buscarPacientes();
        }
    });
    
    ciBusqueda.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            buscarPacientes();
        }
    });
    
    // Ocultar resultados al hacer clic fuera
    document.addEventListener('click', function(e) {
        if (!resultadosBusqueda.contains(e.target) && 
            e.target !== nombreBusqueda && 
            e.target !== ciBusqueda && 
            e.target !== btnBuscar) {
            resultadosBusqueda.style.display = 'none';
        }
    });



    // Función para mostrar notificaciones elegantes
    function showNotification(type, message, title = '') {
        // Eliminar notificaciones anteriores
        const existingNotifications = document.querySelectorAll('.notification');
        existingNotifications.forEach(notification => {
            notification.style.animation = 'slideOut 0.3s ease-out';
            setTimeout(() => notification.remove(), 300);
        });

        // Crear nueva notificación
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        
        // Iconos según el tipo
        const icons = {
            success: 'fas fa-check-circle',
            error: 'fas fa-exclamation-triangle',
            warning: 'fas fa-exclamation-circle'
        };

        notification.innerHTML = `
            <i class="${icons[type]} notification-icon"></i>
            <div class="notification-content">
                ${title ? `<strong>${title}</strong><br>` : ''}
                ${message}
            </div>
            <button class="notification-close">
                <i class="fas fa-times"></i>
            </button>
        `;

        document.body.appendChild(notification);

        // Configurar auto-cierre
        const closeBtn = notification.querySelector('.notification-close');
        closeBtn.addEventListener('click', () => {
            notification.style.animation = 'slideOut 0.3s ease-out';
            setTimeout(() => notification.remove(), 300);
        });

        // Auto-remover después de 5 segundos
        setTimeout(() => {
            if (notification.parentNode) {
                notification.style.animation = 'slideOut 0.3s ease-out';
                setTimeout(() => notification.remove(), 300);
            }
        }, 5000);
    }

    // Función para mostrar mensajes de confirmación
    function showConfirmation(message, callback) {
        const modal = document.createElement('div');
        modal.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 9999;
            animation: fadeIn 0.3s ease-out;
        `;

        modal.innerHTML = `
            <div style="
                background: white;
                padding: 30px;
                border-radius: 12px;
                max-width: 400px;
                width: 90%;
                box-shadow: 0 10px 25px rgba(0,0,0,0.2);
                animation: slideInUp 0.3s ease-out;
            ">
                <div style="text-align: center; margin-bottom: 25px;">
                    <i class="fas fa-question-circle" style="
                        font-size: 3rem;
                        color: var(--primary);
                        margin-bottom: 15px;
                        display: block;
                    "></i>
                    <h3 style="
                        color: var(--text-dark);
                        margin-bottom: 15px;
                        font-size: 1.3rem;
                    ">Confirmar acción</h3>
                    <p style="color: var(--text-light); line-height: 1.5;">
                        ${message}
                    </p>
                </div>
                <div style="display: flex; gap: 15px; justify-content: center;">
                    <button id="confirmCancel" style="
                        padding: 12px 30px;
                        background: var(--light-bg);
                        border: 2px solid var(--border);
                        border-radius: 8px;
                        color: var(--text-dark);
                        font-weight: 600;
                        cursor: pointer;
                        transition: all 0.3s;
                    ">Cancelar</button>
                    <button id="confirmOk" style="
                        padding: 12px 30px;
                        background: linear-gradient(135deg, var(--primary), #3730A3);
                        color: white;
                        border: none;
                        border-radius: 8px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: all 0.3s;
                    ">Continuar</button>
                </div>
            </div>
        `;

        document.body.appendChild(modal);

        modal.querySelector('#confirmCancel').addEventListener('click', () => {
            modal.style.animation = 'fadeOut 0.3s ease-out';
            setTimeout(() => modal.remove(), 300);
        });

        modal.querySelector('#confirmOk').addEventListener('click', () => {
            modal.style.animation = 'fadeOut 0.3s ease-out';
            setTimeout(() => {
                modal.remove();
                if (callback) callback();
            }, 300);
        });

        // Añadir animaciones CSS
        const style = document.createElement('style');
        style.textContent = `
            @keyframes fadeIn {
                from { opacity: 0; }
                to { opacity: 1; }
            }
            @keyframes fadeOut {
                from { opacity: 1; }
                to { opacity: 0; }
            }
            @keyframes slideInUp {
                from {
                    transform: translateY(20px);
                    opacity: 0;
                }
                to {
                    transform: translateY(0);
                    opacity: 1;
                }
            }
        `;
        document.head.appendChild(style);
    }

    // ===== ENVÍO DEL FORMULARIO =====
    document.getElementById('enviarFormulario').addEventListener('click', async function() {
        // Primero validar el formulario
        if (!validarFormularioCompleto()) {
            return;
        }

        // Obtener el ID del paciente del campo numExpediente
        const idPaciente = document.getElementById('numExpediente').value;
        if (!idPaciente) {
            alert('Error: Debe seleccionar un paciente primero');
            return;
        }

        // Obtener datos del archivo
        const codigoArchivo = document.getElementById('codigoArchivo').value;
        const ubicacionFisica = document.getElementById('ubicacionFisica').value;

        // Validar campos del archivo
        if (!codigoArchivo || !ubicacionFisica) {
            alert('Por favor, complete los datos del archivo');
            return;
        }


        console.log("ID Paciente a enviar:", idPaciente);
        console.log("Tipo:", typeof idPaciente);

        try {
            // ✅ CORRECCIÓN AQUÍ: Enviar solo el valor numérico
            const archivoData = {
                codigoArchivo: codigoArchivo,
                ubicacionFisica: ubicacionFisica,
                paciente: {idPaciente: parseInt(idPaciente)},  // ✅ CAMBIO: Solo el número
                estado: "archivado"
            };

            console.log('Enviando archivo:', archivoData);

            // Enviar archivo al backend
            const response = await fetch(API_ARCHIVOS_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(archivoData)
            });

            if (response.ok) {
                const archivoGuardado = await response.json();
                showNotification('success', 
                    `✅ <strong>Archivo guardado exitosamente</strong><br>
                    <small>ID del archivo: ${archivoGuardado.idArchivo}</small><br>
                    <small>Paciente: ${archivoGuardado.paciente?.persona?.nombre || 'No especificado'}</small>`,
                    'Operación exitosa'
                );
                
                limpiarFormularioArchivo();
                
            } else {
                const errorText = await response.text();
                showNotification('error',
                    `⚠️ <strong>Error al guardar el archivo</strong><br>
                    <small>${errorText || 'Error desconocido'}</small>`,
                    'Error en la operación'
                );
            }   
        } catch (error) {
            console.error('Error en la solicitud:', error);
            showNotification('error',
                `⚠️ <strong>Error en la solicitud</strong><br>
                <small>${error.message}</small>`,
                'Error en la operación'
            );
        }
    });
    // Función para validar formulario completo
    function validarFormularioCompleto() {
        const codigoArchivo = document.getElementById('codigoArchivo').value.trim();
        const ubicacionFisica = document.getElementById('ubicacionFisica').value.trim();
        
        // Validar campos del archivo
        if (!codigoArchivo) {
            alert('El código de archivo es obligatorio');
            document.getElementById('codigoArchivo').focus();
            return false;
        }
        
        if (!ubicacionFisica) {
            alert('La ubicación física es obligatoria');
            document.getElementById('ubicacionFisica').focus();
            return false;
        }
        
        // Verificar que se haya seleccionado un paciente
        const idPaciente = document.getElementById('numExpediente').value;
        if (!idPaciente) {
            alert('Error: Debe seleccionar un paciente primero');
            return false;
        }
        
        return true;
    }

    // Función para limpiar formulario de archivo
    function limpiarFormularioArchivo() {
        document.getElementById('codigoArchivo').value = '';
        document.getElementById('ubicacionFisica').value = '';
    }
});