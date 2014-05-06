package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Step")
public class Step {

    @XStreamAlias("Name")
    private String name;

    @XStreamAlias("Condition")
    private String condition;

    @Override
    public String toString() {
        return "Step{" +
                "name='" + name + '\'' +
                ", condition='" + condition + '\'' +
                '}';
    }
}
