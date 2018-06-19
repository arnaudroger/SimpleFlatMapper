package org.simpleflatmapper.lightningcsv;

import java.io.IOException;

public interface CellWriter {
    void writeValue(CharSequence sequence, Appendable appendable) throws IOException;

    void nextCell(Appendable target) throws IOException;

    void endOfRow(Appendable target) throws IOException;

}
