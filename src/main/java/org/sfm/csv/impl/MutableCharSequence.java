package org.sfm.csv.impl;

public class MutableCharSequence implements CharSequence {
    private char[] value;
    private int start;
    private int length;

    public void setValue(char[] value) {
        this.value = value;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= length) throw new StringIndexOutOfBoundsException(index);
        return value[start + index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start >= length) throw new StringIndexOutOfBoundsException(start);
        if (end > length) throw new StringIndexOutOfBoundsException(end);
        return new String(value, this.start + start, this.start + end);
    }

    public String toString() {
        return new String(value, start, length);
    }
}
