# ===== Estagio 1: build com Maven =====
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ===== Estagio 2: imagem final enxuta, so com o JRE =====
FROM eclipse-temurin:21-jre-alpine

# Usuario nao privilegiado (requisito DevOps)
RUN addgroup -S amparo && adduser -S amparo -G amparo

WORKDIR /app
COPY --from=build /build/target/amparo-api-*.jar app.jar

# Variaveis de ambiente lidas pelo application.properties (sobrescritas pelo compose)
ENV SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE=docker \
    DB_URL=jdbc:postgresql://db:5432/amparo \
    DB_USER=amparo \
    DB_PASSWORD=amparo \
    JWT_SECRET=troque-este-segredo-em-producao-minimo-32-caracteres \
    DEVICE_TOKEN=esp32-amparo-dev-token \
    CORS_ORIGINS=*

EXPOSE 8080
USER amparo

ENTRYPOINT ["java", "-jar", "app.jar"]
