package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        HashRing hashRing = new HashRing();
        hashRing.setupStorageNodes();
        hashRing.upload("Apple", "25");
        hashRing.upload("Banana", "36");
        hashRing.upload("Mango", "28");
        hashRing.upload("Pineapple", "67");
        hashRing.upload("India", "Delhi");
        hashRing.upload("USA", "DC");
        hashRing.upload("Canada", "Toronto");
        hashRing.upload("England", "London");
        hashRing.upload("China", "Beijing");
        hashRing.upload("Srilanka", "Colombo");
        hashRing.upload("Australia", "Canberra");
        hashRing.upload("Thailand", "Phuket");
        hashRing.upload("Singapore", "Core");
        hashRing.upload("Japan", "Tokyo");
        hashRing.fetch("Banana");
        System.out.println(hashRing.getRingSetup());
        System.out.println(hashRing.tokenList);
        hashRing.addNode("10.50.44.56");
        System.out.println(hashRing.getRingSetup());
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
        hashRing.removeNode("10.50.44.56");
    }
}