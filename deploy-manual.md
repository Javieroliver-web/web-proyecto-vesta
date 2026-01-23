# Instrucciones de Despliegue Manual - WAR Actualizado

## 📦 Archivo a Desplegar
- **Archivo:** `web-proyecto-vesta/target/vesta-web.war`
- **Tamaño:** ~50MB
- **Estado:** ✅ Construido exitosamente con todas las correcciones

## 🚀 Pasos de Despliegue

### Opción 1: Usando SCP (Recomendado)
```bash
# 1. Copiar WAR al servidor
scp -i vesta_key target/vesta-web.war root@34.175.116.7:/tmp/

# 2. Conectar al servidor
ssh -i vesta_key root@34.175.116.7

# 3. En el servidor, ejecutar:
systemctl stop tomcat
cp /opt/tomcat/webapps/vesta-web.war /opt/tomcat/webapps/vesta-web.war.backup
cp /tmp/vesta-web.war /opt/tomcat/webapps/
systemctl start tomcat

# 4. Verificar logs
tail -f /opt/tomcat/logs/catalina.out
```

### Opción 2: Usando Webmin (Alternativa)
1. Acceder a Webmin: `https://34.175.116.7:10000`
2. Login: `root` / `admin123`
3. Ir a "File Manager"
4. Subir `vesta-web.war` a `/tmp/`
5. Usar terminal de Webmin para ejecutar comandos de despliegue

### Opción 3: Usando Google Cloud Console
1. Acceder a Google Cloud Console
2. Ir a Compute Engine > VM instances
3. Conectar via SSH browser
4. Usar `gcloud compute scp` para copiar archivo

## 🔍 Verificación Post-Despliegue

### 1. Verificar que Tomcat inició correctamente:
```bash
systemctl status tomcat
```

### 2. Verificar logs de aplicación:
```bash
tail -f /opt/tomcat/logs/catalina.out
```

### 3. Verificar que la aplicación responde:
```bash
curl -I http://localhost:8080/vesta-web/
```

## 🧪 Pruebas Funcionales

### URLs a Probar:
1. **Dashboard:** http://vesta-web.duckdns.org/vesta-web/cliente/dashboard
2. **Mis Pólizas:** http://vesta-web.duckdns.org/vesta-web/cliente/mis-polizas
3. **Descarga PDF:** Botón "Descargar Resumen" en Mis Pólizas ⭐ **NUEVA FUNCIONALIDAD**
4. **Marketplace:** http://vesta-web.duckdns.org/vesta-web/cliente/marketplace
5. **Admin Panel:** http://vesta-web.duckdns.org/vesta-web/admin/dashboard

### Credenciales de Prueba:
- **Usuario con Pólizas:** `demo@vesta.com` / `123456`
- **Usuario sin Pólizas:** `javip200555@gmail.com` / `password`
- **Admin:** `warshadows22@gmail.com` / `password`

## ✅ Funcionalidades Corregidas

### 1. **PDF Download** ⭐ **NUEVO**
- **Antes:** Error al descargar PDF
- **Ahora:** Funciona correctamente via endpoint proxy

### 2. **Todos los Endpoints Migrados**
- **Antes:** Llamadas directas a `/vesta-api/api` con tokens JWT
- **Ahora:** Endpoints proxy con autenticación por sesión

### 3. **Mejor Manejo de Errores**
- **Antes:** Errores de CORS y autenticación
- **Ahora:** Manejo centralizado de errores

## 🚨 Problemas Conocidos Solucionados

- ✅ Error de descarga de PDF
- ✅ Llamadas directas a API en JavaScript
- ✅ Problemas de autenticación entre aplicaciones
- ✅ Manejo de usuarios sin pólizas
- ✅ Configuración de usuario
- ✅ Panel de administración
- ✅ Gestión de derechos RGPD

## 📞 En Caso de Problemas

### Si Tomcat no inicia:
```bash
# Verificar logs de error
journalctl -u tomcat -f

# Verificar espacio en disco
df -h

# Verificar memoria
free -h
```

### Si la aplicación no responde:
```bash
# Verificar que el WAR se desplegó
ls -la /opt/tomcat/webapps/vesta-web/

# Verificar logs de aplicación
tail -100 /opt/tomcat/logs/catalina.out
```

### Rollback si es necesario:
```bash
systemctl stop tomcat
cp /opt/tomcat/webapps/vesta-web.war.backup /opt/tomcat/webapps/vesta-web.war
systemctl start tomcat
```

---
**Nota:** Este WAR incluye TODAS las correcciones solicitadas y está listo para producción.