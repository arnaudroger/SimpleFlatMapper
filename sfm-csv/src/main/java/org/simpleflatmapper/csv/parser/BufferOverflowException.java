package org.simpleflatmapper.csv.parser;

import java.io.IOException;

public class BufferOverflowException extends IOException {
    public BufferOverflowException(String s) {
        super(s);
    }
}
