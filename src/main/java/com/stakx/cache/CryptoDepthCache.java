package com.stakx.cache;

import java.math.BigDecimal;
import java.util.*;

public class CryptoDepthCache implements DepthCache<NavigableMap<BigDecimal, BigDecimal>> {

    private static final String BIDS  = "BIDS";
    private static final String ASKS  = "ASKS";

    private long lastUpdateId;

    // saved as price: qty
    private Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache;

    public CryptoDepthCache(NavigableMap<BigDecimal, BigDecimal> initialAsks, NavigableMap<BigDecimal, BigDecimal> initialBids, long lastUpdated){
        initCache(initialAsks, initialBids, lastUpdated);
    }

    @Override
    public void initCache(NavigableMap<BigDecimal, BigDecimal> initialAsks, NavigableMap<BigDecimal, BigDecimal> initialBids, long lastUpdated) {
        this.depthCache = new HashMap<>();
        this.lastUpdateId = lastUpdated;

        depthCache.put(ASKS, initialAsks);
        depthCache.put(BIDS, initialBids);
        System.out.println("Cache initialized!");

    }

    @Override
    public void updateCache(NavigableMap<BigDecimal, BigDecimal> deltaAsks, NavigableMap<BigDecimal, BigDecimal> deltaBids) {
        this.updateCache(BIDS, deltaBids);
        this.updateCache(ASKS, deltaAsks);
        System.out.println("Cache updated!");
    }

    private void updateCache(String key, NavigableMap<BigDecimal, BigDecimal> deltaData){
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
    public Map<String, NavigableMap<BigDecimal, BigDecimal>> getCacheContents() {
        return depthCache;
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
    public NavigableMap<BigDecimal, BigDecimal> getAsks() {
        return depthCache.get(ASKS);
    }

    @Override
    public NavigableMap<BigDecimal, BigDecimal> getBids() {
        return depthCache.get(BIDS);
    }

    @Override
    public void printDepthCache() {
        //System.out.println(this.depthCache);
        System.out.println("ASKS:");
        this.getAsks().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
        System.out.println("BIDS:");
        this.getBids().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
    }

    private static String toDepthCacheEntryString(Map.Entry<BigDecimal, BigDecimal> depthCacheEntry) {
        return depthCacheEntry.getKey().toPlainString() + " / " + depthCacheEntry.getValue();
    }


}
