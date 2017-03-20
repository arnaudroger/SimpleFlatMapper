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
import java.io.InputStream;
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
ReaderBenchmark.testFileChannel                                  true        N/A         16  avgt   10     9322.912 ±    456.031  ns/op
ReaderBenchmark.testFileChannel                                  true        N/A        256  avgt   10     9478.410 ±    322.653  ns/op
ReaderBenchmark.testFileChannel                                  true        N/A       4096  avgt   10    12836.844 ±    355.543  ns/op
ReaderBenchmark.testFileChannel                                  true        N/A      32178  avgt   10    32576.578 ±    285.598  ns/op
ReaderBenchmark.testFileChannel                                  true        N/A     524288  avgt   10   375999.553 ±  11306.361  ns/op

ReaderBenchmark.testFileChannelViaRandomFile                     true        N/A         16  avgt   10    11749.558 ±    345.706  ns/op
ReaderBenchmark.testFileChannelViaRandomFile                     true        N/A        256  avgt   10    11878.419 ±    371.884  ns/op
ReaderBenchmark.testFileChannelViaRandomFile                     true        N/A       4096  avgt   10    15006.245 ±    567.324  ns/op
ReaderBenchmark.testFileChannelViaRandomFile                     true        N/A      32178  avgt   10    36993.495 ±   1451.853  ns/op
ReaderBenchmark.testFileChannelViaRandomFile                     true        N/A     524288  avgt   10   378302.655 ±   5068.252  ns/op

ReaderBenchmark.testFileInputStream                              true        N/A         16  avgt   10    12807.156 ±    809.506  ns/op
ReaderBenchmark.testFileInputStream                              true        N/A        256  avgt   10    12272.287 ±    861.207  ns/op
ReaderBenchmark.testFileInputStream                              true        N/A       4096  avgt   10    15769.939 ±    976.729  ns/op
ReaderBenchmark.testFileInputStream                              true        N/A      32178  avgt   10    38494.994 ±   5206.720  ns/op
ReaderBenchmark.testFileInputStream                              true        N/A     524288  avgt   10   386351.446 ±  10450.844  ns/op

ReaderBenchmark.testFiles                                        true        N/A         16  avgt   10    12376.477 ±    448.096  ns/op
ReaderBenchmark.testFiles                                        true        N/A        256  avgt   10    12493.740 ±    326.430  ns/op
ReaderBenchmark.testFiles                                        true        N/A       4096  avgt   10    16268.272 ±    537.809  ns/op
ReaderBenchmark.testFiles                                        true        N/A      32178  avgt   10    40549.940 ±   1992.624  ns/op
ReaderBenchmark.testFiles                                        true        N/A     524288  avgt   10   438688.718 ±  24330.819  ns/op

ReaderBenchmark.testInputStreamReaderFromChannelInputStream      true        N/A         16  avgt   10     9644.616 ±    273.961  ns/op
ReaderBenchmark.testInputStreamReaderFromChannelInputStream      true        N/A        256  avgt   10     9625.635 ±    472.494  ns/op
ReaderBenchmark.testInputStreamReaderFromChannelInputStream      true        N/A       4096  avgt   10    12769.045 ±    311.633  ns/op
ReaderBenchmark.testInputStreamReaderFromChannelInputStream      true        N/A      32178  avgt   10    33983.105 ±    693.315  ns/op
ReaderBenchmark.testInputStreamReaderFromChannelInputStream      true        N/A     524288  avgt   10   387243.813 ±  17110.493  ns/op

ReaderBenchmark.testFileChannel                                 false        N/A         16  avgt   10     9500.812 ±     96.291  ns/op
ReaderBenchmark.testFileChannel                                 false        N/A        256  avgt   10    10466.876 ±    171.133  ns/op
ReaderBenchmark.testFileChannel                                 false        N/A       4096  avgt   10    31817.229 ±    846.393  ns/op
ReaderBenchmark.testFileChannel                                 false        N/A      32178  avgt   10   193800.511 ±  10374.107  ns/op
ReaderBenchmark.testFileChannel                                 false        N/A     524288  avgt   10  2923155.759 ± 161540.050  ns/op

