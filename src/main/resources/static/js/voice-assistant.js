/**
 * VESTA VOICE ASSISTANT
 * Asistente de voz inteligente con reconocimiento y síntesis de voz
 * Ubicación: vesta-web/src/main/resources/static/js/voice-assistant.js
 */

// ============================================
// 1. CONFIGURACIÓN INICIAL
// ============================================

const VoiceAssistant = {
    // Instancias de APIs
    recognition: null,
    synthesis: window.speechSynthesis,
    
    // Estado
    isListening: false,
    isProcessing: false,
    isSpeaking: false,
    
    // Configuración
    config: {
        language: 'es-ES',
        apiBaseUrl: 'http://localhost:8080/api',
        maxRetries: 3,
        speechRate: 0.9,
        speechPitch: 1.0,
        speechVolume: 1.0
    },
    
    // Callbacks
    onStart: null,
    onEnd: null,
    onError: null,
    onResult: null
};

// ============================================
// 2. INICIALIZACIÓN
// ============================================

VoiceAssistant.init = function(options = {}) {
    console.log('🎤 Inicializando Asistente de Voz Vesta...');
    
    // Combinar configuración
    Object.assign(this.config, options);
    
    // Verificar compatibilidad del navegador
    if (!this.checkBrowserSupport()) {
        console.error('❌ Tu navegador no soporta reconocimiento de voz');
        return false;
    }
    
    // Inicializar reconocimiento de voz
    this.initSpeechRecognition();
    
    // Crear interfaz visual (opcional)
    if (options.createUI !== false) {
        this.createUI();
    }
    
    console.log('✅ Asistente de Voz iniciado correctamente');
    return true;
};

VoiceAssistant.checkBrowserSupport = function() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    
    if (!SpeechRecognition) {
        alert('❌ Tu navegador no soporta reconocimiento de voz. Usa Chrome, Edge o Safari.');
        return false;
    }
    
    if (!this.synthesis) {
        alert('❌ Tu navegador no soporta síntesis de voz.');
        return false;
    }
    
    return true;
};

VoiceAssistant.initSpeechRecognition = function() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    this.recognition = new SpeechRecognition();
    
    // Configuración
    this.recognition.lang = this.config.language;
    this.recognition.continuous = false; // Escucha solo hasta el primer resultado
    this.recognition.interimResults = false; // No mostrar resultados parciales
    this.recognition.maxAlternatives = 1;
    
    // Event Handlers
    this.recognition.onstart = () => this.handleStart();
    this.recognition.onresult = (event) => this.handleResult(event);
    this.recognition.onerror = (event) => this.handleError(event);
    this.recognition.onend = () => this.handleEnd();
};

// ============================================
// 3. CONTROL DEL ASISTENTE
// ============================================

VoiceAssistant.start = function() {
    if (this.isListening) {
        console.warn('⚠️ El asistente ya está escuchando');
        return;
    }
    
    try {
        this.recognition.start();
        console.log('👂 Escuchando...');
    } catch (error) {
        console.error('Error al iniciar reconocimiento:', error);
        this.handleError({ error: 'not-allowed' });
    }
};

VoiceAssistant.stop = function() {
    if (!this.isListening) return;
    
    this.recognition.stop();
    this.isListening = false;
    this.updateUI('idle');
    console.log('🛑 Reconocimiento detenido');
};

VoiceAssistant.toggle = function() {
    if (this.isListening) {
        this.stop();
    } else {
        this.start();
    }
};

// ============================================
// 4. EVENT HANDLERS
// ============================================

VoiceAssistant.handleStart = function() {
    this.isListening = true;
    this.updateUI('listening');
    
    if (this.onStart) {
        this.onStart();
    }
    
    console.log('🎤 Micrófono activado');
};

VoiceAssistant.handleResult = async function(event) {
    const transcript = event.results[0][0].transcript;
    const confidence = event.results[0][0].confidence;
    
    console.log('🗣️ Comando detectado:', transcript);
    console.log('📊 Confianza:', (confidence * 100).toFixed(1) + '%');
    
    this.isListening = false;
    this.isProcessing = true;
    this.updateUI('processing', transcript);
    
    if (this.onResult) {
        this.onResult(transcript, confidence);
    }
    
    // Procesar comando
    await this.processCommand(transcript);
    
    this.isProcessing = false;
    this.updateUI('idle');
};

