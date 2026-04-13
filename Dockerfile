# ===================================
# Stage 1: Build
# ===================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiar archivos de Maven
COPY pom.xml .
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests && \
    mv target/*.war app.war

# ===================================
# Stage 2: Runtime
# ===================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar WAR desde stage de build
COPY --from=build /app/app.war app.war

# Cambiar a usuario no-root
USER spring:spring

# Variable de entorno: Koyeb inyecta PORT; perfil cloud por defecto
ENV PORT=80
ENV SPRING_PROFILES_ACTIVE=cloud

# Exponer puerto
EXPOSE ${PORT}

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT}/vesta-web/actuator/health || exit 1

# Ejecutar aplicaci\u00f3n con JVM optimizada para 512MB RAM (free tier)
ENTRYPOINT ["java", \
    "-Xms128m", "-Xmx384m", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.war"]
