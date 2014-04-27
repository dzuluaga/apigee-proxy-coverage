package com.github.sriki77.apiproxy.instrument.io;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ProxyZipFileHandlerTest {

    @Test
    public void testExpandZipFile() throws Exception {
        File zipFile = new File(getClass().getResource("/profile_test.zip").getFile());
        final File expandZipFile = ProxyZipFileHandler.expandZipFile(zipFile);
        assertThat(expandZipFile.exists(), is(true));
        final File apiproxy = new File(expandZipFile, "apiproxy");
        assertThat(apiproxy.exists(), is(true));
        assertThat(apiproxy.isDirectory(), is(true));
        System.err.println(expandZipFile);
    }
}