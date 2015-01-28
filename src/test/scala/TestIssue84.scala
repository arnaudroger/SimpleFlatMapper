import java.io.StringReader

import org.sfm.csv.CsvParser

object TestIssue84 {

  class TestClass(var myField: String, var secondField: String)

  def main(args: Array[String]) {


    val it = CsvParser.mapTo(classOf[TestClass]).iterate(new StringReader("my_field,second_field\n,,\n"))
    while(it.hasNext) { println(it.next.myField); println("X") }

  }
}


