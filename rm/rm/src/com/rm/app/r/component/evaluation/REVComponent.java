package com.rm.app.r.component.evaluation;

import com.rm.app.r.component.RComponent;

public interface REVComponent extends RComponent {

	public static final String TYPE_EVALUATION = "evaluation";
	public static final String TYPE_EVALUATION_RS = "evaluation.rsquare";
	public static final String TYPE_EVALUATION_PV = "evaluation.pvalue";
	public static final String TYPE_EVALUATION_RSTUD = "evaluation.rstudent";
	public static final String TYPE_EVALUATION_VIF = "evaluation.vif";
	public static final String TYPE_EVALUATION_NCV = "evaluation.ncv";
	public static final String TYPE_EVALUATION_AUTOCORRECT = "evaluation.autocorr";
	public static final String TYPE_EVALUATION_NORMALITY = "evaluation.normality";
	public static final String TYPE_EVALUATION_NFI = "evaluation.nfi";
	public static final String TYPE_EVALUATION_RMSEA = "evaluation.rmsea";
	public static final String TYPE_EVALUATION_COOKS = "evaluation.cooks";
	public static final String TYPE_EVALUATION_DFFITS = "evaluation.dffits";
	public static final String TYPE_EVALUATION_HAT = "evaluation.hat";

	public static final String ATTR_EVALUATION_LOG_FUNCTION = "ATTR_EVALUATION_LOG_FUNCTION";
}
