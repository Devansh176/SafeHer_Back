//package com.example.safeher.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//@RestController
//@RequestMapping("/api/route")
//public class RouteController {
//
//    @GetMapping
//    public ResponseEntity<String> getRoute(
//            @RequestParam double startLat,
//            @RequestParam double startLng,
//            @RequestParam double endLat,
//            @RequestParam double endLng
//    ) {
//        try {
//            String apiKey = "09afac95e6454e57ba7a5d63fa13c91d";
//            String urlString = String.format(
//                    "https://api.geoapify.com/v1/routing?waypoints=%f,%f|%f,%f&mode=walk&apiKey=%s",
//                    startLat, startLng, endLat, endLng, apiKey
//            );
//
//            URL url = new URL(urlString);
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            http.setRequestMethod("GET");
//            http.setRequestProperty("Accept", "application/json");
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String line;
//            while ((line = in.readLine()) != null) {
//                response.append(line);
//            }
//            in.close();
//            http.disconnect();
//
//            return ResponseEntity.ok(response.toString());
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to fetch route: " + e.getMessage());
//        }
//    }
//}
//
