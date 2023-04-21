# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY api/pom.xml .
COPY api/src src

RUN chmod a+x ./mvnw
RUN --mount=type=cache,target=/root/.m2 ./mvnw package -DskipTests  \
    && mkdir -p target/dependency  \
    && (cd target/dependency; jar -xf ../*.jar)
# ------------------------------------------------------------------------------

# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
ARG USER=app
ARG DEPENDENCY=/workspace/app/target/dependency

RUN addgroup -S ${USER} && adduser -S ${USER} -G ${USER}
USER ${USER}

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "crypto.investments.api.ApiApplication"]