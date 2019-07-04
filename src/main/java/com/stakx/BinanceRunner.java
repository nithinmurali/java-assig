package com.stakx;

import com.stakx.api.BinanceOrderBookStreamer;
import com.stakx.api.OrderBookStreamer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BinanceRunner {

    private static String CONFIG_FILE = "config.properties";

    public static void main(String[] args) {

        String api_key=null, api_secret=null;

        // Read configurations
        try (InputStream input = BinanceRunner.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);
            api_key = prop.getProperty("api.key");
            api_secret = prop.getProperty("api.secret");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (api_key != null && api_secret != null){
            OrderBookStreamer binanceStreamer = new BinanceOrderBookStreamer(api_key, api_secret);
            List<String> symbols = new ArrayList<>();
            symbols.add("ETHBTC");
            symbols.add("ETHUSDT");
            binanceStreamer.initStreamer(symbols);
        } else {
            System.out.println("Unable to read api credentials.");
        }

    }

}
