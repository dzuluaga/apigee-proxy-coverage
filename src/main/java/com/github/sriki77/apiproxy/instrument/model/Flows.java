package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("Flows")
public class Flows {

    @XStreamImplicit(itemFieldName = "Flow")
    protected List<Flow> flows;
}
