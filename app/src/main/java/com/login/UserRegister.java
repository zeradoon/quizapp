package com.login;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.common.CommonUtil;
import com.common.NetworkUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.main.QuizMain;
import com.practice.PracticeQuiz;
import com.practice.PracticeSelect;
import com.profile.ProfileMain;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to register a new user to SKMS Quiz Application.
 */
public class UserRegister extends Activity implements OnClickListener {

	/* UserRegister class context */
	private Context m_context = this;

	/* logging variable */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = UserRegister.class.getSimpleName();

	/* member representing service to get company list */
	private static final String DOWNLOAD_COMPANY_PATH = "getCompany.do";

	/* member representing service to register new user */
	private static final String REGISTER_USER_PATH = "registerUser.do";
	
	/* member representing service to verify duplicate User Id */
	private static final String DUPLICATE_USER_ID = "duplicateUserId.do";
	
	/* member representing service to verify duplicate Email Id */
	private static final String DUPLICATE_EMAIL_ID = "duplicateEmailId.do";

	/* company list dialog id */
	private static final int DIALOG_COMPANY = 0;
	
	/* email domain list dialog id */
	private static final int DIALOG_EMAIL_DOMAIN = 1;
	
	/* registration dialog id */
	private static final int DIALOG_REGISTER = 2;
	
	/* validation error dialog id */
	private static final int DIALOG_VALIDATION_ERROR = 3;
	
	/* registration dialog id */
	private static final int DIALOG_AFFIRM_USER_EMAIL = 4;
	
	/* member representing user id for registration */
	private String m_user_id;

	/* member representing user password for registration */
	private String m_user_password;

	/* member representing user name for registration */
	private String m_user_name;

	/* member representing user nickname for registration */
	private String m_user_nickname;

	/* member representing user email for registration */
	private String m_user_email;

	/* member representing user department for registration */
	private String m_user_dept;

	/* member representing user phone for registration */
	private String m_user_phone;

	/* member representing user id for registration */
	private EditText m_et_user_id;
	
	/* member representing user name for registration */
	private EditText m_et_user_name;
	
	/* member representing user password for registration */
	private EditText m_et_user_password;
	
	/* member representing user password for registration */
	private EditText m_et_user_passwordCon;
	
	/* member representing user email for registration */
	private EditText m_et_user_email;
	
	/* member representing user email domain for registration */
	private EditText m_et_user_email_domain;
	
	/* member representing user company for registration */
	private EditText m_et_user_company;
	
	/* member representing user department for registration */
	private EditText m_et_user_dept;
	
	/* member representing user phone for registration */
	private EditText m_et_user_phone;
	
	/* member representing validation result on registration screen after validation check. */
	private ValidationResult m_VR;

	/*
	 * Variables for getting company name selected by user from a list of
	 * companies.
	 */
	private String[] m_companyNameData = null;
	private String[] m_companyCodeData = null;
	private String m_companyCode = "";
	private String m_companyName = "";
	private ArrayList<HashMap<String, String>> m_companyList;
	
	private String[] m_emailDomainNameData = {CommonUtil.EMAIL_DOMAIN_SK_COM,CommonUtil.EMAIL_DOMAIN_INFOSEC_COM};
	private String m_emailDomainName = "";
	
	/* member representing Registration and Cancel button */
	@SuppressWarnings("unused")
	private Button m_RegisterBtn, m_CancelBtn;

	/**
	 * This method is called once on creation of the User Registration Activity.
	 * It is used for initialization of required variables.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_register);

		TextView tvTitle = (TextView) findViewById(R.id.header_title_id);
		tvTitle.setText(getString(R.string.header_title_register));
		
		/* set values input by user to member variables */
		m_et_user_id = (EditText) findViewById(R.id.etUserId);
		m_et_user_name = (EditText) findViewById(R.id.etUserName);
		m_et_user_password = (EditText) findViewById(R.id.etUserPassword);
		m_et_user_passwordCon = (EditText) findViewById(R.id.etUserPasswordCon);
		m_et_user_email = (EditText) findViewById(R.id.etUserEmail);
		m_et_user_email_domain = (EditText) findViewById(R.id.etUserEmailDomain);
		m_et_user_company = (EditText) findViewById(R.id.etUserCompany);
		m_et_user_dept = (EditText) findViewById(R.id.etUserDept);
		m_et_user_phone = (EditText) findViewById(R.id.etUserPhone);
		m_et_user_phone.setText(NetworkUtil.getPhoneNumber(m_context));
		
