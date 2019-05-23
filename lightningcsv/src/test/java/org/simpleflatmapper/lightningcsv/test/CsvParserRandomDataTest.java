package org.simpleflatmapper.lightningcsv.test;

import org.junit.Test;
import org.simpleflatmapper.lightningcsv.CsvParser;
import org.simpleflatmapper.lightningcsv.CsvReader;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.Supplier;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

import static org.junit.Assert.fail;

public class CsvParserRandomDataTest {


	public static final Supplier<CsvParser.DSL> NON_OPT_DSL = new Supplier<CsvParser.DSL>() {
		@Override
		public CsvParser.DSL get() {
			return CsvParser.dsl().disableSpecialisedCharConsumer();
		}
	};
	public static final Supplier<CsvParser.DSL> DSL = new Supplier<CsvParser.DSL>() {
		@Override
		public CsvParser.DSL get() {
			return CsvParser.dsl();
		}
	};
	private Random random = new Random();

	private static final int nbIter = 64;
	@Test
	public void testParseOpt() throws IOException {
		testParse(new Supplier<TestData>() {
			@Override
			public TestData get() {
				return createTestData();
			}
		}, DSL);
	}
	@Test
	public void testParseNonOpt() throws IOException {
		testParse(new Supplier<TestData>() {
			@Override
			public TestData get() {
				return createTestData();
			}
		}, NON_OPT_DSL);
	}

	@Test
	public void testParseCROpt() throws IOException {
		testParse(new Supplier<TestData>() {
			@Override
			public TestData get() {
				TestData testData = createTestData();
				testData.carriageReturn = "\r";
				return testData;
			}
		}, DSL);
	}
	@Test
	public void testParseCRNonOpt() throws IOException {
		testParse(new Supplier<TestData>() {
			@Override
			public TestData get() {
				TestData testData = createTestData();
				testData.carriageReturn = "\r";
				return testData;
			}
		}, NON_OPT_DSL);
	}

	@Test
	public void testParseLFOpt() throws IOException {
		testParse(new Supplier<TestData>() {
			@Override
			public TestData get() {
				TestData testData = createTestData();
				testData.carriageReturn = "\n";
				return testData;
			}
		}, DSL);
	}
	@Test
	public void testParseLFNonOpt() throws IOException {
		testParse(new Supplier<TestData>() {
			@Override
			public TestData get() {
				TestData testData = createTestData();
				testData.carriageReturn = "\n";
				return testData;
			}
		}, NON_OPT_DSL);
	}


	@Test
	public void testParsePipeOpt() throws IOException {
		testParse(new Supplier<TestData>() {
			@Override
			public TestData get() {
				TestData testData = createTestData();
				testData.separator = '|';
				return testData;
			}
		}, DSL);
	}
	@Test
	public void testParsePipeNonOpt() throws IOException {
		testParse(new Supplier<TestData>() {
			@Override
			public TestData get() {
				TestData testData = createTestData();
				testData.separator = '|';
				return testData;
			}
		}, NON_OPT_DSL);
	}


	public void testParse(Supplier<TestData> testDataSupplier, Supplier<CsvParser.DSL> dslSupplier) throws IOException {
		for(int i = 0; i < nbIter; i++) {
			System.out.println("i = " + i);
			CsvParser.DSL dsl = dslSupplier.get();
			TestData testData = testDataSupplier.get();
			testParse(dsl.separator(testData.separator).quote(testData.quoteChar), testData);
		}

	}


	private void testParse(CsvParser.DSL dsl, TestData testData) throws IOException {
		testDsl(testData, dsl.bufferSize(1), "1");
		testDsl(testData, dsl.bufferSize(4), "4");
		testDsl(testData, dsl.bufferSize(1).trimSpaces(), "1ts");
		testDsl(testData, dsl.bufferSize(4).trimSpaces(), "4ts");
		testDsl(testData, dsl, "");
		testDsl(testData, dsl.trimSpaces(), "ts");
		testDsl(testData, dsl.parallelReader(), "pr");
	}

	private TestData createTestData() {

		int nbRows = random.nextInt(32) + 10;

		String[][] expectations = new String[nbRows][];
		for(int i = 0; i < nbRows; i++) {
			int nbCols = random.nextInt(16) + 1;
			String[] row = new String[nbCols];
			for(int j = 0; j < row.length; j++) {
				row[j] = newString();
			}
			expectations[i] = row;
		}

		String[] lastRow = expectations[nbRows - 1];

		if (lastRow.length == 1 && lastRow[0].length() == 0) {
			lastRow[0] = "a";
		}

		TestData testData = new TestData(expectations);
		testData.endWithCarriageReturn = (nbRows % 2) == 0;
		return testData;
	}

	private String newString() {
		int size = random.nextInt(128);

		StringBuilder sb = new StringBuilder(size);
		for(int i = 0; i < size; i++) {
			sb.append(nextChar());
		}
		return sb.toString();
	}

	String availables = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890!@£$%^&*()_+,./' \r\n\"|";
	private char nextChar() {
		return availables.charAt(random.nextInt(availables.length()));
	}

	private void testDsl(TestData testData, CsvParser.DSL dsl, String str) throws IOException {

		char[] chars = toCSV(testData).toString().toCharArray();
		// reader call
		testParseAll(testData, dsl, chars);

		testSkipThenParseAll(testData, dsl, chars);

		testSkipThenParseRows(testData, dsl, chars);

		testSkipThenParseRow(testData, dsl, chars);

		// schema call
		testIterator(testData, dsl, chars);

		testSkipAndIterator(testData, dsl, chars);

		testReadRows(testData, dsl, chars);

		testReadRowsWithLimit(testData, dsl, chars);

		testParse(testData, dsl, chars);

		testParseWithLimit(testData, dsl, chars);
	}

	private void testParse(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		String[][] rows =
				dsl.parse(createReader(chars), new AccumulateCellConsumer()).allValues();

		asserCsvEquals(testData.expectations, rows);
	}

	private void asserCsvEquals(String[][] expectations, String[][] rows) {

		if (!Arrays.deepEquals(expectations, rows)) {

			fail();
		}

	}


