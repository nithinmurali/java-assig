package com.stakx.cache;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

public class RedisDepthCacheTest extends BinanceDepthCacheTest {

    static RedisServer redisServer;
    static RedissonClient redissonClient;

    @BeforeClass
    public static void startRedis(){
        try {
            int port = getTemporaryPort();
            redisServer = new RedisServer(port);
            redisServer.start();
            Config config = new Config();
            config.useSingleServer().setAddress("redis://127.0.0.1:" + port);
            redissonClient = Redisson.create(config);
        } catch (IOException e){
            System.out.println("Unable to start redis.");
        }
    }

    BinanceDepthCache getCacheObject(){
        RedisDepthCache cache = new RedisDepthCache("ETHBTC");
        cache.setRedisServer(RedisDepthCacheTest.redissonClient);
        return cache;
    }

    private static int getTemporaryPort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();
        socket.close();
        return port;
    }


    @AfterClass
    public static void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
            redisServer = null;
        }
    }
}
