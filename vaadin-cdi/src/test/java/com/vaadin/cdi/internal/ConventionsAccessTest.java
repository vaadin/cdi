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

import com.vaadin.cdi.uis.ConventionalUI;
import com.vaadin.cdi.uis.PlainColidingAlternativeUI;
import com.vaadin.cdi.uis.UnannotatedUI;
import com.vaadin.cdi.views.OneAndOnlyViewWithPath;
import com.vaadin.cdi.views.OneAndOnlyViewWithoutPath;
import com.vaadin.cdi.views.OneAndOnlyViewWithoutPathAndAnnotation;

import org.junit.Test;

import static com.vaadin.cdi.internal.ConventionsAccess.deriveMappingForUI;
import static com.vaadin.cdi.internal.ConventionsAccess.deriveMappingForView;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 */
public class ConventionsAccessTest {

    private final DefaultConventions conventions = new DefaultConventions();

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
        final String uiPath = deriveMappingForUI(UnannotatedUI.class);
        assertNull(uiPath);
    }
    
    @Test
    public void upperCamelCaseToLowerHyphenatedTest() {
        String original = "AlphaBetaGamma";
        String expected = "alpha-beta-gamma";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "alphaBetaGamma";
        expected = "alpha-beta-gamma";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "";
        expected = "";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "a";
        expected = "a";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "A";
        expected = "a";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "ABC";
        expected = "abc";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "alllowercase";
        expected = "alllowercase";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "main/sub";
        expected = "main/sub";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "MyCDITest";
        expected = "my-cdi-test";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "MyATest";
        expected = "my-a-test";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "aB";
        expected = "a-b";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "Ab";
        expected = "ab";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));

        original = "MyCDI";
        expected = "my-cdi";
        assertThat(conventions.upperCamelToLowerHyphen(original), is(expected));
    }

}
