package com.github.sriki77.apiproxy.instrument;

import com.github.sriki77.apiproxy.instrument.io.ProxyDirectoryHandler;
import com.github.sriki77.apiproxy.instrument.model.Endpoint;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class KVMapBasedProxyInstrumenterTest {

    private KVMapBasedProxyInstrumenter instrumenter;

    private File testTempDir;
    private ProxyDirectoryHandler handler;

    @Before
    public void setUp() throws Exception {
        testTempDir = FileUtils.getTempDirectory();
        FileUtils.forceMkdir(testTempDir);
        FileUtils.copyDirectory(new File(getClass().getResource("/profile_test").getFile()), testTempDir);
        handler = new ProxyDirectoryHandler(testTempDir);
        instrumenter = new KVMapBasedProxyInstrumenter(handler.getEndpoints().subList(0,1));
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(testTempDir);
    }

    @Test
    public void shouldInstrumentProxyEndpoints() {
        final List<Endpoint> instrument = instrumenter.instrument();
        instrument.forEach(handler::updateEndpoint);
    }

}