package com.github.jonasrutishauser.cdi.test.core.context.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;

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
