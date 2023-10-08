package com.github.jonasrutishauser.cdi.microprofile;

import javax.annotation.Priority;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * Converter for properties of type Horse.
 */
@Priority(1000)
public class HorseConverter implements Converter<Horse> {
    @Override
    public Horse convert(String value) {
        return ImmutableHorse.builder().name(value).build();
    }
}
