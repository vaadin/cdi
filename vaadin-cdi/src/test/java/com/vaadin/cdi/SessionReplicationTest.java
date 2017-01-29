package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.SessionReplicationUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.URL;

@Category(ClusterTestCategory.class)
public class SessionReplicationTest extends AbstractCDIIntegrationTest {

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
        WebArchive archive = ArchiveProvider.createWebArchive("replication",
                SessionReplicationUI.class);
        WebAppDescriptor webAppDescriptor = Descriptors.create(WebAppDescriptor.class).distributable();
        return archive.addAsWebInfResource(new StringAsset(webAppDescriptor.exportAsString()),
                webAppDescriptor.getDescriptorName());
    }

    @Test
    public void testSessionReplication() throws Exception {
        String path = Conventions.deriveMappingForUI(SessionReplicationUI.class);
        firstWindow.navigate().to(url1.toString() + path);
        waitForClient();
        assertSessionLabelsEquals("");
        clickAndWait(SessionReplicationUI.SETVALUEBTN_ID);

        firstWindow.navigate().to(url2.toString() + path);
        waitForClient();
        assertSessionLabelsEquals(SessionReplicationUI.VALUE);
    }

    private void assertSessionLabelsEquals(String expected) {
        Assert.assertEquals(expected, findElement(SessionReplicationUI.HTTPVALUELABEL_ID).getText());
        Assert.assertEquals(expected, findElement(SessionReplicationUI.VAADINVALUELABEL_ID).getText());
        Assert.assertEquals(expected, findElement(SessionReplicationUI.CDIVALUELABEL_ID).getText());
    }

}
