package com.github.jonasrutishauser.cdi.testing;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.jonasrutishauser.cdi.test.core.junit.CdiTestJunitExtension;

@ExtendWith(CdiTestJunitExtension.class)
@ExtendWith(MockitoExtension.class)
abstract class BaseTest {

}
