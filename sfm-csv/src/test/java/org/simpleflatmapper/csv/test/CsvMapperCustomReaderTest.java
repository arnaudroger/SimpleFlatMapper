package org.simpleflatmapper.csv.test;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CellValueReaderFactory;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperBuilder;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.ParsingContextFactoryBuilder;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.DbPrimitiveObject;
import org.simpleflatmapper.csv.impl.cellreader.*;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuples;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.Predicate;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CsvMapperCustomReaderTest {

    private CsvMapperFactory csvMapperFactory;

    @Before
    public void setUp() {
        csvMapperFactory = CsvMapperFactory.newInstance();
    }
    @Test
    public void testMapDbObjectCustomReaderWithWrongType() throws Exception {
        csvMapperFactory
                .addCustomValueReader("id", new CellValueReader<String>() {

                    @Override
                    public String read(char[] chars, int offset, int length,
                                       ParsingContext parsingContext) {
                        return "dddd";
                    }
                })
                .addCustomValueReader("typeName", new CellValueReader<String>() {

                    @Override
                    public String read(char[] chars, int offset, int length,
                                       ParsingContext parsingContext) {
                        return "dddd";
                    }
                });



        try {
            csvMapperFactory
                    .newBuilder(DbObject.class)
                    .addMapping("id");
            fail("Expect exception due to incompatible custom reader");
        } catch(Exception e) {
        }
        try {
            csvMapperFactory.newBuilder(DbObject.class).addMapping("typeName");
            fail("Expect exception due to incompatible custom reader");
        } catch(Exception e) {
        }
    }


    @Test
    public void testMapDbObjectCustomReader() throws Exception {
        CsvMapperBuilder<DbObject> builder = csvMapperFactory.addCustomValueReader("id", new CellValueReader<Long>() {

            @Override
            public Long read(char[] chars, int offset, int length,
                             ParsingContext parsingContext) {
                return 0x5677al;
            }
        }).newBuilder(DbObject.class);
        CsvMapperBuilderTest.addDbObjectFields(builder);

        CsvMapper<DbObject> mapper = builder.mapper();
        List<DbObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListCollector<DbObject>()).getList();
        assertEquals(1, list.size());
        assertEquals(0x5677al, list.get(0).getId());
        assertEquals("name 1", list.get(0).getName());
    }

    @Test
    public void testMapDbFinalObjectCustomReader() throws Exception {
        CsvMapperBuilder<DbFinalObject> builder = csvMapperFactory.addCustomValueReader("id", new CellValueReader<Long>() {

            @Override
            public Long read(char[] chars, int offset, int length,
                             ParsingContext parsingContext) {
                return 0x5677al;
            }
        }).newBuilder(DbFinalObject.class);
        CsvMapperBuilderTest.addDbObjectFields(builder);

        CsvMapper<DbFinalObject> mapper = builder.mapper();
        List<DbFinalObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListCollector<DbFinalObject>()).getList();
        assertEquals(1, list.size());
        assertEquals(0x5677al, list.get(0).getId());
        assertEquals("name 1", list.get(0).getName());
    }


    @Test
    public  void testCustomReaderPrimitivesWithBoxReader() throws IOException {
        CsvMapper<DbPrimitiveObject> intMapper =
            csvMapperFactory
            .newBuilder(DbPrimitiveObject.class)
            .addMapping("p_boolean",
                    CsvColumnDefinition.customReaderDefinition(new CellValueReader<Boolean>() {
                                                                   @Override
                                                                   public Boolean read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                       return true;
                                                                   }
                                                               }
                    ))
            .addMapping("p_byte",
                    CsvColumnDefinition.customReaderDefinition(new CellValueReader<Byte>() {
                                                                   @Override
                                                                   public Byte read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                       return 35;
                                                                   }
                                                               }
                    ))
            .addMapping("p_character",
                    CsvColumnDefinition.customReaderDefinition(new CellValueReader<Character>() {
                                                                   @Override
                                                                   public Character read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                       return 37;
                                                                   }
                                                               }
                    ))
            .addMapping("p_short",
                    CsvColumnDefinition.customReaderDefinition(new CellValueReader<Short>() {
                                                                   @Override
                                                                   public Short read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                       return 39;
                                                                   }
                                                               }
                    ))
            .addMapping("p_int",
                CsvColumnDefinition.customReaderDefinition(new CellValueReader<Integer>() {
                                                               @Override
                                                               public Integer read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                   return 0x76666;
                                                               }
                                                           }
                ))
            .addMapping("p_long",
                    CsvColumnDefinition.customReaderDefinition(new CellValueReader<Long>() {
                                                                   @Override
                                                                   public Long read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                       return 0xf76666l;
                                                                   }
                                                               }
                    ))
            .addMapping("p_float",
                    CsvColumnDefinition.customReaderDefinition(new CellValueReader<Float>() {
                                                                   @Override
                                                                   public Float read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                       return 3.14f;
                                                                   }
                                                               }
                    ))
            .addMapping("p_double",
                    CsvColumnDefinition.customReaderDefinition(new CellValueReader<Double>() {
                                                                   @Override
                                                                   public Double read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                       return 3.1526;
                                                                   }
                                                               }
                    ))
            .mapper();

        DbPrimitiveObject object = intMapper.iterator(new StringReader("false,12,12,12,12,12,12,12")).next();

        assertEquals(true, object.ispBoolean());
        assertEquals(35, object.getpByte());
        assertEquals(37, object.getpCharacter());
        assertEquals(39, object.getpShort());
        assertEquals(0x76666, object.getpInt());
        assertEquals(0xf76666l, object.getpLong());
        assertEquals(3.14f, object.getpFloat(), 0.0001);
        assertEquals(3.1526, object.getpDouble(), 0.000001);
    }

    @Test
    public  void testCustomReaderPrimitivesWithDirectReader() throws IOException {
        CsvMapper<DbPrimitiveObject> intMapper =
                csvMapperFactory
                        .newBuilder(DbPrimitiveObject.class)
                        .addMapping("p_boolean",
                                CsvColumnDefinition.customReaderDefinition(new BooleanCellValueReader() {
                                                                               @Override
                                                                               public Boolean read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return true;
                                                                               }

                                                                               @Override
                                                                               public boolean readBoolean(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return true;
                                                                               }
                                                                           }
                                ))
                        .addMapping("p_byte",
                                CsvColumnDefinition.customReaderDefinition(new ByteCellValueReader() {
                                                                               @Override
                                                                               public Byte read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 35;
                                                                               }

                                                                               @Override
                                                                               public byte readByte(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 35;
                                                                               }
                                                                           }
                                ))
                        .addMapping("p_character",
                                CsvColumnDefinition.customReaderDefinition(new CharCellValueReader() {
                                                                               @Override
                                                                               public Character read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 37;
                                                                               }

                                                                               @Override
                                                                               public char readChar(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 37;
                                                                               }
                                                                           }
                                ))
                        .addMapping("p_short",
                                CsvColumnDefinition.customReaderDefinition(new ShortCellValueReader() {
                                                                               @Override
                                                                               public Short read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 39;
                                                                               }

                                                                               @Override
                                                                               public short readShort(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 39;
                                                                               }
                                                                           }
                                ))
                        .addMapping("p_int",
                                CsvColumnDefinition.customReaderDefinition(new IntegerCellValueReader() {
                                                                               @Override
                                                                               public Integer read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 0x76666;
                                                                               }

                                                                               @Override
                                                                               public int readInt(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 0x76666;
                                                                               }
                                                                           }
                                ))
                        .addMapping("p_long",
                                CsvColumnDefinition.customReaderDefinition(new LongCellValueReader() {
                                                                               @Override
                                                                               public long readLong(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 0xf76666l;
                                                                               }

                                                                               @Override
                                                                               public Long read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 0xf76666l;
                                                                               }
                                                                           }
                                ))
                        .addMapping("p_float",
                                CsvColumnDefinition.customReaderDefinition(new FloatCellValueReader() {
                                                                               @Override
                                                                               public Float read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 3.14f;
                                                                               }

                                                                               @Override
                                                                               public float readFloat(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 3.14f;
                                                                               }
                                                                           }
                                ))
                        .addMapping("p_double",
                                CsvColumnDefinition.customReaderDefinition(new DoubleCellValueReader() {
                                                                               @Override
                                                                               public Double read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 3.1526;
                                                                               }

                                                                               @Override
                                                                               public double readDouble(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                                   return 3.1526;
                                                                               }
                                                                           }
                                ))
                        .mapper();

        DbPrimitiveObject object = intMapper.iterator(new StringReader("false,12,12,12,12,12,12,12")).next();

        assertEquals(true, object.ispBoolean());
        assertEquals(35, object.getpByte());
        assertEquals(37, object.getpCharacter());
        assertEquals(39, object.getpShort());
        assertEquals(0x76666, object.getpInt());
        assertEquals(0xf76666l, object.getpLong());
        assertEquals(3.14f, object.getpFloat(), 0.0001);
        assertEquals(3.1526, object.getpDouble(), 0.000001);
    }

    @Test
    public void testCustomCsvReaderValueFactory() throws IOException {
        CsvMapper<Tuple2<String, String>> csvMapper = CsvMapperFactory.newInstance().addColumnDefinition(new Predicate<CsvColumnKey>() {
            @Override
            public boolean test(CsvColumnKey csvColumnKey) {
                return true;
            }
        }, CsvColumnDefinition.customCellValueReaderFactoryDefinition(new CellValueReaderFactory() {

            @SuppressWarnings("unchecked")
            @Override
            public <P> CellValueReader<P> getReader(Type propertyType, final int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder builder) {
                return (CellValueReader<P>) new CellValueReader<String>() {
                    @Override
                    public String read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                        return "g" + index;
                    }
                };
            }
        })).newMapper(Tuples.typeDef(String.class, String.class));

        Tuple2<String, String> value = csvMapper.iterator(new StringReader("b0,b1\nc0,c1")).next();

        assertEquals("g0", value.first());
        assertEquals("g1", value.second());
    }
}
