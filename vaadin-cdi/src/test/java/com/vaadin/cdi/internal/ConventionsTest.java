/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.cdi.internal;

import static com.vaadin.cdi.internal.Conventions.deriveMappingForUI;
import static com.vaadin.cdi.internal.Conventions.deriveMappingForView;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.vaadin.cdi.uis.ConventionalUI;
import com.vaadin.cdi.uis.PlainColidingAlternativeUI;
import com.vaadin.cdi.views.OneAndOnlyViewWithPath;
import com.vaadin.cdi.views.OneAndOnlyViewWithoutPath;
import com.vaadin.cdi.views.OneAndOnlyViewWithoutPathAndAnnotation;

/**
 */
public class ConventionsTest {

    @Test
    public void extractViewNameUsingPath() {
        String expected = "customTest";
        String actual = deriveMappingForView(OneAndOnlyViewWithPath.class);
        assertThat(actual, is(expected));
    }

    @Test
    public void extractViewNameUsingConvention() {
        String expected = "one-and-only-view-without-path";
        String actual = deriveMappingForView(OneAndOnlyViewWithoutPath.class);
        assertThat(actual, is(expected));
    }

    @Test
    public void extractViewNameUsingConventionWithoutAnnotation() {
        String expected = null;
        String actual = deriveMappingForView(OneAndOnlyViewWithoutPathAndAnnotation.class);
        assertThat(actual, is(expected));
    }

    @Test
    public void extractUIPathUsingConvention() {
        String expected = "conventional";
        String actual = deriveMappingForUI(ConventionalUI.class);
        assertThat(actual, is(expected));
    }

    @Test
    public void extractUIPathUsingAnnotation() {
        String expected = "PlainUI";
        String actual = deriveMappingForUI(PlainColidingAlternativeUI.class);
        assertThat(actual, is(expected));
    }

    @Test
    public void uiAnnotationNotPresent() {
        final String uiPath = deriveMappingForUI(String.class);
        assertNull(uiPath);
    }
    
    @Test
    public void upperCamelCaseToLowerHyphenatedTest() {
        String original = "AlphaBetaGamma";
        String expected = "alpha-beta-gamma";
        assertThat(Conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "alphaBetaGamma";
        expected = "alpha-beta-gamma";
        assertThat(Conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "";
        expected = "";
        assertThat(Conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "a";
        expected = "a";
        assertThat(Conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "A";
        expected = "a";
        assertThat(Conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "ABC";
        expected = "a-b-c";
        assertThat(Conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "alllowercase";
        expected = "alllowercase";
        assertThat(Conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "main/sub";
        expected = "main/sub";
        assertThat(Conventions.upperCamelToLowerHyphen(original), is(expected));
    }

}
