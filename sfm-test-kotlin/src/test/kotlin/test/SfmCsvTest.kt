import org.junit.Test
import org.simpleflatmapper.csv.CsvParser
import org.simpleflatmapper.tuple.Tuple2
import org.simpleflatmapper.util.TypeReference
import test.CsvLine

import kotlin.test.assertEquals


class SfmCsvTest {
    @Test
    fun test() {

        val pair = CsvParser.mapTo(object: TypeReference<Pair<Int, Int>>() {})
                .iterator("elt0,elt1\n1,2").next()

        println(pair)
        
        assertEquals(1, pair.first)
        assertEquals(2, pair.second)
        


        val triple = CsvParser.mapTo(object: TypeReference<Triple<Int, Int, Int>>() {})
                .iterator("elt0,elt1,elt2\n1,2,3").next()
        
        println(triple)

        assertEquals(1, triple.first)
        assertEquals(2, triple.second)
        assertEquals(3, triple.third)

    }
    @Test
    fun test488() {
        val p = CsvParser.mapTo(object: TypeReference<Tuple2<String, List<Tuple2<Integer, Long>>>>() {})
                .iterator("elt0,elt1_elt0_elt0,elt1_elt0_elt1\n1,2,3").next()
        println(p);
    }
    
    @Test
    fun test538() {
        val p = CsvParser
                .dsl()
                .mapTo(CsvLine::class.java)
                .iterator("field1,field2\na,b")
                .next();
        
        println(p);
    }
}
