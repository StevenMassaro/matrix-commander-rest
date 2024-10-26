FROM matrixcommander/matrix-commander:7.7.1
RUN dnf install -y --nodocs java-21-openjdk
COPY target/matrix-commander-rest-0.0.1-SNAPSHOT.jar matrix-commander-rest.jar
ENTRYPOINT ["java", "-jar", "matrix-commander-rest.jar"]