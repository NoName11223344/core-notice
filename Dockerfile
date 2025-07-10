# Stage 1: Build with custom Maven & JDK 23.0.2
FROM eclipse-temurin:23.0.2_7-jdk AS builder

# Install Maven 3.3.2 manually
ENV MAVEN_VERSION=3.9.6
RUN apt-get update && apt-get install -y curl tar && \
    curl -fsSL https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar -xz -C /opt && \
    ln -s /opt/apache-maven-$MAVEN_VERSION /opt/maven
ENV PATH=/opt/maven/bin:$PATH

# Build Spring Boot project
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:23.0.2_7-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.additional-location=file:/app/config/"]
