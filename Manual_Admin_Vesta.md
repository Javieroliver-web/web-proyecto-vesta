---
title: "Manual de Instrucciones: Vesta Seguros"
subtitle: "Guía para Administradores"
author: "Equipo de Desarrollo Vesta"
date: "2026-02-06"
pdf_options:
  format: "A4"
  margin: "25mm"
  printBackground: true
  headerTemplate: |-
    <style>
      section { margin: 0 auto; font-family: system-ui; font-size: 10px; color: #888; text-align: center; width: 100%; }
    </style>
    <section>
      <span>Vesta Seguros - Guía para Administradores</span>
    </section>
  footerTemplate: |-
    <style>
      section { margin: 0 auto; font-family: system-ui; font-size: 10px; color: #888; text-align: center; width: 100%; }
    </style>
    <section>
      Página <span class="pageNumber"></span> de <span class="totalPages"></span>
    </section>
---

<!-- Portada -->
<div style="text-align: center; margin-top: 150px;">
  <h1 style="font-size: 48px; color: #c0392b; margin-bottom: 20px;">Manual de Administración</h1>
  <p style="font-size: 24px; color: #7f8c8d;">Guía Técnica y Operativa - Vesta Seguros</p>
  <br><br><br>
  <p style="font-size: 16px;"><strong>Versión:</strong> 1.0.0</p>
  <p style="font-size: 16px;"><strong>Fecha:</strong> 6 de Febrero, 2026</p>
  <br><br>
  <div style="border: 1px solid #c0392b; padding: 10px; display: inline-block; color: #c0392b;">
    <strong>CONFIDENCIAL: USO INTERNO EXCLUSIVO</strong>
  </div>
</div>

<div style="page-break-after: always;"></div>

# 1. Guía para Administradores

## Gestión de Catálogo
- **Para subir un precio**: Vaya a `Admin > Productos`, edite el ítem y cambie el `Precio Base`. Los cambios aplican solo a nuevas pólizas contratadas a partir del cambio.
- **Para descatalogar**: Marque el producto como `Inactivo`. Desaparecerá de la tienda visible para los usuarios, pero las pólizas existentes asociadas a este producto se mantendrán vigentes hasta su expiración.

## Revisión de Fraude
- Revise diariamente la cola de "Siniestros en Revisión".
- El sistema marca en rojo los casos con `Score > 50`, indicando un riesgo alto.
- **Acciones requeridas**:
    - Revise la foto adjunta para verificar la consistencia con la descripción.
    - Consulte el historial del cliente (reclamaciones recientes, antigüedad).
    - Apruebe o rechace el siniestro basándose en estas evidencias.
