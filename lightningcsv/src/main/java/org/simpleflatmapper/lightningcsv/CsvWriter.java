package org.simpleflatmapper.lightningcsv;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class CsvWriter {

    private final CellWriter cellWriter;
    private final Appendable appendable;

    public CsvWriter(CellWriter cellWriter, Appendable appendable) {
        this.cellWriter = cellWriter;
        this.appendable = appendable;
    }

    public final CsvWriter appendCell(CharSequence charSequence) throws IOException {
        return appendCell(charSequence, 0, charSequence.length());
    }

    public final CsvWriter appendCell(CharSequence charSequence, int start, int end) throws IOException {
        CellWriter cellWriter = this.cellWriter;
        cellWriter.writeValue(charSequence, start, end, appendable);
        cellWriter.nextCell(appendable);
        return this;
    }

    public final CsvWriter appendCell(char[] chars, int start, int end) throws IOException {
        CellWriter cellWriter = this.cellWriter;
        cellWriter.writeValue(chars, start, end, appendable);
        cellWriter.nextCell(appendable);
        return this;
    }

    public final CsvWriter endOfRow() throws IOException {
        cellWriter.endOfRow(appendable);
        return this;
    }

    public final CsvWriter appendRow(CharSequence[] values) throws IOException {
        CellWriter cellWriter = this.cellWriter;
        Appendable appendable = this.appendable;
        if (values != null) {
            if (values.length > 0) {
                cellWriter.writeValue(values[0], appendable);
                for(int i = 1; i < values.length; i++) {
                    cellWriter.nextCell(appendable);
                    cellWriter.writeValue(values[i], appendable);
                }
            }
        }
        cellWriter.endOfRow(appendable);
        return this;
    }

    public final CsvWriter appendRow(Iterable<? extends CharSequence> values) throws IOException {
        CellWriter cellWriter = this.cellWriter;
        Appendable appendable = this.appendable;
        if (values != null) {
            Iterator<? extends CharSequence> iterator = values.iterator();
            if (iterator.hasNext()) {
                cellWriter.writeValue(iterator.next(), appendable);

                while(iterator.hasNext()) {
                    cellWriter.nextCell(appendable);
                    cellWriter.writeValue(iterator.next(), appendable);
                }

            }
        }
        cellWriter.endOfRow(appendable);
        return this;
    }


    public static final class DSL {
        private static final DSL INSTANCE = new DSL();
        private final boolean alwaysEscape;
        private final char separator;
        private final char quote;
        private final char escape;
        private final String endOfLine;

        private DSL() {
            this(CsvCellWriter.DEFAULT_SEPARATOR, CsvCellWriter.DEFAULT_QUOTE, CsvCellWriter.DEFAULT_ESCAPE, CsvCellWriter.DEFAULT_ALWAYS_ESCAPE, CsvCellWriter.DEFAULT_END_OF_LINE);
        }
        private DSL(char separator, char quote, char escape, boolean alwaysEscape, String endOfLine) {
            this.separator = separator;
            this.quote = quote;
            this.escape = escape;
            this.alwaysEscape = alwaysEscape;
            this.endOfLine = endOfLine;
        }

        public DSL separator(char separator) {
            return new DSL(separator, quote, escape, alwaysEscape, endOfLine);
        }
        public DSL quote(char quote) {
            return new DSL(separator, quote, escape, alwaysEscape, endOfLine);
        }
        public DSL escape(char escape) {
            return new DSL(separator, quote, escape, alwaysEscape, endOfLine);
        }
        public DSL alwaysEscape(boolean alwaysEscape) {
            return new DSL(separator, quote, escape, alwaysEscape, endOfLine);
        }

        public DSL endOfLine(String endOfLine) {
            return new DSL(separator, quote, escape, alwaysEscape, endOfLine);
        }

        public DSL alwaysEscape() {
            return alwaysEscape(true);
        }


        public ClosableCsvWriter to(Path path) throws IOException {
            return to(path, StandardCharsets.UTF_8);
        }

        public ClosableCsvWriter to(File file) throws IOException {
            return to(file, StandardCharsets.UTF_8);
        }

        public ClosableCsvWriter to(File file, Charset charset) throws IOException {
            return to(file.toPath(), charset);
        }

        public ClosableCsvWriter to(Path path, Charset charset) throws IOException {
            return new ClosableCsvWriter(new CsvCellWriter(separator, quote, escape, alwaysEscape, endOfLine), getWriter(path, charset));
        }

        private Writer getWriter(Path path, Charset charset) throws IOException {
            return Files.newBufferedWriter(path, charset);
        }


        public CsvWriter to(Appendable appendable) {
            return new CsvWriter(new CsvCellWriter(separator, quote, escape, alwaysEscape, endOfLine), appendable);
        }
    }

    public static DSL dsl() {
        return DSL.INSTANCE;
    }


}
