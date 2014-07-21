package org.flatmap.perf;

import java.util.Random;

import org.flatmap.reflect.FieldSetter;
import org.flatmap.reflect.MethodSetter;
import org.flatmap.reflect.Setter;
import org.flatmap.reflect.primitive.IntFieldSetter;
import org.flatmap.reflect.primitive.IntMethodSetter;
import org.flatmap.reflect.primitive.IntSetter;
import org.junit.BeforeClass;
import org.junit.Test;

/***
 * 
 Exectime String                 292801513
 Exectime Number                 178614125
MethodSetter Exectime String    1339894001
IntMethodSetter Exectime Number 5321094750
FieldSetter Exectime String     3243315335
IntFieldSetter Exectime Number  3490070954

 *
 */
public class SetterPerfTest {

	
	public static class MyClass {
		public String string;
		public int number;
		public String getString() {
			return string;
		}
		public void setString(String string) {
			this.string = string;
		}
		public int getNumber() {
			return number;
		}
		public void setNumber(int number) {
			this.number = number;
		}
	}


	private static final int WARMUP_NB = 1000000;


	private static final int EXECT_NB = 100000000;
	
	
	private static String[] sdata;
	private static int[] idata;
	
	
	static FieldSetter<MyClass, String> stringFieldSetter;
	static IntFieldSetter<MyClass> numberFieldSetter;

	static MethodSetter<MyClass, String> stringMethodSetter;
	static IntMethodSetter<MyClass> numberMethodSetter;

	static Setter<MyClass, String> stringDirectSetter;
	static IntSetter<MyClass> numberDirectSetter;
	
	@BeforeClass
	public static void setUp() throws NoSuchFieldException, SecurityException, NoSuchMethodException {
		stringFieldSetter = new FieldSetter<MyClass, String>(MyClass.class.getDeclaredField("string"));
		numberFieldSetter = new IntFieldSetter<MyClass>(MyClass.class.getDeclaredField("number"));
		
		stringMethodSetter = new MethodSetter<MyClass, String>(MyClass.class.getMethod("setString", String.class));
		numberMethodSetter = new IntMethodSetter<MyClass>(MyClass.class.getMethod("setNumber", int.class));
		
		stringDirectSetter = new Setter<MyClass, String>() {
			@Override
			public void set(MyClass target, String value) throws Exception {
				target.setString(value);
			}

			@Override
			public Class<? extends String> getPropertyType() {
				return String.class;
			}
		};
		
		numberDirectSetter = new IntSetter<MyClass>() {
			@Override
			public void setInt(MyClass target, int value) throws Exception {
				target.setNumber(value);
			}
			
		};
		Random r = new Random();
		sdata = new String[256];
		idata = new int[256];
		for(int i = 0; i < 256; i++) {
			int j = r.nextInt();
			sdata[i] = String.valueOf(i);
			idata[i] = j;
		}
		
	}
	
	@Test
	public void testPerfField() throws Exception {
		runStringTest(stringFieldSetter);
		runNumberTest(numberFieldSetter);
	}
	@Test
	public void testPerfMethod() throws Exception {
		runStringTest(stringMethodSetter);
		runNumberTest(numberMethodSetter);
	}
	@Test
	public void testPerfDirect() throws Exception {
		runStringTest(stringDirectSetter);
		runNumberTest(numberDirectSetter);
	}
	
	private void runStringTest(Setter<MyClass, String> stringSetter) throws Exception {
		// warmup 
		runStringIteration(stringSetter, WARMUP_NB);
		
		long startTime = System.nanoTime();
		runStringIteration(stringSetter, EXECT_NB);
		long elapsed = System.nanoTime()- startTime;
		System.out.println(stringSetter.getClass().getSimpleName() + " Exectime String " + elapsed);
	}
	private void runNumberTest(IntSetter<MyClass> numberSetter) throws Exception {
		// warmup 
		runNumberIteration(numberSetter, WARMUP_NB);
		
		long startTime = System.nanoTime();
		runNumberIteration(numberSetter, EXECT_NB);
		long elapsed = System.nanoTime()- startTime;
		System.out.println(numberSetter.getClass().getSimpleName() + " Exectime Number " + elapsed  );
	}
	
	
	
	private void runStringIteration(Setter<MyClass, String> setter, int nb) throws Exception {
		MyClass object = new MyClass();
		for(int i = 0; i < nb; i++) {
			setter.set(object, sdata[i & 0xff] );
		}
	}
	private void runNumberIteration(IntSetter<MyClass> setter, int nb) throws Exception {
		MyClass object = new MyClass();
		for(int i = 0; i < nb; i++) {
			setter.setInt(object, idata[i & 0xff]);
		}
	}
}
