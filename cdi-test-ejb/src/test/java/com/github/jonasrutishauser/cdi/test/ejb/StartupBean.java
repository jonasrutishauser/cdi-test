package com.github.jonasrutishauser.cdi.test.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

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
