package com.github.jonasrutishauser.cdi.testing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

class DependentScopedBeanTest extends BaseTest {

    @Inject
    private DependentScopedBean dependantScopedBean;

    @Inject
    private ApplicationBean applicationBean;

    @Inject
    private TestScopedBean testScopedBean;

    @Test
    void callTestScoped() {
        Assertions.assertEquals("hello", testScopedBean.getAttribute());
    }

    @Test
    void getAttribute() {
        Assertions.assertEquals("hello", dependantScopedBean.getAttribute());
    }

    @Test
    void setAttributeTransitive() {
        applicationBean.setAttribute("world");
        Assertions.assertEquals("world", dependantScopedBean.getAttribute());
    }

}
