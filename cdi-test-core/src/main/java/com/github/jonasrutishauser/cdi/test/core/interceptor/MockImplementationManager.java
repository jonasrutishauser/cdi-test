package com.github.jonasrutishauser.cdi.test.core.interceptor;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MockImplementationManager {

    public Object getMock(Class<?> type) {
        return null;
    }

}
