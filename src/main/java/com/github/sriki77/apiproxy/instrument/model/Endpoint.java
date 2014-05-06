package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.w3c.dom.Node;

import java.io.File;

public abstract class Endpoint implements NodeHolder {

    @XStreamAlias("FaultRules")
    protected FaultRules faultRules;

    @XStreamAlias("PreFlow")
    protected PreFlow preflow;

    @XStreamAlias("Flows")
    protected Flows flows;

    @XStreamAlias("PostFlow")
    protected PostFlow postflow;

    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;


    private File xmlFile;
    private Node node;

    public void setXMLFile(File xmlFile) {

        this.xmlFile = xmlFile;
    }

    public File getXmlFile() {
        return xmlFile;
    }

    @Override
    public void holdNode(Node node) {
        this.node = node;
        NodeHolder.holdNode(faultRules, NodeHolder.findMyselfUsingXpath(node, "//FaultRules"));
        NodeHolder.holdNode(preflow, NodeHolder.findMyselfUsingXpath(node, "//PreFlow"));
        NodeHolder.holdNode(postflow, NodeHolder.findMyselfUsingXpath(node, "//PostFlow"));
        NodeHolder.holdNode(flows, NodeHolder.findMyselfUsingXpath(node, "//Flows"));
    }

    public FaultRules getFaultRules() {
        return faultRules;
    }

    public PreFlow getPreflow() {
        return preflow;
    }

    public Flows getFlows() {
        return flows;
    }

    public PostFlow getPostflow() {
        return postflow;
    }

    public Node getNode() {
        return node;
    }
}
