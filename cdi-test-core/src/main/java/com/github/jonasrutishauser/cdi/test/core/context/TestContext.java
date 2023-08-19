package com.github.jonasrutishauser.cdi.test.core.context;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import jakarta.enterprise.context.ContextNotActiveException;
import jakarta.enterprise.context.spi.AlterableContext;
import jakarta.enterprise.context.spi.Contextual;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;

public class TestContext implements AlterableContext {

    private final ConcurrentMap<Contextual<?>, Instance<?>> instances = new ConcurrentHashMap<>();

    private final AtomicReference<TestInfo> testInfo = new AtomicReference<>();

    @Override
    public Class<? extends Annotation> getScope() {
        return TestScoped.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        return getSpecial(contextual).orElseGet(() -> (T) instances
                .computeIfAbsent(contextual,
                        c -> new Instance<>(contextual, creationalContext, contextual.create(creationalContext)))
                .getInstance());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Contextual<T> contextual) {
        return getSpecial(contextual).orElseGet(() -> {
            Instance<T> instance = (Instance<T>) instances.get(contextual);
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
        Instance<?> instance = instances.remove(contextual);
        if (instance != null) {
            instance.destroy();
        }
    }

    public void setTestInfo(TestInfo testInfo) {
        this.testInfo.set(Objects.requireNonNull(testInfo));
    }

    TestInfo clearTestInfo() {
        TestInfo oldValue = testInfo.getAndSet(null);
        instances.values().forEach(Instance::destroy);
        instances.clear();
        return oldValue;
    }

    private static class Instance<T> {
        private final Contextual<T> contextual;
        private final CreationalContext<T> ctx;
        private final T instance;
        public Instance(Contextual<T> contextual, CreationalContext<T> ctx, T instance) {
            this.contextual = contextual;
            this.ctx = ctx;
            this.instance = instance;
        }
        public T getInstance() {
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
