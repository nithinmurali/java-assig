package com.stakx.cache;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Map;

public interface DepthCache<T> {

    public void initCache(T initialAsks, T initialBids, long lastUpdated);

    public void updateCache(T deltaAsks, T deltaBids);

    public Map<String, T> getCacheContents();

    public long getUpdated();

    public void setUpdated(long updatedId);

    public T getAsks();

    public T getBids();

    public void printDepthCache();

}
