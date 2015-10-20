package com.rm.app.ui.tool;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rm.app.RMResources;
import com.rm.app.graph.RMGraphFactory;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.component.explanation.RCusExComponent;
import com.rm.app.r.component.explanation.RSTDExComponent;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.ui.RMFlowRunningDialog;
import com.rm.app.util.SpringUtilities;

public class RMExCusDialogBuilder extends RMFlowAdjustDialogBuilder {
	RFlowProperties properties;
	public RMExCusDialogBuilder(RFlowProperties properties) {
		super(properties);
		this.properties = properties;
	}

	public void buildDialogLayout(RComponent rcmpt,
			final RMFlowRunningDialog dialog)
			throws Exception {
		this.dialog = dialog;

		JPanel bigPanel = new JPanel();
		bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.Y_AXIS));
		SpringLayout springLayout = new SpringLayout();

		final JPanel upanel = new JPanel(springLayout);

		final JScrollPane supanel = new JScrollPane();

		int rows = 0;

		JLabel vLabel = new JLabel("Customization Options");
		// JLabel deLabel = new JLabel(" Outliers ");
		JLabel inLabel = new JLabel("  Select  ");

		upanel.add(vLabel);
		// upanel.add(deLabel);
		upanel.add(inLabel);
		rows += 1;

		Document doc = RMAppDocumentBuilder.parse(RMResources
				.getInputStream(RMGraphFactory.PATH_RMCONFIG));
		Node compModelConfiguration = doc.getDocumentElement();

		Node comNode = getNode(compModelConfiguration, rcmpt.getKey());

		NodeList attList = comNode.getChildNodes();

		for (int i = 0, k = attList.getLength(); i < k; i++) {
			Node att = attList.item(i);
			if (!RMGraphFactory.ATTRIBUTENAME.equals(att.getNodeName())) {
				continue;
			}

			String description = RMAppDocumentBuilder.getAttributeValue(att,
					RMAppDocumentBuilder.ATTRIBUTENAME_DISCRIPTION);
			String name = RMAppDocumentBuilder.getAttributeValue(att,
					RMAppDocumentBuilder.ATTRIBUTENAME_NAME);
			String id = RMAppDocumentBuilder.getAttributeValue(att,
					RMAppDocumentBuilder.ATTRIBUTENAME_ID);

			String type = RMAppDocumentBuilder.getAttributeValue(att,
					RMAppDocumentBuilder.ATTRIBUTENAME_TYPE);

			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

			// String rowName = out.getRowName();
			// String outlier = out.getOutlier();

			JLabel lname = new JLabel(description);
			// JLabel lout = new JLabel(outlier.toString());

			JCheckBox checkBox = new JCheckBox();
			checkBox.setName(name);
			inputSet.add(checkBox);
			p.add(checkBox);
			rows++;

			upanel.add(lname);
			// upanel.add(lout);
			upanel.add(p);
		}

		SpringUtilities.makeCompactGrid(upanel, // parent
				rows, 2,// rows,columns
				3, 3, // initX, initY
				3, 3); // xPad, yPad

		// ===================================================================

		if (rows > 10) {
			supanel.setPreferredSize(new Dimension(400, 400));
		}
		supanel.setAutoscrolls(true);
		supanel.setOpaque(true);

		supanel.getViewport().add(upanel);

		bigPanel.add(supanel);

		// add buttons
		bigPanel.add(generateButtonsPanel(rcmpt, dialog));

		bigPanel.setOpaque(true); // content panes must be opaque
		dialog.setContentPane(bigPanel);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// rstudent.setNegligible(true);
				super.windowClosing(e);

			}
		});

	}

	protected JPanel generateButtonsPanel(RComponent rcmpt,
			final RMFlowRunningDialog dialog) {
		dialog.setAlwaysOnTop(true);

		dialog.setModal(true);
		JButton btnOkay = new JButton("OK");
		btnOkay.setActionCommand("OK");

		// Action for OK
		btnOkay.addActionListener(new ButtonClickListener(rcmpt));

		// Action for Continue
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setActionCommand("CANCEL");
		btnCancel.addActionListener(new ButtonCancelListener(rcmpt));

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

		JPanel optionPanel = new JPanel();
		optionPanel.setLayout(new FlowLayout());

		optionPanel.add(btnOkay);
		optionPanel.add(btnCancel);
		bottomPanel.add(optionPanel);
		return bottomPanel;

	}

	private class ButtonClickListener implements ActionListener {

		RCusExComponent rcmpt;

		protected ButtonClickListener(RComponent rcmpt) {
			this.rcmpt = (RCusExComponent) rcmpt;
		}

		public void actionPerformed(ActionEvent e) {

			Iterator areaIterator = inputSet.iterator();

			List<Integer> options = new ArrayList<Integer>();
			while (areaIterator.hasNext()) {
				Object ob = areaIterator.next();
				String key = null;
				String value = "";

				if (ob instanceof JCheckBox) {
					JCheckBox box = (JCheckBox) ob;
					if (box.isSelected()) {
						Integer integer = new Integer(box.getName());
						options.add(integer);
					}
				}
			}

			Collections.sort(options);
			if (options.size() > 0) {
				int[] cmds = new int[options.size() + 1];
				cmds[0] = 0;
				int i = 1;
				for (Integer in : options) {
					cmds[i] = in.intValue();
					System.out.println("cmd ---------------------" + cmds[i]);
					i++;
				}

				Arrays.sort(cmds);
				rcmpt.setCmds(cmds);
			} else {
				rcmpt.setCmds(RSTDExComponent.getCmds(properties.getEqType()));
			}
			dialog.dispose();
		}
	}

	private class ButtonCancelListener implements ActionListener {
		RCusExComponent rcmpt;

		protected ButtonCancelListener(RComponent rcmpt) {
			this.rcmpt = (RCusExComponent) rcmpt;
		}

		public void actionPerformed(ActionEvent e) {
			rcmpt.setCmds(RSTDExComponent.getCmds(properties.getEqType()));
			dialog.dispose();
		}
	}

}
