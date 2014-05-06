package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.w3c.dom.Node;

import java.util.List;

@XStreamAlias("Flows")
public class Flows implements NodeHolder {

    @XStreamImplicit(itemFieldName = "Flow")
    protected List<Flow> flows;

    @Override
    public void holdNode(Node node) {
        NodeHolder.holdNodes(flows, node);
    }


}
