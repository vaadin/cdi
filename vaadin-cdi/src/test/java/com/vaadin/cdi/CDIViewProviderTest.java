package com.vaadin.cdi;

import com.vaadin.cdi.CDIViewProvider;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.cdi.views.OneAndOnlyView;

public class CDIViewProviderTest {

    CDIViewProvider cut;

    @Before
    public void init() {
        this.cut = new CDIViewProvider();
    }

    @Test
    public void extractViewName() {
        String expected = "oneAndOnlyView";
        String actual = this.cut.evaluateViewName(new OneAndOnlyView());
        assertThat(actual, is(expected));
    }

}
