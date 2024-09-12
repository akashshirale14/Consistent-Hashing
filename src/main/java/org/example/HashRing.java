package org.example;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class HashRing {
    List<StorageNode> nodeList;
    List<BigInteger> tokenList;
    private MessageDigest digest;
    public HashRing() {
        this.nodeList = new ArrayList<>();
        this.tokenList = new ArrayList<>();
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException na) {
            System.out.println("Invalid Algorithm for hashing" + na);
        }
    }

    public void setupStorageNodes() {
        StorageNode nodeA = new StorageNode("10.50.44.51");
        StorageNode nodeB = new StorageNode("10.50.44.52");
        StorageNode nodeC = new StorageNode("10.50.44.53");
        StorageNode nodeD = new StorageNode("10.50.44.54");
        StorageNode nodeE = new StorageNode("10.50.44.55");
        nodeList.add(nodeA);
        nodeList.add(nodeB);
        nodeList.add(nodeC);
        nodeList.add(nodeD);
        nodeList.add(nodeE);
        setupTokenList();
    }

    public void setupTokenList(){
        Map<BigInteger, StorageNode> tokenHashMap = new HashMap<>();
        for (StorageNode currNode : nodeList) {
            BigInteger hashValue = currNode.getHash();
            tokenList.add(hashValue);
            tokenHashMap.put(hashValue, currNode);
        }
        Collections.sort(tokenList);
        nodeList.clear();
        for (BigInteger currToken : tokenList) {
            nodeList.add(tokenHashMap.get(currToken));
        }
    }

    public void upload(String key, String  value) {
        int index = hash_function(key);
        StorageNode currNode = nodeList.get(index);
        System.out.println("Key : " + key + " At : " +currNode.getIp());
        currNode.addData(key, value);
    }

    public void fetch(String key) {
        int index = hash_function(key);
        StorageNode currNode = nodeList.get(index);
        System.out.println("Value of Key: " + key + " is " + currNode.getData(key));
    }

    private int hash_function(String key) {
        BigInteger keyHash = calculateHash(key);
        int index = getNodeIndex(keyHash);
        return index;
    }



    public BigInteger calculateHash(String key) {
        byte[] input = digest.digest(key.getBytes(StandardCharsets.UTF_8));
        BigInteger hashValue = new BigInteger(1, input);
        return hashValue.mod(new BigInteger(String.valueOf((long)Math.pow(2,256))));
    }

    private int getNodeIndex(BigInteger keyHash) {
        int start = 0;
        int end = tokenList.size() - 1;
        int index = -1;
        while(start<=end) {
            int mid = (start + end)/2;
            if (mid < 0 || mid >= tokenList.size()) {
                return 0;
            }
            if (keyHash == tokenList.get(mid)) {
               return mid;
            }
            if (keyHash.compareTo(tokenList.get(mid)) > 0) {
                start = mid + 1;
            } else {
                index = mid;
                end = mid - 1;
            }
        }
        return index;
    }


}
