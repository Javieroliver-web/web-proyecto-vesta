/**
 * Sistema de Modales Reutilizable para Vesta
 * Reemplaza los alerts nativos por modales de Bootstrap elegantes
 */

// Contador para IDs únicos de modales
let modalCounter = 0;

/**
 * Crea y muestra un modal genérico
 * @param {Object} options - Configuración del modal
 */
function createModal(options) {
    const {
        title = 'Notificación',
        message = '',
        type = 'info', // info, success, error, warning, confirm
        confirmText = 'Aceptar',
        cancelText = 'Cancelar',
        onConfirm = null,
        onCancel = null,
        showCancel = false
    } = options;

    const modalId = `dynamicModal${modalCounter++}`;

    // Colores según el tipo
    const headerColors = {
        info: 'bg-primary text-white',
        success: 'bg-success text-white',
        error: 'bg-danger text-white',
        warning: 'bg-warning text-dark',
        confirm: 'bg-primary text-white'
    };

    // Iconos según el tipo
    const icons = {
        info: '<i class="bi bi-info-circle-fill me-2"></i>',
        success: '<i class="bi bi-check-circle-fill me-2"></i>',
        error: '<i class="bi bi-x-circle-fill me-2"></i>',
        warning: '<i class="bi bi-exclamation-triangle-fill me-2"></i>',
        confirm: '<i class="bi bi-question-circle-fill me-2"></i>'
    };

    const headerClass = headerColors[type] || headerColors.info;
    const icon = icons[type] || icons.info;

    // Crear HTML del modal
    const modalHTML = `
        <div class="modal fade" id="${modalId}" tabindex="-1" data-bs-backdrop="static" data-bs-keyboard="false">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow-lg">
                    <div class="modal-header ${headerClass}">
                        <h5 class="modal-title">${icon}${title}</h5>
                        <button type="button" class="btn-close ${type === 'error' || type === 'success' || type === 'info' ? 'btn-close-white' : ''}" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p class="mb-0">${message}</p>
                    </div>
                    <div class="modal-footer border-0">
                        ${showCancel ? `<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">${cancelText}</button>` : ''}
                        <button type="button" class="btn btn-${type === 'error' ? 'danger' : type === 'success' ? 'success' : 'primary'}" id="${modalId}Confirm">${confirmText}</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Agregar modal al DOM
    document.body.insertAdjacentHTML('beforeend', modalHTML);

    const modalElement = document.getElementById(modalId);
    const modal = new bootstrap.Modal(modalElement);

    // Manejar botón de confirmación
    const confirmBtn = document.getElementById(`${modalId}Confirm`);
    confirmBtn.addEventListener('click', () => {
        if (onConfirm) onConfirm();
        modal.hide();
    });

    // Manejar cierre del modal
    modalElement.addEventListener('hidden.bs.modal', () => {
        if (onCancel && showCancel) onCancel();
        modalElement.remove();
    });

    // Mostrar modal
    modal.show();

    return modal;
}

/**
 * Muestra un modal informativo
 * @param {string} title - Título del modal
 * @param {string} message - Mensaje a mostrar
 */
function showInfoModal(title, message) {
    return createModal({
        title,
        message,
        type: 'info',
        confirmText: 'Entendido'
    });
}

/**
 * Muestra un modal de éxito
 * @param {string} title - Título del modal
 * @param {string} message - Mensaje a mostrar
 */
function showSuccessModal(title, message) {
    return createModal({
        title,
        message,
        type: 'success',
        confirmText: 'Aceptar'
    });
}

/**
 * Muestra un modal de error
 * @param {string} title - Título del modal
 * @param {string} message - Mensaje de error
 */
function showErrorModal(title, message) {
    return createModal({
        title,
        message,
        type: 'error',
        confirmText: 'Cerrar'
    });
}

/**
 * Muestra un modal de advertencia
 * @param {string} title - Título del modal
 * @param {string} message - Mensaje de advertencia
 */
function showWarningModal(title, message) {
    return createModal({
        title,
        message,
        type: 'warning',
        confirmText: 'Entendido'
    });
}

/**
 * Muestra un modal de confirmación con opciones Sí/No
 * @param {string} title - Título del modal
 * @param {string} message - Pregunta de confirmación
 * @param {Function} onConfirm - Callback al confirmar
 * @param {Function} onCancel - Callback al cancelar (opcional)
 */
function showConfirmModal(title, message, onConfirm, onCancel = null) {
    return createModal({
        title,
        message,
        type: 'confirm',
        confirmText: 'Sí',
        cancelText: 'No',
        showCancel: true,
        onConfirm,
        onCancel
    });
}

/**
 * Muestra un modal de borrado seguro que requiere escribir una palabra clave
 * @param {string} title - Título del modal
 * @param {string} message - Mensaje explicativo
 * @param {string} requiredText - Palabra a escribir (ej: "ELIMINAR")
 * @param {Function} onConfirm - Callback al confirmar
 * @param {string} buttonText - Texto del botón de confirmación (opcional, por defecto "Eliminar Definitivamente")
 */
function showSafeDeleteModal(title, message, requiredText, onConfirm, buttonText = 'Eliminar Definitivamente') {
    const modalId = `safeDelete${modalCounter++}`;

    const modalHTML = `
        <div class="modal fade" id="${modalId}" tabindex="-1" data-bs-backdrop="static">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow-lg border-start border-4 border-danger">
                    <div class="modal-header bg-light text-danger">
                        <h5 class="modal-title"><i class="bi bi-exclamation-octagon-fill me-2"></i>${title}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p class="mb-3">${message}</p>
                        <div class="alert alert-warning py-2 mb-3 small">
                            <i class="bi bi-pencil-fill me-1"></i>
                            Para confirmar, escribe <strong>"${requiredText}"</strong> abajo:
                        </div>
                        <input type="text" 
                               class="form-control form-control-lg text-center fw-bold border-danger" 
                               id="${modalId}Input" 
                               placeholder="${requiredText}" 
                               autocomplete="off">
                    </div>
                    <div class="modal-footer bg-light border-0">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-danger disabled" id="${modalId}Confirm" disabled>
                            <i class="bi bi-trash-fill me-2"></i>${buttonText}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHTML);
    const modalElement = document.getElementById(modalId);
    const modal = new bootstrap.Modal(modalElement);
    const input = document.getElementById(`${modalId}Input`);
    const confirmBtn = document.getElementById(`${modalId}Confirm`);

    // Lógica de validación
    input.addEventListener('input', (e) => {
        if (e.target.value === requiredText) {
            confirmBtn.classList.remove('disabled');
            confirmBtn.removeAttribute('disabled');
        } else {
            confirmBtn.classList.add('disabled');
            confirmBtn.setAttribute('disabled', 'true');
        }
    });

    // Enter para enviar si está habilitado
    input.addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && input.value === requiredText) {
            confirmBtn.click();
        }
    });

    confirmBtn.addEventListener('click', () => {
        if (onConfirm) onConfirm();
        modal.hide();
    });

    modalElement.addEventListener('hidden.bs.modal', () => {
        modalElement.remove();
    });

    modalElement.addEventListener('shown.bs.modal', () => {
        input.focus();
    });

    modal.show();
    return modal;
}

// Exportar funciones para uso global
window.showInfoModal = showInfoModal;
window.showSuccessModal = showSuccessModal;
window.showErrorModal = showErrorModal;
window.showWarningModal = showWarningModal;
window.showConfirmModal = showConfirmModal;
window.showSafeDeleteModal = showSafeDeleteModal;
