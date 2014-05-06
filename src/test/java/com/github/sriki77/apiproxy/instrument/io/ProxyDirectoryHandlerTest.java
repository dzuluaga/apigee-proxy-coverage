package com.github.sriki77.apiproxy.instrument.io;

import com.github.sriki77.apiproxy.instrument.model.*;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathValuesEqual;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathValuesNotEqual;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ProxyDirectoryHandlerTest {

    private File testTempDir;
    private File proxyDir;
    private ProxyDirectoryHandler handler;
    private File apiProxyDir;
    private File proxiesDir;
    private File targetsDir;

    @Before
    public void setUp() throws Exception {
        testTempDir = FileUtils.getTempDirectory();
        FileUtils.forceMkdir(testTempDir);
        FileUtils.copyDirectory(new File(getClass().getResource("/profile_test").getFile()), testTempDir);
        proxyDir = testTempDir;
        apiProxyDir = new File(proxyDir, "apiproxy");
        proxiesDir = new File(apiProxyDir, "proxies");
        targetsDir = new File(apiProxyDir, "targets");
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
        final Stream<Endpoint> endpoints = handler.getEndpoints();
        List<File> allfiles = new ArrayList<>();
        allfiles.addAll(Arrays.asList(proxiesDir.listFiles()));
        allfiles.addAll(Arrays.asList(targetsDir.listFiles()));
        final List<Endpoint> endpointList = endpoints.filter(e -> e != null).collect(Collectors.toList());
        assertThat(endpointList.size(), is(allfiles.size()));
    }

    @Test
    public void shouldUpdateFaultRulesInTheGivenEndpoint() throws Exception {
        final Optional<Endpoint> optEndpoint = handler.getEndpoints().findFirst();
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
        final Optional<Endpoint> optEndpoint = handler.getEndpoints().findFirst();
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

    private Document toDocument(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
    }
}