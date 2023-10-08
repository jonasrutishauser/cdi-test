package com.github.jonasrutishauser.cdi.test.microprofile.config;

import static javax.interceptor.Interceptor.Priority.LIBRARY_BEFORE;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

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
