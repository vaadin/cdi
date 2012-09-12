package com.vaadin.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.util.AnnotationLiteral;

public class CDIBean<T> implements Bean<T> {

    private InjectionTarget<T> it;
    private Class<T> clazz;
    private Class<? extends Annotation> scope;

    public CDIBean(Class<T> clazz, InjectionTarget<T> it,
            Class<? extends Annotation> scope) {
        super();
        this.it = it;
        this.clazz = clazz;
        this.scope = scope;
    }

    @Override
    public Class<T> getBeanClass() {
        return clazz;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return it.getInjectionPoints();
    }

    @Override
    public String getName() {
        return Naming.firstToLower(this.clazz.getName());
    }

    @Override
    public Set<Annotation> getQualifiers() {
        Set<Annotation> qualifiers = new HashSet<Annotation>();

        qualifiers.add(new AnnotationLiteral<Any>() {
        });
        qualifiers.add(new AnnotationLiteral<Default>() {
        });
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return this.scope;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public Set<Type> getTypes() {
        Set<Type> types = new HashSet<Type>();
        types.add(this.clazz);
        types.add(Object.class);
        return types;
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public T create(CreationalContext<T> ctx) {
        T instance = it.produce(ctx);
        it.inject(instance, ctx);
        it.postConstruct(instance);
        return instance;

    }

    @Override
    public void destroy(T instance, CreationalContext<T> ctx) {
        it.preDestroy(instance);
        it.dispose(instance);
        ctx.release();
    }

}
