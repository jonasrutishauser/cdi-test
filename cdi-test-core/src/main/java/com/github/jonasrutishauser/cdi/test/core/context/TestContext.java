package com.github.jonasrutishauser.cdi.test.core.context;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

public class TestContext implements AlterableContext {

    private final ConcurrentMap<Contextual<?>, BeanInstance<?>> instances = new ConcurrentHashMap<>();

    private final AtomicReference<TestInfo> testInfo = new AtomicReference<>();

    @Override
    public Class<? extends Annotation> getScope() {
        return TestScoped.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        return getSpecial(contextual).orElseGet(() -> (T) instances
                .computeIfAbsent(contextual, c -> new BeanInstance<>(contextual, creationalContext)).getInstance());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Contextual<T> contextual) {
        return getSpecial(contextual).orElseGet(() -> {
            BeanInstance<T> instance = (BeanInstance<T>) instances.get(contextual);
            return instance == null ? null : instance.getInstance();
        });
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> getSpecial(Contextual<T> contextual) {
        if (!isActive()) {
            throw new ContextNotActiveException();
        }
        if (contextual instanceof Bean && TestContext.class.equals(((Bean<T>) contextual).getBeanClass())) {
            return Optional.of((T) testInfo.get());
        }
        if (contextual instanceof Bean && testInfo.get().getTestClass().equals(((Bean<T>) contextual).getBeanClass())) {
            return Optional.of((T) testInfo.get().getTestInstance());
        }
        return Optional.empty();
    }

    @Override
    public boolean isActive() {
        return testInfo.get() != null;
    }

    @Override
    public void destroy(Contextual<?> contextual) {
        BeanInstance<?> instance = instances.remove(contextual);
        if (instance != null) {
            instance.destroy();
        }
    }

    public void setTestInfo(TestInfo testInfo) {
        this.testInfo.set(Objects.requireNonNull(testInfo));
    }

    TestInfo clearTestInfo() {
        TestInfo oldValue = testInfo.getAndSet(null);
        instances.values().forEach(BeanInstance::destroy);
        instances.clear();
        return oldValue;
    }

    private static class BeanInstance<T> {
        private final Contextual<T> contextual;
        private final CreationalContext<T> ctx;
        private T instance;

        public BeanInstance(Contextual<T> contextual, CreationalContext<T> ctx) {
            this.contextual = contextual;
            this.ctx = ctx;
        }

        public T getInstance() {
            if (instance == null) {
                synchronized (this) {
                    if (instance == null) {
                        instance = contextual.create(ctx);
                    }
                }
            }
            return instance;
        }

        public void destroy() {
            contextual.destroy(instance, ctx);
        }
    }

    TestInfo getTestInfo() {
        return testInfo.get();
    }
}
