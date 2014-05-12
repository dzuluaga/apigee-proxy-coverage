package com.github.sriki77.apiproxy.instrument.model;

public class PolicyUpdate {
    public final String name;
    public final String policyData;

    public PolicyUpdate(String name, String policyData) {
        this.name = name;
        this.policyData = policyData;
    }

    public String updateFileName(String name) {
        return policyData.replace("#kv#", name);
    }
}
