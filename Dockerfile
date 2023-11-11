FROM openjdk:17-jdk-slim
COPY build/libs/delivery-points-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Xmx3500m","-Xms3500m","-XX:+UseG1GC","-XX:ParallelGCThreads=2","-XX:ConcGCThreads=1","-XX:StartFlightRecording=filename=/app/recordings/recording.jfr","-jar","/app.jar"]
