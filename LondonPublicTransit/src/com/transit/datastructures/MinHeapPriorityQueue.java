package com.transit.datastructures;

import com.transit.model.HazardIncident;

/**
 * Custom Array-Backed Binary Min-Heap Priority Queue for Emergency Hazard Dispatch.
 * Maps severity (1..10) to min-heap urgency key (11 - severityRank) so critical
 * severity-10 emergencies carry priority key 1 and sit at the root for O(1) peek
 * and O(log n) extraction.
 */
public class MinHeapPriorityQueue {
    private HazardIncident[] heap;
    private int size;
    private int capacity;

    public MinHeapPriorityQueue() {
        this(32);
    }

    public MinHeapPriorityQueue(int initialCapacity) {
        this.capacity = Math.max(8, initialCapacity);
        this.heap = new HazardIncident[this.capacity];
        this.size = 0;
    }

    /**
     * Inserts a new HazardIncident and restores the min-heap invariant in O(log n).
     */
    public void insert(HazardIncident incident) {
        if (incident == null) {
            return;
        }
        if (size == capacity) {
            resize();
        }
        heap[size] = incident;
        siftUp(size);
        size++;
    }

    /**
     * Removes and returns the highest-urgency hazard incident in O(log n).
     */
    public HazardIncident extractHighestPriority() {
        if (size == 0) {
            return null;
        }
        HazardIncident top = heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0) {
            siftDown(0);
        }
        return top;
    }

    /**
     * Returns without removing the highest-urgency hazard incident in O(1).
     */
    public HazardIncident peek() {
        return (size == 0) ? null : heap[0];
    }

    /**
     * Returns a snapshot array of all queued incidents sorted in dispatch priority order.
     */
    public HazardIncident[] getAllIncidents() {
        HazardIncident[] snapshot = new HazardIncident[size];
        for (int i = 0; i < size; i++) {
            snapshot[i] = heap[i];
        }
        // Sort copy by urgency key ascending without modifying internal heap array
        for (int i = 1; i < snapshot.length; i++) {
            HazardIncident keyItem = snapshot[i];
            int j = i - 1;
            while (j >= 0 && urgencyKey(snapshot[j]) > urgencyKey(keyItem)) {
                snapshot[j + 1] = snapshot[j];
                j--;
            }
            snapshot[j + 1] = keyItem;
        }
        return snapshot;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Converts severity (1..10) into a min-heap priority key where smaller = higher urgency.
     */
    private int urgencyKey(HazardIncident incident) {
        return 11 - incident.getSeverityRank();
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (urgencyKey(heap[index]) < urgencyKey(heap[parent])) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;

            if (left < size && urgencyKey(heap[left]) < urgencyKey(heap[smallest])) {
                smallest = left;
            }
            if (right < size && urgencyKey(heap[right]) < urgencyKey(heap[smallest])) {
                smallest = right;
            }
            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        HazardIncident temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void resize() {
        capacity *= 2;
        HazardIncident[] expanded = new HazardIncident[capacity];
        for (int i = 0; i < size; i++) {
            expanded[i] = heap[i];
        }
        heap = expanded;
    }
}
