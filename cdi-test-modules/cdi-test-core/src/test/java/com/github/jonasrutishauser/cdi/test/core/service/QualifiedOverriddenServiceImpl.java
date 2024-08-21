package com.github.jonasrutishauser.cdi.test.core.service;

import jakarta.enterprise.context.Dependent;

@TestQualifier
@Dependent
public class QualifiedOverriddenServiceImpl implements OverriddenService {
    @Override
    public String serviceMethod() {
        return "QualifiedOverriddenServiceImpl";
    }
}