VoiceAssistant.handleError = function(event) {
    this.isListening = false;
    this.isProcessing = false;
    
    let errorMessage = 'Error desconocido';
    
    switch(event.error) {
        case 'no-speech':
            errorMessage = 'No se detectó voz. Intenta de nuevo.';
            break;
        case 'audio-capture':
            errorMessage = 'No se pudo acceder al micrófono.';
            break;
        case 'not-allowed':
            errorMessage = 'Permiso denegado. Activa el micrófono en la configuración.';
            break;
        case 'network':
            errorMessage = 'Error de red. Verifica tu conexión.';
            break;
        case 'aborted':
            errorMessage = 'Reconocimiento cancelado.';
            return; // No mostrar error si fue cancelado intencionalmente
    }
    
    console.error('❌ Error:', errorMessage);
    this.updateUI('error', errorMessage);
    this.speak(errorMessage);
    
    if (this.onError) {
        this.onError(event.error, errorMessage);
    }
    
    // Volver a estado idle después de 3 segundos
    setTimeout(() => this.updateUI('idle'), 3000);
};

VoiceAssistant.handleEnd = function() {
    this.isListening = false;
    
    if (!this.isProcessing) {
        this.updateUI('idle');
    }
    
    if (this.onEnd) {
        this.onEnd();
    }
    
    console.log('🏁 Reconocimiento finalizado');
};

// ============================================
// 5. PROCESAMIENTO DE COMANDOS
// ============================================

VoiceAssistant.processCommand = async function(comando) {
    const comandoLower = comando.toLowerCase();
    
    // Comandos locales (sin backend)
    if (this.processLocalCommand(comandoLower)) {
        return;
    }
    
    // Comandos que requieren backend
    await this.processBackendCommand(comandoLower);
};

VoiceAssistant.processLocalCommand = function(comando) {
    // Navegación
    if (comando.includes('ir a') || comando.includes('abrir')) {
        if (comando.includes('inicio') || comando.includes('dashboard')) {
            this.speak('Abriendo dashboard');
            window.location.href = '/cliente/dashboard';
            return true;
        }
        if (comando.includes('analíticas') || comando.includes('estadísticas')) {
            this.speak('Abriendo análisis');
            window.location.href = '/cliente/analytics';
            return true;
        }
        if (comando.includes('comparador') || comando.includes('comparar')) {
            this.speak('Abriendo comparador de seguros');
            window.location.href = '/cliente/comparador';
            return true;
        }
        if (comando.includes('carrito')) {
            this.speak('Abriendo carrito de compra');
            window.location.href = '/cliente/carrito';
            return true;
        }
        if (comando.includes('configuración') || comando.includes('ajustes')) {
            this.speak('Abriendo configuración');
            window.location.href = '/cliente/configuracion';
            return true;
        }
    }
    
    // Tema oscuro
    if (comando.includes('tema oscuro') || comando.includes('modo oscuro')) {
        document.documentElement.setAttribute('data-theme', 'dark');
        localStorage.setItem('theme', 'dark');
        this.speak('Tema oscuro activado');
        return true;
    }
    
    if (comando.includes('tema claro') || comando.includes('modo claro')) {
        document.documentElement.setAttribute('data-theme', 'light');
        localStorage.setItem('theme', 'light');
        this.speak('Tema claro activado');
        return true;
    }
    
    // Ayuda
    if (comando.includes('ayuda') || comando.includes('qué puedes hacer')) {
        const ayuda = 'Puedo ayudarte con: información de pólizas, reportar siniestros, ' +
                      'comparar seguros, consultar precios, y navegar por la aplicación. ' +
                      '¿En qué te ayudo?';
        this.speak(ayuda);
        return true;
    }
    
    return false; // No fue comando local
};

