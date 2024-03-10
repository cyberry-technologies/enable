# Use an official OpenJDK runtime as a parent image
FROM maven:3.8.3-openjdk-17

# Set the working directory to /app
WORKDIR /app

# Copy the pom.xml and source code to the container
COPY ../project/pom.xml .
COPY ../project/src ./src

# Install Maven
#RUN apk add --no-cache maven

# Build the application with Maven
RUN mvn clean package -DskipTests

ENTRYPOINT ["java", "-jar", "target/enable-execution-service-0.0.1-SNAPSHOT.jar"]