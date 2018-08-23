package org.simpleflatmapper.csv;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;

public final class CsvRow {

    private final CsvColumnKey[] keys;
    private final int nbColumns;
    
    private char[] buffer;
    private int bufferLength;
    
    private int[] fieldsBoundaries;
    private int currentIndex;

    public CsvRow(CsvColumnKey[] keys, int maxIndex) {
        buffer = new char[512];
        nbColumns = maxIndex + 1;
        this.keys = keys;
        fieldsBoundaries = new int[nbColumns * 2];
    }

    public CsvColumnKey[] getKeys() {
        return keys;
    }

    public void reset() {
        Arrays.fill(fieldsBoundaries,  0);
        bufferLength = 0;
        currentIndex = 0;
    }
    
    public void addValue(char[] buffer, int offset, int length) {
        if (currentIndex >= nbColumns) {
            return; // ignore
        }
        
        if (bufferLength + length > buffer.length) {
            buffer = Arrays.copyOf(buffer, buffer.length * 2);
        }
        
        System.arraycopy(buffer, offset, this.buffer, bufferLength, length);
        
        fieldsBoundaries[currentIndex * 2] = bufferLength;
        bufferLength += length;
        fieldsBoundaries[currentIndex * 2 + 1] = bufferLength;

        currentIndex++;

    }
    
    public <T> T read(CellValueReader<T> cellValueReader, int i) {
        int start = fieldsBoundaries[i * 2];
        int end = fieldsBoundaries[i * 2 + 1];

        return cellValueReader.read(buffer, start, end, null);
    } 
    
       
    public CharSequence getCharSequence(int i) {
        int start = fieldsBoundaries[i * 2];
        int end = fieldsBoundaries[i * 2 + 1];
        return new CharSequenceImpl(buffer, start, end);
    }

    public String getString(int i) {
        int start = fieldsBoundaries[i * 2];
        int end = fieldsBoundaries[i * 2 + 1];
        if (start == end) return null;
        return String.valueOf(buffer, start, end - start);
    }
    
    public int length(int i) {
        int start = fieldsBoundaries[i * 2];
        int end = fieldsBoundaries[i * 2 + 1];
        return end - start;        
    }

    private boolean isEmpty(int i) {
        return length(i) == 0;
    }

    public byte getByte(int i) {
        return Byte.parseByte(getString(i));
    }
    public char getChar(int i) {
        if (isEmpty(i)) return 0;
        return (char) Integer.parseInt(getString(i));
    }
    public short getShort(int i) {
        if (isEmpty(i)) return 0;
        return Short.parseShort(getString(i));
    }
    public int getInt(int i) {
        if (isEmpty(i)) return 0;
        return Integer.parseInt(getString(i));
    }
    public long getLong(int i) {
        if (isEmpty(i)) return 0;
        return Long.parseLong(getString(i));
    }
    public float getFloat(int i) {
        if (isEmpty(i)) return 0;
        return Float.parseFloat(getString(i));
    }
    public double getDouble(int i) {
        if (isEmpty(i)) return 0;
        return Double.parseDouble(getString(i));
    }
    public boolean getBoolean(int i) {
        if (isEmpty(i)) return false;
        int start = fieldsBoundaries[i * 2];
        int end = fieldsBoundaries[i * 2 + 1];

        return parseBoolean(buffer, start, end - start);
    }

    public static boolean parseBoolean(char[] chars, int offset, int length) {
        switch (length) {
            case 0:
                return false;
            case 1:
                switch (chars[offset]) {
                    case 0:
                    case '0':
                    case 'F':
                    case 'f':
                    case 'n':
                    case 'N':
                        return false;
                    default:
                        return true;
                }
            case 2:
                if ((chars[offset] == 'N' || chars[offset] == 'n')
                        && (chars[offset + 1] == 'O' || chars[offset + 1] == 'o')) {
                    return false;
                }
            case 5:
                if (
                        (chars[offset] == 'F' || chars[offset] == 'f')
                                && (chars[offset + 1] == 'A' || chars[offset + 1] == 'a')
                                && (chars[offset + 2] == 'L' || chars[offset + 2] == 'l')
                                && (chars[offset + 3] == 'S' || chars[offset + 3] == 's')
                                && (chars[offset + 4] == 'E' || chars[offset + 4] == 'e')
                ) {
                    return false;
                }
        }
        return true;
    }


    public Byte getBoxedByte(int i) {
        if (isEmpty(i)) return null;
        return getByte(i);
    }

    public Short getBoxedShort(int i) {
        if (isEmpty(i)) return null;
        return getShort(i);
    }
    public Character getBoxedChar(int i) {
        if (isEmpty(i)) return null;
        return getChar(i);
    }
    public Integer getBoxedInt(int i) {
        if (isEmpty(i)) return null;
        return getInt(i);
    }
    public Long getBoxedLong(int i) {
        if (isEmpty(i)) return null;
        return getLong(i);
    }
    public Float getBoxedFloat(int i) {
        if (isEmpty(i)) return null;
        return getFloat(i);
    }
    public Double getBoxedDouble(int i) {
        if (isEmpty(i)) return null;
        return getDouble(i);
    }
    public Boolean getBoxedBoolean(int i) {
        if (isEmpty(i)) return null;
        return getBoolean(i);
    }
    
    public BigDecimal getBigDecimal(int i) {
        if (isEmpty(i)) return null;
        return new BigDecimal(getString(i));
    }

    public BigInteger getBigInteger(int i) {
        if (isEmpty(i)) return null;
        return new BigInteger(getString(i));
    }

    public UUID getUUID(int i) {
        if (isEmpty(i)) return null;
        return UUID.fromString(getString(i));
    }

    public int getNbColumns() {
        return nbColumns;
    }

    public boolean hasData() {
        return currentIndex > 0;
    }

    private static class CharSequenceImpl implements CharSequence {
        private final char[] buffer;
        private final int start;
        private final int end;
        
        public CharSequenceImpl(char[] buffer, int start, int end) {
            this.buffer = buffer;
            this.start = start;
            this.end = end;
        }

        @Override
        public int length() {
            return end - start;
        }

        @Override
        public char charAt(int index) {
            return buffer[start + index];
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return new CharSequenceImpl(buffer, this.start + start, this.start + end);
        }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CharSequence)) {
                return false;
            }
            CharSequence cs = (CharSequence) o;
            if (cs.length() != length()) return false;
            
            for (int i = 0; i < length(); i++) {
                if (charAt(i) != cs.charAt(i)) return false;
            }
            
            return true;
        }
        
        @Override 
        public int hashCode() {
            int h = 0;
            if (start < end) {
                char val[] = buffer;

                for (int i = start; i < end; i++) {
                    h = 31 * h + val[i];
                }
            }
            return h;
        }

        @Override
        public String toString() {
            return String.valueOf(buffer, start, end - start);
        }
    }
}
