package com.transit.algorithms;

import com.transit.datastructures.CustomDequeue;
import com.transit.datastructures.CustomStack;
import com.transit.datastructures.GraphAdjacencyList;
import com.transit.model.Edge;
import com.transit.model.Station;

/**
 * Graph Traversal and Network Diagnostics Engine providing Breadth-First Search (BFS)
 * and Depth-First Search (DFS) using CustomDequeue and CustomStack (no java.util).
 */
public class GraphTraversal {

    /**
     * Performs Breadth-First Search (BFS) from startId over active (unblocked) edges,
     * returning reachable stations in breadth-first discovery order.
     */
    public Station[] performBFS(GraphAdjacencyList g, String startId) {
        if (g == null || startId == null) {
            return new Station[0];
        }

        Station[] stations = g.getAllStations();
        int n = stations.length;
        int startIdx = g.getStationIndex(startId);
        if (startIdx == -1) {
            return new Station[0];
        }

        boolean[] visited = new boolean[n];
        Station[] orderBuffer = new Station[n];
        int count = 0;

        CustomDequeue<Integer> queue = new CustomDequeue<>();
        visited[startIdx] = true;
        queue.addLast(startIdx);

        while (!queue.isEmpty()) {
            int u = queue.removeFirst();
            orderBuffer[count++] = stations[u];

            Edge[] edges = g.getEdgesFrom(stations[u].getStationId());
            for (int i = 0; i < edges.length; i++) {
                if (edges[i].isBlocked()) {
                    continue;
                }
                int v = g.getStationIndex(edges[i].getTargetStation().getStationId());
                if (v != -1 && !visited[v]) {
                    visited[v] = true;
                    queue.addLast(v);
                }
            }
        }

        Station[] result = new Station[count];
        for (int i = 0; i < count; i++) {
            result[i] = orderBuffer[i];
        }
        return result;
    }

    /**
     * Uses BFS to verify whether targetId is reachable from startId across active lines.
     */
    public boolean checkReachability(GraphAdjacencyList g, String startId, String targetId) {
        if (g == null || startId == null || targetId == null) {
            return false;
        }
        Station[] stations = g.getAllStations();
        int n = stations.length;
        int startIdx = g.getStationIndex(startId);
        int targetIdx = g.getStationIndex(targetId);

        if (startIdx == -1 || targetIdx == -1) {
            return false;
        }
        if (startIdx == targetIdx) {
            return true;
        }

        boolean[] visited = new boolean[n];
        CustomDequeue<Integer> queue = new CustomDequeue<>();

        visited[startIdx] = true;
        queue.addLast(startIdx);

        while (!queue.isEmpty()) {
            int u = queue.removeFirst();
            if (u == targetIdx) {
                return true;
            }
            Edge[] edges = g.getEdgesFrom(stations[u].getStationId());
            for (int i = 0; i < edges.length; i++) {
                if (edges[i].isBlocked()) {
                    continue;
                }
                int v = g.getStationIndex(edges[i].getTargetStation().getStationId());
                if (v != -1 && !visited[v]) {
                    visited[v] = true;
                    queue.addLast(v);
                }
            }
        }
        return false;
    }

    /**
     * Uses BFS to find the path with the fewest station hops/transfers (unweighted shortest path).
     */
    public Station[] findMinimumHopPathBFS(GraphAdjacencyList g, String startId, String targetId) {
        if (g == null || startId == null || targetId == null) {
            return new Station[0];
        }

        Station[] stations = g.getAllStations();
        int n = stations.length;
        int startIdx = g.getStationIndex(startId);
        int targetIdx = g.getStationIndex(targetId);

        if (startIdx == -1 || targetIdx == -1) {
            return new Station[0];
        }

        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = -1;
        }

        CustomDequeue<Integer> queue = new CustomDequeue<>();
        visited[startIdx] = true;
        queue.addLast(startIdx);

        boolean found = false;
        while (!queue.isEmpty()) {
            int u = queue.removeFirst();
            if (u == targetIdx) {
                found = true;
                break;
            }
            Edge[] edges = g.getEdgesFrom(stations[u].getStationId());
            for (int i = 0; i < edges.length; i++) {
                if (edges[i].isBlocked()) {
                    continue;
                }
                int v = g.getStationIndex(edges[i].getTargetStation().getStationId());
                if (v != -1 && !visited[v]) {
                    visited[v] = true;
                    parent[v] = u;
                    queue.addLast(v);
                }
            }
        }

