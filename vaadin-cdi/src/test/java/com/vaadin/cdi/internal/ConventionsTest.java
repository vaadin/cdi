/*
 * Copyright 2012 Vaadin Ltd.
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
import static com.vaadin.cdi.internal.Conventions.firstToLower;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.vaadin.cdi.uis.PlainColidingAlternativeUI;
import com.vaadin.cdi.uis.PlainUI;
import org.junit.Test;

import com.vaadin.cdi.views.OneAndOnlyViewWithPath;
import com.vaadin.cdi.views.OneAndOnlyViewWithoutPath;
import com.vaadin.cdi.views.OneAndOnlyViewWithoutPathAndAnnotation;

/**
 * 
 * @author adam-bien.com
 */
public class ConventionsTest {

    @Test
    public void normalizeLowerFirstCase() {
        String origin = "LoginPage";
        String expected = "loginPage";
        String actual = firstToLower(origin);
        assertThat(actual, is(expected));
    }

    @Test
    public void lowerFirstCaseWithOneCharacter() {
        String origin = "A";
        String expected = "a";
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

    @Test
    public void extractViewNameUsingPath() {
        String expected = "custom";
        String actual = deriveMappingForView(OneAndOnlyViewWithPath.class);
        assertThat(actual, is(expected));
    }

    @Test
    public void extractViewNameUsingConvention() {
        String expected = "oneAndOnlyViewWithoutPath";
        String actual = deriveMappingForView(OneAndOnlyViewWithoutPath.class);
        assertThat(actual, is(expected));
    }
    @Test
    public void extractViewNameUsingConventionWithoutAnnotation() {
        String expected = "oneAndOnlyViewWithoutPathAndAnnotation";
        String actual = deriveMappingForView(OneAndOnlyViewWithoutPathAndAnnotation.class);
        assertThat(actual, is(expected));
    }

    @Test
    public void extractUIPathUsingConvention() {
        String expected = "plainUI";
        String actual = deriveMappingForUI(PlainUI.class);
        assertThat(actual, is(expected));
    }

    @Test
    public void extractUIPathUsingAnnotation() {
        String expected = "plainUI";
        String actual = deriveMappingForUI(PlainColidingAlternativeUI.class);
        assertThat(actual, is(expected));
    }

    @Test
    public void uiAnnotationNotPresent(){
        final String uiPath = deriveMappingForUI(String.class);
        assertNull(uiPath);
    }

}
