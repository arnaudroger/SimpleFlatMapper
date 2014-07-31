package org.sfm.perf;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sfm.reflect.FieldSetter;
import org.sfm.reflect.MethodHandleSetter;
import org.sfm.reflect.MethodSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.primitive.IntFieldSetter;
import org.sfm.reflect.primitive.IntMethodSetter;
import org.sfm.reflect.primitive.IntSetter;

/***
 * 

Stable Result :
Exectime String	 69714000	DirectStringSetter
Exectime Number	 57298000	DirectIntSetter
Exectime String	 68962000	SettersetStringMyClassString
Exectime Number	 62507000	SettersetNumberMyClassint
Exectime String	 97349000	MethodSetter
Exectime Number	253535000	IntMethodSetter
Exectime String	149487000	FieldSetter
Exectime Number	123140000	IntFieldSetter

 *
 */
public class SetterPerfTest {

	
	private static final class DirectIntSetter implements IntSetter<MyClass> {
		@Override
		public void setInt(MyClass target, int value) throws Exception {
			target.setNumber(value);
		}
	}
	private static final class DirectStringSetter implements
			Setter<MyClass, String> {
		@Override
		public void set(MyClass target, String value) throws Exception {
			target.setString(value);
		}

		@Override
		public Class<? extends String> getPropertyType() {
			return String.class;
		}
	}
	public static class MyClass {
		public String string;
		public int number;
		public void setString(String string) {
			this.string = string;
		}
		public void setNumber(int number) {
			this.number = number;
		}
	}
	private static final int WARMUP_NB = 1000000;

	private static final int EXECT_NB =  10000000;
	
	
	private static String[] sdata;
	private static int[] idata;
	
	
	static FieldSetter<MyClass, String> stringFieldSetter;
	static IntFieldSetter<MyClass> numberFieldSetter;

	static MethodSetter<MyClass, String> stringMethodSetter;
	static IntMethodSetter<MyClass> numberMethodSetter;

	static Setter<MyClass, String> stringDirectSetter;
	static IntSetter<MyClass> numberDirectSetter;
	

	static MethodHandleSetter<MyClass, String> stringMethodHandleSetter;
	
	static Setter<MyClass, String> stringAsmSetter;
	static IntSetter<MyClass> numberAsmSetter;
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUp() throws Exception {
		stringFieldSetter = new FieldSetter<MyClass, String>(MyClass.class.getDeclaredField("string"));
		numberFieldSetter = new IntFieldSetter<MyClass>(MyClass.class.getDeclaredField("number"));
		
		Method setStringMethod = MyClass.class.getMethod("setString", String.class);
		Method setNumberMethod = MyClass.class.getMethod("setNumber", int.class);
		
		stringMethodSetter = new MethodSetter<MyClass, String>(setStringMethod);
		numberMethodSetter = new IntMethodSetter<MyClass>(setNumberMethod);
		
		stringMethodHandleSetter = new MethodHandleSetter<>(MethodHandles.lookup().unreflect(setStringMethod));
		
		AsmFactory asmSetterFactory = new AsmFactory();
		
		stringAsmSetter = asmSetterFactory.createSetter(setStringMethod);
		numberAsmSetter = (IntSetter<MyClass>) asmSetterFactory.createSetter(setNumberMethod);
		
		stringDirectSetter = new DirectStringSetter();
		
		numberDirectSetter = new DirectIntSetter();
		Random r = new Random();
		sdata = new String[256];
		idata = new int[256];
		for(int i = 0; i < 256; i++) {
			int j = r.nextInt();
			sdata[i] = String.valueOf(i);
			idata[i] = j;
		}
	
		runWarmUp();

	}

	public static void runWarmUp() throws Exception {
		
		runStringIteration(stringDirectSetter, WARMUP_NB);
		runNumberIteration(numberDirectSetter, WARMUP_NB);
		
		runStringIteration(stringAsmSetter, WARMUP_NB);
		runNumberIteration(numberAsmSetter, WARMUP_NB);
		
		runStringIteration(stringMethodSetter, WARMUP_NB);
		runNumberIteration(numberMethodSetter, WARMUP_NB);
		
		runStringIteration(stringFieldSetter, WARMUP_NB);
		runNumberIteration(numberFieldSetter, WARMUP_NB);

	}
	
	@Test
	public void runPerfTest() throws Exception {
		//for(int i = 0; i < 10; i++) {
			testPerfDirect();
			testPerfAsmMethod();
			testPerfMethod();
			testPerfField();
		//}
	}
	
	public void testPerfField() throws Exception {
		runStringTest(stringFieldSetter);
		runNumberTest(numberFieldSetter);
	}
	public void testPerfMethod() throws Exception {
		runStringTest(stringMethodSetter);
		runNumberTest(numberMethodSetter);
	}
	public void testPerfMethodHandle() throws Exception {
		// to slow to run
		runStringTest(stringMethodHandleSetter);
	}
	
	public void testPerfAsmMethod() throws Exception {
		runStringTest(stringAsmSetter);
		runNumberTest(numberAsmSetter);
	}
	public void testPerfDirect() throws Exception {
		runStringTest(stringDirectSetter);
		runNumberTest(numberDirectSetter);
	}
	
	private void runStringTest(Setter<MyClass, String> stringSetter) throws Exception {
		
		long startTime = System.nanoTime();
		runStringIteration(stringSetter, EXECT_NB);
		long elapsed = System.nanoTime()- startTime;
		System.out.println("Exectime String\t" + elapsed+"\t"+ stringSetter.getClass().getSimpleName());
	}
	private void runNumberTest(IntSetter<MyClass> numberSetter) throws Exception {
		
		long startTime = System.nanoTime();
		runNumberIteration(numberSetter, EXECT_NB);
		long elapsed = System.nanoTime()- startTime;
		System.out.println("Exectime Number\t" + elapsed+"\t"+ numberSetter.getClass().getSimpleName());
	}
	
	
	
	private static void runStringIteration(Setter<MyClass, String> setter, int nb) throws Exception {
		MyClass object = new MyClass();
		for(int i = 0; i < nb; i++) {
			setter.set(object, sdata[i & 0xff] );
		}
	}
	private static void runNumberIteration(IntSetter<MyClass> setter, int nb) throws Exception {
		MyClass object = new MyClass();
		for(int i = 0; i < nb; i++) {
			setter.setInt(object, idata[i & 0xff]);
		}
	}
}
