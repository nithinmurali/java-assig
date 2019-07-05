package com.stakx.api;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.exception.BinanceApiException;
import com.stakx.cache.BinanceDepthCache;
import com.stakx.cache.RedisDepthCache;
import com.stakx.common.Constants;

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

        BinanceApiRestClient client = factory.newRestClient();

        if (symbols == null){
            this.fetchSymbols();
        } else {
            this.symbols = symbols;
        }

        System.out.println("Streamer initialized for " + this.symbols.size() + " symbols.");


        this.orderLimit = Constants.ORDER_LIMIT;
        this.caches = new HashMap<>();

        // create caches
        for (String symbol: this.symbols){
            symbol = symbol.toUpperCase();
            BinanceDepthCache cache = new RedisDepthCache(symbol);
            this.caches.put(symbol, cache);
        }

        // subscribe to updates
        this.startStreaming();

        // fetch order book
        for (String symbol: this.symbols){
            symbol = symbol.toUpperCase();
            OrderBook orderBook = client.getOrderBook(symbol, orderLimit);
            this.caches.get(symbol).initCache(orderBook);
        }
    }

    private void startStreaming() {
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

    private void fetchSymbols(){
        BinanceApiRestClient client = factory.newRestClient();
        if (this.symbols != null){
            this.symbols.clear();
        }

        this.symbols = new ArrayList<>();

        List<TickerPrice> allPrices = client.getAllPrices();
        for (TickerPrice tickerPrice: allPrices){
            String symbol = tickerPrice.getSymbol();
            if (this.checkSymbol(symbol)){
                this.symbols.add(symbol);
                //System.out.println(symbol);
            }
        }
    }

    boolean checkSymbol(String symbol){
        return symbol.endsWith("BTC");
    }

}
