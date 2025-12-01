# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# -DskipTests evita que falle si intenta conectar a la API durante el build
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jdk
WORKDIR /app

# COPIA GENÉRICA: Busca cualquier .jar en target y lo renombra a app.jar
COPY --from=build /app/target/*.jar app.jar

# Configuración de puerto y arranque
EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar"]