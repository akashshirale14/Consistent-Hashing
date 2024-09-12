package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        HashRing hashRing = new HashRing();
        hashRing.setupStorageNodes();
        hashRing.upload("Apple", "25");
        hashRing.upload("Banana", "36");
        hashRing.fetch("Banana");
    }
}