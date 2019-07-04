package com.stakx.api;

import java.util.List;

public interface OrderBookStreamer {

    public void initStreamer(List<String> symbols);
    public void startStreaming();

}
