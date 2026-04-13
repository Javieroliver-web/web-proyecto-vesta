# Vesta Web — Configuración Google Cloud VM (Preservada)

> ⚠️ La VM de Google Cloud está **apagada** para ahorrar costes.
> El despliegue activo actual usa **Koyeb** (perfil `cloud`).
> Para reactivar Google Cloud, ver `GOOGLE_CLOUD_README.md` en el proyecto API.

---

## Perfil activo

| Entorno      | Perfil              | Descripción                         |
|--------------|---------------------|-------------------------------------|
| `cloud`      | Koyeb (actual)      | Hosting gratuito permanente         |
| `prod`       | Google Cloud VM     | Configuración original (preservada) |
| `dev`        | Local               | Desarrollo con Docker Compose       |

---

## URLs de la aplicación (Koyeb)

| Servicio | URL |
|----------|-----|
| API      | `https://vesta-api.koyeb.app/vesta-api` |
| Web      | `https://vesta-web.koyeb.app/vesta-web` |

> ⚠️ Las URLs exactas se definen al crear el servicio en Koyeb.
> Actualizar `API_URL` en las variables de entorno de Koyeb si cambian.

---

## Variables de entorno requeridas en Koyeb (Web)

| Variable              | Valor ejemplo                                      |
|-----------------------|----------------------------------------------------|
| `API_URL`             | `https://vesta-api.koyeb.app/vesta-api/api`        |
| `GOOGLE_CLIENT_ID`    | ID de OAuth2 de Google Cloud Console               |
| `GOOGLE_CLIENT_SECRET`| Secret de OAuth2 de Google Cloud Console           |
| `PORT`                | 8080 (Koyeb lo inyecta automáticamente)            |

---

## OAuth2 Google — Redirect URI

Registrar en Google Cloud Console → APIs → Credenciales:

```
https://[tu-app-web].koyeb.app/vesta-web/login/oauth2/code/google
```

---

## Reactivar Google Cloud

Ver `GOOGLE_CLOUD_README.md` en el proyecto `api-proyecto-vesta`.
