package com.transit.model;

import com.transit.datastructures.DoublyLinkedList;

public class Commuter {
    private String passId;
    private String name;
    private double balance;
    private DoublyLinkedList<String> travelLog;

    public Commuter(String passId, String name, double balance) {
        this.passId = passId;
        this.name = name;
        this.balance = balance;
        this.travelLog = new DoublyLinkedList<>();
    }

    public String getPassId() { return passId; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
    public DoublyLinkedList<String> getTravelLog() { return travelLog; }

    public void logTravel(String step) {
        this.travelLog.insertTail(step);
    }
}