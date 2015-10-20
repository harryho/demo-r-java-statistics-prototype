package com.rm.app.ui.tool;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import com.rm.app.r.component.data.RDataImportComponent;
import com.rm.app.ui.RMAppParamDialog;
import com.rm.app.util.SpringUtilities;

public class RMImportDataDialogBuilder extends RMParamDialogBuilder {

	public void buildDialogLayout(RMGraphCell cell,
			final RMAppParamDialog dialog) throws Exception {

		this.dialog = dialog;

		JPanel bigPanel = new JPanel();
		bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.Y_AXIS));
		final JPanel upanel = new JPanel(new SpringLayout());

		Document doc = RMAppDocumentBuilder.parse(RMResources
				.getInputStream(RMGraphFactory.PATH_RMCONFIG));
		Node compModelConfiguration = doc.getDocumentElement();

		Node comNode = getNode(compModelConfiguration, cell.getKey());

		NodeList attList = comNode.getChildNodes();
		int rows = 0;

		String dataTypeValue = cell
				.getParaStringValue(RDataImportComponent.ATTR_DATA_MODEL_TYPE);
		dataTypeValue = dataTypeValue == null ? "1" : dataTypeValue;

		// add content panel
		final JComponent rawDataUI = generateRawDataUI(cell, dialog);
		final JComponent corDataUI = generateCorrectionDataUI(cell, dialog);

		if ("Correlation matrix".equals(dataTypeValue)
				|| "Covariance matrix".equals(dataTypeValue)) {
			corDataUI.setVisible(true);
			rawDataUI.setVisible(false);
		} else {// raw data
			corDataUI.setVisible(false);
			rawDataUI.setVisible(true);
		}

		for (int i = 0, k = attList.getLength(); i < k; i++) {
			Node att = attList.item(i);
			if (!RMGraphFactory.ATTRIBUTENAME.equals(att.getNodeName())) {
				continue;
			}
			rows++;
			String description = RMAppDocumentBuilder.getAttributeValue(att,
					RMAppDocumentBuilder.ATTRIBUTENAME_DISCRIPTION);
			String name = RMAppDocumentBuilder.getAttributeValue(att,
					RMAppDocumentBuilder.ATTRIBUTENAME_NAME);
			String id = RMAppDocumentBuilder.getAttributeValue(att,
					RMAppDocumentBuilder.ATTRIBUTENAME_ID);
			String type = RMAppDocumentBuilder.getAttributeValue(att,
					RMAppDocumentBuilder.ATTRIBUTENAME_TYPE);

			String value = cell.getParaStringValue(id);

			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

			StringBuffer namesb = new StringBuffer("<html>").append(name);
			if (description != null && !"".equals(description.trim())) {
				namesb.append("<br>(").append(description).append(")");
			}
			namesb.append("</html>");

			JLabel label = new JLabel(namesb.toString());

			if ("textarea".equalsIgnoreCase(type)) {
				JTextArea area = new JTextArea("", getComponentHeight(att),
						getComponentWidth(att));
				area.setName(id);
				area.setText(value);
				area.setLineWrap(false);
				area.setWrapStyleWord(true);
				inputSet.add(area);
				p.add(new JScrollPane(area));
			} else if ("selection".equals(type)) {
				JComboBox c = new JComboBox();
				c.setName(id);
				NodeList options = att.getChildNodes();
				int opSelectedIndex = 0;
				int opIndex = 0;
				for (int oi = 0; oi < options.getLength(); oi++) {
					Node option = options.item(oi);
					if (!RMGraphFactory.OPTIONENAME
							.equals(option.getNodeName())) {
						continue;
					}
					if (RMAppDocumentBuilder.getAttributeValue(option,
							RMAppDocumentBuilder.ATTRIBUTENAME_NAME).equals(
							dataTypeValue)) {
						opSelectedIndex = opIndex;
					}
					opIndex++;
					c.addItem(RMAppDocumentBuilder.getAttributeValue(option,
							RMAppDocumentBuilder.ATTRIBUTENAME_NAME));
				}

				c.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {

					}
				});

				ItemListener itemListener = new ItemListener() {
					public void itemStateChanged(ItemEvent itemEvent) {
						int state = itemEvent.getStateChange();
						if (state == ItemEvent.SELECTED) {
							if ("Correlation matrix"
									.equals(itemEvent.getItem())
									|| "Covariance matrix".equals(itemEvent
											.getItem())) {
								corDataUI.setVisible(true);
								rawDataUI.setVisible(false);
							} else {
								corDataUI.setVisible(false);
								rawDataUI.setVisible(true);
							}
						}
						dialog.repaint();
						dialog.pack();
					}
				};
				c.setSelectedIndex(opSelectedIndex);
				c.addItemListener(itemListener);
				inputSet.add(c);
				p.add(new JScrollPane(c));
			} else {
				JTextField field = new JTextField("", getComponentWidth(att));
				field.setName(id);
				field.setText(value);
				inputSet.add(field);
				p.add(new JScrollPane(field));
			}

			upanel.add(label);
			label.setLabelFor(p);
			upanel.add(p);

		}

		SpringUtilities.makeCompactGrid(upanel, // parent
				rows, 2,// rows,columns
				3, 3, // initX, initY
				3, 3); // xPad, yPad

		bigPanel.add(upanel);

		bigPanel.add(rawDataUI);
		bigPanel.add(corDataUI);

		// add buttons
		bigPanel.add(generateButtonsPanel(cell, dialog));

		bigPanel.setOpaque(true); // content panes must be opaque
		dialog.setContentPane(bigPanel);


	}

	private JComponent generateCorrectionDataUI(final RMGraphCell cell,
			final RMAppParamDialog dialog) {
		JComponent component = new JPanel(new SpringLayout());

		// Number of Observations
		JLabel label1 = new JLabel(
				RDataImportComponent.ATTR_DATA_MODEL_PARA_NOOBSERVATIONS);

		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField field1 = new JTextField("", getComponentWidth(null));
		String value = cell
				.getParaStringValue(RDataImportComponent.ATTR_DATA_MODEL_PARA_NOOBSERVATIONS);
		field1.setName(RDataImportComponent.ATTR_DATA_MODEL_PARA_NOOBSERVATIONS);
		field1.setText(value == null ? "" : value);
		inputSet.add(field1);

		p.add(new JScrollPane(field1));
		component.add(label1);
		label1.setLabelFor(p);
		component.add(p);

		int row = 1;

		for (int i = 1; i <= 10; i++) {
			p = new JPanel(new FlowLayout(FlowLayout.LEFT));

			JLabel label = new JLabel(
					RDataImportComponent.ATTR_DATA_MODEL_PARA_VAR_DISPLAY_VALUE + i);
			JTextField field = new JTextField("", getComponentWidth(null));
			field.setName(RDataImportComponent.ATTR_DATA_MODEL_PARA_VAR + i);
			value = cell
					.getParaStringValue(RDataImportComponent.ATTR_DATA_MODEL_PARA_VAR
							+ i);

			field.setText(value == null ? "" : value);
			inputSet.add(field);

			p.add(new JScrollPane(field));

			component.add(label);
			label.setLabelFor(p);
			component.add(p);
			row++;
		}

		JButton inputMatrixBut = new JButton("Imput Matrix Data");
		inputMatrixBut.addActionListener(new  RMImportDataMatrixWindow(cell,this.dialog));
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label2 = new JLabel("");
		p.add(new JScrollPane(inputMatrixBut));
		component.add(label2);
		component.add(p);
		row++;
		
		SpringUtilities.makeCompactGrid(component, // parent
				row, 2,// rows,columns
				3, 3, // initX, initY
				3, 3); // xPad, yPad

		return component;
	}

	
	private JComponent generateRawDataUI(final RMGraphCell cell,
			final RMAppParamDialog dialog) {

		JComponent component = new JPanel(new SpringLayout());

		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label1 = new JLabel("Data location");
		JTextField field1 = new JTextField("", getComponentWidth(null));
		Attribute attribute1 = (Attribute) cell.getDataMap().get(
				RDataImportComponent.ATTR_DATA_MODEL_LOCATION);
		String value1 = attribute1 == null ? "" : (String) attribute1
				.getValue();
		field1.setName(RDataImportComponent.ATTR_DATA_MODEL_LOCATION);
		field1.setText(value1 == null ? "" : value1);
		inputSet.add(field1);
		p.add(new JScrollPane(field1));
		component.add(label1);
		label1.setLabelFor(p);
		component.add(p);

		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label2 = new JLabel("Data name");
		JTextField field2 = new JTextField("", getComponentWidth(null));
		String value2 = cell
				.getParaStringValue(RDataImportComponent.ATTR_DATA_MODEL_DATA_NAME);
		field2.setName(RDataImportComponent.ATTR_DATA_MODEL_DATA_NAME);
		field2.setText(value2 == null ? "" : value2);
		inputSet.add(field2);
		p.add(new JScrollPane(field2));
		component.add(label2);
		label2.setLabelFor(p);
		component.add(p);

		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label3 = new JLabel("Separation");
		JTextField field3 = new JTextField("", getComponentWidth(null));
		String value3 = cell
				.getParaStringValue(RDataImportComponent.ATTR_DATA_MODEL_SEPERATION);
		field3.setName(RDataImportComponent.ATTR_DATA_MODEL_SEPERATION);
		field3.setText(value3 == null ? "" : value3);
		inputSet.add(field3);
		p.add(new JScrollPane(field3));
		component.add(label3);
		label3.setLabelFor(p);
		component.add(p);

		SpringUtilities.makeCompactGrid(component, // parent
				3, 2,// rows,columns
				3, 3, // initX, initY
				3, 3); // xPad, yPad

		return component;
	}

}
