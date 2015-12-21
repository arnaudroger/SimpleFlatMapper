package org.sfm.utils;

import java.io.IOException;

public interface IOFunction<P, R> {
    R apply(P p) throws IOException;
}
