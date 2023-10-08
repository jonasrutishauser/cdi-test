package com.github.jonasrutishauser.cdi.test.microprofile.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

@ExtendWith(CdiTestJunitExtension.class)
@ConfigPropertyValue(name = "some.string.property", value = "valuefortestclass")
class InjectionTest {

    @Inject
    private InjectionSample injectionSample;

    @Inject
    private Instance<NotUsedBean> notUsedBean;

    @Test
    void testStringProperties() {
        Assertions.assertEquals("valuefortestclass", injectionSample.getSomeProperty());
        Assertions.assertEquals("valuefortestclass", injectionSample.getStringProperty());
        Assertions.assertFalse(injectionSample.getOptionalProperty().isPresent());
    }

    @Test
    @ConfigPropertyValue(name = "some.string.property", value = "valuefortest")
    @ConfigPropertyValue(name = "some.list.property", value = "a,b,c,d,e")
    void overrideInTest() {
        Assertions.assertEquals("valuefortest", injectionSample.getSomeProperty());
        Assertions.assertEquals("valuefortest", injectionSample.getStringProperty());
        Assertions.assertEquals(5, injectionSample.getListProperty().size());
    }

    @Test
    void testNumberProperties() {
        Assertions.assertEquals(true, injectionSample.getBoolProperty());
        Assertions.assertEquals(1, injectionSample.getIntProperty());
        Assertions.assertEquals(1, injectionSample.getLongProperty());
        Assertions.assertEquals(1.0, injectionSample.getDoubleProperty());
        Assertions.assertEquals(1.0f, injectionSample.getFloatProperty());
    }

    @Test
    void testCollectionValues() {
        Assertions.assertEquals(3, injectionSample.getListProperty().size());
        Assertions.assertEquals(2, injectionSample.getSetProperty().size());
    }

    @Test
    void testNotDefinedProperty() {
        assertThrows(NoSuchElementException.class, notUsedBean::get);
    }

    @Dependent
    static class NotUsedBean {
        @ConfigProperty(name = "required.string.property")
        @Inject
        private String stringProperty;
        @ConfigProperty(name = "required.int.property")
        @Inject
        private int intProperty;
        @ConfigProperty(name = "required.boolean.property")
        @Inject
        private boolean booleanProperty;
    }
}
