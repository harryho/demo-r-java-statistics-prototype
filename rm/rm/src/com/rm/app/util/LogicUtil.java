package com.rm.app.util;

import java.math.BigDecimal;

public class LogicUtil {

	private static final String ng = "<=";

	private static final String ne = "<>";

	private static final String nl = ">=";

	private static final String eq = "=";

	private static final String lt = "<";

	private static final String gt = ">";

	/**
	 * 
	 * @param intStr
	 * @param exp
	 * @return
	 */
	public static boolean checkLogicByExpression(String intStr, String exp) {
		if (intStr == null || exp == null || "".equals(exp = exp.trim())) {
			return false;
		}
		intStr = intStr.trim();
		try {
			BigDecimal dec = new BigDecimal(intStr);
			int idx_ng = exp.indexOf(ng);
			if (idx_ng != -1) {
				String value = exp.substring(idx_ng + 2).trim();
				BigDecimal valueDec = new BigDecimal(value);
				return dec.compareTo(valueDec) <= 0;
			}
			int idx_ne = exp.indexOf(ne);
			if (idx_ne != -1) {
				String value = exp.substring(idx_ne + 2).trim();
				BigDecimal valueDec = new BigDecimal(value);
				return dec.compareTo(valueDec) != 0;
			}
			int idx_nl = exp.indexOf(nl);
			if (idx_nl != -1) {
				String value = exp.substring(idx_nl + 2).trim();
				BigDecimal valueDec = new BigDecimal(value);
				return dec.compareTo(valueDec) >= 0;
			}

			int idx_eq = exp.indexOf(eq);
			if (idx_eq != -1) {
				String value = exp.substring(idx_eq + 1).trim();
				BigDecimal valueDec = new BigDecimal(value);
				return dec.equals(valueDec);
			}

			int idx_gt = exp.indexOf(gt);
			if (idx_gt != -1) {
				String value = exp.substring(idx_gt + 1).trim();
				BigDecimal valueDec = new BigDecimal(value);
				return dec.compareTo(valueDec) > 0;
			}

			int idx_lt = exp.indexOf(lt);
			if (idx_lt != -1) {
				String value = exp.substring(idx_lt + 1).trim();
				BigDecimal valueDec = new BigDecimal(value);
				return dec.compareTo(valueDec) < 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean validExpression(String exp) {
		boolean ret = true;
		if (exp == null || "".equals(exp = exp.trim())) {
			return false;
		}
		if (exp.indexOf(eq) == -1 && exp.indexOf(ne) == -1
				&& exp.indexOf(gt) == -1 && exp.indexOf(nl) == -1
				&& exp.indexOf(lt) == -1 && exp.indexOf(ng) == -1) {
			ret = false;
		}
		String tmp = exp.replaceAll(eq, "").replaceAll(ne, "").replaceAll(gt,
				"").replaceAll(nl, "").replaceAll(lt, "").replaceAll(ng, "");

		try {
			new BigDecimal(tmp);
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}
	

	// public static void main(String[] args) {
	// ;
	// System.out.println(LogicUtil.checkLogicByExpression("2.8.1", ">=2.70")
	// & LogicUtil.checkLogicByExpression("2.8.1", "<2.8.1"));
	//	
	// }
}
