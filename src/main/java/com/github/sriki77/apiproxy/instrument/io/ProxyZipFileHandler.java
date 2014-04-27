package com.github.sriki77.apiproxy.instrument.io;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProxyZipFileHandler extends ProxyDirectoryHandler {
    private File zipFile;

    public ProxyZipFileHandler(File zipFile) throws Exception {
        super(expandZipFile(zipFile));
        this.zipFile = zipFile;
    }

    static File expandZipFile(File zipFile) throws ZipException, IOException {
        final ZipFile zip = new ZipFile(zipFile);
        final Path tempDirectory = Files.createTempDirectory(zipFile.getName());
        zip.extractAll(tempDirectory.toString());
        return tempDirectory.toFile();
    }
}
