package com.github.sriki77.apiproxy.instrument.io;

import com.github.sriki77.apiproxy.instrument.model.Endpoint;

import java.io.File;
import java.util.stream.Stream;

public class ProxyDirectoryHandler implements ProxyFileHandler {
    private File proxyDir;

    public ProxyDirectoryHandler(File proxyDir) {
        this.proxyDir = proxyDir;
    }

    @Override
    public Stream<Endpoint> getEndPoints() {
        return null;
    }

    @Override
    public void updateEndPoint(Endpoint endpoint) {

    }
}
