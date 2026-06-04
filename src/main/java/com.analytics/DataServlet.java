package com.analytics;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/dashboard")
public class DataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        // Using your original, working DB connection
        try (Connection conn = DB.connect(); 
             Statement stmt1 = conn.createStatement();
             Statement stmt2 = conn.createStatement()) {
            
            StringBuilder json = new StringBuilder("{");
            
            // 1. ADVANCED QUERY: Join Historical Yield with Economics
            ResultSet rsHistory = stmt1.executeQuery(
                "SELECT h.crop_year, h.crop_name, " +
                "ROUND((h.production_1000_tons * 1000 * e.market_price_per_ton) - (h.area_1000_ha * 1000 * e.farming_cost_per_ha), 2) AS profit " +
                "FROM historical_yield h " +
                "JOIN crop_economics e ON h.crop_name = e.crop_name " +
                "ORDER BY h.crop_year ASC, h.crop_name ASC"
            );
            
            json.append("\"historicalProfit\": [");
            while(rsHistory.next()) {
                json.append("{")
                    .append("\"year\":").append(rsHistory.getInt("crop_year")).append(",")
                    .append("\"crop\":\"").append(rsHistory.getString("crop_name")).append("\",")
                    .append("\"profit\":").append(rsHistory.getDouble("profit"))
                    .append("},");
            }
            if(json.charAt(json.length()-1) == ',') json.deleteCharAt(json.length()-1);
            json.append("],");
            
            // 2. SECOND QUERY: Pull Hydroponics vs Traditional Data
            ResultSet rsHydro = stmt2.executeQuery(
                "SELECT crop_name, cultivation_method, yield_kg_per_sq_meter, water_usage_liters_per_kg " +
                "FROM hydroponics_comparison"
            );
            
            json.append("\"hydroponics\": [");
            while(rsHydro.next()) {
                json.append("{")
                    .append("\"crop\":\"").append(rsHydro.getString("crop_name")).append("\",")
                    .append("\"method\":\"").append(rsHydro.getString("cultivation_method")).append("\",")
                    .append("\"yield\":").append(rsHydro.getDouble("yield_kg_per_sq_meter")).append(",")
                    .append("\"water\":").append(rsHydro.getDouble("water_usage_liters_per_kg"))
                    .append("},");
            }
            if(json.charAt(json.length()-1) == ',') json.deleteCharAt(json.length()-1);
            json.append("]");
            
            json.append("}");
            out.print(json.toString());
            
        } catch (Exception e) {
            resp.setStatus(500);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}