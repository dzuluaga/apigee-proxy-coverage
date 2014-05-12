package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Step")
public class Step implements LocationProvider {

    @XStreamAlias("Name")
    protected String name;

    @XStreamAlias("Condition")
    protected String condition;

    protected LocationProvider parent;

    protected Step(String name, String condition, LocationProvider parent) {
        this.name = name;
        this.condition = condition;
        this.parent = parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "Step{" +
                "name='" + name + '\'' +
                ", condition='" + condition + '\'' +
                '}';
    }


    public Step duplicate() {
        return new Step(name, condition, parent);
    }

    @Override
    public void setParent(LocationProvider parent) {
        this.parent = parent;
    }

    @Override
    public String location() {
        String loc = "Policy: " + name;
        if (condition != null) {
            loc += " Condition: " + condition;
        }
        return LocationProvider.append(parent, loc);
    }

    public String initUsingTemplate(String template) {
        return String.format(template,LocationProvider.endpointName(this),
                LocationProvider.proxyFileName(this), LocationProvider.flowName(this), LocationProvider.policyName(this));
    }

    public String getName() {
        return name;
    }
}
