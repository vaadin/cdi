package com.vaadin.cdi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vaadin.cdi.uis.EmptyUI;

@RunWith(Arquillian.class)
public class UIScopedContextIT {

    @Deployment
    public static WebArchive deploy() {
        return ArchiveProvider.createWebArchive(EmptyUI.class);
    }

    @Before
    public void resetCounter() {
        EmptyUI.resetCounter();
    }

    @Test
    public void invokeUIPage() {
        String content = Site
                .getContent("http://localhost:8080/vaadinextension/emptyUI");
        assertNotNull(content);
        System.out.println(content);
        assertThat(EmptyUI.getNumberOfInstances(), is(1));
        Site.getContent("http://localhost:8080/vaadinextension/emptyUI");
        assertThat(EmptyUI.getNumberOfInstances(), is(1));
    }

}
