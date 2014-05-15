package com.github.sriki77.apiproxy.instrument.io;

public interface ProxyStatsCollector {
    void update(String measure, int count);
}
