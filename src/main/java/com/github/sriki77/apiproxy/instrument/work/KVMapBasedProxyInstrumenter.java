package com.github.sriki77.apiproxy.instrument.work;

import com.github.sriki77.apiproxy.instrument.io.ProxyFileHandler;

public class KVMapBasedProxyInstrumenter implements ProxyInstrumeter {

    @Override
    public void instrument(ProxyFileHandler proxyFileHandler) {
        instrumentProxyEndpoints(proxyFileHandler);
        instrumentTargetEndpoints(proxyFileHandler);
    }

    void instrumentTargetEndpoints(ProxyFileHandler proxyFileHandler) {

    }

    void instrumentProxyEndpoints(ProxyFileHandler proxyFileHandler) {

    }
}
