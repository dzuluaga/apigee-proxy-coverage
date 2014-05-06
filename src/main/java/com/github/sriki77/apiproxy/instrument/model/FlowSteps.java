package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

public abstract class FlowSteps implements NodeHolder {

    @XStreamImplicit(itemFieldName = "Step")
    protected List<Step> steps;

    @Override
    public String toString() {
        return "FlowSteps{" +
                "steps=" + steps +
                '}';
    }
}
