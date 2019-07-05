package com.stakx.cache;

import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

import java.math.BigDecimal;
import java.util.*;

public class BinanceDepthCache extends BaseDepthCache {

    private List<DepthEvent> pendingUpdates;

    public BinanceDepthCache(){
        super();
        this.pendingUpdates = new ArrayList<>();
    }

    public BinanceDepthCache(OrderBook orderBook){
        super(getAsksFromOrderBook(orderBook), getBidsFromOrderBook(orderBook), orderBook.getLastUpdateId());
        this.pendingUpdates = new ArrayList<>();
    }

    public void initCache(OrderBook orderBook) {
        super.initCache(getAsksFromOrderBook(orderBook), getBidsFromOrderBook(orderBook), orderBook.getLastUpdateId());
        this.applyPendingUpdates();
    }

    private static NavigableMap<BigDecimal, BigDecimal> getAsksFromOrderBook(OrderBook orderBook){
        NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>(Comparator.reverseOrder());
        for (OrderBookEntry ask : orderBook.getAsks()) {
            asks.put(new BigDecimal(ask.getPrice()), new BigDecimal(ask.getQty()));
        }
        return asks;
    }

    private static NavigableMap<BigDecimal, BigDecimal> getBidsFromOrderBook(OrderBook orderBook){
        NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Comparator.reverseOrder());
        for (OrderBookEntry bid : orderBook.getBids()) {
            bids.put(new BigDecimal(bid.getPrice()), new BigDecimal(bid.getQty()));
        }
        return bids;
    }

    public void updateCache(DepthEvent event) {

        // if cache is not inited store to pending updates
        if (isEmpty()){
            System.out.println("Update pushed to pending... : " + this.pendingUpdates.size());
            this.pendingUpdates.add(event);
            return;
        }

        // only update only if this update is newer than cache
        if (event.getFinalUpdateId() < this.getUpdated()){
            System.out.println("Update skipped as update is old...");
            return;
        }

        for (OrderBookEntry orderBookDelta : event.getAsks()) {
            BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
            BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
            if (qty.compareTo(BigDecimal.ZERO) == 0) {
                this.getAsks().remove(price);
            } else {
                this.getAsks().put(price, qty);
            }
        }

        for (OrderBookEntry orderBookDelta : event.getBids()) {
            BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
            BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
            if (qty.compareTo(BigDecimal.ZERO) == 0) {
                this.getBids().remove(price);
            } else {
                this.getBids().put(price, qty);
            }
        }

        this.setUpdated(event.getFinalUpdateId());

        System.out.println("Cache updated!");

    }

    private void applyPendingUpdates(){
        //System.out.println("Applying pending updates " + this.pendingUpdates.size());

        if (this.isEmpty()){
            this.pendingUpdates.clear();
            return;
        }

        for (DepthEvent event: this.pendingUpdates){
            if (event.getFinalUpdateId() < this.getUpdated()){
                continue;
            }
            this.updateCache(event);
        }
        this.pendingUpdates.clear();
    }

    public void printDepthCache(String symbol) {
        System.out.println("Cache for " + symbol);
        super.printDepthCache();
    }

    public int getPendingUpdatesSize(){
        if (this.pendingUpdates != null) {
            return this.pendingUpdates.size();
        } else {
            return 0;
        }
    }
}
