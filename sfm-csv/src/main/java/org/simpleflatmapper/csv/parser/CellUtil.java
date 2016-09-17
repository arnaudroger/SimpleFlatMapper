package org.simpleflatmapper.csv.parser;

public class CellUtil {

    public static String toUnescapedString(char[] chars, int start, int end, char escapeChar) {
        if (start < end && chars[start] == escapeChar) {
            start ++;
            end = unescapeInPlace(chars, start, end, escapeChar);
        }
        return  String.valueOf(chars, start, end - start);
    }

    public static int unescapeInPlace(final char[] chars, final int start, final int end, char escapeChar) {
        for(int i = start; i < end - 1; i ++) {
            if (chars[i] == escapeChar) {
                return removeEscapeChars(chars, end, i, escapeChar);
            }
        }

        if (start < end && escapeChar == chars[end - 1]) {
            return end - 1;
        }

        return end;
    }

    private static int removeEscapeChars(final char[] chars, final int end, final int firstEscapeChar, char escapeChar) {
        int j = firstEscapeChar;
        boolean escaped = true;
        for(int i = firstEscapeChar + 1;i < end; i++) {
            escaped = chars[i] == escapeChar && ! escaped;
            if (!escaped) {
                chars[j++] = chars[i];
            }
        }
        return j;
    }
}
