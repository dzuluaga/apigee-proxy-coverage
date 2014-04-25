package com.github.sriki77.apiproxy.instrument;


import com.github.sriki77.apiproxy.instrument.io.ProxyDirectoryHandler;
import com.github.sriki77.apiproxy.instrument.io.ProxyFileHandler;
import com.github.sriki77.apiproxy.instrument.io.ProxyZipFileHandler;
import com.github.sriki77.apiproxy.instrument.work.KVMapBasedProxyInstrumenter;
import com.github.sriki77.apiproxy.instrument.work.ProxyInstrumeter;

import java.io.File;

public class Instrumenter {
    public static void main(String... args) {
        final File file = validateCmdLineArgs(args);
        final Instrumenter instrumenter = new Instrumenter();
        final ProxyFileHandler proxyFileHandler = instrumenter.getProxyFileHandler(file);
        final ProxyInstrumeter proxyInstrumenter = instrumenter.getProxyInstrumenter();
        proxyInstrumenter.instrument(proxyFileHandler);
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

    private ProxyFileHandler getProxyFileHandler(File file) {
        if (file.isDirectory()) {
            return new ProxyDirectoryHandler(file);
        }

        return new ProxyZipFileHandler(file);
    }

    public ProxyInstrumeter getProxyInstrumenter() {
        return new KVMapBasedProxyInstrumenter();
    }
}

