package org.simpleflatmapper.lightningcsv.impl;

import org.simpleflatmapper.lightningcsv.parser.AbstractCharConsumer;
import org.simpleflatmapper.lightningcsv.parser.CellPreProcessor;
import org.simpleflatmapper.lightningcsv.parser.CharBuffer;
import org.simpleflatmapper.lightningcsv.parser.TextFormat;

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