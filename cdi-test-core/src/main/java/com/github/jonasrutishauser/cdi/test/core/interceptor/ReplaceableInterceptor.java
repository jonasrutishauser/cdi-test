package com.github.jonasrutishauser.cdi.test.core.interceptor;

import static javax.interceptor.Interceptor.Priority.LIBRARY_BEFORE;
import static java.util.Collections.emptySet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestExtension;

import javax.annotation.Priority;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Intercepted;
import javax.enterprise.inject.spi.Bean;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Replaceable
@Interceptor
@Priority(LIBRARY_BEFORE)
public class ReplaceableInterceptor {

    private final TestImplementationManager testImplementationManager;
    private final MockImplementationManager mockImplementationManager;
    private final Class<?> targetClass;
    private final Map<Class<?>, Set<Class<?>>> testImplementations;
    private final Instance<Object> instance;
    private final ConcurrentMap<Class<?>, Object> instances = new ConcurrentHashMap<>();

    @Inject
    public ReplaceableInterceptor(TestImplementationManager testImplementationManager,
            MockImplementationManager mockImplementationManager, CdiTestExtension extension,
            @Intercepted Bean<?> targetBean, @Any Instance<Object> instance) {
        this.testImplementationManager = testImplementationManager;
        this.mockImplementationManager = mockImplementationManager;
        this.targetClass = targetBean.getBeanClass();
        this.testImplementations = extension.getTestImplementations(targetBean);
        this.instance = instance;
    }

    @AroundInvoke
    public Object invokeReplaceable(InvocationContext ctx) throws Throwable {
        Object mock = getMock(ctx.getMethod().getDeclaringClass());
        if (mock != null) {
            return callAlternative(ctx, mock);
        }
        for (Class<?> testImplementation : testImplementations.getOrDefault(ctx.getMethod().getDeclaringClass(), emptySet())) {
            if (testImplementationManager.isEnabled(testImplementation)) {
                return callAlternative(ctx, getInstance(testImplementation));
            }
        }
        return ctx.proceed();
    }

    private Object getMock(Class<?> declaringClass) {
        for (Class<?> clazz = targetClass; clazz != null; clazz = clazz.getSuperclass()) {
            Object mock = mockImplementationManager.getMock(clazz);
            if (mock != null || declaringClass.equals(clazz)) {
                return mock;
            }
        }
        return mockImplementationManager.getMock(declaringClass);
    }

    private Object callAlternative(InvocationContext ctx, Object alternative) throws Throwable {
        try {
            return getAlternativeMethod(ctx.getMethod(), alternative).invoke(alternative, ctx.getParameters());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "method " + ctx.getMethod().getName() + " not found on alternative " + alternative);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private Method getAlternativeMethod(Method method, Object alternative) throws NoSuchMethodException {
        if (method.getDeclaringClass().isAssignableFrom(alternative.getClass())) {
            return method;
        }
        return alternative.getClass().getMethod(method.getName(), method.getParameterTypes());
    }

    private Object getInstance(Class<?> testImplementation) {
        return instances.computeIfAbsent(testImplementation, type -> instance.select(type).get());
    }
}
