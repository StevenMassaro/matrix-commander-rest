FROM matrixcommander/matrix-commander:8.0.1
RUN dnf install -y --nodocs java-latest-openjdk-headless
COPY target/matrix-commander-rest-0.0.1-SNAPSHOT.jar matrix-commander-rest.jar
ENTRYPOINT ["java", "-jar", "matrix-commander-rest.jar"]