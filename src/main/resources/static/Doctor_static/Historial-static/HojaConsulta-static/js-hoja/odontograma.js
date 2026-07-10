// ============================================================
// FUNCIONES PARA EL ODONTOGRAMA
// ============================================================

// Variables globales
let odontogramaData = {};
let dienteSeleccionado = null;

// ============================================================
// FUNCIÓN PARA CARGAR FOTOS DEL ODONTOGRAMA
// ============================================================
function cargarFotosOdontograma() {
    const contenedor = document.getElementById('contenedor-fotos-odontograma');
    
    // Ejemplo: Array con URLs de imágenes (puedes reemplazar con datos reales)
    const fotos = [
        // { url: 'ruta/de/la/imagen1.jpg', label: 'Foto 1' },
        // { url: 'ruta/de/la/imagen2.jpg', label: 'Foto 2' },
        // { url: 'ruta/de/la/imagen3.jpg', label: 'Foto 3' }
    ];

    if (!fotos || fotos.length === 0) {
        contenedor.innerHTML = '<div class="mensaje-sin-fotos">No hay fotos disponibles</div>';
        return;
    }

    contenedor.innerHTML = '';

    fotos.forEach((foto, index) => {
        const fotoItem = document.createElement('div');
        fotoItem.className = 'foto-item';

        const img = document.createElement('img');
        img.src = foto.url;
        img.alt = foto.label || `Foto ${index + 1}`;
        img.loading = 'lazy';

        const label = document.createElement('span');
        label.className = 'foto-label';
        label.textContent = foto.label || `Foto ${index + 1}`;

        fotoItem.appendChild(img);
        fotoItem.appendChild(label);
        contenedor.appendChild(fotoItem);
    });
}

// ============================================================
// FUNCIONES PARA GENERAR EL ODONTOGRAMA (DIBUJAR DIENTES)
// ============================================================
function generarOdontograma() {
    const cuadrantes = [
        { id: 'dentalChart1', dientes: [11, 12, 13, 14, 15, 16, 17, 18], titulo: 'CUADRANTE 1 - SUPERIOR DERECHO (11-18)' },
        { id: 'dentalChart2', dientes: [21, 22, 23, 24, 25, 26, 27, 28], titulo: 'CUADRANTE 2 - SUPERIOR IZQUIERDO (21-28)' },
        { id: 'dentalChart3', dientes: [31, 32, 33, 34, 35, 36, 37, 38], titulo: 'CUADRANTE 3 - INFERIOR IZQUIERDO (31-38)' },
        { id: 'dentalChart4', dientes: [41, 42, 43, 44, 45, 46, 47, 48], titulo: 'CUADRANTE 4 - INFERIOR DERECHO (41-48)' }
    ];

    cuadrantes.forEach(cuadrante => {
        const container = document.getElementById(cuadrante.id);
        if (!container) return;

        container.innerHTML = '';
        
        cuadrante.dientes.forEach(numero => {
            const toothContainer = document.createElement('div');
            toothContainer.className = 'tooth-container';

            const numberLabel = document.createElement('div');
            numberLabel.className = 'tooth-number';
            numberLabel.textContent = numero;
            toothContainer.appendChild(numberLabel);

            // SVG del diente
            const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
            svg.setAttribute('viewBox', '0 0 60 70');
            svg.setAttribute('class', 'tooth');
            svg.setAttribute('data-tooth', numero);
            svg.style.width = '28px';
            svg.style.height = '28px';

            // Definir las áreas del diente
            const areas = [
                { id: 'center', d: 'M15,5 L45,5 L45,50 L30,65 L15,50 Z', label: 'C' },
                { id: 'top', d: 'M15,5 L45,5 L45,15 L15,15 Z', label: 'S' },
                { id: 'bottom', d: 'M15,50 L45,50 L30,65 L15,50 Z', label: 'I' },
                { id: 'left', d: 'M15,5 L25,5 L25,50 L15,50 Z', label: 'L' },
                { id: 'right', d: 'M35,5 L45,5 L45,50 L35,50 Z', label: 'R' }
            ];

            areas.forEach(area => {
                const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
                path.setAttribute('d', area.d);
                path.setAttribute('class', area.id);
                path.setAttribute('data-area', area.id);
                path.setAttribute('data-tooth', numero);
                path.style.fill = '#d4edda';
                path.style.stroke = '#000';
                path.style.strokeWidth = '1.5';
                svg.appendChild(path);

                // Etiqueta de la superficie
                const label = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                label.setAttribute('class', 'surface-label');
                label.setAttribute('x', '30');
                label.setAttribute('y', '35');
                label.setAttribute('text-anchor', 'middle');
                label.setAttribute('dominant-baseline', 'middle');
                label.textContent = area.label;
                svg.appendChild(label);
            });

            // Evento click para seleccionar diente
            svg.addEventListener('click', function(e) {
                const toothNumber = this.getAttribute('data-tooth');
                seleccionarDiente(toothNumber);
            });

            toothContainer.appendChild(svg);
            container.appendChild(toothContainer);
        });
    });
}

