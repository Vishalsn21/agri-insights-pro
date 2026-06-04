package com.analytics;
import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
    public static Connection connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Update "password" to your actual MySQL password
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/agri_insights_pro?serverTimezone=UTC", "root", "Vishalsingh2851714");
    }
}