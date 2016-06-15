package com.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.common.CommonUtil;
import com.common.NetworkUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.skcc.portal.skmsquiz.R;
import com.validation.AbstractValidator;
import com.validation.ComparisonValidator;
import com.validation.EmailAddressValidator;
import com.validation.PhoneNumberValidator;
import com.validation.RequiredFieldValidator;
import com.validation.ValidationResult;
import com.validation.Validator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to show recover userid or password for SKMS Quiz Application.
 */
public class RecoverUserIdPassword extends Activity implements OnClickListener {
	
	/* RecoverUserIdPassword class context */
	private Context m_context = this;

	/* logging variable */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = RecoverUserIdPassword.class.getSimpleName();
	
	/* Recover UserId Password mail send dialog id */
	private static final int DIALOG_RECOVER_MAIL_SENT = 0;
	
	/* email domain list dialog id */
	private static final int DIALOG_EMAIL_DOMAIN = 1;
	
	/* validation error dialog id */
	private static final int DIALOG_VALIDATION_ERROR = 2;
	
	/* member representing service to send recovery mail */
	private static final String SEND_MAIL_BY_EMAIL_ADDRESS = "sendMailByEmailAddress.do";
	
	/* member representing user email to recover userid or password */
	private EditText m_et_user_email;
	
	/* member representing user email domain for registration */
	private EditText m_et_user_email_domain;
	
	private String[] m_emailDomainNameData = {CommonUtil.EMAIL_DOMAIN_SK_COM,CommonUtil.EMAIL_DOMAIN_INFOSEC_COM};
	private String m_emailDomainName = "";
	
	/* member representing validation result on registration screen after validation check. */
	private ValidationResult m_VR;

	/**
	 * This method is called once on creation of the RecoverUserIdPassword Activity.
	 * It is used for initialization of required variables.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recover_userid_password);
		
		TextView tvTitle = (TextView) findViewById(R.id.header_title_id);
		tvTitle.setText(getString(R.string.header_title_find_idpw));
		
		/* set values input by user to member variables */
		m_et_user_email = (EditText) findViewById(R.id.forgot_etUserEmail);
		m_et_user_email_domain = (EditText) findViewById(R.id.forgot_etUserEmailDomain);
		
