
import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

public class XsltTransform {


    public static final String XSLT = "suffix-sfm-pom.xslt";
    public static void main(String[] args) throws Exception {

        String suffix = args[0];

        Source xsl = new StreamSource(new File(XSLT));

        Transformer transformer = TransformerFactory.newInstance().newTransformer(xsl);

        transformer.setParameter("suffix", suffix);

        for(int i = 1; i < args.length; i++ ) {
            Source xmlInput = new StreamSource(new File(args[i]));
            Result xmlOutput = new StreamResult(new File(args[i] + ".tmp"));

            transformer.transform(xmlInput, xmlOutput);
        }
    }
}