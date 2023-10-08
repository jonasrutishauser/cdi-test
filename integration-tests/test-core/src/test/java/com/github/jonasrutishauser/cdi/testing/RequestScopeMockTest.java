package com.github.jonasrutishauser.cdi.testing;

import static org.mockito.Mockito.verify;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class RequestScopeMockTest extends BaseTest {

    private static final String SAMPLE = "sample";

    @Mock
    private ApplicationBean applicationBean;

    @Inject
    private RequestBean requestBean;

    @Test
    void setAttributeTransitive() {
        requestBean.setAttribute(SAMPLE);
        verify(applicationBean).setAttribute(SAMPLE);
    }

}
