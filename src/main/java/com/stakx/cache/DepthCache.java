package com.stakx.cache;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.math.BigDecimal;
import java.util.Map;

public interface DepthCache<T extends Map<BigDecimal, BigDecimal>> {

    public void initCache(T initialAsks, T initialBids, long lastUpdated);

    public void updateCache(T deltaAsks, T deltaBids);

    public Map<String, T> getCacheContents();

    public long getUpdated();

    public void setUpdated(long updatedId);

    public T getAsks();

    public T getBids();

    public void printDepthCache();

    public boolean isEmpty();

}
