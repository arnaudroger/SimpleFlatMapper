package org.sfm.csv;

import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

public class CsvMapperImplTest {

	public static Reader dbObjectCsvReader() throws UnsupportedEncodingException {
		Reader sr = new StringReader("1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4");
		return sr;
	}
	
	public static Reader dbObjectCsvReader3Lines() throws UnsupportedEncodingException {
		Reader sr = new StringReader("0,name 0,name0@mail.com,2014-03-04 11:10:03,2,type4\n"
				+ "1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4\n"
				+ "2,name 2,name2@mail.com,2014-03-04 11:10:03,2,type4"
				
				);
		return sr;
	}
	

}
