package com.vaadin.cdi.views;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.ViewScoped;
import com.vaadin.cdi.internal.CDIUtil;
import com.vaadin.cdi.internal.NonPassivatingBean;
import com.vaadin.cdi.internal.ViewScopedContext;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@CDIView("")
public class NonPassivatingContentView extends CustomComponent implements View {

    @Inject
    private NonPassivatingBean bean;
    
    @Inject
    private BeanManager bm;
    
    public static final String label_id = "constructed-non-passivating-bean";
    public static final String custom_bean_id = "custom-non-passivating-bean";
    public static final String failure = "failure";
    public static final String success = "success";
    
    @Override
    public void enter(ViewChangeEvent event) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        
        Label viewLabel = new Label("CrossInjectingView");
        viewLabel.setId("label");
        
        Label label = new Label(bean.getSomeString());
        label.setId(label_id);
        
        ViewScopedContext context = new InitializedViewScopedContext(bm);
        
        // A dummy CreationalContext to avoid using CDI implementation specific
        // classes
        CreationalContext cc = new CreationalContext<Object>() {

            @Override
            public void push(Object incompleteInstance) {
            }

            @Override
            public void release() {
            }
        };
        // this tests getting a new non-PassivationCapable bean from a
        // context for @ViewScoped (a non-passivating scope)
        Object o = context.get(new MyBean(), cc);
        
        layout.addComponent(viewLabel);
        layout.addComponent(label);
        
        Label customBeanLabel = new Label();
        customBeanLabel.setId(custom_bean_id);
        if(o == null) {
            customBeanLabel.setValue(failure);
        } else {
            customBeanLabel.setValue(success);
        }
        layout.addComponent(customBeanLabel);
        
        setCompositionRoot(layout);
        
    }
    
    private static class InitializedViewScopedContext extends ViewScopedContext {
        public InitializedViewScopedContext(BeanManager bm) {
            super(bm);
            // a hack to simulate a view change event
            SessionData sessionData = getSessionData(CDIUtil.getSessionId(), true);
            SessionData.UIData uiData = sessionData.getUIData(UI.getCurrent().getUIId(), true);
            uiData.setOpeningView("");
        }
    }
    
    /**
	 * A custom CDI Bean implementation that does not implement
	 * PassivationCapable.
	 */
    private static class MyBean implements Bean {

        @Override
        public Object create(CreationalContext context) {
            return new Object();
        }

        @Override
        public void destroy(Object instance, CreationalContext context) {
        }

        @Override
        public Set getTypes() {
            return null;
        }

        @Override
        public Set getQualifiers() {
            return new HashSet();
        }

        @Override
        public Class getScope() {
            return ViewScoped.class;
        }

        @Override
        public String getName() {
            return "Test";
        }

        @Override
        public boolean isNullable() {
            return false;
        }

        @Override
        public Set getInjectionPoints() {
            return new HashSet();
        }

        @Override
        public Class getBeanClass() {
            return Object.class;
        }

        @Override
        public Set getStereotypes() {
            return new HashSet();
        }

        @Override
        public boolean isAlternative() {
            return false;
        }
        
    }
}
