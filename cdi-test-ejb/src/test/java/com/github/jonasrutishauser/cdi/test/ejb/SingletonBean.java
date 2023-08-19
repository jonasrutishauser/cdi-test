package com.github.jonasrutishauser.cdi.test.ejb;

import jakarta.ejb.Singleton;

@Singleton
public class SingletonBean {

    private boolean started;

    public void setStarted() {
        this.started = true;
    }

    public boolean isStarted() {
        return started;
    }
}
