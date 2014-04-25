package com.github.sriki77.apiproxy.instrument.io;

import java.io.File;

public class ProxyZipFileHandler extends ProxyDirectoryHandler {
    private File zipFile;

    public ProxyZipFileHandler(File zipFile) {
        super(zipFile);
        this.zipFile = zipFile;
    }
}
