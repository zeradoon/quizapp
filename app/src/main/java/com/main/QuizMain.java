package com.main;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.common.CommonUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.event.SuddenEventQuestion;
import com.login.UserLogin;
import com.login.UserRegister;
import com.mystudy.MyStudyRoom_Select;
import com.practice.PracticeSelect;
import com.real.real_intro;
import com.skcc.portal.skmsquiz.R;

/**
 * @author jungungi
 * 
 */
public class QuizMain extends Activity implements OnClickListener {

	// validate sudden event
	private static final String CHECK_SUDDEN_EVENT = "getSuddenEvent.do";
	
	/** Called when the activity is first created. */
	private int hidden_key = 0;
	
	// logcat 
	private static final String LOG_TAG = QuizMain.class.getSimpleName();
	
	// dialog type
//	private static final int QUIZMAIN_ALERT_DUPLICATE_IMEI = 1;
//	private static final int QUIZMAIN_ALERT_ERROR = 2;
//	private static final int QUIZMAIN_ALERT_APPROVAL = 3;
	private static final int QUIZMAIN_ALERT_FINISH = 1;
//	private static final int QUIZMAIN_ALERT_SUDDEN_JOIN = 2;

	/* members to store userid and password for auto login */
	public static final String PREFS_NAME = "MyPrefsFile"; 
	private static final String PREF_USERID = "userid";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_USERCOMPCD = "usercompcd";
	private static final String PREF_USERDEPT = "userdept";
	private static final String PREF_REMEMBERME = "true";
	
	// 
	private Context m_context = this;

	// 버튼 뷰에 대해서 OnClick 메소드와 같이 다른 메소드에서 접근이 필요하기 때문에 field로 정의함
	private FrameLayout btn01, btn02, btn03, btn04, btn05, btn06, btn07;

	
	//
	private String m_user_id;
	private String m_user_name;
	private String m_user_dept;
	private String m_comp_cd;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// 클릭이벤트가 동일하게 발생하기 때문에
		// OnClickListener를 구현하여 QuizMain에서 OnClick 메소드로 동일 처리하도록 유도
		btn01 = (FrameLayout) findViewById(R.id.mainMenu01);
		btn01.setOnClickListener(this);

		btn02 = (FrameLayout) findViewById(R.id.mainMenu02);
		btn02.setOnClickListener(this);

		btn03 = (FrameLayout) findViewById(R.id.mainMenu03);
		btn03.setOnClickListener(this);

		btn04 = (FrameLayout) findViewById(R.id.mainMenu04);
		btn04.setOnClickListener(this);

		btn05 = (FrameLayout) findViewById(R.id.mainMenu05);
		btn05.setOnClickListener(this);

		btn06 = (FrameLayout) findViewById(R.id.mainMenu06);
		btn06.setOnClickListener(this);
		
		btn07 = (FrameLayout) findViewById(R.id.mainMenu07);
		btn07.setOnClickListener(this);
		
