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

public class RRstudentComponent implements REVComponent {

    private String key;

    // the expression of RSquare component
    private String rsExpress;

    private List<RRstudentComponent.Outlier> outlierList = new ArrayList();

    public List<RRstudentComponent.Outlier> getOutlierList() {
	return outlierList;
    }

    public boolean isNegligible() {
	return negligible;
    }

    public void setNegligible(boolean negligible) {
	this.negligible = negligible;
    }

    private boolean negligible = false;

    private static String RSTUDENT_RM_OUTS_TEMPL = "   rminflu<-function(){  rmr<-c( $ROWS ); $DATA<-$DATA[setdiff(row.names($DATA), rmr),]; return ($DATA) }  \n "
	    + " $DATA<-rminflu()  \n ";

    // private static String RSTUDENT_RM_OUTS_TEMPL =
    // " $DATA<-$DATA[(row.names($DATA)!=\"$ROW\"),] \n";

    private static String RSTUDENT_TEMPL = "outs<-function(estimation, min, max){ ols<-rstudent(estimation); vl<-c(as.numeric(ols));  rl<-names(ols); "
	    + " rmin<-rl[which(vl$LSN(min))]; rmax<-rl[which(vl$RSNmax)]; vmin<-vl[which(vl$LSN(min))]; vmax<-vl[which(vl $RSN(max) )]; "
	    + " rx<-c(rmin, rmax);  vx<-c(vmin, vmax); "
	    + " if(!is.null(rx) && !is.null(vx) && length(rx)>0 && length(vx)>0){ print('OUTLIER_BEGIN'); "
	    + " rst<-paste('ROW=', rx, sep=''); rst<-paste(rst, as.character(vx), sep='#OUTV=');  print(rst);"
	    + " print('OUTLIER_END');  }else{ print('NO_OUTLIER');}}\n" + " outs($ES, ($MIN), ($MAX) ) \n";

    public RRstudentComponent(String key, String rsExpress) {

	this.key = key;
	this.rsExpress = rsExpress;

    }

    public String getKey() {
	return key;
    }

    public String getRCommand(RFlowProperties properties) {
	String rcmd = "";
	rcmd = rcmd + RSTUDENT_TEMPL.replace("$ES", properties.getSummaryParam());
	String leftSign = rsExpress.contains("(") ? "<" : "<=";
	String rightSign = rsExpress.contains(")") ? ">" : ">=";
	String min = rsExpress.substring(1, rsExpress.indexOf(",")).trim();
	String max = rsExpress.substring(rsExpress.indexOf(",") + 1, rsExpress.length() - 1).trim();
	rcmd = rcmd.replace("$MIN", min);
	rcmd = rcmd.replace("$MAX", max);
	rcmd = rcmd.replace("$MAX", max);
	rcmd = rcmd.replace("$LSN", leftSign);
	rcmd = rcmd.replace("$RSN", rightSign);
	return rcmd;
    }

    public long holdedMS() {
	return 800;
    }

    public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
	String cmd = getRCommand(properties);
	RMLogger.info("Rstudent ");

	String str = sb.toString().trim();

	if (str.contains("NO_OUTLIER")) {
	    RMLogger.info("No outlier");
	} else if (str.contains("OUTLIER_BEGIN") && str.contains("OUTLIER_END")) {
	    str = str.substring(str.lastIndexOf("OUTLIER_BEGIN") + 13, str.indexOf("OUTLIER_END"));
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

	    RMLogger.info("Row             Outlier ");

	    for (String out : outs) {
		System.out.println(out);
		if (out.contains("#OUTV=")) {
		    String[] rownout = out.split("#OUTV=");
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
		    for (RRstudentComponent.Outlier out : outlierList) {
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
				// tmpCmd = tmpCmd + RSTUDENT_RM_OUTS_TEMPL.replace("$ROW",
				// out.getRowNum());
				rownames = rownames + out.getRowNum() + ", ";
				rows++;

			    }
			}

		    }

		    if (rows > 0 ) {
			rownames = rownames.substring(0, rownames.lastIndexOf(","));
			rcmd = RSTUDENT_RM_OUTS_TEMPL.replace("$ROWS", rownames);

			rcmd = rcmd.replace("$DATA", properties.getDataParaName());
			str = RMEngine.executeAndWaitReturn(rcmd, 200, false);
			if (str.indexOf("Error") > -1) {
			    RMLogger.error("Error found. Please check your flow and data.");
			    RMLogger.info(str);
			    return RComponent.ERROR;
			} else {
			    System.out.println(rownames);
			    // rownames = rownames.substring(0, rownames.lastIndexOf(","));
			    if (rows > 1) {
				rownames = "The row " + rownames + " have been removed from data as outlier. ";
			    } else {
				rownames = "The row " + rownames + " has been removed from data as outlier. ";
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
	Pattern p = Pattern.compile("^[(\\[][\\-\\+]?\\d+.*\\d*\\s*,\\s*[\\+\\-]?\\d+\\.*\\d*[\\])]$");
	rsExpress = rsExpress.trim();
	Matcher matcher = p.matcher(rsExpress);
	boolean rst = matcher.matches();
	if (!rst)
	    throw new RMException(" Please check Rstudent setting again. e.g. (-2, +2) or [-2, +2)");
	return rst;
    }
}
