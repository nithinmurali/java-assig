package com.stakx.cache;

import com.binance.api.client.domain.market.OrderBook;
import com.stakx.repository.RedisHelper;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.math.BigDecimal;
import java.util.*;

public class RedisDepthCache extends BinanceDepthCache {

    private RedissonClient redisson;
    private String symbol;
    private static final String UPDATED_TIME = "UpdatedTime";
    private static final String ASK_KEY = "ASKS";
    private static final String BIDS_KEY = "BIDS";

    public RedisDepthCache(String symbol){
        super();

        redisson = RedisHelper.CLIENT;
        this.symbol = symbol;
        this.clear();
    }

    public void setRedisServer(RedissonClient redisson){
        this.redisson = redisson;
        this.clear();
    }

    public void initCache(OrderBook orderBook) {
        RMap<String, Long> updateMap = redisson.getMap(UPDATED_TIME);
        updateMap.put(symbol, 0L);

        super.initCache(orderBook);
    }


    public long getUpdated(){
        RMap<String, Long> updateMap = redisson.getMap(UPDATED_TIME);
        return updateMap.get(symbol);  // TODO check if empty
    }

    public void setUpdated(long updatedId){
        System.out.println(updatedId);
        RMap<String, Long> updateMap = redisson.getMap(UPDATED_TIME);
        updateMap.put(this.symbol, updatedId);
    }

    @Override
    public RMap<BigDecimal, BigDecimal> getAsks(){
        return redisson.getMap(symbol+"_"+ASK_KEY);
    }

    public RMap<BigDecimal, BigDecimal> getBids(){
        return redisson.getMap(symbol+"_"+BIDS_KEY);
    }

    public boolean isEmpty(){
        return this.getAsks().isEmpty() && this.getBids().isEmpty();
    }

    void clear(){
        this.getAsks().clear();
        this.getBids().clear();
    }

    void insertAsks(Map<BigDecimal, BigDecimal> initialAsks){
        this.getAsks().putAll(initialAsks);
    }

    void insertBids(Map<BigDecimal, BigDecimal> initialBids){
        this.getBids().putAll(initialBids);
    }

}
