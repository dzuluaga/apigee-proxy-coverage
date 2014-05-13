package com.github.sriki77.apiproxy.instrument.io;

import com.github.sriki77.apiproxy.instrument.model.*;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.custommonkey.xmlunit.XMLAssert.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ProxyDirectoryHandlerTest {

    private File testTempDir;
    private File proxyDir;
    private ProxyDirectoryHandler handler;
    private File apiProxyDir;
    private File proxiesDir;
    private File targetsDir;
    private File policyDir;

    @Before
    public void setUp() throws Exception {
        testTempDir = FileUtils.getTempDirectory();
        FileUtils.forceMkdir(testTempDir);
        FileUtils.copyDirectory(new File(getClass().getResource("/profile_test").getFile()), testTempDir);
        proxyDir = testTempDir;
        apiProxyDir = new File(proxyDir, "apiproxy");
        proxiesDir = new File(apiProxyDir, "proxies");
        targetsDir = new File(apiProxyDir, "targets");
        policyDir = new File(apiProxyDir, "policies");
        handler = new ProxyDirectoryHandler(proxyDir);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(testTempDir);
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


    @Test
    public void shouldReturnStreamOfEndpoints() throws Exception {
        final List<Endpoint> endpoints = handler.getEndpoints();
        List<File> allfiles = new ArrayList<>();
        allfiles.addAll(Arrays.asList(proxiesDir.listFiles()));
        allfiles.addAll(Arrays.asList(targetsDir.listFiles()));
        final List<Endpoint> endpointList = endpoints.stream().filter(e -> e != null).collect(Collectors.toList());
        assertThat(endpointList.size(), is(allfiles.size()));
    }

    @Test
    public void shouldUpdateFaultRulesInTheGivenEndpoint() throws Exception {
        final Optional<Endpoint> optEndpoint = handler.getEndpoints().stream().findFirst();
        assertThat(optEndpoint.isPresent(), is(true));
        final Endpoint endpoint = optEndpoint.get();
        File xmlFile = endpoint.getXmlFile();
        String xmlBefore = FileUtils.readFileToString(xmlFile);
        final FaultRule faultRule = endpoint.getFaultRules().getFaultRules().get(0);
        final List<Step> steps = faultRule.getSteps();
        final Step cloneStep = faultRule.cloneStep(steps.get(0));
        final String dummyName = "Dummy";
        cloneStep.setName(dummyName);

        handler.updateEndpoint(endpoint);
        String xmlAfter = FileUtils.readFileToString(xmlFile);
        assertXpathEvaluatesTo(dummyName, "//FaultRules/FaultRule[1]/Step[1]/Name", xmlAfter);
        assertXpathValuesNotEqual("//FaultRules", xmlBefore, "//FaultRules", xmlAfter);
        assertXpathValuesEqual("//FaultRules/FaultRule[1]/Step[1]", xmlBefore, "//FaultRules/FaultRule[1]/Step[2]", xmlAfter);
        assertXpathValuesEqual("//FaultRules/FaultRule[1]/Step[2]", xmlBefore, "//FaultRules/FaultRule[1]/Step[3]", xmlAfter);
        assertXpathValuesEqual("//Flows", xmlBefore, "//Flows", xmlAfter);
        assertXpathValuesEqual("//PreFlow", xmlBefore, "//PreFlow", xmlAfter);
        assertXpathValuesEqual("//PostFlow", xmlBefore, "//PostFlow", xmlAfter);
        assertXpathValuesEqual("//HTTPProxyConnection", xmlBefore, "//HTTPProxyConnection", xmlAfter);
        assertXpathValuesEqual("//RouteRule", xmlBefore, "//RouteRule", xmlAfter);

    }

    @Test
    public void shouldUpdatePreFlowInTheGivenEndpoint() throws Exception {
        final Optional<Endpoint> optEndpoint = handler.getEndpoints().stream().findFirst();
        assertThat(optEndpoint.isPresent(), is(true));
        final Endpoint endpoint = optEndpoint.get();
        File xmlFile = endpoint.getXmlFile();
        String xmlBefore = FileUtils.readFileToString(xmlFile);
        final FlowSteps flowSteps = endpoint.getPreflow().getRequestFlow();
        final List<Step> steps = flowSteps.getSteps();
        final Step cloneStep = flowSteps.cloneStep(steps.get(0));
        final String dummyName = "Dummy";
        cloneStep.setName(dummyName);

        handler.updateEndpoint(endpoint);
        String xmlAfter = FileUtils.readFileToString(xmlFile);
        assertXpathValuesNotEqual("//PreFlow", xmlBefore, "//PreFlow", xmlAfter);
        assertXpathEvaluatesTo(dummyName, "//PreFlow/Request[1]/Step[1]/Name", xmlAfter);
        assertXpathValuesEqual("//PreFlow/Request[1]/Step[1]", xmlBefore, "//PreFlow/Request[1]/Step[2]", xmlAfter);
        assertXpathValuesEqual("//FaultRules", xmlBefore, "//FaultRules", xmlAfter);
        assertXpathValuesEqual("//Flows", xmlBefore, "//Flows", xmlAfter);
        assertXpathValuesEqual("//PostFlow", xmlBefore, "//PostFlow", xmlAfter);
        assertXpathValuesEqual("//HTTPProxyConnection", xmlBefore, "//HTTPProxyConnection", xmlAfter);
        assertXpathValuesEqual("//RouteRule", xmlBefore, "//RouteRule", xmlAfter);

    }

    @Test
    public void shouldUpdatePolicyInTheGivenEndpoint() throws Exception {
        final Optional<Endpoint> optEndpoint = handler.getEndpoints().stream().findFirst();
        assertThat(optEndpoint.isPresent(), is(true));
        final Endpoint endpoint = optEndpoint.get();
        final String policyName = "test-policy";
        final String policyData = "Hello test";
        final PolicyUpdate policyUpdate = new PolicyUpdate(policyName, policyData);
        endpoint.addUpdate(policyUpdate);
        handler.updateEndpoint(endpoint);
        final Optional<File> policyFile = Stream.of(policyDir.listFiles()).filter(e -> e.getName().startsWith(policyName)).findFirst();
        assertThat(policyFile.isPresent(), is(true));
        assertThat(FileUtils.readFileToString(policyFile.get()), is("Hello test"));


    }

    @Test
    public void shouldReturnCorrectLocationOfStep() throws Exception {
        final Optional<Endpoint> optEndpoint = handler.getEndpoints().stream().findFirst();
        assertThat(optEndpoint.isPresent(), is(true));
        final Endpoint endpoint = optEndpoint.get();
        final FaultRule faultRule = endpoint.getFaultRules().getFaultRules().get(0);
        String location = faultRule.getSteps().get(0).location();
        assertThat(location.contains("Proxy:map_getAccessToken"), is(true));
        assertThat(location.contains("map_getAccessToken.xml"), is(true));
        assertThat(location.contains("FaultRule: FaultHandler"), is(true));
        assertThat(location.contains("Policy: js_setup_splunk_vars"), is(true));

        location = endpoint.getPreflow().getRequestFlow().getSteps().get(0).location();
        assertThat(location.contains("Proxy:map_getAccessToken"), is(true));
        assertThat(location.contains("map_getAccessToken.xml"), is(true));
        assertThat(location.contains("PreFlow:RequestFlow"), is(true));
        assertThat(location.contains("Policy: js_set_flow_resource_name"), is(true));

        location = endpoint.getPostflow().getResponseFlow().getSteps().get(0).location();
        assertThat(location.contains("Proxy:map_getAccessToken"), is(true));
        assertThat(location.contains("map_getAccessToken.xml"), is(true));
        assertThat(location.contains("PostFlow:ResponseFlow"), is(true));
        assertThat(location.contains("Policy: js_setup_splunk_vars"), is(true));

        location = endpoint.getFlows().getFlows().get(0).getRequestFlow().getSteps().get(0).location();
        assertThat(location.contains("Proxy:map_getAccessToken"), is(true));
        assertThat(location.contains("map_getAccessToken.xml"), is(true));
        assertThat(location.contains("map_getAccessToken:RequestFlow"), is(true));
        assertThat(location.contains("Policy: keymap_get_auth_salt"), is(true));


        location = endpoint.getFlows().getFlows().get(0).getResponseFlow().getSteps().get(0).location();
        assertThat(location.contains("Proxy:map_getAccessToken"), is(true));
        assertThat(location.contains("map_getAccessToken.xml"), is(true));
        assertThat(location.contains("map_getAccessToken:ResponseFlow"), is(true));
        assertThat(location.contains("Policy: js_alter_token_exp_for_response"), is(true));


    }

}