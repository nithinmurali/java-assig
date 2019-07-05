package com.stakx.cache;

import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import junit.framework.TestCase;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertNotEquals;

public class BinanceDepthCachetest {

    private OrderBook orderBook;
    private DepthEvent depthEvent;

    @Before
    public void setUp(){
        this.orderBook = this.generateOrderBook();
        this.depthEvent = this.generateEvent(100L);
    }

    @Test
    public void initCacheTest(){
        BinanceDepthCache depthCache = new BinanceDepthCache();
        TestCase.assertTrue(depthCache.isEmpty());
        depthCache.initCache(orderBook);
        TestCase.assertFalse(depthCache.isEmpty());
    }

    @Test
    public void pendingUpdatesTest(){
        BinanceDepthCache depthCache = new BinanceDepthCache();
        TestCase.assertEquals(depthCache.getPendingUpdatesSize(), 0);
        depthCache.updateCache(this.depthEvent);
        TestCase.assertEquals(depthCache.getPendingUpdatesSize(), 1);
        depthCache.initCache(this.orderBook);
        TestCase.assertEquals(depthCache.getPendingUpdatesSize(), 0);
    }

    @Test
    public void updateTest(){
        BinanceDepthCache depthCache = new BinanceDepthCache();
        depthCache.initCache(this.orderBook);
        TestCase.assertEquals(depthCache.getAsks().size(), this.orderBook.getAsks().size());
        TestCase.assertEquals(depthCache.getBids().size(), this.orderBook.getBids().size());
        depthCache.updateCache(this.depthEvent);
        assertNotEquals(depthCache.getAsks().size(), this.orderBook.getAsks().size());
        assertNotEquals(depthCache.getBids().size(), this.orderBook.getBids().size());
    }

    private OrderBook generateOrderBook(){
        OrderBook orderBook = new OrderBook();
        List<OrderBookEntry> asks = new ArrayList<>();
        List<OrderBookEntry> bids = new ArrayList<>();

        for (int i=0; i< 10; i++) {
            OrderBookEntry o = new OrderBookEntry();
            o.setPrice(generateFloat());
            o.setQty(generateFloat());
            asks.add(o);
            o.setPrice(generateFloat());
            o.setQty(generateFloat());
            bids.add(o);
        }
        orderBook.setAsks(asks);
        orderBook.setBids(bids);
        return orderBook;
    }

    private List<OrderBook> generateOrderBooks(int number){
        List<OrderBook> orderBooks = new ArrayList<>();
        for(int i=0;i< number; i++){
            orderBooks.add(generateOrderBook());
        }
        return orderBooks;
    }

    private String generateFloat(){
        Random rand = new Random();
        float f = rand.nextFloat();
        return String.valueOf(f);
    }

    private DepthEvent generateEvent(long updated){
        DepthEvent e = new DepthEvent();
        OrderBook ob = generateOrderBook();
        e.setAsks(ob.getAsks());
        e.setBids(ob.getBids());
        e.setFinalUpdateId(updated);
        return e;
    }
}
