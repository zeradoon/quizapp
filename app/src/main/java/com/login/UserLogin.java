package com.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.common.CommonUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.main.QuizMain;
import com.skcc.portal.skmsquiz.R;
import com.validation.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to login into SKMS Quiz Application.
 */
public class UserLogin extends Activity implements OnClickListener {

	/* UserLogin class context */
	private Context m_context = this;

	/* logging variable */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = UserLogin.class.getSimpleName();
	
	/* User not confirmed dialog id */
	private static final int DIALOG_USER_NOTCONFIRMED = 0;
	
	/* User resend confirm mail dialog id */
	private static final int DIALOG_CONFIRM_MAIL_SENT = 1;
	
	/* validation error dialog id */
	private static final int DIALOG_VALIDATION_ERROR = 2;
		
	/* member representing service to validate user input */
	private static final String VALIDATE_CREDENTIALS = "validateCredentials.do";
	
	/* member representing service to resend confirmation mail. */
	private static final String RESEND_CONFIRMATION = "resendConfirmation.do";

	/* member representing user id for login */
	private EditText m_et_user_id;
	
	/* member representing user password for login */
	private EditText m_et_user_password;
	
	/* member representing user email for sending confirmation mail */
	public String m_et_user_email;
	
	/* member representing remember userid and password checkbox */
	private CheckBox m_et_remember_me;
	
	/* member representing validation result on registration screen after validation check. */
	private ValidationResult m_VR;
	
	/* members to store userid and password for auto login */
	private static final String PREFS_NAME = "MyPrefsFile"; 
	private static final String PREF_USERID = "userid";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_USERNM = "usernm";
	private static final String PREF_USERCOMPNM = "usercompnm";
	private static final String PREF_USERCOMPCD = "usercompcd";
	private static final String PREF_USERDEPT = "userdept";
	private static final String PREF_USEREMAIL = "useremail";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_REMEMBERME = "true";
	
	/**
	 * This method is called once on creation of the UserLogin Activity.
	 * It is used for initialization of required variables.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login);
		
		TextView tvTitle = (TextView) findViewById(R.id.header_title_id);
		tvTitle.setText(getString(R.string.header_title_login));
		
		/* set values input by user to member variables */
		m_et_user_id = (EditText) findViewById(R.id.login_etUserId);
		m_et_user_password = (EditText) findViewById(R.id.login_etUserPassword);
		m_et_remember_me = (CheckBox) findViewById(R.id.login_cbRememberMe);
		
		m_et_user_id.requestFocus();	// set focus 
		
		/* fetch userid and password from preferences. */
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String userId = pref.getString(PREF_USERID, null);
		String userPassword = pref.getString(PREF_PASSWORD, null);
		String rememberMe = pref.getString(PREF_REMEMBERME, null);
		
