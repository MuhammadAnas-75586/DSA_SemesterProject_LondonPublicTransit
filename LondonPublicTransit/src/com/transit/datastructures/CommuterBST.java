/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.transit.datastructures;

/**
 *
 * @author M. Talha Attari
 */

import com.transit.model.Commuter;

public class CommuterBST {
    private class Node {
        String passId;
        Commuter commuter;
        Node left, right;

        Node(String passId, Commuter commuter) {
            this.passId = passId;
            this.commuter = commuter;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;

    public void insert(String passId, Commuter commuter) {
        root = insertRecursive(root, passId, commuter);
    }

    private Node insertRecursive(Node current, String passId, Commuter commuter) {
        if (current == null) {
            return new Node(passId, commuter);
        }

        int comparison = passId.compareTo(current.passId);
        if (comparison < 0) {
            current.left = insertRecursive(current.left, passId, commuter);
        } else if (comparison > 0) {
            current.right = insertRecursive(current.right, passId, commuter);
        } else {
            current.commuter = commuter; // Update value if key already exists
        }
        return current;
    }

    public Commuter search(String passId) {
        return searchRecursive(root, passId);
    }

    private Commuter searchRecursive(Node current, String passId) {
        if (current == null) {
            return null;
        }

        int comparison = passId.compareTo(current.passId);
        if (comparison == 0) {
            return current.commuter;
        }
        return comparison < 0 
            ? searchRecursive(current.left, passId) 
            : searchRecursive(current.right, passId);
    }
}