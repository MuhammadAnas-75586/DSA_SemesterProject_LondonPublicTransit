package com.transit.model;

/**
 * Represents a weighted transit link (rail or bus segment) between two stations
 * in the urban transit network. Supports dynamic disruption/blockage toggling.
 */
public class Edge {
    private Station sourceStation;
    private Station targetStation;
    private double travelTimeMinutes;
    private String lineName;
    private boolean isBlocked;

    public Edge(Station sourceStation, Station targetStation, double travelTimeMinutes, String lineName) {
        this.sourceStation = sourceStation;
        this.targetStation = targetStation;
        this.travelTimeMinutes = travelTimeMinutes;
        this.lineName = lineName;
        this.isBlocked = false;
    }

    public Station getSourceStation() {
        return sourceStation;
    }

    public Station getTargetStation() {
        return targetStation;
    }

    public double getTravelTimeMinutes() {
        return travelTimeMinutes;
    }

    public void setTravelTimeMinutes(double travelTimeMinutes) {
        this.travelTimeMinutes = travelTimeMinutes;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
    }

    @Override
    public String toString() {
        String status = isBlocked ? " [DISRUPTED/BLOCKED]" : "";
        return lineName + ": " + sourceStation.getName() + " -> " + targetStation.getName()
                + " (" + travelTimeMinutes + " mins)" + status;
    }
}
