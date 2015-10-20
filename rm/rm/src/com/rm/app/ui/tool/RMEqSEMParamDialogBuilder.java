package com.rm.app.ui.tool;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rm.app.RMResources;
import com.rm.app.graph.Attribute;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.graph.RMGraphFactory;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.data.RDataCache;
import com.rm.app.r.component.data.RDataComponent;
import com.rm.app.ui.RMAppParamDialog;
import com.rm.app.util.SpringUtilities;

/**
 * Be charge of build out the layout of those components
 * 
 */
public class RMEqSEMParamDialogBuilder extends RMParamDialogBuilder {

    public void buildDialogLayout(RMGraphCell cell, final RMAppParamDialog dialog) throws Exception {
	this.dialog = dialog;
	final Map<String, Attribute> dataMap = cell.getDataMap();

	JPanel bigPanel = new JPanel();
	bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.Y_AXIS));
	SpringLayout springLayout = new SpringLayout();

	final JPanel upanel = new JPanel(springLayout);

	final JScrollPane supanel = new JScrollPane();

	Document doc = RMAppDocumentBuilder.parse(RMResources.getInputStream(RMGraphFactory.PATH_RMCONFIG));
	Node compModelConfiguration = doc.getDocumentElement();

	Node comNode = getNode(compModelConfiguration, cell.getKey());

	NodeList attList = comNode.getChildNodes();

	JScrollPane dynamicPanel = new JScrollPane();

	JPanel fixedPanel = new JPanel(springLayout);
	int rows = 0;
	int rows2 = 0;
	if ("M".equals(cell.getFromDataCache())) {
	    System.out.println(" MMMMMMMMMMMMMMMMMMMMMMM  ");
	    RDataCache dataCache = RDataCache.getIntance();
	    List<String> list = dataCache.getAllNumericVariables();
	    if (list != null && list.size() > 0) {

		Attribute attEndo = dataMap.get("ENDO") == null ? null : (Attribute) dataMap.get("ENDO");
		Attribute attExo = dataMap.get("EXO") == null ? null : (Attribute) dataMap.get("EXO");

		List deList = attEndo == null ? null : (List) attEndo.getValue();
		List inList = attExo == null ? null : (List) attExo.getValue();

		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel vLabel = new JLabel(" Variable Name           ");
		JLabel deLabel = new JLabel(" Endogeneous        ");
		JLabel inLabel = new JLabel(" Exogenous          ");

		upanel.add(vLabel);
		upanel.add(deLabel);
		upanel.add(inLabel);
		rows += 1;
		for (int i = 0; i < list.size(); i++) {
		    String varName = list.get(i);
		    JLabel varLabel = new JLabel(varName);
		    JCheckBox enCheckBox = new JCheckBox();
		    JCheckBox exCheckBox = new JCheckBox();

		    enCheckBox.setName("EN_" + varName);
		    exCheckBox.setName("EX_" + varName);

		    if (deList != null && deList.size() > 0 && deList.contains(varName)) {
			enCheckBox.setSelected(true);
		    }

		    if (inList != null && inList.size() > 0 && inList.contains(varName)) {
			exCheckBox.setSelected(true);
		    }

		    inputSet.add(enCheckBox);
		    inputSet.add(exCheckBox);

		    upanel.add(varLabel);
		    upanel.add(enCheckBox);
		    upanel.add(exCheckBox);
		    rows += 1;
		}

	    }
	    System.out.println(" rows" + rows);
	    System.out.println(" rows 2" + rows2);
	    SpringUtilities.makeCompactGrid(upanel, // parent
		    rows, 3,// rows,columns
		    3, 3, // initX, initY
		    3, 3); // xPad, yPad
	    // }

	    for (int i = 0, k = attList.getLength(); i < k; i++) {
		Node att = attList.item(i);
		if (!RMGraphFactory.ATTRIBUTENAME.equals(att.getNodeName())) {
		    continue;
		}

		String description = RMAppDocumentBuilder.getAttributeValue(att,
			RMAppDocumentBuilder.ATTRIBUTENAME_DISCRIPTION);
		String name = RMAppDocumentBuilder.getAttributeValue(att, RMAppDocumentBuilder.ATTRIBUTENAME_NAME);
		String id = RMAppDocumentBuilder.getAttributeValue(att, RMAppDocumentBuilder.ATTRIBUTENAME_ID);

		String type = RMAppDocumentBuilder.getAttributeValue(att, RMAppDocumentBuilder.ATTRIBUTENAME_TYPE);

		Attribute cacheAtt = (Attribute) dataMap.get(id);
		String value = cacheAtt == null ? "" : (String) cacheAtt.getValue();

		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

		StringBuffer namesb = new StringBuffer("<html>").append(name);
		if (description != null && !"".equals(description.trim())) {
		    namesb.append("<br>(").append(description).append(")");
		}
		namesb.append("</html>");
		JLabel label = new JLabel(namesb.toString());

		if ("textarea".equalsIgnoreCase(type)) {
		    JTextArea area = new JTextArea("", 5, getComponentWidth(att));
		    area.setName(id);
		    area.setText(value);
		    area.setLineWrap(false);
		    area.setWrapStyleWord(true);
		    inputSet.add(area);
		    p.add(area);
		    rows += 5;
		    rows2 += 5;
		} else if ("selection".equals(type)) {
		    JComboBox c = new JComboBox();
		    c.setName(id);
		    NodeList options = att.getChildNodes();
		    int opSelectedIndex = 0;
		    int opIndex = 0;
		    for (int oi = 0; oi < options.getLength(); oi++) {
			Node option = options.item(oi);
			if (!RMGraphFactory.OPTIONENAME.equals(option.getNodeName())) {
			    continue;
			}
			String optionName = RMAppDocumentBuilder.getAttributeValue(option,
				RMAppDocumentBuilder.ATTRIBUTENAME_NAME);
			if (value.equals(optionName)) {
			    opSelectedIndex = opIndex;
			}
			opIndex++;
			c.addItem(optionName);
		    }
		    int width = getComponentWidth(att) <= 100 ? 100 : getComponentWidth(att);
		    int height = getComponentHeight(att) <= 20 ? 20 : getComponentHeight(att);

		    c.setPreferredSize(new Dimension(width, height));
		    c.setSelectedIndex(opSelectedIndex);
		    inputSet.add(c);
		    p.add(c);
		    rows++;
		    rows2++;
		} else if ("checkbox".equals(type)) {
		    JCheckBox checkBox = new JCheckBox();
		    checkBox.setName(id);
		    if ("1".equals(value)) {
			checkBox.setSelected(true);
		    } else {
		    }
		    inputSet.add(checkBox);
		    p.add(checkBox);
		    rows++;
		    rows2++;
		} else {
		    JTextField area = new JTextField("", getComponentWidth(att));
		    area.setName(id);
		    area.setText(value);
		    inputSet.add(area);
		    p.add(area);
		    rows++;
		    rows2++;
		}

		fixedPanel.add(label);
		fixedPanel.add(p);
	    }
	    System.out.println(" rows" + rows);
	    System.out.println(" rows 2" + rows2);
	    SpringUtilities.makeCompactGrid(fixedPanel, // parent
		    rows2, 2,// rows,columns
		    3, 3, // initX, initY
		    3, 3); // xPad, yPad

	}

	// ===================================================================

	// if (rows > 15) {
	// dynamicPanel.setPreferredSize(new Dimension(400, 400));
	// }
	dynamicPanel.setAutoscrolls(true);
	dynamicPanel.setOpaque(true);

	// if ("Y".equals(cell.getFromDataCache())) {
	//
	// dynamicPanel.getViewport().add(upanel);
	//
	// } else if ("M".equals(cell.getFromDataCache())) {

	dynamicPanel.getViewport().add(upanel);

	// } else {
	// dynamicPanel.getViewport().add(fixedPanel);
	// }

	bigPanel.add(dynamicPanel);
	bigPanel.add(fixedPanel);
	// add buttons
	bigPanel.add(generateButtonsPanel(cell, dialog));

	bigPanel.setOpaque(true); // content panes must be opaque
	dialog.setContentPane(bigPanel);

    }

    /**
     * 
     * @param cell
     * @param dialog
     * @return
     */
    protected JPanel generateButtonsPanel(RMGraphCell cell, final RMAppParamDialog dialog) {
	// layout
	Map dataMap = cell.getDataMap();
	dialog.setTitle("Parameters for " + cell.getName());
	dialog.setAlwaysOnTop(true);

	dialog.setModal(true);
	// dialog.setLocation( RMAppContext.getRMApp().getX() + 150,
	// RMAppContext.getRMApp().getY() + 200);

	JButton btnOkay = new JButton("OK");
	btnOkay.setActionCommand("OK");

	// Action for OK
	btnOkay.addActionListener(new ButtonClickListener(cell));

	JButton btnCancel = new JButton("Cancel");
	// btnCancel.setActionCommand("CANCEL");
	btnCancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		// dialog.setActionComand(e.getActionCommand());
		dialog.dispose();

	    }
	});

	JPanel optionPanel = new JPanel();
	optionPanel.setLayout(new FlowLayout());
	optionPanel.add(btnOkay);
	optionPanel.add(btnCancel);
	return optionPanel;

    }

    private class ButtonClickListener implements ActionListener {
	RMGraphCell cell;

	protected ButtonClickListener(RMGraphCell cell) {
	    this.cell = cell;
	}

	public void actionPerformed(ActionEvent e) {
	    Map<String, Attribute> dataMap = cell.getDataMap();
	    Iterator areaIterator = inputSet.iterator();
	    List<String> enList = new ArrayList<String>();
	    List<String> exList = new ArrayList<String>();

	    while (areaIterator.hasNext()) {
		Object ob = areaIterator.next();
		String key = null;
		String value = "";

		if (ob instanceof JTextArea) {
		    JTextArea area = (JTextArea) ob;
		    key = area.getName();
		    value = area.getText();
		} else if (ob instanceof JTextField) {
		    JTextField af = (JTextField) ob;
		    key = af.getName();
		    value = af.getText();
		} else if (ob instanceof JComboBox) {
		    JComboBox box = (JComboBox) ob;
		    key = box.getName();
		    value = (String) box.getSelectedItem();
		} else if (ob instanceof JCheckBox) {
		    JCheckBox box = (JCheckBox) ob;

		    if ("Y".equals(cell.getFromDataCache()) || "M".equals(cell.getFromDataCache())) {

			if (box.isSelected()) {
			    String name = box.getName();
			    if (null != name && name.startsWith("EN_")) {
				value = name.substring(3);
				enList.add(value.trim());

			    } else if (null != name && name.startsWith("EX_")) {
				value = name.substring(3);
				exList.add(value.trim());
			    }
			}

		    } else {
			key = box.getName();
			value = box.isSelected() ? "1" : "0";
		    }

		}

		Attribute attribute = (Attribute) dataMap.get(key);
		if (attribute == null) {
		    attribute = new Attribute();
		    attribute.setId(key);
		}
		attribute.setValue(value);
		dataMap.put(key, attribute);
		// RMLogger.debug("=========> ob:" + ob.getClass());
		RMLogger.debug("=========> key:" + key + "  value:" + value);
	    }

	    dataMap.put("ENDO", null);
	    dataMap.put("EXO", null);
	    if (null != enList && enList.size() > 0) {
		Attribute att = dataMap.get("ENDO") == null ? new Attribute() : (Attribute) dataMap.get("ENDO");
		att.setValue(enList);
		dataMap.put("ENDO", att);
	    }

	    if (null != exList && exList.size() > 0) {
		Attribute att = dataMap.get("EXO") == null ? new Attribute() : (Attribute) dataMap.get("EXO");
		att.setValue(exList);
		dataMap.put("EXO", att);
	    }
	    cell.setDataMap(dataMap);
	    dialog.dispose();

	    if (cell.getKey().equals(RDataComponent.TYPE_DATA_MODEL_SEL)
		    || cell.getKey().equals(RDataComponent.TYPE_DATA_MODEL_IMP)) {
		RDataCache.getIntance().initial(true);
	    }

	}

    }

}
