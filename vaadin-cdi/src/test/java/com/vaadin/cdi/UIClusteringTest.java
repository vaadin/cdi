package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.UIScopedCounterUI;
import io.undertow.Undertow;
import io.undertow.client.UndertowClient;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@Category(ClusterTestCategory.class)
public class UIClusteringTest extends AbstractCDIIntegrationTest {

    private int proxyPort;
    private TestHostSelector hostSelector;

    @Deployment(name = "node1", testable = false)
    @TargetsContainer("node-1")
    public static WebArchive deployment1() {
        return getArchive();
    }

    @Deployment(name = "node2", testable = false)
    @TargetsContainer("node-2")
    public static WebArchive deployment2() {
        return getArchive();
    }

    @OperateOnDeployment("node1")
    @ArquillianResource
    private URL url1;

    @OperateOnDeployment("node2")
    @ArquillianResource
    private URL url2;

    public static WebArchive getArchive() {
        WebArchive archive = ArchiveProvider.createWebArchive("uiscopedcounter",
                UIScopedCounterUI.class);
        WebAppDescriptor webAppDescriptor = Descriptors.create(WebAppDescriptor.class).distributable();
        archive.addAsWebInfResource(new StringAsset(webAppDescriptor.exportAsString()),
                webAppDescriptor.getDescriptorName());
        return archive;
    }

    @Test
    public void testUICounter() throws Exception {
        createReverseProxy();
        String proxyUrl = "http://localhost:" + proxyPort + url1.getPath();

        String path = Conventions.deriveMappingForUI(UIScopedCounterUI.class);
        firstWindow.navigate().to(proxyUrl + path);
        waitForClient();

        String[] portsArr = new String[]{String.valueOf(url1.getPort()), String.valueOf(url2.getPort())};
        for (int i = 1; i < 10; i++) {
            hostSelector.selectedHost = i % 2;
            clickAndWait(UIScopedCounterUI.INC_BUTTON_ID);
            assertEquals(String.valueOf(i), findElement(UIScopedCounterUI.NORMALVALUE_LABEL_ID).getText());
            assertEquals(String.valueOf(i), findElement(UIScopedCounterUI.VALUE_LABEL_ID).getText());
            assertEquals(portsArr[hostSelector.selectedHost], findElement(UIScopedCounterUI.PORT_LABEL_ID).getText());
        }
    }

    private void createReverseProxy() throws URISyntaxException, MalformedURLException {
        hostSelector = new TestHostSelector();
        LoadBalancingProxyClient loadBalancer = new LoadBalancingProxyClient(
                UndertowClient.getInstance(), null, hostSelector);
        loadBalancer
                .addHost(new URL(url1.getProtocol(), url1.getHost(), url1.getPort(), "").toURI())
                .addHost(new URL(url2.getProtocol(), url2.getHost(), url2.getPort(), "").toURI());
        Undertow reverseProxy = Undertow.builder()
                .addHttpListener(0, "localhost")
                .setHandler(new ProxyHandler(loadBalancer, ResponseCodeHandler.HANDLE_404))
                .build();
        reverseProxy.start();
        proxyPort = ((InetSocketAddress) reverseProxy.getListenerInfo().get(0).getAddress()).getPort();
    }

    private class TestHostSelector implements LoadBalancingProxyClient.HostSelector {
        private int selectedHost = 0;

        @Override
        public int selectHost(LoadBalancingProxyClient.Host[] availableHosts) {
            return selectedHost;
        }

    }

}
