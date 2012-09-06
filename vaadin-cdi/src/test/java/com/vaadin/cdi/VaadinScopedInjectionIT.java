package com.vaadin.cdi;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;
import static com.vaadin.cdi.ArchiveProvider.*;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
/**
 *
 * @author adam-bien.com
 */
@RunWith(Arquillian.class)
public class VaadinScopedInjectionIT {

    @Inject
    VaadinScopedBean scoped;

    @Inject
    VaadinScopedBean anotherScoped;
    
    
    
    @Deployment
    public static JavaArchive deploy(){
        return createJavaArchive(VaadinScopedBean.class);
    }
    

    @Test
    public void scopedIsScoped(){
        assertTrue(scoped == anotherScoped);
    }

    @Test
    public void allExistOnlyOnce(){
        assertThat(VaadinScopedBean.COUNTER.get(),is(1));
    }
}
