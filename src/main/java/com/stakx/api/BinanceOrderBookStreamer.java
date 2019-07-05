package com.stakx.api;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.exception.BinanceApiException;
import com.stakx.cache.BinanceDepthCache;
import com.stakx.cache.RedisDepthCache;

import java.util.*;

public class BinanceOrderBookStreamer implements OrderBookStreamer {

    private List<String> symbols;
    private int orderLimit;
    private Map<String, BinanceDepthCache> caches;
    private BinanceApiClientFactory factory;
    private BinanceApiWebSocketClient wsClient;

    public BinanceOrderBookStreamer(String API_KEY, String SECRET){
        factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
    }

    @Override
    public void initStreamer(List<String> symbols) {

        System.out.println("Streamer initialized!");

        BinanceApiRestClient client = factory.newRestClient();

        this.symbols = symbols;
        this.orderLimit = 10;
        this.caches = new HashMap<>();

        // create caches
        for (String symbol: symbols){
            symbol = symbol.toUpperCase();
            BinanceDepthCache cache = new RedisDepthCache(symbol);
            this.caches.put(symbol, cache);
        }

        // subscribe to updates
        this.startStreaming();

        // fetch order book
        for (String symbol: symbols){
            symbol = symbol.toUpperCase();
            OrderBook orderBook = client.getOrderBook(symbol, orderLimit);
            this.caches.get(symbol).initCache(orderBook);
        }
    }

    @Override
    public void startStreaming() {
        wsClient = factory.newWebSocketClient();

        System.out.println("Begin Streaming!");
        String csv = String.join(",", this.symbols);

        wsClient.onDepthEvent(csv.toLowerCase(), (DepthEvent response) -> {
            // TODO try
            String symbol = response.getSymbol().toUpperCase();

            BinanceDepthCache cache = caches.get(symbol);
            cache.updateCache(response);
            cache.printDepthCache(symbol);
        });
    }
}
