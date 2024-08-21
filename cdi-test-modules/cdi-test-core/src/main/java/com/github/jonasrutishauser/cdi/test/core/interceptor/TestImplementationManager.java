package com.github.jonasrutishauser.cdi.test.core.interceptor;

import java.util.HashSet;
import java.util.Set;

import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

@TestScoped
public class TestImplementationManager {
    private final Set<Class<?>> activeTestImplementations = new HashSet<>();

    public void enableTestImplementation(Class<?> testImplementation) {
        activeTestImplementations.add(testImplementation);
    }

    public boolean isEnabled(Class<?> testImplementation) {
        return activeTestImplementations.contains(testImplementation);
    }
}
