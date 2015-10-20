package com.rm.app.validate;

import java.util.ArrayList;
import java.util.List;

import com.rm.app.RMResources;

public class RMFlowValidationMessage {

	public static String KEY = "Validation.Error.";
	
	public static String UNKNOWN="unknow";
	
	public static String INCOMPLETE="incomplete";
	
	public static String INVALID="invalid";
	
	public static String INCORRECT_RELATOINSHIP="incorrect.relationship";
	
	public static String INCORRECT_LOGIC="incorrect.logic";
	
	public static String INCOMPLETE_FLOW="incomplete.flow";
	
	public static String INVALID_RELATIONHSIP="invalid.relationship";
	
	public static String INVALID_COMPONENT="invalid.component";
	
	public static String INVALID_STARTEND="invalid.startend";
	
	public static String INCORRECT_DUPLICATE="incorrect.duplicate";
	
//	public static String RELATIONSHIP="relationship";

	public static String TYPE_INVALID_FLOW = "[Invalid Flow]";

	public static String TYPE_INCOMPLETE_FLOW = "[Incomplete Flow]";

	public static String TYPE_INVALID_COMPONENT = "[Invalid Component]";

	public static String TYPE_INVALID_RELATIONSHIP = "[Invalid Relationship]";

	public static String TYPE_INVALID_R_FUNCTION = "[Invalid R Function]";

	public static String TYPE_INCORRECT_LOGIC = "[Incorrect Logic]";

	public static String TYPE_UNKNOW_ERROR = "[Unknow Error]";
	
	public static String TYPE_INCORRECT_RELATIONSHIP="[Incorrect Relationship]";
	


	private static List<String> msgList = new ArrayList<String>();

	public static void setErrorMessage(String key, Object[] params,
			String errorType) {
		String errorMsg = errorType + " " + RMResources.getString(key, params);

		if (msgList == null)
		    errorMsg = TYPE_INVALID_FLOW + " " + RMResources.getString(KEY+INVALID, null);
		 
		msgList.add((msgList.size()), errorMsg);
	
	}

	public static String getErrorMessage() {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < msgList.size(); i++) {
			if ( i == msgList.size()-1 )
			    buffer.append((String) msgList.get(i));
			else
			    buffer.append((String) msgList.get(i)).append("\n");
		}

		return buffer.toString();

	}

	public static void cleanErrorMessage() {
		msgList.removeAll(msgList);
	}

}
