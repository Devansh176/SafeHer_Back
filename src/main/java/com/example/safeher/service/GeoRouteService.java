package com.example.safeher.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class GeoRouteService {

    private final String apiKey = "09afac95e6454e57ba7a5d63fa13c91d";

    public String extractRouteAndRespond(String from, String to) {
        try {
            double[] startCoords = getCoordinates(from);
            double[] endCoords = getCoordinates(to);

            if (startCoords == null || endCoords == null) {
                return "Could not get coordinates for provided locations.";
            }

            String url = String.format(
                    "https://api.geoapify.com/v1/routing?waypoints=%f,%f|%f,%f&mode=drive&apiKey=%s",
                    startCoords[0], startCoords[1], endCoords[0], endCoords[1], apiKey
            );

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray features = json.getJSONArray("features");
            if (!features.isEmpty()) {
                JSONObject summary = features.getJSONObject(0)
                        .getJSONObject("properties")
                        .getJSONObject("summary");
                double distanceKm = summary.getDouble("distance") / 1000.0;
                double durationMin = summary.getDouble("duration") / 60.0;

                return String.format("Safe route found:\nDistance: %.2f km\nEstimated time: %.2f mins",
                        distanceKm, durationMin);
            } else {
                return "Could not find a route.";
            }

        } catch (Exception e) {
            return "Error fetching route: " + e.getMessage();
        }
    }

    public double[] getCoordinates(String location) {
        try {
            String encoded = URLEncoder.encode(location, "UTF-8");
            String url = String.format(
                    "https://api.geoapify.com/v1/geocode/search?text=%s&apiKey=%s",
                    encoded, apiKey
            );

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray features = json.getJSONArray("features");
            if (features.isEmpty()) return null;

            JSONObject coords = features.getJSONObject(0).getJSONObject("geometry");
            JSONArray coordsArray = coords.getJSONArray("coordinates");

            return new double[]{coordsArray.getDouble(1), coordsArray.getDouble(0)}; // lat, lon
        } catch (Exception e) {
            return null;
        }
    }
}
