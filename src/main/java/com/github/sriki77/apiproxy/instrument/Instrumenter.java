package com.github.sriki77.apiproxy.instrument;


import com.github.sriki77.apiproxy.instrument.io.ProxyDirectoryHandler;
import com.github.sriki77.apiproxy.instrument.io.ProxyFileHandler;
import com.github.sriki77.apiproxy.instrument.io.ProxyStatsCollector;
import com.github.sriki77.apiproxy.instrument.io.ProxyZipFileHandler;
import com.github.sriki77.apiproxy.instrument.model.Endpoint;
import com.github.sriki77.apiproxy.instrument.model.FlowSteps;
import com.github.sriki77.apiproxy.instrument.model.Step;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Instrumenter {
    public static void main(String... args) throws Exception {
        final File file = validateCmdLineArgs(args);
        final Instrumenter instrumenter = new Instrumenter();
        try (ProxyFileHandler proxyFileHandler = instrumenter.getProxyFileHandler(file)) {
            final List<Endpoint> endpoints = proxyFileHandler.getEndpoints();
            final ProxyInstrumeter proxyInstrumenter = instrumenter.getProxyInstrumenter(endpoints);
            final List<Endpoint> instrumentedEndPoints = proxyInstrumenter.instrument();
            instrumentedEndPoints.forEach(proxyFileHandler::updateEndpoint);
            addStats(endpoints, (ProxyStatsCollector) proxyFileHandler);
        }
    }

    private static void addStats(List<Endpoint> endpoints, ProxyStatsCollector collector) {
        collector.update("Endpoints", endpoints.size());
        collector.update("Policies", policiesCount(endpoints));
    }

    private static int policiesCount(List<Endpoint> endpoints) {
        Set<String> policies = new HashSet<>();
        endpoints.forEach(e -> {
            e.getFaultRules().getFaultRules().forEach(f -> policies.addAll(policies(f)));
            policies.addAll(policies(e.getPreflow().getRequestFlow()));
            policies.addAll(policies(e.getPreflow().getResponseFlow()));
            policies.addAll(policies(e.getPostflow().getRequestFlow()));
            policies.addAll(policies(e.getPostflow().getResponseFlow()));
            e.getFlows().getFlows().forEach(f -> {
                policies.addAll(policies(f.getRequestFlow()));
                policies.addAll(policies(f.getResponseFlow()));
            });
        });
        return policies.size();
    }

    private static Set<String> policies(FlowSteps f) {
        return f.getSteps().stream().map(Step::getName).collect(Collectors.toSet());
    }


    private static File validateCmdLineArgs(String[] args) {
        validateArgCount(args);
        final File file = new File(args[0]);
        validateFile(file);
        return file;

    }

    private static void validateFile(File file) {
        if (!file.exists()) {
            System.err.println("Specified proxy file not found: " + file);
            System.exit(-1);
        }
        if (file.isDirectory()) {
            if (!file.canRead() || !file.canWrite()) {
                System.err.println("Specified proxy directory should be readable and writeable: " + file);
                System.exit(-1);
            }
        }
        if (file.isFile()) {
            if (!file.getName().endsWith("zip") || !file.canRead()) {
                System.err.println("Specified proxy file should be readable and a valid bundle zip file: " + file);
                System.exit(-1);
            }
        }

    }

    private static void validateArgCount(String[] args) {
        if (args.length != 1) {
            System.err.printf("Usage: java %s <proxy directory or zip file>", Instrumenter.class.getName());
            System.err.println();
            System.exit(-1);
        }
    }

    private ProxyFileHandler getProxyFileHandler(File file) throws Exception {
        if (file.isDirectory()) {
            return new ProxyDirectoryHandler(file);
        }
        return new ProxyZipFileHandler(file);
    }

    public ProxyInstrumeter getProxyInstrumenter(List<Endpoint> endpoints) {
        return new KVMapBasedProxyInstrumenter(endpoints);
    }
}

