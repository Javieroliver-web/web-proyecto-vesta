#!/bin/bash

# ==========================================
# Variables de Entorno para Tomcat (Production)
# ==========================================

# API URL (Comunicación interna via localhost para evitar Nginx loop)
export API_URL="http://localhost:8080/vesta-api/api"

# URL Pública del Frontend (Para redirects y CORS)
export FRONTEND_URL="https://vesta-web.duckdns.org/vesta-web"

# Base de Datos
export SPRING_DATASOURCE_PASSWORD="admin123"

# Google OAuth Credentials
export GOOGLE_CLIENT_ID="249929311715-e9qf6foamkq5dftrpijv9phha1fltd29.apps.googleusercontent.com"
export GOOGLE_CLIENT_SECRET="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"

# Twilio (Variables placeholder por ahora)
export TWILIO_ACCOUNT_SID="tu_sid"
export TWILIO_AUTH_TOKEN="tu_token"
export TWILIO_PHONE_NUMBER="tu_numero"

# Perfil Activo
export SPRING_PROFILES_ACTIVE="prod"
