package com.rm.app.r.component.explanation.statement;

import com.rm.app.r.flow.RFlowProperties;

public class RExResVsFitPlotStatement implements RExStatement {

    public String getRCommand(RFlowProperties properties) {

	String cmd = "png(filename = '" + properties.getPngFile()
		+ "',width = 400, height = 400, pointsize = 12, bg = 'white') \n" +
				"par(mfrow=c(1,1), mar=c(8,5,4,4)) \n";
	cmd = cmd + "plot(" + properties.getSummaryParam() + "$fitted.values, " + properties.getSummaryParam()
		+ "$residuals, xlab = \"Fitted Values \", ylab = \"Residuals\","
		+ " main=\"Residuals vs Fitted\", sub=" + properties.getSummaryParam() + "$call, " + "xlim=range("
		+ properties.getSummaryParam() + "$fitted.values) , " + "ylim =range(" + properties.getSummaryParam()
		+ "$residuals)*1.25 ) \n";
	cmd = cmd + "lines(supsmu(" + properties.getSummaryParam() + "$fitted.values, " + properties.getSummaryParam()
		+ "$residuals, span=\"cv\", bass=1 )," + "lty=1, col=\"Red\"  , ljoin=2 )";

	return cmd;
    }

    public String formatDisplay(String callBack, RFlowProperties properties) {
	return callBack;
    }
}
