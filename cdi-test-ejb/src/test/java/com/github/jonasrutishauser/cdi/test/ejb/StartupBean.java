package com.github.jonasrutishauser.cdi.test.ejb;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

@Startup
@Singleton
public class StartupBean {

    private final SingletonBean singleton;

    @Inject
    public StartupBean(SingletonBean singleton) {
        this.singleton = singleton;
    }

    @PostConstruct
    void init() {
        singleton.setStarted();
    }

}
