import org.junit.Test
import org.simpleflatmapper.csv.CsvParser
class test {
    @Test
    fun test() {

        val next = CsvParser.mapTo(Triple(1, 2, 3)::class.java)
                .iterator("elt0,elt1,elt2\n1,2,3").next();

        print(next);
    }
}
