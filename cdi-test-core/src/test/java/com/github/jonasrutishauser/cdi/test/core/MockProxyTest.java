package com.github.jonasrutishauser.cdi.test.core;

import static org.mockito.Mockito.verify;

import javax.inject.Inject;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.jonasrutishauser.cdi.test.core.beans.Person;
import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;
import com.github.jonasrutishauser.cdi.test.core.service.BackendService;
import com.github.jonasrutishauser.cdi.test.core.service.SampleService;

/**
 * Show mock injection using test method arguments as supported by the Mockito
 * extension.
 */
@Tag("mockito")
@ExtendWith(CdiTestJunitExtension.class)
@ExtendWith(MockitoExtension.class)
class MockProxyTest {

    @Inject
    private SampleService sampleService;

    @Test
    void createPersonWithMockBackend(@Mock BackendService backendService) {
        Person person = new Person();
        sampleService.storePerson(person);
        verify(backendService).storePerson(person);
    }

}
