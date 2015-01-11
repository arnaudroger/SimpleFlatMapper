package org.sfm.csv;

import org.junit.Before;
import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPrimitiveObject;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.*;
import org.sfm.utils.ListHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CsvMapperCustomReaderTest {

    private CsvMapperFactory csvMapperFactory;

    @Before
    public void setUp() {
        csvMapperFactory = CsvMapperFactory.newInstance();
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
        List<DbObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListHandler<DbObject>()).getList();
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
        List<DbFinalObject> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListHandler<DbFinalObject>()).getList();
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
                    CsvColumnDefinition.newCustomReader(new CellValueReader<Boolean>() {
                                                            @Override
                                                            public Boolean read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                return true;
                                                            }
                                                        }
                    ))
            .addMapping("p_byte",
                    CsvColumnDefinition.newCustomReader(new CellValueReader<Byte>() {
                                                            @Override
                                                            public Byte read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                return 35;
                                                            }
                                                        }
                    ))
            .addMapping("p_character",
                    CsvColumnDefinition.newCustomReader(new CellValueReader<Character>() {
                                                            @Override
                                                            public Character read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                return 37;
                                                            }
                                                        }
                    ))
            .addMapping("p_short",
                    CsvColumnDefinition.newCustomReader(new CellValueReader<Short>() {
                                                            @Override
                                                            public Short read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                return 39;
                                                            }
                                                        }
                    ))
            .addMapping("p_int",
                CsvColumnDefinition.newCustomReader(new CellValueReader<Integer>() {
                                                        @Override
                                                        public Integer read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                            return 0x76666;
                                                        }
                                                    }
                ))
            .addMapping("p_long",
                    CsvColumnDefinition.newCustomReader(new CellValueReader<Long>() {
                                                            @Override
                                                            public Long read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                return 0xf76666l;
                                                            }
                                                        }
                    ))
            .addMapping("p_float",
                    CsvColumnDefinition.newCustomReader(new CellValueReader<Float>() {
                                                            @Override
                                                            public Float read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                return 3.14f;
                                                            }
                                                        }
                    ))
            .addMapping("p_double",
                    CsvColumnDefinition.newCustomReader(new CellValueReader<Double>() {
                                                            @Override
                                                            public Double read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                                                                return 3.1526;
                                                            }
                                                        }
                    ))
            .mapper();

        DbPrimitiveObject object = intMapper.iterate(new StringReader("false,12,12,12,12,12,12,12")).next();

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
                                CsvColumnDefinition.newCustomReader(new BooleanCellValueReader() {
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
                                CsvColumnDefinition.newCustomReader(new ByteCellValueReader() {
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
                                CsvColumnDefinition.newCustomReader(new CharCellValueReader() {
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
                                CsvColumnDefinition.newCustomReader(new ShortCellValueReader() {
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
                                CsvColumnDefinition.newCustomReader(new IntegerCellValueReader() {
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
                                CsvColumnDefinition.newCustomReader(new LongCellValueReader() {
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
                                CsvColumnDefinition.newCustomReader(new FloatCellValueReader() {
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
                                CsvColumnDefinition.newCustomReader(new DoubleCellValueReader() {
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

        DbPrimitiveObject object = intMapper.iterate(new StringReader("false,12,12,12,12,12,12,12")).next();

        assertEquals(true, object.ispBoolean());
        assertEquals(35, object.getpByte());
        assertEquals(37, object.getpCharacter());
        assertEquals(39, object.getpShort());
        assertEquals(0x76666, object.getpInt());
        assertEquals(0xf76666l, object.getpLong());
        assertEquals(3.14f, object.getpFloat(), 0.0001);
        assertEquals(3.1526, object.getpDouble(), 0.000001);
    }

}
