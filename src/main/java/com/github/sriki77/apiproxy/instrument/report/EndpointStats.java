package com.github.sriki77.apiproxy.instrument.report;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("EndpointStats")
public class EndpointStats extends Stats {
    protected List<FlowStats> flowStats = new ArrayList<>();
    public String endpointType;


    @Override
    protected void calcCoverage() {
        flowStats.forEach(f -> f.calcCoverage());
        super.calcCoverage();
    }


    public void add(FlowStats flowStats) {
        this.flowStats.add(flowStats);
    }
}
