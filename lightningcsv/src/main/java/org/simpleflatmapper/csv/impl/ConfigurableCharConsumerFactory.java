package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.parser.AbstractCharConsumer;
import org.simpleflatmapper.csv.parser.CellPreProcessor;
import org.simpleflatmapper.csv.parser.CharBuffer;
import org.simpleflatmapper.csv.parser.ConfigurableCharConsumer;
import org.simpleflatmapper.csv.parser.TextFormat;

public class ConfigurableCharConsumerFactory extends CharConsumerFactory {
    public AbstractCharConsumer newCharConsumer(TextFormat textFormat, CharBuffer charBuffer, CellPreProcessor cellTransformer, boolean specialisedCharConsumer) {
        return new ConfigurableCharConsumer(charBuffer, textFormat, cellTransformer);
    }
}