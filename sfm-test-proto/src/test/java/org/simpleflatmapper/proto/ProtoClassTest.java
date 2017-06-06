package org.simpleflatmapper.proto;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/**
 * Created by aroger on 06/06/2017.
 */
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
                        .useAsm(false)
                        .newBuilder(AddressBookProtos.Person.class)
                        .addMapping("name")
                        .addMapping("id")
                        .addMapping("email")
                        .addMapping("phones_number")
                        .addMapping("phones_type")
                        .mapper();

        AddressBookProtos.Person person = csvMapper.iterator(new StringReader("arnaud,1,arnaud.roger@gmail.com,+4411223333,MOBILE")).next();
        
        assertEquals("arnaud", person.getName());
        assertEquals(1, person.getId());
        assertEquals("arnaud.roger@gmail.com", person.getEmail());
        
        assertEquals(1, person.getPhonesCount());
        AddressBookProtos.Person.PhoneNumber phones = person.getPhones(0);

        assertEquals("+4411223333", phones.getNumber());
        assertEquals(AddressBookProtos.Person.PhoneType.MOBILE, phones.getType());
    }
}
