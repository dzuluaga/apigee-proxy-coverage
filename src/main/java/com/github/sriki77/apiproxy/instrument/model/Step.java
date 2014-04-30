package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("Step")
public class Step {

    @XStreamAlias("Name")
    private String name;

    @XStreamAlias("Condition")
    private String condition;

}