		ImageView m_lnk_signout = (ImageView)findViewById(R.id.lnk_signout);
		m_lnk_signout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				signOutUser();
			}
		});
		
		loadUserInfo();
		
		checkSuddenEvent(m_user_id, m_comp_cd);
	}

	protected void signOutUser() {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().remove(PREF_USERID).remove(PREF_PASSWORD).remove(PREF_REMEMBERME).commit();
		Intent intent = new Intent(QuizMain.this, UserLogin.class);	// create intent to invoke user login activity.
		startActivity(intent);	// start the intent to open user login screen.
		finish();
	}

	private void loadUserInfo() {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		m_user_id = prefs.getString(PREF_USERID, null);
		m_user_name = prefs.getString(PREF_USERNAME, null);
		m_user_dept = prefs.getString(PREF_USERDEPT, null);
		m_comp_cd = prefs.getString(PREF_USERCOMPCD, null);
		
	}

	// 메인화면에서 하드웨어키(뒤로가기) 이벤트
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(LOG_TAG, "onKeyDown : " + keyCode);

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog(QUIZMAIN_ALERT_FINISH);

			return false;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			
			hidden_key++;

			if (hidden_key == 2) {
				hidden_key = 0;
				LayoutInflater factory = LayoutInflater.from(this);
				final View textEntryView = factory.inflate(
						R.layout.main_hidden, null);

				AlertDialog alertdialog = new AlertDialog.Builder(m_context)
				.setIcon(R.drawable.sk_logo)
				.setTitle("만든 사람들")
				.setView(textEntryView)
				.setPositiveButton("좋아요 ^.^",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) { }
						})
				.setNegativeButton("싫어요 -_-",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) { }
						})
				.create();

				alertdialog.show();

			}
			return false;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.quizmain, menu);

		Log.d(LOG_TAG, "onCreateOptionsMenu ");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		loadUserInfo();
		
	    switch (item.getItemId()) {
	        case R.id.profile:
	        	Intent intent = new Intent(QuizMain.this, com.profile.ProfileMain.class);
				intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
				startActivity(intent);
	            return true;
	        case R.id.password:
	        	intent = new Intent(QuizMain.this, com.login.ResetPassword.class);
				intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_PASSWORD, "");
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
				startActivity(intent);
	            return true;
	        case R.id.ranking:
	        	intent = new Intent(QuizMain.this, com.ranking.RankingSelect.class);
				intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
				startActivity(intent);
	            return true;
	        case R.id.events:
	        	intent = new Intent(QuizMain.this, com.event.GeneralEvent.class);
				intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
				startActivity(intent);
	            return true;
	        case R.id.signout:
	            signOutUser();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(LOG_TAG, "onPrepareOptionsMenu ");
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		loadUserInfo();
		
		Intent intent = null;
		if (v == btn01) {
			intent = new Intent(QuizMain.this, PracticeSelect.class);
			intent.putExtra(CommonUtil.USER_ID, m_user_id);
			intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
			intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
		} else if (v == btn02) {
			intent = new Intent(QuizMain.this, real_intro.class);
			intent.putExtra(CommonUtil.USER_ID, m_user_id);
			intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
			intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
		} else if (v == btn03) {
			intent = new Intent(QuizMain.this, MyStudyRoom_Select.class);
			intent.putExtra(CommonUtil.USER_ID, m_user_id);
			intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
			intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
		} else if (v == btn04) {
			intent = new Intent(QuizMain.this, com.ranking.RankingSelect.class);
			intent.putExtra(CommonUtil.USER_ID, m_user_id);
			intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
			intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
		} else if (v == btn05) {
			intent = new Intent(QuizMain.this, com.profile.ProfileMain.class);
			intent.putExtra(CommonUtil.USER_ID, m_user_id);
			intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
			intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
		} else if (v == btn06) {
			intent = new Intent(QuizMain.this, com.notice.NoticeList.class);
			intent.putExtra(CommonUtil.USER_ID, m_user_id);
			intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
			intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
		} else if (v == btn07) {
			intent = new Intent(QuizMain.this, com.event.GeneralEvent.class);
			intent.putExtra(CommonUtil.USER_ID, m_user_id);
			intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
			intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
		}
		
		startActivity(intent);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case QUIZMAIN_ALERT_FINISH:
			dialog = showQuizmainAlertFinish();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	private Dialog showQuizmainAlertFinish() {
		Dialog dialog;
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(R.string.quizmain_alert_finsh_title);
		builder.setMessage(R.string.quizmain_alert_finsh_message);
		builder.setPositiveButton(
				R.string.quizmain_alert_finsh_positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 닫기
						finish();
					}
				});
		builder.setNegativeButton(
				R.string.quizmain_alert_finsh_negative_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 닫기

					}
				});
		dialog = builder.create();
		return dialog;
	}
	
	private void showSuddenEventJoinDialog(final ArrayList<HashMap<String, String>> list) {
		Dialog dialog;
		AlertDialog.Builder builder;
		
		String eventName = (String)list.get(0).get(CommonUtil.NAME);
		String eventDesc = (String)list.get(0).get(CommonUtil.EVENT_DESC);
		
		builder = new AlertDialog.Builder(m_context);
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(eventName);
		builder.setMessage(eventDesc);
		builder.setPositiveButton(
				R.string.sudden_event_join_alert_positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
						Intent intent = new Intent(m_context, SuddenEventQuestion.class);
						intent.putExtra(CommonUtil.LIST, list);
						intent.putExtra(CommonUtil.USER_ID, m_user_id);
						intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
						intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
						startActivity(intent);
					}
				});
		builder.setNegativeButton(
				R.string.sudden_event_join_alert_negative_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog = builder.create();
		
		dialog.show();
	}

	/**
	 */
	private void checkSuddenEvent(String m_user_id, String m_comp_cd) {
		String url = getString(R.string.base_uri) + CHECK_SUDDEN_EVENT;
		HashMap<String, String> parameters = new HashMap<String, String>();
		
		parameters.put(CommonUtil.USER_ID, m_user_id);
		parameters.put(CommonUtil.COMP_CD, m_comp_cd);
		
		Context context = m_context;
		boolean progressVisible = true;
		boolean progressCancelable = false;
		
		int id = 111;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0 ) {
					if(!list.get(0).containsKey(CommonUtil.ERROR))
						showSuddenEventJoinDialog(list);
				}
				return false;
			}

		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible, progressCancelable);
		task.execute();
	}

}