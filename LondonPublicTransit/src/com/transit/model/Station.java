/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.transit.model;

/**
 *
 * @author M. Talha Attari
 */
public class Station {
    private String stationId;
    private String name;
    private int fareZone;

    public Station(String stationId, String name, int fareZone) {
        this.stationId = stationId;
        this.name = name;
        this.fareZone = fareZone;
    }

    public String getStationId() { return stationId; }
    public String getName() { return name; }
    public int getFareZone() { return fareZone; }
}
