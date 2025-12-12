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
    mv target/*.jar app.jar

# ===================================
# Stage 2: Runtime
# ===================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar JAR desde stage de build
COPY --from=build /app/app.jar app.jar

# Cambiar a usuario no-root
USER spring:spring

# Exponer puerto
EXPOSE 80

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
