package com.github.sriki77.apiproxy.instrument.model;

import java.util.List;

public abstract class Endpoint {
    private FaultRules faultRules;
    private Flow preflow;
    private List<Flow> flows;
    private Flow postflow;
}
