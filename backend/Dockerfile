FROM openjdk:21-jdk
VOLUME /tmp
COPY target/ghost-drop-0.0.1-SNAPSHOT.jar ghost-drop.jar
ENTRYPOINT ["java","-jar","/ghost-drop.jar"]