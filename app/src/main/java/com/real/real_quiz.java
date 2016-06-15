package com.real;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonUtil;
import com.common.XmlParser;
import com.common.async.AbstractAsyncActivity;
import com.login.UserLogin;
import com.mystudy.MyStudyRoom_Common;
import com.practice.PracticeQuiz;
import com.skcc.portal.skmsquiz.R;

public class real_quiz extends AbstractAsyncActivity {
	public static final String LOG_TAG = real_quiz.class.getSimpleName();
	/** Called when the activity is first created. */

	int real_cnt = 1;
	int q_type = 0;
	int REAL_QUIZ_TOTAL_CNT = 10;
	int intro_q_type = 0;
	int sel_q_type = 0;
	int[] sel_qType = new int[10];
	int real_qtype = 0;
	int intro_qtype = 0;
	int input_cnt = 0;
	String cnt_string = "";
	String my_answer[] = new String[10];
	String correct_answer[] = new String[10];
	int real_jungdab_yn[] = new int[10];
	String real_seq_no[] = new String[10];
	String sim_answer[] = new String[10];
	String your_shortanswer = "";
	String str_cnt2 = "";
	String str = "";
	String Temp_answer_ox = "";
	String your_multiple_answer = "";
	int resultColor = 0xFFFF0000;
	int black = 0xFF000000;
	boolean real_trial = false;
	boolean loop = true;
	String phone_num = "";
	HttpURLConnection conn;
	URL url;
	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> answerList = new ArrayList<HashMap<String, String>>();
	
	int hours, mins, secs;
	double oo, xx, eee, temp;
	public String answer[] = { "Q_ANSWER", "Q_WR_ANSWER1", "Q_WR_ANSWER2",
			"Q_WR_ANSWER3" };
	TextView real_clock;
	long time = 10 * 60 * 1000;
	// long time = 10 * 1000;
	boolean isAlive = false;
	boolean isThread = false;
	TimeCountDown tcd;
	TimeHandler handler;
	boolean re_send = false;
	// boolean real_solving_stop = false;
	int list_size = 0;

	private Context context;
	// private ValuesToRealQuizAsyncTask realquizAsynctask;
	private Activity activity;
	
	public static final String PREFS_NAME = "MyPrefsFile"; 
	private static final String PREF_USERID = "userid";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_REMEMBERME = "true";
	
	private String m_user_id;
	private String m_user_dept;
	private String m_comp_cd;
	
	private static final String REAL_QUIZ = "realQuiz.do";
	private static final String REAL_QUIZ_ANSWERS = "realQuizAnswers.do";
	
