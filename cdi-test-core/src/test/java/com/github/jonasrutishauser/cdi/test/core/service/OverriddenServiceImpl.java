package com.github.jonasrutishauser.cdi.test.core.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;

@Dependent
public class OverriddenServiceImpl implements OverriddenService {

    @PostConstruct
    protected void create() {
        throw new RuntimeException("not working");
    }

    @Override
    public String serviceMethod() {
        return "OverriddenServiceImpl";
    }
}
