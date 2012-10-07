package com.vaadin.cdi;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import com.vaadin.ui.UI;

/**
 * UIScopedContext is the context for @VaadinUIScoped beans.
 */
public class UIScopedContext implements Context {

    private final BeanManager beanManager;

    public UIScopedContext(final BeanManager beanManager) {
        getLogger().info("Instantiating UIScoped context");
        this.beanManager = beanManager;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return VaadinUI.class;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
        return get(contextual, null);
    }

    @Override
    public <T> T get(final Contextual<T> contextual,
            final CreationalContext<T> creationalContext) {

        BeanStoreContainer beanStoreContainer = getSessionBoundBeanStoreContainer();
        T beanInstance = null;
        int uiId;
        UIBeanStore beanStore;

        if (isInstanceOfUIBean(contextual)) {
            UIBean uiBean = (UIBean) contextual;
            uiId = uiBean.getUiId();
            beanStore = beanStoreContainer.getOrCreateUIBeanStoreFor(uiBean);
            beanInstance = beanStore.getBeanInstance(contextual,
                    creationalContext);
            if (beanStoreContainer.isBeanStoreCreationPending()) {
                beanStoreContainer.assignPendingBeanStoreFor((UI) beanInstance,
                        uiId);
            }
            /**
             * In case of a CDI event listener, the Contextual is NOT a UIBean,
             * rather than just a Bean.
             */
        } else if (isUIBean(contextual)) {
            final UI current = UI.getCurrent();
            if (current == null) {
                throw new IllegalStateException(
                        "CDI listener identified, but there is no active UI available.");
            }
            Bean<T> bean = (Bean<T>) contextual;
            if (bean.getBeanClass().isAssignableFrom(current.getClass())) {
                beanInstance = (T) current;
            }
        } else {
            throw new IllegalStateException(((Bean) contextual).getBeanClass()
                    .getName()
                    + " is not a UI, only UIs can be annotated with @VaadinUI!");
        }

        getLogger().info("Finished getting bean " + beanInstance);
        return beanInstance;
    }

    /**
     * @param contextual
     * @return true if Vaadin UI is assignabled from given bean's representing
     *         type
     */
    private <T> boolean isInstanceOfUIBean(Contextual<T> contextual) {
        if (contextual instanceof UIBean) {
            return UI.class.isAssignableFrom(((Bean<T>) contextual)
                    .getBeanClass());
        }

        return false;
    }

    private <T> boolean isUIBean(Contextual<T> contextual) {
        if (contextual instanceof Bean) {
            return UI.class.isAssignableFrom(((Bean<T>) contextual)
                    .getBeanClass());
        }

        return false;
    }

    /**
     * @return bean store container bound to the user's http session
     */
    private BeanStoreContainer getSessionBoundBeanStoreContainer() {
        Set<Bean<?>> beans = beanManager.getBeans(BeanStoreContainer.class);

        if (beans.isEmpty()) {
            throw new IllegalStateException(
                    "No bean store container bound for session");
        }

        if (beans.size() > 1) {
            throw new IllegalStateException(
                    "More than one bean store container available for session");
        }

        Bean<?> bean = beans.iterator().next();

        return (BeanStoreContainer) beanManager.getReference(bean,
                bean.getBeanClass(), beanManager.createCreationalContext(bean));
    }

    private static Logger getLogger() {
        return Logger.getLogger(UIScopedContext.class.getCanonicalName());
    }
}
