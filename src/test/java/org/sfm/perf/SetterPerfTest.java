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
import org.sfm.reflect.asm.AsmSetterFactory;
import org.sfm.reflect.primitive.IntFieldSetter;
import org.sfm.reflect.primitive.IntMethodSetter;
import org.sfm.reflect.primitive.IntSetter;

/***
 * 
SettersetStringMyClassString Exectime String 728977000
SettersetNumberMyClassint Exectime Number    635268000
 Exectime String                             128610000
 Exectime Number                             573275000
FieldSetter Exectime String                 1552173000
IntFieldSetter Exectime Number              1298505000
MethodSetter Exectime String                1020345000
IntMethodSetter Exectime Number             2944488000


Alone
SettersetStringMyClassString Exectime String 	109234000
SettersetNumberMyClassint Exectime Number 		 75086000
 Exectime String 								110332000
 Exectime Number 								 70675000


Two Togetgher looks like a dispatch optimisation
SettersetStringMyClassString Exectime String 	63223000
SettersetNumberMyClassint Exectime Number 		61856000
 Exectime String 								63112000
 Exectime Number 								61756000


SettersetStringMyClassString Exectime String 	706857000
SettersetNumberMyClassint Exectime Number 		624253000
 Exectime String 								617482000
 Exectime Number 								480755000
MethodSetter Exectime String 					967725000
IntMethodSetter Exectime Number 			   2712176000


Stable Result :
SettersetStringMyClassString Exectime String 	 623102000
SettersetNumberMyClassint Exectime Number 		 482238000
 Exectime String 								 699182000
 Exectime Number 								 568592000
MethodSetter Exectime String 					 933670000
IntMethodSetter Exectime Number 				2730573000
FieldSetter Exectime String 					1465671000
IntFieldSetter Exectime Number 					1192381000

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
		
		AsmSetterFactory asmSetterFactory = new AsmSetterFactory();
		
		stringAsmSetter = asmSetterFactory.createSetter(setStringMethod);
		numberAsmSetter = (IntSetter<MyClass>) asmSetterFactory.createSetter(setNumberMethod);
		
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
	
		runWarmUp();

	}

	public static void runWarmUp() throws Exception {
		
		runStringIteration(stringAsmSetter, WARMUP_NB);
		runNumberIteration(numberAsmSetter, WARMUP_NB);

		
		runStringIteration(stringDirectSetter, WARMUP_NB);
		runNumberIteration(numberDirectSetter, WARMUP_NB);
		
		runStringIteration(stringMethodSetter, WARMUP_NB);
		runNumberIteration(numberMethodSetter, WARMUP_NB);
		
		runStringIteration(stringFieldSetter, WARMUP_NB);
		runNumberIteration(numberFieldSetter, WARMUP_NB);

	}
	
	@Test
	public void runPerfTest() throws Exception {
		testPerfAsmMethod();
		testPerfDirect();
		testPerfMethod();
		testPerfField();
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
		System.out.println(stringSetter.getClass().getSimpleName() + " Exectime String " + elapsed);
	}
	private void runNumberTest(IntSetter<MyClass> numberSetter) throws Exception {
		
		long startTime = System.nanoTime();
		runNumberIteration(numberSetter, EXECT_NB);
		long elapsed = System.nanoTime()- startTime;
		System.out.println(numberSetter.getClass().getSimpleName() + " Exectime Number " + elapsed  );
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
