package com.stakx.cache;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.util.Pair;

import java.math.BigDecimal;
import java.util.*;

public class BaseDepthCache implements DepthCache<Map<BigDecimal, BigDecimal>> {

    private static final String BIDS  = "BIDS";
    private static final String ASKS  = "ASKS";

    private long lastUpdateId;

    // saved as price: qty
    private Map<String, Map<BigDecimal, BigDecimal>> depthCache;

    public BaseDepthCache(){
        this.depthCache = new HashMap<>();
    }

    public BaseDepthCache(Map<BigDecimal, BigDecimal> initialAsks, Map<BigDecimal, BigDecimal> initialBids, long lastUpdated){
        this.depthCache = new HashMap<>();
        initCache(initialAsks, initialBids, lastUpdated);
    }

    @Override
    public void initCache(Map<BigDecimal, BigDecimal> initialAsks, Map<BigDecimal, BigDecimal> initialBids, long lastUpdated) {
        this.clear();
        this.setUpdated(lastUpdated);

        this.insertAsks(initialAsks);
        this.insertBids(initialBids);
        System.out.println("Cache initialized!");
    }

    @Override
    public void updateCache(Map<BigDecimal, BigDecimal> deltaAsks, Map<BigDecimal, BigDecimal> deltaBids) {
        this.updateCache(BIDS, deltaBids);
        this.updateCache(ASKS, deltaAsks);
    }

    private void updateCache(String key, Map<BigDecimal, BigDecimal> deltaData){

        for (Map.Entry<BigDecimal, BigDecimal> entry: deltaData.entrySet()){
            BigDecimal price = entry.getKey();
            BigDecimal qty = entry.getValue();

            if (qty.compareTo(BigDecimal.ZERO) == 0) {
                depthCache.get(key).remove(price);
            } else {
                depthCache.get(key).put(price, qty);
            }
        }

    }

    @Override
    public long getUpdated() {
        return this.lastUpdateId;
    }

    @Override
    public void setUpdated(long updatedId) {
        this.lastUpdateId = updatedId;
    }


    @Override
    public Map<BigDecimal, BigDecimal> getAsks() {
        return depthCache.get(ASKS);
    }

    @Override
    public Map<BigDecimal, BigDecimal> getBids() {
        return depthCache.get(BIDS);
    }

    @Override
    public void printDepthCache() {
        if (this.getAsks() == null && this.getBids()== null){
            System.out.println("Cache empty ...");
            return;
        }
        if (this.getAsks() != null) {
            System.out.println("ASKS:");
            this.getAsks().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
        }
        if(this.getBids()!= null) {
            System.out.println("BIDS:");
            this.getBids().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
        }
    }

    private static String toDepthCacheEntryString(Map.Entry<BigDecimal, BigDecimal> depthCacheEntry) {
        return depthCacheEntry.getKey().toPlainString() + " / " + depthCacheEntry.getValue();
    }

    public boolean isEmpty(){
        return this.depthCache.isEmpty();
    }

    void clear(){
        this.depthCache.clear();
    }

    void insertAsks(Map<BigDecimal, BigDecimal> initialAsks){
        depthCache.put(ASKS, initialAsks);
    }

    void insertBids(Map<BigDecimal, BigDecimal> initialBids){
        depthCache.put(BIDS, initialBids);
    }

    @Override
    public Map<String, NavigableMap<BigDecimal, BigDecimal>> getCacheContents(){
        Map<String, NavigableMap<BigDecimal, BigDecimal>> map = new HashMap<>();
        map.put(ASKS, new TreeMap<>(this.getAsks()));
        map.put(BIDS, new TreeMap<>(this.getBids()));
        return map;
    }

}
