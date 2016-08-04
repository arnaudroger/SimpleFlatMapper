package org.simpleflatmapper.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDHelper {

    public static byte[] toBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        ByteBuffer
                .wrap(bytes)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        return bytes;
    }

    public static UUID fromBytes(byte[] bytes) {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}
