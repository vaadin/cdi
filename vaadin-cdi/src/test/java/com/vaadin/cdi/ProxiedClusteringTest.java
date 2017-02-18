package com.vaadin.cdi;

import com.vaadin.cdi.internal.ClusterIncTestLayout;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.UIScopedIncUI;
import com.vaadin.cdi.uis.ViewScopedIncUI;
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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@Category(ClusterTestCategory.class)
public class ProxiedClusteringTest extends AbstractCDIIntegrationTest {

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

    private ReverseProxy proxy;

    public static WebArchive getArchive() {
        WebArchive archive = ArchiveProvider.createWebArchive("clusterinctest",
                UIScopedIncUI.class, ViewScopedIncUI.class, ClusterIncTestLayout.class);
        WebAppDescriptor webAppDescriptor = Descriptors.create(WebAppDescriptor.class).distributable();
        archive.addAsWebInfResource(new StringAsset(webAppDescriptor.exportAsString()),
                webAppDescriptor.getDescriptorName());
        return archive;
    }

    @Before
    public void setUp() throws Exception {
        if (ReverseProxy.INSTANCE == null) {
            ReverseProxy.INSTANCE = new ReverseProxy(url1, url2);
        }
        proxy = ReverseProxy.INSTANCE;
        proxy.setSelectedHost(0);
    }

    @AfterClass
    public static void tearDownClass() {
        ReverseProxy.INSTANCE.stop();
    }

    @Test
    public void testUICounter() throws Exception {
        String path = Conventions.deriveMappingForUI(UIScopedIncUI.class);
        firstWindow.navigate().to(proxy.getUrl() + path);
        waitForClient();

        doCount();
    }

    @Test
    public void testViewCounter() throws Exception {
        String path = Conventions.deriveMappingForUI(ViewScopedIncUI.class);
        firstWindow.navigate().to(proxy.getUrl() + path);
        waitForClient();
        clickAndWait(ViewScopedIncUI.NAVBTN_ID);

        doCount();
    }

    private void doCount() {
        String[] portsArr = new String[]{String.valueOf(url1.getPort()), String.valueOf(url2.getPort())};
        for (int i = 1; i < 5; i++) {
            proxy.setSelectedHost( i % 2);
            clickAndWait(ClusterIncTestLayout.INC_BUTTON_ID);
            assertEquals(String.valueOf(i), findElement(ClusterIncTestLayout.NORMALVALUE_LABEL_ID).getText());
            assertEquals(String.valueOf(i), findElement(ClusterIncTestLayout.VALUE_LABEL_ID).getText());
            assertEquals(portsArr[proxy.getSelectedHost()], findElement(ClusterIncTestLayout.PORT_LABEL_ID).getText());
        }
    }

    private static class ReverseProxy {

        static ReverseProxy INSTANCE;

        private int selectedHost = 0;
        private final Undertow reverseProxy;

        private String contextRoot;

        public ReverseProxy(URL url1, URL url2) throws URISyntaxException, MalformedURLException {
            LoadBalancingProxyClient loadBalancer = new LoadBalancingProxyClient(
                    UndertowClient.getInstance(), null, new HostSelectorImpl());
            loadBalancer
                    .addHost(new URL(url1.getProtocol(), url1.getHost(), url1.getPort(), "").toURI())
                    .addHost(new URL(url2.getProtocol(), url2.getHost(), url2.getPort(), "").toURI());
            reverseProxy = Undertow.builder()
                    .addHttpListener(0, "localhost")
                    .setHandler(new ProxyHandler(loadBalancer, ResponseCodeHandler.HANDLE_404))
                    .build();
            reverseProxy.start();
            assert url1.getPath() == url2.getPath();
            contextRoot = url1.getPath();
        }

        public void stop() {
            reverseProxy.stop();
        }

        public int getSelectedHost() {
            return selectedHost;
        }

        public void setSelectedHost(int selectedHost) {
            this.selectedHost = selectedHost;
        }

        public String getUrl() {
            int localPort = ((InetSocketAddress) reverseProxy.getListenerInfo().get(0).getAddress()).getPort();
            return  "http://localhost:" + localPort + contextRoot;
        }

        private class HostSelectorImpl implements LoadBalancingProxyClient.HostSelector {
            @Override
            public int selectHost(LoadBalancingProxyClient.Host[] availableHosts) {
                return selectedHost;
            }

        }

    }

}
