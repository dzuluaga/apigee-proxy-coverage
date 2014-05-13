package com.github.sriki77.apiproxy.instrument.io;

import com.github.sriki77.apiproxy.instrument.model.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.commons.io.FileUtils;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProxyDirectoryHandler implements ProxyFileHandler {
    private File apiProxyDir;
    private File proxyFilesDir;
    private File targetFilesDir;
    private XStream xStream;
    private DocumentBuilder builder;
    private Transformer transformer;
    protected File proxyDir;
    private File policyDir;

    public ProxyDirectoryHandler(File proxyDir) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        initProxyRelatedDIrectories(proxyDir);
        initXMLInfra();
    }

    private void initXMLInfra() throws ParserConfigurationException, TransformerConfigurationException {
        xStream = xStreamInit();
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        transformer = TransformerFactory.newInstance().newTransformer();
    }

    private void initProxyRelatedDIrectories(File proxyDir) {
        this.proxyDir = proxyDir;
        apiProxyDir = apiProxyDir(proxyDir);
        proxyFilesDir = getDirNamed("proxies");
        targetFilesDir = getDirNamed("targets");
        policyDir = getDirNamed("policies");
    }

    private XStream xStreamInit() {
        final XStream stream = new XStream(new StaxDriver());
        stream.processAnnotations(new Class[]{ProxyEndpoint.class,
                Endpoint.class, FaultRule.class,
                FaultRules.class, Flow.class, FlowSteps.class,
                RequestFlow.class, ResponseFlow.class, Step.class,
                TargetEndpoint.class});
        return stream;
    }

    private File getDirNamed(String dirName) {
        if (apiProxyDir == null) {
            return null;
        }
        final File proxies = new File(apiProxyDir, dirName);
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
    public List<Endpoint> getEndpoints() {
        return getProxyFiles().map(this::toEndpoint).collect(Collectors.toList());
    }

    private Endpoint toEndpoint(File file) {
        try {
            final Document cleanedDocument = cleanupProxyFile(file);
            final Endpoint endpoint = (Endpoint) xStream.fromXML(toString(cleanedDocument));
            endpoint.init(file, builder.parse(file));
            return endpoint;
        } catch (Exception e) {
            System.err.println("Failed Processing File: " + file);
            throw new RuntimeException(e);
        }
    }

    private String toString(Document cleanedDocument) throws TransformerException {
        final StringWriter cleanedXml = new StringWriter();
        transformer.transform(new DOMSource(cleanedDocument), new StreamResult(cleanedXml));
        return cleanedXml.toString();
    }

    private Document cleanupProxyFile(File file) throws IOException, SAXException, TransformerException {
        final Document document = builder.parse(file);
        cleanupNode(document);
        return document;
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
        return Stream.concat(getFilesFrom(proxyFilesDir), getFilesFrom(targetFilesDir));
    }

    private Stream<File> getFilesFrom(File filesDir) {
        if (filesDir == null) {
            return Stream.empty();
        }
        return Arrays.stream(filesDir.listFiles((dir, name) -> name.endsWith(".xml")));
    }

    @Override
    public void updateEndpoint(Endpoint endpoint) {
        try {
            transformer.transform(new DOMSource(endpoint.getNode()), new StreamResult(endpoint.getXmlFile()));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        final List<PolicyUpdate> updates = endpoint.updates();
        updates.forEach(this::createFile);
    }

    private void createFile(PolicyUpdate u) {
        try {
            final File policyName = new File(policyDir, u.name + ".xml");
            FileUtils.writeStringToFile(policyName, u.policyData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
    }
}
