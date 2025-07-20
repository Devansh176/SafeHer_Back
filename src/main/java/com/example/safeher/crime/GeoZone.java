package com.example.safeher.crime;

public class GeoZone {
    private double lat;
    private double lng;
    private double radiusInMeters;

    public GeoZone(double lat, double lng, double radiusInMeters) {
        this.lat = lat;
        this.lng = lng;
        this.radiusInMeters = radiusInMeters;
    }

    public boolean contains(double checkLat, double checkLng) {
        double earthRadius = 6371000; // meters
        double dLat = Math.toRadians(checkLat - lat);
        double dLng = Math.toRadians(checkLng - lng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(checkLat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        return distance <= radiusInMeters;
    }
}