VoiceAssistant.processBackendCommand = async function(comando) {
    try {
        // Obtener token de sesión
        const token = sessionStorage.getItem('token') || 
                      document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
        
        const response = await fetch(`${this.config.apiBaseUrl}/innovation/voice-command`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token ? `Bearer ${token}` : ''
            },
            body: JSON.stringify({ 
                comando: comando,
                timestamp: new Date().toISOString()
            })
        });
        
        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        
        const resultado = await response.json();
        
        // Responder con voz
        this.speak(resultado.respuesta);
        
        // Si hay acción adicional
        if (resultado.accion) {
            this.executeAction(resultado.accion);
        }
        
    } catch (error) {
        console.error('Error procesando comando:', error);
        const errorMsg = 'Lo siento, no pude procesar tu comando. ¿Puedes repetirlo?';
        this.speak(errorMsg);
    }
};

VoiceAssistant.executeAction = function(accion) {
    switch(accion.tipo) {
        case 'redirect':
            setTimeout(() => {
                window.location.href = accion.url;
            }, 1000);
            break;
        case 'modal':
            // Abrir modal si existe
            const modal = document.querySelector(accion.selector);
            if (modal) {
                new bootstrap.Modal(modal).show();
            }
            break;
        case 'function':
            // Ejecutar función global
            if (typeof window[accion.nombre] === 'function') {
                window[accion.nombre](...(accion.parametros || []));
            }
            break;
    }
};

// ============================================
// 6. SÍNTESIS DE VOZ (HABLAR)
// ============================================

VoiceAssistant.speak = function(texto, options = {}) {
    // Cancelar cualquier voz en progreso
    this.synthesis.cancel();
    
    const utterance = new SpeechSynthesisUtterance(texto);
    
    // Configuración
    utterance.lang = options.lang || this.config.language;
    utterance.rate = options.rate || this.config.speechRate;
    utterance.pitch = options.pitch || this.config.speechPitch;
    utterance.volume = options.volume || this.config.speechVolume;
    
    // Seleccionar voz femenina en español (si está disponible)
    const voices = this.synthesis.getVoices();
    const spanishVoice = voices.find(voice => 
        voice.lang.startsWith('es') && voice.name.toLowerCase().includes('female')
    ) || voices.find(voice => voice.lang.startsWith('es'));
    
    if (spanishVoice) {
        utterance.voice = spanishVoice;
    }
    
    // Event handlers
    utterance.onstart = () => {
        this.isSpeaking = true;
        this.updateUI('speaking');
        console.log('🔊 Hablando:', texto);
    };
    
    utterance.onend = () => {
        this.isSpeaking = false;
        this.updateUI('idle');
        console.log('🔇 Fin del audio');
    };
    
    utterance.onerror = (event) => {
        console.error('Error en síntesis de voz:', event.error);
        this.isSpeaking = false;
        this.updateUI('idle');
    };
    
    // Reproducir
    this.synthesis.speak(utterance);
};

VoiceAssistant.stopSpeaking = function() {
    this.synthesis.cancel();
    this.isSpeaking = false;
    this.updateUI('idle');
};

// ============================================
// 7. INTERFAZ DE USUARIO
// ============================================

