package com.transit.datastructures;

import com.transit.model.Edge;
import com.transit.model.Station;

/**
 * Custom Adjacency List Graph built from scratch without java.util collections.
 * Models transit stations as vertices and weighted transit links as adjacency edges.
 */
public class GraphAdjacencyList {

    private static class EdgeNode {
        Edge edge;
        EdgeNode next;

        EdgeNode(Edge edge) {
            this.edge = edge;
            this.next = null;
        }
    }

    private static class StationNode {
        Station station;
        EdgeNode edgeHead;
        int edgeCount;
        StationNode next;

        StationNode(Station station) {
            this.station = station;
            this.edgeHead = null;
            this.edgeCount = 0;
            this.next = null;
        }
    }

    private StationNode head;
    private int vertexCount;

    public GraphAdjacencyList() {
        this.head = null;
        this.vertexCount = 0;
    }

    /**
     * Adds a new station vertex to the graph if its stationId does not already exist.
     */
    public void addStation(Station station) {
        if (station == null || findStationNode(station.getStationId()) != null) {
            return;
        }
        StationNode newNode = new StationNode(station);
        if (head == null) {
            head = newNode;
        } else {
            StationNode curr = head;
            while (curr.next != null) {
                curr = curr.next;
            }
            curr.next = newNode;
        }
        vertexCount++;
    }

    /**
     * Adds a bidirectional transit connection between source and destination stations.
     */
    public void addConnection(String srcId, String destId, double mins, String line) {
        addDirectedConnection(srcId, destId, mins, line);
        addDirectedConnection(destId, srcId, mins, line);
    }

    /**
     * Adds a single directed transit edge from source station to target station.
     */
    public void addDirectedConnection(String srcId, String destId, double mins, String line) {
        StationNode srcNode = findStationNode(srcId);
        StationNode destNode = findStationNode(destId);
        if (srcNode == null || destNode == null) {
            return;
        }
        Edge edge = new Edge(srcNode.station, destNode.station, mins, line);
        EdgeNode newEdgeNode = new EdgeNode(edge);
        newEdgeNode.next = srcNode.edgeHead;
        srcNode.edgeHead = newEdgeNode;
        srcNode.edgeCount++;
    }

    /**
     * Finds and returns a Station object by its ID (case-insensitive), or null if not found.
     */
    public Station findStation(String stationId) {
        StationNode node = findStationNode(stationId);
        return (node != null) ? node.station : null;
    }

    private StationNode findStationNode(String stationId) {
        if (stationId == null) {
            return null;
        }
        StationNode curr = head;
        while (curr != null) {
            if (curr.station.getStationId().equalsIgnoreCase(stationId)) {
                return curr;
            }
            curr = curr.next;
        }
        return null;
    }

    /**
     * Blocks or unblocks all edges belonging to a specific transit line during disruptions.
     */
    public void setLineDisrupted(String line, boolean blocked) {
        if (line == null) {
            return;
        }
        StationNode sCurr = head;
        while (sCurr != null) {
            EdgeNode eCurr = sCurr.edgeHead;
            while (eCurr != null) {
                if (eCurr.edge.getLineName().equalsIgnoreCase(line)) {
                    eCurr.edge.setBlocked(blocked);
                }
                eCurr = eCurr.next;
            }
            sCurr = sCurr.next;
        }
    }

    /**
     * Blocks or unblocks a specific edge between two stations on a given line.
     */
    public void setSegmentDisrupted(String srcId, String destId, String line, boolean blocked) {
        StationNode sCurr = head;
        while (sCurr != null) {
            EdgeNode eCurr = sCurr.edgeHead;
            while (eCurr != null) {
                Edge e = eCurr.edge;
                boolean matchesLine = (line == null || e.getLineName().equalsIgnoreCase(line));
                boolean forwardMatch = e.getSourceStation().getStationId().equalsIgnoreCase(srcId)
                        && e.getTargetStation().getStationId().equalsIgnoreCase(destId);
                boolean reverseMatch = e.getSourceStation().getStationId().equalsIgnoreCase(destId)
                        && e.getTargetStation().getStationId().equalsIgnoreCase(srcId);
                if (matchesLine && (forwardMatch || reverseMatch)) {
                    e.setBlocked(blocked);
                }
                eCurr = eCurr.next;
            }
            sCurr = sCurr.next;
        }
    }

    /**
     * Returns all stations currently registered in the graph as a fixed array.
     */
    public Station[] getAllStations() {
        Station[] stations = new Station[vertexCount];
        StationNode curr = head;
        int idx = 0;
        while (curr != null && idx < vertexCount) {
            stations[idx++] = curr.station;
            curr = curr.next;
        }
        return stations;
    }

    /**
     * Returns all outgoing edges from a given station ID as an array.
     */
    public Edge[] getEdgesFrom(String stationId) {
        StationNode node = findStationNode(stationId);
        if (node == null) {
            return new Edge[0];
        }
        Edge[] edges = new Edge[node.edgeCount];
        EdgeNode curr = node.edgeHead;
        int idx = 0;
        while (curr != null && idx < node.edgeCount) {
            edges[idx++] = curr.edge;
            curr = curr.next;
        }
        return edges;
    }

    /**
     * Returns the zero-based internal index of a station in the vertex list, or -1 if absent.
     */
    public int getStationIndex(String stationId) {
        if (stationId == null) {
            return -1;
        }
        StationNode curr = head;
        int idx = 0;
        while (curr != null) {
            if (curr.station.getStationId().equalsIgnoreCase(stationId)) {
                return idx;
            }
            curr = curr.next;
            idx++;
        }
        return -1;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
