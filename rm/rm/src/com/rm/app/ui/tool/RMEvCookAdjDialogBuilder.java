package com.rm.app.ui.tool;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import com.rm.app.r.component.RComponent;
import com.rm.app.r.component.evaluation.RCookComponent;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.ui.RMFlowRunningDialog;
import com.rm.app.util.SpringUtilities;

public class RMEvCookAdjDialogBuilder extends RMFlowAdjustDialogBuilder {

    public RMEvCookAdjDialogBuilder(RFlowProperties properties) {
		super(properties);
	}

	public void buildDialogLayout(RComponent rcmpt, final RMFlowRunningDialog dialog) throws Exception {
	this.dialog = dialog;
	// final Map<String, Attribute> dataMap = rcmpt.getDataMap();

	JPanel bigPanel = new JPanel();
	bigPanel.setLayout(new BoxLayout(bigPanel, BoxLayout.Y_AXIS));
	SpringLayout springLayout = new SpringLayout();

	final JPanel upanel = new JPanel(springLayout);

	final JScrollPane supanel = new JScrollPane();

	final RCookComponent rstudent = (RCookComponent) rcmpt;

	List outlierList = rstudent.getOutlierList();

	int rows = 0;

	JLabel vLabel = new JLabel("Row Name");
	JLabel deLabel = new JLabel(" Cook's Distance ");
	JLabel inLabel = new JLabel("  Remove  ");

	upanel.add(vLabel);
	upanel.add(deLabel);
	upanel.add(inLabel);
	rows += 1;

	for (int i = 0, k = outlierList.size(); i < k; i++) {
	    RCookComponent.Outlier out = (RCookComponent.Outlier) outlierList.get(i);

	    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

	    String rowName = out.getRowName();
	    String outlier = out.getOutlier();

	    JLabel lname = new JLabel(rowName.toString());
	    JLabel lout = new JLabel(outlier.toString());

	    JCheckBox checkBox = new JCheckBox();
	    checkBox.setName(out.getRowNum());
	    inputSet.add(checkBox);
	    p.add(checkBox);
	    rows++;

	    upanel.add(lname);
	    upanel.add(lout);
	    upanel.add(p);
	}

	SpringUtilities.makeCompactGrid(upanel, // parent
		rows, 3,// rows,columns
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
		rstudent.setNegligible(true);
	        super.windowClosing(e);
	        
	    }
	});
	
	dialog.setTitle("Cook's Distance");

    }

    protected JPanel generateButtonsPanel(RComponent rcmpt, final RMFlowRunningDialog dialog) {
	final RCookComponent rstudent = (RCookComponent) rcmpt;
	// dialog.setTitle("Parameters for " + rcmpt.rstudent());
	dialog.setAlwaysOnTop(true);

	dialog.setModal(true);
	JButton btnOkay = new JButton("OK");
	btnOkay.setActionCommand("OK");

	// Action for OK
	btnOkay.addActionListener(new ButtonClickListener(rcmpt));
	
	// Action for Continue
	JButton btnCancel = new JButton("Continue");
	btnCancel.setActionCommand("CONTINUE");
	btnCancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		// dialog.setActionComand(e.getActionCommand());
		rstudent.setNegligible(true);
		dialog.dispose();
	    }
	});

	JPanel bottomPanel = new JPanel();
	bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

	JPanel optionPanel = new JPanel();
	optionPanel.setLayout(new FlowLayout());

	JPanel extraPanel = new JPanel();
	extraPanel.setLayout(new FlowLayout());

//	JCheckBox checkBox = new JCheckBox();
//	checkBox.setName("IS_NEGLECT");
//	inputSet.add(checkBox);
	JLabel lable = new JLabel("<html>Please click button [Continue] if you want to continue <br>without removing.</html>");
	extraPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

//	extraPanel.add(checkBox);
	extraPanel.add(lable);
	extraPanel.setVisible(true);

	optionPanel.add(btnOkay);
	optionPanel.add(btnCancel);
	bottomPanel.add(extraPanel);
	bottomPanel.add(optionPanel);
	return bottomPanel;

    }

    private class ButtonClickListener implements ActionListener {
	RCookComponent rcmpt;

	protected ButtonClickListener(RComponent rcmpt) {
	    this.rcmpt = (RCookComponent) rcmpt;
	}

	public void actionPerformed(ActionEvent e) {

	    Iterator areaIterator = inputSet.iterator();
	    List<RCookComponent.Outlier> outList = rcmpt.getOutlierList();
	    while (areaIterator.hasNext()) {
		Object ob = areaIterator.next();
		String key = null;
		String value = "";

		if (ob instanceof JCheckBox) {
		    JCheckBox box = (JCheckBox) ob;

//		    if ("IS_NEGLECT".equals(box.getName())) {
//			if (box.isSelected())
//			    rcmpt.setNegligible(true);
//		    } else {
			for (RCookComponent.Outlier out : outList) {
			    if (out.getRowNum().equals(box.getName())) {
				if (box.isSelected())
				    out.setRemove(true);
			    }
			}
//		    }
		}
	    }
	    dialog.dispose();
	}
    }
}
