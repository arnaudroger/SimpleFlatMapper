package org.simpleflatmapper.jdbi3;

import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.lang.reflect.Type;
import java.util.Optional;

public class SfmArgumentFactory implements ArgumentFactory {
    @Override
    public Optional<Argument> build(Type type, Object o, ConfigRegistry configRegistry) {
        return null;
    }
}
