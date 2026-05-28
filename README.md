# AgriInsights

A Java web application for agricultural analytics.

## Setup

1. Ensure you have Java 8+, Maven, and Tomcat installed.
2. Set up a MySQL database named `agriinsights`.
3. Update the database credentials in `DB.java`.
4. Run `mvn clean package` to build the WAR file.
5. Deploy the WAR file to Tomcat.

## Usage

- Access the application at `http://localhost:8080/agriinsights/`
- The servlet is mapped to `/data` for JSON data.

## Dependencies

- MySQL Connector/J: Download and place in `web/WEB-INF/lib/`
- Servlet API: Copy from Tomcat's lib folder to `web/WEB-INF/lib/`

Or use Maven dependencies as configured in `pom.xml`.