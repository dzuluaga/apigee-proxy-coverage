package com.github.sriki77.apiproxy.instrument.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ProxyZipFileHandlerTest {

    private File testTempDir;
    private File proxyZipFile;

    @Before
    public void setUp() throws Exception {
        testTempDir = FileUtils.getTempDirectory();
        FileUtils.forceMkdir(testTempDir);
        FileUtils.copyFileToDirectory(new File(getClass().getResource("/profile_test.zip").getFile()), testTempDir);
        proxyZipFile = new File(testTempDir,"profile_test.zip");
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteQuietly(testTempDir);
    }

    @Test
    public void testExpandZipFile() throws Exception {
        final File expandZipFile = ProxyZipFileHandler.expandZipFile(proxyZipFile);
        assertThat(expandZipFile.exists(), is(true));
        final File apiproxy = new File(expandZipFile, "apiproxy");
        assertThat(apiproxy.exists(), is(true));
        assertThat(apiproxy.isDirectory(), is(true));
    }

    @Test
    public void testCompressedZipFile() throws Exception {
        final ProxyZipFileHandler handler = new ProxyZipFileHandler(proxyZipFile);
        final File apiproxy = new File(handler.proxyDir, "apiproxy");
        assertThat(apiproxy.exists(), is(true));
        assertThat(apiproxy.isDirectory(), is(true));
        final String data = "Hello World From Text!!";
        FileUtils.writeStringToFile(new File(apiproxy, "test.txt"), data);
        final File instrumentedZipFile = handler.buildInstrumentedZipFile();
        assertThat(instrumentedZipFile.exists(), is(true));
        assertThat(instrumentedZipFile.isFile(), is(true));
        final URL jarUrl =
                new URL("jar:file:"+instrumentedZipFile.getAbsolutePath()+"!/apiproxy/test.txt");
        assertThat(IOUtils.readLines(jarUrl.openStream()).get(0), is(data));
    }
}