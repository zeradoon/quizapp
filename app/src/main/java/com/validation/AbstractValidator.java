package com.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class implements the Validator interface and some basic functions for validation.
 */
public abstract class AbstractValidator implements Validator {
	/**
	 * member variable to represent if validation is required or not.
	 */
	private boolean m_Required = false;	
	
	/**
	 * member variable to represent if object for validation is enabled or not.
	 */
	private boolean m_Enabled = true;
	
	/**
	 * Default error message
	 */
	protected String m_FaultMessage = "Validation failure";
	
	/**
	 * default message for required fields.
	 */
	protected String m_RequiredMessage = "The field is required";
	
	/**
	 * Default constructor
	 */
	public AbstractValidator() {}
	
	/**
	 * Default constructor.
	 */
	public AbstractValidator(boolean required) {
		m_Required = required;
	}
	
	/**
	 * Default constructor
	 */
	public AbstractValidator(boolean required, boolean enabled) {
		m_Required = required;
		m_Enabled = enabled;
	}
	
	/**
	 * This method store the validation results in the object.
	 * 
	 */
	public abstract ValidationResult validate(); 
	/*{
		ValidationResult l_Vr = null;
		if (m_Enabled) {
			if (m_Required && getSource() == null) {
				l_Vr = new ValidationResult(false, m_RequiredMessage);
			}
		} else {
			l_Vr = new ValidationResult(true, "");
		}
		
		return l_Vr;
	}*/
	
	/**
	 * This method quickly validate all validators at once.
	 * 
	 * @param validators an array of validators
	 * @return an array of validation failure results.
	 */
	public static List<ValidationResult> validateAll(List<Validator> validators) {
		List<ValidationResult> l_Result = new ArrayList<ValidationResult>();
		ValidationResult l_Vr = null;
		
		for (Validator v : validators) {
			l_Vr = v.validate();
			if (l_Vr != null && !l_Vr.isValid()) {
				l_Result.add(l_Vr);
			}
		}
		
		return l_Result;
	}
	
	public abstract Object getSource();
		
	public void setFaultMessage(String message) {
		m_FaultMessage = message;
	}
	
	public void setRequiredMessage(String message) {
		m_RequiredMessage = message;
	}

	public void setEnabled(boolean enabled) {
		m_Enabled = enabled;
	}
	
	public void setRequired(boolean required) {
		m_Required = required;
	}
	
	public boolean isEnabled() {
		return m_Enabled;
	}

	public boolean isRequired() {
		return m_Required;
	}

}

