---
title: "Manual de Instrucciones: Usuario"
author: "Vesta Seguros"
date: "2026-02-06"
pdf_options:
  format: "A4"
  margin: "20mm"
  printBackground: true
  headerTemplate: "<div style='font-size: 10px; width: 100%; text-align: right; margin-right: 20px;'>Manual del Usuario - Vesta Seguros</div>"
  footerTemplate: "<div style='font-size: 10px; width: 100%; text-align: center;'><span class='pageNumber'></span> / <span class='totalPages'></span></div>"
---

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;700&display=swap');

body {
    font-family: 'Inter', sans-serif;
    color: #333;
    line-height: 1.7;
    text-align: justify;
}

h1 {
    color: #f4a261;
    text-align: center;
    font-size: 2.8em;
    margin-top: 50px;
    border-bottom: 2px solid #f4a261;
    padding-bottom: 10px;
    font-weight: 700;
}

h2 {
    color: #264653;
    border-left: 6px solid #f4a261;
    padding-left: 15px;
    margin-top: 45px;
    font-weight: 700;
}

h3 {
    color: #2a9d8f;
    margin-top: 25px;
    font-weight: 700;
}

p, li {
    font-size: 1.05em;
    margin-bottom: 12px;
}

img {
    display: block;
    margin: 30px auto;
    max-width: 85%;
    border-radius: 15px;
    border: 1px solid #e0e0e0;
}

.portada {
    height: 90vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    text-align: center;
}

.toc {
    background: #fdfaf6;
    padding: 30px;
    border-radius: 12px;
    margin-bottom: 50px;
    border: 1px solid #faedcd;
}

.page-break {
    page-break-after: always;
}

.highlight-box {
    background-color: #fefae0;
    border-left: 5px solid #e9c46a;
    padding: 20px;
    margin: 25px 0;
    font-style: italic;
    border-radius: 0 8px 8px 0;
}

strong {
    color: #264653;
}
</style>

<div class="portada">
    <h1 style="border:none; font-size: 4.5em; letter-spacing: 2px;">VESTA SEGUROS</h1>
    <h3 style="color: #666; font-weight: 300; font-size: 1.5em;">Revolución On-Demand en Protección Digital</h3>
    <br><br><br>
    <h2 style="border:none; padding:0; font-size: 2em;">Manual del Usuario Final</h2>
    <p style="font-size: 1.2em; color: #888;">Guía completa de uso, contratación y soporte inteligente</p>
    <br><br>
    <p>Versión 1.2 - Febrero 2026</p>
</div>

<div class="page-break"></div>

<div class="toc">

