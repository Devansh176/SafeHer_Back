package com.example.safeher.crime;

import java.util.ArrayList;
import java.util.List;

public class PolylineDecoder {
    public static List<double[]> decode(String encodedPath) {
        List<double[]> path = new ArrayList<>();
        int index = 0, len = encodedPath.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encodedPath.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encodedPath.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            double[] latLng = new double[]{lat / 1E5, lng / 1E5};
            path.add(latLng);
        }

        return path;
    }
}
