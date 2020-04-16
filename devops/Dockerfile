FROM java:8-alpine
ADD app.jar app.jar
RUN sh -c 'touch app.jar'
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar","Application"]