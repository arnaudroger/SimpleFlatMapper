package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.parser.AbstractCharConsumer;
import org.simpleflatmapper.csv.parser.CellPreProcessor;
import org.simpleflatmapper.csv.parser.CharBuffer;
import org.simpleflatmapper.csv.parser.TextFormat;

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