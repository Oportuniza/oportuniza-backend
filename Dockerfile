# Stage 1: Build the application with Maven
FROM openjdk:21-slim AS build

# Install necessary packages
RUN apt-get update && apt-get install -y \
    curl \
    gnupg \
    software-properties-common

# Install HashiCorp GPG key
RUN curl -fsSL https://apt.releases.hashicorp.com/gpg | apt-key add -

# Install Google Cloud SDK
RUN apt-add-repository "deb https://packages.cloud.google.com/apt cloud-sdk main" && \
    apt-get update && \
    apt-get install -y google-cloud-sdk

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and the source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:21-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the previous stage
COPY --from=build /app/target/oportuniza-backend-0.0.1-SNAPSHOT.jar app.jar

# Set up Google Cloud authentication
COPY credentials.json /root/key.json
RUN gcloud auth activate-service-account --key-file=/root/key.json

# Set up Application Default Credentials (ADC)
RUN gcloud auth application-default login

# Run the jar file
ENTRYPOINT ["java","-jar","/app/app.jar"]

# Expose port 8080
EXPOSE 8080
