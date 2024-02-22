FROM openjdk:21
VOLUME /tmp
ADD ./Tour_guide/.mvn/wrapper/maven-wrapper.jar maven-wrapper.jar
RUN bash -c 'touch /app.jar'
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "maven-wrapper.jar"]