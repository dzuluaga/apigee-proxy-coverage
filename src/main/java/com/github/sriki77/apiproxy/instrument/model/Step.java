package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Step")
public class Step {

    @XStreamAlias("Name")
    private String name;

    @XStreamAlias("Condition")
    private String condition;

     public Step() {

    }

    protected Step(String name, String condition) {
        this.name = name;
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
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
        return new Step(name, condition);
    }

}
