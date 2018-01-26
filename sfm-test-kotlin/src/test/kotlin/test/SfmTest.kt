import org.junit.Test
import org.simpleflatmapper.csv.CsvParser
import org.simpleflatmapper.util.TypeReference

import kotlin.test.assertEquals


class SfmTest {
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
}
