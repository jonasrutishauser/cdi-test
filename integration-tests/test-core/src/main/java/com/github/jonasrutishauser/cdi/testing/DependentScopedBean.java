package com.github.jonasrutishauser.cdi.testing;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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
