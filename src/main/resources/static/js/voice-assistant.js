/**
 * ═══════════════════════════════════════════════════════════════
 * VESTA VOICE ASSISTANT - Sistema de Asistente de Voz Inteligente
 * ═══════════════════════════════════════════════════════════════
 * Versión: 2.0
 * Fecha: Diciembre 2025
 * Ubicación: vesta-web/src/main/resources/static/js/voice-assistant.js
 */

'use strict';

// ============================================
// 1. NAMESPACE PRINCIPAL
// ============================================

const VoiceAssistant = {
    // Instancias de APIs
    recognition: null,
    synthesis: window.speechSynthesis,

    // Estado interno
    state: {
        isListening: false,
        isProcessing: false,
        isSpeaking: false,
        isInitialized: false,
        currentTranscript: '',
        lastCommand: null,
        errorCount: 0
    },

    // Configuración
    config: {
        language: 'es-ES',
        apiBaseUrl: window.location.origin + '/api',
        maxRetries: 3,
        retryDelay: 1000,
        speechRate: 0.9,
        speechPitch: 1.0,
        speechVolume: 1.0,
        autoRestart: true,
        debugMode: false,

        // Timeout para evitar bloqueos
        recognitionTimeout: 10000,
        processingTimeout: 15000
    },

    // Callbacks personalizables
    callbacks: {
        onStart: null,
        onEnd: null,
        onError: null,
        onResult: null,
        onCommandProcessed: null
    },

    // Cache de voces
    voicesCache: null,

    // Timers
    timers: {
        recognition: null,
        processing: null
    }
};

// ============================================
// 2. INICIALIZACIÓN Y CONFIGURACIÓN
// ============================================

/**
 * Inicializar el asistente de voz
 * @param {Object} options - Opciones de configuración
 * @returns {Boolean} - true si se inicializó correctamente
 */
VoiceAssistant.init = function (options = {}) {
    if (this.state.isInitialized) {
        console.warn('⚠️ Voice Assistant ya está inicializado');
        return true;
    }

    console.log('🎤 Inicializando Asistente de Voz Vesta...');

    // Combinar configuración
    Object.assign(this.config, options);
    Object.assign(this.callbacks, options.callbacks || {});

    // Verificar compatibilidad
    if (!this.checkBrowserSupport()) {
        return false;
    }

    // Inicializar componentes
    this.initSpeechRecognition();
    this.loadVoices();

    // Crear interfaz visual
    if (options.createUI !== false) {
        this.createUI();
    }

    // Registrar atajos de teclado
    this.registerKeyboardShortcuts();

    this.state.isInitialized = true;
    console.log('✅ Asistente de Voz iniciado correctamente');

    return true;
};

/**
 * Verificar compatibilidad del navegador
 */
VoiceAssistant.checkBrowserSupport = function () {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;

    if (!SpeechRecognition) {
        this.showError('Tu navegador no soporta reconocimiento de voz. Usa Chrome, Edge o Safari.');
        return false;
    }

    if (!this.synthesis) {
        this.showError('Tu navegador no soporta síntesis de voz.');
        return false;
    }

    return true;
};

/**
 * Inicializar Speech Recognition API
 */
VoiceAssistant.initSpeechRecognition = function () {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    this.recognition = new SpeechRecognition();

    // Configuración optimizada
    this.recognition.lang = this.config.language;
    this.recognition.continuous = false;
    this.recognition.interimResults = false;
    this.recognition.maxAlternatives = 1;

    // Event Handlers
    this.recognition.onstart = () => this.handleStart();
    this.recognition.onresult = (event) => this.handleResult(event);
    this.recognition.onerror = (event) => this.handleError(event);
    this.recognition.onend = () => this.handleEnd();
    this.recognition.onspeechstart = () => this.log('🗣️ Voz detectada');
    this.recognition.onspeechend = () => this.log('🤫 Voz finalizada');
};

/**
 * Cargar y cachear voces disponibles
 */
