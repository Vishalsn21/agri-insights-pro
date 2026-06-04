# Stage 1: Build the Java WAR file using Maven
FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Stage 2: Deploy to Tomcat 9
FROM tomcat:9.0-jdk11
# Clear out the default Tomcat junk
RUN rm -rf /usr/local/tomcat/webapps/*
# Copy your WAR file and rename it to ROOT.war so it loads on the main URL (no /agri-insights-pro needed!)
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]