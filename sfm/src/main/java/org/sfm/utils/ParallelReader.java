package org.sfm.utils;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public class ParallelReader extends Reader {

    private final Reader reader;
    private final DataProducer dataProducer;
    private final char[] buffer;
    private final int bufferMask;
    private AtomicLong tail = new AtomicLong();
    private AtomicLong head = new AtomicLong();
    private final long capacity;
    private long tailCache;
    private long headCache;
    private long padding = 64;

    private long readWait = 0;
    private long writeWait  =0;
    private long tailCacheMisses = 0;
    private long headCacheMisses = 0;
    public ParallelReader(Reader reader, Executor executorService) {
        this(reader, executorService, 1 << 13);
    }

    public ParallelReader(Reader reader, Executor executorService, int bufferSize) {
        int powerOf2 =  1 << 32 - Integer.numberOfLeadingZeros(bufferSize - 1);
        if (powerOf2 <= 1024) {
            padding = 0;
        }
        this.reader = reader;
        buffer = new char[powerOf2];
        bufferMask = buffer.length - 1;
        dataProducer = new DataProducer();
        executorService.execute(dataProducer);
        capacity = buffer.length;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        final long currentHead = head.get();
        do {
            if (currentHead < tailCache) {
                int l = read(cbuf, off, len, currentHead, tailCache);

                head.lazySet(currentHead + l);
                return l;
            }

            tailCache = tail.get();
            if (currentHead >= tailCache) {
                if (!dataProducer.run) {
                    if (dataProducer.exception != null) {
                        throw dataProducer.exception;
                    }
                    tailCache = tail.get();
                    if (currentHead >= tailCache) {
                        return -1;
                    }
                }
                readWait ++;
                waitingStrategy();
            } else {
                tailCacheMisses ++;
            }
        } while(true);
    }

    private void waitingStrategy() {
        Thread.yield();
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
        System.out.println("");
        System.out.println("readWait        = " + readWait);
        System.out.println("writeWait       = " + writeWait);
        System.out.println("tailCacheMisses = " + tailCacheMisses);
        System.out.println("headCacheMisses = " + headCacheMisses);
    }

    private class DataProducer implements Runnable {
        private volatile boolean run = true;
        private volatile IOException exception;

        @Override
        public void run() {
            long currentTail = tail.get();
            while(run) {

                final long wrapPoint = currentTail - buffer.length;

                if (headCache - padding <= wrapPoint) {
                    headCache = head.get();
                    if (headCache <= wrapPoint) {
                        writeWait ++;
                        waitingStrategy();
                        continue;
                    }
                    headCacheMisses ++;
                }

                try {
                    int r =  read(currentTail, headCache);
                    if (r == -1) {
                        run = false;
                    } else {
                        currentTail += r;
                        tail.lazySet(currentTail);
                    }
                } catch (IOException e) {
                    exception = e;
                    run = false;
                }
            }
        }

        private int read(long ltail, long lhead) throws IOException {
            long used = ltail - lhead;
            long available = capacity - used;
            int tailIndex = (int) (ltail & bufferMask);
            int realEnd = (int) Math.min(tailIndex + available,  capacity);
            int realAvailable = realEnd - tailIndex;

            return reader.read(buffer, tailIndex, realAvailable);
        }

        public void stop() {
            run = false;
        }
    }
}
