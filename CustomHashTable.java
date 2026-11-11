package com.transit.datastructures;

import com.transit.model.Commuter;

public class CustomHashTable {
    private static class Entry {
        String key;
        Commuter value;
        Entry next;

        Entry(String key, Commuter value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Entry[] buckets;
    private int capacity;

    public CustomHashTable(int capacity) {
        this.capacity = capacity;
        this.buckets = new Entry[capacity];
    }

    private int getBucketIndex(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    public void put(String key, Commuter value) {
        int index = getBucketIndex(key);
        Entry head = buckets[index];
        while (head != null) {
            if (head.key.equals(key)) {
                head.value = value;
                return;
            }
            head = head.next;
        }
        Entry newNode = new Entry(key, value);
        newNode.next = buckets[index];
        buckets[index] = newNode;
    }

    public Commuter get(String key) {
        int index = getBucketIndex(key);
        Entry head = buckets[index];
        while (head != null) {
            if (head.key.equals(key)) {
                return head.value;
            }
            head = head.next;
        }
        return null;
    }
}