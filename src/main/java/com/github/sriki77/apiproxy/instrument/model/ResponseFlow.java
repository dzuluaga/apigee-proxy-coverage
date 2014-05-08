package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.w3c.dom.Node;

@XStreamAlias("Response")
public class ResponseFlow extends FlowSteps {

    private Node node;

    @Override
    public void holdNode(Node node) {
        this.node = node;
    }

    @Override
    protected Node getDOMNode() {
        return node;
    }

    @Override
    public String location() {
        return parent.location()+":ResponseFlow";
    }
}
