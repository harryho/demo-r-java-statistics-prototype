package com.rm.app.r;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class RController {
	/**
	 * Create {@see RModel} (java-side) out of R
	 * 
	 * @param sx
	 *            name
	 * @param type
	 *            type (currently only lm and glm is supported
	 * @return new {@see RModel}
	 */
	public static RModel createRModel(Rengine eng,String sx, String type) {
		RModel m = new RModel(sx, type);
		REXP y = eng.eval("summary(" + sx + ")[[\"r.squared\"]]");
		double[] res;
		if (y != null && (res = y.asDoubleArray()) != null)
			m.setRsquared(res[0]);
		y = eng.eval("AIC(" + sx + ")");
		if (y != null && (res = y.asDoubleArray()) != null)
			m.setAic(res[0]);
		y = eng.eval("deviance(" + sx + ")");
		if (y != null && (res = y.asDoubleArray()) != null)
			m.setDeviance(res[0]);
		int[] res1;
		y = eng.eval("summary(" + sx + ")[[\"df\"]]");
		if (y != null && (res1 = y.asIntArray()) != null)
			m.setDf(res1[0]);
		String[] res2;
		y = eng.eval("family(" + sx + ")[[\"family\"]]");
		if (y != null && (res2 = y.asStringArray()) != null)
			m.setFamily(res2[0]);
		y = eng.eval("suppressWarnings(try(capture.output(" + sx
				+ "[[\"call\"]][[\"formula\"]])))"); // as.character((cm$call))
		if (y != null && (res2 = y.asStringArray()) != null) {
			String call = "";
			for (int i = 0; i < res2.length; i++)
				call += res2[i];
			m.setCall(call);
		}
		y = eng.eval("suppressWarnings(try(capture.output(" + sx
				+ "[[\"call\"]][[\"data\"]])))"); // as.character((cm$call))
		if (y != null && (res2 = y.asStringArray()) != null) {
			String data = "";
			for (int i = 0; i < res2.length; i++)
				data += res2[i];
			if (!data.trim().equals("NULL"))
				m.setData(data);
		}
		return m;
	}
}
