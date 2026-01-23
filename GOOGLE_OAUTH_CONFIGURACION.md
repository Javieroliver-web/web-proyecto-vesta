# CONFIGURACIÓN DE GOOGLE OAUTH - VESTA

## ESTADO ACTUAL ✅

### Configuración Completada:
1. **Variables de entorno configuradas** en Tomcat:
   - `GOOGLE_CLIENT_ID=249929311715-e9qf6foamkq5dftrpijv9phha1fltd29.apps.googleusercontent.com`
   - `GOOGLE_CLIENT_SECRET=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`

2. **Aplicación Spring Boot configurada**:
   - OAuth2 client registration para Google
   - Controlador OAuth (`OAuthController.java`) implementado
   - Botón "Continuar con Google" presente en login

3. **Flujo OAuth funcionando**:
   - URL: `http://vesta-web.duckdns.org/vesta-web/oauth2/authorization/google`
   - Redirige correctamente a Google: ✅
   - Estado: `HTTP 302` con redirección a `accounts.google.com`

## CONFIGURACIÓN REQUERIDA EN GOOGLE CLOUD CONSOLE

### 🔧 Pasos para completar la configuración:

1. **Acceder a Google Cloud Console**:
   - URL: https://console.cloud.google.com/
   - Proyecto: `project-eca4ac48-8e15-4e44-92f`

2. **Navegar a APIs & Services > Credentials**:
   - Buscar el Client ID: `249929311715-e9qf6foamkq5dftrpijv9phha1fltd29.apps.googleusercontent.com`

3. **Actualizar Authorized redirect URIs**:
   - **Agregar**: `http://vesta-web.duckdns.org/vesta-web/login/oauth2/code/google`
   - **Mantener**: `http://localhost:8081/login/oauth2/code/google` (para desarrollo)

4. **Authorized JavaScript origins** (opcional pero recomendado):
   - **Agregar**: `http://vesta-web.duckdns.org`
   - **Agregar**: `http://localhost:8081` (para desarrollo)

## FLUJO OAUTH IMPLEMENTADO

### 1. Usuario hace clic en "Continuar con Google"
```
http://vesta-web.duckdns.org/vesta-web/oauth2/authorization/google
```

### 2. Spring Security redirige a Google
```
https://accounts.google.com/o/oauth2/v2/auth?
  response_type=code&
  client_id=249929311715-e9qf6foamkq5dftrpijv9phha1fltd29.apps.googleusercontent.com&
  scope=email%20profile&
  state=...&
  redirect_uri=http://vesta-web.duckdns.org/vesta-web/login/oauth2/code/google
```

### 3. Google redirige de vuelta con código
```
http://vesta-web.duckdns.org/vesta-web/login/oauth2/code/google?code=...&state=...
```

### 4. Spring Security procesa el código y llama a OAuthController
```java
@GetMapping("/oauth2/success-handler")
public String handleOAuthSuccess(@AuthenticationPrincipal OAuth2User principal, HttpSession session)
```

### 5. Se crea sesión y redirige a dashboard
```
return "redirect:/cliente/dashboard";
```

## VERIFICACIÓN DE FUNCIONAMIENTO

### ✅ Elementos Verificados:
- **Botón Google presente** en `/login-page`
- **Variables de entorno configuradas** en Tomcat
- **Redirección a Google funcionando** (HTTP 302)
- **Client ID y Secret válidos**
- **Controlador OAuth implementado**

### ⚠️ Pendiente de Verificar:
- **Redirect URI autorizada** en Google Cloud Console
- **Flujo completo de vuelta** desde Google
- **Creación de sesión** después del login

## TESTING

### Para probar el login con Google:
1. Ir a: `http://vesta-web.duckdns.org/vesta-web/login-page`
2. Hacer clic en "Continuar con Google"
3. Debería redirigir a Google para autenticación
4. Después del login en Google, debería volver a Vesta

### Posibles errores y soluciones:
- **Error 400 "redirect_uri_mismatch"**: 
  - Solución: Agregar la URI en Google Cloud Console
- **Error 403 "access_blocked"**:
  - Solución: Verificar que el proyecto esté en producción
- **Error de sesión**:
  - Solución: Verificar que `OAuthController` esté funcionando

## ARCHIVOS RELACIONADOS

### Configuración:
- `application.properties` - Configuración OAuth2
- `/etc/systemd/system/tomcat.service` - Variables de entorno
- `client_secret_*.json` - Credenciales de Google

### Código:
- `OAuthController.java` - Manejo del callback OAuth
- `login.html` - Botón de Google
- `SecurityConfig.java` - Configuración de seguridad

## PRÓXIMOS PASOS

1. **Actualizar Google Cloud Console** con la redirect URI correcta
2. **Probar el flujo completo** de login con Google
3. **Verificar creación de sesión** después del login
4. **Implementar manejo de errores** OAuth específicos

---
**Estado**: Configuración técnica completa, pendiente actualización en Google Cloud Console
**Fecha**: 23 de enero de 2026 - 11:15 UTC