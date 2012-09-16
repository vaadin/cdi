package com.vaadin.cdi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class CDIUIProviderTest {

    CDIUIProvider cut;

    @Before
    public void initialize() {
        cut = new CDIUIProvider();
    }

    @Test
    public void uriWithJustUINoEndingSlash() {
        String origin = "http://localhost:8080/hello-cdi/uIWithViewUI";
        String expected = "uIWithViewUI";
        String actual = cut.parseUIMapping(origin);
        assertThat(actual, is(expected));
    }

}
