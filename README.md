# AgriInsights

AgriInsights is a Java-based web application designed for agricultural data analysis and decision support. It provides a centralized platform to analyze seasonal crop yields, compare agricultural performance, track commodity prices, view regional weather information, and estimate profitability through interactive dashboards. The application uses Java Servlets, JDBC, MySQL, JavaScript, Bootstrap, and Chart.js to provide a responsive and data-driven experience. It is designed to transform agricultural records into meaningful visual insights that can help users better understand yield trends, costs, revenue, and overall financial performance.

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
