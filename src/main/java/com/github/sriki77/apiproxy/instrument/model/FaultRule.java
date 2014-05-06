package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.w3c.dom.Node;

@XStreamAlias("FaultRule")
public class FaultRule extends FlowSteps {
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;
    private Node node;

    @Override
    public void holdNode(Node node) {
        this.node = NodeHolder.findMyselfUsingXpath(node, String.format("//FaultRule[@name='%s']", name));
    }


    @Override
    protected Node getDOMNode() {
        return node;
    }
}
