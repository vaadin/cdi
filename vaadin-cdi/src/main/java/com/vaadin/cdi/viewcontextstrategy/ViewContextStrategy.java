/*
 * Vaadin CDI Integration
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.cdi.viewcontextstrategy;

import com.vaadin.navigator.View;

import jakarta.enterprise.context.Dependent;
import java.io.Serializable;

/**
 * Decision strategy on whether target navigation state belongs to active view
 * context. When the target navigation state does not belong to the active view
 * context, the current context will be released and a new one is created.
 * <p>
 * By default the views are using the {@link Dependent} scope, which can be used
 * but is not recommended. Any {@link View} with a {@code ViewContextStrategy}
 * should use one of the scopes provided in the Vaadin CDI integration.
 * <p>
 * Separate annotations annotated by {@link ViewContextStrategyQualifier} have
 * to exist for each of the implementations.
 * <p>
 * Example of a custom implementation:
 * <p>
 * A separate annotation.
 * 
 * <pre>
 * {@literal @}Retention(RetentionPolicy.RUNTIME)
 * {@literal @}Target({ ElementType.TYPE })
 * {@literal @}ViewContextStrategyQualifier
 *  public {@literal @}interface MyStrategyAnnotation {
 *  }
 * </pre>
 * 
 * An implementation class.
 * 
 * <pre>
 * {@literal @}NormalUIScoped
 * {@literal @}MyStrategyAnnotation
 *  public class MyStrategy implements ViewContextStrategy {
 *    public boolean contains(String viewName, String parameters) {
 *      ...
 *    }
 *  }
 * </pre>
 * 
 * Use annotation on the view.
 * 
 * <pre>
 * {@literal @}CDIView("myView")
 * {@literal @}MyStrategyAnnotation
 *  public MyView implements View {
 *  ...
 *  }
 * </pre>
 */
public interface ViewContextStrategy extends Serializable {

    /**
     * Returns whether the active context contains target navigation state. This
     * method should compare the current navigation state and the one given
     * through the parameters and decide if the current context should be held
     * open or released.
     *
     * @param viewName
     *            target navigation view name
     * @param parameters
     *            target navigation parameters
     * @return {@code true} to hold context open; {@code false} to release it
     */
    boolean inCurrentContext(String viewName, String parameters);

}
