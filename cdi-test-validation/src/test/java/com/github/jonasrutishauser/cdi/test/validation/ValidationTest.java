package com.github.jonasrutishauser.cdi.test.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;

@ExtendWith(CdiTestJunitExtension.class)
class ValidationTest {

    @Inject
    private ValidatorFactory factory;

    @Inject
    private Validator validator;

    @Inject
    private TestBean testBean;

    @Test
    void testInject() {
        assertNotNull(factory);
        assertNotNull(validator);
    }

    @Test
    void testMethodParams() {
        assertEquals("test", testBean.validated("test"));

        assertThrows(ValidationException.class, () -> testBean.validated(null));
    }

    @Dependent
    static class TestBean {
        String validated(@NotNull String value) {
            return value;
        }
    }

}
