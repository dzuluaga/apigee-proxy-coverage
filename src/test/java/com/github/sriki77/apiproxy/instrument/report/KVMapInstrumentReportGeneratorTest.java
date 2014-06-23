package com.github.sriki77.apiproxy.instrument.report;

import com.github.sriki77.apiproxy.instrument.io.ProxyDirectoryHandler;
import com.github.sriki77.apiproxy.instrument.model.Endpoint;
import com.github.sriki77.apiproxy.instrument.model.Step;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class KVMapInstrumentReportGeneratorTest {

    private File testTempDir;
    private ProxyDirectoryHandler handler;
    private File instrumentFile;

    @Before
    public void setUp() throws Exception {
        testTempDir = FileUtils.getTempDirectory();
        FileUtils.forceMkdir(testTempDir);
        FileUtils.copyDirectory(new File(getClass().getResource("/profile_test").getFile()), testTempDir);
        FileUtils.copyFileToDirectory(new File(getClass().getResource("/kvm_instrument_test.txt").getFile()), testTempDir);

        handler = new ProxyDirectoryHandler(testTempDir);
        instrumentFile = new File(testTempDir, "kvm_instrument_test.txt");
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteQuietly(testTempDir);
    }

    @Test
    public void noStepIsExecutedForMapAccessToken() throws IOException {
        final KVMapInstrumentReportGenerator generator = new KVMapInstrumentReportGenerator(handler.proxyName(), instrumentFile, testTempDir);
        Endpoint e = getEndpoint("map_getAccessToken");
        generator.generateReport(e);
        final List<Step> steps = getAllSteps(e);
        for (Step step : steps) {
            assertThat(step.isExecuted(), is(false));
        }
    }

    @Test
    public void userCreateTargetStepShouldBeExecutedForOAuthEndpoint() throws IOException {
        final KVMapInstrumentReportGenerator generator = new KVMapInstrumentReportGenerator(handler.proxyName(), instrumentFile, testTempDir);
        Endpoint e = getEndpoint("oauth2");
        generator.generateReport(e);
        final List<Step> steps = getAllSteps(e);
        for (Step step : steps) {
            assertThat(step.isExecuted(), is(step.getName().equals("assign_set_user_create_target")));
        }
    }

    @Test
    public void faultNoLocaleShouldBeExecutedForPartnerEndpoint() throws IOException {
        final KVMapInstrumentReportGenerator generator = new KVMapInstrumentReportGenerator(handler.proxyName(), instrumentFile, testTempDir);
        Endpoint e = getEndpoint("partner");
        generator.generateReport(e);
        final List<Step> steps = getAllSteps(e);
        for (Step step : steps) {
            assertThat(step.isExecuted(), is(step.getName().equals("fault_no_locale")));
        }
    }

    @Test
    public void shouldGenerateReportForEveryEndpoint() throws IOException {
        final KVMapInstrumentReportGenerator generator = new KVMapInstrumentReportGenerator(handler.proxyName(), instrumentFile, testTempDir);
        Endpoint e = getEndpoint("partner");
        generator.generateReport(e);
        assertThat(new File(testTempDir, e.endpointType() + "_" + e.getXmlFile().getName()).exists(), is(true));
        e = getEndpoint("oauth2");
        generator.generateReport(e);
        assertThat(new File(testTempDir, e.endpointType() + "_" + e.getXmlFile().getName()).exists(), is(true));
    }


    private Endpoint getEndpoint(String name) {
        return handler.getEndpoints().stream().filter(e -> name.equals(e.getName())).findFirst().get();
    }

    private List<Step> getAllSteps(Endpoint e) {
        List<Step> steps = new ArrayList<>();
        e.getFaultRules().getFaultRules().forEach(f -> steps.addAll(f.getSteps()));
        steps.addAll(e.getPreflow().getRequestFlow().getSteps());
        steps.addAll(e.getPreflow().getResponseFlow().getSteps());
        steps.addAll(e.getPostflow().getRequestFlow().getSteps());
        steps.addAll(e.getPostflow().getResponseFlow().getSteps());
        e.getFlows().getFlows().forEach(f -> {
            steps.addAll(f.getRequestFlow().getSteps());
            steps.addAll(f.getResponseFlow().getSteps());
        });
        return steps;
    }

}