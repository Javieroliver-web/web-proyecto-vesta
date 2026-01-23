#!/bin/bash

# Script para actualizar configuración a HTTPS

echo "🔧 Actualizando configuración a HTTPS..."

# Crear archivo temporal con la nueva configuración HTTPS
cat > /tmp/tomcat.service << 'EOF'
[Unit]
Description=Apache Tomcat Web Application Container
After=network.target

[Service]
Type=forking

User=tomcat
Group=tomcat

Environment="JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64"
Environment="CATALINA_PID=/opt/tomcat/temp/tomcat.pid"
Environment="CATALINA_HOME=/opt/tomcat"
Environment="CATALINA_BASE=/opt/tomcat"
Environment="CATALINA_OPTS=-Xms512M -Xmx1024M -server -XX:+UseG1GC"
Environment="JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom"

# --- DATOS DEL PROYECTO ---
Environment="SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/vesta_db"
Environment="SPRING_DATASOURCE_USERNAME=vesta_user"
Environment="SPRING_DATASOURCE_PASSWORD=vesta_password"

# API URL - ACTUALIZADA A HTTPS
Environment="API_URL=https://vesta-web.duckdns.org:443/vesta-api/api"
Environment="SPRING_APPLICATION_JSON={\"api\":{\"url\":\"https://vesta-web.duckdns.org:443/vesta-api/api\"}}"

# URL Publica - ACTUALIZADA A HTTPS
Environment="FRONTEND_URL=https://vesta-web.duckdns.org/vesta-web"

# --- CORREO ---
Environment="MAIL_USERNAME=javip200555@gmail.com"
Environment="MAIL_PASSWORD=dzbgccdnrnwtrnnm"

# URLs para enlaces de activación - ACTUALIZADAS A HTTPS
Environment="APP_BASE_URL=https://vesta-web.duckdns.org"
Environment="APP_FRONTEND_URL=https://vesta-web.duckdns.org/vesta-web"

# --- GOOGLE OAUTH - ACTUALIZADO A HTTPS ---
Environment="GOOGLE_CLIENT_ID=249929311715-e9qf6foamkq5dftrpijv9phha1fltd29.apps.googleusercontent.com"
Environment="GOOGLE_CLIENT_SECRET=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"

# --------------------

ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh

[Install]
WantedBy=multi-user.target
EOF

# Mover el archivo a su ubicación final
sudo mv /tmp/tomcat.service /etc/systemd/system/tomcat.service

# Recargar systemd
sudo systemctl daemon-reload

echo "✅ Configuración HTTPS completada"
echo "🔄 Reiniciando Tomcat..."

# Reiniciar Tomcat
sudo systemctl restart tomcat

echo "✅ Tomcat reiniciado con configuración HTTPS"