package org.simpleflatmapper.csv.io;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;


/*
java -jar target/benchmarks.jar ReaderBench -f 5 -i 10 -wi 10 -bm avgt -tu ns -rf csv
 
Benchmark                                     (latin1)  (nbBytes)   Mode  Cnt      Score      Error  Units
ReaderBenchmark.testFileChannel                   true        256  thrpt   10  91774.427 ± 3066.295  ops/s
ReaderBenchmark.testFileChannel                   true       4096  thrpt   10  77065.533 ± 3729.302  ops/s
ReaderBenchmark.testFileChannel                   true      32178  thrpt   10  29670.632 ± 1779.399  ops/s
ReaderBenchmark.testFileChannel                  false        256  thrpt   10  84979.379 ± 2963.105  ops/s
ReaderBenchmark.testFileChannel                  false       4096  thrpt   10  31300.909 ±  825.168  ops/s
ReaderBenchmark.testFileChannel                  false      32178  thrpt   10   5287.533 ±  216.426  ops/s
ReaderBenchmark.testFileChannelViaRandomFile      true        256  thrpt   10  74559.607 ± 2486.186  ops/s
ReaderBenchmark.testFileChannelViaRandomFile      true       4096  thrpt   10  64941.014 ± 2425.150  ops/s
ReaderBenchmark.testFileChannelViaRandomFile      true      32178  thrpt   10  28196.838 ±  464.446  ops/s
ReaderBenchmark.testFileChannelViaRandomFile     false        256  thrpt   10  70453.420 ± 3321.952  ops/s
ReaderBenchmark.testFileChannelViaRandomFile     false       4096  thrpt   10  28775.067 ± 1385.479  ops/s
ReaderBenchmark.testFileChannelViaRandomFile     false      32178  thrpt   10   5065.470 ±  310.718  ops/s
ReaderBenchmark.testFileInputStream               true        256  thrpt   10  66929.634 ± 5366.519  ops/s
ReaderBenchmark.testFileInputStream               true       4096  thrpt   10  59970.777 ± 3434.873  ops/s
ReaderBenchmark.testFileInputStream               true      32178  thrpt   10  25733.113 ± 2706.243  ops/s
ReaderBenchmark.testFileInputStream              false        256  thrpt   10  64438.331 ± 5055.979  ops/s
ReaderBenchmark.testFileInputStream              false       4096  thrpt   10  26278.948 ± 3271.394  ops/s
ReaderBenchmark.testFileInputStream              false      32178  thrpt   10   4779.696 ±  226.616  ops/s

 */
@State(Scope.Benchmark)
public class ReaderBenchmark {
    

    public static String LATIN1 = "abcdefghijklmnopqrstuvwxyz123456790";
    public static String UTF8 = "よばれる － 呼ばれる りゅうは、ごく － 理由は、ごく ふつうの－普通の ひとがごく－人がごく かんたんに－簡単に しよう－使用 ほうほうを－方法を まいにちの－毎日の しごとにすぐ－仕事にすぐ やくだてることができることからきている。－ 役立てることができることからきている。";
 
    
    @Param({"16", "256", "4096", "32178", "524288"})
    public int nbChars; 
    
    @Param({"true", "false"})
    public boolean latin1;
    
    public File file;
    @Setup
    public void setUp() throws IOException {
        file = File.createTempFile("ReaderBenchmark", ".txt");
        
        String sample = latin1 ? LATIN1 : UTF8;
        
        int nb = 0;
        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8")) {
            while(nb < nbChars) {
                int toWrite = Math.min((nbChars - nb) , sample.length());
                fw.append(sample, 0, toWrite);
                nb += toWrite;
            }
        }
    }

    @Benchmark
    public void testFiles(Blackhole blackhole) throws IOException {
        try (Reader reader = Files.newBufferedReader(file.toPath(), Charset.forName("UTF-8"))) {
            consume(reader, blackhole);
        }
    }
    @Benchmark
    public void testFileChannelViaRandomFile(Blackhole blackhole) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            try (FileChannel open = randomAccessFile.getChannel()) {
                try (Reader reader = Channels.newReader(open, "UTF-8")) {
                    consume(reader, blackhole);
                }
            }
        }
    }
    @Benchmark
    public void testFileChannel(Blackhole blackhole) throws IOException {
        try (FileChannel open = FileChannel.open(file.toPath())) {
            try (Reader reader = Channels.newReader(open, "UTF-8")) {
                consume(reader, blackhole);
            }
        }
    }

    @Benchmark
    public void testFileInputStream(Blackhole blackhole) throws IOException {
        try (FileInputStream is = new FileInputStream(file)) {
            try (Reader reader = new InputStreamReader(is, "UTF-8")) {
                consume(reader, blackhole);
            }
        }
    }

    private void consume(Reader reader, Blackhole blackhole) throws IOException {
        char[] buffer = new char[Math.min(4096, nbChars)];
        
        while(reader.read(buffer) != -1) {
            blackhole.consume(buffer);
        }
    }

}
