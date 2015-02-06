package org.sfm.csv;


import org.sfm.csv.impl.ParsingContextFactory;
import org.sfm.map.ParsingContextProvider;

public class ParsingContextFactoryBuilder {

    private ParsingContextProvider[] providers;
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
