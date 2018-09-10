package org.simpleflatmapper.csv.test;

import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.property.DateFormatProperty;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.Predicate;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class CsvMapperDateFormatTest {

	public static final Predicate<CsvColumnKey> TRUE = new Predicate<CsvColumnKey>() {
        @Override
        public boolean test(CsvColumnKey csvColumnKey) {
            return true;
        }
    };

	public static class ObjectWithDate {
		private final Date date1;
		private Date date2;
		private Date date3;
		public ObjectWithDate(Date date1) {
			this.date1 = date1;
		}
		public Date getDate2() {
			return date2;
		}
		public void setDate2(Date date2) {
			this.date2 = date2;
		}
		public Date getDate1() {
			return date1;
		}

		public Date getDate3() {
			return date3;
		}

		public void setDate3(Date date3) {
			this.date3 = date3;
		}
	}
	@Test
	public void testSetCustomDateFormat() throws ParseException, MappingException, IOException {
		String format = "dd/MM/yyyy HH:mm";
		
		CsvMapper<ObjectWithDate> mapper = CsvMapperFactory.newInstance().defaultDateFormat(format).newMapper(ObjectWithDate.class);
		
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		
		String strDate = sdf.format(new Date());
		Date date = sdf.parse(strDate);
		
		String data = "date1,date2\n" + strDate + "," + strDate;
		
		
		List<ObjectWithDate> list = mapper.forEach(new StringReader(data), new ListCollector<ObjectWithDate>()).getList();
		assertEquals(1, list.size());
		
		assertEquals(date, list.get(0).date1);
		assertEquals(date, list.get(0).date2);
	}
	
	@Test
	public void testErrorHandlerAsm() throws ParseException, MappingException, IOException {
		
		@SuppressWarnings("unchecked")
		FieldMapperErrorHandler<CsvColumnKey> fieldMapperErrorHandler = mock(FieldMapperErrorHandler.class);
		CsvMapper<ObjectWithDate> mapper = CsvMapperFactory.newInstance().fieldMapperErrorHandler(fieldMapperErrorHandler).newMapper(ObjectWithDate.class);
		
		String data = "date3,date1,date2\nwrong date,wrong date,wrong date";
		List<ObjectWithDate> list = mapper.forEach(new StringReader(data), new ListCollector<ObjectWithDate>()).getList();
		assertEquals(1, list.size());
		
		assertNull(list.get(0).date1);
		assertNull(list.get(0).date2);
		assertNull(list.get(0).date3);

		verify(fieldMapperErrorHandler).errorMappingField(eq(new CsvColumnKey("date1", 1)), any(), isNull(), any(Exception.class), any(Context.class));
		verify(fieldMapperErrorHandler).errorMappingField(eq(new CsvColumnKey("date3", 0)), any(),same(list.get(0)), any(Exception.class), any(Context.class));
		verify(fieldMapperErrorHandler).errorMappingField(eq(new CsvColumnKey("date2", 2)), any(), same(list.get(0)), any(Exception.class), any(Context.class));
	}

	@Test
	public void testErrorHandlerNoAsm() throws ParseException, MappingException, IOException {

		@SuppressWarnings("unchecked")
		FieldMapperErrorHandler<CsvColumnKey> fieldMapperErrorHandler = mock(FieldMapperErrorHandler.class);
		CsvMapper<ObjectWithDate> mapper = CsvMapperFactory.newInstance().useAsm(false).fieldMapperErrorHandler(fieldMapperErrorHandler).newMapper(ObjectWithDate.class);

		String data = "date3,date1,date2\nwrong date,wrong date,wrong date";
		List<ObjectWithDate> list = mapper.forEach(new StringReader(data), new ListCollector<ObjectWithDate>()).getList();
		assertEquals(1, list.size());

		assertNull(list.get(0).date1);
		assertNull(list.get(0).date2);
		assertNull(list.get(0).date3);

		verify(fieldMapperErrorHandler).errorMappingField(eq(new CsvColumnKey("date1", 1)), any(), isNull(), any(Exception.class), any(Context.class));
		verify(fieldMapperErrorHandler).errorMappingField(eq(new CsvColumnKey("date3", 0)), any(), same(list.get(0)), any(Exception.class), any(Context.class));
		verify(fieldMapperErrorHandler).errorMappingField(eq(new CsvColumnKey("date2", 2)), any(),same(list.get(0)), any(Exception.class), any(Context.class));
	}


	@Test
	public void testReadMultipleFormat() throws  Exception {
		String format1 = "dd/MM/yyyy";
		String format2 = "MM-dd-yyyy";
		String format3 = "yyyyMMdd";

		CsvMapper<ObjectWithDate> mapper =
				CsvMapperFactory
						.newInstance()
						.addColumnProperty(TRUE, new DateFormatProperty(format1))
						.addColumnProperty(TRUE, new DateFormatProperty(format2))
						.addColumnProperty(TRUE, new DateFormatProperty(format3))
						.newMapper(ObjectWithDate.class);

		String data = "date1\n18/06/2016\n06-19-2016\n20160620";



		List<ObjectWithDate> list = mapper.forEach(new StringReader(data), new ListCollector<ObjectWithDate>()).getList();
		assertEquals(3, list.size());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setTimeZone(TimeZone.getDefault());
		assertEquals(sdf.parse("20160618"), list.get(0).date1);
		assertEquals(sdf.parse("20160619"), list.get(1).date1);
		assertEquals(sdf.parse("20160620"), list.get(2).date1);
	}

	@Test
	public void testReadMultipleFormatOverrideDefault() throws  Exception {
		String format1 = "dd/MM/yyyy";
		String format2 = "MM-dd-yyyy";

		CsvMapper<ObjectWithDate> mapper =
				CsvMapperFactory
						.newInstance()
						.defaultDateFormat(format1)
						.addColumnProperty(TRUE, new DateFormatProperty(format2))
						.newMapper(ObjectWithDate.class);

		String data1 = "date1\n18/06/2016";
		String data2 = "date1\n06-19-2016";


		try {
			mapper.forEach(new StringReader(data1), new ListCollector<ObjectWithDate>()).getList();
			fail();
		} catch (Exception e) {
			//
		}

		List<ObjectWithDate> list = mapper.forEach(new StringReader(data2), new ListCollector<ObjectWithDate>()).getList();

		assertEquals(1, list.size());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		assertEquals(sdf.parse("20160619"), list.get(0).date1);
	}
}
