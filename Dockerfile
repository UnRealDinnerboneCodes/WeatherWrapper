FROM gradle:8.0.0-jdk19 as builder

WORKDIR /build

COPY build.gradle /build
COPY gradle.properties /build
COPY src /build/src
COPY settings.gradle /build

RUN gradle shadowJar

FROM openjdk:19-alpine
COPY --from=builder "/build/build/libs/WeatherAPI-1.0.0-all.jar" WeatherWrapper-1.0.0-all.jar

CMD ["java", "-jar", "WeatherWrapper-1.0.0-all.jar"]