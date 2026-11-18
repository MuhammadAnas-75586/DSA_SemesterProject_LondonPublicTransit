package com.transit.datastructures;

import com.transit.model.FareZone;

public class FareZoneBST {
    public static class Node {
        public int zoneId;
        public FareZone fareZone;
        public Node left;
        public Node right;

        public Node(FareZone fareZone) {
            this.zoneId = fareZone.getZoneId();
            this.fareZone = fareZone;
        }
    }

    private Node root;

    public void insert(FareZone fareZone) {
        root = insertRecursive(root, fareZone);
    }

    private Node insertRecursive(Node current, FareZone fareZone) {
        if (current == null) {
            return new Node(fareZone);
        }
        if (fareZone.getZoneId() < current.zoneId) {
            current.left = insertRecursive(current.left, fareZone);
        } else if (fareZone.getZoneId() > current.zoneId) {
            current.right = insertRecursive(current.right, fareZone);
        } else {
            current.fareZone = fareZone;
        }
        return current;
    }

    public FareZone search(int zoneId) {
        return searchRecursive(root, zoneId);
    }

    private FareZone searchRecursive(Node current, int zoneId) {
        if (current == null) {
            return null;
        }
        if (zoneId == current.zoneId) {
            return current.fareZone;
        }
        return zoneId < current.zoneId 
            ? searchRecursive(current.left, zoneId) 
            : searchRecursive(current.right, zoneId);
    }

    public String getInOrderTraversal() {
        StringBuilder sb = new StringBuilder();
        inOrder(root, sb);
        return sb.toString();
    }

    private void inOrder(Node node, StringBuilder sb) {
        if (node != null) {
            inOrder(node.left, sb);
            sb.append(node.fareZone.toString()).append("\n");
            inOrder(node.right, sb);
        }
    }
}