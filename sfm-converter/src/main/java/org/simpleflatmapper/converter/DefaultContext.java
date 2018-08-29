package org.simpleflatmapper.converter;

public class DefaultContext implements Context {

    private final Object[] resources;

    public DefaultContext(Object[] resources) {
        this.resources = resources;
    }


    @Override
    public <T> T context(int i) {
        return (T) resources[i];
    }
}
