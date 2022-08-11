/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.jsoup.Jsoup;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.cdi.context.UIUnderTestContext;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.polymertemplate.TemplateParser.TemplateData;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.internal.CurrentInstance;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.templatemodel.TemplateModel;

@RunWith(CdiTestRunner.class)
public class PolymerTest {

    @UIScoped
    @Tag("uiscoped-label")
    public static class PseudoScopedLabel extends Label {
    }

    @Tag("test-template")
    public static class TestTemplate extends PolymerTemplate<TemplateModel> {

        @Id("pseudo")
        private PseudoScopedLabel pseudo;

        public TestTemplate() {
            super((clazz, tag, service) -> new TemplateData("",
                    Jsoup.parse(getTemplateContent())));
        }

    }

    @Inject
    private Provider<PseudoScopedLabel> pseudoScopedLabelProvider;
    private TestTemplate template;
    private UIUnderTestContext uiUnderTestContext;
    private Instantiator instantiator;

    @Before
    public void setUp() throws Exception {
        uiUnderTestContext = new UIUnderTestContext();
        uiUnderTestContext.activate();
        UI ui = uiUnderTestContext.getUi();
        VaadinService service = ui.getSession().getService();
        service.init();
        VaadinService.setCurrent(service);
        instantiator = service.getInstantiator();
        template = instantiator.getOrCreate(TestTemplate.class);
    }

    @After
    public void tearDown() {
        uiUnderTestContext.tearDownAll();
        CurrentInstance.clearAll();
    }

    @Test
    public void injectField_componentHasScope_scopeIsIgnored() {
        final PseudoScopedLabel label = pseudoScopedLabelProvider.get();
        Assert.assertNotSame(label, template.pseudo);
    }

    @Test
    public void injectField_componentHasScope_elementBindingSuccess() {
        assertNotNull(template.pseudo.getElement().getNode().getParent());
    }

    private static String getTemplateContent() {
        return "<dom-module id=\"test-template\">\n" + "    <template>\n"
                + "        <div>\n"
                + "            <uiscoped-label id=\"pseudo\"/>\n"
                + "            <normaluiscoped-label id=\"normal\"/>\n"
                + "        </div>\n" + "    </template>\n" + "</dom-module>\n";
    }

}
