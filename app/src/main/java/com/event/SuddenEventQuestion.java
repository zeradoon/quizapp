package com.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;

import com.common.CommonUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.skcc.portal.skmsquiz.R;

/**
 * @author jungungi
 * 
 */
public class SuddenEventQuestion extends Activity implements OnClickListener,
		OnCheckedChangeListener, TextWatcher {

	// private static final String QUESTION_TYPE_CODE = "QUESTION_TYPE_CODE";
	//
	// private static final String QUESTION_TYPE = "QUESTION_TYPE";

	private static final int SUDDEN_QUIZ_ALERT_FINISH = 1;

	// validate sudden event
	private static final String GET_QUIZ_QUESTION_LIST_URL = "getEventQuizQuestionList.do";
	private static final String GET_QUIZ_QUESTION_ANSWER_LIST_URL = "getEventQuizQuestionAnswerList.do";
	private static final String REGITER_EVENT_RESULT_DETAIL_URL = "regiterEventResultDetail.do";

	// logcat
	private static final String LOG_TAG = SuddenEventQuestion.class
			.getSimpleName();

	// dialog type
	// private static final int SuddenEvent_ALERT_FINISH = 1;
	// private static final int SuddenEvent_ALERT_SUDDEN_JOIN = 2;

	//
	// private Activity activity = this;
	//
	private Context mContext = this;

	// User Info
	private String m_user_id;
	
	/* member representing user dept */
	private String m_user_dept;
	
	/* member representing user company code */
	private String m_comp_cd;

	// Event Quiz Info
	private String mQuizId;

	// Event ID
	private String mEventId;

	// Event Quiz Question
	private ArrayList<HashMap<String, String>> mQuestionList;

	// Event Quiz Question Answer
	private ArrayList<HashMap<String, String>> mQuestionAnswerList;

	// Event Quiz Question User Answer
	private ArrayList<HashMap<String, String>> mQuestionUserAnswerList;

	/* members to store userid and password for auto login */
	public static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREF_USERID = "userid";
	private static final String PREF_USERDEPT = "userdept";
	// question area
	private ScrollView mShortanswerQuestionScrollview;
	private ScrollView mMutlipleChoiceQuestionScrollview;
	private ScrollView mOxQuestionScrollview;

	// shortanswer area
	private TextView mShortAnswerQuestionText;
	private EditText mShortAnswerQuestionUserAnswer;

	// ox area
	private TextView mOxQustionText;
	private ImageButton mOxQuestionAnswerOButton;
	private ImageButton mOxQuestionAnswerXButton;

	// multiple question area
	private TextView mMultipleQuestionText;
	private RadioGroup mMultipleQuestionAnswerRadioGroup;

	// save button
	private ImageButton mSaveButton;

	// timer text
	private TextView tvTimer;

	//
	private MyCounter myCounter;

	// QUIZ_COMPLETION_TIME
	private int mEventCounterEndTime = 1000 * 60;
	
	private int mEventCounterInterval = 50;

	private String mQuestionUserAnswer = "";

	private String mQuestionId;

	private String mQuestionTypeName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sudden_event_question);

		mShortanswerQuestionScrollview = (ScrollView) findViewById(R.id.shortanswer_question_scrollview);
		mMutlipleChoiceQuestionScrollview = (ScrollView) findViewById(R.id.mutliple_choice_question_scrollview);
		mOxQuestionScrollview = (ScrollView) findViewById(R.id.ox_question_scrollview);

		// shortanswer question
		mShortAnswerQuestionText = (TextView) findViewById(R.id.shortanswer_question_text);
		mShortAnswerQuestionUserAnswer = (EditText) findViewById(R.id.shortanswer_question_user_answer);
		mShortAnswerQuestionUserAnswer.addTextChangedListener(this);

		// ox question
		mOxQustionText = (TextView) findViewById(R.id.ox_qustion_text);
		mOxQuestionAnswerOButton = (ImageButton) findViewById(R.id.btn_ox_qustion_answer_o);
		mOxQuestionAnswerXButton = (ImageButton) findViewById(R.id.btn_ox_qustion_answer_x);

		mOxQuestionAnswerOButton.setOnClickListener(this);
		mOxQuestionAnswerXButton.setOnClickListener(this);

		// multiple_question_text
		mMultipleQuestionText = (TextView) findViewById(R.id.multiple_question_text);
		mMultipleQuestionAnswerRadioGroup = (RadioGroup) findViewById(R.id.btn_multiple_question_answer_radio_group);
		mMultipleQuestionAnswerRadioGroup.setOnCheckedChangeListener(this);

		// save button
		mSaveButton = (ImageButton) findViewById(R.id.btn_sudden_save);
		mSaveButton.setOnClickListener(this);

		// timer
		tvTimer = (TextView) findViewById(R.id.tv_sudden_event_question_clock_time);
		myCounter = new MyCounter(mEventCounterEndTime, mEventCounterInterval, tvTimer);
		
		/* set values input by user to member variables */
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_user_dept = getIntent().getStringExtra(CommonUtil.USER_DEPT);
		m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);

		initEventInfo();

		getEventQuizQuestionList(mQuizId);

		getEventQuizQuestionAnswerList(mQuizId);
	}

	/**
	 * This method is used to alert quiz finish dialog box when escape key is pressed.
	 * 
	 * @param keyCode
	 * @param event
	 * 
	 * @return boolean
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(LOG_TAG, "onKeyDown : " + keyCode);

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog(SUDDEN_QUIZ_ALERT_FINISH);

			return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * This method is used to create quiz finish dialog box when escape key is pressed.
	 * 
	 * @param id
	 * 
	 * @return Dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		
		switch (id) {
			case SUDDEN_QUIZ_ALERT_FINISH:
				dialog = showSuddenQuizAlertFinish();
				break;
			default:
				dialog = null;
		}
		
		return dialog;
	}
	
	/**
	 * This method is used to display quiz finish dialog box when escape key is pressed.
	 * 
	 * @return Dialog
	 */
	private Dialog showSuddenQuizAlertFinish() {
		Dialog dialog;
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(R.string.suddenquiz_alert_finish_title);
		builder.setMessage(R.string.suddenquiz_alert_finish_message);
		builder.setPositiveButton(
				R.string.suddenquiz_alert_finish_positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						registerEventResult(m_user_id, mEventId, m_user_dept, mQuestionId, mQuestionUserAnswer, mQuestionTypeName);
						dialog.dismiss(); // 닫기
						finish();
					}
				});
		builder.setNegativeButton(
				R.string.suddenquiz_alert_finish_negative_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 닫기
					}
				});
		dialog = builder.create();
		return dialog;
	}
	
	@Override
	protected void onResume() {
		myCounter.start();
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		myCounter.cancel();
		super.onStop();
	}
	

	@Override
	public void onClick(View v) {
		if (v == mSaveButton) {
			if (mQuestionUserAnswerList == null) {
				mQuestionUserAnswerList = new ArrayList<HashMap<String, String>>();
			}
			myCounter.cancel();

			registerEventResult(m_user_id, mEventId, m_user_dept, mQuestionId, mQuestionUserAnswer, mQuestionTypeName);
		} else if (v == mOxQuestionAnswerOButton) {
			mQuestionUserAnswer = CommonUtil.QUES_TYPE_OX_ANSWER_O;
			mOxQuestionAnswerOButton
					.setImageResource(R.drawable.btn_o_selector_on);
			mOxQuestionAnswerXButton
					.setImageResource(R.drawable.btn_x_selector);

		} else if (v == mOxQuestionAnswerXButton) {
			mQuestionUserAnswer = CommonUtil.QUES_TYPE_OX_ANSWER_X;
			mOxQuestionAnswerOButton
					.setImageResource(R.drawable.btn_o_selector);
			mOxQuestionAnswerXButton
					.setImageResource(R.drawable.btn_x_selector_on);
		}

	}

	@Override
	public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
		if (rGroup == mMultipleQuestionAnswerRadioGroup) {
			RadioButton rb = (RadioButton) findViewById(checkedId);
			mQuestionUserAnswer = rb.getText().toString();
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// Toast.makeText(mContext, s.toString(), Toast.LENGTH_SHORT).show();
		mQuestionUserAnswer = s.toString();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
	}

	/**
	 */
	private void getEventQuizQuestionList(String mQuizId) {
		String url = getString(R.string.base_uri) + GET_QUIZ_QUESTION_LIST_URL;
		HashMap<String, String> parameters = new HashMap<String, String>();

		parameters.put(CommonUtil.QUIZ_ID, mQuizId);

		Context context = mContext;
		boolean progressVisible = true;
		boolean progressCancelable = false;
		int id = 111;

		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0) {
					if (!list.get(0).containsKey(CommonUtil.ERROR)) {

						for (HashMap<String, String> hm : list) {
							for (Map.Entry<String, String> entry : hm
									.entrySet()) {
								Log.d(LOG_TAG,
										"parameter key : " + entry.getKey()
												+ " = value : "
												+ entry.getValue());
							}
						}
						mQuestionList = list;

						Log.d(LOG_TAG, "question list arrive");

						if (mQuestionAnswerList != null) {
							setQuestion(0);
						}
					}
				}
				return false;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible,
				progressCancelable);
		task.execute();
	}

	/**
	 */
	private void getEventQuizQuestionAnswerList(String mQuizId) {
		String url = getString(R.string.base_uri)
				+ GET_QUIZ_QUESTION_ANSWER_LIST_URL;
		HashMap<String, String> parameters = new HashMap<String, String>();

		parameters.put(CommonUtil.QUIZ_ID, mQuizId);

		Context context = mContext;
		boolean progressVisible = true;
		boolean progressCancelable = false;
		int id = 222;

		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0) {
					if (!list.get(0).containsKey(CommonUtil.ERROR)) {

						for (HashMap<String, String> hm : list) {
							for (Map.Entry<String, String> entry : hm
									.entrySet()) {
								Log.d(LOG_TAG,
										"parameter key : " + entry.getKey()
												+ " = value : "
												+ entry.getValue());
							}
						}
						mQuestionAnswerList = list;

						Log.d(LOG_TAG, "question answer list arrive");

						if (mQuestionList != null) {
							setQuestion(0);
						}
					}
				}
				return false;
			}

		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible,
				progressCancelable);
		task.execute();
	}

	/**
	 */
	private void registerEventResult(String m_user_id, String mEventId, String mUserDept, String mQuestionId, String mQuestionUserAnswer, String mQuestionTypeName) {
		String url = getString(R.string.base_uri)
				+ REGITER_EVENT_RESULT_DETAIL_URL;
		HashMap<String, String> parameters = new HashMap<String, String>();

		// http://localhost:8080/SKMS_WAS/regiterEventResultDetail.do?
		// EVENT_ID=1&
		// USER_ID=jungungi&
		// USER_DEPT=portal&
		// SCORE=100&
		// QUESTION_ID_LIST=317|318&
		// USER_ANSWER_LIST=O|X&
		// ANSWER_RESULT_LIST=RIGHT|WRONG

		parameters.put(CommonUtil.USER_ID, m_user_id);
		parameters.put(CommonUtil.EVENT_ID, mEventId);
		parameters.put(CommonUtil.USER_DEPT, mUserDept);
		parameters.put(CommonUtil.SCORE, "0");
		parameters.put(CommonUtil.QUESTION_ID_LIST, mQuestionId);
		parameters.put(CommonUtil.USER_ANSWER_LIST, mQuestionUserAnswer);
		parameters.put(CommonUtil.ANSWER_RESULT_LIST, Boolean.toString(checkQuestionAnswer(mQuestionId, mQuestionUserAnswer, mQuestionTypeName)));

		Context context = mContext;
		boolean progressVisible = true;
		boolean progressCancelable = false;

		int id = 222;

		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0) {
					showSuddenEventFinishAlertDialog();
				}
				return false;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible,
				progressCancelable);
		task.execute();
	}

	private void initEventInfo() {
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) getIntent()
				.getExtras().get(CommonUtil.LIST);

		for (HashMap<String, String> hm : list) {
			for (Map.Entry<String, String> entry : hm.entrySet()) {
				Log.d(LOG_TAG, "parameter key : " + entry.getKey() + " = value : " + entry.getValue());
			}
		}

		mEventId = list.get(0).get(CommonUtil.ID);
		mQuizId = list.get(0).get(CommonUtil.QUIZ_ID);
	}

	// question type
	// 3 OX
	// 4 객관식
	// 5 주관식
	private void setQuestion(int pos) {
		mQuestionId = mQuestionList.get(pos).get(CommonUtil.ID);
		// mQuestionTypeCode = Integer.parseInt(mQuestionList.get(pos).get(
		// QUESTION_TYPE_CODE));
		mQuestionTypeName = mQuestionList.get(pos).get(CommonUtil.QUESTION_TYPE_NM);
		mQuestionUserAnswer = "";

		if (mQuestionTypeName.equalsIgnoreCase(CommonUtil.QUES_TYPE_OX)) {
			mOxQuestionScrollview.setVisibility(View.VISIBLE);
			mMutlipleChoiceQuestionScrollview.setVisibility(View.GONE);
			mShortanswerQuestionScrollview.setVisibility(View.GONE);

			mOxQustionText.setText(mQuestionList.get(pos).get(CommonUtil.QUESTIONTEXT));
		} else if (mQuestionTypeName.equalsIgnoreCase(CommonUtil.QUES_TYPE_MULTI_CHOICE)) {
			mOxQuestionScrollview.setVisibility(View.GONE);
			mMutlipleChoiceQuestionScrollview.setVisibility(View.VISIBLE);
			mShortanswerQuestionScrollview.setVisibility(View.GONE);

			setMultipleQuestion(pos);
		} else if (mQuestionTypeName.equalsIgnoreCase(CommonUtil.QUES_TYPE_SHORT_ANSWER)) {
			mOxQuestionScrollview.setVisibility(View.GONE);
			mMutlipleChoiceQuestionScrollview.setVisibility(View.GONE);
			mShortanswerQuestionScrollview.setVisibility(View.VISIBLE);

			mShortAnswerQuestionText.setText(mQuestionList.get(pos).get(
					CommonUtil.QUESTIONTEXT));
		}
	}

	private void setMultipleQuestion(int pos) {
		mMultipleQuestionAnswerRadioGroup.removeAllViews();

		mMultipleQuestionText.setText(mQuestionList.get(pos).get(CommonUtil.QUESTIONTEXT));

		Collections.shuffle(mQuestionAnswerList);

		for (HashMap<String, String> data : mQuestionAnswerList) {
			if (data.get(CommonUtil.QUESTION_ID).equals(mQuestionList.get(pos).get(CommonUtil.ID))) {
				RadioButton myRadioButton = new RadioButton(this);
				myRadioButton.setText(data.get(CommonUtil.ANSWER));
				myRadioButton.setButtonDrawable(null);
				myRadioButton.setTextColor(Color
						.parseColor(CommonUtil.MULTI_ANSWER_TEXT_COLOR));
				myRadioButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

				mMultipleQuestionAnswerRadioGroup.addView(myRadioButton);
			}
		}
		mMultipleQuestionAnswerRadioGroup.invalidate();
	}

	private boolean checkQuestionAnswer(String questionId, String userAnswer,
			String questionTypeName) {
		for (HashMap<String, String> data : mQuestionAnswerList) {
			if (data.get(CommonUtil.QUESTION_ID).equals(questionId)
					&& data.get(CommonUtil.ANSWER_TYPE_CODE).equals(CommonUtil.ANSWER_TYPE_PASS)) {

				// OX, MultiChoice
				if (questionTypeName.equalsIgnoreCase(CommonUtil.QUES_TYPE_MULTI_CHOICE)
						|| questionTypeName.equalsIgnoreCase(CommonUtil.QUES_TYPE_OX)) {
					return (data.get(CommonUtil.ANSWER).equalsIgnoreCase(userAnswer));
				}
				// Short Answer
				else if (questionTypeName.equalsIgnoreCase(CommonUtil.QUES_TYPE_SHORT_ANSWER)) {
					if (data.get(CommonUtil.ANSWER).equalsIgnoreCase(userAnswer)) {
						return true;
					} else {
						String[] simAnswerList = data.get(CommonUtil.SIMILAR_ANSWER)
								.split(CommonUtil.SIM_ANSWER_SEPERATOR);
						for (String answer : simAnswerList) {
							if (answer.equalsIgnoreCase(userAnswer)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private void showSuddenEventFinishAlertDialog() {
		Dialog dialog;
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(R.string.sudden_event_finish_alert_title);
		builder.setMessage(R.string.sudden_event_finish_alert_message);
		builder.setPositiveButton(
				R.string.quizmain_alert_finsh_positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 닫기
						finish();
					}
				});

		dialog = builder.create();
		dialog.show();
	}
	
	private void showSuddenEventTimeCompleteAlertDialog() {
		Dialog dialog;
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(R.string.sudden_event_time_complete_alert_title);
		builder.setMessage(R.string.sudden_event_time_complete_alert_message);
		builder.setCancelable(false);		
		builder.setPositiveButton(
				R.string.quizmain_alert_finsh_positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 닫기
						registerEventResult(m_user_id, mEventId, m_user_dept, mQuestionId, mQuestionUserAnswer, mQuestionTypeName);
					}
				});

		dialog = builder.create();
		dialog.show();
	}
	
	/**
	 * 남은시간을 형식에 맞게 변환해줌
	 * */
	private String getTimeFormat(long spandTime) {
		Calendar spandCalendar = Calendar.getInstance();
		spandCalendar.setTimeInMillis(spandTime);

		int minute = spandCalendar.get(Calendar.MINUTE);
		int second = spandCalendar.get(Calendar.SECOND);
		int mils = spandCalendar.get(Calendar.MILLISECOND);

		String str_minute = Integer.toString(minute);
		String str_second = Integer.toString(second);
		String str_mils = Integer.toString((int) (mils / 10));

		if (minute < 10) {
			str_minute = "0" + minute;
		}
		if (second < 10) {
			str_second = "0" + second;
		}
		if (str_mils.length() == 1) {
			str_mils = "00";

		}

		return str_minute + ":" + str_second + ":" + str_mils;
	}

	class MyCounter extends CountDownTimer {
		TextView tv;

		public MyCounter(long millisInFuture, long countDownInterval,
				TextView tv) {
			super(millisInFuture, countDownInterval);			this.tv = tv;
		}

		@Override
		public void onFinish() {
			Log.d(LOG_TAG, "Timer Completed.");
			tv.setText("Timer Completed.");
			showSuddenEventTimeCompleteAlertDialog();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			tv.setText(getTimeFormat(millisUntilFinished));
			Log.d(LOG_TAG, "Timer  : " + getTimeFormat(millisUntilFinished));
		}
	}
	
	
}