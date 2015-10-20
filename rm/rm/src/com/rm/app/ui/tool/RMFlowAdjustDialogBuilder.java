package com.rm.app.ui.tool;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rm.app.graph.RMGraphFactory;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.ui.RMFlowRunningDialog;

public class RMFlowAdjustDialogBuilder {
	RFlowProperties properties;
	public RMFlowAdjustDialogBuilder(RFlowProperties properties){
		this.properties = properties;
	}

	/**
	 * get node from rm.xml by component key
	 * 
	 * @param node
	 * @param key
	 * @return
	 */
	protected Node getNode(Node node, String key) {
		NodeList cnodes = node.getChildNodes();
		Node ret = null;
		for (int i = 0; i < cnodes.getLength(); i++) {
			Node n = cnodes.item(i);
			if (RMGraphFactory.NODENAME_COMPONENT.equals(n.getNodeName())) {
				if (key.equals(RMAppDocumentBuilder.getAttributeValue(n,
						RMAppDocumentBuilder.ATTRIBUTENAME_KEY))) {
					return n;

				}
			}
			ret = getNode(n, key);
			if (ret != null) {
				return ret;
			}
		}
		return ret;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	protected int getComponentWidth(Node node) {
		int width = 30;
		try {
			if (node != null) {
				String tmp = RMAppDocumentBuilder.getAttributeValue(node,
						RMAppDocumentBuilder.ATTRIBUTENAME_WIDTH);
				width = Integer.parseInt(tmp);
			}
		} catch (Exception e) {
			width = 30;
		}
		return width;
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	protected int getComponentHeight(Node node) {
		int height = 5;
		try {
			if (node != null) {
				String tmp = RMAppDocumentBuilder.getAttributeValue(node,
						RMAppDocumentBuilder.ATTRIBUTENAME_HEIGHT);
				height = Integer.parseInt(tmp);
			}

		} catch (Exception e) {
			height = 5;
		}
		return height;
	}

	private class ButtonClickListener implements ActionListener {
		RComponent rcmpt;

		protected ButtonClickListener(RComponent rcmpt) {
			this.rcmpt = rcmpt;
		}

		public void actionPerformed(ActionEvent e) {
//		    
//			Map<String, Attribute> dataMap = rcmpt.getDataMap();
//			Iterator areaIterator = inputSet.iterator();
//			List<String> deList = new ArrayList<String>();
//			List<String> inList = new ArrayList<String>();
//
//			while (areaIterator.hasNext()) {
//				Object ob = areaIterator.next();
//				String key = null;
//				String value = "";
//
//				if (ob instanceof JTextArea) {
//					JTextArea area = (JTextArea) ob;
//					key = area.getName();
//					value = area.getText();
//				} else if (ob instanceof JTextField) {
//					JTextField af = (JTextField) ob;
//					key = af.getName();
//					value = af.getText();
//				} else if (ob instanceof JComboBox) {
//					JComboBox box = (JComboBox) ob;
//					key = box.getName();
//					value = (String) box.getSelectedItem();
//				} else if (ob instanceof JCheckBox) {
//					JCheckBox box = (JCheckBox) ob;
//
//					if ("Y".equals(rcmpt.getFromDataCache())
//							|| "M".equals(rcmpt.getFromDataCache())) {
//
//						if (box.isSelected()) {
//							String name = box.getName();
//							if (null != name && name.startsWith("DE_")) {
//								value = name.substring(3);
//								deList.add(value);
//
//							} else if (null != name && name.startsWith("IN_")) {
//								value = name.substring(3);
//								inList.add(value);
//							}
//						}
//
//					} else {
//						key = box.getName();
//						value = box.isSelected() ? "1" : "0";
//					}
//
//				}
//
//				Attribute attribute = (Attribute) dataMap.get(key);
//				if (attribute == null) {
//					attribute = new Attribute();
//					attribute.setId(key);
//				}
//				attribute.setValue(value);
//				dataMap.put(key, attribute);
//				// RMLogger.debug("=========> ob:" + ob.getClass());
//				RMLogger.debug("=========> key:" + key + "  value:" + value);
//			}
//			
//			
//			dataMap.put("DEPENDENT", null);
//			dataMap.put("INDEPENDENT", null);
//			if (null != deList && deList.size() > 0) {
//				Attribute att = dataMap.get("DEPENDENT") == null ? new Attribute()
//						: (Attribute) dataMap.get("DEPENDENT");
//				att.setValue(deList);
//				dataMap.put("DEPENDENT", att);
//			}
//
//			if (null != inList && inList.size() > 0) {
//				Attribute att = dataMap.get("INDEPENDENT") == null ? new Attribute()
//						: (Attribute) dataMap.get("INDEPENDENT");
//				att.setValue(inList);
//				dataMap.put("INDEPENDENT", att);
//			}
//			rcmpt.setDataMap(dataMap);
//			dialog.dispose();
//
//			if (rcmpt.getKey().equals(RDataComponent.TYPE_DATA_MODEL_SEL)
//					|| rcmpt.getKey().equals(RDataComponent.TYPE_DATA_MODEL_IMP)) {
//				DataCache.getIntance().initial(true);
//			}

		}

	}

	/**
	 * 
	 * @param rcmpt
	 * @param dialog
	 * @return
	 */
	protected JPanel generateButtonsPanel(RComponent rcmpt,
			final RMFlowRunningDialog dialog) {
		// layout
//		Map dataMap = rcmpt.getDataMap();
//		dialog.setTitle("Parameters for " + rcmpt.getName());
		dialog.setAlwaysOnTop(true);

		dialog.setModal(true);
//		dialog.setLocation(   RMAppContext.getRMApp().getX() + 150, RMAppContext.getRMApp().getY() + 200);
		
		JButton btnOkay = new JButton("OK");
		btnOkay.setActionCommand("OK");

		// Action for OK
		btnOkay.addActionListener(new ButtonClickListener( rcmpt));

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setActionCommand("CANCEL");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setActionComand(e.getActionCommand());

				dialog.dispose();

			}
		});

		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new FlowLayout());
		optionPanel.add(btnOkay);
		optionPanel.add(btnCancel);
		return optionPanel;

	}

	Set inputSet = new HashSet();
	RMFlowRunningDialog dialog = null;
	
	public void buildDialogLayout(  RComponent rcmpt,
			final RMFlowRunningDialog dialog) throws Exception {
		this.dialog = dialog;
	}
}
