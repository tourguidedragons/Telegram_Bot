FROM openjdk:21
VOLUME /tmp
ADD ./target/TourBot-0.0.1-SNAPSHOT.jar maven-wrapper.jar
RUN bash -c 'touch /app.jar'
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "maven-wrapper.jar"]