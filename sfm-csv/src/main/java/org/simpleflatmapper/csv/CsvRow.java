package org.simpleflatmapper.csv;

import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.lightningcsv.parser.CharBuffer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;

public final class CsvRow implements CellConsumer {

    private final CsvColumnKey[] keys;
    private final int nbColumns;
    
    private final CharBuffer charBuffer;

    protected int[] fieldsBoundaries;
    protected int currentIndex;
    protected int rowStartMark;

    public CsvRow(CsvColumnKey[] keys, int maxIndex, CharBuffer charBuffer) {
        nbColumns = maxIndex + 1;
        this.keys = keys;
        this.charBuffer = charBuffer;
        fieldsBoundaries = new int[nbColumns * 2];
    }
    
    public int getIndex(String column) {
        for(CsvColumnKey key : keys) {
            if (key.getName().equals(column)) {
                return key.getIndex();
            }
        }
        return -1;
    }

    public CsvColumnKey[] getKeys() {
        return keys;
    }

    public void reset() {
        int[] fieldsBoundaries = this.fieldsBoundaries;
        for(int i = 0; i < fieldsBoundaries.length; i++)
            fieldsBoundaries[i] = 0;
        currentIndex = 0;
    }
    
    public void addValue(int offset, int length) {
        int index = this.currentIndex;
        int[] fieldsBoundaries = this.fieldsBoundaries;
        if (index + 1 < fieldsBoundaries.length) {
            fieldsBoundaries[index] = offset;
            fieldsBoundaries[index + 1] = length;
            currentIndex = index + 2;
        }
    }
    
    public <T> T read(CellValueReader<T> cellValueReader, int i) {
        int rowOffset = fieldsBoundaries[i * 2];
        int length = fieldsBoundaries[i * 2 + 1];
        return cellValueReader.read(charBuffer.buffer, rowStartMark + rowOffset, length , null);
    } 
    
       
    public CharSequence getCharSequence(int i) {
        int length = fieldsBoundaries[i * 2 + 1];
        if (length == 0) return null;
        int rowOffset = fieldsBoundaries[i * 2];
        return new CharSequenceImpl(charBuffer.buffer, rowStartMark + rowOffset, rowStartMark + rowOffset + length);
    }

    public String getString(int i) {
        int length = fieldsBoundaries[i * 2 + 1];
        if (length == 0) return null;

        int rowOffset = fieldsBoundaries[i * 2];
        return new String(charBuffer.buffer, rowStartMark + rowOffset, length);
    }
    
    public int length(int i) {
        return fieldsBoundaries[i * 2 + 1];
    }

    private boolean isEmpty(int i) {
        return length(i) == 0;
    }

    public byte getByte(int i) {
        if (isEmpty(i)) return 0;
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
        int rowOffset = fieldsBoundaries[i * 2];
        int length = fieldsBoundaries[i * 2 + 1];
        return parseInt(charBuffer.buffer, rowStartMark + rowOffset, length);
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
        int rowOffset = fieldsBoundaries[i * 2];
        int length = fieldsBoundaries[i * 2 + 1];

        return parseBoolean(charBuffer.buffer,  rowStartMark + rowOffset, length);
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

    public boolean containsOnly(char c) {
        for(int column = 0; column < nbColumns; column ++) {
            int start = fieldsBoundaries[column * 2];
            int end = start + fieldsBoundaries[column * 2 + 1];
            for(int i = start; i < end; i++) {
                if (charBuffer.buffer[rowStartMark + i] != c) return false;
            }
        }
        return true;
    }

    @Override
    public void newCell(char[] chars, int offset, int length) {
        int index = currentIndex;
        int[] fieldsBoundaries = this.fieldsBoundaries;
        if (index + 1 < fieldsBoundaries.length) {
            fieldsBoundaries[index] = offset - charBuffer.rowStartMark;
            fieldsBoundaries[index + 1] = length;
            currentIndex = index + 2;
        }
    }

    @Override
    public boolean endOfRow() {
        rowStartMark = charBuffer.rowStartMark;
        return true;
    }

    @Override
    public void end() {
        rowStartMark = charBuffer.rowStartMark;
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


    private static final int RADIX = 10;

    public static int parseInt(char[] chars, int from, int len)
            throws NumberFormatException
    {

        int result = 0;
        boolean negative = false;
        int i = from;
        int to = from + len;
        int limit = -Integer.MAX_VALUE;
        int multmin;
        int digit;

        if (len > 0) {
            char firstChar = chars[i];
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    return numberFormatException(chars, from, to);
                }

                if (len == 1) // Cannot have lone "+" or "-"
                    numberFormatException(chars, from, to);
                i++;
            }
            multmin = limit / RADIX;
            while (i < to) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(chars[i++],RADIX);
                if (digit < 0) {
                    numberFormatException(chars, from, to);
                }
                if (result < multmin) {
                    numberFormatException(chars, from, to);
                }
                result *= RADIX;
                if (result < limit + digit) {
                    numberFormatException(chars, from, to);
                }
                result -= digit;
            }
        } else {
            return 0;
        }
        return negative ? result : -result;
    }

    public static int numberFormatException(char[] chars, int from, int to) {
        throw new NumberFormatException("For input string: \"" + new String(chars, from, to - from) + "\"");
    }
}
