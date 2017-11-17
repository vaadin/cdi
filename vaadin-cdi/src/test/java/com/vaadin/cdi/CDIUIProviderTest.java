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
        String origin = "/uIWithViewUI";
        String expected = "uIWithViewUI";
        String actual = cut.parseUIMapping(origin);
        assertThat(actual, is(expected));
    }

    @Test
    public void uriWithJustUIWithEndingSlash() {
        String origin = "/uIWithViewUI/";
        String expected = "uIWithViewUI";
        String actual = cut.parseUIMapping(origin);
        assertThat(actual, is(expected));
    }

    /* HashBang -style URI Fragments */

    @Test
    public void uriWithUIAndViewWithoutEndingSlash() {
        String origin = "/uIWithViewUI/#!helloView";
        String expected = "uIWithViewUI";
        String actual = cut.parseUIMapping(origin);
        assertThat(actual, is(expected));
    }

    @Test
    public void uriWithUIAndViewWithEndingSlash() {
        String origin = "/uIWithViewUI/#!helloView/";
        String expected = "uIWithViewUI";
        String actual = cut.parseUIMapping(origin);
        assertThat(actual, is(expected));
    }

    @Test
    public void uriWithUIAndViewWithParameters() {
        String origin = "/uIWithViewUI/#!helloView/param1=foo&param2=bar";
        String expected = "uIWithViewUI";
        String actual = cut.parseUIMapping(origin);
        assertThat(actual, is(expected));
    }

    /*
     * PushState based navigation requires that the full path info is used as-is
     * without ending slash. CDIUIProvider should match UI with
     * String::startsWith.
     */

    @Test
    public void uriWithUIAndViewWithEndingSlashForPushStateNavigation() {
        String origin = "/uIWithViewUI/helloView/";
        String expected = "uIWithViewUI/helloView";
        String actual = cut.parseUIMapping(origin);
        assertThat(actual, is(expected));
    }

    @Test
    public void uriWithUIAndViewForPushStateNavigation() {
        String origin = "/uIWithViewUI/helloView";
        String expected = "uIWithViewUI/helloView";
        String actual = cut.parseUIMapping(origin);
        assertThat(actual, is(expected));
    }

    @Test
    public void uriWithUIAndViewAndParametersForPushStateNavigation() {
        String origin = "/uIWithViewUI/helloView/param1=foo/param2=bar";
        String expected = "uIWithViewUI/helloView/param1=foo/param2=bar";
        String actual = cut.parseUIMapping(origin);
        assertThat(actual, is(expected));
    }
}
