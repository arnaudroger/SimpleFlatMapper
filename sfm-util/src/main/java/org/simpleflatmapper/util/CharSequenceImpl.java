package org.simpleflatmapper.util;

public final class CharSequenceImpl implements CharSequence {
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
