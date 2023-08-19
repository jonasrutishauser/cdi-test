package com.github.jonasrutishauser.cdi.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

class RequestScopePlainTest extends BaseTest {

    private static final String SAMPLE = "sample";

    @Inject
    private ApplicationBean applicationBean;

    @Inject
    private RequestBean requestBean;

    @Test
    void setAttributeTransitive() {
        Assertions.assertNull(applicationBean.getAttribute());
        requestBean.setAttribute(SAMPLE);
        assertEquals(SAMPLE, applicationBean.getAttribute());
    }

}