        if (!found) {
            return new Station[0];
        }

        CustomStack<Station> reverseStack = new CustomStack<>();
        int curr = targetIdx;
        while (curr != -1) {
            reverseStack.push(stations[curr]);
            curr = parent[curr];
        }

        Station[] path = new Station[reverseStack.size()];
        int idx = 0;
        while (!reverseStack.isEmpty()) {
            path[idx++] = reverseStack.pop();
        }
        return path;
    }

    /**
     * Performs Depth-First Search (DFS) from startId over active edges using CustomStack.
     */
    public Station[] performDFS(GraphAdjacencyList g, String startId) {
        if (g == null || startId == null) {
            return new Station[0];
        }

        Station[] stations = g.getAllStations();
        int n = stations.length;
        int startIdx = g.getStationIndex(startId);
        if (startIdx == -1) {
            return new Station[0];
        }

        boolean[] visited = new boolean[n];
        Station[] orderBuffer = new Station[n];
        int count = 0;

        CustomStack<Integer> stack = new CustomStack<>();
        stack.push(startIdx);

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (!visited[u]) {
                visited[u] = true;
                orderBuffer[count++] = stations[u];

                Edge[] edges = g.getEdgesFrom(stations[u].getStationId());
                for (int i = edges.length - 1; i >= 0; i--) {
                    if (edges[i].isBlocked()) {
                        continue;
                    }
                    int v = g.getStationIndex(edges[i].getTargetStation().getStationId());
                    if (v != -1 && !visited[v]) {
                        stack.push(v);
                    }
                }
            }
        }

        Station[] result = new Station[count];
        for (int i = 0; i < count; i++) {
            result[i] = orderBuffer[i];
        }
        return result;
    }

    /**
     * Performs DFS cycle detection across active transit network corridors.
     */
    public boolean detectCycles(GraphAdjacencyList g) {
        if (g == null) {
            return false;
        }
        Station[] stations = g.getAllStations();
        int n = stations.length;
        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (dfsCycleHelper(g, stations, i, -1, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfsCycleHelper(GraphAdjacencyList g, Station[] stations, int u, int parent, boolean[] visited) {
        visited[u] = true;
        Edge[] edges = g.getEdgesFrom(stations[u].getStationId());
        for (int i = 0; i < edges.length; i++) {
            if (edges[i].isBlocked()) {
                continue;
            }
            int v = g.getStationIndex(edges[i].getTargetStation().getStationId());
            if (v == -1) {
                continue;
            }
            if (!visited[v]) {
                if (dfsCycleHelper(g, stations, v, u, visited)) {
                    return true;
                }
            } else if (v != parent) {
                return true;
            }
        }
        return false;
    }

    /**
     * Identifies stations cut off (unreachable via DFS) from referenceStationId
     * when one or more lines are disrupted.
     */
    public Station[] findIsolatedStations(GraphAdjacencyList g, String referenceStationId) {
        if (g == null || referenceStationId == null) {
            return new Station[0];
        }
        Station[] stations = g.getAllStations();
        int n = stations.length;
        int refIdx = g.getStationIndex(referenceStationId);
        if (refIdx == -1) {
            return stations;
        }

        boolean[] reachable = new boolean[n];
        CustomStack<Integer> stack = new CustomStack<>();
        stack.push(refIdx);

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (!reachable[u]) {
                reachable[u] = true;
                Edge[] edges = g.getEdgesFrom(stations[u].getStationId());
                for (int i = 0; i < edges.length; i++) {
                    if (edges[i].isBlocked()) {
                        continue;
                    }
                    int v = g.getStationIndex(edges[i].getTargetStation().getStationId());
                    if (v != -1 && !reachable[v]) {
                        stack.push(v);
                    }
                }
            }
        }

        int isolatedCount = 0;
        for (int i = 0; i < n; i++) {
            if (!reachable[i]) {
                isolatedCount++;
            }
        }

        Station[] isolated = new Station[isolatedCount];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            if (!reachable[i]) {
                isolated[idx++] = stations[i];
            }
        }
        return isolated;
    }
}