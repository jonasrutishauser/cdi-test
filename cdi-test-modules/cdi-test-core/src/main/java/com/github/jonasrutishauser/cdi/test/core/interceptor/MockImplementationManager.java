package com.github.jonasrutishauser.cdi.test.core.interceptor;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MockImplementationManager {

    /**
     * @param type
     *            class for which a mock is requested
     * @return mock object for requested type or {@code null} if no mock is
     *         available
     */
    public Object getMock(Class<?> type) {
        return null;
    }

}
