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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;


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
