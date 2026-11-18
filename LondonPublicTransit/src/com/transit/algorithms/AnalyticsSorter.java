package com.transit.algorithms;

/**
 * Operational Analytics Sorting Engine implementing MergeSort (O(n log n) guaranteed)
 * for station passenger volume ranking and QuickSort for congestion index ranking.
 */
public class AnalyticsSorter {

    /**
     * Data model representing daily operational metrics for a transit station.
     */
    public static class StationTrafficMetric {
        private String stationId;
        private String stationName;
        private int dailyPassengerVolume;
        private double congestionScore; // 0.0 to 100.0 percentage

        public StationTrafficMetric(String stationId, String stationName, int dailyPassengerVolume, double congestionScore) {
            this.stationId = stationId;
            this.stationName = stationName;
            this.dailyPassengerVolume = dailyPassengerVolume;
            this.congestionScore = congestionScore;
        }

        public String getStationId() { return stationId; }
        public String getStationName() { return stationName; }
        public int getDailyPassengerVolume() { return dailyPassengerVolume; }
        public double getCongestionScore() { return congestionScore; }

        @Override
        public String toString() {
            return String.format("%s (%s) | Passengers: %,d | Congestion: %.1f%%",
                    stationName, stationId, dailyPassengerVolume, congestionScore);
        }
    }

    /**
     * Sorts StationTrafficMetric array in descending order of dailyPassengerVolume
     * using MergeSort with guaranteed O(n log n) time complexity.
     */
    public void mergeSort(StationTrafficMetric[] arr, int left, int right) {
        if (arr == null || left >= right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private void merge(StationTrafficMetric[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        StationTrafficMetric[] leftArr = new StationTrafficMetric[n1];
        StationTrafficMetric[] rightArr = new StationTrafficMetric[n2];

        for (int i = 0; i < n1; i++) {
            leftArr[i] = arr[left + i];
        }
        for (int j = 0; j < n2; j++) {
            rightArr[j] = arr[mid + 1 + j];
        }

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            // Descending order: busiest stations first
            if (leftArr[i].getDailyPassengerVolume() >= rightArr[j].getDailyPassengerVolume()) {
                arr[k++] = leftArr[i++];
            } else {
                arr[k++] = rightArr[j++];
            }
        }

        while (i < n1) {
            arr[k++] = leftArr[i++];
        }
        while (j < n2) {
            arr[k++] = rightArr[j++];
        }
    }

    /**
     * Sorts StationTrafficMetric array in descending order of congestionScore
     * using in-place QuickSort (O(n log n) average time complexity).
     */
    public void quickSortByCongestion(StationTrafficMetric[] arr, int low, int high) {
        if (arr == null || low >= high) {
            return;
        }
        int pivotIndex = partition(arr, low, high);
        quickSortByCongestion(arr, low, pivotIndex - 1);
        quickSortByCongestion(arr, pivotIndex + 1, high);
    }

    private int partition(StationTrafficMetric[] arr, int low, int high) {
        double pivot = arr[high].getCongestionScore();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            // Descending order: highest congestion first
            if (arr[j].getCongestionScore() >= pivot) {
                i++;
                StationTrafficMetric temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        StationTrafficMetric temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }
}
