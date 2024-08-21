package com.github.jonasrutishauser.cdi.test.core.interceptor;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MockImplementationManager {

    public Object getMock(Class<?> type) {
        return null;
    }

}
