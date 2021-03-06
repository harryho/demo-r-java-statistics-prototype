import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

class TestException extends Exception {
    public TestException(String msg) { super(msg); }
}

public class RSTest {
    public static void main(String[] args) {
	try {
	    RConnection c = new RConnection();

	    System.out.println(">>"+c.eval("R.version$version.string").asString()+"<<");

		{
			System.out.println("* Test string and list retrieval");
			RList l = c.eval("{d=data.frame(\"huhu\",c(11:20)); lapply(d,as.character)}").asList();
			int cols = l.size();
			int rows = l.at(0).length();
			String[][] s = new String[cols][];
			for (int i=0; i<cols; i++) s[i]=l.at(i).asStrings();
			System.out.println("PASSED");
		}
		
	    {
		System.out.println("* Test NA/NaN support in double vectors...");
		double R_NA = Double.longBitsToDouble(0x7ff00000000007a2L);
		// int R_NA_int = -2147483648; // just for completeness
		double x[] = { 1.0, 0.5, R_NA, Double.NaN, 3.5 };
		c.assign("x",x);
		String nas = c.eval("paste(capture.output(print(x)),collapse='\\n')").asString();
		System.out.println(nas);
		if (!nas.equals("[1] 1.0 0.5  NA NaN 3.5"))
		    throw new TestException("NA/NaN assign+retrieve test failed");
		System.out.println("PASSED");
	    }
		
	    {
			System.out.println("* Test assigning of lists and vectors ...");
			RList l = new RList();
			l.put("a",new REXPInteger(new int[] { 0,1,2,3}));
			l.put("b",new REXPDouble(new double[] { 0.5,1.2,2.3,3.0}));
			System.out.println("  assign x=pairlist");
			c.assign("x", new REXPList(l));
			System.out.println("  assign y=vector");
			c.assign("y", new REXPGenericVector(l));
			System.out.println("  assign z=data.frame");
			c.assign("z", REXP.createDataFrame(l));
			System.out.println("  pull all three back to Java");
			REXP x = c.parseAndEval("x");
			System.out.println("  x = "+x);
			x = c.eval("y");
			System.out.println("  y = "+x);
			x = c.eval("z");
			System.out.println("  z = "+x);
			System.out.println("PASSED");
	    }
		{
			System.out.println("* Test support for logicals ... ");
			System.out.println("  assign b={true,false,true}");
			c.assign("b", new REXPLogical(new boolean[] { true, false, true }));
			REXP x = c.parseAndEval("b");
			System.out.println("  " + ((x != null) ? x.toDebugString() : "NULL"));
			if (!x.isLogical() || x.length() != 3)
				throw new TestException("boolean array assign+retrieve test failed");
			boolean q[] = ((REXPLogical)x).isTRUE();
			if (q[0] != true || q[1] != false || q[2] != true)
				throw new TestException("boolean array assign+retrieve test failed (value mismatch)");
			System.out.println("  get c(TRUE,FLASE,NA)");
			x = c.parseAndEval("c(TRUE,FALSE,NA)");
			System.out.println("  " + ((x != null) ? x.toDebugString() : "NULL"));
			if (!x.isLogical() || x.length() != 3)
				throw new TestException("boolean array NA test failed");
			boolean q1[] = ((REXPLogical)x).isTRUE();
			boolean q2[] = ((REXPLogical)x).isFALSE();
			boolean q3[] = ((REXPLogical)x).isNA();
			if (q1[0] != true || q1[1] != false || q1[2] != false ||
				q2[0] != false || q2[1] != true || q2[2] != false ||
				q3[0] != false || q3[1] != false || q3[2] != true)
				throw new TestException("boolean array NA test failed (value mismatch)");
		}

		{ // regression: object bit was not set for Java-side generated objects before 0.5-3
			System.out.println("* Testing functionality of assembled S3 objects ...");
			// we have already assigned the data.frame in previous test, so we jsut re-use it
			REXP x = c.parseAndEval("z[2,2]");
			System.out.println("  z[2,2] = " + x);
			if (x == null || x.length() != 1 || x.asDouble() != 1.2)
				throw new TestException("S3 object bit regression test failed");
			System.out.println("PASSED");
		}
		
		{ // this test does a pull and push of a data frame. It will fail when the S3 test above failed.
			System.out.println("* Testing pass-though capability for data.frames ...");
			REXP df = c.parseAndEval("{data(iris); iris}");
			c.assign("df", df);
			REXP x = c.eval("identical(df, iris)");
			System.out.println("  identical(df, iris) = "+x);
			if (x == null || !x.isLogical() || x.length() != 1 || !((REXPLogical)x).isTrue()[0])
				throw new TestException("Pass-through test for a data.frame failed");
			System.out.println("PASSED");
		}
		
            { // factors
                System.out.println("* Test support of factors");
                REXP f = c.parseAndEval("factor(paste('F',as.integer(runif(20)*5),sep=''))");
				System.out.println("  f="+f);
                System.out.println("  isFactor: "+f.isFactor()+", asFactor: "+f.asFactor());
                if (!f.isFactor() || f.asFactor() == null) throw new TestException("factor test failed");
                System.out.println("  singe-level factor used to degenerate:");
                f = c.parseAndEval("factor('foo')");
                System.out.println("  isFactor: "+f.isFactor()+", asFactor: "+f.asFactor());
                if (!f.isFactor() || f.asFactor() == null) throw new TestException("single factor test failed (not a factor)");
				if (!f.asFactor().at(0).equals("foo")) throw new TestException("single factor test failed (wrong value)");
                System.out.println("  test factors with null elements contents:");
				c.assign("f", new REXPFactor(new RFactor(new String[] { "foo", "bar", "foo", "foo", null, "bar" })));
				f = c.parseAndEval("f");
                if (!f.isFactor() || f.asFactor() == null) throw new TestException("factor assign-eval test failed (not a factor)");
				System.out.println("  f = "+f.asFactor());
				f = c.parseAndEval("as.factor(c(1,'a','b',1,'b'))");
				System.out.println("  f = "+f);
                if (!f.isFactor() || f.asFactor() == null) throw new TestException("factor test failed (not a factor)");
				System.out.println("PASSED");
            }


	    {
			System.out.println("* Lowess test");
			double x[] = c.eval("rnorm(100)").asDoubles();
			double y[] = c.eval("rnorm(100)").asDoubles();
			c.assign("x", x);
			c.assign("y", y);
			RList l = c.parseAndEval("lowess(x,y)").asList();
			System.out.println("  "+l);
			x = l.at("x").asDoubles();
			y = l.at("y").asDoubles();
			System.out.println("PASSED");
		}

	    {
			// multi-line expressions
			System.out.println("* Test multi-line expressions");
			if (c.eval("{ a=1:10\nb=11:20\nmean(b-a) }\n").asInteger()!=10)
				throw new TestException("multi-line test failed.");
			System.out.println("PASSED");
	    }
		{
            System.out.println("* Matrix tests\n  matrix: create a matrix");
            int m=100, n=100;
            double[] mat=new double[m*n];
            int i=0;
            while (i<m*n) mat[i++]=i/100;
            System.out.println("  matrix: assign a matrix");
            c.assign("m", mat);
            c.voidEval("m<-matrix(m,"+m+","+n+")");
            System.out.println("matrix: cross-product");
            double[][] mr=c.parseAndEval("crossprod(m,m)").asDoubleMatrix();
			System.out.println("PASSED");
		}
		
		{
			System.out.println("* Test serialization and raw vectors");
			byte[] b = c.eval("serialize(ls, NULL, ascii=FALSE)").asBytes();
			System.out.println("  serialized ls is "+b.length+" bytes long");
			c.assign("r", new REXPRaw(b));
			String[] s = c.eval("unserialize(r)()").asStrings();
			System.out.println("  we have "+s.length+" items in the workspace");
			System.out.println("PASSED");
		}
		
		
		{ // string encoding test (will work with Rserve 0.5-3 and higher only)
			System.out.println("* Test string encoding support ...");
			String t = "ひらがな"; // hiragana (literally, in hiragana ;))
			c.setStringEncoding("utf8");
			// -- Just in case the console is not UTF-8 don't display it
			//System.out.println("  unicode text: "+t);
			c.assign("s", t);
			REXP x = c.parseAndEval("nchar(s)");
			System.out.println("  nchar = " + x);
			if (x == null || !x.isInteger() || x.asInteger() != 4)
				throw new TestException("UTF-8 encoding string length test failed");
			// we cannot really test any other encoding ..
			System.out.println("PASSED");
		}
		
		} catch (RserveException rse) {
	    System.out.println(rse);
	} catch (REXPMismatchException mme) {
	    System.out.println(mme);
	    mme.printStackTrace();
        } catch(TestException te) {
            System.err.println("** Test failed: "+te.getMessage());
            te.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
