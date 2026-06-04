package com.analytics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public static Connection connect() throws ClassNotFoundException, SQLException {
        // Explicitly register the driver for Tomcat visibility
        Class.forName(DRIVER);
        
        // In Cloud Production, Railway automatically provides these environment variables
        String host = System.getenv("MYSQLHOST");
        String port = System.getenv("MYSQLPORT");
        String database = System.getenv("MYSQLDATABASE");
        String user = System.getenv("MYSQLUSER");
        String password = System.getenv("MYSQLPASSWORD");

        // If these variables don't exist, fall back to your local machine configurations
        if (host == null || host.isEmpty()) {
            host = "localhost";
            port = "3306";
            database = "agri_insights";
            user = "root";
            password = "root"; 
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true";
        return DriverManager.getConnection(url, user, password);
    }
}