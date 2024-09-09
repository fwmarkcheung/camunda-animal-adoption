# multi-layer build

# Create an image labelled "build"
# and use it to run Maven and build the fat jar, then unpack it.
#FROM openjdk:17-jdk-alpine AS build
FROM openjdk:21-jdk AS build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Create the app image using the build image above
#FROM openjdk:17-jdk-alpine
FROM openjdk:21-jdk
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","io.camunda.demo.pick_animal.PickAnimalApplication"]