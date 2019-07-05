package com.stakx;

import com.stakx.api.BinanceOrderBookStreamer;
import com.stakx.api.OrderBookStreamer;
import com.stakx.common.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BinanceRunner {

    public static void main(String[] args) {

        Properties prop = Constants.getConfigProp();
        if(prop == null){
            System.out.println("Unable to load config file for reading api keys");
            return;
        }
        String api_key = prop.getProperty("api.key");
        String api_secret = prop.getProperty("api.secret");

        if (api_key != null && api_secret != null){
            OrderBookStreamer binanceStreamer = new BinanceOrderBookStreamer(api_key, api_secret);
            //List<String> symbols = new ArrayList<>();
            //symbols.add("ETHBTC");
            binanceStreamer.initStreamer(null);
        } else {
            System.out.println("Unable to read api credentials.");
        }

    }

}
