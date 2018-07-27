package org.simpleflatmapper.lightningcsv.impl;

import org.simpleflatmapper.lightningcsv.parser.AbstractCharConsumer;
import org.simpleflatmapper.lightningcsv.parser.CellPreProcessor;
import org.simpleflatmapper.lightningcsv.parser.CharBuffer;
import org.simpleflatmapper.lightningcsv.parser.CharConsumerFactory;
import org.simpleflatmapper.lightningcsv.parser.ConfigurableCharConsumer;
import org.simpleflatmapper.lightningcsv.parser.TextFormat;

public class ConfigurableCharConsumerFactory extends CharConsumerFactory {
    public AbstractCharConsumer newCharConsumer(TextFormat textFormat, CharBuffer charBuffer, CellPreProcessor cellTransformer, boolean specialisedCharConsumer) {
        return new ConfigurableCharConsumer(charBuffer, textFormat, cellTransformer);
    }
}