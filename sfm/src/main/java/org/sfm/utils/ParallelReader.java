package org.sfm.utils;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.Executor;

public class ParallelReader extends Reader {

    private final Reader reader;
    private final DataProducer dataProducer;
    private final char[] buffer;
    private final int bufferMask;
    private volatile long tail = 0;
    private volatile long head = 0;
    private final long capacity;

    public ParallelReader(Reader reader, Executor executorService) {
        this(reader, executorService, 1 << 13);
    }

    public ParallelReader(Reader reader, Executor executorService, int bufferSize) {
        this.reader = reader;
        buffer = new char[bufferSize];
        bufferMask = bufferSize - 1;
        dataProducer = new DataProducer();
        executorService.execute(dataProducer);
        capacity = buffer.length;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (dataProducer.exception != null) {
            throw dataProducer.exception;
        }

        do {
            boolean run = dataProducer.run;
            long lhead = head;
            long ltail = tail;

            if (lhead != ltail) {
                return read(cbuf, off, len, lhead, ltail);
            } else if (run) {
                waitingStrategy();
            } else {
                return -1;
            }
        } while(true);
    }

    private void waitingStrategy() {
        //System.out.println("BP Thread.currentThread().getName() = " + Thread.currentThread().getName());
       // Thread.yield();
    }

    private int read(char[] cbuf, int off, int len, long lhead, long ltail) {
        long availableLength = ltail - lhead;
        int actualHead = (int) (lhead & bufferMask);
        long realEnd = Math.min(actualHead + availableLength, capacity);
        long realLength = realEnd - actualHead;

        int actualLength = (int) Math.min(realLength, len);

        System.arraycopy(buffer, actualHead, cbuf, off, actualLength);

        head = lhead + actualLength;
        return actualLength;
    }

    @Override
    public void close() throws IOException {
        dataProducer.stop();
        reader.close();
    }

    private class DataProducer implements Runnable {
        private volatile boolean run = true;
        private volatile IOException exception;

        @Override
        public void run() {
            while(run) {
                long ltail = tail;
                long lhead = head;
                if (ltail - lhead < capacity) {
                    try {
                        fill(ltail, lhead);
                    } catch (IOException e) {
                        exception = e;
                        run = false;
                    }
                } else {
                    waitingStrategy();
                }
            }
        }

        private void fill(long ltail, long lhead) throws IOException {
            long used = ltail - lhead;
            long available = capacity - used;
            int tailIndex = (int) (ltail & bufferMask);
            int realEnd = (int) Math.min(tailIndex + available,  capacity);
            int realAvailable = realEnd - tailIndex;

            int r = reader.read(buffer, tailIndex, realAvailable);

            if (r == -1) {
                run = false;
            } else {
                tail = ltail + r;
            }
        }

        public void stop() {
            run = false;
        }
    }
}
