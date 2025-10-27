FROM maven:3.9-eclipse-temurin-24 AS build

WORKDIR /opt/ine5417

COPY pom.xml settings.xml ./
COPY ./src/main ./src/main

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -s settings.xml clean package -DskipTests

FROM maven:3.9-eclipse-temurin-24

WORKDIR /opt/ine5417

COPY --from=build /opt/ine5417/target/ine5417-*.jar ine5417.jar

CMD java -jar ine5417.jar
