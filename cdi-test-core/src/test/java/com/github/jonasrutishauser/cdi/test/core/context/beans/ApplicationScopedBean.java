package com.github.jonasrutishauser.cdi.test.core.context.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ApplicationScopedBean extends ScopedBean {

    @Inject
    private DependentScopedBean dependentScopedBean;

    public DependentScopedBean getDependentScopedBean() {
        return dependentScopedBean;
    }
}
