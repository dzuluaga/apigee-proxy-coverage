package com.github.sriki77.apiproxy.instrument.report;

import com.github.sriki77.apiproxy.instrument.model.Endpoint;
import com.github.sriki77.apiproxy.instrument.model.Step;
import com.jayway.jsonpath.JsonPath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KVMapInstrumentReportGenerator implements InstrumentReportGenerator {
    private final File kvInstrumentFile;
    private final File reportDirectory;
    private List<String> instrumentEntries;

    public KVMapInstrumentReportGenerator(File kvInstrumentFile, File reportDirectory) throws IOException {
        this.kvInstrumentFile = kvInstrumentFile;
        this.reportDirectory = reportDirectory;
        instrumentEntries = JsonPath.read(this.kvInstrumentFile, "$.entry[*].value");
    }

    @Override
    public void generateReport(Endpoint e) {
        final List<Step> allSteps = getAllSteps(e);
        allSteps.forEach(this::updateStep);
    }

    private void updateStep(Step step) {
        step.setExecuted(instrumentEntries.contains(step.location()));
    }

    private List<Step> getAllSteps(Endpoint e) {
        List<Step> steps = new ArrayList<>();
        e.getFaultRules().getFaultRules().forEach(f -> steps.addAll(f.getSteps()));
        steps.addAll(e.getPreflow().getRequestFlow().getSteps());
        steps.addAll(e.getPreflow().getResponseFlow().getSteps());
        steps.addAll(e.getPostflow().getRequestFlow().getSteps());
        steps.addAll(e.getPostflow().getResponseFlow().getSteps());
        e.getFlows().getFlows().forEach(f -> {
            steps.addAll(f.getRequestFlow().getSteps());
            steps.addAll(f.getResponseFlow().getSteps());
        });
        return steps;
    }
}
