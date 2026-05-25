/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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

    @Test
    public void uriWithUIAndViewWithoutEndingSlash() {
        String origin = "/uIWithViewUI/!helloView";
        String expected = "uIWithViewUI";
        String actual = cut.parseUIMapping(origin);
        assertThat(actual, is(expected));
    }

    @Test
    public void uriWithUIAndViewWithEndingSlash() {
        String origin = "/uIWithViewUI/!helloView/";
        String expected = "uIWithViewUI";
        String actual = cut.parseUIMapping(origin);
        assertThat(actual, is(expected));
    }

    @Test
    public void uriWithUIAndViewWithParameters() {
        String origin = "/uIWithViewUI!helloView/param1=foo&param2=bar";
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
