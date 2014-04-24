package com.github.sriki77.apiproxy.instrument.work;

import com.github.sriki77.apiproxy.instrument.io.ProxyFileHandler;

public interface ProxyInstrumeter {
    void instrument(ProxyFileHandler proxyFileHandler);
}
