package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PostFlow")
public class PostFlow extends Flow {
    @Override
    protected String getNodeXPath() {
        return "//PostFlow";
    }
}
