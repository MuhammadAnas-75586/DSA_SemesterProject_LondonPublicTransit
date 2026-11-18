package com.transit.datastructures;

/**
 * Custom Double-Ended Queue (Dequeue) built from scratch using bidirectional linked nodes.
 * Buffers station turnstile and platform boarding lines, supporting FIFO standard
 * passenger entry (addLast / removeFirst) and LIFO emergency priority boarding (addFirst).
 */
public class CustomDequeue<T> {

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int count;

    public CustomDequeue() {
        this.head = null;
        this.tail = null;
        this.count = 0;
    }

    /**
     * Inserts a high-priority or emergency passenger at the front of the queue in O(1).
     */
    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        count++;
    }

    /**
     * Enqueues a standard commuter at the rear of the turnstile queue in O(1).
     */
    public void addLast(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        count++;
    }

    /**
     * Dispatches/admits the passenger at the front of the queue in O(1).
     */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T item = head.data;
        if (count == 1) {
            head = tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }
        count--;
        return item;
    }

    /**
     * Removes and returns the passenger at the rear of the queue in O(1).
     */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T item = tail.data;
        if (count == 1) {
            head = tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        count--;
        return item;
    }

    /**
     * Returns the front element without removing it in O(1).
     */
    public T peekFirst() {
        return isEmpty() ? null : head.data;
    }

    /**
     * Returns the rear element without removing it in O(1).
     */
    public T peekLast() {
        return isEmpty() ? null : tail.data;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public void clear() {
        head = null;
        tail = null;
        count = 0;
    }
}