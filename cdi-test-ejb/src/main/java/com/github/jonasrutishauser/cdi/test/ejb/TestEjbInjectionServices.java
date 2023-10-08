package com.github.jonasrutishauser.cdi.test.ejb;

import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.weld.injection.spi.EjbInjectionServices;
import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

public class TestEjbInjectionServices implements EjbInjectionServices {

    @Override
    public ResourceReferenceFactory<Object> registerEjbInjectionPoint(InjectionPoint injectionPoint) {
        return () -> new ResourceReference<Object>() {
            @Override
            public Object getInstance() {
                return CDI.current().select((Class<?>) injectionPoint.getType()).get();
            }

            @Override
            public void release() {
                // nothing
            }
        };
    }

    @Override
    public void cleanup() {
        // nothing
    }

}
