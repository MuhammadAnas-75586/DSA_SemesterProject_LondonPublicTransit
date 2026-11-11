package com.transit.model;

public class FareZone {
    private int zoneId;
    private String zoneName;
    private double baseFare;
    private double multiplier;

    public FareZone(int zoneId, String zoneName, double baseFare, double multiplier) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.baseFare = baseFare;
        this.multiplier = multiplier;
    }

    public int getZoneId() { return zoneId; }
    public String getZoneName() { return zoneName; }
    public double getBaseFare() { return baseFare; }
    public double getMultiplier() { return multiplier; }

    public double calculateFare(int distanceKm) {
        return baseFare + (distanceKm * multiplier);
    }

    @Override
    public String toString() {
        return String.format("Zone %d: %s | Base: $%.2f | Rate: $%.2f/km", 
                zoneId, zoneName, baseFare, multiplier);
    }
}