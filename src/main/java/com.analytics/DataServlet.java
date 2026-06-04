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
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try (Connection conn = DB.connect(); 
             Statement stmt1 = conn.createStatement();
             Statement stmt2 = conn.createStatement()) {
            
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"status\":\"ok\",");
            
            // 1. EXTRACT CROP PROFITABILITY RECORDS WITH ANALYTICAL DATA MATRICES
            String sqlProfits = "SELECT h.crop_year AS year, h.crop_name AS crop, 'Punjab' AS state, " +
                                "ROUND(h.production_1000_tons * 1000, 0) AS production, " +
                                "ROUND(h.production_1000_tons * 1000 * e.market_price_per_ton, 2) AS revenue, " +
                                "ROUND(h.area_1000_ha * 1000 * e.farming_cost_per_ha, 2) AS totalCost, " +
                                "ROUND((h.production_1000_tons * 1000 * e.market_price_per_ton) - (h.area_1000_ha * 1000 * e.farming_cost_per_ha), 2) AS profit, " +
                                "ROUND((h.production_1000_tons / h.area_1000_ha), 2) AS yieldValue " +
                                "FROM historical_yield h " +
                                "JOIN crop_economics e ON h.crop_name = e.crop_name " +
                                "ORDER BY h.crop_year ASC, h.crop_name ASC";
            
            ResultSet rsProfits = stmt1.executeQuery(sqlProfits);
            
            json.append("\"cropProfits\": [");
            int totalRecords = 0;
            int yearMin = 9999;
            int yearMax = 0;
            
            while(rsProfits.next()) {
                totalRecords++;
                int currentYear = rsProfits.getInt("year");
                if (currentYear < yearMin) yearMin = currentYear;
                if (currentYear > yearMax) yearMax = currentYear;
                
                json.append("{")
                    .append("\"year\":").append(currentYear).append(",")
                    .append("\"crop\":\"").append(rsProfits.getString("crop")).append("\",")
                    .append("\"state\":\"").append(rsProfits.getString("state")).append("\",")
                    .append("\"production\":").append(rsProfits.getDouble("production")).append(",")
                    .append("\"revenue\":").append(rsProfits.getDouble("revenue")).append(",")
                    .append("\"totalCost\":").append(rsProfits.getDouble("totalCost")).append(",")
                    .append("\"profit\":").append(rsProfits.getDouble("profit")).append(",")
                    .append("\"yieldValue\":").append(rsProfits.getDouble("yieldValue"))
                    .append("},");
            }
            if(totalRecords > 0) {
                json.deleteCharAt(json.length() - 1); // remove trailing comma
            }
            json.append("],");
            
            // 2. EXTRACT HYDROPONICS TOPOLOGY ANALYSIS METRICS
            String sqlHydro = "SELECT crop_name, cultivation_method, yield_kg_per_sq_meter, water_usage_liters_per_kg " +
                              "FROM hydroponics_comparison";
            
            ResultSet rsHydro = stmt2.executeQuery(sqlHydro);
            
            json.append("\"hydroponics\": [");
            boolean hasHydro = false;
            while(rsHydro.next()) {
                hasHydro = true;
                json.append("{")
                    .append("\"crop\":\"").append(rsHydro.getString("crop_name")).append("\",")
                    .append("\"method\":\"").append(rsHydro.getString("cultivation_method")).append("\",")
                    .append("\"yieldPerSqm\":").append(rsHydro.getDouble("yield_kg_per_sq_meter")).append(",")
                    .append("\"waterUsageLiters\":").append(rsHydro.getDouble("water_usage_liters_per_kg"))
                    .append("},");
            }
            if(hasHydro) {
                json.deleteCharAt(json.length() - 1);
            }
            json.append("],");
            
            // 3. ATTACH SYSTEM ARCHITECTURE SUMMARY SCHEMA METRICS
            if (yearMin == 9999) { yearMin = 2020; yearMax = 2022; }
            json.append("\"summary\": {")
                .append("\"totalCrops\":4,")
                .append("\"yearMin\":").append(yearMin).append(",")
                .append("\"yearMax\":").append(yearMax).append(",")
                .append("\"totalRecords\":").append(totalRecords)
                .append("}");
                
            json.append("}");
            out.print(json.toString());
            
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"error\":\"Runtime Pipeline Exception: " + e.getMessage() + "\"}");
        }
    }
}