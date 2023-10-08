package com.github.jonasrutishauser.cdi.test.core.service;

import java.lang.reflect.InvocationTargetException;

import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.annotations.ActivatableTestImplementation;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;
import com.github.jonasrutishauser.cdi.test.core.beans.Person;

@TestScoped
@ActivatableTestImplementation
public class BackendServiceTestImplementation extends BackendService {

    private int invocations;

    private RuntimeException exceptionToThrow;

    @Override
    public String storePerson(Person person) {
        if (exceptionToThrow != null) {
            throw exceptionToThrow;
        } else {
            invocations++;
        }
        return null;
    }

    protected void testStarted(@Observes @Initialized(TestScoped.class) TestInfo testInfo) {
        final BackendServiceException serviceException = testInfo.getTestMethod().getAnnotation(BackendServiceException.class);
        if (serviceException != null) {
            try {
                exceptionToThrow = serviceException.value()
                        .getDeclaredConstructor()
                        .newInstance();
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("creating test exception failed", e);
            }
        }
    }

    public int getInvocations() {
        return invocations;
    }
}