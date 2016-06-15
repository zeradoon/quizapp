package com.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.common.CommonUtil;
import com.common.http.CommonDownloader;

import android.R;
import android.widget.EditText;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class represent the validation for email address fields only.
 */
public class EmailAddressValidator extends AbstractValidator {
	
	/**
	 * member variable to represent the required field object.
	 */
	private Object m_Source;
	
	/**
	 * Default constructor
	 */
	public EmailAddressValidator(EditText source) {
		super(true);
		m_Source = source;
	}
	
	/**
	 * Default constructor
	 */
	public EmailAddressValidator(EditText source, String requiredMessage) {
		super(true);
		m_Source = source;
		m_RequiredMessage = requiredMessage;
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
	 * This method perform the email field validation on the object.
	 * 
	 * @return validation result wrapped into validationresult object.
	 */
	@Override
	public ValidationResult validate() {
		ValidationResult l_VR = null;
		String l_Source = ((EditText)m_Source).getText().toString();
		
		if (l_Source.length() == 0)
		{
			//l_VR = new ValidationResult(false, m_RequiredMessage);
			//((EditText)m_Source).setError(m_RequiredMessage);
			l_VR = new ValidationResult(false, m_RequiredMessage, m_Source, CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT);
		}
		else {
			Pattern pattern = Pattern.compile(CommonUtil.REGEX_PATTERN_EMAIL);
			Matcher matcher = pattern.matcher(l_Source);
			
			if (matcher.matches()) {
				//l_VR = new ValidationResult(true, "");
			}
			else
			{
				//l_VR = new ValidationResult(false, "");
				//((EditText)m_Source).setText(null);
				//((EditText)m_Source).setError(m_RequiredMessage);
				l_VR = new ValidationResult(false, m_RequiredMessage, m_Source, CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT);
			}
		}
		
		return l_VR;
	}

}