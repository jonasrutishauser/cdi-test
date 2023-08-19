package com.github.jonasrutishauser.cdi.test.jndi;

import static javax.naming.Context.INITIAL_CONTEXT_FACTORY;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.weld.injection.spi.helpers.AbstractResourceServices;
import org.osjava.sj.SimpleJndi;
import org.osjava.sj.jndi.MemoryContext;

import jakarta.annotation.Resource;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;

public class TestResourceService extends AbstractResourceServices {

    static {
        if (System.getProperty(INITIAL_CONTEXT_FACTORY) == null) {
            System.setProperty(INITIAL_CONTEXT_FACTORY, org.osjava.sj.MemoryContextFactory.class.getName());
            System.setProperty(SimpleJndi.SHARED, "true");
            System.setProperty(SimpleJndi.JNDI_SYNTAX_SEPARATOR, "/");
            System.setProperty(MemoryContext.IGNORE_CLOSE, "true");
        }
    }

    @Override
    public Object resolveResource(InjectionPoint injectionPoint) {
        try {
            return super.resolveResource(injectionPoint);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("Error looking up ") && injectionPoint.getType() instanceof Class) {
                Instance<?> instance = CDI.current().select((Class<?>) injectionPoint.getType());
                if (instance.isResolvable()) {
                    return instance.get();
                }
            }
            throw e;
        }
    }

    @Override
    protected String getResourceName(InjectionPoint injectionPoint) {
        Resource resource = getResourceAnnotation(injectionPoint);
        if (!resource.lookup().isBlank()) {
            return resource.lookup();
        }
        return super.getResourceName(injectionPoint);
    }

    @Override
    protected Context getContext() {
        try {
            return new InitialContext();
        } catch (NamingException e) {
            throw new IllegalStateException();
        }
    }

}
