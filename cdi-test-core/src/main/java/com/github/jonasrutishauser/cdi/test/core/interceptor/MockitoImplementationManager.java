package com.github.jonasrutishauser.cdi.test.core.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.jboss.weld.exceptions.IllegalStateException;
import org.mockito.Mockito;
import org.mockito.listeners.MockCreationListener;
import org.mockito.mock.MockCreationSettings;

import com.github.jonasrutishauser.cdi.test.api.TestInfo;
import com.github.jonasrutishauser.cdi.test.api.context.TestScoped;

import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;

@TestScoped
@Alternative
@Priority(0)
public class MockitoImplementationManager extends MockImplementationManager implements MockCreationListener {

    private final Map<Class<?>, Object> mocks = new HashMap<>();

    @Override
    public void onMockCreated(Object mock, @SuppressWarnings("rawtypes") MockCreationSettings settings) {
        Class<?> mockType = settings.getTypeToMock();
        if (mocks.containsKey(mockType)) {
            throw new IllegalStateException("mock " + mockType + " already defined");
        }
        mocks.put(mockType, mock);
    }

    @Override
    public Object getMock(Class<?> type) {
        return mocks.get(type);
    }

    void install(@Observes @Initialized(TestScoped.class) TestInfo event) {
        Mockito.framework().addListener(this);
    }

    @PreDestroy
    void uninstall() {
        Mockito.framework().removeListener(this);
    }

}
