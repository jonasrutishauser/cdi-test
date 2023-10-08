package com.github.jonasrutishauser.cdi.test.core.junit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import javax.interceptor.InvocationContext;

abstract class TestMethodInvocationContext<T> implements InvocationContext {

    private final Map<String, Object> contextData;

    private final T target;
    private final Method method;
    private Object[] parameters;
    
    protected TestMethodInvocationContext(T target, Method method, Object[] parameters, Map<String, Object> contextData) {
        this.target = target;
        this.method = method;
        this.parameters = parameters;
        this.contextData = contextData;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Object getTimer() {
        return null;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Constructor<?> getConstructor() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public void setParameters(Object[] params) {
        throw new UnsupportedOperationException("Not supported by junit");
    }

    @Override
    public Map<String, Object> getContextData() {
        return contextData;
    }
}
