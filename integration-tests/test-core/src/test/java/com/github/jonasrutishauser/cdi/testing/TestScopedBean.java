package com.github.jonasrutishauser.cdi.testing;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

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
