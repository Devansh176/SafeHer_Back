package com.example.safeher.service;

import com.example.safeher.crime.CrimeZoneData;
import com.example.safeher.crime.GeoZone;
import com.example.safeher.crime.LightingMap;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SafetyScoringService {

    public double calculateSafetyScore(List<double[]> routePoints) {
        List<GeoZone> crimeZones = CrimeZoneData.getHighCrimeZones();
        List<GeoZone> lightingZones = LightingMap.getPoorlyLitZones();

        double score = 100.0;

        for (double[] point : routePoints) {
            double lat = point[0];
            double lng = point[1];

            for (GeoZone zone : crimeZones) {
                if (zone.contains(lat, lng)) {
                    score -= 10;
                    break;
                }
            }

            for (GeoZone zone : lightingZones) {
                if (zone.contains(lat, lng)) {
                    score -= 5;
                    break;
                }
            }
        }

        return Math.max(score, 0);
    }
}
