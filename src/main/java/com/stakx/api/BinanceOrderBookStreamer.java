package com.stakx.api;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.stakx.cache.BinanceDepthCache;

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

        for (String symbol: symbols){
            symbol = symbol.toUpperCase();
            OrderBook orderBook = client.getOrderBook(symbol, orderLimit);

            BinanceDepthCache cache = new BinanceDepthCache(orderBook);
            this.caches.put(symbol, cache);
        }
    }

    @Override
    public void startStreaming() {
        wsClient = factory.newWebSocketClient();

        System.out.println("Begin Streaming!");
        String csv = String.join(",", this.symbols);

        wsClient.onDepthEvent(csv.toLowerCase(), (DepthEvent response) -> {
            String symbol = response.getSymbol().toUpperCase();
            BinanceDepthCache cache = caches.get(symbol);
            if (response.getFinalUpdateId() > cache.getUpdated()) {
                cache.setUpdated(response.getFinalUpdateId());
                cache.updateCache(response.getAsks(), response.getBids());
                cache.printDepthCache(symbol);
            }
        });
    }

    @Override
    public void stopStreaming() {

    }
}
