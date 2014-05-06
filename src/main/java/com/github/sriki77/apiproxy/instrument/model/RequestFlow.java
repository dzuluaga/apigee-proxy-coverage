package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.w3c.dom.Node;

@XStreamAlias("Request")
public class RequestFlow extends FlowSteps {

    private Node node;

    @Override
    public void holdNode(Node node) {
        this.node = node;
    }

}
