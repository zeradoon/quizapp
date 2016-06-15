package com.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.common.CommonUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.main.QuizMain;
import com.skcc.portal.skmsquiz.R;
import com.validation.AbstractValidator;
import com.validation.ComparisonValidator;
import com.validation.RequiredFieldValidator;
import com.validation.ValidationResult;
import com.validation.Validator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to reset password of a user in SKMS Quiz Application.
 */
public class ResetPassword  extends Activity implements OnClickListener {

	/* ResetPassword class context */
	private Context m_context = this;

	/* logging variable */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = ResetPassword.class.getSimpleName();

	/* member representing service to update user password */
	private static final String UPDATE_USER_PASSWORD = "updateUserPassword.do";
	
	/* member representing service to validate user input */
	private static final String CONFIRM_CURRENT_PASSWORD = "validateCredentials.do";
	
	/* User Password successfully changed dialog id */
	private static final int DIALOG_PASSWORD_SUCCESSFULLY_UPDATED = 0;
	
	/* member representing user id */
	private String m_user_id;
	
	/* member representing user password */
	private String m_user_password;
	
	/* member representing for confirmation of user current password  */
	private static boolean confirmPassword = false;
	
	/* member representing user password for login */
	private EditText m_et_user_password;
	
	/* member representing user password for login */
	private EditText m_et_old_password;
	
	/* member representing user password for login */
	private EditText m_et_new_password;
	
	/* member representing user password for login */
	private EditText m_et_new_passwordCon;
	
	/* member representing validation result on registration screen after validation check. */
	private ValidationResult m_VR;
	
	/**
	 * This method is called once on creation of the ResetPassword Activity.
	 * It is used for initialization of required variables.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reset_password);
		
		TextView tvTitle = (TextView) findViewById(R.id.header_title_id);
		tvTitle.setText(getString(R.string.header_title_change_pw));
		
		/* set values input by user to member variables */
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_user_password = getIntent().getStringExtra(CommonUtil.USER_PASSWORD);
		
		m_et_old_password = (EditText) findViewById(R.id.reset_password_oldPassword);
		m_et_new_password = (EditText) findViewById(R.id.reset_password_newPassword);
		m_et_new_passwordCon = (EditText) findViewById(R.id.reset_password_newPasswordCon);
		
		m_et_user_password = new EditText(m_context);
		m_et_user_password.setText(m_user_password);
		m_et_old_password.setText(m_user_password);
		
		if (m_user_password.equalsIgnoreCase("")) {
			m_et_old_password.requestFocus();	// set focus
			confirmPassword = true;
		} else {
			m_et_new_password.requestFocus();	// set focus
		}
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
	 * This method is called when user click anything on Reset Password screen.
	 * 
	 * @param resetPasswordView
	 */
	@Override
	public void onClick(View resetPasswordView) {
		switch (resetPasswordView.getId()) {
		case R.id.ChangePasswordBtn:			// If the object is change password button then invoke update password operation.
			if (confirmPassword) {
				confirmPassword(m_user_id, ((EditText)m_et_old_password).getText().toString());
			}
			else if (validatePasswords()) {
				updateUserPassword();
			}
			break;
		}
	}
	
	/**
	 * This method validates if the User has provided userid and password for login or not.
	 * 
	 * @return boolean
	 */
	private boolean validatePasswords() {
		List<Validator> validators = new ArrayList<Validator>();	// List of all validators that can be applied to the user registration screen.
		
		if (!confirmPassword) {
			validators.add(new ComparisonValidator(m_et_old_password, m_et_user_password, getString(R.string.incorrect_old_password), CommonUtil.REGEX_PATTERN_PASSWORD));	// Add specific validators for validation.
		}
		validators.add(new ComparisonValidator(m_et_new_password, m_et_new_passwordCon, getString(R.string.new_password_req), CommonUtil.REGEX_PATTERN_PASSWORD));	// Add specific validators for validation.
		
		/* Perform validation on registration fields at once and set error messages if any. */
		List<ValidationResult> l_ValidationResults = AbstractValidator.validateAll(validators);
		
		/* If validation throws any error, set the form as invalid else send true for valid form input */
		if (l_ValidationResults != null && l_ValidationResults.size() != 0) {
			m_VR = l_ValidationResults.get(0);

			if (m_VR.getObjClass().equalsIgnoreCase(CommonUtil.SCREEN_CONTROL_TYPE_EDIT_TEXT)) {
				((EditText)m_VR.getObject()).setError(m_VR.getMessage());
				((EditText)m_VR.getObject()).requestFocus();
			}
			return false;	
		} else {
			return true;	
		}
	}
	
	/**
	 * This method updates new password in the database.
	 */
	private void updateUserPassword() {
		String url = getString(R.string.base_uri) + UPDATE_USER_PASSWORD;	// get the url for reset password.
		HashMap<String, String> parameters = new HashMap<String, String>();	// object to store user details in a map.
		
		parameters.put(CommonUtil.USER_ID, m_user_id);
		parameters.put(CommonUtil.USER_PASSWORD, ((EditText)m_et_new_password).getText().toString());

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
						showDialog(DIALOG_PASSWORD_SUCCESSFULLY_UPDATED);
				}
				
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,	parameters, id, context, callable, progressVisible);
		task.execute();
	}
	
	/**
	 * This method validates current password from the database.
	 */
	private void confirmPassword(String m_user_id, String m_user_password) {
		String url = getString(R.string.base_uri) + CONFIRM_CURRENT_PASSWORD;	// get the url for reset password.
		HashMap<String, String> parameters = new HashMap<String, String>();	// object to store user details in a map.
		
		parameters.put(CommonUtil.USER_ID, m_user_id);
		parameters.put(CommonUtil.USER_PASSWORD, m_user_password);

		Context context = m_context;
		boolean progressVisible = true;
		int id = 111;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list, int id) {
				String confirmValue = "";
				ArrayList<HashMap<String, String>> m_ResultList = list;
				
				for(int i=0;i<m_ResultList.size();i++){
					confirmValue = String.valueOf(list.get(i).get(CommonUtil.CONFIRM_VALUE));
				}
				
				if (Integer.parseInt(confirmValue) == 3) {
					if (validatePasswords()) {
						updateUserPassword();
					}
				} else {
					((EditText)m_et_old_password).setError(getString(R.string.invalid_password_credentials));
					((EditText)m_et_old_password).requestFocus();
				}
				
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,	parameters, id, context, callable, progressVisible);
		task.execute();
	}
	
	/**
	 * This method is called when any dialog is opened by Reset Password Activity.
	 * 
	 * @param dialogId
	 * @return Dialog box
	 */
	@Override
	protected Dialog onCreateDialog(int dialogId) {
		switch (dialogId) {
			case DIALOG_PASSWORD_SUCCESSFULLY_UPDATED:	
				LayoutInflater factory = LayoutInflater.from(this);
				final View registrationConfirmView = factory.inflate(R.layout.alert_dialog_password_successfully_changed, null);
				
				return new AlertDialog.Builder(m_context)
				.setIcon(R.drawable.sk_logo)
				.setTitle(R.string.alert_dialog_password_successfully_changed_title)
				.setView(registrationConfirmView)
				.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								Intent intent = new Intent(ResetPassword.this, QuizMain.class);
								startActivity(intent);
								dialog.dismiss();
								finish();
							}
						}).create();
		}

		return super.onCreateDialog(dialogId);
	}
	

}
