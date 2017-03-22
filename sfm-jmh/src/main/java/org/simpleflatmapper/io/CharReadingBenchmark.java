package org.simpleflatmapper.io;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.Random;

@State(Scope.Benchmark)
public class CharReadingBenchmark {
    
    private File file;
    
    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRST1234567890!@£$%^&*()_+";
    
    public int fileSize = 50_000_000;
    
    public Charset charset;

    @Param({"8192", "32768", "131072"})
    private int bufferSize;

    @Setup
    public void setUp() throws IOException {
        charset = Charset.forName("UTF-8");
        file = File.createTempFile("test", ".txt");
        try (Writer fw = new OutputStreamWriter(new FileOutputStream(file), charset)) {
            Random random = new Random();
            for(int i = 0; i < fileSize; i++) {
                fw.append(chars.charAt(random.nextInt(chars.length())));
            }
        }
    }
    
    
    @Benchmark
    public void reader(Blackhole blackhole) throws IOException {
        bufferSize = 4096 * 32;
        char[] buffer = new char[bufferSize];

        try (FileChannel channel = FileChannel.open(file.toPath())) {
            try (Reader reader = Channels.newReader(channel, charset.newDecoder(), -1)) {
                int l;
                while ((l = reader.read(buffer)) != -1) {
                    blackhole.consume(buffer);
                }
            }
        }
    }


    @Benchmark
    public void memoryMappedFile(Blackhole blackhole) throws IOException {
        char[] buffer = new char[bufferSize];

        try (FileChannel channel = FileChannel.open(file.toPath())) {
            MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

            CharsetDecoder charsetDecoder = charset.newDecoder();

            while(byteBuffer.hasRemaining()) {
                CoderResult coderResult = charsetDecoder.decode(byteBuffer, CharBuffer.wrap(buffer), true);
                blackhole.consume(buffer);
            }
        }
    }

    @Benchmark
    public void memoryMappedFileBytes(Blackhole blackhole) throws IOException {
        byte[] buffer = new byte[bufferSize];

        try (FileChannel channel = FileChannel.open(file.toPath())) {
            MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

            while(byteBuffer.hasRemaining()) {
                byteBuffer.get(buffer, 0, Math.min(buffer.length, byteBuffer.remaining()));
                blackhole.consume(buffer);
            }
            
        }
    }

    @Benchmark
    public void fileChannelBytes(Blackhole blackhole) throws IOException {
        byte[] buffer = new byte[bufferSize];

        ByteBuffer wrap = ByteBuffer.wrap(buffer);
        try (FileChannel channel = FileChannel.open(file.toPath())) {
            int l;
            while((l = channel.read(wrap)) != -1) {
                blackhole.consume(buffer);
                wrap.clear();
            }
        }
    }

    @Benchmark
    public void intputStreamBytes(Blackhole blackhole) throws IOException {
        byte[] buffer = new byte[bufferSize];

        try (FileChannel channel = FileChannel.open(file.toPath())) {
            try (InputStream is = Channels.newInputStream(channel)) {
                int l;
                while ((l = is.read(buffer)) != -1) {
                    blackhole.consume(buffer);
                }
            }
        }
    }
}