// ============================================================
// FUNCIÓN PARA SELECCIONAR DIENTE
// ============================================================
function seleccionarDiente(numero) {
    // Quitar selección anterior
    document.querySelectorAll('.tooth.selected').forEach(el => {
        el.classList.remove('selected');
    });

    // Seleccionar nuevo diente
    const tooth = document.querySelector(`.tooth[data-tooth="${numero}"]`);
    if (tooth) {
        tooth.classList.add('selected');
        dienteSeleccionado = numero;
        document.getElementById('currentSelection').textContent = `Diente seleccionado: ${numero}`;
        document.getElementById('areaSelector').style.display = 'flex';
    }
}

// ============================================================
// FUNCIONES PARA APLICAR DIAGNÓSTICO
// ============================================================
function aplicarDiagnostico(state) {
    if (!dienteSeleccionado) {
        alert('Primero seleccione un diente');
        return;
    }

    const areaActiva = document.querySelector('.area-btn.active');
    if (!areaActiva) {
        alert('Primero seleccione un área del diente');
        return;
    }

    const area = areaActiva.getAttribute('data-area');
    const tooth = document.querySelector(`.tooth[data-tooth="${dienteSeleccionado}"]`);
    
    if (tooth) {
        const areaElement = tooth.querySelector(`[data-area="${area}"]`);
        if (areaElement) {
            // Quitar clases anteriores
            areaElement.classList.remove('sano', 'cariado', 'obturado', 'obturado_cariado', 
                'extraccion', 'extraido', 'perdido_otra', 'endodoncia', 'corona');
            // Agregar nueva clase
            areaElement.classList.add(state);
            
            // Actualizar el estado del diente en el objeto odontogramaData
            if (!odontogramaData[dienteSeleccionado]) {
                odontogramaData[dienteSeleccionado] = {};
            }
            odontogramaData[dienteSeleccionado][area] = state;
            
            actualizarIndices();
            mostrarToast(`Diagnóstico aplicado: ${state} en diente ${dienteSeleccionado} - ${area}`);
        }
    }
}

function aplicarArea(area) {
    // Solo registra el área seleccionada
    console.log(`Área seleccionada: ${area}`);
}

// ============================================================
// FUNCIÓN PARA ACTUALIZAR ÍNDICES
// ============================================================
function actualizarIndices() {
    let cariados = 0, perdidos = 0, obturados = 0, sanos = 0;
    const totalDientes = 32;

    Object.keys(odontogramaData).forEach(toothId => {
        const tooth = odontogramaData[toothId];
        let toothCariado = false, toothPerdido = false, toothObturado = false;
        let toothSano = true;

        Object.keys(tooth).forEach(area => {
            const state = tooth[area];
            if (state === 'cariado' || state === 'obturado_cariado') {
                toothCariado = true;
                toothSano = false;
            }
            if (state === 'extraccion' || state === 'extraido' || state === 'perdido_otra') {
                toothPerdido = true;
                toothSano = false;
            }
            if (state === 'obturado' || state === 'obturado_cariado' || 
                state === 'endodoncia' || state === 'corona') {
                toothObturado = true;
                toothSano = false;
            }
        });

        if (toothCariado) cariados++;
        if (toothPerdido) perdidos++;
        if (toothObturado) obturados++;
        if (toothSano) sanos++;
    });

    const cValue = document.getElementById('cValue');
    const pValue = document.getElementById('pValue');
    const oValue = document.getElementById('oValue');
    const sValue = document.getElementById('sValue');
    const totalDientesInput = document.getElementById('totalDientes');
    const cpodTotal = document.getElementById('cpodTotal');

    if (cValue) cValue.value = cariados;
    if (pValue) pValue.value = perdidos;
    if (oValue) oValue.value = obturados;
    if (sValue) sValue.value = sanos;
    if (totalDientesInput) totalDientesInput.value = totalDientes;
    
    const cpodTotalValue = cariados + perdidos + obturados;
    if (cpodTotal) cpodTotal.textContent = cpodTotalValue;
}

// ============================================================
// FUNCIÓN PARA MOSTRAR TOAST
// ============================================================
function mostrarToast(mensaje) {
    console.log('✅ ' + mensaje);
}

// ============================================================
// INICIALIZACIÓN
// ============================================================
document.addEventListener('DOMContentLoaded', function() {
    // Generar odontograma
    generarOdontograma();
    
    // Cargar fotos
    cargarFotosOdontograma();

    // Eventos para criterios de diagnóstico
    document.querySelectorAll('.criteria-item').forEach(item => {
        item.addEventListener('click', function() {
            const state = this.getAttribute('data-state');
            aplicarDiagnostico(state);
        });
    });

    // Eventos para áreas
    document.querySelectorAll('.area-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            document.querySelectorAll('.area-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            const area = this.getAttribute('data-area');
            aplicarArea(area);
        });
    });
});

console.log('🦷 Odontograma cargado correctamente');