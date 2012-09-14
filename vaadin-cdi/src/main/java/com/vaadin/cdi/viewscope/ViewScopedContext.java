package com.vaadin.cdi.viewscope;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import com.vaadin.cdi.UIBeanStore;
import com.vaadin.cdi.UIScopedContext;
import com.vaadin.navigator.View;

/**
 * Maintains view-scoped managed beans
 * 
 * @author Adam Bien, adam-bien.com
 */
public class ViewScopedContext implements Context {

    private boolean active;
    private final BeanManager beanManager;
    private final Map<View, UIBeanStore> beanStores = new HashMap<View, UIBeanStore>();

    public ViewScopedContext(final BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    private UIBeanStore getCurrentBeanStore() {
        return getCurrentBeanStore(CurrentView.getCurrent());
    }

    private UIBeanStore getCurrentBeanStore(View scopedView) {
        return beanStores.get(scopedView);
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
        return get(contextual, null);
    }

    @Override
    public <T> T get(final Contextual<T> contextual,
            final CreationalContext<T> creationalContext) {
        UIBeanStore currentBeanStore;
        Bean<T> bean = (Bean<T>) contextual;
        if (View.class.isAssignableFrom(bean.getBeanClass())) {
            View scopedView = createScopedView((Bean<View>) bean,
                    (CreationalContext<View>) creationalContext);
            currentBeanStore = getCurrentBeanStore(scopedView);
        } else {
            currentBeanStore = getCurrentBeanStore();
        }
        return currentBeanStore.getBeanInstance(bean, creationalContext);
    }

    public View createScopedView(Bean<View> t, CreationalContext<View> context) {
        View view = t.create(context);
        CurrentView.set(view);
        return view;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinViewScoped.class;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private static Logger getLogger() {
        return Logger.getLogger(UIScopedContext.class.getCanonicalName());
    }

}
