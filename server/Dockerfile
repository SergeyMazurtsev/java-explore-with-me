FROM bellsoft/liberica-openjdk-alpine:11.0.16.1-1
COPY target/*.jar app.jar
#ENV TZ=Europe/Moscow
ENTRYPOINT ["java","-jar","/app.jar"]