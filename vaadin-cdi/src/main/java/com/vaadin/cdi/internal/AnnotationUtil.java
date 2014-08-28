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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.URLMapping;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

/**
 * Utility methods for accessing CDI related annotations.
 */
public class AnnotationUtil {

    /**
     * Search all UI beans with {@link CDIUI} annotation without a path
     * parameter (root beans).
     * 
     * The actual URL of the UI will be preceded by the servlet URL mapping that
     * can be specified with the {@link URLMapping} annotation on the root
     * {@link CDIUI}, in web.xml or using Servlet 3.0 annotations.
     * 
     * @return all UI beans with {@link CDIUI} annotation without path
     */
    public static Set<Bean<?>> getRootUiBeans(BeanManager beanManager) {
        Set<Bean<?>> uiBeans = getUiBeans(beanManager);
        Set<Bean<?>> rootBeans = new HashSet<Bean<?>>();
        for (Bean<?> bean : uiBeans) {
            Class<?> beanClass = bean.getBeanClass();
            // uiBeans may also contain UIs without the @CDIUI annotation -
            // ignore those
            CDIUI uiAnnotation = beanClass.getAnnotation(CDIUI.class);
            if (uiAnnotation == null) {
                continue;
            }

            String path = Conventions.deriveMappingForUI(beanClass);
            if (null != path && path.isEmpty()) {
                rootBeans.add(bean);
            }
        }
        return rootBeans;
    }

    /**
     * List all UI beans (whether or not they have the {@link CDIUI} annotation.
     * 
     * @param beanManager
     * @return set of UI beans
     */
    public static Set<Bean<?>> getUiBeans(BeanManager beanManager) {
        // The annotation @CDIUI can have a value, so using it as the type
        // parameter of AnnotationLiteral is somewhat problematic.
        Set<Bean<?>> uiBeans = beanManager.getBeans(UI.class,
                new AnnotationLiteral<Any>() {
                });
        return uiBeans;
    }

    public static List<String> getCDIViewMappings(BeanManager beanManager) {
        Set<Bean<?>> viewBeans = beanManager.getBeans(View.class,
                new AnnotationLiteral<Any>() {
                });
        List<String> mappingList = new LinkedList<String>();
        for (Bean<?> viewBean : viewBeans) {
            Class<?> beanClass = viewBean.getBeanClass();
            if (beanClass.getAnnotation(CDIView.class) == null) {
                continue;
            }
            String mapping = Conventions.deriveMappingForView(viewBean
                    .getBeanClass());
            if (mapping != null) {
                mappingList.add(mapping);
            }
        }
        Collections.sort(mappingList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // descending string length order to get the longest view
                // mappings first
                return o1.length() - o2.length();
            }
        });

        return mappingList;
    }

}
