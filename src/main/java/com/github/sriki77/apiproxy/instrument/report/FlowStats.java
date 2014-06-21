package com.github.sriki77.apiproxy.instrument.report;

import com.github.sriki77.apiproxy.instrument.model.Step;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

@XStreamAlias("FlowStats")
public class FlowStats extends Stats {

    protected FlowStats(String name, List<Step> steps) {
        this.name = name;
        updateStats(steps);
    }

}
