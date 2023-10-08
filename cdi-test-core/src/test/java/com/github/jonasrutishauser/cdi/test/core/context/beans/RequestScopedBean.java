package com.github.jonasrutishauser.cdi.test.core.context.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class RequestScopedBean extends ScopedBean {

    @Inject
    private DependentScopedBean dependentScopedBean;

    public DependentScopedBean getDependentScopedBean() {
        return dependentScopedBean;
    }

}
