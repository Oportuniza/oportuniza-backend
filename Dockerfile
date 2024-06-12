# Stage 1: Build the application with Maven
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and the source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the previous stage
COPY --from=build /app/target/your-app.jar app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app.jar"]

# Expose port 8080
EXPOSE 8080
