package org.simpleflatmapper.proto;

import com.google.protobuf.Timestamp;
import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.map.property.DateFormatProperty;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class ProtoClassTest {
    /*
    message Person {
  string name = 1;
  int32 id = 2;  // Unique ID number for this person.
  string email = 3;

  enum PhoneType {
    MOBILE = 0;
    HOME = 1;
    WORK = 2;
  }

  message PhoneNumber {
    string number = 1;
    PhoneType type = 2;
  }

  repeated PhoneNumber phones = 4;
}

// Our address book file is just one of these.
message AddressBook {
  repeated Person people = 1;
}
     */

    @Test
    public void testMapPhone() throws IOException {

        CsvMapper<AddressBookProtos.Person.PhoneNumber> csvMapper =
                CsvMapperFactory
                        .newInstance()
                        .useAsm(false)
                        .newBuilder(AddressBookProtos.Person.PhoneNumber.class)
                        .addMapping("number")
                        .addMapping("type")
                        .mapper();


        AddressBookProtos.Person.PhoneNumber phones =  csvMapper.iterator(new StringReader("+4411223333,MOBILE")).next();

        assertEquals("+4411223333", phones.getNumber());
        assertEquals(AddressBookProtos.Person.PhoneType.MOBILE, phones.getType());
    }
    
    @Test
    public void testMapPerson() throws IOException {

        CsvMapper<AddressBookProtos.Person> csvMapper = 
                CsvMapperFactory
                        .newInstance()
                        .enableSpeculativeArrayIndexResolution()
                        .useAsm(false)
                        .newBuilder(AddressBookProtos.Person.class)
                        .addMapping("name")
                        .addMapping("id")
                        .addMapping("email")
                        .addMapping("phones_number")
                        .addMapping("phones_type")
                        .addMapping("phones_number")
                        .addMapping("phones_type")
                        .mapper();

        AddressBookProtos.Person person = csvMapper.iterator(new StringReader("arnaud,1,arnaud.roger@gmail.com,+4411223333,MOBILE,+4411223334,HOME")).next();
        
        assertEquals("arnaud", person.getName());
        assertEquals(1, person.getId());
        assertEquals("arnaud.roger@gmail.com", person.getEmail());
        
        assertEquals(2, person.getPhonesCount());
        AddressBookProtos.Person.PhoneNumber phones = person.getPhones(0);

        assertEquals("+4411223333", phones.getNumber());
        assertEquals(AddressBookProtos.Person.PhoneType.MOBILE, phones.getType());

        phones = person.getPhones(1);

        assertEquals("+4411223334", phones.getNumber());
        assertEquals(AddressBookProtos.Person.PhoneType.HOME, phones.getType());

    }

    @Test
    public void testMapPersonTs() throws IOException, ParseException {
        CsvMapper<AddressBookProtos.Person> csvMapper =
                CsvMapperFactory
                        .newInstance()
                        .useAsm(false)
                        .newBuilder(AddressBookProtos.Person.class)
                        .addMapping("name")
                        .addMapping("ts", new DateFormatProperty("yyyyMMdd"))
                        .mapper();

        AddressBookProtos.Person person = csvMapper.iterator(new StringReader("arnaud,20170607")).next();

        assertEquals("arnaud", person.getName());
        assertEquals(new SimpleDateFormat("yyyyMMdd").parse("20170607").getTime(), person.getTs().getSeconds() * 1000);

    }

    @Test
    public void testMapPersonIntValue() throws IOException, ParseException {
        CsvMapper<AddressBookProtos.Person> csvMapper =
                CsvMapperFactory
                        .newInstance()
                        .useAsm(false)
                        .newBuilder(AddressBookProtos.Person.class)
                        .addMapping("name")
                        .addMapping("oint")
                        .mapper();

        AddressBookProtos.Person person = csvMapper.iterator(new StringReader("arnaud,36")).next();

        assertEquals("arnaud", person.getName());
        assertEquals(36, person.getOint().getValue());

    }
    
    
    @Test
    public void testClassMeta() throws Exception {

        ClassMeta<AddressBookProtos.Person> classMeta = ReflectionService
                .newInstance()
                .getClassMeta(AddressBookProtos.Person.class);


        PropertyMeta<AddressBookProtos.Person, Object> ts = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("ts"), new Object[0], (TypeAffinity)null, PropertyFinder.PropertyFilter.trueFilter());
        
        assertEquals(Timestamp.class, ts.getPropertyType());

    }
}
