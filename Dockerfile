FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle* settings.gradle* ./
COPY gradle.properties ./gradle.properties
RUN chmod +x ./gradlew

COPY modules ./modules
RUN ./gradlew :infrastructure:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/modules/infrastructure/build/libs/xm-crypto-investment-*.jar /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]