VoiceAssistant.loadVoices = function () {
    const loadVoicesInterval = setInterval(() => {
        const voices = this.synthesis.getVoices();
        if (voices.length > 0) {
            this.voicesCache = voices;
            this.log(`🎵 ${voices.length} voces cargadas`);
            clearInterval(loadVoicesInterval);
        }
    }, 100);

    // También escuchar el evento
    if (this.synthesis.onvoiceschanged !== undefined) {
        this.synthesis.onvoiceschanged = () => {
            this.voicesCache = this.synthesis.getVoices();
        };
    }
};

// ============================================
// 3. CONTROL DEL ASISTENTE
// ============================================

/**
 * Activar el reconocimiento de voz
 */
VoiceAssistant.start = function () {
    if (this.state.isListening) {
        this.log('⚠️ Ya está escuchando');
        return;
    }

    if (this.state.isSpeaking) {
        this.stopSpeaking();
    }

    try {
        this.recognition.start();

        // Timeout de seguridad
        this.timers.recognition = setTimeout(() => {
            if (this.state.isListening) {
                this.log('⏱️ Timeout de reconocimiento');
                this.stop();
                this.speak('No te escuché. ¿Puedes repetir?');
            }
        }, this.config.recognitionTimeout);

    } catch (error) {
        this.log('❌ Error al iniciar:', error);

        // Si ya está activo, intentar detenerlo primero
        if (error.message && error.message.includes('already started')) {
            this.recognition.stop();
            setTimeout(() => this.start(), 100);
        } else {
            this.handleError({ error: 'not-allowed' });
        }
    }
};

/**
 * Detener el reconocimiento de voz
 */
VoiceAssistant.stop = function () {
    if (!this.state.isListening) return;

    try {
        this.recognition.stop();
        this.clearTimer('recognition');
        this.state.isListening = false;
        this.updateUI('idle');
        this.log('🛑 Reconocimiento detenido');
    } catch (error) {
        this.log('Error al detener:', error);
    }
};

/**
 * Alternar entre activar/desactivar
 */
VoiceAssistant.toggle = function () {
    if (this.state.isListening) {
        this.stop();
    } else {
        this.start();
    }
};

// ============================================
// 4. EVENT HANDLERS
// ============================================

/**
 * Handler: Inicio de reconocimiento
 */
VoiceAssistant.handleStart = function () {
    this.state.isListening = true;
    this.state.errorCount = 0;
    this.updateUI('listening');

    if (this.callbacks.onStart) {
        this.callbacks.onStart();
    }

    this.log('🎤 Micrófono activado - Hablando...');
};

/**
 * Handler: Resultado detectado
 */
VoiceAssistant.handleResult = async function (event) {
    this.clearTimer('recognition');

    const result = event.results[0][0];
    const transcript = result.transcript.trim();
    const confidence = result.confidence;

    this.state.currentTranscript = transcript;
    this.state.lastCommand = {
        text: transcript,
        confidence: confidence,
        timestamp: new Date()
    };

    this.log(`🗣️ "${transcript}" (${(confidence * 100).toFixed(1)}%)`);

    this.state.isListening = false;
    this.state.isProcessing = true;
    this.updateUI('processing', transcript);

    if (this.callbacks.onResult) {
        this.callbacks.onResult(transcript, confidence);
    }

    // Procesar comando con timeout
    this.timers.processing = setTimeout(() => {
        if (this.state.isProcessing) {
            this.log('⏱️ Timeout de procesamiento');
            this.state.isProcessing = false;
            this.updateUI('error', 'Tiempo de espera agotado');
        }
    }, this.config.processingTimeout);

    try {
        await this.processCommand(transcript);
    } catch (error) {
        this.log('❌ Error procesando comando:', error);
        this.speak('Lo siento, ocurrió un error al procesar tu solicitud.');
    } finally {
        this.clearTimer('processing');
        this.state.isProcessing = false;
        this.updateUI('idle');
    }
};

/**
 * Handler: Error en reconocimiento
 */
