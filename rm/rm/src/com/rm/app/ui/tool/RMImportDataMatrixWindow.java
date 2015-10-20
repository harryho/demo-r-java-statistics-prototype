package com.rm.app.ui.tool;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.rm.app.graph.Attribute;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.data.RDataImportComponent;
import com.rm.app.ui.RMAppParamDialog;
import com.rm.app.util.SpringUtilities;

public class RMImportDataMatrixWindow extends JDialog implements ActionListener {

	private static final long serialVersionUID = -1302783470727637771L;
	RMGraphCell cell;
	RMAppParamDialog parentDialog;

	private Set inputSet = null;

	protected RMImportDataMatrixWindow(RMGraphCell cell,
			RMAppParamDialog parentDialog) {
		this.cell = cell;
		this.parentDialog = parentDialog;
		inputSet = new HashSet();
	}

	public void actionPerformed(ActionEvent e) {
		parentDialog.setVisible(false);
		setTitle("Matrix inputed window for " + cell.getName());
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) / 2 - 200);
		int y = (int) ((dimension.getHeight() - getHeight()) / 2 - 200);
		if (x <= 0 || y <= 0) {
			this.setSize(parentDialog.getSize());
		} else {
			setLocation(x, y);
		}

		// display content begin
		JPanel bigPanel = new JPanel();
		bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.Y_AXIS));
		final JPanel upanel = new JPanel(new SpringLayout());
		JComponent component = new JPanel(new SpringLayout());
		int rows = 0;
		JPanel p = null;

		for (int i = 1; i <= 10; i++) {

			p = new JPanel(new FlowLayout(FlowLayout.LEFT));
			JLabel label = new JLabel(
					RDataImportComponent.ATTR_DATA_MODEL_PARA_VAR_DISPLAY_VALUE
							+ i);
			JTextField field = new JTextField("", RMParamDialogBuilder
					.getComponentWidth(null));
			field
					.setName(RDataImportComponent.ATTR_DATA_MODEL_PARA_DATA_INITVAR
							+ i);
			String value = cell
					.getParaStringValue(RDataImportComponent.ATTR_DATA_MODEL_PARA_DATA_INITVAR
							+ i);

			field.setText(value == null ? "" : value);
			inputSet.add(field);

			p.add(new JScrollPane(field));

			component.add(label);
			label.setLabelFor(p);
			component.add(p);
			rows++;
		}
		JLabel label2 = new JLabel(
				RDataImportComponent.ATTR_DATA_MODEL_PARA_MATRIX_DATA);
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JTextArea area = new JTextArea("", RMParamDialogBuilder
				.getComponentHeight(null), RMParamDialogBuilder
				.getComponentWidth(null));
		area.setName(RDataImportComponent.ATTR_DATA_MODEL_PARA_MATRIX_DATA);
		area
				.setText(cell
						.getParaStringValue(RDataImportComponent.ATTR_DATA_MODEL_PARA_MATRIX_DATA));
		area.setLineWrap(false);
		area.setWrapStyleWord(true);
		inputSet.add(area);

		p.add(new JScrollPane(area));
		// label2.setLabelFor(p);
		component.add(label2);
		component.add(p);
		rows++;
		// display content end

		SpringUtilities.makeCompactGrid(component, // parent
				rows, 2,// rows,columns
				3, 3, // initX, initY
				3, 3); // xPad, yPad

		bigPanel.add(component);

		// add buttons
		bigPanel.add(generateButtonsPanel(this));

		bigPanel.setOpaque(true); // content panes must be opaque
		setContentPane(bigPanel);

		setResizable(false);
		this.setAlwaysOnTop(true);
		pack();
		setVisible(true);
	}

	protected JPanel generateButtonsPanel(final JDialog dialog) {

		JButton btnOkay = new JButton("OK");

		// Action for OK
		btnOkay.addActionListener(new ButtonClickListener(dialog));

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inputSet = new HashSet();
				dialog.dispose();
				parentDialog.setVisible(true);
			}
		});

		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new FlowLayout());
		optionPanel.add(btnOkay);
		optionPanel.add(btnCancel);
		return optionPanel;

	}

	private class ButtonClickListener implements ActionListener {
		JDialog dialog;

		protected ButtonClickListener(JDialog dialog) {
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e) {
			Map<String, Attribute> dataMap = cell.getDataMap();
			RMLogger.debug("begin click ok: dataMap= " + dataMap);
			RMLogger.debug("inputSet= " + inputSet);
			Iterator areaIterator = inputSet.iterator();

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
				}

				RMLogger.debug("Key= " + key + " value=" + value);

				Attribute attribute = (Attribute) dataMap.get(key);
				if (attribute == null) {
					attribute = new Attribute();
					attribute.setId(key);
				}
				attribute.setValue(value);
				dataMap.put(key, attribute);
			}

			cell.setDataMap(dataMap);
			RMLogger.debug("after click ok: dataMap= " + dataMap);
			inputSet = new HashSet();
			dialog.dispose();
			//
			// setValueToParent();
			// parentDialog.repaint();
			// parentDialog.pack();
			parentDialog.setVisible(true);
		}

		private void setValueToParent() {
			Map<String, Attribute> dataMap = cell.getDataMap();
			for (int i = 0; i < 10; i++) {
				String value = cell
						.getParaStringValue(RDataImportComponent.ATTR_DATA_MODEL_PARA_VAR
								+ i);
				if (value == null || "".equals(value.trim())) {
					continue;
				} else { // if user have input,then don't set value to any
					// field.
					return;
				}
			}

			Component[] components = parentDialog.getContentPane()
					.getComponents();

			if (components != null) {
				for (int k = 0; k < components.length; k++) {
					Component com = components[k];
					RMLogger.debug("Component------------> " + com.getName());
					for (int i = 0; i < 10; i++) {
						String key = RDataImportComponent.ATTR_DATA_MODEL_PARA_VAR
								+ i;
						if (!key.equals(com.getName())) {
							continue;
						}
						Attribute attribute = dataMap.get(key);
						String value = cell
								.getParaStringValue(RDataImportComponent.ATTR_DATA_MODEL_PARA_DATA_INITVAR
										+ i);
						if (attribute == null) {
							attribute = new Attribute();
							attribute.setId(key);
						}
						JTextField jl = (JTextField) com;
						jl.setText(value);
						attribute.setValue(value);
						dataMap.put(key, attribute);
					}
				}
			}

		}
	}

}
