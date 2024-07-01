package com.github.jonasrutishauser.cdi.test.core.interceptor;

import static jakarta.interceptor.Interceptor.Priority.LIBRARY_BEFORE;
import static java.util.Collections.emptySet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestExtension;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Intercepted;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

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
        return Stream.<Class<?>>iterate(targetClass, clazz -> clazz.getSuperclass() != null, Class::getSuperclass) //
                .flatMap(this::getSelfAndInterfaces) //
                .map(mockImplementationManager::getMock) //
                .filter(Objects::nonNull) //
                .findFirst() //
                .orElseGet(() -> mockImplementationManager.getMock(declaringClass));
    }

    private Stream<Class<?>> getSelfAndInterfaces(Class<?> clazz) {
        return Stream.concat(Stream.of(clazz), Arrays.stream(clazz.getInterfaces()).flatMap(this::getSelfAndInterfaces));
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