VoiceAssistant.handleError = function (event) {
    this.clearTimer('recognition');
    this.state.isListening = false;
    this.state.isProcessing = false;
    this.state.errorCount++;

    let errorMessage = 'Error desconocido';
    let shouldSpeak = true;

    switch (event.error) {
        case 'no-speech':
            errorMessage = 'No se detectó voz. Intenta de nuevo.';
            break;

        case 'audio-capture':
            errorMessage = 'No se pudo acceder al micrófono.';
            break;

        case 'not-allowed':
            errorMessage = 'Permiso denegado. Activa el micrófono en la configuración del navegador.';
            break;

        case 'network':
            errorMessage = 'Error de red. Verifica tu conexión a Internet.';
            break;

        case 'aborted':
            // Cancelación intencional, no mostrar error
            this.updateUI('idle');
            return;

        case 'service-not-allowed':
            errorMessage = 'Servicio de reconocimiento no disponible.';
            break;
    }

    this.log('❌ Error:', errorMessage);
    this.updateUI('error', errorMessage);

    if (shouldSpeak && this.state.errorCount <= 2) {
        this.speak(errorMessage);
    }

    if (this.callbacks.onError) {
        this.callbacks.onError(event.error, errorMessage);
    }

    // Volver a idle después de 3 segundos
    setTimeout(() => {
        if (!this.state.isListening && !this.state.isProcessing) {
            this.updateUI('idle');
        }
    }, 3000);
};

/**
 * Handler: Finalización de reconocimiento
 */
VoiceAssistant.handleEnd = function () {
    this.clearTimer('recognition');
    this.state.isListening = false;

    if (!this.state.isProcessing) {
        this.updateUI('idle');
    }

    if (this.callbacks.onEnd) {
        this.callbacks.onEnd();
    }

    this.log('🏁 Reconocimiento finalizado');
};

// ============================================
// 5. PROCESAMIENTO DE COMANDOS
// ============================================

/**
 * Procesar comando recibido
 */
VoiceAssistant.processCommand = async function (comando) {
    const comandoLower = comando.toLowerCase();

    // 1. Comandos locales (sin backend)
    if (await this.processLocalCommand(comandoLower)) {
        return;
    }

    // 2. Comandos que requieren backend
    await this.processBackendCommand(comandoLower, comando);
};

/**
 * Procesar comandos locales (navegación, tema, etc)
 */
VoiceAssistant.processLocalCommand = async function (comando) {
    // NAVEGACIÓN
    const navegacion = this.detectNavigation(comando);
    if (navegacion) {
        this.speak(navegacion.mensaje);
        setTimeout(() => {
            window.location.href = navegacion.url;
        }, 1000);
        return true;
    }

    // TEMA OSCURO/CLARO
    if (comando.includes('tema oscuro') || comando.includes('modo oscuro') || comando.includes('oscuro')) {
        document.documentElement.setAttribute('data-theme', 'dark');
        localStorage.setItem('theme', 'dark');
        this.speak('Tema oscuro activado');
        return true;
    }

    if (comando.includes('tema claro') || comando.includes('modo claro') || comando.includes('claro')) {
        document.documentElement.setAttribute('data-theme', 'light');
        localStorage.setItem('theme', 'light');
        this.speak('Tema claro activado');
        return true;
    }

    // AYUDA
    if (comando.includes('ayuda') || comando.includes('qué puedes hacer') || comando.includes('comandos')) {
        const ayuda = 'Puedo ayudarte con: información de pólizas, reportar siniestros, ' +
            'comparar seguros, consultar precios, navegar por la aplicación, ' +
            'cambiar el tema, y responder preguntas. ¿En qué te ayudo?';
        this.speak(ayuda);
        return true;
    }

    // REPETIR ÚLTIMO COMANDO
    if (comando.includes('repetir') || comando.includes('repite')) {
        if (this.state.lastCommand) {
            this.speak(`Dijiste: ${this.state.lastCommand.text}`);
            return true;
        }
    }

    // CANCELAR
    if (comando.includes('cancelar') || comando.includes('olvida') || comando.includes('nada')) {
        this.speak('Vale, cancelado');
        return true;
    }

    return false;
};

