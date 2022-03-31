FROM gcr.io/distroless/java11

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

CMD ["/app.jar"]