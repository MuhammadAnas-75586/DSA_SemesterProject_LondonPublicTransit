package com.transit.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Encapsulates a track, signal, or platform safety emergency event
 * with an urgency severity ranking (1 = Minor, 10 = Critical Emergency).
 */
public class HazardIncident {
    private String incidentId;
    private String stationId;
    private String lineName;
    private String description;
    private int severityRank; // 1 to 10 (10 = highest urgency)
    private String timestamp;

    public HazardIncident(String incidentId, String stationId, String lineName, String description, int severityRank) {
        this.incidentId = incidentId;
        this.stationId = stationId;
        this.lineName = lineName;
        this.description = description;
        if (severityRank < 1) {
            this.severityRank = 1;
        } else if (severityRank > 10) {
            this.severityRank = 10;
        } else {
            this.severityRank = severityRank;
        }
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public String getIncidentId() {
        return incidentId;
    }

    public String getStationId() {
        return stationId;
    }

    public String getLineName() {
        return lineName;
    }

    public String getDescription() {
        return description;
    }

    public int getSeverityRank() {
        return severityRank;
    }

    public void setSeverityRank(int severityRank) {
        this.severityRank = Math.max(1, Math.min(10, severityRank));
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[SEVERITY " + severityRank + "/10] (" + incidentId + ") "
                + lineName + " @ " + stationId + ": " + description + " [" + timestamp + "]";
    }
}