		m_et_user_email_domain.setOnClickListener(this);
	}
	
	/**
	 * This method finish the activity when the back button is pressed on phone.
	 * 
	 * @param keyCode
	 * @param event
	 * 
	 * @return boolean
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * This method is called when user click anything on Recovery screen.
	 * 
	 * @param recoveryView
	 */
	@Override
	public void onClick(View recoveryView) {
		switch (recoveryView.getId()) {
			case R.id.forgot_FindUserIdBtn:				// If the object is find button then validate user details and send recovery mail.
				boolean isFormValid = validateForm();	// variable to represent if user input is valid or not.
				
				/* Validate the input from user and throw any error. If the input is valid then send the recovery mail to the user. */
				if (isFormValid ) {
						sendMailByEmailAddress();
				}
				
				break;
			case R.id.forgot_etUserEmailDomain:			// If the object is email domain textbox then open the domain list dialog box.
				showDialog(DIALOG_EMAIL_DOMAIN);
				
				break;
			case R.id.forgot_cancelBtn:					// If the object is cancel button then cancel the screen and return to login screen.
				finish();
				
				break;
		}
	}

	/**
	 * This method send the recovery mail to the user.
	 */
	private void sendMailByEmailAddress() {
		String url = getString(R.string.base_uri) + SEND_MAIL_BY_EMAIL_ADDRESS;

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_EMAIL, ((EditText)m_et_user_email).getText().toString().concat(getString(R.string.tvUserEmailDomain)).concat(((EditText) m_et_user_email_domain).getText().toString()));
		parameters.put(CommonUtil.USER_PHONE, NetworkUtil.getPhoneNumber(m_context));
		
		Context context = m_context;
		boolean progressVisible = true;
		int id = 111;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list, int id) {
				ArrayList<HashMap<String, String>> m_ResultList = list;
				String msgCode = "";
				
				if (m_ResultList.size() > 0) {
					for (int i = 0; i < m_ResultList.size(); i++) {
						if (m_ResultList.get(i) != null)
							msgCode = m_ResultList.get(i).get(CommonUtil.MESSAGE);
					}
					
					if (msgCode.equalsIgnoreCase(CommonUtil.SUCCESS))
						showDialog(DIALOG_RECOVER_MAIL_SENT);
					else if (msgCode.equalsIgnoreCase(CommonUtil.FAILURE)){
						m_et_user_email.setError(getString(R.string.emailid_phone_not_exist));
					}
				}
				return true;
			}
		};

		HttpGetParameterAsyncTask task = new HttpGetParameterAsyncTask(url,	parameters, id, context, callable, progressVisible);
		task.execute();
	}

	/**
	 * This method validate all the input from user and throws any error in case input is invalid.
	 * 
	 * @return true if form is valid else false.
	 */
	private boolean validateForm() {
		List<Validator> validators = new ArrayList<Validator>();	
		validators.add(new EmailAddressValidator(m_et_user_email, getString(R.string.email_id_invalid)));	// Add specific validators for validation.
		validators.add(new RequiredFieldValidator(m_et_user_email_domain, getString(R.string.select_email_domain), null));	// Add specific validators for validation.

		/* Perform validation on user input fields at once and set error messages if any. */
		List<ValidationResult> l_ValidationResults = AbstractValidator.validateAll(validators);
		
		/* If validation throws any error, set the form as invalid else send true for valid form input */
		if (l_ValidationResults != null && l_ValidationResults.size() != 0) {
			m_VR = l_ValidationResults.get(0);
			showDialog(DIALOG_VALIDATION_ERROR);
			return false;	
		} else {
			return true;	
		}
		
	}
	
	/**
	 * This method set the corresponding error messages to error message dialog pop-up.
	 * 
	 * @param dialogId
	 * @param dialog
	 */
	@Override
	protected void onPrepareDialog(final int dialogId, final Dialog dialog) {
		switch (dialogId) {
			case DIALOG_VALIDATION_ERROR:
				((AlertDialog)dialog).setMessage(m_VR.getMessage());
		}
	}
	
	/**
	 * This method is called when any dialog is opened by Recovery Activity.
	 * 
	 * @param dialogId
	 * @return Dialog box
	 */
	@Override
	protected Dialog onCreateDialog(int dialogId) {
		switch (dialogId) {
			case DIALOG_RECOVER_MAIL_SENT:	// Open the recover userid/password dialog box.
				LayoutInflater factory = LayoutInflater.from(this);
				final View registrationConfirmView = factory.inflate(R.layout.alert_dialog_recover_details_mail, null);
				
				return new AlertDialog.Builder(m_context)
				.setIcon(R.drawable.sk_logo)
				.setTitle(R.string.alert_dialog_recover_details_mail_title)
				.setView(registrationConfirmView)
				.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.dismiss();
								finish();
							}
						}).create();
				
			case DIALOG_EMAIL_DOMAIN:
				int checkedEmailDomainPosition = 0;
	
				for (int i = 0; i < 2; i++) {
					if (m_emailDomainNameData[i].equalsIgnoreCase(m_emailDomainName)) {
						checkedEmailDomainPosition = i;
						break;
					}
				}
	
				return new AlertDialog.Builder(m_context)
						.setIcon(R.drawable.sk_logo)
						.setTitle(R.string.register_email_domain_alert_dialog_title)
						.setSingleChoiceItems(m_emailDomainNameData, checkedEmailDomainPosition, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int whichButton) {
										if (!m_emailDomainName.equalsIgnoreCase(m_emailDomainNameData[whichButton])) {
											m_emailDomainName = m_emailDomainNameData[whichButton];
											m_et_user_email_domain.setText(m_emailDomainNameData[whichButton]);
										}
										dialog.dismiss();
									}
						}).create();
			
			case DIALOG_VALIDATION_ERROR:
				return new AlertDialog.Builder(m_context)
				.setIcon(R.drawable.sk_logo)
				.setTitle(R.string.alert_dialog_validation_error_title)
				//.setView(registrationConfirmView2)
				.setMessage(m_VR.getMessage())
				.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								if (m_VR.getObjClass().equalsIgnoreCase(CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT)) {
									((EditText)m_VR.getObject()).requestFocus();
								} 
								if (m_VR.getObjClass().equalsIgnoreCase(CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT) && m_VR.getMessage().equalsIgnoreCase((getString(R.string.select_email_domain)))){
									showDialog(DIALOG_EMAIL_DOMAIN);
								}
							}
						}).create();
		}

		return super.onCreateDialog(dialogId);
	}

	
}
