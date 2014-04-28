package com.github.sriki77.apiproxy.instrument.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ProxyDirectoryHandlerTest {

    private File proxyDir;
    private ProxyDirectoryHandler handler;
    private File apiProxyDir;
    private File proxiesDir;
    private File targetsDir;

    @Before
    public void setUp() throws Exception {
        proxyDir = new File(getClass().getResource("/profile_test").getFile());
        apiProxyDir = new File(proxyDir, "apiproxy");
        proxiesDir = new File(apiProxyDir, "proxies");
        targetsDir = new File(apiProxyDir, "targets");
        handler = new ProxyDirectoryHandler(proxyDir);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shouldReturnStreamOfProxyFiles() throws Exception {
        final Stream<File> proxyFiles = handler.getProxyFiles();
        List<File> allfiles = new ArrayList<>();
        allfiles.addAll(Arrays.asList(proxiesDir.listFiles()));
        allfiles.addAll(Arrays.asList(targetsDir.listFiles()));
        final List<File> actualList = proxyFiles.collect(Collectors.toList());
        assertThat(actualList.size(), is(allfiles.size()));
        assertThat(actualList, is(allfiles));
    }
}