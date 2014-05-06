package com.github.sriki77.apiproxy.instrument.model;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.w3c.dom.Node;

import java.util.List;

@XStreamAlias("FaultRules")
public class FaultRules implements NodeHolder {

    @XStreamImplicit(itemFieldName = "FaultRule")
    private List<FaultRule> faultRules;

    @Override
    public void holdNode(Node node) {
        NodeHolder.holdNodes(faultRules, node);
    }

}
