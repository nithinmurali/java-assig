package com.stakx.cache;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;

public interface DepthCache<T extends Map<BigDecimal, BigDecimal>> {

    public void initCache(T initialAsks, T initialBids, long lastUpdated);

    public void updateCache(T deltaAsks, T deltaBids);

    public Map<String, NavigableMap<BigDecimal, BigDecimal>> getCacheContents();

    public long getUpdated();

    public void setUpdated(long updatedId);

    public T getAsks();

    public T getBids();

    public void printDepthCache();

    public boolean isEmpty();

}
