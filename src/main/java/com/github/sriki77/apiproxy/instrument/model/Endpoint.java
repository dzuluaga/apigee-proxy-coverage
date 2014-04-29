package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

public abstract class Endpoint {
    @XStreamImplicit(itemFieldName="FaultRules")
    protected List<FaultRules> faultRulesList;

    @XStreamImplicit(itemFieldName="PreFlow")
    protected List<Flow>  preflow;

    @XStreamImplicit(itemFieldName="Flows")
    protected List<Flows> flows;

    @XStreamAlias("PostFlow")
    @XStreamImplicit(itemFieldName="PostFlow")
    protected List<Flow>  postflow;

    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;
}
