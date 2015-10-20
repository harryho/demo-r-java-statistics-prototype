import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wltea.expression.ExpressionEvaluator;

import com.rm.app.util.ObjectUtil;

public class Test {

    private enum Seasons {
	winter, spring, summer, fall
    };

    public static void main(String[] args) {
	// String ss = "12345678";
	// System.out.println(ss.substring(3));
	//	
	// long start= System.currentTimeMillis();
	// int i =0;
	// while(true ){
	// System.out.println("  ");
	// long end= System.currentTimeMillis();
	// System.out.println(" i -- "+ i +"  start  "+ start + " end "+end );
	//	    
	// if((end-start)>3000)
	// break;
	// i++;
	//		
	// }
	Object o = Seasons.valueOf("winter");
	System.out.println(o + " " + o.getClass());
	for (Seasons s : Seasons.values()) {

	    System.out.println(s.getClass());
	    System.out.println(s);
	}

	// String eq =" aaa = bbb + ccc ";
	// String eq2 =" aaa = bbb + ccc ";
	// System.out.println( eq.substring(0, eq.indexOf("=")));
	// System.out.println( eq.substring(eq.indexOf("=")));
	//         
	// List <String> list = new ArrayList<String>();
	//
	// list.add(eq);list.add(eq2);
	// System.out.println(list);
	// Pattern p=Pattern.compile("\\s+");
	// for(int i =0 ; i < list.size(); i++){
	// // for(String e : list){
	// String e = list.get(i);
	// System.out.println(e );
	// Matcher m = p.matcher(e);
	// e=m.replaceAll("");
	// System.out.println(e );
	// list.set(i, m.replaceAll(""));
	// }
	// System.out.println(list);

//	String callBack = "\"FITEQ##RIQ->ROccAsp#gamma61#0.24##RSES->ROccAsp#gamma71#0.18##FSES->FOccAsp#gamma83#0.22##FIQ->FOccAsp#gamma93#0.31##FOccAsp->ROccAsp#beta31#0.4##ROccAsp->FOccAsp#beta13#0.42##ROccAsp<->FOccAsp#sigma13#-0.5##FOccAsp<->FOccAsp#sigma33#0.72FITEQ\"";
//	if (callBack.contains("FITEQ"))
//	    callBack = callBack.substring(callBack.indexOf("FITEQ##") + 7, callBack.lastIndexOf("FITEQ")).trim();
//	String[] paths = callBack.split("##");
//	ObjectUtil.print(paths);
//	String a = " sdf,  sdf. sdff, s ";
//	System.out.println(a.substring(0, a.lastIndexOf(",")));
//
//	String expression="0.088<0.01";
//
//	boolean b = (Boolean) ExpressionEvaluator.evaluate(expression);
	
	Pattern p = Pattern.compile("^[<>]\\d?[[*+-/]{1}[(]{1}[\\dkn]{1}[*+-/]?[\\dkn]?[)]{1}]*/[(]?[nk][*+-/]?[kn]?[*+-/]?[\\d]?[)]?[nk]?");  //[\\*\\+-/]?[(]?\\d?[\\*\\+-/]\\d?[)]?\\s*/\\s*(\\s*n[\\+-]?[[k][\\+-]]?\\d)$
	String rsExpress=">4*(k+1)/(n+1)"; ///(n-k-1)
	Matcher matcher = p.matcher(rsExpress);
	
	System.out.println( matcher.matches()+" sss, s".substring(0," sss, s".lastIndexOf(",")));
	
	p = Pattern.compile("^[<>]\\d?[*+-/]?sqrt[(]+[\\dkn]?[*+-/]?[\\dkn]?[*+-/]?[\\dkn]?[)]*[[*+-/]?[(]*[\\dkn]?[*+-/]?[\\dkn]?[*+-/]?[\\dkn]?[)]*]*[)]+"); //   /[(]?[nk][*+-/]?[kn]?[*+-/]?[\\d]?[)]?"); 
	rsExpress=">2*sqrt((k+1)/(n-k-1))"; // /(n-k-1))"; ///(n-k-1)
	 matcher = p.matcher(rsExpress);
	 System.out.println( matcher.matches());
    }

}
