package com.github.jonasrutishauser.cdi.testing;

import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

@TestScoped
public class TestScopedBean {

    @Inject
    private DependentScopedBean dependentScopedBean;

    @PostConstruct
    public void setup() {
        dependentScopedBean.getAttribute();
    }

    public String getAttribute() {
        return dependentScopedBean.getAttribute();
    }
}
