package com.github.jonasrutishauser.cdi.test.core.junit;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestInstances;

import com.github.jonasrutishauser.cdi.test.api.ImmutableTestInfo;

public class CdiTestJunitExtension implements BeforeEachCallback, AfterTestExecutionCallback, AfterEachCallback, InvocationInterceptor {

    private static final Namespace NS = Namespace.create(CdiTestJunitExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        CdiContainer cdiContainer = context.getRoot().getStore(NS).getOrComputeIfAbsent(CdiContainer.class);
        cdiContainer.setTest(ImmutableTestInfo.builder().testClass(context.getRequiredTestClass())
                .testInstance(context.getRequiredTestInstance()).testMethod(context.getRequiredTestMethod())
                .testName(context.getDisplayName()).build());
        inject(cdiContainer, context.getStore(NS), context.getRequiredTestInstances(), context.getRequiredTestClass(), TestMethodInterceptor.class);
    }

    private void inject(CdiContainer cdiContainer, Store store, TestInstances testInstances, Class<?> testClass, Object storeKey) {
        Optional<?> instance = testInstances.findInstance(testClass);
        if (instance.isPresent()) {
            store.put(storeKey, cdiContainer.inject(instance.get(), testClass));
            if (testClass.getEnclosingClass() != null) {
                inject(cdiContainer, store, testInstances, testClass.getEnclosingClass(), new Object());
            }
        }
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
            ExtensionContext extensionContext) throws Throwable {
        extensionContext.getStore(NS).get(TestMethodInterceptor.class, TestMethodInterceptor.class)
                .intercept(invocationContext.getExecutable(), invocationContext.getArguments(), invocation::proceed);
    }
    
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        context.getRoot().getStore(NS).get(CdiContainer.class, CdiContainer.class).testEnded();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        context.getRoot().getStore(NS).get(CdiContainer.class, CdiContainer.class).clearTest();
    }

}
