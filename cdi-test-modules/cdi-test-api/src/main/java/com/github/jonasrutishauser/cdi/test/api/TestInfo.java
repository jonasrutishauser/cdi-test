package com.github.jonasrutishauser.cdi.test.api;

import java.lang.reflect.Method;

import org.immutables.value.Value;

@Value.Immutable
public interface TestInfo {
    Object getTestInstance();
    Class<?> getTestClass();
    Method getTestMethod();
    String getTestName();
}
