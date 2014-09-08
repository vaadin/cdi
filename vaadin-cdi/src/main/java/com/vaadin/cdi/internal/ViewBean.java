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

import java.util.logging.Logger;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.PassivationCapable;

import com.vaadin.ui.UI;

public class ViewBean extends UIBean {

    /**
     * The mapped name of the view this ViewBean is associated with.
     * 
     * Must be a non-null value.
     */
    protected String viewIdentifier;

    public ViewBean(Bean delegate, long sessionId, int uiId,
            String viewIdentifier) {
        super(delegate, sessionId, uiId);
        this.viewIdentifier = viewIdentifier;
    }

    public ViewBean(Bean delegate, int uiId, String viewName) {
        super(delegate, CDIUtil.getSessionId(), uiId);
        this.viewIdentifier = viewName;
    }

    public ViewBean(Bean delegate, String viewName) {
        super(delegate, CDIUtil.getSessionId(), UI.getCurrent().getUIId());
        this.viewIdentifier = viewName;
    }

    @Override
    public boolean equals(Object o) {
        ViewBean bean = null;
        if (o instanceof ViewBean) {
            bean = (ViewBean) o;
            return super.equals(o)
                    && this.viewIdentifier.equals(bean.viewIdentifier);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = (int) sessionId;
        result = (31 * result + uiId) ^ viewIdentifier.hashCode();
        return result;
    }

    @Override
    public String getId() {
        StringBuilder sb = new StringBuilder(
                "com.vaadin.cdi.internal.ViewBean#");
        sb.append(uiId);
        sb.append("#");
        sb.append(sessionId);
        sb.append("#");
        sb.append(viewIdentifier);

        if (delegate instanceof PassivationCapable) {
            String delegatePassivationID = ((PassivationCapable) delegate)
                    .getId();
            if (delegatePassivationID != null
                    && !delegatePassivationID.isEmpty()) {
                sb.append("#");
                sb.append(delegatePassivationID);
            } else {
                sb.append("#null#");
                sb.append(delegate.getBeanClass().getCanonicalName());
            }
        } else {
            // Even if the bean itself is not passivation capable, we're still
            // using ViewBean.getid() as a key in ContextualStorage. It may mix
            // up beans in some cases, specifically if we're injecting
            // non-passivation-capable beans as @ViewScoped
            sb.append("#");
            sb.append(delegate.getBeanClass().getCanonicalName());
        }
        return sb.toString();
    }

    private Logger getLogger() {
        return Logger.getLogger(ViewBean.class.getCanonicalName());
    }
}
