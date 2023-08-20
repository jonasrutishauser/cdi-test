package com.github.jonasrutishauser.cdi.test.microprofile.config;

import static jakarta.interceptor.Interceptor.Priority.LIBRARY_BEFORE;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;

@TestScoped
class TestProperties {

    private final Map<String, String> properties = new HashMap<>();
    private boolean initialized;

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public boolean isInitialized() {
        return initialized;
    }

    void setTestProperties(@Observes @Priority(LIBRARY_BEFORE) @Initialized(ApplicationScoped.class) Object event, TestInfo testInfo) {
        setTestProperties(testInfo);
    }

    void setTestProperties(@Observes @Priority(LIBRARY_BEFORE) @Initialized(TestScoped.class) TestInfo testInfo) {
        for (ConfigPropertyValue property : testInfo.getTestClass().getAnnotationsByType(ConfigPropertyValue.class)) {
            properties.put(property.name(), property.value());
        }
        for (ConfigPropertyValue property : testInfo.getTestMethod().getAnnotationsByType(ConfigPropertyValue.class)) {
            properties.put(property.name(), property.value());
        }
        initialized = true;
    }
}
