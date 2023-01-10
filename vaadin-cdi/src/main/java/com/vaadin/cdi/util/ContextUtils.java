package com.vaadin.cdi.util;

import java.lang.annotation.Annotation;
import jakarta.enterprise.context.ContextNotActiveException;
import jakarta.enterprise.inject.Typed;
import jakarta.enterprise.inject.spi.BeanManager;

/**
 * A set of utility methods for working with contexts.
 */
@Typed()
public abstract class ContextUtils
{
    private ContextUtils()
    {
        // prevent instantiation
    }

    /**
     * Checks if the context for the given scope annotation is active.
     *
     * @param scopeAnnotationClass The scope annotation (e.g. @RequestScoped.class)
     * @return If the context is active.
     */
    public static boolean isContextActive(Class<? extends Annotation> scopeAnnotationClass)
    {
        return isContextActive(scopeAnnotationClass, BeanManagerProvider.getInstance().getBeanManager());
    }

    /**
     * Checks if the context for the given scope annotation is active.
     *
     * @param scopeAnnotationClass The scope annotation (e.g. @RequestScoped.class)
     * @param beanManager The {@link BeanManager}
     * @return If the context is active.
     */
    public static boolean isContextActive(Class<? extends Annotation> scopeAnnotationClass, BeanManager beanManager)
    {
        try
        {
            if (beanManager.getContext(scopeAnnotationClass) == null
                    || !beanManager.getContext(scopeAnnotationClass).isActive())
            {
                return false;
            }
        }
        catch (ContextNotActiveException e)
        {
            return false;
        }

        return true;
    }
}
