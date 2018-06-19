package org.simpleflatmapper.lightningcsv.parser;

import java.io.IOException;

public class BufferOverflowException extends IOException {
    public BufferOverflowException(String s) {
        super(s);
    }
}
