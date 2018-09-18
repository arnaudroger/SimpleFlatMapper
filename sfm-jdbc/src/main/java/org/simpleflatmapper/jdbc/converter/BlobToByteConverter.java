package org.simpleflatmapper.jdbc.converter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.sql.Blob;
import java.sql.SQLException;

public class BlobToByteConverter implements ContextualConverter<Blob, byte[]> {
    @Override
    public byte[] convert(Blob in, Context context) throws Exception {
        if (in != null) {
            long length = in.length();
            if (length > Integer.MAX_VALUE) {
                throw new SQLException("Blob is too big to fit in an byte array length " + in.length());
            }
            return in.getBytes(0, (int) length);
        }
        return null;
    }
}
