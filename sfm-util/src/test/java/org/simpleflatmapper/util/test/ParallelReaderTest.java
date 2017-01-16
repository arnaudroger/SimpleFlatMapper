package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.ParallelReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.Assert.assertEquals;

public class ParallelReaderTest {


   ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Test
    public void testStringBiggerThanBuffer() throws IOException {
        String str = "0123456789\n0123456789";
        testReadFromString(str, new char[3], false, 4);
    }

    public void testReadFromString(String str, char[] buffer, boolean slow, int bufferSize) throws IOException {
        StringReader stringReader = new StringReader(str);


        StringBuilder sb = new StringBuilder();
        ParallelReader parallelReader = new ParallelReader(stringReader, executorService, bufferSize);
        try {
            int l;
            while((l = parallelReader.read(buffer, 0, buffer.length)) != -1) {
                sb.append(buffer, 0, l);
                if (slow) {
                    LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(50));
                }
            }
        } finally {
            parallelReader.close();
        }

        assertEquals(str, sb.toString());


        stringReader = new StringReader(str);


        sb = new StringBuilder();
        parallelReader = new ParallelReader(stringReader, executorService, bufferSize);
        try {
            int l;
            while((l = parallelReader.read()) != -1) {
                sb.append((char)l);
            }
        } finally {
            parallelReader.close();
        }

        assertEquals(str, sb.toString());
    }


    String data = "abcdefghijklmnopqrstuvwxyz\n";
    @Test
    public void testReadFromRandomStringsReader() throws IOException {
        Random random = new Random();
        for(int i = 0; i < 256; i++) {
            int strLength = random.nextInt(1024 * 64);
            StringBuilder sb = new StringBuilder(strLength);
            for(int j = 0; j < strLength; j++) {
                if (j % data.length() ==0) {
                    sb.append(i + ":" + String.valueOf(j / data.length()) + " - ");
                }
                sb.append(data.charAt(j % data.length()));
            }
            int bufferSize = random.nextInt(8096) + 1;

            String str = sb.toString();
            testReadFromString(str, new char[bufferSize], false, 1 << 10);
            testReadFromString(str, new char[bufferSize], true, 1 << 10);
        }
    }

}
