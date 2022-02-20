FROM gradle:7.3.1-jdk17 as builder

WORKDIR /build

COPY build.gradle /build
COPY gradle.properties /build
COPY src /build/src
COPY settings.gradle /build

RUN gradle shadowJar
RUN echo Files
RUN ls -l /build/build/libs

FROM openjdk:17
COPY --from=builder /build/build/libs/WeatherWrapper-1.0.0-all.jar WeatherWrapper-1.0.0-all.jar

CMD ["java", "-jar", "WeatherWrapper-1.0.0-all.jar"]