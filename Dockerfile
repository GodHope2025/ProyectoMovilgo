# Etapa 1: compila el JAR con Maven
FROM maven:3.9-eclipse-temurin-21 AS compilacion
WORKDIR /app
COPY pom.xml .
COPY config config
RUN mvn -B -ntp dependency:go-offline -Dcheckstyle.skip
COPY src src
RUN mvn -B -ntp package -DskipTests -Dcheckstyle.skip -Djacoco.skip=true

# Etapa 2: imagen liviana solo con el JRE para ejecutar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=compilacion /app/target/movilgo.jar app.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC" \
    THYMELEAF_CACHE=true
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
