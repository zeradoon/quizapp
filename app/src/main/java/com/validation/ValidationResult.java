package com.validation;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class store the validation result after validation.
 */
public class ValidationResult {
	/**
	 * member variable to represent if validation is ok or not.
	 */
	private boolean m_Ok = false;
	
	/**
	 * member variable to represent the error message string.
	 */
	private String m_ErrorMsg = "";
	
	private Object m_Object;
	
	private String m_Obj_Class;
	
	/**
	 * Default constructor
	 */
	public ValidationResult(boolean ok, String message) {
		m_Ok = ok;
		m_ErrorMsg = message;
	}
	
	public ValidationResult(boolean ok, String message, Object object, String objClass) {
		m_Ok = ok;
		m_ErrorMsg = message;
		m_Object = object;
		m_Obj_Class = objClass;
	}

	public boolean isValid() {
		return m_Ok;
	}
	
	public String getMessage() {
		return m_ErrorMsg;
	}
	
	public String getObjClass() {
		return m_Obj_Class;
	}
	
	public Object getObject() {
		return m_Object;
	}
}
