package com.github.jonasrutishauser.cdi.microprofile;

import org.immutables.value.Value;

@Value.Immutable
public interface Horse {
    String getName();
}
