package org.simpleflatmapper.csv;


import org.simpleflatmapper.map.ParsingContextProvider;

public class ParsingContextFactoryBuilder {

    private final ParsingContextProvider[] providers;
    private boolean hasProviders = false;

    public ParsingContextFactoryBuilder(int size) {
        this.providers = new ParsingContextProvider[size];
    }
    public void addParsingContextProvider(int index, ParsingContextProvider provider) {
        providers[index] = provider; hasProviders = true;
    }

    public ParsingContextFactory newFactory() {
        return new ParsingContextFactory(hasProviders ? providers : null);
    }
}
