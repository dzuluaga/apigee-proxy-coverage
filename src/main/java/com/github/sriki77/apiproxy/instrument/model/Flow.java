package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Flow")
public class Flow {

    @XStreamAlias("Request")
    protected RequestFlow requestFlow;

    @XStreamAlias("Response")
    protected ResponseFlow responseFlow;

    @XStreamAlias("Condition")
    protected String condition;

    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;
}
