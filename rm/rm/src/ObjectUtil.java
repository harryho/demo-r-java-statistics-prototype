

public class ObjectUtil {

    public static void print(Object obj) {
	int index = 0;
	if (obj instanceof int[]) {
	    int[] ints = (int[]) obj;
	    for (int i : ints)
		System.out.println(" index :: " + (index++) + " value :: " + i);
	} else if (obj instanceof double[]) {
	    double[] dbls = (double[]) obj;
	    for (double i : dbls)
		System.out.println(" index :: " + (index++) + " value :: " + i);
	} else if (obj instanceof String[]) {
	    String[] strs = (String[]) obj;
	    for (String i : strs)
		System.out.println(" index :: " + (index++) + " value :: " + i);
	} else if (obj instanceof Object[]) {
	    Object[] strs = (Object[]) obj;
	    for (Object i : strs)
		System.out.println(" index :: " + (index++) + " value :: " + i);
	} 

    }

}
