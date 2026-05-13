/*
 * Main.java
 *
 * Created on Jul 1, 2007, 7:24:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author gs145266
 */
public class Main {

    private static final int DEFAULT_REQUEST_COUNT = 100;

    /** Creates a new instance of Main */
    public Main() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            HttpUnitOptions.setExceptionsThrownOnScriptError(false);
            ServletRunner sr = new ServletRunner();
            sr.registerServlet("myServlet", HelloWorld.class.getName());
            ServletUnitClient sc = sr.newClient();
            int requestCount = getRequestCount(args);
            WebRequest request = new GetMethodWebRequest("http://test.meterware.com/myServlet");
            long startedAt = System.currentTimeMillis();
            for (int number = 1; number <= requestCount; number++) {
                WebResponse response = sc.getResponse(request);
                if (number == 1 || number == requestCount) {
                    System.out.println("Count: " + number + ", status: " + response.getResponseCode());
                }
            }
            long elapsed = System.currentTimeMillis() - startedAt;
            System.out.println("Processed " + requestCount + " requests in " + elapsed + " ms");
        } catch (MalformedURLException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
    }

    private static int getRequestCount(String[] args) {
        if (args.length == 0) {
            return DEFAULT_REQUEST_COUNT;
        }
        return Integer.parseInt(args[0]);
    }
}
