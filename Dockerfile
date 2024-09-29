# Usar una imagen base de OpenJDK
FROM openjdk:17-jdk-alpine

# Instalar tzdata para configurar la zona horaria
RUN apk add --no-cache tzdata

# Configurar la zona horaria a America/Lima
ENV TZ=America/Lima

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR del proyecto
ARG JAR_FILE=target/ecommerce-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} ecommerce.jar

# Exponer el puerto 8080
EXPOSE 8080

# Ejecutar la aplicacion
ENTRYPOINT ["java", "-jar", "ecommerce.jar"]
