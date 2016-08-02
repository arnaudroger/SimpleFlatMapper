package org.simpleflatmapper.utils;

import java.util.UUID;

public class UUIDRWBS {

    public static byte[] toBytes(UUID uuid) {
        byte[] bytes = new byte[16];

        writeLong(bytes, 0, uuid.getMostSignificantBits());
        writeLong(bytes, 8, uuid.getLeastSignificantBits());

        return bytes;
    }

    private static void writeLong(byte[] bytes, int i, long l) {
        bytes[i + 7] = (byte)(l      );
        bytes[i + 6] = (byte)((l>>8) );
        bytes[i + 5] = (byte)((l>>16));
        bytes[i + 4] = (byte)((l>>24));
        bytes[i + 3] = (byte)((l>>32));
        bytes[i + 2] = (byte)((l>>40));
        bytes[i + 1] = (byte)((l>>48));
        bytes[i    ] = (byte)((l>>56));
    }

    private static long readLong(byte[] bytes, int i) {
        long l =
                   ((long)bytes[i + 7] & 0xff)
                | (((long)bytes[i + 6] & 0xff) <<  8)
                | (((long)bytes[i + 5] & 0xff) << 16)
                | (((long)bytes[i + 4] & 0xff) << 24)
                | (((long)bytes[i + 3] & 0xff) << 32)
                | (((long)bytes[i + 2] & 0xff) << 40)
                | (((long)bytes[i + 1] & 0xff) << 48)
                | (((long)bytes[i    ] & 0xff) << 56)
                ;

        return l;
    }

    public static UUID fromBytes(byte[] bytes) {
        return new UUID(readLong(bytes, 0), readLong(bytes, 8));
    }


    public static void main(String[] args) {
        final UUID uuid = UUID.randomUUID();

        byte[] b1 = UUIDRWBB.toBytes(uuid);
        byte[] b2 = UUIDRWBS.toBytes(uuid);

        if (b1.length != b2.length) {
            throw new IllegalStateException();
        }

        for(int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]) {
                throw new IllegalStateException();
            }
        }

        if  (!uuid.equals(UUIDRWBB.fromBytes(b1)) || !uuid.equals(UUIDRWBS.fromBytes(b1))) {
            throw new IllegalStateException();
        }
    }

}
