package com.github.sriki77.apiproxy.instrument;


import com.github.sriki77.apiproxy.instrument.io.ProxyFileHandler;
import com.github.sriki77.apiproxy.instrument.work.ProxyInstrumeter;

public class Instrumenter {
    public static void main(String... args) {
        validateCmdLineArgs(args);
        final Instrumenter instrumenter = new Instrumenter();
        final ProxyFileHandler proxyFileHandler = instrumenter.getProxyFileHandler(args);
        final ProxyInstrumeter proxyInstrumenter = instrumenter.getProxyInstrumenter();
        proxyInstrumenter.instrument(proxyFileHandler);
    }

    private static void validateCmdLineArgs(String[] args) {
        if (args.length != 1) {
            System.err.printf("Usage: java %s <proxy directory or zip file>", Instrumenter.class.getName());
            System.err.println();
            System.exit(-1);
        }
    }

    private ProxyFileHandler getProxyFileHandler(String[] args) {
        return null;
    }

    public ProxyInstrumeter getProxyInstrumenter() {
        return null;
    }
}