VoiceAssistant.createUI = function() {
    // Verificar si ya existe
    if (document.getElementById('voice-assistant-btn')) return;
    
    const html = `
        <div id="voice-assistant-container" style="position: fixed; bottom: 90px; right: 20px; z-index: 1050;">
            <button id="voice-assistant-btn" class="btn btn-lg rounded-circle shadow-lg" 
                    style="width: 64px; height: 64px; background: linear-gradient(135deg, #f4b94c 0%, #ffcc7c 100%); 
                           border: none; transition: all 0.3s ease;"
                    title="Asistente de Voz (Presiona V)">
                <i class="bi bi-mic-fill fs-3 text-white" id="voice-icon"></i>
            </button>
            
            <!-- Toast de estado -->
            <div class="position-fixed top-0 start-50 translate-middle-x p-3" style="z-index: 1060;">
                <div id="voice-toast" class="toast align-items-center border-0" role="alert">
                    <div class="d-flex">
                        <div class="toast-body fw-bold" id="voice-toast-body">
                            Escuchando...
                        </div>
                        <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', html);
    
    // Event listeners
    const btn = document.getElementById('voice-assistant-btn');
    btn.addEventListener('click', () => this.toggle());
    
    // Atajo de teclado: Presionar "V"
    document.addEventListener('keydown', (e) => {
        if (e.key === 'v' || e.key === 'V') {
            // Solo si no está en un input
            if (!['INPUT', 'TEXTAREA'].includes(e.target.tagName)) {
                e.preventDefault();
                this.toggle();
            }
        }
        
        // ESC para cancelar
        if (e.key === 'Escape' && this.isListening) {
            this.stop();
        }
    });
};

VoiceAssistant.updateUI = function(state, message = '') {
    const btn = document.getElementById('voice-assistant-btn');
    const icon = document.getElementById('voice-icon');
    const toast = document.getElementById('voice-toast');
    const toastBody = document.getElementById('voice-toast-body');
    
    if (!btn || !icon) return;
    
    // Resetear animaciones
    btn.style.animation = 'none';
    
    switch(state) {
        case 'listening':
            btn.style.background = 'linear-gradient(135deg, #dc3545 0%, #fd7e14 100%)';
            icon.className = 'bi bi-mic-fill fs-3 text-white';
            btn.style.animation = 'pulse 1.5s ease-in-out infinite';
            this.showToast('🎤 Escuchando...', 'danger');
            break;
            
        case 'processing':
            btn.style.background = 'linear-gradient(135deg, #0dcaf0 0%, #0d6efd 100%)';
            icon.className = 'bi bi-arrow-repeat fs-3 text-white';
            icon.style.animation = 'spin 1s linear infinite';
            this.showToast(`⚙️ Procesando: "${message}"`, 'info');
            break;
            
        case 'speaking':
            btn.style.background = 'linear-gradient(135deg, #198754 0%, #20c997 100%)';
            icon.className = 'bi bi-volume-up-fill fs-3 text-white';
            btn.style.animation = 'pulse 1s ease-in-out infinite';
            this.showToast('🔊 Hablando...', 'success');
            break;
            
        case 'error':
            btn.style.background = 'linear-gradient(135deg, #dc3545 0%, #c71f37 100%)';
            icon.className = 'bi bi-exclamation-triangle-fill fs-3 text-white';
            this.showToast(`❌ ${message}`, 'danger');
            break;
            
        case 'idle':
        default:
            btn.style.background = 'linear-gradient(135deg, #f4b94c 0%, #ffcc7c 100%)';
            icon.className = 'bi bi-mic-fill fs-3 text-white';
            icon.style.animation = 'none';
            if (toast) {
                bootstrap.Toast.getInstance(toast)?.hide();
            }
            break;
    }
};

VoiceAssistant.showToast = function(message, type = 'info') {
    const toast = document.getElementById('voice-toast');
    const toastBody = document.getElementById('voice-toast-body');
    
    if (!toast || !toastBody) return;
    
    // Colores
    const colors = {
        info: '#0dcaf0',
        success: '#198754',
        danger: '#dc3545',
        warning: '#ffc107'
    };
    
    toast.style.background = colors[type] || colors.info;
    toast.style.color = 'white';
    toastBody.textContent = message;
    
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
};

// ============================================
// 8. ANIMACIONES CSS
// ============================================

// Inyectar estilos de animación
const style = document.createElement('style');
style.textContent = `
    @keyframes pulse {
        0%, 100% { transform: scale(1); }
        50% { transform: scale(1.1); }
    }
    
    @keyframes spin {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
    }
    
    #voice-assistant-btn:hover {
        transform: scale(1.05);
        box-shadow: 0 8px 25px rgba(244, 185, 76, 0.4) !important;
    }
    
    #voice-assistant-btn:active {
        transform: scale(0.95);
    }
`;
document.head.appendChild(style);

// ============================================
// 9. AUTO-INICIALIZACIÓN
// ============================================

// Inicializar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        VoiceAssistant.init();
    });
} else {
    VoiceAssistant.init();
}

// Exportar para uso global
window.VoiceAssistant = VoiceAssistant;

console.log('🎙️ Asistente de Voz Vesta cargado');