		m_et_user_company.setOnClickListener(this); 								// register listener for selection of company from company list.
		m_et_user_email_domain.setOnClickListener(this);
		
		m_et_user_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {	// register listener for validation of duplicate User Id.
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String l_UserId = m_et_user_id.getText().toString();
					String url = getString(R.string.base_uri) + DUPLICATE_USER_ID;	
					HashMap<String, String> parameters = new HashMap<String, String>();
					parameters.put(CommonUtil.USER_ID, l_UserId);

					Context context = m_context;	// User Registration Activity context.
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
								
								if (Integer.parseInt(msgCode) > 0) {
									m_VR = new ValidationResult(true, getString(R.string.user_id_not_available), m_et_user_id, CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT);
									showDialog(DIALOG_VALIDATION_ERROR);
								}
							}
							return true;
						}
					};

					HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,	parameters, id, context, callable, progressVisible);
					task.execute();
				}
			}
		});
		
		/* populate company list on creation of registration activity. */
		downloadCompany();
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
	 * This method is called when any dialog is opened by User Registration Activity. 
	 * If @param id == 0 then open the dialog box with company list.
	 * 
	 * @param dialogId
	 * @return Dialog box
	 */
	@Override
	protected Dialog onCreateDialog(int dialogId, final Bundle savedDialogInstanceState) {
		switch (dialogId) {
			case DIALOG_COMPANY:
				int checkedCompanyPosition = 0;
	
				for (int i = 0; i < m_companyNameData.length; i++) {
					if (m_companyNameData[i].equalsIgnoreCase(m_companyName)) {
						checkedCompanyPosition = i;
						break;
					}
				}
	
				return new AlertDialog.Builder(m_context)
						.setIcon(R.drawable.sk_logo)
						.setTitle(R.string.profile_main_company_alert_dialog_title)
						.setSingleChoiceItems(m_companyNameData, checkedCompanyPosition, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int whichButton) {
										if (!m_companyCode.equalsIgnoreCase(m_companyCodeData[whichButton])) {
											m_companyCode = m_companyCodeData[whichButton];
											m_et_user_company.setText(m_companyNameData[whichButton]);
										}
										dialog.dismiss();
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
				
			case DIALOG_REGISTER:
				LayoutInflater factory = LayoutInflater.from(this);
				final View registrationConfirmView = factory.inflate(R.layout.alert_dialog_registration_confirm, null);
				
				return new AlertDialog.Builder(m_context)
				.setIcon(R.drawable.sk_logo)
				.setTitle(R.string.alert_dialog_registration_confirm_title)
				.setView(registrationConfirmView)
				.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								finish();
							}
						}).create();
				
			case DIALOG_VALIDATION_ERROR:
				//LayoutInflater factory2 = LayoutInflater.from(this);
				//View registrationConfirmView2 = factory2.inflate(R.layout.alert_dialog_validation_error, null);
				//TextView tv = (TextView)registrationConfirmView2.findViewById(R.id.tv_alert_dialog_validation_error);
				//tv.setText(m_VR.getMessage());
				
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
								
								if (m_VR.getObjClass().equalsIgnoreCase(CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT) && m_VR.getMessage().equalsIgnoreCase(getString(R.string.select_email_domain))){
									showDialog(DIALOG_EMAIL_DOMAIN);
								} else if (m_VR.getObjClass().equalsIgnoreCase(CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT) && m_VR.getMessage().equalsIgnoreCase(getString(R.string.select_email_domain))){
									showDialog(DIALOG_COMPANY);
								}
							}
						}).create();
			case DIALOG_AFFIRM_USER_EMAIL:
				factory = LayoutInflater.from(this);
				String userEmail = "";
				if (savedDialogInstanceState != null) {
					userEmail = savedDialogInstanceState.getString(CommonUtil.USER_EMAIL);
				}
				final View emailConfirmView = factory.inflate(R.layout.alert_dialog_affirm_user_email, null);
				((EditText)emailConfirmView.findViewById(R.id.alert_dialog_confirm_UserEmail)).setText(userEmail);
				
				return new AlertDialog.Builder(m_context)
				.setIcon(R.drawable.sk_logo)
				.setTitle(R.string.alert_dialog_affirm_user_email_title)
				.setView(emailConfirmView)
				.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								registerUserData();
								dialog.dismiss();
								
							}
						})
				.setNegativeButton(R.string.alert_dialog_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
							}
						}).create();
		}

		return super.onCreateDialog(dialogId);
	}

	/**
	 * This method is called when user click anything on User Registration screen.
	 * 
	 * @param userRegViewObj
	 */
	@Override
	public void onClick(View userRegViewObj) {
		switch (userRegViewObj.getId()) {
			case R.id.etUserCompany:				// If the object is Company textbox then open the company list dialog box.
				showDialog(DIALOG_COMPANY);
				
				break;
			case R.id.etUserEmailDomain:			// If the object is email domain textbox then open the domain list dialog box.
				showDialog(DIALOG_EMAIL_DOMAIN);
				
				break;
			case R.id.login_RegisterBtn:			// If the object is registration button then validate and register user.
				registerUser();
				
				//finish();
				break;
			case R.id.login_CancelBtn:				// If the object is cancel button then exit the application.
				//Intent intent = null;
				//intent = new Intent(UserRegister.this, UserLogin.class);	// create intent to invoke user registration activity.
				//startActivity(intent);	// start the intent to open user registration screen.
				finish();
				
				break;
		}
	}

	/**
	 * This method downloads the company list in background when the User Registration Activity is created.
	 * 
	 */
	private void downloadCompany() {
		String url = getString(R.string.base_uri) + DOWNLOAD_COMPANY_PATH;	// get the url for company list service.
		HashMap<String, String> parameters = null;	// object to store company list in a map. 

		Context context = m_context;	// User Registration Activity context.
		boolean progressVisible = true;
		int id = 111;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				m_companyList = list;
				
				if (m_companyList.size() > 0) {
					m_companyNameData = new String[m_companyList.size()];
					m_companyCodeData = new String[m_companyList.size()];
					
					for (int i = 0; i < m_companyList.size(); i++) {
						m_companyNameData[i] = m_companyList.get(i).get(CommonUtil.COMP_NM);
						m_companyCodeData[i] = m_companyList.get(i).get(CommonUtil.COMP_CD);
					}
					//m_companyCode = m_companyCodeData[0];
					//m_et_user_company.setText(m_companyNameData[0]);
				} else {
					Toast.makeText(m_context, R.string.profile_main_company_data_empty_message,	Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,	parameters, id, context, callable, progressVisible);
		task.execute();
	}

	/**
	 * This method is used to validate and register a new user to database.
	 */
	private void registerUser() {
		/* set values input by user to member variables */
		m_user_id = ((EditText) findViewById(R.id.etUserId)).getText().toString();
		m_user_password = ((EditText) findViewById(R.id.etUserPassword)).getText().toString();
		m_user_name = ((EditText) findViewById(R.id.etUserName)).getText().toString();
		
		if (((EditText) findViewById(R.id.etUserNickName)).getText().toString().length() < 1)
			m_user_nickname = m_user_name;
		else 
			m_user_nickname = ((EditText) findViewById(R.id.etUserNickName)).getText().toString();
		
		m_user_email = ((EditText) findViewById(R.id.etUserEmail)).getText().toString() + getString(R.string.tvUserEmailDomain) + ((EditText) findViewById(R.id.etUserEmailDomain)).getText().toString();
		m_user_dept = ((EditText) findViewById(R.id.etUserDept)).getText().toString();
		m_user_phone = ((EditText) findViewById(R.id.etUserPhone)).getText().toString();

		boolean isFormValid = validateForm();	// variable to represent if user input is valid or not.
		
		/* Validate the input from user and throw any error. If the input is valid then register the user. */
		if (isFormValid ) {
			isEmailAvailable();
		}
	}
	
	/**
	 * This method confirms if the email already registered by another user or not. 
	 * If already registered, throw error else proceed with registration.
	 */
	private void isEmailAvailable() {
		String url = getString(R.string.base_uri) + DUPLICATE_EMAIL_ID;	
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_EMAIL, m_user_email);
		final String userEmail = m_user_email;
		
		Context context = m_context;	// User Registration Activity context.
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
					
					if (Integer.parseInt(msgCode) > 0) {
						m_VR = new ValidationResult(true, getString(R.string.email_id_not_available), m_et_user_email, CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT);
						showDialog(DIALOG_VALIDATION_ERROR);
					} else {
						Bundle args = new Bundle();
						args.putString(CommonUtil.USER_EMAIL, userEmail);
						
						showDialog(DIALOG_AFFIRM_USER_EMAIL, args);
						//registerUserData();
					}
				}
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url, parameters, id, context, callable, progressVisible); 
		task.execute();
	}
	
	/**
	 * This method register a new user to SKMS Quiz Application.
	 */
	private void registerUserData() {
		String url = getString(R.string.base_uri) + REGISTER_USER_PATH;		// get url for user registration service.
		HashMap<String, String> parameters = new HashMap<String, String>();	// object to store user input in a map. 
	    
		/* store user input values from the registration form into an object.*/
		parameters.put(CommonUtil.USER_ID, m_user_id); 
		parameters.put(CommonUtil.USER_PASSWORD,m_user_password); 
		parameters.put(CommonUtil.USER_NM, m_user_name);
		parameters.put(CommonUtil.USER_NICKNM, m_user_nickname);
		parameters.put(CommonUtil.USER_EMAIL, m_user_email);
		parameters.put(CommonUtil.USER_DEPT, m_user_dept);
		parameters.put(CommonUtil.USER_PHONE, m_user_phone); 
		parameters.put(CommonUtil.IMEI, NetworkUtil.getIMEI(m_context));
		parameters.put(CommonUtil.UPDATE_ID, m_user_id);
		
		if (!m_companyCode.equals("")) { 
			parameters.put(CommonUtil.USER_COMPANY, m_companyCode); 
		} 
		
		Context context = m_context;	// get user registration activity context. 
		boolean progressVisible = true; 
		int id = 111;
	    
		/* Execute the user registration service to register a new user. */
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
						showDialog(DIALOG_REGISTER);
					else 
						Toast.makeText(m_context, getString(R.string.registration_error), Toast.LENGTH_SHORT).show();
				}
				
				return true; 
			} 
		};
	  
		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url, parameters, id, context, callable, progressVisible); 
		task.execute();
	}

	/**
	 * This method validate all the input from user and throws any error in case input is invalid.
	 * 
	 * @return true if form is valid else false.
	 */
	private boolean validateForm() {
		List<Validator> validators = new ArrayList<Validator>();	// List of all validators that can be applied to the user registration screen.
		
		validators.add(new RequiredFieldValidator(m_et_user_id, getString(R.string.user_id_req), CommonUtil.REGEX_PATTERN_USER_ID));	// Add specific validators for validation.
		validators.add(new ComparisonValidator(m_et_user_password, m_et_user_passwordCon, getString(R.string.new_password_req), CommonUtil.REGEX_PATTERN_PASSWORD));	// Add specific validators for validation.
		validators.add(new RequiredFieldValidator(m_et_user_name, getString(R.string.username_blank), null));	// Add specific validators for validation.
		validators.add(new PhoneNumberValidator(m_et_user_phone, getString(R.string.phone_num_invalid)));	// Add specific validators for validation.
		validators.add(new RequiredFieldValidator(m_et_user_company, getString(R.string.select_company), null));	// Add specific validators for validation.
		validators.add(new RequiredFieldValidator(m_et_user_dept, getString(R.string.department_name_blank), null));	// Add specific validators for validation.
		validators.add(new EmailAddressValidator(m_et_user_email, getString(R.string.email_id_invalid)));	// Add specific validators for validation.
		validators.add(new RequiredFieldValidator(m_et_user_email_domain, getString(R.string.select_email_domain), null));	// Add specific validators for validation.
		
		/* Perform validation on registration fields at once and set error messages if any. */
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

}
