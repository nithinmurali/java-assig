package com.stakx.api;

import com.binance.api.client.BinanceApiCallback;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class BinanceOrderBookStreamer implements OrderBookStreamer {

    private List<String> symbols;
    private int orderLimit;
    private Map<String, BinanceDepthCache> caches;
    private BinanceApiClientFactory factory;
    private BinanceApiWebSocketClient wsClient;
    private final WsCallback wsCallback = new WsCallback();

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
            //BinanceDepthCache cache = new BinanceDepthCache();
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

        final Consumer<DepthEvent> updateCache = newEvent -> {
            String symbol = newEvent.getSymbol().toUpperCase();
            BinanceDepthCache cache = caches.get(symbol);
            cache.updateCache(newEvent);
            cache.printDepthCache(symbol);
        };

        wsCallback.setHandler(updateCache);
        wsClient.onDepthEvent(csv.toLowerCase(),wsCallback);
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

    public List<String> getSymbols(){
        return this.symbols;
    }

    boolean checkSymbol(String symbol){
        return symbol.endsWith("BTC");
    }

    private final class WsCallback implements BinanceApiCallback<DepthEvent> {

        private final AtomicReference<Consumer<DepthEvent>> handler = new AtomicReference<>();

        @Override
        public void onResponse(DepthEvent depthEvent) {
            try {
                handler.get().accept(depthEvent);
            } catch (final Exception e) {
                System.err.println("Exception caught processing depth event");
                e.printStackTrace(System.err);
            }
        }

        @Override
        public void onFailure(Throwable cause) {
            System.out.println("WS connection failed. Reconnecting. cause:" + cause.getMessage());

            initStreamer(getSymbols());
        }

        private void setHandler(final Consumer<DepthEvent> handler) {
            this.handler.set(handler);
        }
    }

}
