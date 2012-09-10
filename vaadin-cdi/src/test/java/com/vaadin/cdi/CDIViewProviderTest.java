package com.vaadin.cdi;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;

/**
 *
 * @author adam-bien.com
 */
public class CDIViewProviderTest {

    CDIViewProvider cut;
    
    @Before
    public void initialize(){
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
