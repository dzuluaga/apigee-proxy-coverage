package com.github.sriki77.apiproxy.instrument.report;

import com.github.sriki77.apiproxy.instrument.io.Util;
import com.github.sriki77.apiproxy.instrument.model.*;
import com.jayway.jsonpath.JsonPath;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KVMapInstrumentReportGenerator implements InstrumentReportGenerator, Closeable {

    private static List<Step> overallSteps = new ArrayList<>();
    private static ProxyStats proxyStats = new ProxyStats();

    private String proxyName;
    private final File kvInstrumentFile;
    private final File reportDirectory;
    private List<String> instrumentEntries;

    public KVMapInstrumentReportGenerator(String proxyName, File kvInstrumentFile, File reportDirectory) throws IOException {
        this.proxyName = proxyName;
        this.kvInstrumentFile = kvInstrumentFile;
        this.reportDirectory = reportDirectory;
        instrumentEntries = cleanUpEntries(JsonPath.read(this.kvInstrumentFile, "$.entry[*].value"));
    }

    private List<String> cleanUpEntries(List<String> entries) {
        return entries.stream().map(e -> e.replace(" ", "")).collect(Collectors.toList());
    }

    @Override
    public void generateReport(Endpoint e) {
        final List<Step> allSteps = getAllSteps(e);
        allSteps.forEach(this::updateStep);
        updateStats(e, allSteps);
        writeToDisk(e);
    }

    private void updateStats(Endpoint e, List<Step> allSteps) {
        overallSteps.addAll(allSteps);
        EndpointStats endpointStats = new EndpointStats();
        endpointStats.name=e.getName();
        endpointStats.endpointType =e.endpointType();

        updateStats(allSteps, endpointStats);
        proxyStats.add(endpointStats);
        updateFlowStats(e, endpointStats);
    }


    private void writeToDisk(Endpoint e) {
        final XStream xStream = Util.xStreamInit();
        try {
            final File outFile = new File(reportDirectory, e.endpointType() + "_" + e.getXmlFile().getName());
            final PrettyPrintWriter writer = new PrettyPrintWriter(new FileWriter(outFile));
            xStream.marshal(e, writer);
            writer.close();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void updateStats(List<Step> steps, Stats stats) {
        Set<String> uniquePolicies = new HashSet<>();
        Set<String> executedPolicies = new HashSet<>();
        steps.forEach(s -> {
            uniquePolicies.add(s.getName());
            if (s.isExecuted()) {
                executedPolicies.add(s.getName());
            }
        });
        stats.totalPolicies = uniquePolicies.size();
        stats.executedPolicies = executedPolicies.size();
    }

    private void updateStep(Step step) {
        final String location = step.location();
        step.setExecuted(instrumentEntries.contains(location.replace(" ", "")));
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

    private void updateFlowStats(Endpoint e, EndpointStats stats) {
        updateFaultFlowStats(e, stats);
        updatePreFlowStats(e, stats);
        updatePostFlowStats(e, stats);
        for (Flow flow : e.getFlows().getFlows()) {
            List<Step> steps = new ArrayList<>();
            steps.addAll(flow.getRequestFlow().getSteps());
            steps.addAll(flow.getResponseFlow().getSteps());
            stats.add(new FlowStats("Flow: " + flow.getName(), steps));
        }
    }

    private void updatePostFlowStats(Endpoint e, EndpointStats stats) {
        List<Step> steps = new ArrayList<>();
        steps.addAll(e.getPostflow().getRequestFlow().getSteps());
        steps.addAll(e.getPostflow().getResponseFlow().getSteps());
        stats.add(new FlowStats("Post Flow", steps));
    }

    private void updatePreFlowStats(Endpoint e, EndpointStats stats) {
        List<Step> steps = new ArrayList<>();
        steps.addAll(e.getPreflow().getRequestFlow().getSteps());
        steps.addAll(e.getPreflow().getResponseFlow().getSteps());
        stats.add(new FlowStats("Pre Flow", steps));
    }

    private void updateFaultFlowStats(Endpoint e, EndpointStats stats) {
        for (FaultRule faultRule : e.getFaultRules().getFaultRules()) {
            stats.add(new FlowStats("Fault Rule: " + faultRule.getName(), faultRule.getSteps()));
        }

    }

    @Override
    public void close() throws IOException {
        proxyStats.name = proxyName;
        updateStats(overallSteps, proxyStats);
        proxyStats.calcCoverage();
        final XStream xStream = new XStream();
        xStream.processAnnotations(new Class[]{EndpointStats.class,
                FlowStats.class, ProxyStats.class});
        final PrettyPrintWriter writer = new PrettyPrintWriter(new FileWriter(new File(reportDirectory, "summary.xml")));
        xStream.marshal(proxyStats, writer);
        writer.close();
    }
}
