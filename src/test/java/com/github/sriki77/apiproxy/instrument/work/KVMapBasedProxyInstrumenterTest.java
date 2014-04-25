package com.github.sriki77.apiproxy.instrument.work;

import com.github.sriki77.apiproxy.instrument.io.ProxyFileHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KVMapBasedProxyInstrumenterTest {

    private ProxyFileHandler proxyFileHandler;
    private KVMapBasedProxyInstrumenter instrumenter;

    @Before
    public void setUp() throws Exception {
        proxyFileHandler = mock(ProxyFileHandler.class);
        instrumenter = new KVMapBasedProxyInstrumenter();
    }

    @Test
    public void shouldInstrumentProxyEndpoints() {
        instrumenter.instrumentProxyEndpoints(proxyFileHandler);
    }

    @After
    public void tearDown() throws Exception {
    }
}