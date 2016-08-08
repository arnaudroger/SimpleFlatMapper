package org.simpleflatmapper.converter.joda;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;

import static org.simpleflatmapper.converter.test.ConverterServiceTestHelper.testConverter;

public class JodaTimeConverterServiceTest {


    @Test
    public void testDateToJodaDateTime() throws Exception {
        DateTime dateTime = DateTime.now();
        testConverter(dateTime.toDate(), dateTime, Date.class, DateTime.class);
    }

}