/**
 * Detectar comandos de navegación
 */
VoiceAssistant.detectNavigation = function (comando) {
    const routes = [
        {
            keywords: ['inicio', 'dashboard', 'principal', 'home'],
            url: '/cliente/dashboard',
            mensaje: 'Abriendo el inicio'
        },
        {
            keywords: ['analítica', 'estadística', 'gráfico', 'análisis'],
            url: '/cliente/analytics',
            mensaje: 'Abriendo tus estadísticas'
        },
        {
            keywords: ['comparador', 'comparar', 'comparación'],
            url: '/cliente/comparador',
            mensaje: 'Abriendo el comparador de seguros'
        },
        {
            keywords: ['carrito', 'compra', 'cesta'],
            url: '/cliente/carrito',
            mensaje: 'Abriendo tu carrito'
        },
        {
            keywords: ['configuración', 'ajuste', 'setting', 'perfil'],
            url: '/cliente/configuracion',
            mensaje: 'Abriendo configuración'
        },
        {
            keywords: ['privacidad', 'datos', 'rgpd'],
            url: '/mis-datos',
            mensaje: 'Abriendo gestión de datos'
        }
    ];

    for (const route of routes) {
        if (route.keywords.some(keyword => comando.includes(keyword))) {
            return route;
        }
    }

    return null;
};

/**
 * Procesar comandos que requieren backend
 */
VoiceAssistant.processBackendCommand = async function (comandoLower, comandoOriginal) {
    try {
        const token = this.getAuthToken();

        const response = await fetch(`${this.config.apiBaseUrl}/innovation/voice-command`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': token ? `Bearer ${token}` : ''
            },
            body: JSON.stringify({
                comando: comandoOriginal,
                comandoLower: comandoLower,
                timestamp: new Date().toISOString(),
                confidence: this.state.lastCommand?.confidence || 0
            })
        });

        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }

        const resultado = await response.json();

        // Responder con voz
        if (resultado.respuesta) {
            this.speak(resultado.respuesta);
        }

        // Ejecutar acción adicional si existe
        if (resultado.accion) {
            setTimeout(() => {
                this.executeAction(resultado.accion);
            }, 1000);
        }

        if (this.callbacks.onCommandProcessed) {
            this.callbacks.onCommandProcessed(resultado);
        }

    } catch (error) {
        this.log('❌ Error en backend:', error);

        // Respuesta de fallback
        const fallbackResponse = this.generateFallbackResponse(comandoLower);
        this.speak(fallbackResponse);
    }
};

/**
 * Generar respuesta de fallback cuando el backend falla
 */
VoiceAssistant.generateFallbackResponse = function (comando) {
    if (comando.includes('precio') || comando.includes('cuesta') || comando.includes('cuánto')) {
        return 'Nuestros seguros empiezan desde 9 euros con 99 céntimos al mes. ¿Quieres ver el catálogo completo?';
    }

    if (comando.includes('póliza') || comando.includes('seguro')) {
        return 'Para consultar tus pólizas, di "ir a inicio" o accede al dashboard.';
    }

    if (comando.includes('siniestro') || comando.includes('daño')) {
        return 'Para reportar un siniestro, necesitas acceder al dashboard y hacer clic en "Reportar Siniestro".';
    }

    return 'Lo siento, no pude procesar tu solicitud en este momento. Por favor, intenta de nuevo o usa la navegación manual.';
};

/**
 * Ejecutar acción recibida del backend
 */
VoiceAssistant.executeAction = function (accion) {
    switch (accion.tipo) {
        case 'redirect':
            window.location.href = accion.url;
            break;

        case 'modal':
            const modal = document.querySelector(accion.selector);
            if (modal && typeof bootstrap !== 'undefined') {
                new bootstrap.Modal(modal).show();
            }
            break;

        case 'function':
            if (typeof window[accion.nombre] === 'function') {
                window[accion.nombre](...(accion.parametros || []));
            }
            break;

        case 'scroll':
            const element = document.querySelector(accion.selector);
            if (element) {
                element.scrollIntoView({ behavior: 'smooth' });
            }
            break;
    }
};

