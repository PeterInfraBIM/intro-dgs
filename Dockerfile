FROM openjdk
LABEL author="peter.willems@infrabim.nl"
COPY build/libs/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar" ]