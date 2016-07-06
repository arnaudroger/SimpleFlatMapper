package org.sfm.utils;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;

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
        do {
            final long currentHead = head;
            final long currentTail = tail;
            if (currentHead < currentTail) {
                int l = read(cbuf, off, len, currentHead, currentTail);
                head = currentHead + l;
                return l;
            }

            if (!dataProducer.run) {
                if (tail >= head) {
                    return -1;
                }
            }

            waitingStrategy();

        } while(true);
    }

    private void waitingStrategy() {
       LockSupport.parkNanos(1000);
    }

    private int read(char[] cbuf, int off, int len, long lhead, long ltail) {

        int currentHead = (int) (lhead & bufferMask);
        int usedLength = (int) (ltail - lhead);

        int block1Length = Math.min(len, Math.min(usedLength, (int) (capacity - currentHead)));
        int block2Length =  Math.min(len, usedLength) - block1Length;

        System.arraycopy(buffer, currentHead, cbuf, off, block1Length);
        System.arraycopy(buffer, 0, cbuf, off+ block1Length, block2Length);

        return block1Length + block2Length;
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

                final long currentTail = tail;
                final long currentHead = head;
                final long wrapPoint = currentTail - buffer.length;

                if (head <= wrapPoint) {
                    waitingStrategy();
                    continue;
                }

                try {
                    fill(currentTail, currentHead);
                } catch (IOException e) {
                    exception = e;
                    run = false;
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
