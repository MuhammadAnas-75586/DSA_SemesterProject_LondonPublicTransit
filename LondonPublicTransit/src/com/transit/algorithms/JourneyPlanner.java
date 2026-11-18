package com.transit.algorithms;

import com.transit.datastructures.CustomStack;
import com.transit.datastructures.GraphAdjacencyList;
import com.transit.model.Edge;
import com.transit.model.Station;

/**
 * Shortest-Path Routing Engine implementing Dijkstra's Algorithm paired with a
 * custom Min-Heap priority queue over GraphAdjacencyList. Dynamically avoids
 * blocked/disrupted segments and computes line transfers and estimated fares.
 */
public class JourneyPlanner {

    /**
     * Represents a single leg/segment of a planned multi-modal journey.
     */
    public static class Step {
        private Station fromStation;
        private Station toStation;
        private String lineName;
        private double segmentMinutes;
        private boolean isTransfer;

        public Step(Station fromStation, Station toStation, String lineName, double segmentMinutes, boolean isTransfer) {
            this.fromStation = fromStation;
            this.toStation = toStation;
            this.lineName = lineName;
            this.segmentMinutes = segmentMinutes;
            this.isTransfer = isTransfer;
        }

        public Station getFromStation() { return fromStation; }
        public Station getToStation() { return toStation; }
        public String getLineName() { return lineName; }
        public double getSegmentMinutes() { return segmentMinutes; }
        public boolean isTransfer() { return isTransfer; }

        @Override
        public String toString() {
            String transferNote = isTransfer ? " [TRANSFER]" : "";
            return fromStation.getName() + " -> " + toStation.getName()
                    + " via " + lineName + " (" + segmentMinutes + " min)" + transferNote;
        }
    }

    /**
     * Encapsulates the complete outcome of a Dijkstra route computation.
     */
    public static class PathResult {
        private boolean reachable;
        private double totalMinutes;
        private int transferCount;
        private double estimatedFare;
        private Step[] steps;
        private String summaryMessage;

        public PathResult(boolean reachable, double totalMinutes, int transferCount,
                          double estimatedFare, Step[] steps, String summaryMessage) {
            this.reachable = reachable;
            this.totalMinutes = totalMinutes;
            this.transferCount = transferCount;
            this.estimatedFare = estimatedFare;
            this.steps = (steps != null) ? steps : new Step[0];
            this.summaryMessage = summaryMessage;
        }

        public boolean isReachable() { return reachable; }
        public double getTotalMinutes() { return totalMinutes; }
        public int getTransferCount() { return transferCount; }
        public double getEstimatedFare() { return estimatedFare; }
        public Step[] getSteps() { return steps; }
        public String getSummaryMessage() { return summaryMessage; }
    }

    /**
     * Internal custom Min-Heap for Dijkstra vertex relaxation (no java.util.PriorityQueue).
     */
    private static class DijkstraMinHeap {
        private int[] vertexIndices;
        private double[] priorities;
        private int size;

        DijkstraMinHeap(int capacity) {
            int cap = Math.max(16, capacity * 4);
            this.vertexIndices = new int[cap];
            this.priorities = new double[cap];
            this.size = 0;
        }

        void push(int vertexIdx, double dist) {
            if (size == vertexIndices.length) {
                int newCap = vertexIndices.length * 2;
                int[] newV = new int[newCap];
                double[] newP = new double[newCap];
                for (int i = 0; i < size; i++) {
                    newV[i] = vertexIndices[i];
                    newP[i] = priorities[i];
                }
                vertexIndices = newV;
                priorities = newP;
            }
            vertexIndices[size] = vertexIdx;
            priorities[size] = dist;
            int curr = size++;
            while (curr > 0) {
                int parent = (curr - 1) / 2;
                if (priorities[curr] < priorities[parent]) {
                    swap(curr, parent);
                    curr = parent;
                } else {
                    break;
                }
            }
        }

        int popMinVertex() {
            int minVertex = vertexIndices[0];
            size--;
            if (size > 0) {
                vertexIndices[0] = vertexIndices[size];
                priorities[0] = priorities[size];
                int curr = 0;
                while (true) {
                    int left = 2 * curr + 1;
                    int right = 2 * curr + 2;
                    int smallest = curr;
                    if (left < size && priorities[left] < priorities[smallest]) {
                        smallest = left;
                    }
                    if (right < size && priorities[right] < priorities[smallest]) {
                        smallest = right;
                    }
                    if (smallest != curr) {
                        swap(curr, smallest);
                        curr = smallest;
                    } else {
                        break;
                    }
                }
            }
            return minVertex;
        }

        boolean isEmpty() {
            return size == 0;
        }

