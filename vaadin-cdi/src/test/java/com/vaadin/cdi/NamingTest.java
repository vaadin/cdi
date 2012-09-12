package com.vaadin.cdi;

import static com.vaadin.cdi.Naming.firstToLower;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * 
 * @author adam-bien.com
 */
public class NamingTest {

    @Test
    public void normalizeLowerFirstCase() {
        String origin = "LoginPage";
        String expected = "loginPage";
        String actual = firstToLower(origin);
        assertThat(actual, is(expected));
    }

    @Test
    public void normalizeNothingToDo() {
        String origin = "loginPage";
        String expected = "loginPage";
        String actual = firstToLower(origin);
        assertThat(actual, is(expected));
    }

}
