package com.rm.app.r.component.explanation;

import com.rm.app.r.component.RComponent;

public interface REXComponent extends RComponent {
    public static final String TYPE_EXPLANATION = "explanation";
    public static final String TYPE_EXPLANATION_STD = "explanation.std";
    public static final String TYPE_EXPLANATION_COM = "explanation.com";
    public static final String TYPE_EXPLANATION_CUS = "explanation.cus";
    public static final String TYPE_EXPLANATION_CUS4SEM = "explanation.cus4sem";
    public static final String TYPE_EXPLANATION_STD4SEM = "explanation.std4sem";
    public static final String TYPE_EXPLANATION_COM4SEM = "explanation.com4sem";
    
    public static final String STATEMENT_SIGNIFICANCE = "significance"; 
    public static final String STATEMENT_COEFFICIENTS_TABLE = "coefficients table"; 
    public static final String STATEMENT_PLOT_FITTED_VALUE_RES = "plot of fitted value vs residuals"; 
    public static final String STATEMENT_VAR_EFFECTS = "variable effects statement"; 
    public static final String STATEMENT_FULL_PLOT = "full plot"; 
    
    
}
