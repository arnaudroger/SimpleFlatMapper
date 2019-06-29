package org.simpleflatmapper.csv;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import org.simpleflatmapper.lightningcsv.Row;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.Consumer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

@State(Scope.Benchmark)
public class DiscriminatorBenchmark {
    
    
    File file;
    @Setup
    public void setUp() throws IOException {

        file = new File("test.csv");
        
        try(FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            writer.append("id,name,type,b_id,b_type,b_c_id\n");

            Random r = new Random();
            for(int i = 0; i < 100000; i++) {
                writer.append(Integer.toString(i))
                        .append(",")
                        .append("name " + i)
                        .append(",")
                        .append("typea" + (r.nextInt(40) + 1))
                        .append(",")
                        .append(Integer.toString(i))
                        .append(",")
                        .append("typeb" + (r.nextInt(20) + 1))
                        .append(",")
                        .append(Integer.toString(r.nextInt()))
                        .append("\n")
                        ;
            }
            
            writer.flush();
        }
    }
    
    @TearDown
    public void tearDown() {
        //file.delete();
    }
    
    @Benchmark
    public void testNoDiscriminator(Blackhole blackhole) throws IOException {
        CsvParser.mapWith(
                CsvMapperFactory.newInstance()
                    .discriminator(A.class).with(A1.class)
                    .discriminator(B.class).with(B1.class)
                    .newMapper(A.class)
        ).stream(file, s -> {
            s.forEach(blackhole::consume);
            return null;
        });
    }
    
    
    @Benchmark
    public void testWithDiscriminator(Blackhole blackhole) throws IOException {
        CsvParser.mapWith(
                CsvMapperFactory.newInstance()
                        .discriminator(A.class).onGetter(r -> r.getString(2)).with( b -> {
                            for(int i = 1; i <= 40; i++) {
                                try {
                                    b.when("typea" + i, Class.forName("org.simpleflatmapper.csv.DiscriminatorBenchmark$A" + i));
                                } catch (ClassNotFoundException e) {
                                    throw new Error(e);
                                }
                            }
                        })
                        .discriminator(B.class).onGetter(r -> r.getString(4)).with(b -> {
                            for(int i = 1; i <= 20; i++) {
                                try {
                                    b.when("typeb" + i, Class.forName("org.simpleflatmapper.csv.DiscriminatorBenchmark$B" + i));
                                } catch (ClassNotFoundException e) {
                                    throw new Error(e);
                                }
                            }
                        })
                        .newMapper(A.class)
        ).stream(file, s -> {
            s.forEach(blackhole::consume);
            return null;
        });
    }

    @Benchmark
    public void testWithDiscriminatorWithKeys(Blackhole blackhole) throws IOException {
        CsvParser.mapWith(
                CsvMapperFactory.newInstance()
                        .discriminator(A.class).onGetter(r -> r.getString(2)).with( b -> {
                            for(int i = 1; i <= 40; i++) {
                                try {
                                    b.when("typea" + i, Class.forName("org.simpleflatmapper.csv.DiscriminatorBenchmark$A" + i));
                                } catch (ClassNotFoundException e) {
                                    throw new Error(e);
                                }
                            }
                        })
                        .discriminator(B.class).onGetter(r -> r.getString(4)).with( b -> {
                            for(int i = 1; i <= 20; i++) {
                                try {
                                    b.when("typeb" + i, Class.forName("org.simpleflatmapper.csv.DiscriminatorBenchmark$B" + i));
                                } catch (ClassNotFoundException e) {
                                    throw new Error(e);
                                }
                            }
                        })
                        .addKeys("id", "b_id", "b_c_id")
                        .newMapper(A.class)
        ).stream(file, s -> {
            s.forEach(blackhole::consume);
            return null;
        });
    }

    @Benchmark
    public void testWithDiscriminatorWithPartialKeys(Blackhole blackhole) throws IOException {
        CsvParser.mapWith(
                CsvMapperFactory.newInstance()
                        .discriminator(A.class).onGetter(r -> r.getString(2)).with( b -> {
                            for(int i = 1; i <= 40; i++) {
                                try {
                                    b.when("typea" + i, Class.forName("org.simpleflatmapper.csv.DiscriminatorBenchmark$A" + i));
                                } catch (ClassNotFoundException e) {
                                    throw new Error(e);
                                }
                            }
                        })
                        .discriminator(B.class).onGetter(r -> r.getString(4)).with( b -> {
                            for(int i = 1; i <= 20; i++) {
                                try {
                                    b.when("typeb" + i, Class.forName("org.simpleflatmapper.csv.DiscriminatorBenchmark$B" + i));
                                } catch (ClassNotFoundException e) {
                                    throw new Error(e);
                                }
                            }
                        })
                        .addKeys("id")
                        .newMapper(A.class)
        ).stream(file, s -> {
            s.forEach(blackhole::consume);
            return null;
        });
    }