		/* If the userid and password are already saved, then autologin user */
		if (rememberMe != null && rememberMe.equalsIgnoreCase(CommonUtil.BOOLEAN_TRUE)) {
			if (!userId.equalsIgnoreCase("null") || !userId.equals("")) {
				m_et_user_id.setText(userId);
				m_et_user_password.setText(userPassword);
				m_et_remember_me.setChecked(true);
			} else {
				m_et_user_id.setText("");
				m_et_user_password.setText("");
				m_et_remember_me.setChecked(false);
			}
			
			validateCredentials();
		}
	}
	
	/**
	 * This method is called when user click anything on User Login screen.
	 * 
	 * @param loginView
	 */
	@Override
	public void onClick(View loginView) {
		switch (loginView.getId()) {
			case R.id.login_LoginBtn:				// If the object is login button then invoke login operation.
				if (validateForm()) {
					validateCredentials();
				};
				
				break;
			case R.id.RegisterUserBtn:				// If the object is registration button then invoke registration screen.
				registerUser();
				
				break;
			case R.id.ForgotUserNamePasswordBtn:	// If the object is forgot username/password then invoke recover screen.
				recoverUserIdPassword();
				
				break;
		}
	}
	
	/**
	 * This method invoke the registration screen to register a user to SKMS Quiz application.
	 */
	private void registerUser() {
		Intent intent = new Intent(UserLogin.this, UserRegister.class);	// create intent to invoke user registration activity.
		startActivity(intent);	// start the intent to open user registration screen.
	}

	/**
	 * This method invokes login operation to SKMS Quiz application when the login button is clicked.
	 * 
	 * @param validUserCredentials
	 */
	private void loginUser(int validUserCredentials) {
		switch (validUserCredentials) {
			case 1:	
				userNotConfirmed();		// call method to show user not confirmed dialog.
				
				break;
			case 2:
				resetPassword();
				
				break;
			case 3:
				/* store the userid and password if remember me checkbox is checked. If the details are correct, login user. */
				if (m_et_remember_me.isChecked())
					getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
							.edit()
							.putString(PREF_USERID, m_et_user_id.getText().toString())
							.putString(PREF_PASSWORD, m_et_user_password.getText().toString())
							.putString(PREF_REMEMBERME, "true").commit();
				
				Intent intent = new Intent(UserLogin.this, QuizMain.class);
				startActivity(intent);
				finish();
				
				break;
			case 4:	// In case of wrong credentials, show the error message.
				((EditText)m_et_user_password).setError(getString(R.string.invalid_password_credentials));
				((EditText)m_et_user_password).requestFocus();
				
				break;
			case 5:	// In case of wrong credentials, show the error message.
				((EditText)m_et_user_id).setError(getString(R.string.invalid_loginid_credentials));
				((EditText)m_et_user_id).requestFocus();
				
				break;
		}
	}
	
	/**
	 * This method display the change password activity to reset password.
	 */
	private void resetPassword() {
		Intent intent = new Intent(UserLogin.this, ResetPassword.class);	// create intent to invoke reset password activity.
		
		intent.putExtra(CommonUtil.USER_ID, m_et_user_id.getText().toString());
		intent.putExtra(CommonUtil.USER_PASSWORD, m_et_user_password.getText().toString());
		
		startActivity(intent);	// start the intent to open reset password screen.
		finish();
	}

	/**
	 * This method validates if the User has provided userid and password for login or not.
	 * 
	 * @return boolean
	 */
	private boolean validateForm() {
		List<Validator> validators = new ArrayList<Validator>();	// List of all validators that can be applied to login screen.
		
		validators.add(new RequiredFieldValidator(m_et_user_id, getString(R.string.required_field)));	// Add specific validators for validation.
		validators.add(new RequiredFieldValidator(m_et_user_password, getString(R.string.required_field)));	// Add specific validators for validation.
		
		/* Perform validation on login fields at once and set error messages if any. */
		List<ValidationResult> l_ValidationResults = AbstractValidator.validateAll(validators);
		
		if (l_ValidationResults != null && l_ValidationResults.size() != 0) {
			m_VR = l_ValidationResults.get(0);
			
			if (m_VR.getObjClass().equalsIgnoreCase(CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT)) {
				((EditText)m_VR.getObject()).setError(m_VR.getMessage());
				((EditText)m_VR.getObject()).requestFocus();
			}
		}
		
		/* If validation throws any error, set the form as invalid else send true for valid form input */
		if (l_ValidationResults.size() != 0) {
			return false;	
		} else {
			return true;
		}
	}
	
	/**
	 * This method validates user credentials for login and return the value based on that.
	 */
	private void validateCredentials() {
		String l_UserId = m_et_user_id.getText().toString();
		String l_UserPassword = m_et_user_password.getText().toString();
		String url = getString(R.string.base_uri) + VALIDATE_CREDENTIALS;	

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_ID, l_UserId);
		parameters.put(CommonUtil.USER_PASSWORD, l_UserPassword);

		Context context = m_context;
		boolean progressVisible = true;
		int id = 111;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list, int id) {
				ArrayList<HashMap<String, String>> m_ResultList = list;
				String userId = "";
				String userPassword = "";
				String confirmValue = "";
				String userName = "";
				String userNm = "";
				String userCompNm = "";
				String userCompCd = "";
				String userDept = "";
				String userEmail = "";
				
				for(int i=0;i<m_ResultList.size();i++){
					confirmValue = String.valueOf(list.get(i).get(CommonUtil.CONFIRM_VALUE));
					userId = String.valueOf(list.get(i).get(CommonUtil.USER_ID));
					userPassword = String.valueOf(list.get(i).get(CommonUtil.USER_PASSWORD));
					userName = String.valueOf(list.get(i).get(CommonUtil.USER_NAME));
					userNm = String.valueOf(list.get(i).get(CommonUtil.USER_NM));
					userCompNm = String.valueOf(list.get(i).get(CommonUtil.USER_COMP_NM));
					userCompCd = String.valueOf(list.get(i).get(CommonUtil.USER_COMP_CD));
					userDept = String.valueOf(list.get(i).get(CommonUtil.USER_DEPT));
					userEmail = String.valueOf(list.get(i).get(CommonUtil.USER_EMAIL));
				}
				
				getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
				.edit()
				.putString(PREF_USERID, userId)
				.putString(PREF_PASSWORD, userPassword)
				.putString(PREF_USERNAME, userName)
				.putString(PREF_USERNM, userNm)
				.putString(PREF_USERCOMPNM, userCompNm)
				.putString(PREF_USERCOMPCD, userCompCd)
				.putString(PREF_USERDEPT, userDept)
				.putString(PREF_USEREMAIL, userEmail).commit();
				
				loginUser(Integer.parseInt(confirmValue));
				
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,	parameters, id, context, callable, progressVisible);
		task.execute();
	}

	/**
	 * This method open the Recover UserID/Password page.
	 */
	private void recoverUserIdPassword() {
		Intent intent = new Intent(UserLogin.this, RecoverUserIdPassword.class);
		startActivity(intent);
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
			case DIALOG_USER_NOTCONFIRMED:
				LayoutInflater factory = LayoutInflater.from(this);
				View registrationConfirmView = factory.inflate(R.layout.alert_dialog_registration_not_confirm, null);
				((EditText)registrationConfirmView.findViewById(R.id.alert_dialog_confirm_UserEmail)).setText(m_et_user_email);
		}
	}
	
	/**
	 * This method is called when any dialog is opened by User Login Activity.
	 * 
	 * @param dialogId
	 * @return Dialog box
	 */
	@Override
	protected Dialog onCreateDialog(int dialogId) {
		switch (dialogId) {
			case DIALOG_USER_NOTCONFIRMED:
				
				LayoutInflater factory = LayoutInflater.from(this);
				final View registrationConfirmView = factory.inflate(R.layout.alert_dialog_registration_not_confirm, null);
				((EditText)registrationConfirmView.findViewById(R.id.alert_dialog_confirm_UserEmail)).setText(m_et_user_email);
				
				return new AlertDialog.Builder(m_context)
				.setIcon(R.drawable.sk_logo)
				.setTitle(R.string.alert_dialog_registration_not_confirm_title)
				.setView(registrationConfirmView)
				.setPositiveButton(R.string.alert_dialog_send_confirm_email,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								String lUserEmail = ((EditText)registrationConfirmView.findViewById(R.id.alert_dialog_confirm_UserEmail)).getText().toString();
								resendConfirmationEmail(lUserEmail);
								dialog.dismiss();
								
							}

							private void resendConfirmationEmail(String userEmail) {
								String url = getString(R.string.base_uri) + RESEND_CONFIRMATION;	// get the url for resending confirm mail.
								HashMap<String, String> parameters = new HashMap<String, String>();	// object to store user details in a map.
								
								parameters.put(CommonUtil.USER_ID, ((EditText)m_et_user_id).getText().toString());
								parameters.put(CommonUtil.USER_EMAIL, userEmail);

								Context context = m_context;
								boolean progressVisible = true;
								int id = 111;
								BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

									@Override
									public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list, int id) {
										showDialog(DIALOG_CONFIRM_MAIL_SENT);
										return true;
									}
								};

								HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,	parameters, id, context, callable, progressVisible);
								task.execute();
							}
						})
				.setNegativeButton(R.string.alert_dialog_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
							}
						}).create();
				
			case DIALOG_CONFIRM_MAIL_SENT:
				LayoutInflater factory2 = LayoutInflater.from(this);
				final View registrationConfirmView2 = factory2.inflate(R.layout.alert_dialog_confirm_mail_sent, null);
				
				return new AlertDialog.Builder(m_context)
				.setIcon(R.drawable.sk_logo)
				.setTitle(R.string.alert_dialog_confirm_mail_sent_title)
				.setView(registrationConfirmView2)
				.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
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
								if (m_VR.getObjClass().equalsIgnoreCase(CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT))
									((EditText)m_VR.getObject()).requestFocus();								
							}
						}).create();
		}

		return super.onCreateDialog(dialogId);
	}

	/**
	 * This method show dialog box for not confirmed user.
	 */
	private void userNotConfirmed() {
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		m_et_user_email = pref.getString(PREF_USEREMAIL, null);
		
		showDialog(DIALOG_USER_NOTCONFIRMED);
	}


}
