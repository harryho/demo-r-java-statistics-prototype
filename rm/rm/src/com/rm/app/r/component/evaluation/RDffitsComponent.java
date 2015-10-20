package com.rm.app.r.component.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;

import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.r.RMEngine;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.ui.RMFlowRunningDialog;

public class RDffitsComponent implements REVComponent {

    private String key;

    // the expression of RSquare component
    private String rsExpress;

    private List<RDffitsComponent.Outlier> outlierList = new ArrayList();

    public List<RDffitsComponent.Outlier> getOutlierList() {
	return outlierList;
    }

    public boolean isNegligible() {
	return negligible;
    }

    public void setNegligible(boolean negligible) {
	this.negligible = negligible;
    }

    private boolean negligible = false;

    private static String DFFITS_RM_TEMPL = "   rminflu<-function(){  rmr<-c( $ROWS ); $DATA<-$DATA[setdiff(row.names($DATA), rmr),]; return ($DATA) }  \n "
	    + " $DATA<-rminflu()  \n ";
//     + " l<-length(row.names($DATA)) ;  \n print(l);  \n ";

    private static String DFFITS_TEMPL = "dffitsFunc<-function(estimation, k){ n<-length(row.names($ES$model)); "
	    + "ckd<-influence.measures(estimation)$infmat[,'dffit']; vl<-c(abs(as.numeric(ckd)));  rl<-names(ckd); "
	    + " rnl<-rl[which(vl$EXPRESSION)]; rvl<-vl[which(vl$EXPRESSION)];  rx<-c(rnl);  vx<-c(rvl); "
	    + " if(!is.null(rx) && !is.null(vx) && length(rx)>0 && length(vx)>0){ print('INFLU_BEGIN'); "
	    + " rst<-paste('ROW=', rx, sep=''); rst<-paste(rst, as.character(vx), sep='#INFLU=');  print(rst);"
	    + " print('INFLU_END');  }else{ print('NO_HIGH_INFLU');}}\n" + " dffitsFunc($ES, $K ) \n";

    public RDffitsComponent(String key, String rsExpress) {

	this.key = key;
	this.rsExpress = rsExpress;

    }

    public String getKey() {
	return key;
    }

    public String getRCommand(RFlowProperties properties) {
	String rcmd = "";
	rcmd = rcmd + DFFITS_TEMPL.replace("$ES", properties.getSummaryParam());
	String k = Integer.toString(properties.getIndepend_var().size());
	rcmd = rcmd.replace("$K", k);
	rcmd = rcmd.replace("$EXPRESSION", rsExpress);
	return rcmd;
    }

    public long holdedMS() {
	return 800;
    }

    public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
	String cmd = getRCommand(properties);
	RMLogger.info(" DFFITS ");

	String str = sb.toString().trim();

