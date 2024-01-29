FROM openjdk:11
RUN mkdir /app
COPY ./target/synergy-cooperative-*.war /app/synergy-cooperative.war
WORKDIR /app
EXPOSE 5701 8000
ENTRYPOINT [ "java", "-jar", "/app/synergy-cooperative.war" ]
