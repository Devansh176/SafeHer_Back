package com.example.safeher.crime;

import java.util.List;

public class CrimeZoneData {
    public static List<GeoZone> getHighCrimeZones() {
        return List.of(
                new GeoZone(21.1234, 79.0700, 300),
                new GeoZone(21.1344, 79.0800, 300)
        );
    }
}