        private void swap(int i, int j) {
            int tv = vertexIndices[i];
            vertexIndices[i] = vertexIndices[j];
            vertexIndices[j] = tv;

            double tp = priorities[i];
            priorities[i] = priorities[j];
            priorities[j] = tp;
        }
    }

    /**
     * Computes the shortest travel-time route between start and dest station IDs
     * using Dijkstra's algorithm, ignoring blocked/disrupted transit edges.
     */
    public PathResult computeShortestPath(GraphAdjacencyList g, String start, String dest) {
        Step[] emptySteps = new Step[0];
        if (g == null || start == null || dest == null) {
            return new PathResult(false, 0.0, 0, 0.0, emptySteps, "Invalid graph or station input.");
        }

        Station[] stations = g.getAllStations();
        int n = stations.length;
        int startIdx = g.getStationIndex(start);
        int destIdx = g.getStationIndex(dest);

        if (startIdx == -1 || destIdx == -1) {
            return new PathResult(false, 0.0, 0, 0.0, emptySteps, "Start or destination station not found.");
        }

        if (startIdx == destIdx) {
            return new PathResult(true, 0.0, 0, 1.75, emptySteps,
                    "Origin and destination are the same station: " + stations[startIdx].getName());
        }

        double[] dist = new double[n];
        int[] prevVertex = new int[n];
        Edge[] prevEdge = new Edge[n];
        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            dist[i] = Double.POSITIVE_INFINITY;
            prevVertex[i] = -1;
            prevEdge[i] = null;
            visited[i] = false;
        }

        dist[startIdx] = 0.0;
        DijkstraMinHeap pq = new DijkstraMinHeap(n);
        pq.push(startIdx, 0.0);

        while (!pq.isEmpty()) {
            int u = pq.popMinVertex();
            if (visited[u]) {
                continue;
            }
            visited[u] = true;

            if (u == destIdx) {
                break;
            }

            Edge[] outgoing = g.getEdgesFrom(stations[u].getStationId());
            for (int i = 0; i < outgoing.length; i++) {
                Edge edge = outgoing[i];
                if (edge.isBlocked()) {
                    continue; // Skip disrupted/closed lines
                }
                int v = g.getStationIndex(edge.getTargetStation().getStationId());
                if (v == -1 || visited[v]) {
                    continue;
                }

                // Small transfer penalty (2.0 mins) when switching lines
                double transferPenalty = 0.0;
                if (prevEdge[u] != null && !prevEdge[u].getLineName().equalsIgnoreCase(edge.getLineName())) {
                    transferPenalty = 2.0;
                }

                double candidateDist = dist[u] + edge.getTravelTimeMinutes() + transferPenalty;
                if (candidateDist < dist[v]) {
                    dist[v] = candidateDist;
                    prevVertex[v] = u;
                    prevEdge[v] = edge;
                    pq.push(v, candidateDist);
                }
            }
        }

        if (dist[destIdx] == Double.POSITIVE_INFINITY) {
            return new PathResult(false, 0.0, 0, 0.0, emptySteps,
                    "No active route found between " + stations[startIdx].getName()
                            + " and " + stations[destIdx].getName() + " due to line disruptions.");
        }

        // Reconstruct path using CustomStack
        CustomStack<Edge> edgeStack = new CustomStack<>();
        int curr = destIdx;
        while (curr != -1 && prevEdge[curr] != null) {
            edgeStack.push(prevEdge[curr]);
            curr = prevVertex[curr];
        }

        Step[] routeSteps = new Step[edgeStack.size()];
        String lastLine = null;
        int transfers = 0;
        int stepIdx = 0;

        while (!edgeStack.isEmpty()) {
            Edge e = edgeStack.pop();
            boolean isTransfer = (lastLine != null && !lastLine.equalsIgnoreCase(e.getLineName()));
            if (isTransfer) {
                transfers++;
            }
            lastLine = e.getLineName();
            routeSteps[stepIdx++] = new Step(
                    e.getSourceStation(),
                    e.getTargetStation(),
                    e.getLineName(),
                    e.getTravelTimeMinutes(),
                    isTransfer
            );
        }

        int zoneDiff = Math.abs(stations[startIdx].getFareZone() - stations[destIdx].getFareZone());
        double estimatedFare = 2.80 + (zoneDiff * 0.90);

        String summary = String.format("Optimal Route: %s -> %s | Total Time: %.1f mins | Transfers: %d | Est. Fare: £%.2f",
                stations[startIdx].getName(), stations[destIdx].getName(), dist[destIdx], transfers, estimatedFare);

        return new PathResult(true, dist[destIdx], transfers, estimatedFare, routeSteps, summary);
    }
}