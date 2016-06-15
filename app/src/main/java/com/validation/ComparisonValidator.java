package com.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.common.CommonUtil;

import android.widget.EditText;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class represent the validation for comparing data of two fields .
 */
public class ComparisonValidator extends AbstractValidator {
	
	/**
	 * member variable to represent the comparison object.
	 */
	private Object m_Source;
	
	/**
	 * member variable to represent the second comparison object.
	 */
	private Object m_Source2;
	
	/**
	 * member variable to represent the matching pattern for object.
	 */
	private String m_RegEx;
	
	/**
	 * Default error message
	 */
	protected String m_FaultMessage = "Field cannot be blank.";
	
	/**
	 * default message for required fields.
	 */
	protected String m_RequiredMessage = "The field is required";
	
	/**
	 * Default constructor
	 */
	public ComparisonValidator(EditText source, EditText secondSource) {
		super(true);
		m_Source = source;
		m_Source2 = secondSource;
	}
	
	/**
	 * Default constructor
	 */
	public ComparisonValidator(EditText source, EditText secondSource, String requiredMessage) {
		super(true);
		m_Source = source;
		m_Source2 = secondSource;
		m_RequiredMessage = requiredMessage;
	}
	
	public ComparisonValidator(EditText source, EditText secondSource, String requiredMessage, String regEx) {
		super(true);
		m_Source = source;
		m_Source2 = secondSource;
		m_RequiredMessage = requiredMessage;
		m_RegEx = regEx;
	}

	@Override
	public Object getSource() {
		return m_Source;
	}

	public Object getSecondSource() {
		return m_Source2;
	}
	
	/**
	 * This method perform the comparison field validation on the object.
	 * 
	 * @return validation result wrapped into validationresult object.
	 */
	@Override
	public ValidationResult validate() {
		ValidationResult l_VR = null;
		String l_Source = ((EditText)m_Source).getText().toString();
		String l_Source2 = ((EditText)m_Source2).getText().toString();
		
		if (m_RegEx != null) {
			Pattern pattern = Pattern.compile(m_RegEx);
			Matcher matcher = pattern.matcher(l_Source);
			Matcher matcher2 = pattern.matcher(l_Source2);
			
			if (matcher.matches() ) {
				if (matcher2.matches()) {
					if (l_Source.equalsIgnoreCase(l_Source2)) {
						//l_VR = new ValidationResult(true, "");
					}
					else {
						l_VR = new ValidationResult(false, CommonUtil.PASSWORDS_NOT_MATCH, m_Source, CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT);
					}
				}
				else {
					l_VR = new ValidationResult(false, m_RequiredMessage, m_Source2, CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT);
					//l_VR = new ValidationResult(false, "Passwords don't match");
					//((EditText)m_Source).setText(null);
					//((EditText)m_Source2).setText(null);
					//((EditText)m_Source).setError("Passwords don't match");
					//((EditText)m_Source).requestFocus();
				}
			} else {
				l_VR = new ValidationResult(false, m_RequiredMessage, m_Source, CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT);
				//l_VR = new ValidationResult(false, m_RequiredMessage);
				//((EditText)m_Source).setText(null);
				//((EditText)m_Source2).setText(null);
				//((EditText)m_Source).setError(m_RequiredMessage);
				//((EditText)m_Source).requestFocus();
			}
		} else {
			l_VR = new ValidationResult(false, CommonUtil.REQUIRED_MESSAGE, m_Source, CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT);
			//l_VR = new ValidationResult(false, "Field cannot be blank.");
			//((EditText)m_Source).setError("Field cannot be blank.");
		}
		
		return l_VR;
	}

}
