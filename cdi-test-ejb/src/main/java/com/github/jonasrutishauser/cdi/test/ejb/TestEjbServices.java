package com.github.jonasrutishauser.cdi.test.ejb;

import org.jboss.weld.ejb.api.SessionObjectReference;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.jboss.weld.ejb.spi.EjbServices;
import org.jboss.weld.ejb.spi.InterceptorBindings;

import jakarta.enterprise.inject.spi.CDI;

public class TestEjbServices implements EjbServices {

    @Override
    public SessionObjectReference resolveEjb(EjbDescriptor<?> ejbDescriptor) {
        return new SessionObjectReference() {
            @Override
            public void remove() {
                // not supported
            }
            
            @Override
            public boolean isRemoved() {
                // not supported
                return false;
            }

            @Override
            public <S> S getBusinessObject(Class<S> businessInterfaceType) {
                return CDI.current().select(businessInterfaceType, EjbInstance.Literal.INSTANCE).get();
            }
        };
    }

    @Override
    public void registerInterceptors(EjbDescriptor<?> ejbDescriptor, InterceptorBindings interceptorBindings) {
        // not supported
    }

    @Override
    public void cleanup() {
        // nothing
    }

}
