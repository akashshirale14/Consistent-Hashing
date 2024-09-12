package org.example;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class StorageNode {
    private String ip;
    private BigInteger hash;
    private Map<String, String> data;
    private MessageDigest digest;

    public StorageNode(String ip)  {
        this.ip = ip;
        data = new HashMap<>();
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException na) {
            System.out.println("Invalid Algorithm for hashing" + na);
        }
        this.hash = calculateHash();
    }

    public String getIp(){
        return ip;
    }

    public void addData(String key, String value) {
        data.put(key, value);
    }

    public void removeData(String key) {
        data.remove(key);
    }

    public String getData(String key) {
        return data.get(key);
    }

    public BigInteger getHash(){
        return hash;
    }

    public BigInteger calculateHash() {
        byte[] input = digest.digest(ip.getBytes(StandardCharsets.UTF_8));
        BigInteger hashValue = new BigInteger(1, input);
        return hashValue.mod(new BigInteger(String.valueOf((long)Math.pow(2,256))));
    }

}
