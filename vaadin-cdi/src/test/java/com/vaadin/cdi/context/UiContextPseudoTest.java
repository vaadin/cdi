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

package com.vaadin.cdi.context;

import com.vaadin.cdi.annotation.UIScoped;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.runner.RunWith;

@RunWith(CdiTestRunner.class)
public class UiContextPseudoTest
        extends AbstractContextTest<UiContextPseudoTest.UIScopedTestBean> {

    @Override
    protected Class<UIScopedTestBean> getBeanType() {
        return UIScopedTestBean.class;
    }

    @Override
    protected UnderTestContext newContextUnderTest() {
        return new UIUnderTestContext();
    }

    @Override
    protected boolean isNormalScoped() {
        return false;
    }

    @UIScoped
    public static class UIScopedTestBean extends TestBean {
    }

}
