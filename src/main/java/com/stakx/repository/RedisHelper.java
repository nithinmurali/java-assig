package com.stakx.repository;

import com.stakx.common.Constants;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Properties;

public class RedisHelper {
    public static RedissonClient CLIENT;

    static {
        Config config = new Config();
        Properties prop = Constants.getConfigProp();
        if (prop != null) {
            config.useSingleServer().setAddress(prop.getProperty("redis.url")).setConnectionPoolSize(200);
        } else {
            config.useSingleServer().setAddress("redis://127.0.0.1:6379").setConnectionPoolSize(200);
        }

        CLIENT = Redisson.create(config);
    }
}
