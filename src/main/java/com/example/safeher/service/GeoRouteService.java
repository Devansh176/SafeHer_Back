package com.example.safeher.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeoRouteService {

    private final String apiKey = "09afac95e6454e57ba7a5d63fa13c91d"; // Your actual key

    public String extractRouteAndRespond(String message) {
        try {
            // Step 1: Extract locations using regex
            Pattern pattern = Pattern.compile("from (.*?) to (.*)");
            Matcher matcher = pattern.matcher(message);

            if (!matcher.find()) {
                return "Please use format: 'route from [source] to [destination]'";
            }

            String source = matcher.group(1).trim();
            String destination = matcher.group(2).trim();

            // Step 2: Get coordinates
            double[] srcCoords = getCoordinates(source);
            double[] destCoords = getCoordinates(destination);

            if (srcCoords == null || destCoords == null) {
                return "Could not get coordinates for provided locations.";
            }

            // Step 3: Build routing request
            String routingUrl = String.format(
                    "https://api.geoapify.com/v1/routing?waypoints=%f,%f|%f,%f&mode=walk&apiKey=%s",
                    srcCoords[0], srcCoords[1], destCoords[0], destCoords[1], apiKey
            );

            URL url = new URL(routingUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            in.close();
            conn.disconnect();

            return "Route fetched successfully! 🛣️ (Details can be viewed on map integration)";
        } catch (Exception e) {
            return "Error fetching route: " + e.getMessage();
        }
    }

    private double[] getCoordinates(String place) {
        try {
            String encodedPlace = URLEncoder.encode(place, "UTF-8");
            String urlStr = String.format(
                    "https://api.geoapify.com/v1/geocode/search?text=%s&apiKey=%s",
                    encodedPlace, apiKey
            );

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                json.append(line);
            }

            in.close();
            conn.disconnect();

            // Extract coordinates from JSON response
            String latRegex = "\"lat\":([0-9.\\-]+)";
            String lonRegex = "\"lon\":([0-9.\\-]+)";
            Pattern latPattern = Pattern.compile(latRegex);
            Pattern lonPattern = Pattern.compile(lonRegex);
            Matcher latMatcher = latPattern.matcher(json);
            Matcher lonMatcher = lonPattern.matcher(json);

            if (latMatcher.find() && lonMatcher.find()) {
                double lat = Double.parseDouble(latMatcher.group(1));
                double lon = Double.parseDouble(lonMatcher.group(1));
                return new double[]{lat, lon};
            }

        } catch (Exception e) {
            System.out.println("Failed to get coordinates: " + e.getMessage());
        }

        return null;
    }
}
