package com.github.jonasrutishauser.cdi.test.core.junit;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.function.Executable;

interface TestMethodInterceptor {
    void intercept(Method method, List<Object> parameters, Executable proceed) throws Throwable;
}
