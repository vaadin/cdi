package com.vaadin.cdi.internal;

import com.vaadin.cdi.viewcontextstrategy.ViewContextStrategy;
import com.vaadin.cdi.viewcontextstrategy.ViewContextStrategyQualifier;
import com.vaadin.cdi.viewcontextstrategy.ViewNameAndParametersDriven;
import com.vaadin.navigator.View;
import org.apache.deltaspike.core.api.literal.AnyLiteral;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Looks up ViewContextStrategy for view classes.
 */
@ApplicationScoped
public class ViewContextStrategyProvider {
    private static AnyLiteral ANY_LITERAL = new AnyLiteral();
    @Inject
    private BeanManager beanManager;

    public ViewContextStrategy lookupStrategy(Class<? extends View> viewClass) {
        Class<? extends Annotation> annotationClass = findStrategyAnnotation(viewClass);
        if (annotationClass == null) {
            annotationClass = ViewNameAndParametersDriven.class;
        }
        final Bean strategyBean = findStrategyBean(annotationClass);
        if (strategyBean == null) {
            throw new IllegalStateException(
                    "No ViewContextStrategy found for " + annotationClass.getCanonicalName());
        }
        return BeanProvider.getContextualReference(ViewContextStrategy.class, strategyBean);
    }

    private Class<? extends Annotation> findStrategyAnnotation(Class<? extends View> viewClass) {
        final Annotation[] annotations = viewClass.getAnnotations();
        for (Annotation annotation : annotations) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.getAnnotation(ViewContextStrategyQualifier.class) != null) {
                return annotationType;
            }
        }
        return null;
    }

    private Bean findStrategyBean(Class<? extends Annotation> annotationClass) {
        final Set<Bean<?>> strategyBeans =
                beanManager.getBeans(ViewContextStrategy.class, ANY_LITERAL);
        for (Bean<?> strategyBean : strategyBeans) {
            final Class<?> strategyBeanClass = strategyBean.getBeanClass();
            if (ViewContextStrategy.class.isAssignableFrom(strategyBeanClass)
                    && strategyBeanClass.getAnnotation(annotationClass) != null) {
                return strategyBean;
            }
        }
        return null;
    }
}
