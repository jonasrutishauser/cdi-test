package com.github.jonasrutishauser.cdi.test.core.junit;

import static jakarta.enterprise.inject.spi.InterceptionType.AROUND_INVOKE;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.jupiter.api.function.Executable;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.enterprise.inject.spi.Interceptor;

class CdiTestMethodInterceptor<T> implements TestMethodInterceptor, CloseableResource {
    private final BeanManager beanManager;
    private final Class<T> testClass;
    private final T testInstance;
    private final InjectionTarget<T> injectionTarget;
    private final CreationalContext<T> ctx;

    public CdiTestMethodInterceptor(BeanManager beanManager, Class<T> testClass, T testInstance,
            InjectionTarget<T> injectionTarget, CreationalContext<T> ctx) {
        this.beanManager = beanManager;
        this.testClass = testClass;
        this.testInstance = testInstance;
        this.injectionTarget = injectionTarget;
        this.ctx = ctx;
    }

    public void intercept(Method method, List<Object> parameters, Executable proceed) throws Throwable {
        Annotation[] interceptorBindings = getInterceptorBindings(beanManager.createAnnotatedType(testClass), method);
        List<Interceptor<?>> interceptors = Collections.emptyList();
        if (interceptorBindings.length != 0) {
            interceptors = beanManager.resolveInterceptors(AROUND_INVOKE, interceptorBindings);
        }
        new InvocationContext(testInstance, method, parameters.toArray(), new HashMap<>(), interceptors, proceed)
                .proceed();
    }

    private class InvocationContext extends TestMethodInvocationContext<T> {
        private final List<Interceptor<?>> remainingInterceptors;
        private final Executable proceed;

        public InvocationContext(T target, Method method, Object[] parameters, Map<String, Object> contextData,
                List<Interceptor<?>> remainingInterceptors, Executable proceed) {
            super(target, method, parameters, contextData);
            this.remainingInterceptors = remainingInterceptors;
            this.proceed = proceed;
        }

        @Override
        public Object proceed() throws Exception {
            if (remainingInterceptors.isEmpty()) {
                try {
                    proceed.execute();
                } catch (Exception e) {
                    throw e;
                } catch (Error e) {
                    throw e;
                } catch (Throwable t) {
                    throw new UndeclaredThrowableException(t);
                }
            } else {
                callInterceptor(remainingInterceptors.get(0));
            }
            return null;
        }

        private <I> void callInterceptor(Interceptor<I> interceptor) throws Exception {
            @SuppressWarnings("unchecked")
            I instance = (I) beanManager.getReference(interceptor, interceptor.getBeanClass(), ctx);
            interceptor.intercept(AROUND_INVOKE, instance,
                    new InvocationContext(testInstance, getMethod(), getParameters(), getContextData(),
                            remainingInterceptors.subList(1, remainingInterceptors.size()), proceed));
        }
    }

    private Annotation[] getInterceptorBindings(AnnotatedType<?> annotatedType, Method method) {
        return Stream
                .concat(annotatedType.getAnnotations().stream(),
                        annotatedType.getMethods().stream().filter(m -> method.equals(m.getJavaMember()))
                                .map(AnnotatedMethod::getAnnotations).flatMap(Set::stream))
                .filter(a -> beanManager.isInterceptorBinding(a.annotationType())).toArray(Annotation[]::new);
    }

    @Override
    public void close() {
        injectionTarget.preDestroy(testClass.cast(testInstance));
        ctx.release();
    }
}
