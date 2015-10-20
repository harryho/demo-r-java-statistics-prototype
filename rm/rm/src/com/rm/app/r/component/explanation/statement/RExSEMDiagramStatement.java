package com.rm.app.r.component.explanation.statement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

//import sun.awt.AppContext;

import com.rm.app.RMAppContext;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.r.flow.output.PathDiagramWindow;

@SuppressWarnings("unused")
public class RExSEMDiagramStatement implements RExStatement {

    public String formatDisplay(String callBack, RFlowProperties properties) {

	BufferedWriter writer = null;
	try {
	    String fileName = RMAppContext.cachePath + properties.getSummaryParam() + ".path";

	    String pngName = properties.getPngFile();

	    File file = new File(fileName);

	    if (file.exists())
		file.delete();
	    
	    writer = new BufferedWriter(new FileWriter(fileName));
	    writer.write(callBack);
	    writer.flush();
	    try {
		writer.close();
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	    int i = 0;
	    while (!file.exists()) {
		try {
		    Thread.sleep(100);
		    if (i++ == 10)
			break;
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }

	    if (!file.exists())
		return callBack;

	    String cmd = RMAppContext.graphvizPath + "dot  -Tpng   -o\"" + pngName + "\"  \"" + fileName + "\"";
	    Process connector = Runtime.getRuntime().exec(cmd);

	    int code = 0;
	    i = 0;
	    while (code != 0) {
		try {
		    System.out.println(i + "  " + code);
		    code = connector.waitFor();
		    Thread.sleep(200);
		    if (i++ == 20)
			break;
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }

	    File pngFile = new File(pngName);

	    i = 0;
	    while (!pngFile.exists()) {
		try {		    
		    Thread.sleep(100);
		    if (i++ == 10)
			break;
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }

	    if (!pngFile.exists()) {
		System.out.println("  png not exist  ");
		return callBack;
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	}

	return "";
    }

    public String getRCommand(RFlowProperties properties) {

	return "path.diagram(" + properties.getSummaryParam() + ")  \n";
    }

}
