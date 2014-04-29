package com.github.sriki77.apiproxy.instrument.io;

import com.github.sriki77.apiproxy.instrument.model.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
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
    private File proxyDir;
    private final File workingDir;
    private DocumentBuilder builder;
    private Transformer transformer;

    public ProxyDirectoryHandler(File proxyDir) throws IOException, ParserConfigurationException, TransformerConfigurationException {
        this.proxyDir = proxyDir;
        initProxyRelatedDIrectories(proxyDir);
        workingDir = Files.createTempDirectory("proxy-working-dir").toFile();
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
        StreamSource xslSource = new StreamSource(getClass().getResourceAsStream("/non-flow-tags-remover.xsl"));
        Transformer transformer = tFactory.newTransformer(xslSource);
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

    private File cleanupProxyFile(File file) throws IOException, SAXException, TransformerException {
        FileUtils.copyFileToDirectory(file, workingDir);
        File wipFile = new File(workingDir, file.getName());
        cleanXMLInWipFile(wipFile);
        return wipFile;
    }

    private void cleanXMLInWipFile(File file) throws IOException, SAXException, TransformerException {
        final StringWriter output = new StringWriter();
        transformer.transform(new StreamSource(file), new StreamResult(output));
        FileUtils.writeStringToFile(file, output.toString());
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
