package com.github.sriki77.apiproxy.instrument.io;

import com.github.sriki77.apiproxy.instrument.model.Endpoint;

public interface ProxyFileHandler {

    java.util.List<Endpoint> getEndpoints();

    void updateEndpoint(Endpoint endpoint);
}
