package com.rm.app.io;

import java.beans.ExceptionListener;
import java.beans.PersistenceDelegate;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.rm.app.RMAppContext;
import com.rm.app.graph.RMGraph;
import com.rm.app.log.SystemLogger;
import com.rm.app.ui.action.ActionHelper;

/**
 * 
 * 
 */
public class RMXMLSaver {

    public static boolean saveToXML(String filename) {
	OutputStream out;
	try {
	    RMGraph graph = RMAppContext.getGraph();
	    out = getOutputStream(filename);
	    writeObject(graph.getGraphLayoutCache(), out);
	    out.flush();
	    out.close();
	    graph.setIsSaved(true);
	    graph.setReferXML(filename);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return false;
    }

    public static void writeObject(Object object, OutputStream out) {
	final List problems = new LinkedList();
	if (object != null) {
	    XMLEncoder enc = new XMLEncoder(out);
	    enc.setExceptionListener(new ExceptionListener() {
		public void exceptionThrown(Exception e) {
		    // Uncomment this line for debugging
		    // XML encoding:
		    e.printStackTrace();
		    problems.add(e);
		}
	    });
	    configureEncoder(enc);
	    enc.writeObject(object);
	    enc.close();
	}
	if (!problems.isEmpty())
	    throw new RuntimeException(problems.size() + " errors while writing " + object + " (" + problems.get(0)
		    + ")");
    }

    // public static String writeToString(Object object, OutputStream out) {
    // final List problems = new LinkedList();
    // String s = null;
    // if (object != null) {
    // XMLEncoder enc = new XMLEncoder(out);
    // enc.setExceptionListener(new ExceptionListener() {
    // public void exceptionThrown(Exception e) {
    // // Uncomment this line for debugging
    // // XML encoding:
    // e.printStackTrace();
    // problems.add(e);
    // }
    // });
    // configureEncoder(enc);
    // s = enc.writeObject(o)toString();
    // enc.close();
    // }
    // // if (!problems.isEmpty())
    // // throw new RuntimeException(problems.size() + " errors while writing "
    // + object + " (" + problems.get(0)
    // // + ")");
    // return s;
    // }

    protected static void configureEncoder(XMLEncoder enc) {

	Iterator it = RMAppContext.persistenceDelegates.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry entry = (Map.Entry) it.next();
	    enc.setPersistenceDelegate((Class) entry.getKey(), (PersistenceDelegate) entry.getValue());
	}
	enc.setPersistenceDelegate(ArrayList.class, enc.getPersistenceDelegate(List.class));
    }

    public static OutputStream getOutputStream(String uri) throws FileNotFoundException {
		OutputStream out = null;
		if (ActionHelper.isURL(uri))
		    out = new ByteArrayOutputStream();
		else
		    out = new BufferedOutputStream(new FileOutputStream(uri));
		return out;
    }

    public static boolean saveToXML4Log(String filename) {
	OutputStream out;
	try {
	    RMGraph graph = RMAppContext.getGraph();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    out = getOutputStream(filename + ".bak");
	    FileWriter writer = new FileWriter(filename+"."+formatter.format(new Date()).substring(0,10), true);
	    writeObject(graph.getGraphLayoutCache(), out);
	    out.flush();
	    out.close();

	    BufferedReader reader = new BufferedReader(new FileReader(filename + ".bak"));
	    String line = null;
	    writer.write( formatter.format(new Date())+"\n");
	    while ((line = reader.readLine()) != null)
		writer.write(line);
	    writer.flush();
	    writer.close();
	    // graph.setIsSaved(true);
	    // graph.setReferXML(filename);
	} catch (Exception e) {
    		SystemLogger.fileLogger.warn(e.toString());
		e.printStackTrace();
	}

	return false;
    }
}
