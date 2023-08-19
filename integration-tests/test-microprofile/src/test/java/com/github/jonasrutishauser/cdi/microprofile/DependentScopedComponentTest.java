package com.github.jonasrutishauser.cdi.microprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;
import com.github.jonasrutishauser.cdi.test.microprofile.config.ConfigPropertyValue;

import jakarta.inject.Inject;

@ExtendWith(CdiTestJunitExtension.class)
class DependentScopedComponentTest {

    @Inject
    private DependentScopedComponent dependentScopedComponent;

    @Test
    void testDefaultValue() {
        assertEquals("Hello World", dependentScopedComponent.getStringProperty());
    }

    @Test
    @ConfigPropertyValue(name = "some.string.property", value = "Hello Test")
    void testTestCaseAnnotatedValue() {
        assertEquals("Hello Test", dependentScopedComponent.getStringProperty());
    }
}
