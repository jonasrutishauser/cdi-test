package com.github.jonasrutishauser.cdi.testing;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class DependentScopedBean {

    @Inject
    private ApplicationBean applicationBean;

    @Inject
    private ProducedBean producedBean;

    @PostConstruct
    public void setup() {
        applicationBean.setAttribute(producedBean.getName());
    }

    public String getAttribute() {
        return applicationBean.getAttribute();
    }
}
