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
        this.active = true;
    }

    private UIBeanStore getCurrentBeanStore() {
        View current = CurrentView.getCurrent();
        if (current == null) {
            return null;
        }
        return beanStores.get(current);
    }

    /**
     * Returns an existing, or null
     */
    @Override
    public <T> T get(final Contextual<T> contextual) {
        Bean<T> bean = (Bean<T>) contextual;
        LOG().info(
                "Trying to fetch an existing instance for: "
                        + bean.getBeanClass());
        UIBeanStore currentBeanStore = getCurrentBeanStore();
        if (currentBeanStore == null) {
            LOG().info(
                    "BeanStore for instance " + bean.getBeanClass()
                            + " does not exist yet, creating a new one");
            return null;
        }
        return currentBeanStore.getBeanInstance(bean);
    }

    /**
     * Returns an existing, or a new instance
     */
    @Override
    public <T> T get(final Contextual<T> contextual,
            final CreationalContext<T> creationalContext) {
        Bean<T> bean = (Bean<T>) contextual;
        UIBeanStore currentBeanStore = getCurrentBeanStore();
        if (currentBeanStore == null) {
            if (View.class.isAssignableFrom(bean.getBeanClass())) {
                LOG().info(
                        "Requested bean " + bean.getBeanClass()
                                + " is a View, creating a new one");
                currentBeanStore = createStoreAndView(creationalContext, bean);
            } else {
                throw new IllegalStateException(
                        "CurrentView is null and requested class is: "
                                + bean.getBeanClass());
            }
        }
        return currentBeanStore.getBeanInstance(bean, creationalContext);
    }

    private <T> UIBeanStore createStoreAndView(
            final CreationalContext<T> creationalContext, Bean<T> bean) {
        UIBeanStore currentBeanStore;
        View scopedView = createScopedView((Bean<View>) bean,
                (CreationalContext<View>) creationalContext);
        UIBeanStore uiBeanStore = new UIBeanStore();
        beanStores.put(scopedView, uiBeanStore);
        currentBeanStore = uiBeanStore;
        return currentBeanStore;
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

    private static Logger LOG() {
        return Logger.getLogger(UIScopedContext.class.getCanonicalName());
    }

}
