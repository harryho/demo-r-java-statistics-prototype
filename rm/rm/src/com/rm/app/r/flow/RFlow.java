package com.rm.app.r.flow;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.rm.app.RMAppContext;
import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.r.RHelper;
import com.rm.app.r.RMEngine;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.component.data.RDataCache;
import com.rm.app.r.component.data.RDataComponent;
import com.rm.app.r.component.data.RDataHelper;
import com.rm.app.r.component.estimation.RGLSComponent;
import com.rm.app.r.component.evaluation.RCookComponent;
import com.rm.app.r.component.evaluation.RDffitsComponent;
import com.rm.app.r.component.evaluation.RHATComponent;
import com.rm.app.r.component.evaluation.RPValueComponent;
import com.rm.app.r.component.evaluation.RRstudentComponent;
import com.rm.app.r.flow.output.GraphWindow;

public class RFlow {

    // the sequence on flows queue
    public int sequence = 0;

    private RFlowProperties properties;

    public RFlow(int sequence) {
	this.sequence = sequence;
    }

    public boolean hasStopCommand = false;

    private List<RComponent> items = new ArrayList<RComponent>();

    private final int MAX_LOOP_TIME = 5;

    public void addItem(RComponent item) {
	items.add(item);
    }

    private boolean executeFlow() throws Exception {
	RMEngine.MAINRCONSOLE.cleanCallBackMessage();
	boolean ret = false;
	String pngFile = RMAppContext.cachePath + properties.getSummaryParam() + ".png";

	properties.setPngFile(pngFile);

	// RMEngine.execute("png(filename = '" + pngFile
	// + "',width = 480, height = 480, pointsize = 12, bg = 'white')",
	// false);

	for (int i = 0, k = items.size(); i < k; i++) {

	    RComponent item = items.get(i);
	    RMLogger.debug("Rflow item " + i + "======> " + item.getKey());

	    // Notice that data has been load on cache.then not need to load it
	    // again.
	    if (RDataHelper.isDataComponent(item)) {
		RDataComponent dataCom = (RDataComponent) item;
		if (RDataCache.getIntance().isDataLoaded(dataCom)) {
		    RMLogger.debug("Use the data on cache.");
		    continue;
		}
	    }

	    executeComponent(item);

	    StringBuffer sb = RMEngine.MAINRCONSOLE.callBackMessage;
	    RMEngine.MAINRCONSOLE.cleanCallBackMessage();
	    // handle the callback
	    if (hasError(sb.toString())) {
		RMLogger.error("Error found.");
		RMLogger.info(sb.toString());
		return false;
	    }
	    int callBackCode = item.handleCallBack(sb, getProperties());
	    // if the call back is from P-value,try to remove the unsuccessful
	    // variables
	    if (callBackCode == RComponent.REPROCESS && (item instanceof RPValueComponent)) {

		// just do this for MR.
		RPValueComponent pv = (RPValueComponent) item;
		List<String> errorList = pv.getFailedVar();
		RMLogger.debug("errorList++++++++++++++++++++++++>" + errorList);
		if (errorList != null && errorList.size() > 0) {
		    List<String> indpl = properties.getIndepend_var();
		    RMLogger.debug("indpl++++++++++++++++++++++++>" + indpl);

		    List<String> newIndepend = new ArrayList<String>();
		    for (int ki = 0; ki < indpl.size(); ki++) {
			String indv = indpl.get(ki);
			if (!errorList.contains(indv)) {
			    newIndepend.add(indv);
			}

		    }
		    RMLogger.debug("newIndepend++++++++++++++++++++++++>" + newIndepend);
		    if (newIndepend.size() > 0) {
			properties.setIndepend_var(newIndepend);
			properties.generateNewSummaryParam();
			i = 0;// repeat foreach again from next by DB component
			RMLogger.warn("Suggest to use independent variables:" + newIndepend);
			RMLogger.info("Continue ...");

		    } else {
			RMLogger.warn("All the independent variables do not pass P-value tests");
			JOptionPane.showMessageDialog(null, "All the independent variables do not pass P-value tests!");
			return false;
		    }
		}

	    } else if (callBackCode == RComponent.REPROCESS
		    && (item instanceof RRstudentComponent || item instanceof RCookComponent || item instanceof RDffitsComponent || item instanceof RHATComponent)) {
		System.out.println(" ############################################################ ");
		i = 0;
	    } else if (callBackCode == RComponent.SKIP && (item instanceof RGLSComponent)) {
		System.out.println(" ############################################################ ");
		i = items.size() - 2;
	    } else if (callBackCode == RComponent.ERROR) {
		return false;
	    }

	}// End of for

	// deal with result graph
	GraphWindow gw = new GraphWindow();

	// RMEngine.execute("plot(" + properties.getSummaryParam() + ")",
	// false);
	RMEngine.execute("dev.off()", false);
	try {
	    Thread.sleep(RComponent.HOLD_MS);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	System.out.println("  gw.displayPng(pngFile)   " + gw.displayPng(pngFile));
	if (gw.displayPng(pngFile)) {// if any graph output

	    RHelper.CURRENT_OUTPUT_GRAGH_WINDOW = gw;
	}
	RMAppContext.getRMApp().setAlwaysOnTop(false);
	ret = true;
	return ret;
    }

    /**
     * 
     * @param item
     * @return false when terminated!
     */
    private boolean executeComponent(RComponent item) throws RMException {
	int loop_time = 0;
	String cmd = item.getRCommand(getProperties());
	if (cmd == null || "".equals(cmd.trim())) {
	    return true;

	}
	RMEngine.execute(cmd, false);
	if (item.holdedMS() > 0) {
	    while (!hasStopCommand) {
		try {
		    Thread.sleep(item.holdedMS());
		} catch (InterruptedException e) {
		    e.printStackTrace();
		    if (hasStopCommand) {// stop by user
			RMLogger.warn("Process is terminated!");
			throw new RMException("Process is terminated!");
		    }
		}
		loop_time++;
		if (RMEngine.MAINRCONSOLE.callBackMessage.length() > 0 || loop_time > MAX_LOOP_TIME) {
		    break;
		}

	    }
	}
	return true;
    }

    public boolean run() throws Exception {
	return executeFlow();

    }

    // public String getPngFile() {
    // return
    // }

    public RFlowProperties getProperties() {
	return properties;
    }

    public void setProperties(RFlowProperties properties) {
	this.properties = properties;
    }

    /**
     * 
     * @param info
     * @return
     */
    public static boolean hasError(String info) {
	if (info != null && info.indexOf("Error") == 0) {
	    return true;
	}
	return false;
    }

}
