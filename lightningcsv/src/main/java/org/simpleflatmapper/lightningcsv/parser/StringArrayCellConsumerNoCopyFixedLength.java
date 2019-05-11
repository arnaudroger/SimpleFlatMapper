package org.simpleflatmapper.lightningcsv.parser;

import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.ErrorHelper;

public final class StringArrayCellConsumerNoCopyFixedLength<RH extends CheckedConsumer<? super String[]>> implements CellConsumer {
        private final RH handler;
        private int currentIndex;
        private String[] currentRow;

        public StringArrayCellConsumerNoCopyFixedLength(RH handler, int numberOfCells) {
            this.handler = handler;
            this.currentRow = new String[numberOfCells];
        }

        public void newCell(char[] chars, int offset, int length) {
            int currentIndex = this.currentIndex;
            String[] currentRow = this.currentRow;
            if (currentIndex < currentRow.length) {
                currentRow[currentIndex] = length > 0 ? new String(chars, offset, length) : "";
                this.currentIndex++;
            }
        }


        public boolean endOfRow() {
            try {
                return this._endOfRow();
            } catch (Exception var2) {
                return (Boolean) ErrorHelper.rethrow(var2);
            }
        }

        private boolean _endOfRow() throws Exception {
            this.handler.accept(currentRow);
            this.currentIndex = 0;
            return true;
        }

        public RH handler() {
            return this.handler;
        }

        public void end() {
            if (this.currentIndex > 0) {
                this.endOfRow();
            }

        }
    }