// ============================================
// 6. SÍNTESIS DE VOZ
// ============================================

/**
 * Hacer que el asistente hable
 */
VoiceAssistant.speak = function (texto, options = {}) {
    // Cancelar cualquier voz en progreso
    this.synthesis.cancel();

    const utterance = new SpeechSynthesisUtterance(texto);

    // Configuración
    utterance.lang = options.lang || this.config.language;
    utterance.rate = options.rate || this.config.speechRate;
    utterance.pitch = options.pitch || this.config.speechPitch;
    utterance.volume = options.volume || this.config.speechVolume;

    // Seleccionar mejor voz disponible
    const voice = this.selectBestVoice(options.lang || this.config.language);
    if (voice) {
        utterance.voice = voice;
    }

    // Event handlers
    utterance.onstart = () => {
        this.state.isSpeaking = true;
        this.updateUI('speaking');
        this.log('🔊 Hablando:', texto.substring(0, 50) + '...');
    };

    utterance.onend = () => {
        this.state.isSpeaking = false;
        if (!this.state.isListening && !this.state.isProcessing) {
            this.updateUI('idle');
        }
        this.log('🔇 Audio finalizado');
    };

    utterance.onerror = (event) => {
        this.log('❌ Error en síntesis:', event.error);
        this.state.isSpeaking = false;
        this.updateUI('idle');
    };

    // Reproducir
    this.synthesis.speak(utterance);
};

/**
 * Seleccionar la mejor voz disponible
 */
VoiceAssistant.selectBestVoice = function (lang) {
    if (!this.voicesCache || this.voicesCache.length === 0) {
        return null;
    }

    const voices = this.voicesCache;
    const langPrefix = lang.split('-')[0]; // 'es' de 'es-ES'

    // Preferencia: voz femenina en español de España
    let voice = voices.find(v =>
        v.lang === lang &&
        (v.name.toLowerCase().includes('female') || v.name.toLowerCase().includes('mujer'))
    );

    if (!voice) {
        // Cualquier voz en español de España
        voice = voices.find(v => v.lang === lang);
    }

    if (!voice) {
        // Cualquier voz en español
        voice = voices.find(v => v.lang.startsWith(langPrefix));
    }

    return voice;
};

/**
 * Detener síntesis de voz
 */
VoiceAssistant.stopSpeaking = function () {
    this.synthesis.cancel();
    this.state.isSpeaking = false;
    if (!this.state.isListening && !this.state.isProcessing) {
        this.updateUI('idle');
    }
};

// ============================================
// 7. INTERFAZ DE USUARIO
// ============================================

/**
 * Crear interfaz visual del asistente
 */
