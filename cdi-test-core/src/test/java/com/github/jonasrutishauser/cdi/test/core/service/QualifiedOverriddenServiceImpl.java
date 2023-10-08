package com.github.jonasrutishauser.cdi.test.core.service;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

@TestQualifier
@Dependent
public class QualifiedOverriddenServiceImpl implements OverriddenService {

    @PostConstruct
    protected void create() {
    }

    @Override
    public String serviceMethod() {
        return "QualifiedOverriddenServiceImpl";
    }
}
