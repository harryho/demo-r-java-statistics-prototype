package com.rm.app.io;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import com.rm.app.RMAppConst;
import com.rm.app.RMAppContext;
import com.rm.app.RMResources;
import com.rm.app.graph.RMCellViewFactory;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.graph.RMModel;
import com.rm.app.log.RMLogger;
import com.rm.app.ui.action.ActionHelper;

/**
 * Load research map from xml file.
 * 
 * 
 */
public class RMXMLLoader {

	public static boolean loadXML(String fileName) throws Exception {
		// try {
		Object file = readFile(fileName);
		if (file != null) {
			RMAppContext.getGraph()
					.setGraphLayoutCache((GraphLayoutCache) file);
			GraphModel model = new RMModel();
			RMCellViewFactory cellViewFactory = (RMCellViewFactory) RMAppContext.get(RMAppConst.RM_GRAPH_CELL_VIEW_FACTORY);
			GraphLayoutCache cache = RMAppContext.getGraph()			
					.getGraphLayoutCache();
			cache.setFactory(cellViewFactory);			
			Iterator iterator = cache.getVisibleSet().iterator();
			RMAppContext.getGraph().setGraphLayoutCache(cache);

			// List list = new ArrayList();
			Map map = new HashMap();

			Iterator iterator2 = cache.getVisibleSet().iterator();
			Object[] cells = new Object[cache.getVisibleSet().size()];
			int i = 0;
			while (iterator2.hasNext()) {
				Object object = iterator2.next();
				if (object instanceof RMGraphCell) {
					//
					RMGraphCell cell = (RMGraphCell) object;
					cell.setAllowsChildren(true);
System.out.println("open.... "+cell.getKey());
					AttributeMap atts = cell.getAttributes();
					RMLogger.debug(atts);

					Set keySet = atts.keySet();
					Set entrySet = atts.entrySet();
					Iterator keyIterator = keySet.iterator();
					Iterator entryIterator = entrySet.iterator();
					Map newAtt = new HashMap();

					while (keyIterator.hasNext()) {
						String key = (String) keyIterator.next();
						Object value = atts.get(key);

						if (GraphConstants.ICON.equals(key)) {
							// icon = (ImageIcon) value;
							URL url = null;
							String strURL = value.toString();
							String imgName = strURL.substring(strURL
									.lastIndexOf("/") + 1, strURL.length());
							String path = RMResources
									.getString(RMAppConst.RM_COMPONENT_ICON_PATH);
							url = ClassLoader
									.getSystemResource((path + imgName));
							// }
							ImageIcon analysisIcon = new ImageIcon(url);
							GraphConstants.setIcon(newAtt, analysisIcon);
						}

					}
					// Add a Bounds Attribute to the Map
					GraphConstants.setBounds(newAtt, new Rectangle2D.Double(
							cell.getPosX(), cell.getPosY(), 50, 50));
					// Make sure the cell is resized on insert
					GraphConstants.setResize(newAtt, true);
					// Add a nice looking gradient background
					GraphConstants.setGradientColor(newAtt, Color.WHITE);
					// Add a Border Color Attribute to the Map
					GraphConstants.setBorderColor(newAtt, Color.WHITE);
					// Add a White Background
					GraphConstants.setBackground(newAtt, Color.white);
					// Make Vertex Opaque
					GraphConstants.setOpaque(newAtt, true);

					cell.getAttributes().applyMap(newAtt);
					cells[i++] = cell;
				} else {
					cells[i++] = object;
				}
			}

			RMAppContext.getGraph().getGraphLayoutCache().insert(cells);
		
			return true;
		}

		return false;
	}

	public static InputStream getInputStream(String uri)
			throws MalformedURLException, FileNotFoundException, IOException {

		URL url = RMResources.class.getResource(uri);
		try {
			return new BufferedInputStream(url.openStream());
		} catch (Exception e) {
			return new BufferedInputStream(ActionHelper.isURL(uri) ? new URL(
					uri).openStream() : new FileInputStream(uri));
		}
	}

	public static Object readFile(String uri) throws Exception{

		InputStream in = getInputStream(uri);
		Object file = readObject(in);
		in.close();

		return file;
	}

	public static Object readObject(InputStream in) throws Exception {
		XMLDecoder dec = new XMLDecoder(in);
		Object obj = null;
		if (dec != null) {
			obj = dec.readObject();
			dec.close();

		}
		return obj;

	}

}
