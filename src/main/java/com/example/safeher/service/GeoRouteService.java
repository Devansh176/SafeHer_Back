package com.example.safeher.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
public class GeoRouteService {

    private final String apiKey = "09afac95e6454e57ba7a5d63fa13c91d";

    public String extractRouteAndRespond(String message) {
        try {
            String[] locations = parseLocationsFromMessage(message);
            if (locations.length != 2) {
                return "Please provide both source and destination cities.";
            }

            double[] source = getCoordinates(locations[0]);
            double[] destination = getCoordinates(locations[1]);

            if (source == null || destination == null) {
                return "Could not get coordinates for provided locations.";
            }

            return fetchRouteDetails(source, destination);

        } catch (Exception e) {
            return "Error fetching route: " + e.getMessage();
        }
    }

    private String[] parseLocationsFromMessage(String message) {
        message = message.replaceAll("[^a-zA-Z0-9, ]", "").toLowerCase();
        String[] words = message.split("from|to");
        if (words.length >= 3) {
            return new String[]{words[1].trim(), words[2].trim()};
        } else {
            return new String[0];
        }
    }

    private double[] getCoordinates(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, "UTF-8");
            String urlStr = "https://api.geoapify.com/v1/geocode/search?text=" + encodedCity + "&apiKey=" + apiKey;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder res = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                res.append(line);
            }
            in.close();
            conn.disconnect();

            JSONObject json = new JSONObject(res.toString());
            JSONArray features = json.getJSONArray("features");

            if (features.isEmpty()) return null;

            JSONObject coords = features.getJSONObject(0).getJSONObject("geometry");
            JSONArray coordArray = coords.getJSONArray("coordinates");

            return new double[]{coordArray.getDouble(1), coordArray.getDouble(0)}; // lat, lng

        } catch (Exception e) {
            return null;
        }
    }

    private String fetchRouteDetails(double[] src, double[] dest) throws Exception {
        String routeUrl = String.format(
                "https://api.geoapify.com/v1/routing?waypoints=%f,%f|%f,%f&mode=walk&apiKey=%s",
                src[0], src[1], dest[0], dest[1], apiKey
        );

        URL url = new URL(routeUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Accept", "application/json");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder res = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            res.append(line);
        }
        in.close();
        conn.disconnect();

        JSONObject json = new JSONObject(res.toString());
        JSONArray features = json.getJSONArray("features");

        if (features.isEmpty()) {
            return "No route found.";
        }

        JSONObject properties = features.getJSONObject(0).getJSONObject("properties");

        double distance = properties.getDouble("distance"); // in meters
        int time = properties.getInt("time"); // in seconds

        return String.format("Safe walking route found! Distance: %.2f km, Time: %d min.",
                distance / 1000, time / 60);
    }
}
