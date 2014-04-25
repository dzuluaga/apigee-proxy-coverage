package com.github.sriki77.apiproxy.instrument.io;

import com.github.sriki77.apiproxy.instrument.model.Endpoint;

import java.util.stream.Stream;

public interface ProxyFileHandler {

    Stream<Endpoint> getEndPoints();

    void updateEndPoint(Endpoint endpoint);
}
