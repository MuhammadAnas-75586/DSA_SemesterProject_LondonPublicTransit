package com.transit.datastructures;

/**
 * Custom LIFO Stack built from scratch using linked nodes (no java.util collections).
 * Manages journey navigation state history and path reconstruction in O(1) time.
 */
public class CustomStack<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> top;
    private int count;

    public CustomStack() {
        this.top = null;
        this.count = 0;
    }

    /**
     * Pushes an element onto the top of the stack in O(1) time.
     */
    public void push(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = top;
        top = newNode;
        count++;
    }

    /**
     * Removes and returns the top element of the stack in O(1) time, or null if empty.
     */
    public T pop() {
        if (isEmpty()) {
            return null;
        }
        T item = top.data;
        top = top.next;
        count--;
        return item;
    }

    /**
     * Inspects the top element without removing it in O(1) time, or null if empty.
     */
    public T peek() {
        return isEmpty() ? null : top.data;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public void clear() {
        top = null;
        count = 0;
    }
}