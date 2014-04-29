package com.github.sriki77.apiproxy.instrument.model;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("FaultRules")
public class FaultRules {

    @XStreamImplicit(itemFieldName="FaultRule")
    private List<FaultRule> faultRules;
}
