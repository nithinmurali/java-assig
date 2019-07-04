package com.stakx.cache;

import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class BinanceDepthCache extends CryptoDepthCache {

    public BinanceDepthCache(OrderBook orderBook){
        super(getAsksFromOrderBook(orderBook), getBidsFromOrderBook(orderBook), orderBook.getLastUpdateId());
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

    public void updateCache(List<OrderBookEntry> orderBookDeltaAsks, List<OrderBookEntry>  orderBookDeltaBids) {
        for (OrderBookEntry orderBookDelta : orderBookDeltaAsks) {
            BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
            BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
            if (qty.compareTo(BigDecimal.ZERO) == 0) {
                // qty=0 means remove this level
                this.getAsks().remove(price);
            } else {
                this.getAsks().put(price, qty);
            }
        }

        for (OrderBookEntry orderBookDelta : orderBookDeltaBids) {
            BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
            BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
            if (qty.compareTo(BigDecimal.ZERO) == 0) {
                // qty=0 means remove this level
                this.getBids().remove(price);
            } else {
                this.getBids().put(price, qty);
            }
        }

    }

    public void printDepthCache(String symbol) {
        System.out.println("Cache for " + symbol);
        super.printDepthCache();
    }
}
