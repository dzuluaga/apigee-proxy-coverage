package com.github.sriki77.apiproxy.instrument.io;

import com.github.sriki77.apiproxy.instrument.model.Endpoint;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

public class ProxyDirectoryHandler implements ProxyFileHandler {
    private final File apiProxyDir;
    private final File proxyFilesDir;
    private final File targetFilesDir;
    private File proxyDir;

    public ProxyDirectoryHandler(File proxyDir) {
        this.proxyDir = proxyDir;
        apiProxyDir = apiProxyDir(proxyDir);
        proxyFilesDir = proxyFilesDir();
        targetFilesDir = targetFilesDir();
    }

    private File targetFilesDir() {
        if (apiProxyDir == null) {
            return null;
        }
        final File proxies = new File(apiProxyDir, "targets");
        if (!proxies.exists()) {
            return null;
        }
        return proxies;
    }

    private File proxyFilesDir() {
        if (apiProxyDir == null) {
            return null;
        }
        final File proxies = new File(apiProxyDir, "proxies");
        if (!proxies.exists()) {
            return null;
        }
        return proxies;
    }

    private File apiProxyDir(File proxyDir) {
        final File apiproxy = new File(proxyDir, "apiproxy");
        if (!apiproxy.exists()) {
            return null;
        }
        return apiproxy;
    }

    @Override
    public Stream<Endpoint> getEndpoints() {
        return getProxyFiles().map(f -> toEndpoint(f));
    }

    private Endpoint toEndpoint(File file) {
        return null;
    }

    Stream<File> getProxyFiles() {
        return Stream.concat(getProxyDirFiles(), getTargetDirFiles());
    }

    private Stream<File> getTargetDirFiles() {
        if (targetFilesDir == null) {
            return Stream.empty();
        }
        return Arrays.stream(targetFilesDir.listFiles((dir, name) -> name.endsWith(".xml")));
    }

    private Stream<File> getProxyDirFiles() {
        if (proxyFilesDir == null) {
            return Stream.empty();
        }
        return Arrays.stream(proxyFilesDir.listFiles((dir, name) -> name.endsWith(".xml")));
    }

    @Override
    public void updateEndpoint(Endpoint endpoint) {

    }

}
