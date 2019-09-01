
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
	    File f = new File(args[i]);
            File t = new File(args[i] + "~");
            Source xmlInput = new StreamSource(f);
            Result xmlOutput = new StreamResult(t);

            transformer.transform(xmlInput, xmlOutput);
	    f.delete(); t.renameTo(f);

        }
    }
}
