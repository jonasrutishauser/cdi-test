package com.github.jonasrutishauser.cdi.test.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jonasrutishauser.cdi.test.core.beans.Person;
import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;
import com.github.jonasrutishauser.cdi.test.core.service.BackendServiceTestPartialImplementation;
import com.github.jonasrutishauser.cdi.test.core.service.SampleService;

import jakarta.inject.Inject;

/**
 * Demo and test for the probably partly exotic case of an only partially
 * complete test implementation:
 * <p>
 * Only the actually available methods will be redirected dynamically, all the
 * others will throw an {@link IllegalStateException}. So for a test
 * implementation you're not forced to implement all of the methods that you
 * might not even expect to be used!
 * </p>
 */
@ExtendWith(CdiTestJunitExtension.class)
class ActivatePartialAlternativeForRegularBeanTest {

    @Inject
    private SampleService sampleService;
    @Inject
    private BackendServiceTestPartialImplementation testBackendService;

    @Test
    void callTestActivatedService() {
        Person person = new Person();
        Assertions.assertThrows(IllegalStateException.class, () -> sampleService.storePerson(person));
    }

}
