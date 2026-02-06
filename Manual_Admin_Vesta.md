---
title: "Manual de Instrucciones: Administrador"
author: "Vesta Seguros"
date: "2026-02-06"
pdf_options:
  format: "A4"
  margin: "20mm"
  printBackground: true
  headerTemplate: "<div style='font-size: 10px; width: 100%; text-align: right; margin-right: 20px;'>Manual del Administrador - Vesta Seguros</div>"
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
    box-shadow: 0 15px 35px rgba(0,0,0,0.15);
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

.admin-box {
    background-color: #f1f8fe;
    border-left: 5px solid #3a86ff;
    padding: 20px;
    margin: 25px 0;
    border-radius: 0 8px 8px 0;
}

strong {
    color: #264653;
}
</style>

<div class="portada">
    <h1 style="border:none; font-size: 4.5em; letter-spacing: 2px;">VESTA SEGUROS</h1>
    <h3 style="color: #666; font-weight: 300; font-size: 1.5em;">Gestión Operativa y Control de Negocio</h3>
    <br><br><br>
    <h2 style="border:none; padding:0; font-size: 2em;">Manual del Administrador</h2>
    <p style="font-size: 1.2em; color: #888;">Guía técnica para la supervisión de siniestros, catálogo y usuarios</p>
    <br><br>
    <p>Versión 1.2 - Febrero 2026</p>
</div>

<div class="page-break"></div>

<div class="toc">

## Contenidos de Soporte
1. [El Rol del Administrador](#1-el-rol-del-administrador)
2. [Dashboard de Operaciones y Ventas](#2-dashboard-de-operaciones-y-ventas)
3. [Gestión de Usuarios y Clientes](#3-gestión-de-usuarios-y-clientes)
4. [Gestión de Siniestros IA y Peritaje Digital](#4-gestión-de-siniestros-ia-y-peritaje-digital)
5. [Administración del Catálogo de Productos](#5-administración-del-catálogo-de-productos)
6. [Seguridad Operativa y Auditoría](#6-seguridad-operativa-y-auditoría)

</div>

## 1. El Rol del Administrador
El administrador de **Vesta Seguros** es la pieza clave para la estabilidad operativa de la plataforma. Su función principal es la supervisión del flujo de negocio, garantizando que tanto la contratación de pólizas como el procesamiento de siniestros se realicen de forma fluida y conforme a las reglas del sistema.

Este manual detalla las herramientas de soporte, supervisión y gestión de incidencias que el administrador tiene a su disposición para mantener la excelencia en el servicio.

<div class="admin-box">
  <strong>IMPORTANTE:</strong> El perfil de Administrador no dispone de permisos para la modificación de roles de usuario, tarea reservada exclusivamente para el perfil de Propietario (Owner).
</div>

<div class="page-break"></div>

## 2. Dashboard de Operaciones y Ventas
El panel de control principal ofrece una visión analítica del rendimiento de Vesta. Mediante widgets dinámicos, el administrador puede monitorizar en tiempo real el volumen de ventas, la distribución por tipo de seguro y el estado general de la plataforma.

Esta pantalla permite detectar picos de demanda o anomalías en la contratación, facilitando la toma de decisiones basada en datos.

![Panel de Ventas](./docs/img/admin_dashboard_sales.png)

<div class="page-break"></div>

## 3. Gestión de Usuarios y Clientes
En el apartado de **Usuarios**, el administrador tiene acceso a la ficha completa de cada cliente registrado. Puede consultar el historial de pólizas, estados de cuenta y datos de contacto para realizar labores de soporte técnico o atención al cliente.

La interfaz permite filtrar por nombre, email o rol, facilitando la localización rápida de expedientes en entornos de alto volumen de datos.

![Listado de Usuarios](./docs/img/admin_user_list.png)
![Detalle de Usuario](./docs/img/admin_user_detail.png)

<div class="page-break"></div>

## 4. Gestión de Siniestros IA y Peritaje Digital
Esta es la sección más crítica del panel administrativo. Vesta utiliza un motor de **IA de Peritaje Digital** para pre-evaluar cada reclamación.

### El Motor de Riesgos (Fraud Score)
Cada siniestro reportado por un usuario llega al administrador con un **Scoring de Riesgo** generado automáticamente. Este score evalúa la antigüedad del usuario, imágenes de daños y metadatos para alertar sobre posibles fraudes.
- **Riesgo Bajo (Verde)**: Sugerencia de aprobación automática.
- **Riesgo Crítico (Rojo)**: Alerta de investigación manual obligatoria.

El administrador debe revisar el análisis de la IA y emitir el veredicto definitivo (Aprobar o Rechazar), el cual se comunica al usuario de forma instantánea.

![Detalle de Siniestro con IA](./docs/img/admin_claim_detail_ia.png)

<div class="page-break"></div>

## 5. Administración del Catálogo de Productos
Vesta es una plataforma viva. Desde el catálogo, el administrador puede gestionar la oferta de seguros en el marketplace.
- **Actualización de Precios**: El administrador puede ajustar las primas base según la estrategia de negocio.
- **Visibilidad**: Activar o desactivar productos (ej: seguros estacionales de eventos) de forma inmediata para todos los usuarios.
- **Gestión de Pólizas**: Supervisión global de todos los contratos vigentes, con capacidad para exportar reportes detallados en formato **Excel**.

![Catálogo de Productos](./docs/img/admin_catalog_list.png)
![Nuevo Producto](./docs/img/admin_catalog_new_product.png)
![Listado de Pólizas](./docs/img/admin_policy_list.png)

<div class="page-break"></div>

## 6. Seguridad Operativa y Auditoría
Como gestor de datos sensibles, el administrador tiene la responsabilidad de mantener la **Auditoría de Seguridad**. El panel registra cada evento crítico (Logins, modificaciones de pólizas, exportaciones de datos), permitiendo una trazabilidad total ante cualquier auditoría externa o interna.

Es obligatorio el uso de **Doble Factor de Autenticación (2FA)** para acceder a estas funciones avanzadas, garantizando que el panel de control esté blindado ante intentos de acceso no autorizado.

![Auditoría de Seguridad](./docs/img/owner_security_audit_list.png)
![Configuración 2FA](./docs/img/user_2fa_config.png)
