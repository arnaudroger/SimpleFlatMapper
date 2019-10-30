package org.simpleflatmapper.lightningcsv;

import java.io.*;
import java.nio.charset.Charset;

//IFJAVA8_START
import java.nio.file.Files;
import java.nio.file.Path;
//IFJAVA8_END
import java.util.Iterator;

public class CsvWriter {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final CellWriter cellWriter;
    private final Appendable appendable;


    private boolean rowHasData = false;

    public CsvWriter(CellWriter cellWriter, Appendable appendable) {
        this.cellWriter = cellWriter;
        this.appendable = appendable;
    }

    public final CsvWriter appendCell(CharSequence charSequence) throws IOException {
        return appendCell(charSequence, 0, charSequence.length());
    }

    public final CsvWriter appendCell(CharSequence charSequence, int start, int end) throws IOException {
        CellWriter cellWriter = this.cellWriter;
        Appendable appendable = this.appendable;

        if (rowHasData) {
            cellWriter.nextCell(appendable);
        }

        cellWriter.writeValue(charSequence, start, end, appendable);
        rowHasData = true;

        return this;
    }

    public final CsvWriter appendCell(char[] chars) throws IOException {
        return appendCell(chars, 0, chars.length);
    }

    public final CsvWriter appendCell(char[] chars, int start, int end) throws IOException {
        CellWriter cellWriter = this.cellWriter;
        Appendable appendable = this.appendable;

        if (rowHasData) {
            cellWriter.nextCell(appendable);
        }

        cellWriter.writeValue(chars, start, end, appendable);
        rowHasData = true;

        return this;
    }

    public final CsvWriter endOfRow() throws IOException {
        cellWriter.endOfRow(appendable);
        rowHasData = false;
        return this;
    }

    public final CsvWriter appendRow(CharSequence... values) throws IOException {
        CellWriter cellWriter = this.cellWriter;
        Appendable appendable = this.appendable;
        if (values != null) {
            if (values.length > 0) {
                if (rowHasData) {
                    cellWriter.nextCell(appendable);
                }
                cellWriter.writeValue(values[0], appendable);
                for(int i = 1; i < values.length; i++) {
                    cellWriter.nextCell(appendable);
                    cellWriter.writeValue(values[i], appendable);
                }
            }
        }
        cellWriter.endOfRow(appendable);
        rowHasData = false;
        return this;
    }

    public final CsvWriter appendRow(Iterable<? extends CharSequence> values) throws IOException {
        CellWriter cellWriter = this.cellWriter;
        Appendable appendable = this.appendable;
        if (values != null) {
            Iterator<? extends CharSequence> iterator = values.iterator();
            if (iterator.hasNext()) {
                if (rowHasData) {
                    cellWriter.nextCell(appendable);
                }
                cellWriter.writeValue(iterator.next(), appendable);

                while(iterator.hasNext()) {
                    cellWriter.nextCell(appendable);
                    cellWriter.writeValue(iterator.next(), appendable);
                }

            }
        }
        cellWriter.endOfRow(appendable);
        rowHasData = false;
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


        //IFJAVA8_START
        public ClosableCsvWriter to(Path path) throws IOException {
            return to(path, UTF_8);
        }
        public ClosableCsvWriter to(Path path, Charset charset) throws IOException {
            return new ClosableCsvWriter(new CsvCellWriter(separator, quote, escape, alwaysEscape, endOfLine), getWriter(path, charset));
        }
        private Writer getWriter(Path path, Charset charset) throws IOException {
            return Files.newBufferedWriter(path, charset);
        }
        //IFJAVA8_END

        public ClosableCsvWriter to(File file) throws IOException {
            return to(file, UTF_8);
        }

        public ClosableCsvWriter to(File file, Charset charset) throws IOException {
            return new ClosableCsvWriter(new CsvCellWriter(separator, quote, escape, alwaysEscape, endOfLine), getWriter(file, charset));
        }

        private Writer getWriter(File file, Charset charset) throws IOException {
            //IFJAVA8_START
            if (true) {
                return getWriter(file.toPath(), charset);
            }
            //IFJAVA8_END
            return new OutputStreamWriter(new FileOutputStream(file), charset);
        }

        public CsvWriter to(Appendable appendable) {
            return new CsvWriter(new CsvCellWriter(separator, quote, escape, alwaysEscape, endOfLine), appendable);
        }
    }

    public static DSL dsl() {
        return DSL.INSTANCE;
    }


}
