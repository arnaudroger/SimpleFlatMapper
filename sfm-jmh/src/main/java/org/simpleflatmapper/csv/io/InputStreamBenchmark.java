package org.simpleflatmapper.csv.io;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Random;


/*
java -jar target/benchmarks.jar InputStreamBench -f 5 -i 10 -wi 10 -bm avgt -tu ns -rf csv
 
 
Benchmark                                                    (latin1)  (nbBytes)  (nbChars)  Mode  Cnt        Score        Error  Units
InputStreamBenchmark.testFileChannel                              N/A         16        N/A  avgt   10     8977.331 ±    435.043  ns/op
InputStreamBenchmark.testFileChannel                              N/A       4096        N/A  avgt   10     9317.365 ±    353.604  ns/op
InputStreamBenchmark.testFileChannel                              N/A      32178        N/A  avgt   10    16918.700 ±    444.074  ns/op

InputStreamBenchmark.testFileChannelViaRandomFile                 N/A         16        N/A  avgt   10    11152.358 ±    386.047  ns/op
InputStreamBenchmark.testFileChannelViaRandomFile                 N/A       4096        N/A  avgt   10    11337.523 ±    428.330  ns/op
InputStreamBenchmark.testFileChannelViaRandomFile                 N/A      32178        N/A  avgt   10    19453.311 ±   1347.894  ns/op

InputStreamBenchmark.testFileInputStream                          N/A         16        N/A  avgt   10    12064.893 ±   2195.136  ns/op
InputStreamBenchmark.testFileInputStream                          N/A       4096        N/A  avgt   10    12355.153 ±   2752.407  ns/op
InputStreamBenchmark.testFileInputStream                          N/A      32178        N/A  avgt   10    20682.621 ±   3246.232  ns/op

InputStreamBenchmark.testFiles                                    N/A         16        N/A  avgt   10     9020.596 ±    450.964  ns/op
InputStreamBenchmark.testFiles                                    N/A       4096        N/A  avgt   10     9304.132 ±    553.883  ns/op
InputStreamBenchmark.testFiles                                    N/A      32178        N/A  avgt   10    17405.981 ±    679.683  ns/op
 

 */
@State(Scope.Benchmark)
public class InputStreamBenchmark {
    

 
    
    @Param({"16", "4096", "32178", "500000", "5000000"})
    public int nbBytes; 
    
    public File file;
    
    @Setup
    public void setUp() throws IOException {
        file = File.createTempFile("InputStreamBenchmark", ".bin");

        Random random = new Random();
        int nb = 0;
        try (OutputStream fw = new FileOutputStream(file)) {
            while(nb < nbBytes) {
                int toWrite = nbBytes - nb;
                byte[] bytes = new byte[toWrite];
                random.nextBytes(bytes);
                fw.write(bytes);
                nb += toWrite;
            }
        }
    }

    @Benchmark
    public void testFiles(Blackhole blackhole) throws IOException {
        try (InputStream reader = Files.newInputStream(file.toPath())) {
            consume(reader, blackhole);
        }
    }
    @Benchmark
    public void testFileChannelViaRandomFile(Blackhole blackhole) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            try (FileChannel open = randomAccessFile.getChannel()) {
                try (InputStream inputStream = Channels.newInputStream(open)) {
                    consume(inputStream, blackhole);
                }
            }
        }
    }
    @Benchmark
    public void testFileChannel(Blackhole blackhole) throws IOException {
        try (FileChannel open = FileChannel.open(file.toPath())) {
            try (InputStream is = Channels.newInputStream(open)) {
                consume(is, blackhole);
            }
        }
    }

    @Benchmark
    public void testFileInputStream(Blackhole blackhole) throws IOException {
        try (FileInputStream is = new FileInputStream(file)) {
            consume(is, blackhole);
        }
    }

    private void consume(InputStream is, Blackhole blackhole) throws IOException {
        byte[] buffer = new byte[4096];
        
        while(is.read(buffer) != -1) {
            blackhole.consume(buffer);
        }
    }

}