	private void testParseWithLimit(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {

		String[][] rows =
				dsl.limit(1).parse(createReader(chars), new AccumulateCellConsumer()).allValues();

		asserCsvEquals(toSubArray(testData.expectations, 0, 1), rows);
	}


	private void testReadRows(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		List<String[]> rows =
				dsl.reader(createReader(chars)).read(new ListCollector<String[]>()).getList();

		asserCsvEquals(testData.expectations, rows.toArray(new String[0][]));
	}



	private void testReadRowsWithLimit(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		List<String[]> rows =
				dsl.reader(createReader(chars)).read(new ListCollector<String[]>(), 1).getList();

		asserCsvEquals(toSubArray(testData.expectations, 0, 1), rows.toArray(new String[0][]));
	}

	private void testIterator(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {

		List<String[]> rows = new ArrayList<String[]>();
		for(String[] row : dsl.reader(createReader(chars))) {
			rows.add(row);
		}

		asserCsvEquals(testData.expectations, rows.toArray(new String[0][]));
	}

	private void testSkipAndIterator(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {

		List<String[]> rows = new ArrayList<String[]>();
		for(String[] row : dsl.skip(1).reader(createReader(chars))) {
			rows.add(row);
		}

		asserCsvEquals(toSubArray(testData.expectations, 1), rows.toArray(new String[0][]));
	}

	private void testSkipThenParseRow(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		AccumulateCellConsumer cellConsumer = new AccumulateCellConsumer();
		dsl.skip(1).reader(createReader(chars)).parseRow(cellConsumer);

		asserCsvEquals(toSubArray(testData.expectations, 1, 1), cellConsumer.allValues());
	}

	private void testSkipThenParseRows(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		String[][] cells;
		cells = dsl.skip(1).reader(createReader(chars)).parseRows(new AccumulateCellConsumer(), 2).allValues();

		asserCsvEquals(toSubArray(testData.expectations, 1, 2), cells);
	}

	private void testSkipThenParseAll(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		String[][] cells;
		cells = dsl.skip(1).reader(createReader(chars)).parseAll(new AccumulateCellConsumer()).allValues();

		asserCsvEquals(toSubArray(testData.expectations, 1, testData.expectations.length - 1), cells);
	}

	private String[][] toSubArray(String[][] expectations, int fromIndex) {
		return toSubArray(expectations, fromIndex, expectations.length - fromIndex);
	}
	private String[][] toSubArray(String[][] expectations, int fromIndex, int length) {
		return Arrays.asList(expectations).subList(fromIndex, fromIndex + length).toArray(new String[0][]);
	}

	private void testParseAll(TestData testData, CsvParser.DSL dsl, char[] chars) throws IOException {
		String[][] cells;
		cells =
				dsl.reader(createReader(chars)).parseAll(new AccumulateCellConsumer()).allValues();
		asserCsvEquals(testData.expectations, cells);

		CsvReader reader = dsl.reader(createReader(chars));
		
		AccumulateCellConsumer acc = new AccumulateCellConsumer();
		
		while(reader.rawParseRow(reader.wrapConsumer(acc), true));

		asserCsvEquals(testData.expectations, acc.allValues());

	}

	private Reader createReader(char[] chars) {
		return new CharArrayReader(chars);
	}

	private CharSequence toCSV(TestData testData) {
		String[][] cells = testData.expectations;
		char separator = testData.separator;
		char quoteChar = testData.quoteChar;
		String carriageReturn = testData.carriageReturn;

		StringBuilder sb = new StringBuilder();

		for(int rowIndex = 0; rowIndex < cells.length; rowIndex++) {
			String[] row = cells[rowIndex];

			for (int colIndex = 0; colIndex < row.length; colIndex++) {
				String cell = row[colIndex];
				if (colIndex > 0) {
					sb.append(separator);
				}
				if (needEscape(cell, testData)) {
					sb.append(quoteChar);
					for (int j = 0; j < cell.length(); j++) {
						char c = cell.charAt(j);
						if (c == quoteChar) {
							sb.append(quoteChar);
						}
						sb.append(c);
					}
					sb.append(quoteChar);
				} else {
					sb.append(cell);
				}
			}
			if (testData.endWithCarriageReturn || rowIndex != cells.length -1) {
				sb.append(carriageReturn);
			}

		}

		return sb;
	}

	private boolean needEscape(String cell, TestData testData) {
		for(int i = 0; i < cell.length(); i++) {
			char c = cell.charAt(i);
			if (c == '\r' || c == '\n' || c == ' ') {
				return true;
			}
			if (testData.separator == c || testData.quoteChar == c) {
				return true;
			}

		}
		return false;
	}

	private static class AccumulateCellConsumer implements CellConsumer {
		final List<String[]> rows = new ArrayList<String[]>();
		final List<String> currentRow = new ArrayList<String>();

		@Override
		public void newCell(char[] chars, int offset, int length) {
			currentRow.add(new String(chars, offset, length));
		}

		@Override
		public boolean endOfRow() {
			rows.add(currentRow.toArray(new String[0]));
			currentRow.clear();
			return true;
		}

		@Override
		public void end() {
			if (!currentRow.isEmpty()) {
				rows.add(currentRow.toArray(new String[0]));
			}
			currentRow.clear();
		}

		public String[][] allValues() {
			return rows.toArray(new String[0][]);
		}
	}


	private static class TestData {
		boolean endWithCarriageReturn;
		String[][] expectations;

		char quoteChar = '"';
		char separator = ',';
		String carriageReturn = "\r\n";

		public TestData(String[][] expectations) {
			this.expectations = expectations;
		}
	}


	@Test
	public void testPipe() throws IOException {
		String str = "t46m_q£'EnBtjZNThc2d@n8,ee8q^%HU0MMhBL',\"cHz@nMoaNn9Wf\n" +
				"\n" +
				"m$z|gwXV,rY&\n" +
				"qf3^7)I@\"\n" +
				"\"2|QZLr\"\"£uR@7@QUGtggho(!pn8n6d5zT\n" +
				",Iv87OMfPi/*HAWalhp1d4V\",R@ERA@,\"Wh\n" +
				"*.\",cVQ@YqyXXkuxe)2QKQgVa9$Y!DAeP4G7aW1qeGyFKv@w92gnQt2MzBS3^8H£dv,\"StUA05Lg(£\n" +
				"ptL7|w6ol\",F)5y0(E|JM^tEpAcn340W,\"'E*53(jduU&Bz/k^0r5(\n" +
				"wMT&|(L|6t_5eA$rQ2(2VXL1UgbGS\n" +
				"he3E4F2zqaWPLlH2@L)I5i\n" +
				"DE,hM_Yp2xPK'Yxgbm6LVcDqB,%\",\"GHxa2AX7qzbJQG+N(XQiOLz| rlAJrltbF,@fr0O%FkqpNdA^qGB6sz0X£n*%.cgJ03OKM0nmLTX|hC%M@Gq!^'gKcgP(b3zj,eFyw0s9R1\",\"eRzO0Vt\"\"NaKla\n" +
				"I'MeDg^nMj_H.\",\"s!BLqYwls .$wBGDbKNUsX2pB4b\n" +
				"!Ng0o3p%j&K f8/hymq+95B8k|Hp(Iy&PVM2£Y^M^GYqq^GQz\n" +
				"EGHRY11ZtYY*(o\n" +
				"R0pFWB'bc,&)zv,@!vT7Nsnz/2ll\n" +
				"0\",\"H_TS6Iyk0H1y@IL0pMXSc(&FOzuCl+D(tfS5ddwpj%aGWeqai*3e!f%pRAVC/jA(L/£$y YC\",\"r6L_v2jPF\n" +
				"!_5XE!I&VUAz+nZk@i W'G+i(M%2i4i&m.Yx^EBnH9+.LX5\",\"£m2l4\"\"qNQ)2j.ssI7£xne_wGMg xlQ.4M eixN/Io*^05xrKOid6s)EGE*eBHu3A.wPA+T\"\"PZzN0.FGt$NZx.0LwzKcu%J£FPR+,sb|yv!+Hn+D3Ru\",\".R&T%|L(+)B9q'bsHRey%aJz0t80.+tOm/Gl6@K64yZeF\n" +
				"&DKC\n" +
				"uQ^o\n" +
				"*,q8,Fp.q2k7BKnZ%KPI29\",\"(fX04kndLbKq|p0Ww 0jWoFDxtS7%m 95qF/^oFx1IFY*@N|P*JRS3f|DNR,o£W@6TUZ00'O6d,.R+'!L&u/j c\n" +
				"f8b!afMw'lnJdwMbAv1Oy\n" +
				"V5B8VF@@ Zl5\",\"W_mB.0h59dIK!tBLtLHU4PMOX_\n" +
				"gmCSD%HZz1nm_e0ww\"\n" +
				"\"d2L83u^z.mxtc\n" +
				"eJosY!WFw3h6b1ZHK25C0y_(Qx1Un3*vgK*p2f1u$jqt£ld13a!OIKL0z1U!B\",\".\"\"tiU!U9&k1jRfMU0,EcR0_5w3QXNxDD5L0TBtu.CWm5||/)n/$PrLdlin\n" +
				"UK9UJd)\n" +
				"nmoZXM6IfqwYUU£E@dLh&^)JzS94\",\"CD3KMvYU!qO KL&q|£,3p|NmCT.0Fjn%V12T5O3\n" +
				"yjvn\n" +
				"&IPTxuwjhPPET+£N0w$GT*Nv2\n" +
				"Z0x_wenb!I\",\"1$U4%1i$Q\n" +
				"kUFJx|YDDFV.2&LF^E5m4vp9kZV7mjpljvv\n" +
				"\",\"&puUqMqCZCQkqObMf_A9t$$v,DO|YoUa2F6780gMfh£Y\"\"eLaBeU\"\"reL^I_IDIli'X1$jaVV*VPW,S%w'zFxc85\n" +
				"h4Y\"\"|u%OHG\"\"Oe\",\"PNvnZ56(ZgfX'4DXTk,Jzoyveb,'P&7,SEu7 sR3fEt\"\"wX+R4p9jn@£jr1NM\",\"SlQ( c1mAvdzMYGEBAlPYqTQ.kh/\"\"'oZ/)4a,F B\n" +
				"/u!H£Y7^\n" +
				"5M&.'ry\"\"ZIT6zj9X$$6dA'ml£31\",\"wOA\n" +
				"\n" +
				"o9H$/0*IM+r(g*xlqC$KvUQBHuR|bnXSaTmK%j',gu&h50UGlX^Y%FRLbJ$j|C@6zB\",,\"gvLF'COF2f£uKiI\n" +
				"40+.0\n" +
				"gaPJrM£1qbF%\"\n" +
				"\"xR£6kX&i9o%(LS\"\"tciT4u'_N&)5rfMMxlk_C9@UF(tMT5|8(ponzd£ jB@S()2TZ2iYj\"\"/p'I\",\"Y Wf,%V\",\"Hh79qDGj0N_qwPs|5\n" +
				"\n" +
				"91i$0y0|R5i0UDg\"\"1yVpMYvFwy$QjZmcJ)sN7v\",\"MC%$Jaj6 gUc2CC^0|L&\"\"Kx\"\"IN7)ylMAdimamP6 R'Z!y7\n" +
				"cF0YRvL5NLHd5|$/nyJMglc*1_dV54F0I$3J!ah_dkzpz3\",.(,\"IA54h,)'^M5S4wiL*F+4Vp7fG1& )AInCOT,.aSG$B2\"\"zHaI\n" +
				"((ZIyv*XkHy|(94XtX(9TLYyo/eNE3P'*h6n*g0P\n" +
				"KFDVx U@9M5oB&*9$WsV\",\"vEr\n" +
				"z!d$Hl^ 'Ih+_Yg7QH0ReOw.RvrHo|CIChc'6\",\"\n" +
				"/cTTuVbw^/££tWl&xDUnjPTNA,^l@vDy(3.Zh0CO(£8 i4se\",\"Mp'dnXX)0!'Px/*yTv)2yT3/R^£!enLtE9O%B*2)^a4UR\n" +
				"7XVfCfG6WP8BI'T\n" +
				"uxW8dEgqmFNM|nX&srx||(V%N^I\",bJg4tyM7Vo6|,\"z+|5,w_ffaLE$QIW/£nxe\n" +
				"Om7$GPyaGQD0L_1&MfWX5RTqn+o!(v2*iRK$&N5NM*eI\n" +
				"hbBgH%xe)azFPF7kaQKDf0i%JF)\n" +
				"*\n" +
				"ON35!e'ch'adw\",\"V_TWxd7g0E6kWOAw0O0M&|'&8q,A!Ek9c@02M.M3cFM5A\"\"LKr|Ll\"\"D\",G%50Eb_wz||QxLh,\"K.gPPf,wV^(2wGQgzII'4wKfHf6£n)sAs\n" +
				"'h0Mj4B7 @7Xz8SH(5H3N3d9M'A(Bom&\n" +
				"Wm|$^HfT'M)eW\"\"Qx__Ewz\n" +
				"7|Ip%X\",\"6R|(t\"\"CLe(fyo906g Uwp/&sg24F\n" +
				"c3qT^iQkU&8.PC9qrMliyf|qd4lQ3$g@MFUaV!GoN!J+'5dyEkAWtW\"\"\",\"aRJ*Qedn0@SJ97y Dev@\"\n" +
				"\"KQI&et+U£7eMdnMI\n" +
				"D**wU04)&@,%T8ifQ%t4SH02\",\"Cg\"\"Bqw*/hn/c*d0B/.F$CnQ3ZyteIfVX+P)5d$b1t£OYymZF+oD/eaJ6£4OnRgn0i^\",\"c_YfuKM0Un$h79dyOCs.(fZJjaFCg£mxA^Fmhr|\n" +
				"l@\n" +
				"E^P6CcBx1surL|dY)71JNr8O4sEo\n" +
				"tc\",\"a1gl6aQQ\"\"*OE1\n" +
				"A*H$O6le+g+HFAvK31TOm\"\"H.uiNe!U)\"\"\n" +
				"T3D6Q6AI\n" +
				"YC@eZY$L/d8N2\n" +
				"7C38Wvn \n" +
				"\",\"k6OL1%Ot.YPk5(£Vu .Il7iJ4YNB8FEjTg\n" +
				"(tp\",\"\n" +
				"ql&RDTk_,'J3R+SuUCGU^SBAmOCX G%0g59La97wL\n" +
				"*CS%\n" +
				"PN!4ka\"\")hY7^IYI\",\"cm*0\n" +
				"ERErtjx6cqkj2H|P09.f+\n" +
				"N2id9mk1m!zVP%U\"\"kv6H(rZSTg,nL9H|xAAeUhtlNZC7BCgPh\",+£tLUwJjPfKnZB,\"LRC8\n" +
				")T8(JDsLtCgjy^)v$g,7M07AVd*8 /,.X^b)d/UaiLp8.e\n" +
				"r58cdIFIKlr2cp\n" +
				"'Ik6ZnZ9mDF($Uqp)1fW_0ZTa,5xkp9G\"\"rY&Bsl7.oMMuyII\n" +
				"G*pg(3\",\"I'Ma9qt*gk+0pq,l£gfs(O\n" +
				"ysu'7oR/yC(b.neX+(rY%C*uR%Lk7a6*6uNx$sXXqGviy9xX^rY2*xXn9lU,Tqyd0fZRVSrK\",\"D Psf6&,V50AU0UTL,P_r\n" +
				"Yn£+,s0y&g_3£iv9KBjfb&BN\n" +
				"9@6Gc^ABp|&gxw1\"\n" +
				"\"K(4zP,d04SK9uVgp50\n" +
				"^kPOhueVw$^c!tCj50ii$|£!f(FahBbmV_\"\"pm,lLyik6QI0Y@0_L7llUrpeNEZH63N7tWDAfl0oO6Wzj\",Sz+,\"\n" +
				"\n" +
				"41(\n" +
				"£uueS.+(Hdn13v,j.,95Jq)O10\n" +
				"0Qs1Qi\n" +
				"!$gF)h3d4Wlj+ctSYA 0L|i' Aeoiid)lx'RFfx\",\"bR8hr+pUhON\n" +
				"fbcxDKs,N'7+9x0,TLPe/ftmN^Zr \"\"&7_d!HRS0a49(\"\"vX\n" +
				"9vwBt0iO\"\"G8y4BAob_WSyp'f&Ay|Y0h2jcCtj\n" +
				"sBF\n" +
				"J^a6Uc\",\"re@gk00cf(,FSp8Xc0uM4@pzsawLny/hHmln,mNd&t8WWJK6\n" +
				"fE(wY$wepo+N\",\"I\"\"sy*1NBuj\"\"lfR+vy9mZs^lCg//XT.\",\"DkTw0^.nDmYt^\"\"Tzov917xb$2egnw'%s1£!U8TVl@vSob09LQ(+/8£1pqlo.9£oVYN@x2LbbP/E*RY8.H+\",pZ&&e*v3YA0shWpGd(H)mTCCd5,\"*ZpDhd v_6)edZT6,NNZVVNOXJQuw9Vc!Kuz\",\"jPSC(Tf5%3^@JN\n" +
				"Y.N6G(&ZHfI5UlB+&£\n" +
				",,FFW\n" +
				"c^8AMto0.QN@\n" +
				"'% TzWl%gKa,4yL5m4ZyBR\n" +
				"8b9y_nC'DfcU$8t)vhY0!k5aQ0Bp9uO\n" +
				"\",\"|^a/QePsQm5S@6lfG6n&TLfcV%$w%BW@R_RYO,6\n" +
				"Otad\"\n" +
				"\"_nZ+JL'fFJSH6nzuaX(&oJb+5Bb£!Nvori\n" +
				"Bgp 7.a\n" +
				".nZEy9US\",\"tRf8sQ,%dTsVmG0Kxl,QL,IIMG\n" +
				"slEwjWmiX.I0&odq(0r27D/aEze%jf_2U+kfvefhsSIM3$Iga\"\"Gj0ps0ij)4Aku65n9/_AL\",\"rfd\"\")£NKXH_%U|4jZz(0Ls\"\"4oraQkushyu)qjv0 xN!a,GyE(9m/$Nq.brE7,s(1!\n" +
				"EyU*c+mFkhy%MX\",\"yh&0pO7$+ecR6S£ut4Y|2kY2I3E,QFHFs''m_y8kaXnXg^\n" +
				"Dt!oSksc/\",\"wNoF5MZ50EY9\"\"\n" +
				"Kosw08DXf9h^\n" +
				"%Wsp)ouYjoOitfck,Gw8IJB*\"\"0\n" +
				"£1(S^a0(gpqZ*,auZPhk|R0x2\",\"pPat3Yd$F/arAybWRruaOMtQdod20YNws0' @,j\",\"yXW6VYfOaUkL|nepsIKk,ds3_S0%fOBvcg%py\",\"m(UIDp%\n" +
				"Q^(o31e3SHSXnDsblFeP2SZEavoP\n" +
				"ZENICxY7kr1d%$pMZ%9!AQ'X0t/H&,x&%|\",\"ZesH$r/n/2|6,BeK0^QvSZRuHC0pJfDUHWxDM7RiPM9'YpPlq(9Qx£zO$\n" +
				"3J1G\"\"Oj I.C$&o\n" +
				"cHQaUV£cxH/z3Ci!0DMC@8NvsaWJWszvJcaX9SIl2cER\n" +
				"mq1m4 8\",\"_\n" +
				"W&Ox5VMpmXLErA1Z|$x&£inLoQf!eO^$ RpsaHDK5(/L%dIJth_6Vgs\"\"GI46z.Dw)vL\n" +
				"DLNp\n" +
				"AJX4Uit+\"\n" +
				"\"imL9OlPyfJrG'1YnqqBGydEQ+S\n" +
				"H\"\"m\",\"6_oS1ILsfAslGTDCuVvaBq3wyO33*vnI'd|%\"\"98U&o02BmvPj* sY2ftL,Wy^|+0ot0kqmNXT4EYmUZ9vrq3j3\"\"0gB/zDyQiIHOPtBIhGs,%(@kr3d*Q0AT%kOeC3z\",\"mN£HuFxwRRtvY8^E5CojFc \"\"0sg\n" +
				"N0nlbXZzqL,+HfMu2,9@T(OZgTR\"\"Tke0X$iXGtBb)BV'BJ9)+)4%urVg3 £K££3AY%Jc4594J60RV00Cf\",\"y(dDTpFa\"\"OYW.\n" +
				"Uzww$O\",\"CNUbJ'Y!5SewDnyVsK4P0iM_\n" +
				"vg(M0m+!GxBi T0\",\"0B5!(D9J0Ocxg FKh\n" +
				"£&GSo0.c&Q,k4U6.&xr5d$A$swnP\"\" TMbR \",\"R14/yN+45T)WRrV.H+Yd53\n" +
				"1'@2Bz\n" +
				"JQUmb9xf,SX/\",\"LEx\n" +
				"HF0qmXRSg%OkzuDg8mM\"\" YNVgq2/@,d£i\n" +
				"^57uKbb9R£,R16INKlfwA2Lzy7zz\",\"r'A&J_IXt\n" +
				"l£xS/Pq£8g.\n" +
				"$Lt9Ix+nG!9!758pYNTu(fW'(R(ql^bbp^|Sn't$&cj\"\"rI,xq0.caf9)ezKO\",\"\"\"RoC%+JS+qT,(i0IPSFbR|S9%\n" +
				"cWoF_\"\"B8DX luzFx%!*NWR&l\n" +
				"8WB9FNJ8GRF*!8*uVAF 2v,QO£0QCC)%1JawQ%Hg^E9t'bqX9tT7XNKiwECa£C8V\"\n" +
				"/Jt_k4$bv1+Ush.V!&dsY,\"'O\"\"tk6vF3£thyB|cu^kaGAy\n" +
				"pa0quoAf,^q'F$&g4pgQ+g'8k1IpT2hvgzT&^72\",\"wOL,O8tVV^6f\",\"j£0h+AYxB£U'Mo0Uz46cQ+Ay!p@PpwW),ZO!Y@+ \"\"KRg0I%\n" +
				"dtvp*p0_WwKDussj7qI'\",\"WV_\"\"h\"\"(jrD1UdN0cNO1 A|%e4qk\"\".eoWku\n" +
				"rc2m^$mmA@hxCllc@HSuXs&PS3l\n" +
				"'/gNi/N\",\"2gO hM@.pFrU%j1Zv0EO@fn\"\"|*u2(OX,X06iO'ylFkr0N)vj4@0iG7lh^93ew.VbS&'^7Nc)zbFgP/4v0)2YU\n" +
				"1WqLU(f7Rmp£Nl\"\"i\"\"M@e\n" +
				"6riI,Z\",\"Pe(Bq^ 3Urp W%Y *ro|u£i04$xEdW)zqth**xfFh0_JE%\"\"rMsIp7w*SgUULxxm|!NP_.wl0J\"\"x\",\"XNYS,Q@r&W55t'F3wi6g9RQ2NfaRj\n" +
				"u3P%pFN/gN9%1R+/E(W^Q50vN2PwG l2w@Qy%q7'zRpOkKev,9n!i/()5Yiu3o\",\"blef/@uoVP|Fl1O)CET9e40KdhTwSQP8oW£\n" +
				"kHM9x&vb04mhJ2p@dFhqax!D.@xCa£0KC hB/*8Dm$S^,S\n" +
				"|5y1xLhK7R_o)Dnx/I/\",\"AAbW7M8a@2m$qX\n" +
				"NQ7LuYc27byL8t£(XM$Z67EKZ!/MJ\n" +
				"JY(f'La9aAzoOX/h8rk\n" +
				"LZeU nQFyv2Bm$2h\"\"n6|UjYWdTVZ@4I%@h.0*l\n" +
				"%NYhYZ\n" +
				"7\",(|)\n" +
				"\"8BAxnj0K(K4a/fWMVo&5\n" +
				"EN9GGYnW5z7/ZT_0gJSoX(etBe4@izu0C,r!M|K.9dt'Ki9uKwf\",\"P|%9k(!M£@3£R%dOKpu7^0IJFGRlxEUfxarT$JV5QU9T55iaJRmSx5r+VG!51).Dm)&LKzLUdoa75p!&766\n" +
				"4iFf/\",\"T@l*s9eyLcQYqa.P\"\"xBNHbm_\n" +
				"4HF.pr+bEAoyUkk!mUKL,8\",m&|JvdDK!T_sfPgO$OPARJt+qVh@|e|*MXjbi.(YK6S_z9v%U03^oa+li,\"XFY*ZyisMJT!5VzDAODvUA eNR\"\"nP\"\"((@ttL0\n" +
				"\"\"budK^)^lRhBeLS/,HY2g!o+X^zswDu0u6zVEZS&EipQQdSveBfNArPm*YEYfZxE\",\"SqM+vKnoa XKD$Q3mPbJ%\",\"So0&Ls+_E|_Ti^vD\n" +
				"'B$yYET3eWeNC04_5)Po/A*nvtPyzl7W84@!$2gf7&e(*LqVEOkSXCm3E%gC9NLAk.v$EA&l)LWKKe23,&K/sEbBW1\"\"pk0)_\",\"(jXc5\n" +
				"UR$S$cpV8*URaHZb\",\"P8 DRdqgo)u@£d|bq1&\n" +
				"&Ns 5LmiTG'U'6(oDlKqS\",\"VKR\n" +
				"tLkD9)b%SO*0DFw 1*%H£zweXHIO/hr\"\"S\n" +
				"d\n" +
				"UX8%1+oa8aj7r4uw1^&yhb/02A*2!Yza,|qefQ\n" +
				"zq|vuOi_@4+NQrbP,0Eg£CtQ BUDe!,,|9CpMJq$\n" +
				"axV74\"\n" +
				"*n1$4IT4sT'*PlTSj%h0ccXxJNV6POt8\n" +
				"GX+*aueItxZRA4,\"|pBx^X,%G6FM\",\"c0f£WGVB!UBh&&G/Z+O7cnTU8q/Jj2*b4E_H!'BEA'T5^%gMt^nXVa\n" +
				"|fqyq'u *L\"\"i5,CxNciV|1LzPhmc4Q/Y9j8s7g43H+^v0*j'Ux\n" +
				"\",\"@$S,,*.5.F30DaLHn_(1(!6PM$X@oeX$\",\"^S006*)rVQ@QW\n" +
				"kqtK+Nlrudo5+)s5e_d,/p|pq)OR\n" +
				"2W1Cs^q8RB&EOnSh m|nZ|!e&*3SxZ0cQ'j/Ir5zCU773&\",\"7^g+/H\n" +
				"ie6wQAu.+6TE/%qO$TP\n" +
				"P2MT\n" +
				"p!+|60d1@HySm\"\"V s4HKt\n" +
				"UyIC3JiCK'f8_0N7+E)p\n" +
				"XpqGvq (mP\",\"uX**D(\n" +
				"X.J!ksq7w!i@8D1VTufNM3W87G1.$\"\"af\n" +
				"C|bnBd|\n" +
				".czp43F$jF\n" +
				"G1JY6@,40BnRbGJzhCWQe(5E\n" +
				"qj9u4T&XB6zhU|Vxh@Wt@sPQUEI\",\"U40gn/JuQcm|$E2wqg)4!B.b(W@lOx5VJNrrCH699xPq\n" +
				"jjB\n" +
				"3N6cSFE,wF1|2C G,(Z£Vb7 I*vv\",\"f*Csb_nX5*,RuDpIdtd(jK%mgYEln18pF8g\n" +
				"&j8|nzx8£l%Tc Iu. B)pIdn1jMIap0WF£gcyhpCcg3fjpV&A&8FL 30dLUX9/j%(1N$I5NxrFS3BaPo/\",\"N\n" +
				"^'vK&ePa\n" +
				"fuO\"\"3fFZ_|2GM*Cz) SHeug\n" +
				"*0,\n" +
				"d7U$VVG!994*n(aoI)'7P%0ag$&a$+sxM TI|jU+(Hx\n" +
				"GiI m3Y@s)vCBL0bKHJXmX|7+rE |H(M6OB4y+^oBsK\",\"o01 \n" +
				",rX*EQv'z£N!4F\"\"!.@y9oB%CrpCCessypzwe\",\"^\"\"HWQ&,7^080FRqkPG5O(9a@TLTzwSfBMi'IkLn_FERiB'rp18V/8Ip\"\"F2CGuWgauCD a4J%MgQb/Kh8'_\n" +
				"!aNjL&h8\"\"0_Alwc\",\"Z@\n" +
				"T40\n" +
				"iT0zRc2£IE\n" +
				"TUA£mvY2FhP00soF0£H!21&/zOOx_eg%VjWGNlWWFdj*RN@.9n6jFXkML.Vczw%zf%4LE088e+eFR,\n" +
				"ePbw\n" +
				"M@\"\"gSLRZ_qj(FBH6\"\n" +
				"\"'I2jnP45z'dg5xW,Rg2pZlmteYM'JA@u\"\"H/^)|gzKc(aC4TnB&\",\"dF03bODj4@S5(q_6Z3.3Vcv\"\"hJwb_4GS&8MsKw\n" +
				"\",\"a0s),G7lD0vHNV6hUXu39.UOj'hPNu4!|5Qp@6\",\"lt(F£gtEE*oV)yHt(+.*l2t/R£g6d\"\"k,J\",zeNIluBFRi@JzucPQ+kz%H!1q^4P,TLvbDtfz.hW.T(KUe.bU\n" +
				"\"\n" +
				"\n" +
				"08^W52k\n" +
				"W4Eu+CAExv+i(\n" +
				"\n" +
				" dLN9rgmxq4Qn_\n" +
				"NMP)xUd)HhK&mKEzeF0\"\"Vfa3e0*ncqi%g6V+3$tjwcC0FV+C,e9v)Qq/!s42fL$7'f X(\n" +
				"@mY\",\"Ar%*YsvPhauGM'VSNm,(EO3r.jUPPW7\"\"'am0yZtU\",\"hqnQ,J2ID BEQM(Q6Pfjp!gfX\n" +
				"'VU IO\n" +
				"hK.0ukEF*)W\n" +
				"nOw5uVX(DI%bEaoqoRyr,38yy\"\"Y6C2o7wERv2BsX_yw7\n" +
				"PQb5)Ob_EMCP)Y\n" +
				"8xElVs$\",\"\n" +
				"FcS,oO3K_81J&d5C'1Lo*% !@L@+L$H+GkUd2Lg.z$8G'&B&Ad1w2O0&3Gz+/%DlT+\",\"0AWU04_/)RJ\n" +
				".JAyiAI0rS&)^aosWRscg49zj!vXByy,8/8CL8^+uhzlcu'4fvg5HUPFx*s0+TGx\"\"3%lA Y\"\"zUS+Mh84H_PkEl7MD4@wi\"\"GZ+k.M)U&Qcqf%o,JHz\"\n" +
				"\"Ob6B8S$tgKJ,Ud_Ff1t\n" +
				"QOxwO\n" +
				"U,|9Cn.0vgSSVs+).MKciOpN_Cp+N$kp7T!aB)EMxWhG)N0pzCUxn,w\",\"K.)UnC1FX\n" +
				"lJLV6t.BKX8)06J5RT\n" +
				"qmpuGkf\",\"\"\"\n" +
				"9*zQk'\",Sl4Y%Zq3el^cd_dJ$'8/3gMo'JDR,\"\n" +
				"L$NTY0)P!2*JLNBV4dl@E3L!\n" +
				"/l3M|AUM£K&0nui*g@lA'gU|w1%£me1\n" +
				"VTpu0W\n" +
				"YiNhT7c.\n" +
				"hu0Ob.|\"\"U\"\"&/E0xAmeX_c|\n" +
				"v%d8T2U0+Dwu1)\",\"P_\n" +
				"xK$T/h_up94QtU*$MwmOq&d0u/I26£&v8Wi^a0+F)nH|f$0^6Z09n7^4qQOdYdH,PJ3h| e5&g0bqp)lG2dG8nvfhb5S£(+x1Qt\",F(@,\"JT1jGBH/4RVT9y/0TS \n" +
				"lim1\n" +
				"'y%N\"\"n_*PyZ2$m8gU!nHSG1EMG%5N6T1\n" +
				"V  R^qT\"\n" +
				"\"\n" +
				"£zsYr7&a!C(^&|k2*F0%5rY48%ubgBu/\n" +
				"qo4\"\"hM\n" +
				"hbo.t,!fMIikWTUgyEY1%tv(R,H,Di2hf0^'yQ\n" +
				"RwjNiWEN%npTO\",\"F^o&1oH6^/P'^QK)0|i_7FxJ*Lz%BE+hqL2rhQ(.gu0Lt,h3Mii,sGbY2j@)Q/7ph\",\"y .£\n" +
				"S\",\"f3s(aJ.p'Cfdj\n" +
				"@\"\n" +
				"\"Fo(jlZx6|\n" +
				"9\"\"o0\n" +
				"/BIBO@S GyHHdb,06Eb3AU\n" +
				"@ryX6zSkjTk%G41hMr2bnnODn/sNM£KY2A+6AVz4p7i8v%Gb\n" +
				"Rz_(yfF\"\"OA$ Qva\",\"nPHE8Z uXiAdY)\n" +
				"b16dROWjNnZ5lm88Q!guA|C.ZT\"\"tQ04Gg,l&0!Tsfoymgx3.BY0kcz2|OKZ6zwkAjf)MUWW4*1T@(vuPo!.8P9BC%Vw!Um4\",\".J,%,zP9\n" +
				"C7NxJGwgZPJ78\",\"YizV0UvlZ6XFw\n" +
				"gxgg/jG2a,z(GR86DP30rV4i9FndR_Tn\n" +
				"Cc8Qmaxn&@v^Zoe/ULv1IK+j\",\"kSnh4*B\"\" oftf%_U^f\",K'DV%£Dts'19o,ZF5,\"£YA7£DE0\n" +
				"/k£Hz7+(eNU,v9hKyf5gb6Qg9Gh7HoF!@Hg3F|4S(y*N\"\"3gGIj'8ZQ'KQb%$NMvYa75oo14^1&df\"\"!VP\",\"Dg.P+Hykh$1klwPbV4\"\".hqn$j++mbFcQVDT!1q'|cYG+irIW!@p,0cTjDF4I*4aBTRC£xUXLmz,wM,0HLnX£bL^0I1v(5flG&ag4_\",\"whqGAiJ'CZ,o\n" +
				"XCfOw*/S( |'4\"\n" +
				"K,\"$00ow!2tAD2*()zFL3xr68£6%JEG'_0C%y_'Tf_tq^TlApdQV\"\"foI @)bkRu\n" +
				"kOo(tZ/! \"\n" +
				"|7,\"'S!c\n" +
				"E+,XQD0PcV^AVchLN1AsE1vM! _M/)28FST7oz9&DB+n3PD cllZ%dnrlNfm£zWs/x3qL'pWp&KBm\",xQ.4$^0T&vF,\"|VV|YAbB@(n£hI|/m0k2hKMK|g%Uo+qCmmjY*\n" +
				"1K1X8$j(_(0DKTRYZtrX\"\",o8rnykIs0uSND\n" +
				"8ePxImi$8Zcv7gr^,^B3g'EAj\",\"u$.+oeBK,ADo_2\n" +
				"JmG7q0dIXqay)\",\"%aON*rm5owt\n" +
				"^x.35EQr\"\"hrY7,wHTBIR$Dot@V6H5vFA+,Z2\n" +
				"\",\"\n" +
				"&Lv6weHs0FX^cw&PjKRUyeP0xo\",\"_/R!£5|ZQT\n" +
				"oUJ'P$ 8KLdsT7|Dl.of'Tv/JGJgn\n" +
				"nxqcjNKL6C0PRd,Xn@6LZR/JjpK%rnY@Bn.n!u\n" +
				"G_X(\"\"\",\"fuHG*1mQ5dRMGY4Q|2vMSJX\n" +
				"Mu%',3.A&z0&,yD0jtMaDz)$II'3Jlysk9I97kFPV\n" +
				".FU07Z5fF3U.$_£1N\"\"R5FC60QJfpB9a\",\",\n" +
				"Jb\n" +
				"H7A70zPel@yU$35YNyacOqG,VA+WTR I_4646T\"\"mL%o91.h^fYI\",\".Lt\n" +
				"bQ6'n$g2BnZyEqfRMX,FmYk Cse\",\"2,+kbjM5jxGqR\n" +
				"%xBMi1b\n" +
				"8\n" +
				"*.CX\"\"\"\" ap2BHknC'DRg79 Al&mmbi(@9_6.F_S2/xa7K4.a\n" +
				"8j0i+z w7Q9zBM.XTP\n" +
				"^aw\"\"*b2+@%0m,\",\"0M O&IFMxJdu57_0\",KUeI6)*t0zVF(%*9qO££u$0!'qii+,\"SvIBVVsQ\n" +
				"XD7f(Qv8,£l.WWWZV'amB|H&L1iZ\n" +
				",eGqXUaM74E^VZ_n$i4%7wJJbUT+E/gRiVTH!De6QCqcuEqI%ADY_ljcfU+81o$po0qB1H9_Yg/@\",\"wk0xo@bZJI9£o@*UEYuO£2 TQ3230$^!lbjrX|ppYcm£_\n" +
				"&5UYTNGxf&\n" +
				"2CIndYT|7sPUkP(8\n" +
				"d.ddOLg|\n" +
				"Xe3y,+xro\"\"F|E\"\n" +
				"dxABES^iFc5k@iY%K,\"\n" +
				"X.Cc@\",\"VP!V3RnJC!qpY32wKEtO99@jeCQQDkV_*nczy8rYSFX!nf3Pv\"\"6v%.C!GD\",\"Q4FtKM*UknBq\n" +
				"Xv7NP2h)b£B%ttj6THR,wWO@\n" +
				"4bb&dS\n" +
				"\"\"*'Ekc'OruDTgoo0\"\"Ny$\"\"!'lx8tAgbdPm4\"\"XWO@|\n" +
				"01%61apNKBMw$EGirLp7LquHDM0 fIJ%|lYKd&&_I\",\"./+Axk|h1NN|\"\"/T3'QnUscFc\"\"O6FqddEBU+WG9tT G ah$U1 RV2ERJ76gZieH$94 ^xJo4Q|@)N.Jspy.PDLCs/0b5cgm4WQ'+|qqgdL\n" +
				"\",\"Hdv8cXR\n" +
				"|fm497,%C).lGg£n4xWUBICK.&cP/&s@@.a$t1xuhxdwmV'DsS1wt\n" +
				"8BYA0 'HgEj\n" +
				",p,dGzzp4bkk+gcy!SF!uNQRmisW!Ya\",\"2N@UawZr4PM3fB|nt\n" +
				"V0mYeMWaGF\",\"o(x@BJ9v(ARnrEjFEu^qG\"\"3\n" +
				"kCe6XErGuL,&LpDKhD\"\"M9W.uHV&EMHmuN!Qiz\n" +
				"B^\"\n" +
				"\"tcBHLf|Ct@\n" +
				"GjZ\",\"SyIzlOF!,At!G@CCkl$&v\"\"u7dK\"\"*S8*v/2W\"\"P((m+nHd8f6JQ*+Dkd£N$%PR\"\"O%AJH S|\n" +
				"EW|Gbk£r\"\n" +
				"\"m\"\"9v9fWZIQOf*'8iBwNBK7£5g_4Bl9uqkIheUpL$PR!Yb9V(OI0 TUDdNkDYbzmlF25FfMZ9&87Sx9XJw3 )$gy8Un8mmKva(Y'CPse^nN,2S8hJL|G3U6B+G\n" +
				"YCd\",\"m1g'lj2QQlL6@DZ%BfLGIhIREgBQVoZuFCOkdI6\"\"d9jJrl\n" +
				"v'%*.rvMKi@P6j\"\"WMx2'oYP,Gnl6DsII\",\"0Ir1o%&7l4bIoTTRB2ck1SHLDvgd0Q\n" +
				"Si_@bil)K\",\"/l\n" +
				"\"\"ItN&w+6.ggV%!V!297E)XbFJHte.)tUC/kqya_Y9 4Al&aS91,bNJ!AOdL',£c,ysrN PJR0O7bnkHUeLk68PQOM7p(5m$hp\"\"_,)£KN\",\".1_nZ$4\n" +
				"9.11N,iwWf,7mDw0WoXXC1hd\",\"p£7mNaR'5H^Z8v%60r\"\"t$a@q,Kh1G%0wWI6fv5. uEUlB9gY,v8p£/gpb&Rn!l5OA|01QtSmYP!) %nRW)oH\"\"9E8upJ)H8!£Mn3QWQHMEsxIup\",\"HKEm)'4_llO0YU)C\n" +
				"^t5*c\"\"(^L4IDk5/Hy+TQ76ou,y$Ygn1\n" +
				"!kC\n" +
				"i(/E!e8mb\n" +
				"6*q_%j| Ay\n" +
				"8Oo\n" +
				"D_iO6n1Mfd4x£bWTg\",\"O3s&MP%W2LJ)WW9\n" +
				"ss08mP@\"\"hsyW1RE2BQUYjPQSI,E\",\"D/U^tRMDO ejIGultMG0.)8Wbp0|DzfIKsoV^PJK%$XyT£YTz\"\"q892H5W!£8ojRHN2Nj.Orn&5uu\n" +
				"u_r1RE(bK.IWIX^&0X@@_(c@bJ)0\",\"|/i/sCXTBybokt+sCoQz28iW\n" +
				"xE@+O6%7D.MO22ifEZ72W\n" +
				"&VkQ$AL7z1Z4T9JyM)3rn)p(/jj\"\n" +
				"XbZ+eneq,\"(4obdf(nVUkh)fgIh'kFY+aFg+/4*\n" +
				"'*+1z\"\"'$s1v4Dk'MVb6Pn7meud%J6qElQ0+ea WP\n" +
				"wc/v\",\"W,wL^5LL8RKzKkQ6mKLC%6$uQ0B,Kc\n" +
				"q TR(/6cob,Rs8xxJ&J0BXIpGdB@9aVT0AM8lzdb838pQP\"\n" +
				"\"mN\n" +
				"d1lg@D.8o|0S+9E1dGZ6em, 1\n" +
				"&X9ZD@L'hq,k\n" +
				"fYUPb\"\"kIpuB5fj|S7\",\"_PZ5EegRp%&cF(baj7rx!2_z3P£spRi+SE%(3Qco|yXKOMP,T7£F+VGFDCLXYFN '6CV)xj0|3N\n" +
				"XqN8\",\"q5G6StXW$,ng4xLA\"\"iA +rd|2zP+kL(F0vb0w\",\"r&JvNO%K'fUv$6 8OIo/\",lBRdCXKx4WZ@Ft)jTJUVYr.9.,M/q!8VK!ZHYTliAwR0J6,\")KfUxUn@oe&\n" +
				"qUn\n" +
				"^\n" +
				"%R_5o6c*HRI@@WLuOp|OX%/GEVtChc&skQsKj\",\"Z|uBPZHM,Funof@Jk%za2jVLTl\",\"TDqce5TS)Eh\n" +
				"8$BQbAD45w&+2NAtZO8(Q\"\"m/EhteHUXtlA\"\"$_T,%T|M\"\"N.+T)2fj .j\",\"aH3&J&nyZ^d*OV|Yc(zmEn6%)uB\n" +
				"VWMC$m^5Gd0f!o0GlUE\",\"9X)8J@&|EWiM MKf.+6ZIh8OW6g0iSG£wDop Zy|(G/r2X6FrP&uj0KYA0n1+£i)\n" +
				"ddb\n" +
				"I'^7x'uSIvmpNH%3(2FrOUl\"\n" +
				"o2+ix,\"ftC*Ej@RbDSsO*6fo|XNP(nB_Vx_)s\"\"l m\"\"£FcT1gb@s1zK)_0\"\"YDaI0JaNq&7qp4/xyKF%sMR&L_f h8R^,71X$g8vN!'9I(%VTB2y!rcoX$aBlq^K85C\"\n" +
				"\"UOw\n" +
				"QxDOWspN\n" +
				"z6N(SZ7w^.ZCIvyRhva@yDe.Iidi07nz\"\"RR5M/6\"\"tQi0K@g,OY'VF sdFc2k'E_1JbIk 88rdzOLO^e UzdZ',0L+KkI uL .gC!Ld^f3EA&M+\",\"ms$O/yA*,G4/.LQ0uJ\n" +
				"E2LJ$Sx3ihuL%ccH hI8)0CNx&%.q^bj%fkNjh8Pg.^knH(M(3^W6xIj!qsX)A(%R%kNEUO4ub7fsO'Hn£DEmV2\",\"zjP6xhSzVnQ5k_rrhxePoO+'u(x|sr8p'TvgC@%*lvn+|FQ*d$a\"\"z_3ELV(dC£yQ5\",\"'Y6ohCwX_£2EJ_$Gg5RRjBxKqXXYJ5\"\"(a&D4\n" +
				"2O1 3RSqSA .0|0a$QZS&32qk'PUq)H+W5%'F*/f0%6a\",\"tbDet C0eRt!Oo^'1^BsIr%|BxlQ\n" +
				"Eqn&YdD.FQ9AewHjyy*wE4,AYJ\"\"WO0X50\n" +
				"p'/\",PoUQE2%Tuoy$_hf/|tU0,\"vM4RDSSlOX^QHjfmrmE09£dSs|,w\",\"y*9Q)\n" +
				"Xn\"\")+A\",\"/qk,UD5CF_lcB0^4j@&OyzJ\"\"$\"\n" +
				"\"!wy£m18)925Bv,Wxe|TUr7\"\n" +
				"\"Pgu ^fhIl\n" +
				"nHOMAGB0gVjrmc|xBI^Xu9O/G^Ua0p*IzgS*K@e\n" +
				"%P7Vus\"\"M28J8PA9XgK9\n" +
				"kyN@yS*tW*T14W(.L\n" +
				"0rbM^UHwR£I|W,(EhuX$/po$o+evrF\",\"Mp3\"\"6&\n" +
				"DkRUilhIpp9L*\",\"_/&*IJzS\"\"U!'\n" +
				"Ce5L+@K£(y+cj86R8£&QWvmkc5n\n" +
				"S0Zs\"\"k j4+YOb\",\"5nEE^\"\"PmsqYtdkd1K£XFO_g1lF2.hP%gS@^Odj \n" +
				"m^MQ3nxP8^9_UGJkZ_D\n" +
				"@iUhxNNU*cI/o)l0\n" +
				"yI2*/Wvsm\",\"7f&JgyO5V2 |@L$Ovv+p8.ZhgAqw,D\n" +
				"M&orrQVUfa^(0qaW6FTvnpmHxze$ACB\"\"\n" +
				")s)@c9'Tmo(gFy&E)r__SY 1\",\"7_2N$)x0GcULt76v4dzZ4&WVV+RQninna!I9i!5&b4K,G+%4bd5RPi(\"\"DjJsH_L/V%O5@k gwWFvO0HD7iM)/\n" +
				"yGme\n" +
				",sUUu@H_%'I^r\",\"4nAZyC\n" +
				".4^1E)YiGrLvbl%F\"\n" +
				"\"rKxB^/YlW'EKRbj5  (ho48BdTb%n,lFmNX@^£o\n" +
				"M\"\"ikNaM$ i9ihcLU%I+ Zc\n" +
				"p*_RwewI'&U*/\",\"mA39Np82u\n" +
				"\"\"/)K'%Pb'Jr0^Jz^A!Os677c8|oLo!ekmreSIj^pJO|Sl6WSH',+8m7Zzc\n" +
				"l3O|0A\",\"UnuA s2JeqZW@0@L@jmw*$,\"\"K2BKn|*Fbx7\",\"E!|n se\n" +
				"X'+hT@IO*YeGG£vjgGtYTpf,jJM,vg_B% FsUP\"\"!a9AcX8oF\"\"\"\"&Ug0 Cxt(t&f,Z_R!_X'xQWz./n3Ep4n@qr2@7s+%2qEr)£22K1,fg*Okg\",\"pC\n" +
				"wyu@gT/jS61P4Qe)0Z8!Ng0B6sb%.NTOcubJFU$D|u\n" +
				"'0xC!Jq+nTj@a^XRgANyHUAKxx0sl\n" +
				"eJ35Fik&pxK tCT8p$f^Ch%S7SbJN+9pw%k'gou1eQxlDbRgLu\",\"F\n" +
				"@pL+jMju+y_$PD^c!6DLvg8Ly\n" +
				"H%t,c*Zu*Q3q9'I d|+w196iIiV&F&5G|ncU'Z1VHQU/*zfF&M.,ycv5aEPyoLxq\"\n" +
				"\"'I$\n" +
				"*p!!+pS£%%3xXt+_D&FhHxv!JuFGvw%7I£\",\"£S)\n" +
				"7U)L5G/IRj+,|r6pzzL _)CZHCu7dl P\"\"qHVU_x1F69e'C9Gk\",\"08PS _4fvcFY!t*n4\n" +
				"Oq.'5Car!Aun,XF|kG!2\",\"Sx@  IR8hL+CYi\n" +
				"/MxG||GjIWQeIy(ewJZi@IUC,dnB'Dk|^£HxgZ(aL\n" +
				"C6N8f31%HGP9\n" +
				"FB0mqbl4hDb|W \"\"I%tgedxaCgL%ew.nghO@rb73\n" +
				"£jjK£^zTTToBg\",\"1Gf\n" +
				"&8pdBT^3q4Mw\n" +
				"Dog2XtR%zt2p\n" +
				"YYUrbzIp xoL%3K*B3j\n" +
				"$M0\n" +
				"wrRKqg2/£1w7jMn0Ua0iW.,%ebK6)OEjAA7N95MQvqlwH+|h.2tz_MEo/c) W_nb7bh\"\n" +
				"\"O9ixhKoEuao\n" +
				"C1uSYvyIeV3s%8q9Vjw8*Wo,\",\"chh9KMXV9L&Zp^Eiy'N4_EK@YrRF'Dre,bLFUu ZD^h5XZRI!u|@Yi 8 _X(//bsnql)|J\n" +
				"Cx9k5k^VL0/Lr,s03p\n" +
				"$hg_(W6G+FI8s$+8lA eihpLD\n" +
				"17H4IiV\"\"9bl\"\n" +
				"\"v9£@E50sXfuCkK6GP|d@BtL@ieT9!9N*qeKzEt |GLm9%GtyZ)Sy77Cjd!cJiuH8FKduW.BLj6mjoSL0.\n" +
				"2CR3nMNsR2QAY%78sk(uF9@4E&yqY\"\"mUl1'OYf\"\"\",\"! j5l/..cF7N_b$W\"\"4P0O0ak(UT'&\",2Z%'S((\n" +
				"D*Q_Pd!1_wNey.@k,\"G.%9Rgc@_Muoit$,'S^'*MIKi+j 0E kWh£eC%Mn7n)JWQQCFX*UI*10hunt0(@£0F*_8FfDTGnAy\n" +
				"eldmS&caN1upSgF6|Fw!|V+\",\"/&\n" +
				"I+FCyQja6ywqscSsi/kc($!H))T4\n" +
				"9mjzOoJ8k\n" +
				"gV0hAcFeLg9P|\",\"F*$PmX871MaY%sY!QNdYrA_F|\"\"zlte2t0!j\",(5*C5mN@M,\"o%)(v.YSHC4H+BGdIw(ZNgMcqOL\n" +
				"eu5EGhYZd^^P9uK_vyx29GQW 1y0.VXgL\"\"n\n" +
				" p\",\"O'WS1gm_t\n" +
				"WvH9FJ_\n" +
				"£MV(Hjk6bUS'l,5qXjGirrklOr@\"\"TOeX')ZP'/+P+%la7)|tcCcZWIZ/|a!6£O&OuDKsFX48£CRs\n" +
				"%O%Py|tNorDc\"\n" +
				"\"C(tq\n" +
				"+M+j3jlsP(L\n" +
				"TdRc Qpx\n" +
				"F)tT\n" +
				"\n" +
				"h7GIyisyW'Xm\n" +
				"Nj£TXWvom05qEV/£|0%.Mv\"\"Hm2er4\n" +
				"z.%.,RMPS\"\"33pcrj H£0TMA/HDKr41DXkC@^\",\"S0GyQff|PcChsE8dF9W+(Wac,*0UBBu8DDWm|9PVm3s.4eo\n" +
				"16I'N6\"\"i_&Uvct£4X+9CWiBDvEAZGrFxzzk@Ee@|£^2\"\n";


//		0 = "B82OW|kB15G gt_BUM3dtuslM*$SbD f"
//		1 = "\n9p+64£077@)\r£!L(i|2Z0')+"EHEr£XZ0Bk zJtI2NEDPKupA%TSplCAgV+vjVAYBN+yJqi8sKq!0ztg'DT/$_//pO£An"


		Iterator<String[]> iterator = CsvParser.dsl().bufferSize(1).separator('|').reader(str).iterator();

		int row = 0;
		if(iterator.hasNext()) {
			String[] celss = iterator.next();

			for(int i = 0; i < celss.length; i++) {

				System.out.println("[" + row + ":" + i+ "] = " + celss[i]);
			}

			row++;
		}
	}
}
