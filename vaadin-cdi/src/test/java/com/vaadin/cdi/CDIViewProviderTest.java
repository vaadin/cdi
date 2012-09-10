package com.vaadin.cdi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author adam-bien.com
 */
public class CDIViewProviderTest {

    CDIViewProvider cut;

    @Before
    public void initialize() {
        cut = new CDIViewProvider();
    }

    @Test
    public void normalizeLowerFirstCase() {
        String origin = "LoginPage";
        String expected = "loginPage";
        String actual = cut.normalize(origin);
        assertThat(actual, is(expected));
    }

    @Test
    public void normalizeNothingToDo() {
        String origin = "loginPage";
        String expected = "loginPage";
        String actual = cut.normalize(origin);
        assertThat(actual, is(expected));
    }
}
