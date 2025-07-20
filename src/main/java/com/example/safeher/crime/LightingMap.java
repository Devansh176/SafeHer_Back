package com.example.safeher.crime;

import java.util.List;

public class LightingMap {
    public static List<GeoZone> getPoorlyLitZones() {
        return List.of(
                new GeoZone(21.1288, 79.0755, 300),
                new GeoZone(21.1321, 79.0733, 300)
        );
    }
}