## Contenidos de la Guía
1. [Introducción y Valor de Vesta](#1-introducción-y-valor-de-vesta)
2. [Gestión de Identidad y Privacidad](#2-gestión-de-identidad-y-privacidad)
3. [El Marketplace: Seguros a Medida](#3-el-marketplace-seguros-a-medida)
4. [Gestión Post-Venta y Siniestros IA](#4-gestión-post-venta-y-siniestros-ia)
5. [Seguridad Avanzada y Personalización](#5-seguridad-avanzada-y-personalización)
6. [Asociado Digital y Soporte 24/7](#6-asociado-digital-y-soporte-247)

</div>

## 1. Introducción y Valor de Vesta
Bienvenido a la nueva era del aseguratodo. **Vesta Seguros** no es una aseguradora convencional; es una plataforma tecnológica diseñada para ofrecerte protección **específica, inmediata y justa**. 

Nuestra filosofía se basa en el modelo **On-Demand**: paga solo por lo que necesitas, cuando lo necesitas. Ya sea un seguro para un viaje de fin de semana, una póliza para tu nueva mascota o la protección de tus dispositivos electrónicos, Vesta pone el control total en tu mano mediante una interfaz intuitiva asistida por Inteligencia Artificial.

![Experiencia Vesta](./docs/img/landing_page_main.png)

<div class="page-break"></div>

## 2. Gestión de Identidad y Privacidad
El acceso a Vesta ha sido simplificado para que puedas comenzar a protegerte en menos de 60 segundos. 

### Registro y Acceso Social
Ofrecemos dos vías de entrada:
- **Registro Tradicional**: Un proceso validado donde tus credenciales se protegen con algoritmos de hashing de última generación (**BCrypt**), asegurando que nadie, ni siquiera nosotros, pueda conocer tu contraseña.
- **Acceso con Google**: Utiliza tu identidad social para entrar de forma instantánea mediante el estándar **OAuth2**, sin necesidad de recordar nuevas contraseñas.

![Acceso de Usuario](./docs/img/register_page.png)
![Inicio de Sesión](./docs/img/login_page.png)

### Transparencia Legal (RGPD)
En cumplimiento con el **Reglamento General de Protección de Datos (RGPD)**, Vesta te otorga control total sobre tu información. Puedes consultar nuestras políticas de privacidad, términos de servicio y gestión de cookies de forma transparente desde tu panel de ajustes. Tu privacidad es nuestro compromiso técnico más firme.

![Transparencia Legal](./docs/img/user_legal_terms.png)
![Privacidad](./docs/img/user_privacy_policy_view.png)

<div class="page-break"></div>

## 3. El Marketplace: Seguros a Medida
Nuestro catálogo de productos está en constante evolución. Navega por las categorías de hogar, tecnología y movilidad para encontrar la solución que mejor se adapte a tu estilo de vida.

![Catálogo Marketplace](./docs/img/user_marketplace_catalog.png)

### Configuración Inteligente
A diferencia de los seguros anuales rígidos, en Vesta tú decides la duración. Puedes configurar tu cobertura por **meses o años**, viendo el precio final calculado en tiempo real antes de confirmar. Sin letras pequeñas, sin sorpresas.

![Personalización de Póliza](./docs/img/user_product_detail_config.png)

### Pago Transparente y Seguro
Utilizamos una pasarela de pago **TPV Virtual** cifrada que garantiza que tus datos financieros viajan por canales 100% seguros. Una vez completado el pago, tu protección se activa de forma instantánea y recibirás un certificado PDF oficial con plena validez legal.

![Pasarela Segura](./docs/img/user_tpv_payment_gateway.png)
![Certificado de Cobertura](./docs/img/official_certificate_pdf.png)

<div class="page-break"></div>

## 4. Gestión Post-Venta y Siniestros IA
Tus seguros activos se centralizan en el panel **"Mis Pólizas"**. Desde aquí puedes monitorizar fechas de vencimiento, descargar certificados de cobertura y, lo más importante, gestionar incidencias.

![Control de Pólizas](./docs/img/user_my_policies_list.png)
![Detalle de Póliza](./docs/img/policy_detail_modal.png)

### Reporte de Siniestros con Perito IA
Sabemos que sufrir un siniestro es un momento estresante. Por ello, hemos desarrollado al **Perito IA**, un asistente de visión artificial que analiza tus evidencias fotográficas en tiempo real. 
1. Describe brevemente qué ha ocurrido.
2. Sube una fotografía del daño.
3. Nuestra IA procesará la información al instante, ofreciéndote una pre-aprobación o derivando tu caso a un humano si es complejo. Este proceso reduce el tiempo de respuesta de días a **segundos**.

![Siniestros Inteligentes](./docs/img/user_claim_report_modal.png)

<div class="page-break"></div>

## 5. Seguridad Avanzada y Personalización
Tu seguridad es nuestra prioridad. Vesta integra capas de protección de grado bancario para tu cuenta.

### Doble Factor de Autenticación (2FA)
Te recomendamos encarecidamente activar el **2FA**. Mediante aplicaciones como Google Authenticator o Authy, añadirás una capa extra que impedirá accesos no autorizados aunque tu contraseña se vea comprometida.

![Seguridad de Cuenta](./docs/img/user_settings_security.png)

### Personalización de Experiencia
Ajusta la interfaz a tu gusto: activa el **Modo Oscuro** para reducir la fatiga visual o configura tu **Ubicación Predeterminada** para que el dashboard te ofrezca sugerencias inteligentes basadas en el clima local (ej: "Se prevé lluvia, ¡asegura tu patinete eléctrico!").

![Ajustes y Seguridad](./docs/img/user_settings_general_dark.png)
![Zona de Peligro](./docs/img/user_settings_danger_zone.png)
![Confirmación de Eliminación](./docs/img/user_delete_confirmation_modal.png)
![Escudo 2FA](./docs/img/user_2fa_config.png)

<div class="page-break"></div>

## 6. Asociado Digital y Soporte 24/7
Incluso con la tecnología más avanzada, a veces necesitamos ayuda humana o respuestas rápidas.

### Asistente Vesta (Chatbot AI)
En la esquina inferior de tu pantalla siempre encontrarás al **Asistente Vesta**. Es un experto en nuestras coberturas y procesos legales, capaz de responder tus dudas las 24 horas del día, los 7 días de la semana. Pregúntale sobre cláusulas, estados de siniestros o ayuda para navegar por la plataforma.

![Asistente Virtual](./docs/img/user_chatbot_ai_assistant.png)
![Centro de Ayuda](./docs/img/help_center_modal.png)

<div class="highlight-box">
  "En Vesta Seguros, la tecnología trabaja para ti, garantizando que siempre estés protegido ante lo inesperado con la máxima eficiencia."
</div>