	if (str.contains("NO_HIGH_INFLU")) {
	    RMLogger.info("No Hight Influence");
	} else if (str.contains("INFLU_BEGIN") && str.contains("INFLU_END")) {
	    str = str.substring(str.lastIndexOf("INFLU_BEGIN") + 13, str.indexOf("INFLU_END"));
	    Pattern pp = Pattern.compile("\\[\\d+\\]");
	    Matcher mm = pp.matcher(str);
	    String cont = mm.replaceAll(" ");
	    mm = Pattern.compile("\\\"").matcher(cont);
	    cont = mm.replaceAll(" ");
	    mm = Pattern.compile("\\s+").matcher(cont);
	    cont = mm.replaceAll(" ");
	    System.out.println(cont);
	    String[] outs = cont.trim().split("ROW=");
	    outlierList.clear();

	    RMLogger.info("Row             DFFITS  ");

	    for (String out : outs) {
		System.out.println(out);
		if (out.contains("#INFLU=")) {
		    String[] rownout = out.split("#INFLU=");
		    System.out.println(rownout[0] + rownout.length);
		    System.out.println(rownout[1]);
		    String rowNum = rownout[0].trim();
		    String outlier = rownout[1].trim();
		    RMLogger.info(rowNum + "            " + outlier);
		    Outlier outl = new Outlier(rowNum, rowNum, outlier, false);
		    this.outlierList.add(outl);
		}
	    }
	    Collections.sort(outlierList);
	    try {
		JDialog jDialog = new RMFlowRunningDialog(this, properties);

		System.out.println("    " + isNegligible() + "  " + outlierList.size());
		if (isNegligible() || outlierList.size() <= 0) {
		    return RComponent.SUCEESS;
		} else {
		    for (RDffitsComponent.Outlier out : outlierList) {
			System.out.println("    " + out.getRowNum() + "  " + out.isRemove());
		    }

		    String rcmd = "";
		    int rows = 0;
//		    String tmpCmd = "";
		    String rownames = "";
		    if (outlierList.size() > 0) {

			for (int i = 0; i < outlierList.size(); i++) {
			    Outlier out = outlierList.get(i);
			    if (out.isRemove()) {
				// tmpCmd = tmpCmd + COOKS_DISTANCE_TEMPL.replace("$ROWS",
				// out.getRowNum());
				rownames = rownames + out.getRowNum() + ", ";
				rows++;

			    }
			}

		    }

		    if (rows > 0 ) {
			// rcmd = tmpCmd;
			rownames = rownames.substring(0, rownames.lastIndexOf(","));
			rcmd = DFFITS_RM_TEMPL.replace("$ROWS", rownames);
			rcmd = rcmd.replace("$DATA", properties.getDataParaName());
			str = RMEngine.executeAndWaitReturn(rcmd, 200, false);
//			 System.out.println( str);
			if (str.indexOf("Error") > -1) {
			    RMLogger.error("Error found. Please check your flow and data.");
			    RMLogger.info(str);
			    return RComponent.ERROR;
			} else {
			    System.out.println(rownames);
			    // rownames = rownames.substring(0, rownames.lastIndexOf(","));
			    if (rows > 1) {
				rownames = "The row " + rownames + " have been removed from data as influcence. ";
			    } else {
				rownames = "The row " + rownames + " has been removed from data as influcence. ";
			    }
			    RMLogger.info("");
			    RMLogger.warn(rownames);
			    RMLogger.info("");
			    System.out.println(rownames);
			    return RComponent.REPROCESS;
			}
		    }
		}

	    } catch (Exception e) {
		e.printStackTrace();

	    }

	}

	return RComponent.SUCEESS;
    }

    public class Outlier implements Comparable {
	public Outlier(String rowNum, String rowName, String outlier, boolean isRemove) {
	    this.rowNum = rowNum;
	    this.rowName = rowName;
	    this.outlier = outlier;
	    this.isRemove = isRemove;

	}

	public boolean isRemove() {
	    return isRemove;
	}

	public void setRemove(boolean isRemove) {
	    this.isRemove = isRemove;
	}

	public String getRowNum() {
	    return rowNum;
	}

	public String getRowName() {
	    return rowName;
	}

	public String getOutlier() {
	    return outlier;
	}

	String rowNum;
	String rowName;
	String outlier;
	boolean isRemove = false;

	public int compareTo(Object arg0) {
	    Outlier a2 = (Outlier) arg0;
	    return this.getRowNum().compareTo(a2.getRowNum());
	}

    }

    public boolean validation() throws RMException {
	boolean rst = false;
	if (rsExpress == null || rsExpress.trim().length() <= 0) {
	    throw new RMException(" Please check DFFITS setting again. e.g. 4/(n-k-1)");
	} else {
	    Matcher matcher = Pattern.compile("\\s+").matcher(rsExpress.trim());
	    rsExpress = matcher.replaceAll("").trim();
	    Pattern p = Pattern.compile("^[<>]\\d?[*+-/]?sqrt[(]+[\\dkn]?[*+-/]?[\\dkn]?[*+-/]?[\\dkn]?[)]*[[*+-/]?[(]*[\\dkn]?[*+-/]?[\\dkn]?[*+-/]?[\\dkn]?[)]*]*[)]+");
	    // rsExpress = rsExpress.trim();
	    matcher = p.matcher(rsExpress);
	    rst = matcher.matches();
	    if (!rst)
		throw new RMException(" Please check DFFITS setting again. e.g. 2 * sqrt ((k+1)/(n-k-1))");
	}
	return rst;
    }
}
