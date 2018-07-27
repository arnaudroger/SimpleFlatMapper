package org.simpleflatmapper.csv;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.lightningcsv.parser.TextFormat;

public class UnescapeBenchmark {

/*
Benchmark                   (type)                           (value)  Mode  Cnt   Score   Error  Units
UnescapeBenchmark.unescape       1       "unescape no escaped quote"  avgt   50  17.402 ± 0.238  ns/op
UnescapeBenchmark.unescape       1  "unescape one ""escaped quote"""  avgt   50  25.521 ± 0.595  ns/op
UnescapeBenchmark.unescape       1   """a""a""a""a""a""a""a""a""a"""  avgt   50  24.265 ± 0.435  ns/op
UnescapeBenchmark.unescape       2       "unescape no escaped quote"  avgt   50  19.401 ± 0.503  ns/op
UnescapeBenchmark.unescape       2  "unescape one ""escaped quote"""  avgt   50  27.177 ± 0.602  ns/op
UnescapeBenchmark.unescape       2   """a""a""a""a""a""a""a""a""a"""  avgt   50  27.313 ± 0.445  ns/op
B
 */


    interface Unescaper {
         void newCell(char[] chars, int start, int end, CellConsumer cellConsumer);
    }

    @Benchmark
    public void unescape(UnescapeParam param, Blackhole blackhole) {
        param.unescaper.newCell(param.content, 0, param.content.length, param.cellConsumer);
    }

    @State(Scope.Benchmark)
    public static class UnescapeParam {
        public static final TextFormat TEXT_FORMAT = new TextFormat(',', '"', '"', false);
        public char[] content;

        public Unescaper unescaper;

        @Param(value =  { "1", "2"})
        public int type;

        @Param(value = { "\"unescape no escaped quote\"", "\"unescape one \"\"escaped quote\"\"\"", "\"\"\"a\"\"a\"\"a\"\"a\"\"a\"\"a\"\"a\"\"a\"\"a\"\"\"" })
        public String value;

        public BlackholeCellConsumer cellConsumer;

        @Setup
        public void setUp(Blackhole blackhole) {
            switch (type) {
                case 1:
                    unescaper = new UnescapeCellPreProcessor1(TEXT_FORMAT);
                    break;
                case 2:
                    unescaper = new UnescapeCellPreProcessor2(TEXT_FORMAT);
                    break;
            }
            content = value.toCharArray();

            cellConsumer = new BlackholeCellConsumer(blackhole);

        }
    }

    static class BlackholeCellConsumer implements CellConsumer {

        public final Blackhole blackhole;

        BlackholeCellConsumer(Blackhole blackhole) {
            this.blackhole = blackhole;
        }

        @Override
        public void newCell(char[] chars, int offset, int length) {
            blackhole.consume(chars);
            blackhole.consume(offset);
            blackhole.consume(length);
        }
    }

    public static class UnescapeCellPreProcessor1 implements Unescaper {

        private final TextFormat textFormat;

        public UnescapeCellPreProcessor1(TextFormat textFormat) {
            this.textFormat = textFormat;
        }


        public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer) {
            int strStart = start;
            int strEnd = end;

            int escapeChar = textFormat.escapeChar;
            if (strStart < strEnd && chars[strStart] == escapeChar) {
                strStart ++;
                strEnd = unescape(chars, strStart, strEnd, escapeChar);
            }

            cellConsumer.newCell(chars, strStart, strEnd - strStart);
        }

        private int unescape(final char[] chars, final int start, final int end, final int escapeChar) {
            for(int i = start; i < end - 1; i ++) {
                if (chars[i] == escapeChar) {
                    return removeEscapeChars(chars, end, i, escapeChar);
                }
            }

            if (start < end && chars[end - 1] == escapeChar) {
                return end - 1;
            }

            return end;
        }

        private int removeEscapeChars(final char[] chars, final int end, final int firstEscapeChar, final int escapeChar) {
            int destIndex = firstEscapeChar;
            boolean escaped = true;
            for(int sourceIndex = firstEscapeChar + 1;sourceIndex < end; sourceIndex++) {
                char c = chars[sourceIndex];
                if (c != escapeChar || escaped) {
                    chars[destIndex++] = c;
                    escaped = false;
                } else {
                    escaped = true;
                }
            }
            return destIndex;
        }
    }

    public static class UnescapeCellPreProcessor2 implements Unescaper {

        private final TextFormat textFormat;

        public UnescapeCellPreProcessor2(TextFormat textFormat) {
            this.textFormat = textFormat;
        }


        public final void newCell(char[] chars, int start, int end, CellConsumer cellConsumer) {
            int strStart = start;
            int strEnd = end;

            int escapeChar = textFormat.escapeChar;
            if (strStart < strEnd && chars[strStart] == escapeChar) {
                strStart ++;
                strEnd = unescape(chars, strStart, strEnd, escapeChar);
            }

            cellConsumer.newCell(chars, strStart, strEnd - strStart);
        }

        private int unescape(final char[] chars, final int start, final int end, final int escapeChar) {
            int indexOfEscapeChars = findChar(chars, start, end, escapeChar);

            if (indexOfEscapeChars >= end - 1) {
                return indexOfEscapeChars;
            } else {
                return removeEscapeChars(chars, end, indexOfEscapeChars, escapeChar);
            }
        }

        private int findChar(char[] chars, int start, int end, int escapeChar) {
            for(int i = start; i < end; i++) {
                if (chars[i] == escapeChar) return i;
            }
            return end;
        }

        private int removeEscapeChars(final char[] chars, final int end, final int firstEscapeChar, final int escapeChar) {
            int destIndex = firstEscapeChar;
            boolean escaped = true;
            for(int sourceIndex = firstEscapeChar + 1;sourceIndex < end; sourceIndex++) {
                char c = chars[sourceIndex];
                if (c != escapeChar || escaped) {
                    chars[destIndex++] = c;
                    escaped = false;
                } else {
                    escaped = true;
                }
            }
            return destIndex;
        }
    }
}
