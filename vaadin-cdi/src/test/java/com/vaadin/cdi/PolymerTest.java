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

import com.vaadin.cdi.annotation.NormalUIScoped;
import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.cdi.context.UIUnderTestContext;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.polymertemplate.TemplateParser;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.templatemodel.TemplateModel;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

@RunWith(CdiTestRunner.class)
public class PolymerTest {

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
        instantiator = service.getInstantiator();
        template = instantiator.getOrCreate(TestTemplate.class);
    }

    @After
    public void tearDown() {
        uiUnderTestContext.tearDownAll();
    }

    @Test
    public void injectField_componentHasScope_scopedInstanceInjected() {
        final PseudoScopedLabel label = pseudoScopedLabelProvider.get();
        assertSame(label, template.pseudo);
    }

    @Test
    public void injectField_componentHasScope_elementBindingSuccess() {
        assertNotNull(template.pseudo.getElement().getNode().getParent());
    }

    /**
     * Test to show element binding with a normal scoped component doesn't work.
     * It is bound to a new element instead of the one in the template element tree.
     * <p>
     * Vaadin Component consumes binding info from thread local
     * in the no-arg constructor. Proxies don't work.
     */
    @Test
    public void injectField_componentNormalScoped_elementBindingFailure() {
        assertNull(template.normal.getElement().getNode().getParent());
    }

    /**
     * Test to show element binding with an already instantiated
     * component doesn't work.
     * <p>
     * Because of the binding in Component constructor seen before.
     */
    @Test
    public void injectField_instantiatedBeforeInjection_elementBindingFailure() {
        // Instantiate a new template.
        // Components are scoped, they won't be instantiated again.
        template = instantiator.getOrCreate(TestTemplate.class);
        assertNull(template.normal.getElement().getNode().getParent());
    }

    @UIScoped
    @Tag("uiscoped-label")
    public static class PseudoScopedLabel extends Label {
    }

    @NormalUIScoped
    @Tag("normaluiscoped-label")
    public static class NormalScopedLabel extends Label {
    }

    @Tag("test-template")
    public static class TestTemplate extends PolymerTemplate<TemplateModel> {

        @Id("pseudo")
        private PseudoScopedLabel pseudo;

        @Id("normal")
        private NormalScopedLabel normal;

        public TestTemplate() {
            super(new TestTemplateParser(
                    TestTemplate.class
                            .getResourceAsStream("test-template.html")));
        }

    }

    private static class TestTemplateParser implements TemplateParser {

        private final InputStream content;

        private TestTemplateParser(InputStream content) {
            this.content = content;
        }

        @Override
        public TemplateData getTemplateContent(Class<? extends PolymerTemplate<?>> clazz,
                                               String tag,
                                               VaadinService service) {
            try {
                Document document = Jsoup.parse(content,
                        StandardCharsets.UTF_8.name(), "");
                Element element = document.getElementsByTag("dom-module").get(0);
                return new TemplateData("dummy", element);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
