package com.vaadin.cdi;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.cdi.views.OneAndOnlyViewWithPath;
import com.vaadin.cdi.views.OneAndOnlyViewWithoutPath;
import com.vaadin.cdi.views.OneAndOnlyViewWithoutPathAndAnnotation;

public class CDIViewProviderTest {

    CDIViewProvider cut;

    @Before
    public void init() {
        this.cut = new CDIViewProvider();
    }

    @Test
    public void extractViewNameUsingPath() {
        String expected = "custom";
        String actual = this.cut.evaluateViewName(new OneAndOnlyViewWithPath());
        assertThat(actual, is(expected));
    }

    @Test
    public void extractViewNameUsingConvention() {
        String expected = "oneAndOnlyViewWithoutPath";
        String actual = this.cut
                .evaluateViewName(new OneAndOnlyViewWithoutPath());
        assertThat(actual, is(expected));
    }

    @Test
    public void extractViewNameUsingConventionWithoutAnnotation() {
        String expected = "oneAndOnlyViewWithoutPathAndAnnotation";
        String actual = this.cut
                .evaluateViewName(new OneAndOnlyViewWithoutPathAndAnnotation());
        assertThat(actual, is(expected));
    }

}