VoiceAssistant.createUI = function () {
    if (document.getElementById('voice-assistant-btn')) {
        this.log('⚠️ UI ya existe');
        return;
    }

    const html = `
        <div id="voice-assistant-container" style="position: fixed; bottom: 90px; right: 20px; z-index: 1050;">
            <button id="voice-assistant-btn" 
                    class="btn btn-lg rounded-circle shadow-lg" 
                    style="width: 64px; height: 64px; 
                           background: linear-gradient(135deg, #f4b94c 0%, #ffcc7c 100%); 
                           border: none; 
                           transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                           position: relative;"
                    title="Asistente de Voz (Presiona V)"
                    aria-label="Activar asistente de voz">
                <i class="bi bi-mic-fill fs-3 text-white" id="voice-icon"></i>
                <span id="voice-status-indicator" 
                      style="position: absolute; 
                             bottom: 0; 
                             right: 0; 
                             width: 16px; 
                             height: 16px; 
                             background: #198754; 
                             border: 3px solid white; 
                             border-radius: 50%;
                             display: none;"></span>
            </button>
            
            <div class="position-fixed top-0 start-50 translate-middle-x p-3" style="z-index: 1060;">
                <div id="voice-toast" 
                     class="toast align-items-center border-0 shadow-lg" 
                     role="alert"
                     style="min-width: 300px;">
                    <div class="d-flex">
                        <div class="toast-body fw-bold d-flex align-items-center gap-2" id="voice-toast-body">
                            <span id="voice-toast-icon"></span>
                            <span id="voice-toast-text">Escuchando...</span>
                        </div>
                        <button type="button" 
                                class="btn-close btn-close-white me-2 m-auto" 
                                data-bs-dismiss="toast"
                                aria-label="Cerrar"></button>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', html);

    // Event listeners
    const btn = document.getElementById('voice-assistant-btn');
    btn.addEventListener('click', () => this.toggle());

    // Efectos hover
    btn.addEventListener('mouseenter', () => {
        if (!this.state.isListening && !this.state.isProcessing) {
            btn.style.transform = 'scale(1.05)';
        }
    });

    btn.addEventListener('mouseleave', () => {
        btn.style.transform = 'scale(1)';
    });
};

/**
 * Registrar atajos de teclado
 */
VoiceAssistant.registerKeyboardShortcuts = function () {
    document.addEventListener('keydown', (e) => {
        // V para activar/desactivar
        if ((e.key === 'v' || e.key === 'V') && !this.isTyping(e)) {
            e.preventDefault();
            this.toggle();
        }

        // ESC para cancelar
        if (e.key === 'Escape') {
            if (this.state.isListening) {
                this.stop();
            }
            if (this.state.isSpeaking) {
                this.stopSpeaking();
            }
        }

        // ESPACIO para detener voz
        if (e.key === ' ' && this.state.isSpeaking && !this.isTyping(e)) {
            e.preventDefault();
            this.stopSpeaking();
        }
    });
};

/**
 * Verificar si el usuario está escribiendo
 */
VoiceAssistant.isTyping = function (event) {
    const activeElement = document.activeElement;
    return ['INPUT', 'TEXTAREA', 'SELECT'].includes(activeElement.tagName) ||
        activeElement.isContentEditable;
};

/**
 * Actualizar interfaz según estado
 */
VoiceAssistant.updateUI = function (state, message = '') {
    const btn = document.getElementById('voice-assistant-btn');
    const icon = document.getElementById('voice-icon');
    const indicator = document.getElementById('voice-status-indicator');

    if (!btn || !icon) return;

    // Resetear
    btn.style.animation = 'none';
    if (icon) icon.style.animation = 'none';

    switch (state) {
        case 'listening':
            btn.style.background = 'linear-gradient(135deg, #dc3545 0%, #fd7e14 100%)';
            icon.className = 'bi bi-mic-fill fs-3 text-white';
            btn.style.animation = 'pulse-listening 1.5s ease-in-out infinite';
            if (indicator) {
                indicator.style.display = 'block';
                indicator.style.background = '#dc3545';
            }
            this.showToast('🎤 Escuchando...', 'danger');
            break;

        case 'processing':
            btn.style.background = 'linear-gradient(135deg, #0dcaf0 0%, #0d6efd 100%)';
            icon.className = 'bi bi-arrow-repeat fs-3 text-white';
            icon.style.animation = 'spin 1s linear infinite';
            if (indicator) {
                indicator.style.display = 'block';
                indicator.style.background = '#0dcaf0';
            }
            this.showToast(`⚙️ Procesando: "${message.substring(0, 30)}..."`, 'info');
            break;

        case 'speaking':
            btn.style.background = 'linear-gradient(135deg, #198754 0%, #20c997 100%)';
            icon.className = 'bi bi-volume-up-fill fs-3 text-white';
            btn.style.animation = 'pulse-speaking 1s ease-in-out infinite';
            if (indicator) {
                indicator.style.display = 'block';
                indicator.style.background = '#198754';
            }
            this.showToast('🔊 Hablando...', 'success');
            break;

        case 'error':
            btn.style.background = 'linear-gradient(135deg, #dc3545 0%, #c71f37 100%)';
            icon.className = 'bi bi-exclamation-triangle-fill fs-3 text-white';
            if (indicator) indicator.style.display = 'none';
            this.showToast(`❌ ${message}`, 'danger');
            break;

        case 'idle':
        default:
            btn.style.background = 'linear-gradient(135deg, #f4b94c 0%, #ffcc7c 100%)';
            icon.className = 'bi bi-mic-fill fs-3 text-white';
            if (indicator) indicator.style.display = 'none';
            this.hideToast();
            break;
    }
};

/**
 * Mostrar toast con mensaje
 */
VoiceAssistant.showToast = function (message, type = 'info') {
    const toast = document.getElementById('voice-toast');
    const toastText = document.getElementById('voice-toast-text');
    const toastIcon = document.getElementById('voice-toast-icon');

    if (!toast || !toastText) return;

    // Colores según tipo
    const colors = {
        info: '#0dcaf0',
        success: '#198754',
        danger: '#dc3545',
        warning: '#ffc107'
    };

    // Iconos según tipo
    const icons = {
        info: '⚙️',
        success: '✅',
        danger: '🎤',
        warning: '⚠️'
    };

    toast.style.background = colors[type] || colors.info;
    toast.style.color = 'white';
    toastText.textContent = message;

    if (toastIcon) {
        toastIcon.textContent = icons[type] || icons.info;
    }

    // Mostrar toast
    if (typeof bootstrap !== 'undefined') {
        const bsToast = new bootstrap.Toast(toast, {
            autohide: false
        });
        bsToast.show();
    }
};

/**
 * Ocultar toast
 */
VoiceAssistant.hideToast = function () {
    const toast = document.getElementById('voice-toast');
    if (toast && typeof bootstrap !== 'undefined') {
        const bsToast = bootstrap.Toast.getInstance(toast);
        if (bsToast) {
            bsToast.hide();
        }
    }
};

// ============================================
// 8. UTILIDADES
// ============================================

/**
 * Obtener token de autenticación
 */
VoiceAssistant.getAuthToken = function () {
    // Intentar obtener de sessionStorage
    let token = sessionStorage.getItem('token');

    // Intentar obtener de localStorage
    if (!token) {
        token = localStorage.getItem('token');
    }

    // Intentar obtener de meta tag CSRF
    if (!token) {
        const csrfMeta = document.querySelector('meta[name="_csrf"]');
        if (csrfMeta) {
            token = csrfMeta.getAttribute('content');
        }
    }

    // Intentar obtener de variable global (Thymeleaf)
    if (!token && typeof authToken !== 'undefined') {
        token = authToken;
    }

    return token || '';
};

/**
 * Limpiar timer
 */
VoiceAssistant.clearTimer = function (timerName) {
    if (this.timers[timerName]) {
        clearTimeout(this.timers[timerName]);
        this.timers[timerName] = null;
    }
};

/**
 * Logging condicional
 */
VoiceAssistant.log = function (...args) {
    if (this.config.debugMode) {
        console.log('[VoiceAssistant]', ...args);
    }
};

/**
 * Mostrar error al usuario
 */
VoiceAssistant.showError = function (message) {
    // Intentar usar modal si está disponible
    if (typeof showErrorModal !== 'undefined') {
        showErrorModal('Error del Asistente de Voz', message);
    } else if (typeof alert !== 'undefined') {
        // Fallback a alert si modal-utils no está cargado
        alert('❌ ' + message);
    } else {
        console.error(message);
    }
};

/**
 * Destruir el asistente
 */
VoiceAssistant.destroy = function () {
    this.log('🗑️ Destruyendo asistente...');

    // Detener todo
    this.stop();
    this.stopSpeaking();

    // Limpiar timers
    this.clearTimer('recognition');
    this.clearTimer('processing');

    // Remover UI
    const container = document.getElementById('voice-assistant-container');
    if (container) {
        container.remove();
    }

    // Resetear estado
    this.state.isInitialized = false;

    this.log('✅ Asistente destruido');
};

/**
 * Obtener información del estado actual
 */
VoiceAssistant.getStatus = function () {
    return {
        isInitialized: this.state.isInitialized,
        isListening: this.state.isListening,
        isProcessing: this.state.isProcessing,
        isSpeaking: this.state.isSpeaking,
        lastCommand: this.state.lastCommand,
        errorCount: this.state.errorCount,
        voicesAvailable: this.voicesCache ? this.voicesCache.length : 0
    };
};

// ============================================
// 9. ANIMACIONES CSS
// ============================================

// Inyectar estilos de animación
const voiceAssistantStyles = document.createElement('style');
voiceAssistantStyles.id = 'voice-assistant-styles';
voiceAssistantStyles.textContent = `
    /* Animación de pulso para escuchar */
    @keyframes pulse-listening {
        0%, 100% { 
            transform: scale(1); 
            box-shadow: 0 6px 24px rgba(220, 53, 69, 0.4);
        }
        50% { 
            transform: scale(1.1); 
            box-shadow: 0 8px 32px rgba(220, 53, 69, 0.6);
        }
    }
    
    /* Animación de pulso para hablar */
    @keyframes pulse-speaking {
        0%, 100% { 
            transform: scale(1); 
            box-shadow: 0 6px 24px rgba(25, 135, 84, 0.4);
        }
        50% { 
            transform: scale(1.05); 
            box-shadow: 0 8px 32px rgba(25, 135, 84, 0.6);
        }
    }
    
    /* Animación de rotación */
    @keyframes spin {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
    }
    
    /* Estilos del botón */
    #voice-assistant-btn {
        cursor: pointer;
        user-select: none;
    }
    
    #voice-assistant-btn:hover {
        box-shadow: 0 8px 25px rgba(244, 185, 76, 0.5) !important;
    }
    
    #voice-assistant-btn:active {
        transform: scale(0.95) !important;
    }
    
    /* Animación de entrada del toast */
    @keyframes slideInDown {
        from {
            transform: translate(-50%, -100%);
            opacity: 0;
        }
        to {
            transform: translate(-50%, 0);
            opacity: 1;
        }
    }
    
    #voice-toast {
        animation: slideInDown 0.3s ease-out;
    }
    
    /* Indicador de estado */
    #voice-status-indicator {
        animation: pulse-indicator 2s ease-in-out infinite;
    }
    
    @keyframes pulse-indicator {
        0%, 100% { opacity: 1; }
        50% { opacity: 0.5; }
    }
