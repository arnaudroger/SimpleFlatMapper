package org.simpleflatmapper.lightningcsv.parser;

import org.simpleflatmapper.lightningcsv.impl.AsmCharConsumerFactory;
import org.simpleflatmapper.lightningcsv.impl.ConfigurableCharConsumerFactory;

public abstract class CharConsumerFactory {
    public abstract AbstractCharConsumer newCharConsumer(TextFormat textFormat, CharBuffer charBuffer, CellPreProcessor cellTransformer, boolean specialisedCharConsumer);
    
    
    public static CharConsumerFactory newInstance() {
        
        try {
            return new AsmCharConsumerFactory();
        } catch (Throwable t) {
            // ignore
        }
        return new ConfigurableCharConsumerFactory();
    }
}