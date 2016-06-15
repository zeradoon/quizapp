package com.validation;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This interface is base class of validation framework for SKMS Quiz Application.
 */
public interface Validator {
	/**
	 * @return true if enabled
	 */
	boolean isEnabled();
	
	/**
	 * @return true if required
	 */
	boolean isRequired();
	
	/**
	 * This method set the Message to be displayed when the required validation is not met.
	 * @param message the error message
	 */
	void setRequiredMessage(String message);
	
	/**
	 * This method set the enable member variable.
	 * 
	 * @param enabled.
	 */
	void setEnabled(boolean enabled);
	
	/**
	 * This method set the required member variable.
	 * 
	 * @param required.
	 */
	void setRequired(boolean required);
	
	/**
	 * This method return the source object that is under validation. 
	 * The actual implementation of the validator is responsible for the correctness of the Object.
	 * 
	 * @return the source object 
	 */
	Object getSource();
	
	/**
	 * This method is called when an input field is to be validated using the validator
	 * 
	 * @return an validation result object
	 */
	ValidationResult validate();
	
	/**
	 * Set a message that will be displayed when the validation requirements are NOT met.
	 * 
	 * @param message the fault message to be displayed
	 */
	void setFaultMessage(String message);
}
