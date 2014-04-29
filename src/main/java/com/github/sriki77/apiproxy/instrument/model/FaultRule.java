package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("FaultRules")
public class FaultRule extends FlowSteps {
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;
}
