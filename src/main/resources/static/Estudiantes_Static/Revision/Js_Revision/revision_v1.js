// ===== VARIABLES GLOBALES =====
// URL base de tu API Spring Boot
const API_BASE_URL = 'http://localhost:8080/api/pacientes';

// Variable global para almacenar el ID de la consulta guardada
let ultimoIdConsulta = null;

// ===== DOCUMENT READY =====
document.addEventListener('DOMContentLoaded', async function() {

    const idEstudiante = sessionStorage.getItem('idEstudiante');  

    if (!idEstudiante){
        alert('Debe iniciar sesión primero');
        window.location.href='/';
        return;
    }

    await mostrarPacientePrestado();

    // ===== MOSTRAR PACIENTE PRESTADO =====
    async function mostrarPacientePrestado() {
        try {
            const idEstudianteFront = sessionStorage.getItem('idEstudiante');
            console.log('🔵 Frontend - ID en sessionStorage:', idEstudianteFront);
            
            const sesionResponse = await fetch('/api/pacientes/debug/verificar-sesion');
            const sesionData = await sesionResponse.json();
            console.log('🟢 Backend - Datos de sesión:', sesionData);
            
            if (!sesionData.tieneSesion && idEstudianteFront) {
                console.warn('⚠️ El frontend tiene ID pero el backend no. Intentando reparar...');
            }
            
            const response = await fetch('/api/pacientes/estudiante/paciente-prestado');
            const data = await response.json();
            
            console.log('📦 Datos del préstamo:', data);
            
            const bannerExistente = document.querySelector('.prestamo-banner');
            if (bannerExistente) bannerExistente.remove();
            
            if (data.tienePrestamo) {
                const bannerHTML = `
                    <div class="prestamo-banner" style="
                        background-color: #e3f2fd;
                        border: 2px solid #2196f3;
                        border-radius: 8px;
                        padding: 15px;
                        margin-bottom: 20px;
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                        flex-wrap: wrap;
                    ">
                        <div>
                            <strong style="color: #1976d2;">📋 PACIENTE ASIGNADO:</strong>
                            <span style="margin-left: 10px; font-size: 1.2em; color: #0d47a1;">
                                ${data.nombreCompleto}
                            </span>
                        </div>
                        <div>
                            <span style="background-color: #4caf50; color: white; padding: 5px 10px; border-radius: 20px;">
                                CI: ${data.ci}
                            </span>
                            <span style="margin-left: 10px; color: #f57c00;">
                                ⏰ Límite: ${data.fechaLimite}
                            </span>
                        </div>
                    </div>
                `;
                
                document.querySelector('.form-section').insertAdjacentHTML('afterbegin', bannerHTML);
                
                // Auto-llenar el campo de búsqueda con el CI del paciente prestado
                const ciBusquedaInput = document.getElementById('ciBusqueda');
                if (ciBusquedaInput && data.ci) {
                    ciBusquedaInput.value = data.ci;
                    setTimeout(() => {
                        buscarPacientes();
                    }, 500);
                }
                
            } else {
                const warningHTML = `
                    <div class="prestamo-banner" style="
                        background-color: #fff3e0;
                        border: 2px solid #ff9800;
                        border-radius: 8px;
                        padding: 15px;
                        margin-bottom: 20px;
                        color: #e65100;
                    ">
                        ⚠️ No tienes ningún préstamo activo. Debes solicitar un archivo en recepción.
                    </div>
                `;
                document.querySelector('.form-section').insertAdjacentHTML('afterbegin', warningHTML);
            }
        } catch (error) {
            console.error('❌ Error al obtener paciente prestado:', error);
        }
    }

    // ===== FUNCIÓN PARA LIMPIAR TODO EL FORMULARIO =====
    function limpiarFormularioCompleto() {
        const textosIds = [
            'nombres_pesona', 'apellidoPaterno_persona', 'apellidoMaterno_persona',
            'direccion_persona', 'telefono_persona',
            'otrosEnfermedades', 'especifiqueAlergia', 'semanasEmbarazo',
            'tratamientoMedico', 'medicamentoActual', 'especifiqueHemorragia',
            'atm', 'ganglios', 'otrosRespiratorio',
            'labios', 'lengua', 'paladar', 'pisoBoca', 'mucosaYugal', 'encias',
            'tipoProtesis', 'tiempoProtesis',
            'ultimaVisita', 'frecuenciaCepillado', 'observacionesHigiene', 'otrosHabitos'
        ];
        
        textosIds.forEach(id => {
            const campo = document.getElementById(id);
            if (campo) campo.value = '';
        });

        const checkboxesIds = [
            'anemia', 'cardiopatias', 'gastricas', 'hepatitis', 'tuberculosis',
            'asma', 'diabetes', 'epilepsia', 'hipertension', 'vih',
            'fuma', 'bebe', 'cepillo-si', 'hilo-si', 'enjuague-si', 'sangrado-si'
        ];
        
        checkboxesIds.forEach(id => {
            const cb = document.getElementById(id);
            if (cb) cb.checked = false;
        });

        const radioGroups = [
            'alergias', 'embarazo', 'hemorragia', 'protesis', 
            'tratamiento', 'medicamento', 'higiene', 'respirador'
        ];
        
        radioGroups.forEach(group => {
            const radios = document.querySelectorAll(`input[name="${group}"]`);
            radios.forEach(radio => radio.checked = false);
        });
        
        const ningunaEnfermedad = document.getElementById('ningunaEnfermedad');
        if (ningunaEnfermedad) ningunaEnfermedad.checked = false;

        const camposDeshabilitados = [
            'especifiqueAlergia', 'semanasEmbarazo', 'especifiqueHemorragia',
            'tipoProtesis', 'tiempoProtesis', 'tratamientoMedico', 'medicamentoActual'
        ];
        
        camposDeshabilitados.forEach(id => {
            const campo = document.getElementById(id);
            if (campo) campo.disabled = false;
        });
        console.log('✅ Formulario limpiado completamente');
    }

    // ===== VALIDACIONES =====
    const ningunaEnfermedad = document.getElementById('ningunaEnfermedad');
    const enfermedadesCheckboxes = document.querySelectorAll('.enfermedad-checkbox');
    const otrosEnfermedades = document.getElementById('otrosEnfermedades');
    const otrosValidation = document.getElementById('otrosValidation');

    function actualizarValidacionEnfermedades() {
        const algunaEnfermedadSeleccionada = Array.from(enfermedadesCheckboxes).some(cb => cb.checked) || (otrosEnfermedades && otrosEnfermedades.value.trim() !== '');
        
        if (algunaEnfermedadSeleccionada && ningunaEnfermedad && ningunaEnfermedad.checked) {
            if (otrosValidation) otrosValidation.style.display = 'block';
            if (ningunaEnfermedad) ningunaEnfermedad.classList.add('error-border');
        } else {
            if (otrosValidation) otrosValidation.style.display = 'none';
            if (ningunaEnfermedad) ningunaEnfermedad.classList.remove('error-border');
        }

        if (ningunaEnfermedad && ningunaEnfermedad.checked) {
            enfermedadesCheckboxes.forEach(checkbox => { checkbox.disabled = true; });
            if (otrosEnfermedades) otrosEnfermedades.disabled = true;
        } else {
            enfermedadesCheckboxes.forEach(checkbox => { checkbox.disabled = false; });
            if (otrosEnfermedades) otrosEnfermedades.disabled = false;
        }
    }

    if (ningunaEnfermedad) {
        ningunaEnfermedad.addEventListener('change', function() {
            if (this.checked) {
                enfermedadesCheckboxes.forEach(checkbox => {
                    checkbox.checked = false;
                    checkbox.disabled = true;
                });
                if (otrosEnfermedades) {
                    otrosEnfermedades.value = '';
                    otrosEnfermedades.disabled = true;
                }
            } else {
                enfermedadesCheckboxes.forEach(checkbox => { checkbox.disabled = false; });
                if (otrosEnfermedades) otrosEnfermedades.disabled = false;
            }
            actualizarValidacionEnfermedades();
        });
    }

    enfermedadesCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            if (this.checked && ningunaEnfermedad) {
                ningunaEnfermedad.checked = false;
            }
            actualizarValidacionEnfermedades();
        });
    });

    if (otrosEnfermedades) {
        otrosEnfermedades.addEventListener('input', function() {
            if (this.value.trim() !== '' && ningunaEnfermedad) {
                ningunaEnfermedad.checked = false;
            }
            actualizarValidacionEnfermedades();
        });
    }

    // Validación Alergias
    const alergiaSi = document.getElementById('alergias-si');
    const alergiaNo = document.getElementById('alergias-no');
    const especifiqueAlergia = document.getElementById('especifiqueAlergia');
    const alergiaValidation = document.getElementById('alergiaValidation');

    function actualizarValidacionAlergias() {
        if (alergiaSi && alergiaSi.checked && especifiqueAlergia && especifiqueAlergia.value.trim() === '') {
            if (alergiaValidation) alergiaValidation.style.display = 'block';
            if (especifiqueAlergia) especifiqueAlergia.classList.add('error-border');
        } else {
            if (alergiaValidation) alergiaValidation.style.display = 'none';
            if (especifiqueAlergia) especifiqueAlergia.classList.remove('error-border');
        }
    }

    if (alergiaSi) {
        alergiaSi.addEventListener('change', function() {
            if (especifiqueAlergia) especifiqueAlergia.disabled = false;
            actualizarValidacionAlergias();
        });
    }

    if (alergiaNo) {
        alergiaNo.addEventListener('change', function() {
            if (especifiqueAlergia) {
                especifiqueAlergia.disabled = true;
                especifiqueAlergia.value = '';
            }
            if (alergiaValidation) alergiaValidation.style.display = 'none';
        });
    }

    if (especifiqueAlergia) {
        especifiqueAlergia.addEventListener('input', actualizarValidacionAlergias);
    }

    // Validación Embarazo
    const embarazoSi = document.getElementById('embarazo-si');
    const embarazoNo = document.getElementById('embarazo-no');
    const semanasEmbarazo = document.getElementById('semanasEmbarazo');
    const embarazoValidation = document.getElementById('embarazoValidation');

    function actualizarValidacionEmbarazo() {
        if (embarazoSi && embarazoSi.checked && semanasEmbarazo && semanasEmbarazo.value.trim() === '') {
            if (embarazoValidation) embarazoValidation.style.display = 'block';
            if (semanasEmbarazo) semanasEmbarazo.classList.add('error-border');
        } else {
            if (embarazoValidation) embarazoValidation.style.display = 'none';
            if (semanasEmbarazo) semanasEmbarazo.classList.remove('error-border');
        }
    }

    if (embarazoSi) {
        embarazoSi.addEventListener('change', function() {
            if (semanasEmbarazo) semanasEmbarazo.disabled = false;
            actualizarValidacionEmbarazo();
        });
    }

    if (embarazoNo) {
        embarazoNo.addEventListener('change', function() {
            if (semanasEmbarazo) {
                semanasEmbarazo.disabled = true;
                semanasEmbarazo.value = '';
            }
            if (embarazoValidation) embarazoValidation.style.display = 'none';
        });
    }

    if (semanasEmbarazo) {
        semanasEmbarazo.addEventListener('input', actualizarValidacionEmbarazo);
    }

    // Validación Hemorragia
    const hemorragiaSi = document.getElementById('hemorragia-si');
    const hemorragiaNo = document.getElementById('hemorragia-no');
    const especifiqueHemorragia = document.getElementById('especifiqueHemorragia');
    const hemorragiaValidation = document.getElementById('hemorragiaValidation');

    function actualizarValidacionHemorragia() {
        if (hemorragiaSi && hemorragiaSi.checked && especifiqueHemorragia && especifiqueHemorragia.value.trim() === '') {
            if (hemorragiaValidation) hemorragiaValidation.style.display = 'block';
            if (especifiqueHemorragia) especifiqueHemorragia.classList.add('error-border');
        } else {
            if (hemorragiaValidation) hemorragiaValidation.style.display = 'none';
            if (especifiqueHemorragia) especifiqueHemorragia.classList.remove('error-border');
        }
    }

    if (hemorragiaSi) {
        hemorragiaSi.addEventListener('change', function() {
            if (especifiqueHemorragia) especifiqueHemorragia.disabled = false;
            actualizarValidacionHemorragia();
        });
    }

    if (hemorragiaNo) {
        hemorragiaNo.addEventListener('change', function() {
            if (especifiqueHemorragia) {
                especifiqueHemorragia.disabled = true;
                especifiqueHemorragia.value = '';
            }
            if (hemorragiaValidation) hemorragiaValidation.style.display = 'none';
        });
    }

    if (especifiqueHemorragia) {
        especifiqueHemorragia.addEventListener('input', actualizarValidacionHemorragia);
    }

    // Validación Prótesis
    const protesisSi = document.getElementById('protesis-si');
    const protesisNo = document.getElementById('protesis-no');
    const tipoProtesis = document.getElementById('tipoProtesis');
    const tiempoProtesis = document.getElementById('tiempoProtesis');
    const protesisValidation = document.getElementById('protesisValidation');

    function actualizarValidacionProtesis() {
        if (protesisSi && protesisSi.checked && tipoProtesis && tipoProtesis.value.trim() === '') {
            if (protesisValidation) protesisValidation.style.display = 'block';
            if (tipoProtesis) tipoProtesis.classList.add('error-border');
        } else {
            if (protesisValidation) protesisValidation.style.display = 'none';
            if (tipoProtesis) tipoProtesis.classList.remove('error-border');
        }
    }

    if (protesisSi) {
        protesisSi.addEventListener('change', function() {
            if (tipoProtesis) tipoProtesis.disabled = false;
            if (tiempoProtesis) tiempoProtesis.disabled = false;
            actualizarValidacionProtesis();
        });
    }

    if (protesisNo) {
        protesisNo.addEventListener('change', function() {
            if (tipoProtesis) {
                tipoProtesis.disabled = true;
                tipoProtesis.value = '';
            }
            if (tiempoProtesis) {
                tiempoProtesis.disabled = true;
                tiempoProtesis.value = '';
            }
            if (protesisValidation) protesisValidation.style.display = 'none';
        });
    }

    if (tipoProtesis) {
        tipoProtesis.addEventListener('input', actualizarValidacionProtesis);
    }

    // Validación Tratamiento Médico
    const tratamientoSi = document.getElementById('tratamiento-si');
    const tratamientoNo = document.getElementById('tratamiento-no');
    const tratamientoMedico = document.getElementById('tratamientoMedico');
    const tratamientoValidation = document.getElementById('tratamientoValidation');

    function actualizarValidacionTratamiento() {
        if (tratamientoSi && tratamientoSi.checked && tratamientoMedico && tratamientoMedico.value.trim() === '') {
            if (tratamientoValidation) tratamientoValidation.style.display = 'block';
            if (tratamientoMedico) tratamientoMedico.classList.add('error-border');
        } else {
            if (tratamientoValidation) tratamientoValidation.style.display = 'none';
            if (tratamientoMedico) tratamientoMedico.classList.remove('error-border');
        }
    }

    if (tratamientoSi) {
        tratamientoSi.addEventListener('change', function() {
            if (tratamientoMedico) tratamientoMedico.disabled = false;
            actualizarValidacionTratamiento();
        });
    }

    if (tratamientoNo) {
        tratamientoNo.addEventListener('change', function() {
            if (tratamientoMedico) {
                tratamientoMedico.disabled = true;
                tratamientoMedico.value = '';
            }
            if (tratamientoValidation) tratamientoValidation.style.display = 'none';
        });
    }

    if (tratamientoMedico) {
        tratamientoMedico.addEventListener('input', actualizarValidacionTratamiento);
    }

    // Validación Medicamento Actual
    const medicamentoSi = document.getElementById('medicamento-si');
    const medicamentoNo = document.getElementById('medicamento-no');
    const medicamentoActual = document.getElementById('medicamentoActual');
    const medicamentoValidation = document.getElementById('medicamentoValidation');

    function actualizarValidacionMedicamento() {
        if (medicamentoSi && medicamentoSi.checked && medicamentoActual && medicamentoActual.value.trim() === '') {
            if (medicamentoValidation) medicamentoValidation.style.display = 'block';
            if (medicamentoActual) medicamentoActual.classList.add('error-border');
        } else {
            if (medicamentoValidation) medicamentoValidation.style.display = 'none';
            if (medicamentoActual) medicamentoActual.classList.remove('error-border');
        }
    }

    if (medicamentoSi) {
        medicamentoSi.addEventListener('change', function() {
            if (medicamentoActual) medicamentoActual.disabled = false;
            actualizarValidacionMedicamento();
        });
    }

    if (medicamentoNo) {
        medicamentoNo.addEventListener('change', function() {
            if (medicamentoActual) {
                medicamentoActual.disabled = true;
                medicamentoActual.value = '';
            }
            if (medicamentoValidation) medicamentoValidation.style.display = 'none';
        });
    }

    if (medicamentoActual) {
        medicamentoActual.addEventListener('input', actualizarValidacionMedicamento);
    }

    // Validación Hábitos
    const habitoCheckboxes = document.querySelectorAll('.habito-checkbox');
    const otrosHabitos = document.getElementById('otrosHabitos');

    habitoCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const algunHabitoSeleccionado = Array.from(habitoCheckboxes).some(cb => cb.checked);
            if (algunHabitoSeleccionado && otrosHabitos) {
                otrosHabitos.value = '';
            }
        });
    });

    if (otrosHabitos) {
        otrosHabitos.addEventListener('input', function() {
            if (this.value.trim() !== '') {
                habitoCheckboxes.forEach(checkbox => {
                    checkbox.checked = false;
                });
            }
        });
    }

    // Manejo del campo "Otros" en respirador
    const otrosRespiratorio = document.getElementById('otrosRespiratorio');
    
    document.querySelectorAll('input[name="respirador"]').forEach(radio => {
        radio.addEventListener('change', function() {
            if (otrosRespiratorio) otrosRespiratorio.value = '';
        });
    });

    if (otrosRespiratorio) {
        otrosRespiratorio.addEventListener('focus', function() {
            document.querySelectorAll('input[name="respirador"]').forEach(radio => {
                radio.checked = false;
            });
        });
    }

    // ========== FUNCIONALIDAD DE BÚSQUEDA ==========
    
    const nombreBusqueda = document.getElementById('nombreBusqueda');
    const ciBusqueda = document.getElementById('ciBusqueda');
    const btnBuscar = document.getElementById('btnBuscar');
    const resultadosBusqueda = document.getElementById('resultadosBusqueda');
    const listaResultados = document.getElementById('listaResultados');
    
    const numHistoriaClinica = document.getElementById('numHistoriaClinica');
    const ciPaciente = document.getElementById('ciPaciente');
    const numExpediente = document.getElementById('numExpediente');
    const personaInformacion = document.getElementById('personaInformacion');
    const edadPaciente = document.getElementById('edadPaciente');
    
    const apellidoPaterno = document.getElementById('apellidoPaterno');
    const apellidoMaterno = document.getElementById('apellidoMaterno');
    const nombres = document.getElementById('nombres');
    const lugarNacimiento = document.getElementById('lugarNacimiento');
    const ocupacion = document.getElementById('ocupacion');
    const estadoCivil = document.getElementById('estadoCivil');
    const direccion = document.getElementById('direccion');
    const telefono = document.getElementById('telefono');
    const gradoInstruccion = document.getElementById('gradoInstruccion');
    
    const headerExpediente = document.getElementById('headerExpediente');
    const headerHistoria = document.getElementById('headerHistoria');
    const headerCI = document.getElementById('headerCI');

    async function buscarPacientes() {
        const nombre = nombreBusqueda.value.trim();
        const ci = ciBusqueda.value.trim();

        if (!nombre && !ci) {
            alert('Por favor, ingrese un CI o nombre para buscar.');
            return;
        }

        listaResultados.innerHTML = '<div class="loading">Buscando pacientes...</div>';
        resultadosBusqueda.style.display = 'block';
        btnBuscar.disabled = true;
        btnBuscar.textContent = 'BUSCANDO...';

        try {
            let url;
            let pacientes = [];
            
            if (ci) {
                url = `/api/pacientes/estudiante/buscar-por-ci?ci=${encodeURIComponent(ci)}`;
                console.log('🔍 Buscando por CI:', ci);
                
                const response = await fetch(url);
                
                if (response.status === 403) {
                    const errorData = await response.json();
                    
                    if (errorData.pacientePermitido) {
                        const pacientePermitido = errorData.pacientePermitido;
                        listaResultados.innerHTML = `
                            <div style="background-color: #ffebee; border-left: 4px solid #f44336; padding: 20px; border-radius: 4px;">
                                <h3 style="color: #d32f2f; margin-bottom: 10px;">⛔ Paciente Incorrecto</h3>
                                <p style="margin-bottom: 10px;">${errorData.mensaje || 'No tienes permiso para consultar este paciente.'}</p>
                                <div style="background-color: #fff; padding: 15px; border-radius: 4px; margin-top: 10px;">
                                    <strong style="color: #1976d2;">✅ Paciente que DEBES consultar:</strong>
                                    <p style="font-size: 1.1em; margin-top: 5px;">${pacientePermitido.nombreCompleto} (CI: ${pacientePermitido.ci})</p>
                                </div>
                                <button onclick="autoCompletarPacientePermitido()" style="margin-top: 15px; background-color: #2196f3; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer;">Buscar paciente permitido</button>
                            </div>
                        `;
                        return;
                    }
                }
                
                if (response.ok) {
                    const resultado = await response.json();
                    pacientes = Array.isArray(resultado) ? resultado : [resultado];
                } else {
                    throw new Error(`Error en la búsqueda: ${response.status}`);
                }
            } 
            
            if (pacientes.length === 0 && nombre) {
                url = `/api/pacientes/estudiante/buscar?term=${encodeURIComponent(nombre)}`;
                console.log('🔍 Buscando por nombre:', nombre);
                
                const response = await fetch(url);
                
                if (response.ok) {
                    pacientes = await response.json();
                } else if (response.status !== 404) {
                    throw new Error(`Error en la búsqueda: ${response.status}`);
                }
            }

            console.log('Pacientes encontrados:', pacientes);
            
            if (!pacientes || pacientes.length === 0) {
                listaResultados.innerHTML = '<div class="no-results">No se encontraron pacientes con los criterios de búsqueda.</div>';
            } else {
                mostrarResultados(pacientes);
            }

        } catch (error) {
            console.error('Error al buscar pacientes:', error);
            listaResultados.innerHTML = '<div class="no-results">Error al realizar la búsqueda. Verifique la conexión.</div>';
        } finally {
            btnBuscar.disabled = false;
            btnBuscar.textContent = 'BUSCAR';
        }
    }

    window.autoCompletarPacientePermitido = function() {
        fetch('/api/pacientes/estudiante/paciente-prestado')
            .then(response => response.json())
            .then(data => {
                if (data.tienePrestamo) {
                    ciBusqueda.value = data.ci;
                    buscarPacientes();
                }
            });
    }
        
    function mostrarResultados(pacientes) {
        listaResultados.innerHTML = '';
        
        if (!pacientes || pacientes.length === 0) {
            listaResultados.innerHTML = '<div class="no-results">No se encontraron pacientes con los criterios de búsqueda.</div>';
        } else {
            pacientes.forEach(paciente => {
                const item = document.createElement('div');
                item.className = 'result-item';

                const nombreCompleto = `${paciente.persona?.nombre || ''} ${paciente.persona?.apellidoPaterno || ''} ${paciente.persona?.apellidoMaterno || ''}`.trim();
                const ciMostrado = paciente.ci || paciente.persona?.ci || 'N/A';

                item.innerHTML = `
                    <strong><i class="fas fa-user"></i> ${nombreCompleto}</strong><br>
                    <small><i class="fas fa-id-card"></i> CI: ${ciMostrado}</small><br>
                    <small><i class="fas fa-file-medical"></i> Historial: ${paciente.historialClinico || 'N/A'}</small>
                `;
                item.style.cursor = 'pointer';
                item.style.padding = '15px';
                item.style.borderBottom = '1px solid #e0e0e0';
                item.addEventListener('click', () => seleccionarPaciente(paciente));
                listaResultados.appendChild(item);
            });
        }
        
        resultadosBusqueda.style.display = 'block';
    }
    
    function seleccionarPaciente(paciente) {
        const ciPacienteValue = paciente.ci || paciente.persona?.ci || '';
        const nombreCompleto = `${paciente.persona?.nombre || ''} ${paciente.persona?.apellidoPaterno || ''} ${paciente.persona?.apellidoMaterno || ''}`.trim();
        
        console.log('📋 Seleccionando paciente:', {
            id: paciente.idPaciente || paciente.id,
            ci: ciPacienteValue,
            nombre: nombreCompleto
        });
        
        numHistoriaClinica.value = paciente.historialClinico || '';
        ciPaciente.value = ciPacienteValue;
        numExpediente.value = paciente.idPaciente || paciente.id || '';
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
            const masculino = document.getElementById('masculino');
            const femenino = document.getElementById('femenino');
            if (sexo === 'M' && masculino) {
                masculino.checked = true;
            } else if (sexo === 'F' && femenino) {
                femenino.checked = true;
            }
        }

        headerExpediente.textContent = paciente.idPaciente || paciente.id || '-';
        headerHistoria.textContent = paciente.historialClinico || '-';
        headerCI.textContent = ciPacienteValue || '-';

        limpiarFormularioCompleto();

        resultadosBusqueda.style.display = 'none';
        nombreBusqueda.value = '';
        ciBusqueda.value = '';

        Swal.fire({
            title: '✅ Paciente Seleccionado',
            text: `${nombreCompleto} (CI: ${ciPacienteValue})`,
            icon: 'success',
            timer: 2000,
            showConfirmButton: false,
            timerProgressBar: true,
            background: '#f0fdf4',
            iconColor: '#10b981'
        });

        document.querySelector('.form-section').scrollIntoView({ behavior: 'smooth' });
    }
    
    btnBuscar.addEventListener('click', buscarPacientes);
    
    if (nombreBusqueda) {
        nombreBusqueda.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') buscarPacientes();
        });
    }
    
    if (ciBusqueda) {
        ciBusqueda.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') buscarPacientes();
        });
        
        ciBusqueda.addEventListener('input', function(e) {
            this.value = this.value.replace(/[^0-9]/g, '');
        });
    }
    
    document.addEventListener('click', function(e) {
        if (resultadosBusqueda && !resultadosBusqueda.contains(e.target) && 
            e.target !== nombreBusqueda && e.target !== ciBusqueda && e.target !== btnBuscar) {
            resultadosBusqueda.style.display = 'none';
        }
    });

    // ===== FUNCIONES AUXILIARES =====
    
    function obtenerRespiradorSeleccionado() {
        const respNasal = document.getElementById('resp-nasal');
        const respBucal = document.getElementById('resp-bucal');
        const respBucoNasal = document.getElementById('resp-buco-nasal');
        
        if (respNasal && respNasal.checked) return 'Nasal';
        if (respBucal && respBucal.checked) return 'Bucal';
        if (respBucoNasal && respBucoNasal.checked) return 'Buco nasal';
        
        const otrosResp = document.getElementById('otrosRespiratorio');
        if (otrosResp && otrosResp.value.trim()) return `Otros: ${otrosResp.value.trim()}`;
        
        return '';
    }

    function obtenerHigieneBucalSeleccionada() {
        const higieneBuena = document.getElementById('higiene-buena');
        const higieneRegular = document.getElementById('higiene-regular');
        const higieneMala = document.getElementById('higiene-mala');
        
        if (higieneBuena && higieneBuena.checked) return 'Buena';
        if (higieneRegular && higieneRegular.checked) return 'Regular';
        if (higieneMala && higieneMala.checked) return 'Mala';
        return '';
    }

    function validarFormularioCompleto() {
        let errores = [];

        const numHistoria = document.getElementById('numHistoriaClinica');
        const ci = document.getElementById('ciPaciente');
        const nombresPaciente = document.getElementById('nombres');
        const apellidoPaternoPaciente = document.getElementById('apellidoPaterno');
        
        if (!numHistoria.value.trim()) errores.push('Número de Historia Clínica es obligatorio');
        if (!ci.value.trim()) errores.push('C.I. es obligatorio');
        if (!nombresPaciente.value.trim()) errores.push('Nombres son obligatorios');
        if (!apellidoPaternoPaciente.value.trim()) errores.push('Apellido Paterno es obligatorio');

        const alergiaSiRadio = document.getElementById('alergias-si');
        const especifiqueAlergiaInput = document.getElementById('especifiqueAlergia');
        if (alergiaSiRadio && alergiaSiRadio.checked && especifiqueAlergiaInput && !especifiqueAlergiaInput.value.trim()) {
            errores.push('Debe especificar la alergia');
        }

        const embarazoSiRadio = document.getElementById('embarazo-si');
        const semanasEmbarazoInput = document.getElementById('semanasEmbarazo');
        if (embarazoSiRadio && embarazoSiRadio.checked && semanasEmbarazoInput && !semanasEmbarazoInput.value.trim()) {
            errores.push('Debe especificar las semanas de embarazo');
        }

        const hemorragiaSiRadio = document.getElementById('hemorragia-si');
        const especifiqueHemorragiaInput = document.getElementById('especifiqueHemorragia');
        if (hemorragiaSiRadio && hemorragiaSiRadio.checked && especifiqueHemorragiaInput && !especifiqueHemorragiaInput.value.trim()) {
            errores.push('Debe especificar el tipo de hemorragia');
        }

        const protesisSiRadio = document.getElementById('protesis-si');
        const tipoProtesisInput = document.getElementById('tipoProtesis');
        if (protesisSiRadio && protesisSiRadio.checked && tipoProtesisInput && !tipoProtesisInput.value.trim()) {
            errores.push('Debe especificar el tipo de prótesis');
        }

        const tratamientoSiRadio = document.getElementById('tratamiento-si');
        const tratamientoMedicoInput = document.getElementById('tratamientoMedico');
        if (tratamientoSiRadio && tratamientoSiRadio.checked && tratamientoMedicoInput && !tratamientoMedicoInput.value.trim()) {
            errores.push('Debe especificar el tratamiento médico');
        }

        const medicamentoSiRadio = document.getElementById('medicamento-si');
        const medicamentoActualInput = document.getElementById('medicamentoActual');
        if (medicamentoSiRadio && medicamentoSiRadio.checked && medicamentoActualInput && !medicamentoActualInput.value.trim()) {
            errores.push('Debe especificar los medicamentos que recibe');
        }

        const ningunaEnfermedadCheckbox = document.getElementById('ningunaEnfermedad');
        const enfermedadesCheckboxesList = document.querySelectorAll('.enfermedad-checkbox');
        const otrosEnfermedadesInput = document.getElementById('otrosEnfermedades');
        
        const algunaEnfermedadSeleccionada = Array.from(enfermedadesCheckboxesList).some(cb => cb.checked) || (otrosEnfermedadesInput && otrosEnfermedadesInput.value.trim() !== '');
        if (algunaEnfermedadSeleccionada && ningunaEnfermedadCheckbox && ningunaEnfermedadCheckbox.checked) {
            errores.push('No puede seleccionar "Ninguno" junto con otras enfermedades');
        }

        if (errores.length > 0) {
            alert('Errores encontrados:\n' + errores.join('\n'));
            return false;
        }
        return true;
    }

    // ============================================================
    // ===== ENVÍO DEL FORMULARIO CON CONSULTA + ODONTOGRAMA =====
    // ============================================================
    document.getElementById('enviarFormulario').addEventListener('click', async function() {
        if (!validarFormularioCompleto()) {
            return;
        }
        
        const prestamoResponse = await fetch('/api/pacientes/estudiante/paciente-prestado');
        const prestamoData = await prestamoResponse.json();
        
        if (!prestamoData.tienePrestamo) {
            alert('No tienes un préstamo activo. No puedes guardar la consulta.');
            return;
        }
        
        const idPaciente = document.getElementById('numExpediente').value;
        if (!idPaciente) {
            alert('Error: Debe seleccionar un paciente primero');
            return;
        }

        console.log('🔍 ID Paciente a verificar:', idPaciente);
        console.log('🔍 ID Estudiante en sesión:', sessionStorage.getItem('idEstudiante'));

        const permisoResponse = await fetch(`/api/pacientes/estudiante/verificar-permiso?idPaciente=${idPaciente}`);
        const permisoData = await permisoResponse.json();
        
        if (!permisoData.puedeConsultar) {
            alert('No puedes guardar esta consulta. Solo puedes consultar al paciente que tienes prestado.');
            return;
        }
        
        // ===== OBTENER DIAGNÓSTICO COMPLETO (CPO + CEO) =====
        const edadPacienteValue = parseInt(document.getElementById('edadPaciente').value) || 0;
        const tipoActivo = document.getElementById('tipoDenticion').value;
        
        // ===== 1. DIAGNÓSTICO COMPLETO CON TODOS LOS CAMPOS =====
        const diagnosticoCompleto = {
            // CPO-D (Permanentes)
            cpo_cariados: parseInt(document.getElementById('cValue').value) || 0,
            cpo_perdidos: parseInt(document.getElementById('pValue').value) || 0,
            cpo_obturados: parseInt(document.getElementById('oValue').value) || 0,
            cpo_total: parseInt(document.getElementById('cpodTotal').textContent) || 0,
            total_piezas_permanentes: parseInt(document.getElementById('totalDientes').value) || 32,
            piezas_sanas_permanentes: parseInt(document.getElementById('sValue').value) || 0,
            descripcionCPO: `Revisión para paciente de ${edadPacienteValue} años - Dentición permanente`,
            
            // CEO (Temporales)
            ceo_cariados: parseInt(document.getElementById('ceoCariados').value) || 0,
            ceo_extraidos: parseInt(document.getElementById('ceoExtraidos').value) || 0,
            ceo_obturados: parseInt(document.getElementById('ceoObturados').value) || 0,
            ceo_total: parseInt(document.getElementById('ceoTotal').textContent) || 0,
            total_piezas_temporales: parseInt(document.getElementById('totalDientesTemp').value) || 20,
            piezas_sanas_temporales: parseInt(document.getElementById('ceoSanos').value) || 0,
            descripcionCEO: `Revisión para paciente de ${edadPacienteValue} años - Dentición temporal`
        };
        
        // ===== 2. OBTENER DIENTES DE AMBOS TIPOS =====
        const dientesPermanentes = [];
        const dientesTemporales = [];
        
        // Dientes permanentes
        const datosPermanentes = window.toothDiagnostics?.permanente || {};
        for (const [numeroDiente, areas] of Object.entries(datosPermanentes)) {
            const carasData = [];
            let tieneProblema = false;
            
            for (const [cara, diagnosis] of Object.entries(areas)) {
                if (diagnosis !== 'sano') {
                    tieneProblema = true;
                    carasData.push({ cara: cara, diagnostico: diagnosis });
                }
            }
            
            if (tieneProblema) {
                dientesPermanentes.push({
                    numero_diente: parseInt(numeroDiente),
                    caras: carasData
                });
            }
        }
        
        // Dientes temporales
        const datosTemporales = window.toothDiagnostics?.temporal || {};
        for (const [numeroDiente, areas] of Object.entries(datosTemporales)) {
            const carasData = [];
            let tieneProblema = false;
            
            for (const [cara, diagnosis] of Object.entries(areas)) {
                if (diagnosis !== 'sano') {
                    tieneProblema = true;
                    carasData.push({ cara: cara, diagnostico: diagnosis });
                }
            }
            
            if (tieneProblema) {
                dientesTemporales.push({
                    numero_diente: parseInt(numeroDiente),
                    caras: carasData
                });
            }
        }
        
        console.log('📊 CPO Dientes:', dientesPermanentes.length);
        console.log('📊 CEO Dientes:', dientesTemporales.length);
        console.log('📊 Tipo activo:', tipoActivo);
        
        // ===== CONSTRUIR DATOS COMPLETOS =====
        const datosCompletos = {
            // Datos de la consulta (heredados de ConsultaCompletaDTO)
            fecha: new Date().toISOString().split('T')[0],
            observaciones: document.getElementById('observacionesHigiene').value,
            idPaciente: parseInt(idPaciente),
            idEstudiante: parseInt(sessionStorage.getItem('idEstudiante')),
            idPrestamoActual: prestamoData.idPrestamoActual,
            edadPaciente: edadPacienteValue,

            // Informante
            informanteNombres: document.getElementById('nombres_pesona').value,
            informanteApellidoPaterno: document.getElementById('apellidoPaterno_persona').value,
            informanteApellidoMaterno: document.getElementById('apellidoMaterno_persona').value,
            informanteDireccion: document.getElementById('direccion_persona').value,
            informanteTelefono: document.getElementById('telefono_persona').value,

            // Patologia Personal
            anemia: document.getElementById('anemia').checked,
            cardiopatias: document.getElementById('cardiopatias').checked,
            enfGastricos: document.getElementById('gastricas').checked,
            hepatitis: document.getElementById('hepatitis').checked,
            tuberculosis: document.getElementById('tuberculosis').checked,
            asma: document.getElementById('asma').checked,
            diabetesMel: document.getElementById('diabetes').checked,
            epilepsia: document.getElementById('epilepsia').checked,
            hipertension: document.getElementById('hipertension').checked,
            vih: document.getElementById('vih').checked,
            otros: document.getElementById('otrosEnfermedades').value,
            ninguno: document.getElementById('ningunaEnfermedad').checked,
            alergias: document.getElementById('alergias-si').checked,
            especifiqueAlergia: document.getElementById('especifiqueAlergia').value,
            embarazo: document.getElementById('embarazo-si').checked,
            semanaEmbarazo: document.getElementById('semanasEmbarazo').value,

            // Tratamiento médico
            tratamientoMedico: document.getElementById('tratamientoMedico').value,
            recibeAlgunMedicamento: document.getElementById('medicamentoActual').value,
            tuvoHemorragiaDental: document.getElementById('hemorragia-si').checked,
            especifiqueHemorragia: document.getElementById('especifiqueHemorragia').value,

            // ExamenExtraOral
            atm: document.getElementById('atm').value,
            gangliosLinfaticos: document.getElementById('ganglios').value,
            respirador: obtenerRespiradorSeleccionado(),
            otrosRespiratorio: document.getElementById('otrosRespiratorio').value,

            // ExamenIntraOral
            labios: document.getElementById('labios').value,
            lengua: document.getElementById('lengua').value,
            paladar: document.getElementById('paladar').value,
            pisoDeLaBoca: document.getElementById('pisoBoca').value,
            mucosaYugal: document.getElementById('mucosaYugal').value,
            encias: document.getElementById('encias').value,
            utilizaProtesisDental: document.getElementById('protesis-si').checked,
            tipoProtesis: document.getElementById('tipoProtesis').value,
            tiempoProtesis: document.getElementById('tiempoProtesis').value,

            // AntecedentesBucodentales
            fechaRevision: document.getElementById('ultimaVisita').value,
            habitoFuma: document.getElementById('fuma').checked,
            habitoBebe: document.getElementById('bebe').checked,
            otrosHabitos: document.getElementById('otrosHabitos').value,

            // AntecedentesHigieneOral
            utilizaCepilloDental: document.getElementById('cepillo-si').checked,
            utilizaHiloDental: document.getElementById('hilo-si').checked,
            utilizaEnjuagueBucal: document.getElementById('enjuague-si').checked,
            frecuenciaCepillo: document.getElementById('frecuenciaCepillado').value,
            sangradoEncias: document.getElementById('sangrado-si').checked,
            higieneBucal: obtenerHigieneBucalSeleccionada(),
            observacionesHigiene: document.getElementById('observacionesHigiene').value,

            // ===== DATOS DEL ODONTOGRAMA (ConsultaConRevisionDTO) =====
            observacionesGenerales: `Revisión odontológica - CPO: ${dientesPermanentes.length} dientes, CEO: ${dientesTemporales.length} dientes`,
            diagnostico: diagnosticoCompleto,
            dientesPermanentes: dientesPermanentes,
            dientesTemporales: dientesTemporales,
            tipoDenticionActivo: tipoActivo
        };

        console.log('📦 Datos completos a enviar (resumen):', {
            idPaciente: datosCompletos.idPaciente,
            idEstudiante: datosCompletos.idEstudiante,
            dientesPermanentes: datosCompletos.dientesPermanentes.length,
            dientesTemporales: datosCompletos.dientesTemporales.length,
            tipoActivo: datosCompletos.tipoDenticionActivo
        });

        const btnEnviar = document.getElementById('enviarFormulario');
        btnEnviar.disabled = true;
        btnEnviar.textContent = 'Guardando...';
        document.getElementById('loadingMessage').style.display = 'block';

        try {
            const response = await fetch('/api/consultas/completa-con-revision', {
                method: 'POST',
                body: JSON.stringify(datosCompletos),
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const result = await response.json();
            
            if (!result.success) {
                throw new Error(result.error || 'Error al guardar la consulta');
            }

            ultimoIdConsulta = result.idConsulta;
            
            document.getElementById('consultaIdGuardada').textContent = ultimoIdConsulta;
            document.getElementById('seccionFotos').style.display = 'block';
            
            const fotosInput = document.getElementById('fotosOdontograma');
            if (fotosInput) fotosInput.value = '';
            const previsualizacionFotosDiv = document.getElementById('previsualizacionFotos');
            if (previsualizacionFotosDiv) previsualizacionFotosDiv.innerHTML = '';
            
            document.getElementById('seccionFotos').scrollIntoView({ behavior: 'smooth' });
            
            Swal.fire({
                title: '✅ Consulta y Odontograma Guardados',
                text: `¡Todo se ha guardado exitosamente!\nID Consulta: ${ultimoIdConsulta}\nID Revisión: ${result.idRevision || 'N/A'}\nCPO: ${dientesPermanentes.length} dientes\nCEO: ${dientesTemporales.length} dientes`,
                icon: 'success',
                timer: 4000,
                showConfirmButton: true,
                confirmButtonText: 'Subir Fotos',
                timerProgressBar: true,
                background: '#f0fdf4',
                iconColor: '#10b981'
            }).then((result) => {
                if (result.isConfirmed) {
                    document.getElementById('seccionFotos').scrollIntoView({ behavior: 'smooth' });
                }
            });
            
        } catch (error) {
            console.error('❌ Error al guardar:', error);
            Swal.fire({
                title: 'Error',
                text: 'Error al guardar: ' + error.message,
                icon: 'error',
                confirmButtonText: 'Aceptar'
            });
        } finally {
            document.getElementById('loadingMessage').style.display = 'none';
            btnEnviar.disabled = false;
            btnEnviar.textContent = 'GUARDAR HISTORIA CLÍNICA';
        }
    });

    // ===== SUBIR FOTOS =====
    const btnSubirFotos = document.getElementById('btnSubirFotos');
    if (btnSubirFotos) {
        btnSubirFotos.addEventListener('click', async function() {
            const fotosInput = document.getElementById('fotosOdontograma');
            const archivos = fotosInput.files;
            
            if (archivos.length === 0) {
                Swal.fire({
                    title: 'Sin fotos',
                    text: 'Debe seleccionar al menos una foto para subir.',
                    icon: 'warning',
                    confirmButtonText: 'Aceptar'
                });
                return;
            }
            
            if (!ultimoIdConsulta) {
                Swal.fire({
                    title: 'Error',
                    text: 'No hay una consulta activa. Por favor, guarde primero la consulta.',
                    icon: 'error',
                    confirmButtonText: 'Aceptar'
                });
                return;
            }
            
            const btnSubir = this;
            btnSubir.disabled = true;
            btnSubir.textContent = 'Subiendo...';
            
            const progresoDiv = document.getElementById('progresoFotos');
            const progresoTexto = document.getElementById('progresoTexto');
            const barraProgreso = document.getElementById('barraProgreso');
            
            progresoDiv.style.display = 'block';
            
            let exitosas = 0;
            let fallidas = 0;
            
            for (let i = 0; i < archivos.length; i++) {
                const fotoFormData = new FormData();
                fotoFormData.append('idConsulta', ultimoIdConsulta);
                fotoFormData.append('archivo', archivos[i]);
                
                progresoTexto.textContent = `Subiendo fotos: ${i + 1}/${archivos.length}`;
                const porcentaje = ((i + 1) / archivos.length) * 100;
                barraProgreso.style.width = `${porcentaje}%`;
                
                try {
                    const response = await fetch('/api/consultas/subir-foto', {
                        method: 'POST',
                        body: fotoFormData
                    });
                    
                    if (response.ok) {
                        exitosas++;
                    } else {
                        fallidas++;
                    }
                } catch (error) {
                    console.error(`Error al subir foto ${i + 1}:`, error);
                    fallidas++;
                }
            }
            
            if (fallidas === 0) {
                Swal.fire({
                    title: '✅ Completado',
                    text: `Se subieron ${exitosas} foto(s) correctamente.`,
                    icon: 'success',
                    timer: 3000,
                    showConfirmButton: false,
                    timerProgressBar: true
                });
                fotosInput.value = '';
                const previsualizacionFotosDiv = document.getElementById('previsualizacionFotos');
                if (previsualizacionFotosDiv) previsualizacionFotosDiv.innerHTML = '';
                progresoDiv.style.display = 'none';
            } else {
                Swal.fire({
                    title: '⚠️ Advertencia',
                    text: `Subida completada: ${exitosas} exitosas, ${fallidas} fallidas.`,
                    icon: 'warning',
                    confirmButtonText: 'Aceptar'
                });
            }
            
            btnSubir.disabled = false;
            btnSubir.textContent = 'SUBIR FOTOS';
        });
    }

    // ===== BOTÓN PARA LIMPIAR MANUALMENTE =====
    const botonLimpiar = document.getElementById('limpiarFormularioBtn');
    if (botonLimpiar) {
        botonLimpiar.addEventListener('click', function() {
            Swal.fire({
                title: '¿Limpiar formulario?',
                text: 'Los datos no guardados se perderán.',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#dc2626',
                cancelButtonColor: '#6b7280',
                confirmButtonText: 'Sí, limpiar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    limpiarFormularioCompleto();
                    Swal.fire({
                        title: 'Limpiado',
                        text: 'Formulario limpiado completamente',
                        icon: 'success',
                        timer: 1500,
                        showConfirmButton: false
                    });
                }
            });
        });
    }
    
    // Inicializar estado de los campos al cargar la página
    actualizarValidacionEnfermedades();
    
    // Inicializar estado de tratamiento médico y medicamento
    const tratamientoNoRadio = document.getElementById('tratamiento-no');
    if (tratamientoNoRadio) {
        tratamientoNoRadio.checked = true;
        const tratamientoMedicoInput = document.getElementById('tratamientoMedico');
        if (tratamientoMedicoInput) {
            tratamientoMedicoInput.disabled = true;
            tratamientoMedicoInput.value = '';
        }
    }

    const medicamentoNoRadio = document.getElementById('medicamento-no');
    if (medicamentoNoRadio) {
        medicamentoNoRadio.checked = true;
        const medicamentoActualInput = document.getElementById('medicamentoActual');
        if (medicamentoActualInput) {
            medicamentoActualInput.disabled = true;
            medicamentoActualInput.value = '';
        }
    }
});
// ============================================================
// ===== ODONTOGRAMA - Script independiente (VERSIÓN FINAL) =====
// ============================================================
(function() {
    // Configuración para ADULTOS (dientes permanentes)
    const cuadrantesAdultos = {
        1: { element: document.getElementById('dentalChart1'), dientes: [18, 17, 16, 15, 14, 13, 12, 11] },
        2: { element: document.getElementById('dentalChart2'), dientes: [21, 22, 23, 24, 25, 26, 27, 28] },
        3: { element: document.getElementById('dentalChart3'), dientes: [31, 32, 33, 34, 35, 36, 37, 38] },
        4: { element: document.getElementById('dentalChart4'), dientes: [48, 47, 46, 45, 44, 43, 42, 41] }
    };
    
    // Configuración para NIÑOS (dientes temporales)
    const cuadrantesNinos = {
        1: { element: document.getElementById('dentalChart1'), dientes: [55, 54, 53, 52, 51] },
        2: { element: document.getElementById('dentalChart2'), dientes: [61, 62, 63, 64, 65] },
        3: { element: document.getElementById('dentalChart3'), dientes: [85, 84, 83, 82, 81] },
        4: { element: document.getElementById('dentalChart4'), dientes: [71, 72, 73, 74, 75] }
    };
    
    let currentCuadrantes = cuadrantesAdultos;
    let selectedTooth = null;
    let selectedArea = null;
    let tipoDenticionActual = 'permanente';
    
    // ===== INICIALIZAR ALMACENAMIENTO GLOBAL =====
    if (!window.toothDiagnostics) {
        window.toothDiagnostics = {
            permanente: {},
            temporal: {}
        };
        console.log('✅ Inicializado window.toothDiagnostics');
    }
    
    // ===== FUNCIÓN PARA INICIALIZAR DIENTES POR DEFECTO =====
    function inicializarDientesPorDefecto(tipo) {
        const cuadrantes = tipo === 'permanente' ? cuadrantesAdultos : cuadrantesNinos;
        const datos = {};
        
        for (const [cuadrante, config] of Object.entries(cuadrantes)) {
            config.dientes.forEach(numeroDiente => {
                datos[numeroDiente] = {
                    center: 'sano',
                    top: 'sano',
                    bottom: 'sano',
                    left: 'sano',
                    right: 'sano'
                };
            });
        }
        return datos;
    }
    
    // ===== INICIALIZAR AMBOS TIPOS SI ESTÁN VACÍOS =====
    if (Object.keys(window.toothDiagnostics.permanente).length === 0) {
        window.toothDiagnostics.permanente = inicializarDientesPorDefecto('permanente');
        console.log('✅ Inicializados dientes permanentes por defecto (32 dientes)');
    }
    if (Object.keys(window.toothDiagnostics.temporal).length === 0) {
        window.toothDiagnostics.temporal = inicializarDientesPorDefecto('temporal');
        console.log('✅ Inicializados dientes temporales por defecto (20 dientes)');
    }
    
    // Referencia al conjunto actual
    let toothDiagnostics = window.toothDiagnostics.permanente;
    
    // ===== DIAGNÓSTICOS =====
    const perdidosDiagnostics = ['extraccion', 'extraido', 'perdido_otra'];
    
    const severidadMap = {
        'sano': 0,
        'cariado': 1,
        'obturado': 2,
        'obturado_cariado': 2,
        'endodoncia': 2,
        'corona': 2,
        'extraccion': 3,
        'extraido': 3,
        'perdido_otra': 3
    };
    
    const cpoMap = {
        'cariado': 'C',
        'obturado': 'O',
        'obturado_cariado': 'O',
        'endodoncia': 'O',
        'corona': 'O',
        'extraccion': 'P',
        'extraido': 'P',
        'perdido_otra': 'P'
    };
    
    const ceoMap = {
        'cariado': 'C',
        'obturado': 'O',
        'obturado_cariado': 'O',
        'endodoncia': 'O',
        'corona': 'O',
        'extraccion': 'E',
        'extraido': 'E',
        'perdido_otra': 'E'
    };
    
    function limpiarOdontograma() {
        for (let i = 1; i <= 4; i++) {
            const chart = document.getElementById(`dentalChart${i}`);
            if (chart) {
                chart.innerHTML = '';
            }
        }
    }
    
    function generarOdontograma() {
        limpiarOdontograma();
        let totalDientes = 0;
        
        const cuadrantes = tipoDenticionActual === 'permanente' ? cuadrantesAdultos : cuadrantesNinos;
        currentCuadrantes = cuadrantes;
        
        // Obtener los diagnósticos guardados para este tipo
        toothDiagnostics = window.toothDiagnostics[tipoDenticionActual];
        
        for (const [cuadrante, config] of Object.entries(cuadrantes)) {
            if (config.dientes.length === 0) continue;
            
            config.dientes.forEach(numeroDiente => {
                crearDiente(numeroDiente, parseInt(cuadrante), config.element);
                totalDientes++;
            });
        }
        
        console.log('Odontograma generado con', totalDientes, 'dientes para', tipoDenticionActual);
    }
    
    function crearDiente(numeroDiente, cuadrante, contenedor) {
        const toothContainer = document.createElement('div');
        toothContainer.className = 'tooth-container';
        
        const toothNumber = document.createElement('div');
        toothNumber.className = 'tooth-number';
        toothNumber.textContent = numeroDiente;
        
        const toothElement = document.createElement('div');
        toothElement.className = 'tooth';
        toothElement.dataset.numeroDiente = numeroDiente;
        toothElement.dataset.cuadrante = cuadrante;
        
        const svg = crearSVGDiente(cuadrante, numeroDiente);
        toothElement.appendChild(svg);
        
        toothContainer.appendChild(toothNumber);
        toothContainer.appendChild(toothElement);
        contenedor.appendChild(toothContainer);
        
        if (toothDiagnostics[numeroDiente]) {
            actualizarSVG(svg, numeroDiente, toothElement);
        }
    }
    
    function crearSVGDiente(cuadrante, numeroDiente) {
        const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        svg.setAttribute('viewBox', '0 0 100 100');
        
        const defs = document.createElementNS('http://www.w3.org/2000/svg', 'defs');
        const pattern = document.createElementNS('http://www.w3.org/2000/svg', 'pattern');
        pattern.setAttribute('id', 'obturado-cariado-pattern');
        pattern.setAttribute('patternUnits', 'userSpaceOnUse');
        pattern.setAttribute('width', '10');
        pattern.setAttribute('height', '10');
        
        const rect1 = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
        rect1.setAttribute('width', '10');
        rect1.setAttribute('height', '10');
        rect1.setAttribute('fill', '#f8d7da');
        
        const rect2 = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
        rect2.setAttribute('x', '5');
        rect2.setAttribute('y', '5');
        rect2.setAttribute('width', '5');
        rect2.setAttribute('height', '5');
        rect2.setAttribute('fill', '#fff3cd');
        
        pattern.appendChild(rect1);
        pattern.appendChild(rect2);
        defs.appendChild(pattern);
        svg.appendChild(defs);
        
        const esTemporal = (numeroDiente >= 51 && numeroDiente <= 85);
        const esPosterior = esTemporal ? (numeroDiente % 10 >= 4) : ((numeroDiente % 10) >= 4);
        const labels = getSurfaceLabels(cuadrante, esPosterior);
        
        const outline = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
        outline.setAttribute('x', '5');
        outline.setAttribute('y', '5');
        outline.setAttribute('width', '90');
        outline.setAttribute('height', '90');
        outline.setAttribute('stroke', 'black');
        outline.setAttribute('stroke-width', '2');
        outline.setAttribute('fill', 'none');
        
        const center = crearArea('center', 35, 35, 30, 30);
        const left = crearAreaPath('left', 'M5,5 L35,35 L35,65 L5,95 Z');
        const right = crearAreaPath('right', 'M95,5 L65,35 L65,65 L95,95 Z');
        const top = crearAreaPath('top', 'M5,5 L35,35 L65,35 L95,5 Z');
        const bottom = crearAreaPath('bottom', 'M5,95 L35,65 L65,65 L95,95 Z');
        
        svg.appendChild(outline);
        svg.appendChild(center);
        svg.appendChild(left);
        svg.appendChild(right);
        svg.appendChild(top);
        svg.appendChild(bottom);
        
        agregarEtiquetas(svg, labels);
        
        center.classList.add('sano');
        left.classList.add('sano');
        right.classList.add('sano');
        top.classList.add('sano');
        bottom.classList.add('sano');
        
        return svg;
    }
    
    function crearArea(area, x, y, width, height) {
        const element = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
        element.setAttribute('class', area);
        element.setAttribute('x', x);
        element.setAttribute('y', y);
        element.setAttribute('width', width);
        element.setAttribute('height', height);
        element.setAttribute('fill', 'white');
        return element;
    }
    
    function crearAreaPath(area, d) {
        const element = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        element.setAttribute('class', area);
        element.setAttribute('d', d);
        element.setAttribute('fill', 'white');
        return element;
    }
    
    function getSurfaceLabels(cuadrante, esPosterior) {
        const superficie = esPosterior ? 'O' : 'I';
        switch(cuadrante) {
            case 1: return { top: superficie, bottom: 'P', left: 'M', right: 'D' };
            case 2: return { top: superficie, bottom: 'P', left: 'D', right: 'M' };
            case 3: return { top: 'L', bottom: superficie, left: 'D', right: 'M' };
            case 4: return { top: 'L', bottom: superficie, left: 'M', right: 'D' };
            default: return { top: '', bottom: '', left: '', right: '' };
        }
    }
    
    function agregarEtiquetas(svg, labels) {
        const textos = [
            { x: 50, y: 15, text: labels.top, rotate: 0 },
            { x: 50, y: 90, text: labels.bottom, rotate: 0 },
            { x: 15, y: 50, text: labels.left, rotate: -90 },
            { x: 85, y: 50, text: labels.right, rotate: 90 }
        ];
        
        textos.forEach(t => {
            if (t.text) {
                const text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                text.setAttribute('x', t.x);
                text.setAttribute('y', t.y);
                text.setAttribute('text-anchor', 'middle');
                text.setAttribute('class', 'surface-label');
                if (t.rotate !== 0) {
                    text.setAttribute('transform', `rotate(${t.rotate}, ${t.x}, ${t.y})`);
                }
                text.textContent = t.text;
                svg.appendChild(text);
            }
        });
    }
    
    function actualizarSVG(svg, numeroDiente, toothElement) {
        const areas = ['center', 'left', 'right', 'top', 'bottom'];
        
        let tienePerdido = false;
        let perdidoType = null;
        
        for (const area of areas) {
            const diagnosis = toothDiagnostics[numeroDiente][area];
            if (perdidosDiagnostics.includes(diagnosis)) {
                tienePerdido = true;
                perdidoType = diagnosis;
                break;
            }
        }
        
        if (tienePerdido && toothElement) {
            toothElement.classList.remove('extraccion', 'extraido', 'perdido_otra');
            toothElement.classList.add(perdidoType);
            
            areas.forEach(area => {
                const areaElement = svg.querySelector(`.${area}`);
                if (areaElement) {
                    const clasesDiagnostico = Object.keys(severidadMap);
                    clasesDiagnostico.forEach(cls => {
                        areaElement.classList.remove(cls);
                    });
                    areaElement.classList.add(perdidoType);
                }
            });
        } else {
            if (toothElement) {
                toothElement.classList.remove('extraccion', 'extraido', 'perdido_otra');
            }
            areas.forEach(area => {
                const areaElement = svg.querySelector(`.${area}`);
                if (areaElement) {
                    const clasesDiagnostico = Object.keys(severidadMap);
                    clasesDiagnostico.forEach(cls => {
                        areaElement.classList.remove(cls);
                    });
                    const diagnosis = toothDiagnostics[numeroDiente][area];
                    areaElement.classList.add(diagnosis);
                }
            });
        }
    }
    
    function obtenerDiagnosticoPrincipal(numeroDiente) {
        const areas = toothDiagnostics[numeroDiente];
        let maxSeveridad = -1;
        let diagnosticoPrincipal = 'sano';
        
        for (const [area, diagnosis] of Object.entries(areas)) {
            const severidad = severidadMap[diagnosis] || 0;
            if (severidad > maxSeveridad) {
                maxSeveridad = severidad;
                diagnosticoPrincipal = diagnosis;
            }
        }
        
        return { diagnostico: diagnosticoPrincipal, severidad: maxSeveridad };
    }
    
    // ===== EVENTOS DEL ODONTOGRAMA =====
    document.addEventListener('click', function(e) {
        const areaElement = e.target.closest('.center, .left, .right, .top, .bottom');
        if (areaElement) {
            const toothDiv = areaElement.closest('.tooth');
            if (toothDiv) {
                document.querySelectorAll('.tooth').forEach(t => t.classList.remove('selected'));
                toothDiv.classList.add('selected');
                selectedTooth = toothDiv;
                
                let clickedArea = null;
                if (areaElement.classList.contains('center')) clickedArea = 'center';
                else if (areaElement.classList.contains('left')) clickedArea = 'left';
                else if (areaElement.classList.contains('right')) clickedArea = 'right';
                else if (areaElement.classList.contains('top')) clickedArea = 'top';
                else if (areaElement.classList.contains('bottom')) clickedArea = 'bottom';
                
                if (clickedArea) {
                    selectedArea = clickedArea;
                    document.querySelectorAll('.area-btn').forEach(btn => {
                        if (btn.dataset.area === clickedArea) {
                            btn.classList.add('active');
                        } else {
                            btn.classList.remove('active');
                        }
                    });
                    document.getElementById('areaSelector').style.display = 'flex';
                    document.getElementById('currentSelection').textContent = 
                        `Diente ${toothDiv.dataset.numeroDiente}, área ${clickedArea} seleccionada. Elija un diagnóstico.`;
                } else {
                    document.getElementById('areaSelector').style.display = 'flex';
                    selectedArea = null;
                    document.getElementById('currentSelection').textContent = 
                        `Diente ${toothDiv.dataset.numeroDiente} seleccionado. Seleccione un área.`;
                }
            }
        }
    });
    
    document.querySelectorAll('.area-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            if (!selectedTooth) {
                alert('Primero seleccione un diente');
                return;
            }
            document.querySelectorAll('.area-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            selectedArea = this.dataset.area;
            document.getElementById('currentSelection').textContent = 
                `Diente ${selectedTooth.dataset.numeroDiente}, área ${selectedArea}. Seleccione un diagnóstico.`;
        });
    });
    
    // ===== GUARDAR DIAGNÓSTICO - CORREGIDO =====
    document.querySelectorAll('.criteria-item').forEach(criteria => {
        criteria.addEventListener('click', function() {
            if (!selectedTooth) {
                alert('Primero seleccione un diente');
                return;
            }
            if (!selectedArea) {
                alert('Primero seleccione un área del diente');
                return;
            }
            
            const diagnosis = this.dataset.state;
            const numeroDiente = parseInt(selectedTooth.dataset.numeroDiente);
            
            // Guardar en el conjunto actual
            toothDiagnostics[numeroDiente][selectedArea] = diagnosis;
            
            // ⭐ CRÍTICO: Sincronizar con el almacenamiento global
            window.toothDiagnostics[tipoDenticionActual] = JSON.parse(JSON.stringify(toothDiagnostics));
            
            console.log(`✅ GUARDADO: Diente ${numeroDiente}, área ${selectedArea} = ${diagnosis} (${tipoDenticionActual})`);
            
            const svg = selectedTooth.querySelector('svg');
            actualizarSVG(svg, numeroDiente, selectedTooth);
            
            document.getElementById('currentSelection').textContent = 
                `Diente ${numeroDiente}, área ${selectedArea} marcada como: ${this.querySelector('.criteria-label').textContent}`;
            
            // Recargar índices
            calcularIndices();
        });
    });
    
    // ===== CALCULAR ÍNDICES - CORREGIDO =====
    function calcularIndices() {
        let cariados = 0, perdidos = 0, obturados = 0;
        let ceoCariados = 0, ceoExtraidos = 0, ceoObturados = 0;
        
        // Obtener datos de AMBOS tipos
        const permanente = window.toothDiagnostics.permanente || {};
        const temporal = window.toothDiagnostics.temporal || {};
        
        console.log('📊 CALCULANDO ÍNDICES - Permanentes:', Object.keys(permanente).length, 'Temporales:', Object.keys(temporal).length);
        
        // Procesar permanentes
        for (const [numeroDiente, areas] of Object.entries(permanente)) {
            const esPermanente = (parseInt(numeroDiente) >= 11 && parseInt(numeroDiente) <= 48);
            if (!esPermanente) continue;
            
            let maxSeveridad = 0;
            let diagnosticoPrincipal = 'sano';
            
            for (const [area, diagnosis] of Object.entries(areas)) {
                const severidad = severidadMap[diagnosis] || 0;
                if (severidad > maxSeveridad) {
                    maxSeveridad = severidad;
                    diagnosticoPrincipal = diagnosis;
                }
            }
            
            if (maxSeveridad > 0 && diagnosticoPrincipal !== 'sano') {
                const tipo = cpoMap[diagnosticoPrincipal];
                if (tipo === 'C') cariados++;
                else if (tipo === 'P') perdidos++;
                else if (tipo === 'O') obturados++;
            }
        }
        
        // Procesar temporales
        for (const [numeroDiente, areas] of Object.entries(temporal)) {
            const esTemporal = (parseInt(numeroDiente) >= 51 && parseInt(numeroDiente) <= 85);
            if (!esTemporal) continue;
            
            let maxSeveridad = 0;
            let diagnosticoPrincipal = 'sano';
            
            for (const [area, diagnosis] of Object.entries(areas)) {
                const severidad = severidadMap[diagnosis] || 0;
                if (severidad > maxSeveridad) {
                    maxSeveridad = severidad;
                    diagnosticoPrincipal = diagnosis;
                }
            }
            
            if (maxSeveridad > 0 && diagnosticoPrincipal !== 'sano') {
                const tipo = ceoMap[diagnosticoPrincipal];
                if (tipo === 'C') ceoCariados++;
                else if (tipo === 'E') ceoExtraidos++;
                else if (tipo === 'O') ceoObturados++;
            }
        }
        
        // Calcular sanos y totales
        const totalDientesPermanentes = 32;
        const totalDientesTemporales = 20;
        const totalCariados = cariados + perdidos + obturados;
        const totalCEO = ceoCariados + ceoExtraidos + ceoObturados;
        
        const dientesSanosPermanentes = Math.max(0, totalDientesPermanentes - totalCariados);
        const dientesSanosTemporales = Math.max(0, totalDientesTemporales - totalCEO);
        
        document.getElementById('cValue').value = cariados;
        document.getElementById('pValue').value = perdidos;
        document.getElementById('oValue').value = obturados;
        document.getElementById('sValue').value = dientesSanosPermanentes;
        document.getElementById('totalDientes').value = totalDientesPermanentes;
        document.getElementById('cpodTotal').textContent = totalCariados;
        
        document.getElementById('ceoCariados').value = ceoCariados;
        document.getElementById('ceoExtraidos').value = ceoExtraidos;
        document.getElementById('ceoObturados').value = ceoObturados;
        document.getElementById('ceoSanos').value = dientesSanosTemporales;
        document.getElementById('totalDientesTemp').value = totalDientesTemporales;
        document.getElementById('ceoTotal').textContent = totalCEO;
        
        console.log(`📊 ÍNDICES - CPO: C:${cariados}, P:${perdidos}, O:${obturados} | CEO: C:${ceoCariados}, E:${ceoExtraidos}, O:${ceoObturados}`);
    }
    
    // ===== CAMBIAR ENTRE DENTICIONES =====
    document.getElementById('tipoDenticion').addEventListener('change', function() {
        // ⭐ CRÍTICO: Guardar el estado actual ANTES de cambiar
        window.toothDiagnostics[tipoDenticionActual] = JSON.parse(JSON.stringify(toothDiagnostics));
        console.log(`💾 GUARDADO estado de ${tipoDenticionActual}: ${Object.keys(toothDiagnostics).length} dientes`);
        
        tipoDenticionActual = this.value;
        const esPermanente = tipoDenticionActual === 'permanente';
        
        if (esPermanente) {
            document.getElementById('titleQuadrant1').textContent = 'CUADRANTE 1 - SUPERIOR DERECHO (11-18)';
            document.getElementById('titleQuadrant2').textContent = 'CUADRANTE 2 - SUPERIOR IZQUIERDO (21-28)';
            document.getElementById('titleQuadrant3').textContent = 'CUADRANTE 3 - INFERIOR IZQUIERDO (31-38)';
            document.getElementById('titleQuadrant4').textContent = 'CUADRANTE 4 - INFERIOR DERECHO (41-48)';
            currentCuadrantes = cuadrantesAdultos;
        } else {
            document.getElementById('titleQuadrant1').textContent = 'CUADRANTE 1 - SUPERIOR DERECHO (51-55)';
            document.getElementById('titleQuadrant2').textContent = 'CUADRANTE 2 - SUPERIOR IZQUIERDO (61-65)';
            document.getElementById('titleQuadrant3').textContent = 'CUADRANTE 3 - INFERIOR IZQUIERDO (81-85)';
            document.getElementById('titleQuadrant4').textContent = 'CUADRANTE 4 - INFERIOR DERECHO (71-75)';
            currentCuadrantes = cuadrantesNinos;
        }
        
        document.getElementById('cpoGroup').style.display = esPermanente ? 'block' : 'none';
        document.getElementById('ceoGroup').style.display = esPermanente ? 'none' : 'block';
        
        // Obtener los datos del nuevo tipo
        toothDiagnostics = window.toothDiagnostics[tipoDenticionActual];
        console.log(`🔄 CAMBIADO a ${tipoDenticionActual}: ${Object.keys(toothDiagnostics).length} dientes`);
        
        generarOdontograma();
        calcularIndices();
    });
    
    // ===== FUNCIÓN PARA OBTENER DATOS DE AMBOS TIPOS =====
    function obtenerDatosCompletos() {
        // Guardar el estado actual antes de obtener los datos
        window.toothDiagnostics[tipoDenticionActual] = JSON.parse(JSON.stringify(toothDiagnostics));
        
        console.log('📊 window.toothDiagnostics COMPLETO:', {
            permanente: Object.keys(window.toothDiagnostics.permanente || {}).length,
            temporal: Object.keys(window.toothDiagnostics.temporal || {}).length
        });
        
        const permanente = window.toothDiagnostics.permanente || {};
        const temporal = window.toothDiagnostics.temporal || {};
        
        // Procesar permanentes
        const dientesPermanentes = [];
        for (const [numeroDiente, areas] of Object.entries(permanente)) {
            const carasData = [];
            let tieneProblema = false;
            
            for (const [cara, diagnosis] of Object.entries(areas)) {
                if (diagnosis !== 'sano') {
                    tieneProblema = true;
                    carasData.push({ cara: cara, diagnostico: diagnosis });
                }
            }
            
            if (tieneProblema) {
                dientesPermanentes.push({
                    numero_diente: parseInt(numeroDiente),
                    caras: carasData
                });
            }
        }
        
        // Procesar temporales
        const dientesTemporales = [];
        for (const [numeroDiente, areas] of Object.entries(temporal)) {
            const carasData = [];
            let tieneProblema = false;
            
            for (const [cara, diagnosis] of Object.entries(areas)) {
                if (diagnosis !== 'sano') {
                    tieneProblema = true;
                    carasData.push({ cara: cara, diagnostico: diagnosis });
                }
            }
            
            if (tieneProblema) {
                dientesTemporales.push({
                    numero_diente: parseInt(numeroDiente),
                    caras: carasData
                });
            }
        }
        
        console.log('📊 DATOS A ENVIAR:', {
            permanentes: dientesPermanentes.length,
            temporales: dientesTemporales.length
        });
        
        return {
            dientesPermanentes: dientesPermanentes,
            dientesTemporales: dientesTemporales
        };
    }
    
    // Exponer funciones globales
    window.obtenerDatosCompletos = obtenerDatosCompletos;
    window.obtenerTipoDenticionActual = function() { return tipoDenticionActual; };
    window.obtenerToothDiagnostics = function() { return window.toothDiagnostics; };
    window.calcularIndicesGlobal = calcularIndices;
    
    // Guardar revisión del odontograma (BOTÓN INDEPENDIENTE - ahora solo informativo)
    document.getElementById('saveOdontograma').addEventListener('click', async function() {
        showNotification('info', 'El odontograma se guardará junto con la consulta al hacer clic en "GUARDAR HISTORIA CLÍNICA"', 'Información');
    });
    
    // Cargar revisión del odontograma
    document.getElementById('cargarRevisionBtn').addEventListener('click', async function() {
        const idRevision = document.getElementById('idRevisionCargar').value;
        if (!idRevision) {
            showNotification('warning', 'Ingrese un ID de revisión', 'Aviso');
            return;
        }
        
        try {
            const response = await fetch(`/api/consultas/revision/${idRevision}`);
            const data = await response.json();
            
            if (data.success) {
                // Resetear diagnóstico del tipo actual
                const datosActuales = window.toothDiagnostics[tipoDenticionActual] || {};
                for (const [numeroDiente, areas] of Object.entries(datosActuales)) {
                    for (const area of Object.keys(areas)) {
                        datosActuales[numeroDiente][area] = 'sano';
                    }
                }
                
                if (data.dientes) {
                    data.dientes.forEach(diente => {
                        const numeroDiente = diente.numero_diente;
                        if (datosActuales[numeroDiente]) {
                            diente.caras.forEach(cara => {
                                if (datosActuales[numeroDiente][cara.cara]) {
                                    datosActuales[numeroDiente][cara.cara] = cara.diagnostico;
                                }
                            });
                        }
                    });
                }
                
                window.toothDiagnostics[tipoDenticionActual] = datosActuales;
                toothDiagnostics = datosActuales;
                
                document.querySelectorAll('.tooth').forEach(tooth => {
                    const numeroDiente = parseInt(tooth.dataset.numeroDiente);
                    const svg = tooth.querySelector('svg');
                    actualizarSVG(svg, numeroDiente, tooth);
                });
                
                if (data.diagnostico) {
                    if (data.diagnostico.cpo_cariados !== undefined) {
                        document.getElementById('cValue').value = data.diagnostico.cpo_cariados || 0;
                        document.getElementById('pValue').value = data.diagnostico.cpo_perdidos || 0;
                        document.getElementById('oValue').value = data.diagnostico.cpo_obturados || 0;
                        document.getElementById('cpodTotal').textContent = data.diagnostico.cpo_total || 0;
                    } else {
                        document.getElementById('ceoCariados').value = data.diagnostico.ceo_cariados || 0;
                        document.getElementById('ceoExtraidos').value = data.diagnostico.ceo_extraidos || 0;
                        document.getElementById('ceoObturados').value = data.diagnostico.ceo_obturados || 0;
                        document.getElementById('ceoTotal').textContent = data.diagnostico.ceo_total || 0;
                    }
                }
                
                calcularIndices();
                showNotification('success', '✅ Revisión cargada exitosamente', 'Éxito');
            } else {
                showNotification('error', 'No se encontró la revisión', 'Error');
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification('error', 'Error al cargar la revisión', 'Error');
        }
    });
    
    // Limpiar todo el odontograma (SOLO el tipo actual)
    document.getElementById('clearAll').addEventListener('click', function() {
        const datosActuales = window.toothDiagnostics[tipoDenticionActual] || {};
        for (const [numeroDiente, areas] of Object.entries(datosActuales)) {
            for (const area of Object.keys(areas)) {
                datosActuales[numeroDiente][area] = 'sano';
            }
        }
        window.toothDiagnostics[tipoDenticionActual] = datosActuales;
        toothDiagnostics = datosActuales;
        
        document.querySelectorAll('.tooth').forEach(tooth => {
            const numeroDiente = parseInt(tooth.dataset.numeroDiente);
            const svg = tooth.querySelector('svg');
            actualizarSVG(svg, numeroDiente, tooth);
        });
        
        document.getElementById('cValue').value = '0';
        document.getElementById('pValue').value = '0';
        document.getElementById('oValue').value = '0';
        document.getElementById('sValue').value = '32';
        document.getElementById('totalDientes').value = '32';
        document.getElementById('cpodTotal').textContent = '0';
        document.getElementById('ceoCariados').value = '0';
        document.getElementById('ceoExtraidos').value = '0';
        document.getElementById('ceoObturados').value = '0';
        document.getElementById('ceoSanos').value = '20';
        document.getElementById('totalDientesTemp').value = '20';
        document.getElementById('ceoTotal').textContent = '0';
        
        selectedTooth = null;
        selectedArea = null;
        document.getElementById('areaSelector').style.display = 'none';
        document.getElementById('currentSelection').textContent = 'Seleccione un diente y luego elija un diagnóstico';
        
        calcularIndices();
        showNotification('info', 'Odontograma limpiado correctamente', 'Aviso');
    });
    
    document.getElementById('calcularIndices').addEventListener('click', calcularIndices);
    
    // Función para mostrar notificaciones (si no existe)
    if (typeof showNotification === 'undefined') {
        window.showNotification = function(type, message, title = '') {
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
        };
    }
    
    // ===== INICIALIZAR =====
    console.log('🚀 Inicializando odontograma...');
    console.log('📊 Datos iniciales:', {
        permanente: Object.keys(window.toothDiagnostics.permanente).length,
        temporal: Object.keys(window.toothDiagnostics.temporal).length
    });
    generarOdontograma();
    calcularIndices();
})();