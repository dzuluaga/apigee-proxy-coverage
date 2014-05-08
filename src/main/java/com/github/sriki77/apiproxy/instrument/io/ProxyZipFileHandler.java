package com.github.sriki77.apiproxy.instrument.io;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

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

    public File instrumentedZipFile() throws ZipException {
        final File parentDir = zipFile.getParentFile();
        final File targetZipFile = targetZipFile(parentDir);
        FileUtils.deleteQuietly(targetZipFile);
        final ZipFile zip = new ZipFile(targetZipFile);
        final ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        parameters.setIncludeRootFolder(false);
        zip.createZipFileFromFolder(proxyDir, parameters, false, -1);
        return targetZipFile;
    }

    private File targetZipFile(File parentDir) {
        final String origName = zipFile.getName();
        final String newName = FilenameUtils.getBaseName(origName) + "_instr." +
                FilenameUtils.getExtension(origName);
        return new File(parentDir, newName);
    }
}
