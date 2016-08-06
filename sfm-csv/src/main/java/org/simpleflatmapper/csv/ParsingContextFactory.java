package org.simpleflatmapper.csv;


import org.simpleflatmapper.map.ParsingContextProvider;

public class ParsingContextFactory {

    private static final ParsingContext NULL_PARSING_CONTEXT = new ParsingContext(null);
    private final ParsingContextProvider[] providers;
	
	public ParsingContextFactory(ParsingContextProvider[] providers) {
	    this.providers = providers;
	}


	public ParsingContext newContext() {
        if (providers != null) {
            Object[] context = new Object[providers.length];

            for (int i = 0; i < providers.length; i++) {
                ParsingContextProvider provider = providers[i];
                if (provider != null) {
                    context[i] = provider.newContext();
                }
            }
            return new ParsingContext(context);
        } else {
            return NULL_PARSING_CONTEXT;
        }
	}
}
