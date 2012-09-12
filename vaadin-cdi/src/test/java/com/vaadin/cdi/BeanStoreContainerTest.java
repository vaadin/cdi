package com.vaadin.cdi;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author adam-bien.com
 */
public class BeanStoreContainerTest {

    BeanStoreContainer cut;

    @Before
    public void init() {
        cut = new BeanStoreContainer();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullWithNull() {
        cut.getBeanStore(null);
    }
}
