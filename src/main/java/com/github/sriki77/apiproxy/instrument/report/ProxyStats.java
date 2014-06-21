package com.github.sriki77.apiproxy.instrument.report;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("ProxyStats")
public class ProxyStats extends Stats {

    protected List<EndpointStats> endpointStats = new ArrayList<>();

    @Override
    protected void calcCoverage() {
        endpointStats.forEach(e -> e.calcCoverage());
        super.calcCoverage();
    }

    public void add(EndpointStats endpointStats) {
        this.endpointStats.add(endpointStats);
    }
}
