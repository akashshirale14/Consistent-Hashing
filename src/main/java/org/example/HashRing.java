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
    Map<BigInteger, StorageNode> tokenHashMap;

    public HashRing() {
        this.nodeList = new ArrayList<>();
        this.tokenList = new ArrayList<>();
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException na) {
            System.out.println("Invalid Algorithm for hashing" + na);
        }
        tokenHashMap = new HashMap<>();
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
        tokenList.clear();
        tokenHashMap.clear();;
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
        if (index == -1) {
            index = 0;
        }
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

    public List<String> getRingSetup() {
        List<String> ans  = new ArrayList<>();
        for (StorageNode curr : nodeList) {
            ans.add(curr.getIp());
        }
        return ans;
    }

    public void addNode(String ip) {
        System.out.println("Adding new node...");
        StorageNode newNode = new StorageNode(ip);
        nodeList.add(newNode);
        setupTokenList();
        int newIndex = tokenList.indexOf(newNode.calculateHash());
        int nextNodeIndex = newIndex + 1;
        if (newIndex == tokenList.size()-1) {
            nextNodeIndex = 0;
        }
        int prevNodeIndex = newIndex - 1;
        if (newIndex == 0) {
            prevNodeIndex = tokenList.size() - 1;
        }
        transferData(tokenList.get(prevNodeIndex),tokenList.get(newIndex), nodeList.get(nextNodeIndex), nodeList.get(newIndex));
    }

    private void transferData(BigInteger start, BigInteger end, StorageNode oldNode, StorageNode newNode) {
        System.out.println("Transferring data from old node: " + oldNode.getIp() + " to new node: " + newNode.getIp());
        Map<String, String> oldNodeData = oldNode.getAllData();
        Map<String, String> transferredData = new HashMap<>();
        for(Map.Entry<String, String> entry : oldNodeData.entrySet()) {
            String key = entry.getKey();
            BigInteger keyHash = calculateHash(key);
            if (keyHash.compareTo(start) > 0 && keyHash.compareTo(end)<=0) {
                System.out.println("Moving key " + key + " to new node");
                newNode.addData(key,entry.getValue());
                transferredData.put(key, entry.getValue());
            }
        }
        System.out.println("Printing currData...");
        oldNode.printAllData();
        oldNode.removeTransferredData(transferredData);
        System.out.println("Printing newData...");
        oldNode.printAllData();
        System.out.println("***********************************************");
        newNode.printAllData();
    }

    private void transferDataAfterRemoval(StorageNode leavingNode, StorageNode receivingNode) {
        Map<String, String> currData = leavingNode.getAllData();
        for (Map.Entry<String, String> entry : currData.entrySet()) {
            System.out.println("Transferring Data: Key: " + entry.getKey() + " : " + entry.getValue());
            receivingNode.addData(entry.getKey(),entry.getValue());
        }
    }

    public void removeNode(String ip) {
        BigInteger ipHash = calculateHash(ip);
        int currIndex = tokenList.indexOf(ipHash);
        if (currIndex == -1 ) {
            System.out.println("Node doesnt exist in the ring");
            return;
        }
        int nextIndex = currIndex + 1;
        if (currIndex == tokenList.size() - 1) {
            nextIndex = 0;
        }
        StorageNode leavingNode = nodeList.get(currIndex);
        leavingNode.printAllData();
        StorageNode receivingNode = nodeList.get(nextIndex);
        receivingNode.printAllData();
        transferDataAfterRemoval(leavingNode,receivingNode);
        receivingNode.printAllData();
    }


}
