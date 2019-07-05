package com.stakx.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constants {
    public static final String CONFIG_FILE = "config.properties";

    public static Properties getConfigProp(){
        try (InputStream input = Constants.class.getClassLoader().getResourceAsStream(Constants.CONFIG_FILE)) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return null;
            }
            prop.load(input);
            return prop;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
