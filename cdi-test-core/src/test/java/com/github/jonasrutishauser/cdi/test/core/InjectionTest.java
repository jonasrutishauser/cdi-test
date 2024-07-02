package com.github.jonasrutishauser.cdi.test.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.Principal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.core.beans.ConstructorInjected;
import com.github.jonasrutishauser.cdi.test.core.beans.CustomPerson;
import com.github.jonasrutishauser.cdi.test.core.beans.Person;
import com.github.jonasrutishauser.cdi.test.core.beans.Request;
import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;
import com.github.jonasrutishauser.cdi.test.core.service.BackendService;
import com.github.jonasrutishauser.cdi.test.core.service.OverriddenService;
import com.github.jonasrutishauser.cdi.test.core.service.TestQualifier;

import jakarta.inject.Inject;

@ExtendWith(CdiTestJunitExtension.class)
class InjectionTest {

    @Inject
    private Person person;
    @Inject
    private TestInfo testInfo;
    @Inject
    private ConstructorInjected constructorInjected;
    @Inject
    private BackendService backendService;
    @Inject
    private OverriddenService sampleService;
    @TestQualifier
    @Inject
    private OverriddenService qualifiedSampleService;
    @CustomPerson
    @Inject
    private Person customPerson;

    @Inject
    private Principal principal;

    @Test
    void injection() {
        assertNotNull(person);
        assertNotNull(constructorInjected);
        assertNotNull(customPerson);
        assertNotNull(principal);
    }

    @Test
    void testInfo() throws NoSuchMethodException {
        assertNotNull(testInfo);
        assertEquals(this.getClass(), testInfo.getTestClass());
        assertEquals("testInfo()", testInfo.getTestName());
        assertEquals(this, testInfo.getTestInstance());
        assertEquals(InjectionTest.class.getDeclaredMethod("testInfo"), testInfo.getTestMethod());
    }

    @Test
    void proxiedCostructorInjection() {
        assertNotNull(constructorInjected.getPerson());
        assertNotNull(constructorInjected.getRequest());
    }

    @Test
    void persons() {
        checkPersonWorks(person);
        checkPersonWorks(constructorInjected.getPerson());
    }

    @Test
    void qualifierInjectionWorkingOnTestedClass() {
        assertEquals("OverridingServiceImpl", backendService.storePerson(person));
        assertEquals("QualifiedOverriddenServiceImpl", backendService.storePersonQualified(person));
    }

    @Test
    void qualifierInjectionWorkingOnTestCase() {
        assertEquals("OverridingServiceImpl", sampleService.serviceMethod());
        assertEquals("QualifiedOverriddenServiceImpl", qualifiedSampleService.serviceMethod());
    }

    @Test
    void principal() {
        assertEquals("principal()", principal.getName());
    }

    private void checkPersonWorks(Person person) {
        person.setName("test");
        assertEquals("test", person.getName());
    }

    @Nested
    class NestedTest {
        @Inject
        private Request request;

        @Test
        void injection() {
            assertNotNull(request);
            InjectionTest.this.injection();
        }
    }
}
