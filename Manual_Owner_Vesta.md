---
title: "Manual de Instrucciones: Owner"
author: "Vesta Seguros"
date: "2026-02-06"
pdf_options:
  format: "A4"
  margin: "20mm"
  printBackground: true
  headerTemplate: "<div style='font-size: 10px; width: 100%; text-align: right; margin-right: 20px;'>Manual del Owner - Vesta Seguros</div>"
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
    border-left: 6px solid #9d0208;
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
    background: #fffafa;
    padding: 30px;
    border-radius: 12px;
    margin-bottom: 50px;
    border: 1px solid #f08080;
}

.page-break {
    page-break-after: always;
}

.owner-box {
    background-color: #fdf0f0;
    border-left: 5px solid #d00000;
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
    <h3 style="color: #666; font-weight: 300; font-size: 1.5em;">Fiscalización, Seguridad y Estrategia</h3>
    <br><br><br>
    <h2 style="border:none; padding:0; font-size: 2em;">Manual del Propietario (Owner)</h2>
    <p style="font-size: 1.2em; color: #888;">Autoridad máxima del sistema: Auditoría crítica y control de privilegios</p>
    <br><br>
    <p>Versión 1.2 - Febrero 2026</p>
</div>

<div class="page-break"></div>

<div class="toc">

## Contenidos de Alta Autoridad
1. [El Rol del Owner en Vesta](#1-el-rol-del-owner-en-vesta)
2. [Gestión Exclusiva de Roles y Privilegios](#2-gestión-exclusiva-de-roles-y-privilegios)
3. [Auditoría Técnica y Trazabilidad de Eventos](#3-auditoría-técnica-y-trazabilidad-de-eventos)
4. [Acciones Críticas de Seguridad](#4-acciones-críticas-de-seguridad)
5. [Fiscalización de Negocio e IA](#5-fiscalización-de-negocio-e-ia)

</div>

## 1. El Rol del Owner en Vesta
El perfil de **Owner** constituye la máxima autoridad técnica y legal dentro de la plataforma Vesta Seguros. Su responsabilidad trasciende la operativa diaria, enfocándose en la fiscalización del sistema, la gestión de la seguridad crítica y el cumplimiento normativo.

Este manual documenta las funciones restringidas a las que únicamente el Owner tiene acceso, garantizando que el control de privilegios y la integridad de los registros se mantengan bajo una supervisión superior.

<div class="page-break"></div>

## 2. Gestión Exclusiva de Roles y Privilegios
A diferencia de cualquier otro perfil, el Owner es el único usuario con capacidad para modificar la jerarquía de accesos de la plataforma.

### Escalación de Privilegios
El Owner puede asignar el rol de **Administrador** a usuarios locales para delegar la operativa diaria. Esta función es fundamental para la gestión del equipo humano y debe realizarse bajo estrictos protocolos de seguridad, ya que otorga acceso a datos sensibles de clientes y siniestros.

<div class="owner-box">
  <strong>FUNCIÓN COMPARTIDA:</strong> El Owner comparte con el administrador la visión del dashboard de ventas y gestión de catálogo, pero es el único que puede ver y pulsar el botón de "Cambiar Rol" en el listado de usuarios.
</div>

![Modificar Permisos](./docs/img/admin_user_role.png)
![Listado de Usuarios](./docs/img/admin_user_list.png)

<div class="page-break"></div>

## 3. Auditoría Técnica y Trazabilidad de Eventos
El Owner es el garante del **Derecho a la Auditoría**. El sistema genera registros inmutables de cada acción relevante realizada por administradores y clientes.
- **Trazabilidad de Logins**: Seguimiento de accesos sociales (Google) y locales.
- **Exportación de Historial**: Capacidad exclusiva de generar archivos `.txt` detallados con la traza de actividad de cualquier usuario. Estos registros son fundamentales ante posibles disputas legales o auditorías de seguridad externas.

![Auditoría de Seguridad](./docs/img/owner_security_audit_list.png)
![Logs Descargados](./docs/img/downloaded_logs_list.png)
![Contenido del Log](./docs/img/user_activity_logs_content.png)

<div class="page-break"></div>

## 4. Acciones Críticas de Seguridad
Existen escenarios donde es necesario intervenir físicamente en la disponibilidad de los datos o accesos. El Owner dispone de herramientas exclusivas para:
- **Bloqueo Preventivo**: Suspender temporalmente cuentas bajo sospecha de fraude o brecha de seguridad.
- **Eliminación Permanente de Usuarios**: Ejecutar la baja definitiva de usuarios, gestionando diligentemente el borrado lógico y la anonimización de datos para cumplir con el **Derecho al Olvido** del RGPD sin comprometer los registros de seguros legalmente obligatorios.

![Bloqueo de Cuenta](./docs/img/admin_user_block.png)
![Confirmación de Eliminación](./docs/img/admin_user_delete.png)

<div class="page-break"></div>

## 5. Fiscalización de Negocio e IA
Como autoridad máxima, el Owner supervisa la eficacia del motor de IA de Vesta. Puede auditar las resoluciones de siniestros y validar si el motor de riesgos (Fraud Score) se está comportando según la estrategia de la empresa, ajustando las políticas operativas del equipo de administración.

![Detalle de Siniestro con IA](./docs/img/admin_claim_detail_ia.png)
![Catálogo y Pólizas](./docs/img/admin_catalog_list.png)

<div class="page-break"></div>

## 6. Configuración Global de la Plataforma
El Owner tiene la potestad de ajustar la apariencia y parámetros globales del sistema para alinearlos con la identidad corporativa.

![Configuración Global](./docs/img/admin_settings_appearance.png)
