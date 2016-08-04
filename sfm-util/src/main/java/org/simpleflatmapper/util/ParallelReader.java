package org.simpleflatmapper.util;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public class ParallelReader extends Reader {
    private static final int DEFAULT_MAX_READ = 8192;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 32;

    private final Reader reader;
    private final DataProducer dataProducer;
    private final char[] buffer;

    private final int bufferMask;
    private final int maxRead;

    private AtomicLong tail = new AtomicLong();
    private AtomicLong head = new AtomicLong();

    private final long capacity;
    private long tailCache;
    private long headCache;

    private final long padding;


    public ParallelReader(Reader reader, Executor executorService) {
        this(reader, executorService, DEFAULT_BUFFER_SIZE);
    }

    public ParallelReader(Reader reader, Executor executorService, int bufferSize) {
        this(reader, executorService, bufferSize, DEFAULT_MAX_READ);
    }
    public ParallelReader(Reader reader, Executor executorService, int bufferSize, int maxRead) {
        int powerOf2 =  1 << 32 - Integer.numberOfLeadingZeros(bufferSize - 1);
        padding = powerOf2 <= 1024 ? 0 : 64;
        this.reader = reader;
        buffer = new char[powerOf2];
        bufferMask = buffer.length - 1;
        dataProducer = new DataProducer();
        executorService.execute(dataProducer);
        capacity = buffer.length;
        this.maxRead = maxRead;
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
                waitingStrategy();
            }
        } while(true);
    }

    @Override
    public int read() throws IOException {

        final long currentHead = head.get();
        do {
            if (currentHead < tailCache) {

                int headIndex = (int) (currentHead & bufferMask);

                char c = buffer[headIndex];

                head.lazySet(currentHead + 1);

                return c;
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
                waitingStrategy();
            }
        } while(true);
    }

    private void waitingStrategy() {
        Thread.yield();
    }

    private int read(char[] cbuf, int off, int len, long currentHead, long currentTail) {

        int headIndex = (int) (currentHead & bufferMask);
        int usedLength = (int) (currentTail - currentHead);

        int block1Length = Math.min(len, Math.min(usedLength, (int) (capacity - headIndex)));
        int block2Length =  Math.min(len, usedLength) - block1Length;

        System.arraycopy(buffer, headIndex, cbuf, off, block1Length);
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
            long currentTail = tail.get();
            while(run) {

                final long wrapPoint = currentTail - buffer.length;

                if (headCache - padding <= wrapPoint) {
                    headCache = head.get();
                    if (headCache <= wrapPoint) {
                        waitingStrategy();
                        continue;
                    }
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

        private int read(long currentTail, long currentHead) throws IOException {
            long used = currentTail - currentHead;

            long length = Math.min(capacity - used, maxRead);

            int tailIndex = (int) (currentTail & bufferMask);

            int endBlock1 = (int) Math.min(tailIndex + length,  capacity);

            int block1Length = endBlock1 - tailIndex;

            return reader.read(buffer, tailIndex, block1Length);
        }

        public void stop() {
            run = false;
        }
    }
}