	private static final int REAL_QUIZ_ALERT_FINISH = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.real_intro_test);
		
		ImageView m_btn_home = (ImageView)findViewById(R.id.btn_home);
		m_btn_home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(REAL_QUIZ_ALERT_FINISH);
				//finish();
			}
		});

		context = this;
		activity = this;

		real_clock = (TextView) findViewById(R.id.real_clock);
		
		
		m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_user_dept = getIntent().getStringExtra(CommonUtil.USER_DEPT);
		
		// realquizAsynctask = new ValuesToRealQuizAsyncTask();
		new ValuesToRealQuizAsyncTask().execute(m_comp_cd);
		// realquizAsynctask.execute();
		
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
			showDialog(REAL_QUIZ_ALERT_FINISH);

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
	protected Dialog onCreateDialog(int id, Bundle savedDialogInstanceState) {
		Dialog dialog;
		
		switch (id) {
			case REAL_QUIZ_ALERT_FINISH:
				dialog = showRealQuizAlertFinish(savedDialogInstanceState);
				break;
			default:
				dialog = null;
		}
		
		return dialog;
	}
	
	/**
	 * This method is used to display quiz finish dialog box when escape/home key is pressed.
	 * 
	 * @return Dialog
	 */
	private Dialog showRealQuizAlertFinish(final Bundle savedDialogInstanceState) {
		Dialog dialog;
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(R.string.realquiz_alert_finish_title);
		builder.setMessage(R.string.realquiz_alert_finish_message);
		builder.setPositiveButton(
				R.string.realquiz_alert_finish_positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (savedDialogInstanceState != null) {
							String hasParams = savedDialogInstanceState.getString(CommonUtil.IS_DIALOG_HAS_PARAMS);
							String className = savedDialogInstanceState.getString(CommonUtil.ACTIVITY_CLASS);
							
							Intent intent = null;
							Bundle args = new Bundle();
							
							if (hasParams != null) {
								try {
									intent = new Intent(real_quiz.this, Class.forName(className));
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
					        	
								intent.putExtra(CommonUtil.USER_ID, m_user_id);
								intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
								intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
					        	
								startActivity(intent);
								dialog.dismiss(); // 닫기
								finish();
							}
						} else {
							dialog.dismiss(); // 닫기
							finish();
						}
					}
				});
		builder.setNegativeButton(
				R.string.realquiz_alert_finish_negative_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 닫기
					}
				});
		dialog = builder.create();
		return dialog;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("", "onDestroy()");
		super.onDestroy();

		stopTimer();
	}

	private void startTimer() {
		if (!isThread) {
			this.isAlive = true;
			isThread = true;
			tcd = new TimeCountDown(handler);
			tcd.start();
		}
	}

	private void stopTimer() {
		this.isAlive = false;
		this.isThread = false;
		// tcd.interrupt();
		tcd = null;
	}

	/*
	 * 버튼 클릭시 동작 수행.
	 */
	public void monclick(View v) {
		String quesType = "";
		switch (v.getId()) {

		// ############################ 이전버튼 처리
		case R.id.btn_real_previous_multiple:
		case R.id.btn_real_previous_ox:
		case R.id.btn_real_previous_shortanswer:
			real_cnt--;
			input_cnt = real_cnt - 1;
			
			quesType = (list.get(input_cnt)).get(CommonUtil.QUES_TYPE);
			
			if (CommonUtil.QUES_TYPE_OX.equalsIgnoreCase(quesType)) {
				sel_q_type = 1;
	        } else if (CommonUtil.QUES_TYPE_MULTI_CHOICE.equalsIgnoreCase(quesType)) {
	        	sel_q_type = 2;
	        } else if (CommonUtil.QUES_TYPE_SHORT_ANSWER.equalsIgnoreCase(quesType)) {
	        	sel_q_type = 3;
	        }
			
			//sel_q_type = Integer.valueOf((list.get(input_cnt)).get("Q_TYPE"));
			
			real_view_select(sel_q_type, real_cnt);
			// 뷰 선택하는 함수
			// 화면에 문제뿌리는 함수
			break;

		// ############################ 다음버튼 처리
		case R.id.btn_real_next_multiple:
		case R.id.btn_real_next_ox:
		case R.id.btn_real_next_shortanswer:
			real_cnt++;
			input_cnt = real_cnt - 1;
			
			quesType = (list.get(input_cnt)).get(CommonUtil.QUES_TYPE);
			
			if (CommonUtil.QUES_TYPE_OX.equalsIgnoreCase(quesType)) {
				sel_q_type = 1;
	        } else if (CommonUtil.QUES_TYPE_MULTI_CHOICE.equalsIgnoreCase(quesType)) {
	        	sel_q_type = 2;
	        } else if (CommonUtil.QUES_TYPE_SHORT_ANSWER.equalsIgnoreCase(quesType)) {
	        	sel_q_type = 3;
	        }
			//sel_q_type = Integer.valueOf((list.get(input_cnt)).get("Q_TYPE"));

			str_cnt2 = Integer.toString(real_cnt);
			str = Integer.toString(sel_q_type);

			real_view_select(sel_q_type, real_cnt);

			// 뷰 선택하는 함수
			// 화면에 문제뿌리는 함수
			break;

		// ############################ 답지로 이동버튼
		// case R.id.btn_real_multiple_answerlist:
		case R.id.btn_real_quiz_answerlist:
			// case R.id.btn_real_shortanswer_answerlist:

			showAnswerList();

			break;

		// ############################## 답선택버튼
		case R.id.btn_real_quiz_answer_o:
			Temp_answer_ox = CommonUtil.QUES_TYPE_OX_ANSWER_O;
			setOXButtonImage();
			break;
		case R.id.btn_real_quiz_answer_x:
			Temp_answer_ox = CommonUtil.QUES_TYPE_OX_ANSWER_X;
			setOXButtonImage();
			break;

		case R.id.btn_real_quiz_answer_radio1:
			RadioButton rg1 = (RadioButton) findViewById(R.id.btn_real_quiz_answer_radio1);
			your_multiple_answer = rg1.getText().toString();

			break;

		case R.id.btn_real_quiz_answer_radio2:
			RadioButton rg2 = (RadioButton) findViewById(R.id.btn_real_quiz_answer_radio2);
			your_multiple_answer = rg2.getText().toString();

			break;

		case R.id.btn_real_quiz_answer_radio3:
			RadioButton rg3 = (RadioButton) findViewById(R.id.btn_real_quiz_answer_radio3);
			your_multiple_answer = rg3.getText().toString();

			break;

		case R.id.btn_real_quiz_answer_radio4:
			RadioButton rg4 = (RadioButton) findViewById(R.id.btn_real_quiz_answer_radio4);
			your_multiple_answer = rg4.getText().toString();

			break;

		// ############################ 답안지 제출버튼 처리
		case R.id.btn_real_summitsheet:

			if (CommonUtil.showNetworkAlert(real_quiz.this)) {

				re_send = true;
				
				tcd = null;
				stopTimer();

//답체크 루프시작				
				for (int i = 0; i < 10; i++) {
					quesType = (list.get(input_cnt)).get(CommonUtil.QUES_TYPE);
					int sel_type = 1;
					if (CommonUtil.QUES_TYPE_OX.equalsIgnoreCase(quesType)) {
						sel_type = 1;
			        } else if (CommonUtil.QUES_TYPE_MULTI_CHOICE.equalsIgnoreCase(quesType)) {
			        	sel_type = 2;
			        } else if (CommonUtil.QUES_TYPE_SHORT_ANSWER.equalsIgnoreCase(quesType)) {
			        	sel_type = 3;
			        }
					
					//int sel_type = Integer.valueOf((list.get(i)).get("Q_TYPE"));
					if(sel_type!=3){    // 1,2번 유형인경우
						if (my_answer[i].equals(correct_answer[i]))
							real_jungdab_yn[i] = 0;
					    else
					    	real_jungdab_yn[i] = 1;
						}
					else	{           //3번 유형인경우
						String str_my_answer      = my_answer[i].toLowerCase().trim().replaceAll(" ",""); 
	                    String str_correct_answer = correct_answer[i].toLowerCase().trim().replaceAll(" ","");
						if ("".equalsIgnoreCase(sim_answer[i].trim())) {
							if (str_my_answer.equalsIgnoreCase(str_correct_answer))
								//replaceAll(" ","") 
								real_jungdab_yn[i] = 0;
							else
								real_jungdab_yn[i] = 1;
						}// 유사답안 없을땐 기존과 같이 처리

						// 유사답안 있을경우
						else {
							String delimiter = ";";
							if (sim_answer[i].indexOf(";") != -1) {
								delimiter = ";";
							} else if (sim_answer[i].indexOf("|") != -1) {
								delimiter = "|";
							} else if (sim_answer[i].indexOf(",") != -1) {
								delimiter = ",";
							}
							String[] sim_Array = sim_answer[i].split(delimiter);

							for (int k = 0; k < sim_Array.length; k++) {
								
								if (str_my_answer
										.equals(sim_Array[k].toLowerCase().trim().replaceAll(" ",""))) {
									real_jungdab_yn[i] = 0;
									
									break;
								} 
								else
									real_jungdab_yn[i] = 1;
								}// 중첩for문 끝
							if(str_my_answer.equalsIgnoreCase(str_correct_answer))
								real_jungdab_yn[i] = 0;
						}
						}
}// for문 끝

				Intent intent = new Intent(real_quiz.this,	real_quiz_resultlist.class);

				Bundle real_result = new Bundle();
				real_result.putIntArray(CommonUtil.REAL_JUNGDAB, real_jungdab_yn);
				real_result.putStringArray(CommonUtil.REAL_SEQ, real_seq_no);
				real_result.putString(CommonUtil.USER_ID, m_user_id);
				real_result.putString(CommonUtil.COMP_CD, m_comp_cd);
				
				intent.putExtras(real_result);
				startActivity(intent);
				finish();
			}
			break;

		// ############################ 저장버튼 처리
		case R.id.btn_real_save_multiple:
			input_cnt = real_cnt - 1;
			my_answer[input_cnt] = your_multiple_answer;

			if (your_multiple_answer.equals("")) {
				CommonUtil.showAlert(context, CommonUtil.ALERT_TITLE_NOTICE,
						CommonUtil.ALERT_MESSAGE_OBJECTIVE, CommonUtil.ALERT_POSITIVE_BUTTON);
			} else {
				Toast.makeText(context, R.string.realquiz_store_answer, Toast.LENGTH_SHORT).show();
			}
			
			break;

		case R.id.btn_real_save_ox:
			input_cnt = real_cnt - 1;
			my_answer[input_cnt] = Temp_answer_ox;
			
			if (Temp_answer_ox.equals("")) {
				CommonUtil.showAlert(context, CommonUtil.ALERT_TITLE_NOTICE,
						CommonUtil.ALERT_MESSAGE_OXQUIZ, CommonUtil.ALERT_POSITIVE_BUTTON);
			} else {
				Toast.makeText(context, R.string.realquiz_store_answer, Toast.LENGTH_SHORT).show();
			}
			
			break;

		case R.id.btn_real_save_shortanswer:
			input_cnt = real_cnt - 1;
			str_cnt2 = Integer.toString(real_cnt);
			EditText et = (EditText) findViewById(R.id.youranswer_real_edittext);
			your_shortanswer = et.getText().toString();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
			my_answer[input_cnt] = your_shortanswer;

			if (!"".equals(et.getText().toString())) {
				Toast.makeText(context, R.string.realquiz_store_answer, Toast.LENGTH_SHORT).show();
			} else {
				CommonUtil.showAlert(real_quiz.this,
						CommonUtil.ALERT_TITLE_NOTICE,
						CommonUtil.ALERT_MESSAGE_SUBJECTIVE,
						CommonUtil.ALERT_POSITIVE_BUTTON);
			}
			break;

		// 바로가기 버튼 처리
		case R.id.btn_real_gotoquestion1:
			//real_view_select(Integer.valueOf((list.get(0)).get("Q_TYPE")), 1);
			real_view_select(sel_qType[0], 1);
			break;
		case R.id.btn_real_gotoquestion2:
			//real_view_select(Integer.valueOf((list.get(1)).get("Q_TYPE")), 2);
			real_view_select(sel_qType[1], 2);
			break;
		case R.id.btn_real_gotoquestion3:
			//real_view_select(Integer.valueOf((list.get(2)).get("Q_TYPE")), 3);
			real_view_select(sel_qType[2], 3);
			break;
		case R.id.btn_real_gotoquestion4:
			//real_view_select(Integer.valueOf((list.get(3)).get("Q_TYPE")), 4);
			real_view_select(sel_qType[3], 4);
			break;
		case R.id.btn_real_gotoquestion5:
			//real_view_select(Integer.valueOf((list.get(4)).get("Q_TYPE")), 5);
			real_view_select(sel_qType[4], 5);
			break;
		case R.id.btn_real_gotoquestion6:
			//real_view_select(Integer.valueOf((list.get(5)).get("Q_TYPE")), 6);
			real_view_select(sel_qType[5], 6);
			break;
		case R.id.btn_real_gotoquestion7:
			//real_view_select(Integer.valueOf((list.get(6)).get("Q_TYPE")), 7);
			real_view_select(sel_qType[6], 7);
			break;
		case R.id.btn_real_gotoquestion8:
			//real_view_select(Integer.valueOf((list.get(7)).get("Q_TYPE")), 8);
			real_view_select(sel_qType[7], 8);
			break;
		case R.id.btn_real_gotoquestion9:
			//real_view_select(Integer.valueOf((list.get(8)).get("Q_TYPE")), 9);
			real_view_select(sel_qType[8], 9);
			break;
		case R.id.btn_real_gotoquestion10:
			//real_view_select(Integer.valueOf((list.get(9)).get("Q_TYPE")), 10);
			real_view_select(sel_qType[9], 10);
			break;

		}

	}

	private void showAnswerList() {
		findViewById(R.id.real_quiz_ox).setVisibility(LinearLayout.GONE);
		findViewById(R.id.real_quiz_multiple).setVisibility(
				LinearLayout.GONE);
		findViewById(R.id.real_quiz_shortanswer).setVisibility(
				LinearLayout.GONE);
		findViewById(R.id.real_quiz_answerlist).setVisibility(
				LinearLayout.VISIBLE);

		if (my_answer[0] == "") {
			((TextView) findViewById(R.id.real_answer_q1))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_answer_a1))
					.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			((TextView) findViewById(R.id.real_answer_q1)).setText("문제1");
			((TextView) findViewById(R.id.real_answer_q1))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_answer_a1)).setText(CommonUtil.ANSWER_SAVED);
			((TextView) findViewById(R.id.real_answer_a1))
					.setTextColor(ColorStateList.valueOf(black));
		}

		if (my_answer[1] == "") {
			((TextView) findViewById(R.id.real_answer_q2))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_answer_a2))
					.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			((TextView) findViewById(R.id.real_answer_q2)).setText("문제2");
			((TextView) findViewById(R.id.real_answer_q2))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_answer_a2)).setText(CommonUtil.ANSWER_SAVED);
			((TextView) findViewById(R.id.real_answer_a2))
					.setTextColor(ColorStateList.valueOf(black));
		}

		if (my_answer[2] == "") {
			((TextView) findViewById(R.id.real_answer_q3))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_answer_a3))
					.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			((TextView) findViewById(R.id.real_answer_q3)).setText("문제3");
			((TextView) findViewById(R.id.real_answer_q3))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_answer_a3)).setText(CommonUtil.ANSWER_SAVED);
			((TextView) findViewById(R.id.real_answer_a3))
					.setTextColor(ColorStateList.valueOf(black));
		}

		if (my_answer[3] == "") {
			((TextView) findViewById(R.id.real_answer_q4))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_answer_a4))
					.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			((TextView) findViewById(R.id.real_answer_q4)).setText("문제4");
			((TextView) findViewById(R.id.real_answer_q4))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_answer_a4)).setText(CommonUtil.ANSWER_SAVED);
			((TextView) findViewById(R.id.real_answer_a4))
					.setTextColor(ColorStateList.valueOf(black));
		}

		if (my_answer[4] == "") {
			((TextView) findViewById(R.id.real_answer_q5))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_answer_a5))
					.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			((TextView) findViewById(R.id.real_answer_q5)).setText("문제5");
			((TextView) findViewById(R.id.real_answer_q5))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_answer_a5)).setText(CommonUtil.ANSWER_SAVED);
			((TextView) findViewById(R.id.real_answer_a5))
					.setTextColor(ColorStateList.valueOf(black));
		}

		if (my_answer[5] == "") {
			((TextView) findViewById(R.id.real_answer_q6))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_answer_a6))
					.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			((TextView) findViewById(R.id.real_answer_q6)).setText("문제6");
			((TextView) findViewById(R.id.real_answer_q6))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_answer_a6)).setText(CommonUtil.ANSWER_SAVED);
			((TextView) findViewById(R.id.real_answer_a6))
					.setTextColor(ColorStateList.valueOf(black));
		}

		if (my_answer[6] == "") {
			((TextView) findViewById(R.id.real_answer_q7))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_answer_a7))
					.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			((TextView) findViewById(R.id.real_answer_q7)).setText("문제7");
			((TextView) findViewById(R.id.real_answer_q7))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_answer_a7)).setText(CommonUtil.ANSWER_SAVED);
			((TextView) findViewById(R.id.real_answer_a7))
					.setTextColor(ColorStateList.valueOf(black));
		}

		if (my_answer[7] == "") {
			((TextView) findViewById(R.id.real_answer_q8))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_answer_a8))
					.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			((TextView) findViewById(R.id.real_answer_q8)).setText("문제8");
			((TextView) findViewById(R.id.real_answer_q8))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_answer_a8)).setText(CommonUtil.ANSWER_SAVED);
			((TextView) findViewById(R.id.real_answer_a8))
					.setTextColor(ColorStateList.valueOf(black));
		}

		if (my_answer[8] == "") {
			((TextView) findViewById(R.id.real_answer_q9))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_answer_a9))
					.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			((TextView) findViewById(R.id.real_answer_q9)).setText("문제9");
			((TextView) findViewById(R.id.real_answer_q9))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_answer_a9)).setText(CommonUtil.ANSWER_SAVED);
			((TextView) findViewById(R.id.real_answer_a9))
					.setTextColor(ColorStateList.valueOf(black));
		}

		if (my_answer[9] == "") {
			((TextView) findViewById(R.id.real_answer_q10))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_answer_a10))
					.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			((TextView) findViewById(R.id.real_answer_q10)).setText("문제10");
			((TextView) findViewById(R.id.real_answer_q10))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_answer_a10)).setText(CommonUtil.ANSWER_SAVED);
			((TextView) findViewById(R.id.real_answer_a10))
					.setTextColor(ColorStateList.valueOf(black));
		}
	}

	private void setOXButtonImage() {
		ImageButton oButton = (ImageButton) findViewById(R.id.btn_real_quiz_answer_o);
		ImageButton xButton = (ImageButton) findViewById(R.id.btn_real_quiz_answer_x);
		
		if (Temp_answer_ox.equalsIgnoreCase(CommonUtil.QUES_TYPE_OX_ANSWER_O)) {
			oButton.setImageResource(R.drawable.btn_o_selector_on);
			xButton.setImageResource(R.drawable.btn_x_selector);
		} else if (Temp_answer_ox.equalsIgnoreCase(CommonUtil.QUES_TYPE_OX_ANSWER_X)) {
			oButton.setImageResource(R.drawable.btn_o_selector);
			xButton.setImageResource(R.drawable.btn_x_selector_on);
		} else {
			oButton.setImageResource(R.drawable.btn_o_selector);
			xButton.setImageResource(R.drawable.btn_x_selector);
		}

	}

	/**
	 * @param q_type
	 * @param cur_cnt
	 */
	public void real_view_select(int q_type, int cur_cnt) {

		real_view_init();

		/**
		 * 현재 타이머가 살아 있을때만 문제 이동 가능 그렇지 않으면 답안지로 이동
		 */
		if (isAlive) {
			LinearLayout curView = null;

			if (q_type == 1) {

				real_cnt = cur_cnt;
				input_cnt = cur_cnt - 1;
				curView = (LinearLayout) findViewById(R.id.real_quiz_ox);
				
				findViewById(R.id.real_quiz_ox).setVisibility(
						LinearLayout.VISIBLE);
				findViewById(R.id.real_quiz_multiple).setVisibility(
						LinearLayout.GONE);
				findViewById(R.id.real_quiz_shortanswer).setVisibility(
						LinearLayout.GONE);
				findViewById(R.id.real_quiz_answerlist).setVisibility(
						LinearLayout.GONE);

				cnt_string = CommonUtil.QUESTION_STRING + Integer.toString(cur_cnt);
				
				if (cur_cnt == 1) {
					findViewById(R.id.btn_real_previous_ox).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_real_next_ox).setVisibility(LinearLayout.VISIBLE);
				} else if (cur_cnt == 10) {
					findViewById(R.id.btn_real_next_ox).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_real_previous_ox).setVisibility(LinearLayout.VISIBLE);
				} else {
					findViewById(R.id.btn_real_next_ox).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_real_previous_ox).setVisibility(LinearLayout.VISIBLE);
				}
				
				String q_ox = String.valueOf((list.get(input_cnt))
						.get(CommonUtil.Q_QUESTION));
				TextView ox_tv1 = ((TextView) findViewById(R.id.real_quiz_1_question));
				TextView ox_tv2 = ((TextView) findViewById(R.id.real_quiz_num1));

				ox_tv1.setText(q_ox);
				ox_tv2.setText(cnt_string);

				Temp_answer_ox = my_answer[input_cnt];
				setOXButtonImage();

			} else if (q_type == 2) {
				RadioGroup rGroup = (RadioGroup) findViewById(R.id.btn_real_quiz_answer_radio);
				rGroup.clearCheck();

				findViewById(R.id.btn_real_quiz_answer_radio).setVisibility(LinearLayout.GONE);
				real_cnt = cur_cnt;
				input_cnt = cur_cnt - 1;
				curView = (LinearLayout) findViewById(R.id.real_quiz_multiple);
				
				findViewById(R.id.real_quiz_ox).setVisibility(LinearLayout.GONE);
				findViewById(R.id.real_quiz_multiple).setVisibility(LinearLayout.VISIBLE);
				findViewById(R.id.real_quiz_shortanswer).setVisibility(LinearLayout.GONE);
				findViewById(R.id.real_quiz_answerlist).setVisibility(LinearLayout.GONE);
				findViewById(R.id.btn_real_quiz_answer_radio).setVisibility(LinearLayout.VISIBLE);
				
				if (cur_cnt == 1) {
					findViewById(R.id.btn_real_previous_multiple).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_real_next_multiple).setVisibility(LinearLayout.VISIBLE);
				} else if (cur_cnt == 10) {
					findViewById(R.id.btn_real_next_multiple).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_real_previous_multiple).setVisibility(LinearLayout.VISIBLE);
				} else {
					findViewById(R.id.btn_real_next_multiple).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_real_previous_multiple).setVisibility(LinearLayout.VISIBLE);
				}
				
				cnt_string = CommonUtil.QUESTION_STRING + Integer.toString(cur_cnt);

				/*
				 * ((TextView) findViewById(R.id.btn_real_quiz_answer_radio1))
				 * .setText((list.get(input_cnt)).get("Q_ANSWER")); ((TextView)
				 * findViewById(R.id.btn_real_quiz_answer_radio2))
				 * .setText((list.get(input_cnt)).get("Q_WR_ANSWER1"));
				 * ((TextView) findViewById(R.id.btn_real_quiz_answer_radio3))
				 * .setText((list.get(input_cnt)).get("Q_WR_ANSWER2"));
				 * ((TextView) findViewById(R.id.btn_real_quiz_answer_radio4))
				 * .setText((list.get(input_cnt)).get("Q_WR_ANSWER3"));
				 */
				//

				Random rgen = new Random(); // Random number generator
				int[] cards = new int[4]; // 보기문제 개수

				for (int i = 0; i < cards.length; i++) { // --- Initialize the
															// array to the ints
															// 0-3
					cards[i] = i;
				}
				for (int i = 0; i < cards.length; i++) { // --- Shuffle by
															// exchanging each
															// element randomly
					int randomPosition = rgen.nextInt(cards.length);
					int temp = cards[i];
					cards[i] = cards[randomPosition];
					cards[randomPosition] = temp;
				}
				
				/*
				((TextView) findViewById(R.id.btn_real_quiz_answer_radio1)).setText((list.get(input_cnt)).get(answer[cards[0]]));
				((TextView) findViewById(R.id.btn_real_quiz_answer_radio2)).setText((list.get(input_cnt)).get(answer[cards[1]]));
				((TextView) findViewById(R.id.btn_real_quiz_answer_radio3)).setText((list.get(input_cnt)).get(answer[cards[2]]));
				((TextView) findViewById(R.id.btn_real_quiz_answer_radio4)).setText((list.get(input_cnt)).get(answer[cards[3]]));
				*/
				String quesId = list.get(input_cnt).get(CommonUtil.SEQ_NO);
				List<HashMap<String,String>> lAnswerList = new ArrayList<HashMap<String, String>>();
				
				HashMap<String, String> map = new HashMap<String, String>();
				for(int i=0;i<answerList.size();i++){
					map = answerList.get(i);
					if (quesId.equals((String)(map.get(CommonUtil.QUESTION_ID))))
							lAnswerList.add(answerList.get(i));
				}
				
				
				((TextView) findViewById(R.id.btn_real_quiz_answer_radio1)).setText(lAnswerList.get(0).get(CommonUtil.Q_ANSWER));
				((TextView) findViewById(R.id.btn_real_quiz_answer_radio2)).setText(lAnswerList.get(1).get(CommonUtil.Q_ANSWER));
				((TextView) findViewById(R.id.btn_real_quiz_answer_radio3)).setText(lAnswerList.get(2).get(CommonUtil.Q_ANSWER));
				((TextView) findViewById(R.id.btn_real_quiz_answer_radio4)).setText(lAnswerList.get(3).get(CommonUtil.Q_ANSWER));

				
				String q_multiple = String.valueOf((list.get(input_cnt)).get(CommonUtil.Q_QUESTION));
				TextView multiple_tv1 = ((TextView) findViewById(R.id.real_quiz_2_question));
				TextView multiple_tv2 = ((TextView) findViewById(R.id.real_quiz_num2));
				multiple_tv1.setText(q_multiple);
				multiple_tv2.setText(cnt_string);

				your_multiple_answer = my_answer[input_cnt];
				setRadioButton(your_multiple_answer);

			} else if (q_type == 3) {
				real_cnt = cur_cnt;
				input_cnt = cur_cnt - 1;
				curView = (LinearLayout) findViewById(R.id.real_quiz_shortanswer);
				
				findViewById(R.id.real_quiz_ox).setVisibility(LinearLayout.GONE);
				findViewById(R.id.real_quiz_multiple).setVisibility(LinearLayout.GONE);
				findViewById(R.id.real_quiz_shortanswer).setVisibility(LinearLayout.VISIBLE);
				findViewById(R.id.real_quiz_answerlist).setVisibility(LinearLayout.GONE);
				
				if (cur_cnt == 1) {
					findViewById(R.id.btn_real_previous_shortanswer).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_real_next_shortanswer).setVisibility(LinearLayout.VISIBLE);
				} else if (cur_cnt == 10) {
					findViewById(R.id.btn_real_next_shortanswer).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_real_previous_shortanswer).setVisibility(LinearLayout.VISIBLE);
				} else {
					findViewById(R.id.btn_real_next_shortanswer).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_real_previous_shortanswer).setVisibility(LinearLayout.VISIBLE);
				}

				cnt_string = CommonUtil.QUESTION_STRING + Integer.toString(cur_cnt);
				
				String q_shortanswer = String.valueOf((list.get(input_cnt))
						.get(CommonUtil.Q_QUESTION));
				TextView short_tv1 = ((TextView) findViewById(R.id.real_quiz_3_question));
				TextView shotr_tv2 = ((TextView) findViewById(R.id.real_quiz_num3));
				short_tv1.setText(q_shortanswer);
				shotr_tv2.setText(cnt_string);

				your_shortanswer = my_answer[input_cnt];
				EditText et = (EditText) findViewById(R.id.youranswer_real_edittext);
				et.setText(your_shortanswer);
				
			}
			
			android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);         
			imm.hideSoftInputFromWindow(curView.getWindowToken(), 0); 
		} else {
			real_go_answerlist();
			Toast.makeText(context, "제한시간이 만료되었습니다.", Toast.LENGTH_SHORT)
					.show();
		}
		
		
	}

	private void setRadioButton(String answer) {
		RadioButton rg1 = (RadioButton) findViewById(R.id.btn_real_quiz_answer_radio1);
		String rg1_answer = rg1.getText().toString();
		RadioButton rg2 = (RadioButton) findViewById(R.id.btn_real_quiz_answer_radio2);
		String rg2_answer = rg2.getText().toString();
		RadioButton rg3 = (RadioButton) findViewById(R.id.btn_real_quiz_answer_radio3);
		String rg3_answer = rg3.getText().toString();
		RadioButton rg4 = (RadioButton) findViewById(R.id.btn_real_quiz_answer_radio4);
		String rg4_answer = rg4.getText().toString();

		if (answer.equals(rg1_answer)) {
			rg1.setChecked(true);
		} else if (answer.equals(rg2_answer)) {
			rg2.setChecked(true);
		} else if (answer.equals(rg3_answer)) {
			rg3.setChecked(true);
		} else if (answer.equals(rg4_answer)) {
			rg4.setChecked(true);
		}

	}

	public void real_view_init() {

		findViewById(R.id.real_quiz_ox).setVisibility(LinearLayout.GONE);
		findViewById(R.id.real_quiz_multiple).setVisibility(LinearLayout.GONE);
		findViewById(R.id.real_quiz_shortanswer).setVisibility(
				LinearLayout.GONE);
		findViewById(R.id.real_quiz_answerlist)
				.setVisibility(LinearLayout.GONE);
	}

	// 타이머에서의 제출
	public void real_exit() {
		if (re_send == false) {
			for (int i = 0; i < 10; i++) {
				if (my_answer[i].equals(correct_answer[i]))
					real_jungdab_yn[i] = 0;
				else
					real_jungdab_yn[i] = 1;
			}
			Intent intent = new Intent(real_quiz.this,
					real_quiz_resultlist.class);

			Bundle real_result = new Bundle();
			real_result.putIntArray(CommonUtil.REAL_JUNGDAB, real_jungdab_yn);
			real_result.putStringArray(CommonUtil.REAL_SEQ, real_seq_no);
			real_result.putString(CommonUtil.USER_ID, m_user_id);
			real_result.putString(CommonUtil.COMP_CD, m_comp_cd);
			
			intent.putExtras(real_result);
			startActivity(intent);
			finish();
		}
	}

	public void real_go_answerlist() {

		if (re_send == false) {
			findViewById(R.id.real_quiz_ox).setVisibility(LinearLayout.GONE);
			findViewById(R.id.real_quiz_multiple).setVisibility(
					LinearLayout.GONE);
			findViewById(R.id.real_quiz_shortanswer).setVisibility(
					LinearLayout.GONE);
			findViewById(R.id.real_quiz_answerlist).setVisibility(
					LinearLayout.VISIBLE);

			if (my_answer[0] == "") {
				((TextView) findViewById(R.id.real_answer_q1))
						.setTextColor(ColorStateList.valueOf(resultColor));
				((TextView) findViewById(R.id.real_answer_a1))
						.setTextColor(ColorStateList.valueOf(resultColor));
			} else {
				((TextView) findViewById(R.id.real_answer_q1)).setText("문제1");
				((TextView) findViewById(R.id.real_answer_q1))
						.setTextColor(ColorStateList.valueOf(black));
				((TextView) findViewById(R.id.real_answer_a1)).setText(CommonUtil.ANSWER_SAVED);
				((TextView) findViewById(R.id.real_answer_a1))
						.setTextColor(ColorStateList.valueOf(black));
			}

			if (my_answer[1] == "") {
				((TextView) findViewById(R.id.real_answer_q2))
						.setTextColor(ColorStateList.valueOf(resultColor));
				((TextView) findViewById(R.id.real_answer_a2))
						.setTextColor(ColorStateList.valueOf(resultColor));
			} else {
				((TextView) findViewById(R.id.real_answer_q2)).setText("문제2");
				((TextView) findViewById(R.id.real_answer_q2))
						.setTextColor(ColorStateList.valueOf(black));
				((TextView) findViewById(R.id.real_answer_a2)).setText(CommonUtil.ANSWER_SAVED);
				((TextView) findViewById(R.id.real_answer_a2))
						.setTextColor(ColorStateList.valueOf(black));
			}

			if (my_answer[2] == "") {
				((TextView) findViewById(R.id.real_answer_q3))
						.setTextColor(ColorStateList.valueOf(resultColor));
				((TextView) findViewById(R.id.real_answer_a3))
						.setTextColor(ColorStateList.valueOf(resultColor));
			} else {
				((TextView) findViewById(R.id.real_answer_q3)).setText("문제3");
				((TextView) findViewById(R.id.real_answer_q3))
						.setTextColor(ColorStateList.valueOf(black));
				((TextView) findViewById(R.id.real_answer_a3)).setText(CommonUtil.ANSWER_SAVED);
				((TextView) findViewById(R.id.real_answer_a3))
						.setTextColor(ColorStateList.valueOf(black));
			}

			if (my_answer[3] == "") {
				((TextView) findViewById(R.id.real_answer_q4))
						.setTextColor(ColorStateList.valueOf(resultColor));
				((TextView) findViewById(R.id.real_answer_a4))
						.setTextColor(ColorStateList.valueOf(resultColor));
			} else {
				((TextView) findViewById(R.id.real_answer_q4)).setText("문제4");
				((TextView) findViewById(R.id.real_answer_q4))
						.setTextColor(ColorStateList.valueOf(black));
				((TextView) findViewById(R.id.real_answer_a4)).setText(CommonUtil.ANSWER_SAVED);
				((TextView) findViewById(R.id.real_answer_a4))
						.setTextColor(ColorStateList.valueOf(black));
			}

			if (my_answer[4] == "") {
				((TextView) findViewById(R.id.real_answer_q5))
						.setTextColor(ColorStateList.valueOf(resultColor));
				((TextView) findViewById(R.id.real_answer_a5))
						.setTextColor(ColorStateList.valueOf(resultColor));
			} else {
				((TextView) findViewById(R.id.real_answer_q5)).setText("문제5");
				((TextView) findViewById(R.id.real_answer_q5))
						.setTextColor(ColorStateList.valueOf(black));
				((TextView) findViewById(R.id.real_answer_a5)).setText(CommonUtil.ANSWER_SAVED);
				((TextView) findViewById(R.id.real_answer_a5))
						.setTextColor(ColorStateList.valueOf(black));
			}

			if (my_answer[5] == "") {
				((TextView) findViewById(R.id.real_answer_q6))
						.setTextColor(ColorStateList.valueOf(resultColor));
				((TextView) findViewById(R.id.real_answer_a6))
						.setTextColor(ColorStateList.valueOf(resultColor));
			} else {
				((TextView) findViewById(R.id.real_answer_q6)).setText("문제6");
				((TextView) findViewById(R.id.real_answer_q6))
						.setTextColor(ColorStateList.valueOf(black));
				((TextView) findViewById(R.id.real_answer_a6)).setText(CommonUtil.ANSWER_SAVED);
				((TextView) findViewById(R.id.real_answer_a6))
						.setTextColor(ColorStateList.valueOf(black));
			}

			if (my_answer[6] == "") {
				((TextView) findViewById(R.id.real_answer_q7))
						.setTextColor(ColorStateList.valueOf(resultColor));
				((TextView) findViewById(R.id.real_answer_a7))
						.setTextColor(ColorStateList.valueOf(resultColor));
			} else {
				((TextView) findViewById(R.id.real_answer_q7)).setText("문제7");
				((TextView) findViewById(R.id.real_answer_q7))
						.setTextColor(ColorStateList.valueOf(black));
				((TextView) findViewById(R.id.real_answer_a7)).setText(CommonUtil.ANSWER_SAVED);
				((TextView) findViewById(R.id.real_answer_a7))
						.setTextColor(ColorStateList.valueOf(black));
			}

			if (my_answer[7] == "") {
				((TextView) findViewById(R.id.real_answer_q8))
						.setTextColor(ColorStateList.valueOf(resultColor));
				((TextView) findViewById(R.id.real_answer_a8))
						.setTextColor(ColorStateList.valueOf(resultColor));
			} else {
				((TextView) findViewById(R.id.real_answer_q8)).setText("문제8");
				((TextView) findViewById(R.id.real_answer_q8))
						.setTextColor(ColorStateList.valueOf(black));
				((TextView) findViewById(R.id.real_answer_a8)).setText(CommonUtil.ANSWER_SAVED);
				((TextView) findViewById(R.id.real_answer_a8))
						.setTextColor(ColorStateList.valueOf(black));
			}

			if (my_answer[8] == "") {
				((TextView) findViewById(R.id.real_answer_q9))
						.setTextColor(ColorStateList.valueOf(resultColor));
				((TextView) findViewById(R.id.real_answer_a9))
						.setTextColor(ColorStateList.valueOf(resultColor));
			} else {
				((TextView) findViewById(R.id.real_answer_q9)).setText("문제9");
				((TextView) findViewById(R.id.real_answer_q9))
						.setTextColor(ColorStateList.valueOf(black));
				((TextView) findViewById(R.id.real_answer_a9)).setText(CommonUtil.ANSWER_SAVED);
				((TextView) findViewById(R.id.real_answer_a9))
						.setTextColor(ColorStateList.valueOf(black));
			}

			if (my_answer[9] == "") {
				((TextView) findViewById(R.id.real_answer_q10))
						.setTextColor(ColorStateList.valueOf(resultColor));
				((TextView) findViewById(R.id.real_answer_a10))
						.setTextColor(ColorStateList.valueOf(resultColor));
			} else {
				((TextView) findViewById(R.id.real_answer_q10)).setText("문제10");
				((TextView) findViewById(R.id.real_answer_q10))
						.setTextColor(ColorStateList.valueOf(black));
				((TextView) findViewById(R.id.real_answer_a10)).setText(CommonUtil.ANSWER_SAVED);
				((TextView) findViewById(R.id.real_answer_a10))
						.setTextColor(ColorStateList.valueOf(black));
			}
		}

	}

	public ArrayList<HashMap<String, String>> getValuesFromXML(String addr) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		int map_chk = 0;

		try {
			URL url = new URL(addr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			if (conn != null) {
				conn.setConnectTimeout(7000);
				conn.setUseCaches(false);
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));
					for (;;) {
						String line = br.readLine();
						String tagName = null;
						String tagValue = null;

						if (line == null)
							break;

						if (line.indexOf("<") == line.lastIndexOf("<"))
							map_chk = 1;

						if (line.indexOf("<") != line.lastIndexOf("<")) {
							map_chk = 0;

							line = line.trim();
							tagName = line.substring(line.indexOf("<") + 1,
									line.indexOf(">"));
							tagValue = line.substring(
									line.indexOf("<![CDATA[") + 9,
									line.indexOf("]]>"));

							map.put(tagName, tagValue);
						}
						
						if ((map_chk == 1) && (map.size() > 1)) {
							list.add(map);
							map = new HashMap<String, String>();
						}
					}
					br.close();
				}
				conn.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	protected void signOutUser() {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().remove(PREF_USERID).remove(PREF_PASSWORD).remove(PREF_REMEMBERME).commit();
		Intent intent = new Intent(real_quiz.this, UserLogin.class);	// create intent to invoke user login activity.
		startActivity(intent);	// start the intent to open user login screen.
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.realquiz, menu);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Bundle args = new Bundle();
		
	    switch (item.getItemId()) {
	    	case R.id.home:
	        	args.putString(CommonUtil.IS_DIALOG_HAS_PARAMS, CommonUtil.BOOLEAN_TRUE);
	        	args.putString(CommonUtil.ACTIVITY_CLASS, CommonUtil.MAIN_ACTIVITY_CLASS);
	        	
	        	showDialog(REAL_QUIZ_ALERT_FINISH, args);
	            return true;
	        case R.id.profile:
	        	args.putString(CommonUtil.IS_DIALOG_HAS_PARAMS, CommonUtil.BOOLEAN_TRUE);
	        	args.putString(CommonUtil.ACTIVITY_CLASS, CommonUtil.PROFILE_ACTIVITY_CLASS);
	        	
	        	showDialog(REAL_QUIZ_ALERT_FINISH, args);
	            return true;
	        case R.id.ranking:
	        	args.putString(CommonUtil.IS_DIALOG_HAS_PARAMS, CommonUtil.BOOLEAN_TRUE);
	        	args.putString(CommonUtil.ACTIVITY_CLASS, CommonUtil.RANKING_ACTIVITY_CLASS);
	        	
	        	showDialog(REAL_QUIZ_ALERT_FINISH, args);
	            return true;
	        case R.id.events:
	        	args.putString(CommonUtil.IS_DIALOG_HAS_PARAMS, CommonUtil.BOOLEAN_TRUE);
	        	args.putString(CommonUtil.ACTIVITY_CLASS, CommonUtil.GENERAL_EVENT_ACTIVITY_CLASS);
	        	
	        	showDialog(REAL_QUIZ_ALERT_FINISH, args);
	            return true;
	        case R.id.answerlist:
	        	showAnswerList();
	            return true;
	        case R.id.signout:
	            signOutUser();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
		//applyMenuChoice(item);
	}

	private void populateMenu(Menu menu) {
		menu.add(Menu.NONE, 1, Menu.NONE, "1번");
		menu.add(Menu.NONE, 2, Menu.NONE, "2번");
		menu.add(Menu.NONE, 3, Menu.NONE, "3번");
		menu.add(Menu.NONE, 4, Menu.NONE, "4번");
		menu.add(Menu.NONE, 5, Menu.NONE, "5번");
		menu.add(Menu.NONE, 6, Menu.NONE, "6번");
		menu.add(Menu.NONE, 7, Menu.NONE, "7번");
		menu.add(Menu.NONE, 8, Menu.NONE, "8번");
		menu.add(Menu.NONE, 9, Menu.NONE, "9번");
		menu.add(Menu.NONE, 10, Menu.NONE, "10번");

	}

	private void applyMenuChoice(MenuItem item) {
		// TODO Auto-generated method stub

		int menu_id = item.getItemId();
		Intent i = null;

		switch (menu_id) {
		case 1:

			startActivityForResult(i, 3);
			break;

		case 2:

			startActivityForResult(i, 3);
			break;

		case 3:

			startActivityForResult(i, 3);
			break;

		case 4:

			startActivityForResult(i, 3);
			break;

		case 5:

			startActivityForResult(i, 3);
			break;

		case 6:

			startActivityForResult(i, 3);
			break;

		case 7:

			startActivityForResult(i, 3);
			break;

		case 8:

			startActivityForResult(i, 3);
			break;

		case 9:

			startActivityForResult(i, 3);
			break;

		}
	}

	/********************************************************************************
		 ********************************************************************************		
		 *******************************************************************************/
	/*
	 * 백버튼 제어
	 * 
	 * @Override public boolean dispatchKeyEvent(KeyEvent e){ if(e.getKeyCode()
	 * == KeyEvent.KEYCODE_BACK){ if(e.getAction() == KeyEvent.ACTION_DOWN){
	 * 
	 * } return true; } return super.dispatchKeyEvent(e); }
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 3) {
			finish();
		}
	}

	// Handler 클래스
	/**
	 * 스레드로부터 주기적으로 호출받아 시간판을 새로고침한다
	 * */
	class TimeHandler extends Handler {

		TextView target;
		long startTime;

		// 제한시간
		long limitTime = 2 * 60 * 1000;

		public TimeHandler(TextView target) {
			this.target = target;
			this.startTime = Calendar.getInstance().getTimeInMillis();

		}

		public TimeHandler(TextView target, long time) {
			this.target = target;
			this.startTime = Calendar.getInstance().getTimeInMillis();

			limitTime = time;
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			// 시간계산알고리즘
			// 남은시간=(시작시간+제한시간)-현재시간
			long spandTime = startTime + limitTime
					- Calendar.getInstance().getTimeInMillis();

			if (spandTime < 0) {
				if (isAlive) {
					isAlive = false;
					isThread = false;
					// real_solving_stop = true;
					tcd = null;
					target.setText(CommonUtil.TIMER_FINISH_TEXT);
					real_go_answerlist();
					return;
				}
			} else {
				target.setText(getTimeFormat(spandTime));
			}
		}

		/**
		 * 남은시간을 형식에 맞게 변환해줌
		 * */
		public String getTimeFormat(long spandTime) {
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
	}

	// 스레드 클레스
	class TimeCountDown extends Thread {

		Handler handler;

		public TimeCountDown(Handler handler) {
			this.handler = handler;
		}

		public void run() {
			while (isAlive) {
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				handler.sendEmptyMessage(0);
			}
			// real_clock.setText("00:00:00");

		}
	}

	private class ValuesToRealQuizAsyncTask extends
			AsyncTask<String, Void, HashMap<String, ?>> {
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		protected HashMap<String, ?> doInBackground(String... params) {
			String compCd = "";
	    	if (params[0] != null)
	    		compCd = params[0];
			
			List<HashMap<String,String>> realQuizList = new ArrayList<HashMap<String, String>>();
			List<HashMap<String,String>> realQuizAnswerList = new ArrayList<HashMap<String, String>>();

			realQuizList = (ArrayList<HashMap<String, String>>) XmlParser.getValuesFromXML(getString(R.string.base_uri) + REAL_QUIZ + "?" + CommonUtil.COMP_CODE + "=" + compCd);
			
			List<HashMap<String,String>> quesList = new ArrayList<HashMap<String, String>>();
			
			
			for(int i=0; i<realQuizList.size(); i++){
				HashMap<String, String> quesMap = new HashMap<String, String>();
				quesMap.put(CommonUtil.SEQ_NO, ((HashMap<String, String>) realQuizList.get(i)).get(CommonUtil.SEQ_NO));
				quesList.add(quesMap);
			}
			
			String quesListStr = MyStudyRoom_Common.ListToXml2(quesList);
			
			realQuizAnswerList = XmlParser.getValuesFromXML(getString(R.string.base_uri) + REAL_QUIZ_ANSWERS + "?" + CommonUtil.QUES_LIST_STR + "=" + quesListStr); 
				//.getValuesFromXML("http://150.28.68.41:8080/SKMS_WAS/realQuiz.do");
				
			Log.d(LOG_TAG, "list size : " + Integer.toString(realQuizList.size()));
			
			HashMap quizMap = new HashMap();
			quizMap.put(CommonUtil.QUES_LIST, realQuizList);
			quizMap.put(CommonUtil.ANSWER_LIST, realQuizAnswerList);
			
			return quizMap;
		}

		@Override
		protected void onPostExecute(HashMap<String, ?> quizMap) {
			List<HashMap<String,String>> map = (List<HashMap<String,String>>)quizMap.get(CommonUtil.QUES_LIST);
			answerList = (List<HashMap<String,String>>) quizMap.get(CommonUtil.ANSWER_LIST);
	    	
	    	
			if (map.size() == 10) {
				list = map;
				dismissProgressDialog();
				
				// 내답지 초기화 & 정답지 세팅
				for (int i = 0; i < 10; i++) {
					my_answer[i] = "";
					real_jungdab_yn[i] = 0;
					sim_answer[i] = "";
					correct_answer[i] = (list.get(i)).get(CommonUtil.Q_ANSWER);
					real_seq_no[i] = (list.get(i)).get(CommonUtil.SEQ_NO);
					String quesType = (list.get(i)).get(CommonUtil.QUES_TYPE);

					if (i==0 && CommonUtil.QUES_TYPE_OX.equalsIgnoreCase(quesType))
						intro_qtype = 1;
					else if (i==0 && CommonUtil.QUES_TYPE_MULTI_CHOICE.equalsIgnoreCase(quesType))
						intro_qtype = 2;
					else if (i==0 && CommonUtil.QUES_TYPE_SHORT_ANSWER.equalsIgnoreCase(quesType))
						intro_qtype = 3;
					
					int sel_tp = 1;

					if (CommonUtil.QUES_TYPE_OX.equalsIgnoreCase(quesType)) {
						sel_tp = 1;
						sel_q_type = 1;
						sel_qType[i] = 1;
			        } else if (CommonUtil.QUES_TYPE_MULTI_CHOICE.equalsIgnoreCase(quesType)) {
			        	sel_tp = 2;
			        	sel_q_type = 2;
			        	sel_qType[i] = 2;
			        } else if (CommonUtil.QUES_TYPE_SHORT_ANSWER.equalsIgnoreCase(quesType)) {
			        	sel_tp = 3;
			        	sel_q_type = 3;
			        	sel_qType[i] = 3;
			        }
					
				    if(sel_tp==3)
				    {
				    	sim_answer[i] = (list.get(i)).get(CommonUtil.SIMILAR_ANSWER);
				    }
				}

				// 1번 문제 보이기
				//intro_qtype = Integer.valueOf((list.get(0)).get("Q_TYPE"));

				handler = new TimeHandler(real_clock, time);
				real_clock.setText(handler.getTimeFormat(time));
				startTimer();

				real_view_select(intro_qtype, real_cnt);
			} else {
				AlertDialog alert = new AlertDialog.Builder(context)
						.setIcon(R.drawable.sk_logo)
						.setTitle(
								R.string.realquiz_download_list_retry_alert_title)
						.setMessage(
								R.string.realquiz_download_list_retry_alert_message)
						.setPositiveButton(R.string.alert_dialog_ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {

										new ValuesToRealQuizAsyncTask()
												.execute();
									}
								})
						.setNegativeButton(R.string.alert_dialog_cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										activity.finish();
									}
								}).create();

				alert.show();
			}

		}
	}
	



}// class닫음