    public void withDiscriminatorWithKeys(Consumer<A> aConsumer) throws IOException {
        CsvParser.mapWith(
                CsvMapperFactory.newInstance()
                        .discriminator(A.class).onGetter(r -> r.getString(2)).with( b -> {
                            for(int i = 1; i <= 40; i++) {
                                try {
                                    b.when("typea" + i, Class.forName("org.simpleflatmapper.csv.DiscriminatorBenchmark$A" + i));
                                } catch (ClassNotFoundException e) {
                                    throw new Error(e);
                                }
                            }
                        })
                        .discriminator(B.class).onGetter(r -> r.getString(4)).with( b -> {
                            for(int i = 1; i <= 20; i++) {
                                try {
                                    b.when("typeb" + i, Class.forName("org.simpleflatmapper.csv.DiscriminatorBenchmark$B" + i));
                                } catch (ClassNotFoundException e) {
                                    throw new Error(e);
                                }
                            }
                        })
                        .addKeys("id")
                        .newMapper(A.class)
        ).stream(file, s -> {
            s.forEach(aConsumer);
            return null;
        });
    }


    public static void main(String[] args) throws IOException {
        DiscriminatorBenchmark db = new DiscriminatorBenchmark();
        db.setUp();

        db.withDiscriminatorWithKeys(v -> {
            System.out.println("v = " + v);
        });

        db.tearDown();
    }

    public static class A {
        public final Integer id;
        public final String name;
        public final String type;
        public final B b;

        public A(Integer id, String name, String type, B b) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.b = b;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", b=" + b +
                    '}';
        }
    }

    public static class C
    {
        public final Integer id;

        public C(Integer id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "C{" +
                    "id=" + id +
                    '}';
        }
    }

    public static class B
    {
        public final Integer id;
        public final String type;

        public B(Integer id, String type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public String toString() {
            return "B{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
    
    public static class B1 extends B
    {
        public final C c;

        public B1(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B2 extends B
    {
        public final C c;

        public B2(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }

    }
    public static class B3 extends B
    {
        public final C c;

        public B3(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }

    }
    public static class B4 extends B
    {
        public final C c;

        public B4(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B5 extends B
    {
        public final C c;

        public B5(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B6 extends B
    {
        public final C c;

        public B6(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B7 extends B
    {
        public final C c;

        public B7(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B8 extends B
    {
        public final C c;

        public B8(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B9 extends B
    {
        public final C c;

        public B9(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B10 extends B
    {
        public final C c;

        public B10(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }

    public static class B11 extends B
    {
        public final C c;

        public B11(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B12 extends B
    {
        public final C c;

        public B12(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B13 extends B
    {
        public final C c;

        public B13(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B14 extends B
    {
        public final C c;

        public B14(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B15 extends B
    {
        public final C c;

        public B15(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B16 extends B
    {
        public final C c;

        public B16(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B17 extends B
    {
        public final C c;

        public B17(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B18 extends B
    {
        public final C c;

        public B18(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B19 extends B
    {
        public final C c;

        public B19(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }
    public static class B20 extends B
    {
        public final C c;

        public B20(Integer id, String type, C c) {
            super(id, type);
            this.c = c;
        }
        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", c=" + c +
                    '}';
        }
    }

    

    public static class A1 extends A
    {

        public A1(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A2 extends A
    {

        public A2(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A3 extends A
    {

        public A3(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A4 extends A
    {

        public A4(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A5 extends A
    {

        public A5(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A6 extends A
    {

        public A6(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A7 extends A
    {

        public A7(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A8 extends A
    {

        public A8(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A9 extends A
    {

        public A9(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A10 extends A
    {

        public A10(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A11 extends A
    {

        public A11(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A12 extends A
    {

        public A12(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A13 extends A
    {

        public A13(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A14 extends A
    {

        public A14(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A15 extends A
    {

        public A15(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A16 extends A
    {

        public A16(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A17 extends A
    {

        public A17(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A18 extends A
    {

        public A18(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A19 extends A
    {

        public A19(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A20 extends A
    {

        public A20(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A21 extends A
    {

        public A21(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A22 extends A
    {

        public A22(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A23 extends A
    {

        public A23(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A24 extends A
    {

        public A24(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A25 extends A
    {

        public A25(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A26 extends A
    {

        public A26(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A27 extends A
    {

        public A27(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A28 extends A
    {

        public A28(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A29 extends A
    {

        public A29(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A30 extends A
    {

        public A30(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A31 extends A
    {

        public A31(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A32 extends A
    {

        public A32(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A33 extends A
    {

        public A33(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A34 extends A
    {

        public A34(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A35 extends A
    {

        public A35(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A36 extends A
    {

        public A36(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A37 extends A
    {

        public A37(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A38 extends A
    {

        public A38(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A39 extends A
    {

        public A39(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }
    public static class A40 extends A
    {

        public A40(Integer id, String name, String type, B b) {
            super(id, name, type, b);
        }
    }

}