`;

// Añadir estilos al documento
if (!document.getElementById('voice-assistant-styles')) {
    document.head.appendChild(voiceAssistantStyles);
}

// ============================================
// 10. AUTO-INICIALIZACIÓN
// ============================================

/**
 * Inicializar automáticamente cuando el DOM esté listo
 */
function initVoiceAssistant() {
    try {
        VoiceAssistant.init({
            debugMode: false,
            createUI: true,
            autoRestart: true
        });
    } catch (error) {
        console.error('Error al inicializar Voice Assistant:', error);
    }
}

// Inicializar cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initVoiceAssistant);
} else {
    // DOM ya está listo
    initVoiceAssistant();
}

// ============================================
// 11. EXPORTAR A WINDOW
// ============================================

// Exportar para uso global
window.VoiceAssistant = VoiceAssistant;

// Alias para facilitar el uso
window.VA = VoiceAssistant;

// ============================================
// 12. EVENTOS GLOBALES
// ============================================

// Limpiar al cerrar/recargar la página
window.addEventListener('beforeunload', () => {
    if (VoiceAssistant.state.isInitialized) {
        VoiceAssistant.stop();
        VoiceAssistant.stopSpeaking();
    }
});

// Pausar cuando la pestaña pierde el foco
document.addEventListener('visibilitychange', () => {
    if (document.hidden) {
        if (VoiceAssistant.state.isListening) {
            VoiceAssistant.stop();
        }
        if (VoiceAssistant.state.isSpeaking) {
            VoiceAssistant.stopSpeaking();
        }
    }
});

// ============================================
// FIN DEL ARCHIVO
// ============================================

console.log('🎙️ Vesta Voice Assistant v2.0 cargado correctamente');