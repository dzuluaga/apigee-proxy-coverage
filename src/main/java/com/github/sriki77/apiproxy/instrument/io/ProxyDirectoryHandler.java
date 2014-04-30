package com.github.sriki77.apiproxy.instrument.io;

import com.github.sriki77.apiproxy.instrument.model.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Stream;

public class ProxyDirectoryHandler implements ProxyFileHandler {
    private File apiProxyDir;
    private File proxyFilesDir;
    private File targetFilesDir;
    private XStream xStream;
    private DocumentBuilder builder;
    private Transformer transformer;

    public ProxyDirectoryHandler(File proxyDir) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        initProxyRelatedDIrectories(proxyDir);
        initXMLInfra();
    }

    private void initXMLInfra() throws ParserConfigurationException, TransformerConfigurationException {
        builder = initDocBuilder();
        transformer = initTransformer();
    }

    private void initProxyRelatedDIrectories(File proxyDir) {
        apiProxyDir = apiProxyDir(proxyDir);
        proxyFilesDir = proxyFilesDir();
        targetFilesDir = targetFilesDir();
        xStream = xStreamInit();
    }

    private Transformer initTransformer() throws TransformerConfigurationException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        return transformer;
    }

    private DocumentBuilder initDocBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder();
    }

    private XStream xStreamInit() {
        final XStream stream = new XStream(new StaxDriver());
        stream.processAnnotations(new Class[]{ProxyEndpoint.class, Endpoint.class, FaultRule.class,
                FaultRules.class, Flow.class, FlowSteps.class, RequestFlow.class, ResponseFlow.class, Step.class,
                TargetEndpoint.class});
        return stream;
    }

    private File targetFilesDir() {
        if (apiProxyDir == null) {
            return null;
        }
        final File proxies = new File(apiProxyDir, "targets");
        if (!proxies.exists()) {
            return null;
        }
        return proxies;
    }

    private File proxyFilesDir() {
        if (apiProxyDir == null) {
            return null;
        }
        final File proxies = new File(apiProxyDir, "proxies");
        if (!proxies.exists()) {
            return null;
        }
        return proxies;
    }

    private File apiProxyDir(File proxyDir) {
        final File apiproxy = new File(proxyDir, "apiproxy");
        if (!apiproxy.exists()) {
            return null;
        }
        return apiproxy;
    }

    @Override
    public Stream<Endpoint> getEndpoints() {
        return getProxyFiles().map(this::toEndpoint);
    }

    private Endpoint toEndpoint(File file) {
        try {
            return (Endpoint) xStream.fromXML(cleanupProxyFile(file));
        } catch (Exception e) {
            System.err.println("Failed Processing File: " + file);
            throw new RuntimeException(e);
        }
    }

    private String cleanupProxyFile(File file) throws IOException, SAXException, TransformerException {
        final Document document = builder.parse(file);
        cleanupNode(document);
        final StringWriter cleanedXml = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(cleanedXml));
        return cleanedXml.toString();
    }

    private void cleanupNode(Node node) {
        final NodeList topChildren = node.getChildNodes();
        for (int i = 0; i < topChildren.getLength(); i++) {
            final Node n = topChildren.item(i);
            NodeCleaner.clean(n);
            cleanupNode(n);
        }
    }

    Stream<File> getProxyFiles() {
        return Stream.concat(getProxyDirFiles(), getTargetDirFiles());
    }

    private Stream<File> getTargetDirFiles() {
        if (targetFilesDir == null) {
            return Stream.empty();
        }
        return Arrays.stream(targetFilesDir.listFiles((dir, name) -> name.endsWith(".xml")));
    }

    private Stream<File> getProxyDirFiles() {
        if (proxyFilesDir == null) {
            return Stream.empty();
        }
        return Arrays.stream(proxyFilesDir.listFiles((dir, name) -> name.endsWith(".xml")));
    }

    @Override
    public void updateEndpoint(Endpoint endpoint) {

    }

}
