import org.rosuda.JRclient.REXP;
import org.rosuda.JRclient.RSrvException;
import org.rosuda.JRclient.Rconnection;


public class RTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub

	try {


	    Rconnection c = new Rconnection();
	    REXP x = c.eval("R.version.string");
	    System.out.println(x.asString());

	    } catch (RSrvException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    } 

    }

}
