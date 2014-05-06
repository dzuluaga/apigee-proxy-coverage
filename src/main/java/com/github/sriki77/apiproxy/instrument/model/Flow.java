package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.w3c.dom.Node;

@XStreamAlias("Flow")
public class Flow implements NodeHolder {

    @XStreamAlias("Request")
    protected RequestFlow requestFlow;

    @XStreamAlias("Response")
    protected ResponseFlow responseFlow;

    @XStreamAlias("Condition")
    protected String condition;

    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    @Override
    public void holdNode(Node node) {
        NodeHolder.holdNode(requestFlow, NodeHolder.findMyselfUsingXpath(node, getReqNodeXPath()));
        NodeHolder.holdNode(responseFlow, NodeHolder.findMyselfUsingXpath(node, getResNodeXPath()));
    }

    protected String getReqNodeXPath() {
        return String.format("//Flow[@name='%s']/Request", name);
    }

    protected String getResNodeXPath() {
        return String.format("//Flow[@name='%s']/Response", name);
    }

    public RequestFlow getRequestFlow() {
        return requestFlow;
    }

    public ResponseFlow getResponseFlow() {
        return responseFlow;
    }
}
