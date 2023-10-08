package com.github.jonasrutishauser.cdi.test.core.context.beans;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class ApplicationToTestScopedBean extends ScopedBean {

    private Object initializationObject;
    
    public Object getInitializationObject() {
        return initializationObject;
    }

    void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {
        this.initializationObject = event;
    }

}
