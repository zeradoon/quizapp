package com.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.common.CommonUtil;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class represent the validation for required fields only.
 */
public class RequiredFieldValidator extends AbstractValidator {
	
	/**
	 * member variable to represent the required field object.
	 */
	private Object m_Source;
	
	/**
	 * member variable to represent the matching pattern for object.
	 */
	private String m_RegEx;
	
	/**
	 * Default constructor
	 */
	public RequiredFieldValidator(EditText source) {
		super(true);
		m_Source = source;
	}
	
	/**
	 * Default constructor
	 */
	public RequiredFieldValidator(EditText source, String requiredMessage) {
		super(true);
		m_Source = source;
		m_RequiredMessage = requiredMessage;
	}
	
	public RequiredFieldValidator(EditText source, String requiredMessage, String regEx) {
		super(true);
		m_Source = source;
		m_RequiredMessage = requiredMessage;
		m_RegEx = regEx;
	}
	
	public RequiredFieldValidator(Object source, String requiredMessage, String regEx) {
		super(true);
		m_Source = source;
		m_RequiredMessage = requiredMessage;
		m_RegEx = regEx;
	}

	/**
	 * This method return the source object to be validated.
	 * 
	 * @return source object.
	 */
	@Override
	public Object getSource() {
		return m_Source;
	}
	
	/**
	 * This method perform the required field validation on the object.
	 * 
	 * @return validation result wrapped into validationresult object.
	 */
	@Override
	public ValidationResult validate() {
		ValidationResult l_VR = null;
		String l_SourceVal = "";
		String l_ObjClass = "";
		
		if (m_Source.getClass().getName().equalsIgnoreCase(CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT_CLASSNAME)) {
			l_SourceVal = ((EditText)m_Source).getText().toString();
			l_ObjClass = CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT;
		}
		else if (m_Source.getClass().getName().equalsIgnoreCase(CommonUtil.SCREEN_CONTROL_TYPE_TEXT_VIEW_CLASSNAME)) {
			l_SourceVal = ((TextView)m_Source).getText().toString();
			l_ObjClass = CommonUtil.SCREEN_CONTROL_TYPE_TEXT_VIEW;
		}
		
		if (m_RegEx != null) {
			Pattern pattern = Pattern.compile(m_RegEx);
			Matcher matcher = pattern.matcher(l_SourceVal);
			
			if (matcher.matches()) {
				//l_VR = new ValidationResult(true, "");
			} else {
				l_VR = new ValidationResult(false, m_RequiredMessage, m_Source, l_ObjClass);
				//((EditText)m_Source).setError(m_RequiredMessage);
			}
		} else if (l_SourceVal.length() == 0 || l_SourceVal.length() > 20) {
			l_VR = new ValidationResult(false, m_RequiredMessage, m_Source, l_ObjClass);
			//((EditText)m_Source).setError("Field cannot be blank.");
		}
		
		return l_VR;
	}

}