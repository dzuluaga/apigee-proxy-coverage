package com.github.sriki77.apiproxy.instrument.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PreFlow")
public class PreFlow extends Flow {

    @Override
    protected String getReqNodeXPath() {
        return "//PreFlow/Request";
    }

    @Override
    protected String getResNodeXPath() {
        return "//PreFlow/Response";
    }
}