ReaderBenchmark.testFileChannelViaRandomFile                    false        N/A         16  avgt   10    12259.177 ±    552.465  ns/op
ReaderBenchmark.testFileChannelViaRandomFile                    false        N/A        256  avgt   10    13004.571 ±    741.187  ns/op
ReaderBenchmark.testFileChannelViaRandomFile                    false        N/A       4096  avgt   10    35314.110 ±   2388.371  ns/op
ReaderBenchmark.testFileChannelViaRandomFile                    false        N/A      32178  avgt   10   189175.393 ±   8136.221  ns/op
ReaderBenchmark.testFileChannelViaRandomFile                    false        N/A     524288  avgt   10  2858018.731 ± 116611.939  ns/op

ReaderBenchmark.testFileInputStream                             false        N/A         16  avgt   10    12958.011 ±   1124.609  ns/op
ReaderBenchmark.testFileInputStream                             false        N/A        256  avgt   10    13514.658 ±   1157.090  ns/op
ReaderBenchmark.testFileInputStream                             false        N/A       4096  avgt   10    37062.578 ±   4223.953  ns/op
ReaderBenchmark.testFileInputStream                             false        N/A      32178  avgt   10   205162.227 ±  11377.574  ns/op
ReaderBenchmark.testFileInputStream                             false        N/A     524288  avgt   10  3188006.585 ± 201485.544  ns/op

ReaderBenchmark.testFiles                                       false        N/A         16  avgt   10    12612.580 ±    658.560  ns/op
ReaderBenchmark.testFiles                                       false        N/A        256  avgt   10    13678.707 ±    746.372  ns/op
ReaderBenchmark.testFiles                                       false        N/A       4096  avgt   10    36484.202 ±   2040.361  ns/op
ReaderBenchmark.testFiles                                       false        N/A      32178  avgt   10   175335.711 ±   5833.526  ns/op
ReaderBenchmark.testFiles                                       false        N/A     524288  avgt   10  3147663.738 ± 119773.115  ns/op

ReaderBenchmark.testInputStreamReaderFromChannelInputStream     false        N/A         16  avgt   10     9427.272 ±    325.296  ns/op
ReaderBenchmark.testInputStreamReaderFromChannelInputStream     false        N/A        256  avgt   10    10706.807 ±    412.783  ns/op
ReaderBenchmark.testInputStreamReaderFromChannelInputStream     false        N/A       4096  avgt   10    35232.609 ±   2521.247  ns/op
ReaderBenchmark.testInputStreamReaderFromChannelInputStream     false        N/A      32178  avgt   10   200061.404 ±   2667.944  ns/op
ReaderBenchmark.testInputStreamReaderFromChannelInputStream     false        N/A     524288  avgt   10  2646672.382 ± 100473.956  ns/op

 */
@State(Scope.Benchmark)
public class ReaderBenchmark {
    

    public static String LATIN1 = "abcdefghijklmnopqrstuvwxyz123456790";
    public static String UTF8 = "よばれる － 呼ばれる りゅうは、ごく － 理由は、ごく ふつうの－普通の ひとがごく－人がごく かんたんに－簡単に しよう－使用 ほうほうを－方法を まいにちの－毎日の しごとにすぐ－仕事にすぐ やくだてることができることからきている。－ 役立てることができることからきている。";
 
    
    @Param({"16", "256", "4096", "32178", "50000", "5000000"})
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
    public void testInputStreamReaderFromChannelInputStream(Blackhole blackhole) throws IOException {
        try (InputStream open = Files.newInputStream(file.toPath())) {
            try (Reader reader = new InputStreamReader(open, "UTF-8")) {
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
