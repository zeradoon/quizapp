package com.event;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonUtil;
import com.common.XmlParser;
import com.common.async.AbstractAsyncActivity;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.mystudy.MyStudyRoom_Common;
import com.skcc.portal.skmsquiz.R;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to join Survival Event Quiz in SKMS Quiz Application.
 */
public class SurvivalEventQuiz extends AbstractAsyncActivity {
	/* SurvivalEventQuiz class context */
	private Context m_context = this;

	/* logging variable */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = SurvivalEventQuiz.class.getSimpleName();
	
	private static final int SURVIVAL_QUIZ_ALERT_FINISH = 1;
	
	private Activity m_activity;
	
	/* member representing user id */
	private String m_user_id;
	
	/* member representing user dept */
	private String m_user_dept;
	
	/* member representing user company code */
	private String m_comp_cd;
	
	/* member representing event code */
	private String m_event_id;
	
	/* member representing event name */
	private String m_event_name;
	
	/* member representing event name */
	private String m_event_type;
	
	/* member representing quiz code */
	private String m_quiz_id;
	
	/* member representing ques time */
	private long m_ques_comp_time;
	
	/* member representing question count */
	private int m_ques_count;
	
	/* member representing question count */
	private int m_ques_attempt_count = 0;
	
	/* member representing event virtual team name */
	private String m_event_virtual_team_name;
	
	private static final String INSERT_EVENT_VIRTUAL_TEAM_MEMBER = "insertEventVirtualTeamMember.do";
	private static final String GENERAL_EVENT_QUIZ = "generalEventQuiz.do"; 
	private static final String GENERAL_EVENT_QUIZ_ANSWERS = "generalEventQuizAnswers.do";
	
	int SURVIVALEVENT_QUIZ_TOTAL_CNT = 0;
	int[] sel_qType = new int[0];	
	String[] my_answer = new String[0];
	String[] correct_answer = new String[0];
	int[] survivalevent_jungdab_yn = new int[0];
	String[] survivalevent_seq_no = new String[0];
	String[] sim_answer = new String[0];	
	long time = 1 * 60 * 1000;
	TextView survivalevent_clock;
	
	private ListView m_answerListView;
	private List<HashMap<String, String>> m_viewAnswerList;
	private SurvivalEventQuizAdapter m_survivalEventQuizAdapter;
	
	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> answerList = new ArrayList<HashMap<String, String>>();
	
	int cur_q_type = 0;
	int survivalevent_cnt = 1;
	int intro_q_type = 0;
	int sel_q_type = 0;
	int survivalevent_qtype = 0;
	int intro_qtype = 0;
	int input_cnt = 0;
	String cnt_string = "";
	String your_shortanswer = "";
	String str_cnt2 = "";
	String str = "";
	String Temp_answer_ox = "";
	String your_multiple_answer = "";
	
	boolean isAlive = false;
	boolean isThread = false;
	boolean re_send = false;
	
	TimeCountDown tcd;
	TimeHandler handler;
	
	
	/**
	 * This method is called once on creation of the SurvivalEventQuiz Activity.
	 * It is used for initialization of required variables.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.survivalevent_quiz_main);
		
		/* set values input by user to member variables */

		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_user_dept = getIntent().getStringExtra(CommonUtil.USER_DEPT);
		m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		m_event_id = getIntent().getStringExtra(CommonUtil.EVENT_ID);
		m_event_name = getIntent().getStringExtra(CommonUtil.EVENT_NAME);
		m_event_type = getIntent().getStringExtra(CommonUtil.EVENT_TYPE_NAME);
		m_quiz_id = getIntent().getStringExtra(CommonUtil.QUIZ_ID);
		m_ques_count = Integer.parseInt(getIntent().getStringExtra(CommonUtil.QUES_COUNT));
		
		if (m_event_type.equalsIgnoreCase(CommonUtil.EVENT_TYPE_VIRTUAL_TEAM)) {
			String virtualTeamId = getIntent().getStringExtra(CommonUtil.VIRTUAL_TEAM_ID);
			m_event_virtual_team_name = getIntent().getStringExtra(CommonUtil.VIRTUAL_TEAM_NAME);
			insertEventVirtualTeamMember(m_event_id, m_event_virtual_team_name, m_user_id, m_user_dept);
		}
		
		java.sql.Time compTime = java.sql.Time.valueOf(getIntent().getStringExtra(CommonUtil.QUIZ_COMPLETION_TIME));
		m_ques_comp_time = (compTime.getHours()*60*60*1000) + (compTime.getMinutes()*60*1000) + (compTime.getSeconds()*1000);
		time = m_ques_comp_time;
		
		SURVIVALEVENT_QUIZ_TOTAL_CNT = m_ques_count;
		sel_qType = new int[m_ques_count];	
		my_answer = new String[m_ques_count];
		correct_answer = new String[m_ques_count];
		survivalevent_jungdab_yn = new int[m_ques_count];
		survivalevent_seq_no = new String[m_ques_count];
		sim_answer = new String[m_ques_count];	
		survivalevent_clock = (TextView) findViewById(R.id.survivalevent_clock);
		
		ImageView m_btn_home = (ImageView)findViewById(R.id.btn_home);
		m_btn_home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(SURVIVAL_QUIZ_ALERT_FINISH);
				//finish();
			}
		});

		m_context = this;
		m_activity = this;

		m_answerListView = (ListView) findViewById(R.id.survivalevent_answerlistview);

		m_survivalEventQuizAdapter = new SurvivalEventQuizAdapter(m_viewAnswerList, m_ques_count, my_answer,  m_context);
		m_answerListView.setAdapter(m_survivalEventQuizAdapter);

		new ValuesToGeneralEventQuizAsyncTask().execute(m_event_id);
	}
	
	/**
	 * This method is used to add user to virtual team in database if user join the virtual team event.
	 * 
	 * @param virtualTeamId
	 * @param virtualTeamName
	 * @param userId
	 * @param userDept
	 */
	private void insertEventVirtualTeamMember(String eventId, String virtualTeamName, String userId, String userDept) {
		String url = getString(R.string.base_uri) + INSERT_EVENT_VIRTUAL_TEAM_MEMBER;
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.EVENT_ID, eventId);
		parameters.put(CommonUtil.VIRTUAL_TEAM_NAME, virtualTeamName);
		parameters.put(CommonUtil.USER_ID, userId);
		parameters.put(CommonUtil.USER_DEPT, userDept);
		
		Context context = m_context;
		boolean progressVisible = false;
		int id = 222;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {
			@Override
			public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list, int id) {
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url, parameters, id, context, callable, progressVisible);
		task.execute();
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
			showDialog(SURVIVAL_QUIZ_ALERT_FINISH);

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
			case SURVIVAL_QUIZ_ALERT_FINISH:
				dialog = showSurvivalQuizAlertFinish();
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
	private Dialog showSurvivalQuizAlertFinish() {
		Dialog dialog;
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(R.string.survivalquiz_alert_finish_title);
		builder.setMessage(R.string.survivalquiz_alert_finish_message);
		builder.setPositiveButton(
				R.string.survivalquiz_alert_finish_positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						registerEventResult();
						dialog.dismiss(); // 닫기
						finish();
					}
				});
		builder.setNegativeButton(
				R.string.survivalquiz_alert_finish_negative_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 닫기

					}
				});
		dialog = builder.create();
		return dialog;
	}
	
	/**
	 * This method activates a particular view of the quiz based on user selection.
	 * 
	 * @param v
	 */
	public void onclick(View v) {
		String quesType = "";
		
		switch (v.getId()) {
			case R.id.btn_survivalevent_next_multiple:
				if (saveMultipleAnswer()) {
					m_ques_attempt_count++;
					nextQuestion();
				}
				
				break;
			case R.id.btn_survivalevent_next_ox:
				if (saveOXAnswer()) {
					m_ques_attempt_count++;
					nextQuestion();
				}
				
				break;
			case R.id.btn_survivalevent_next_shortanswer:
				if (saveShortAnswer()) {
					m_ques_attempt_count++;
					nextQuestion();
				}
				
				break;
			case R.id.btn_survivalevent_quiz_answer_o:
				Temp_answer_ox = CommonUtil.QUES_TYPE_OX_ANSWER_O;
				setOXButtonImage();
				
				break;
			case R.id.btn_survivalevent_quiz_answer_x:
				Temp_answer_ox = CommonUtil.QUES_TYPE_OX_ANSWER_X;
				setOXButtonImage();
				
				break;
			case R.id.btn_survivalevent_quiz_answer_radio1:
				RadioButton rg1 = (RadioButton) findViewById(R.id.btn_survivalevent_quiz_answer_radio1);
				your_multiple_answer = rg1.getText().toString();
				
				break;
			case R.id.btn_survivalevent_quiz_answer_radio2:
				RadioButton rg2 = (RadioButton) findViewById(R.id.btn_survivalevent_quiz_answer_radio2);
				your_multiple_answer = rg2.getText().toString();
	
				break;
			case R.id.btn_survivalevent_quiz_answer_radio3:
				RadioButton rg3 = (RadioButton) findViewById(R.id.btn_survivalevent_quiz_answer_radio3);
				your_multiple_answer = rg3.getText().toString();
				
				break;
			case R.id.btn_survivalevent_quiz_answer_radio4:
				RadioButton rg4 = (RadioButton) findViewById(R.id.btn_survivalevent_quiz_answer_radio4);
				your_multiple_answer = rg4.getText().toString();

				break;
			case R.id.btn_survivalevent_summitsheet_ox:
				if (saveOXAnswer()) {
					m_ques_attempt_count++;
					registerEventResult();
				}
				
				break;
			case R.id.btn_survivalevent_summitsheet_multiple:
				if (saveMultipleAnswer()) {
					m_ques_attempt_count++;
					registerEventResult();
				}
				
				break;
			case R.id.btn_survivalevent_summitsheet_shortanswer:
				if (saveShortAnswer()) {
					m_ques_attempt_count++;
					registerEventResult();
				}
				
				break;
		}
	}
	
	/**
	 * This method is used to navigate to next question when next button is clicked.
	 */
	private void nextQuestion() {
		String quesType = "";
		survivalevent_cnt++;
		input_cnt = survivalevent_cnt - 1;
		
		quesType = (list.get(input_cnt)).get(CommonUtil.QUES_TYPE);
		
		if (CommonUtil.QUES_TYPE_OX.equalsIgnoreCase(quesType)) {
			sel_q_type = 1;
        } else if (CommonUtil.QUES_TYPE_MULTI_CHOICE.equalsIgnoreCase(quesType)) {
        	sel_q_type = 2;
        } else if (CommonUtil.QUES_TYPE_SHORT_ANSWER.equalsIgnoreCase(quesType)) {
        	sel_q_type = 3;
        }
		
		str_cnt2 = Integer.toString(survivalevent_cnt);
		str = Integer.toString(sel_q_type);
		
		stopTimer();
		isAlive = false;
		
		if (confirmAnswer(input_cnt-1, cur_q_type-1)) {
			showSurvivalEventCorrectAnswerAlertDialog();
			handler.setTime(m_ques_comp_time);
			survivalevent_clock.setText(handler.getTimeFormat(m_ques_comp_time));
			startTimer();
			survivalevent_view_select(sel_q_type, survivalevent_cnt);
		} else {
			showSurvivalEventWrongAnswerAlertDialog();
		}
	}
	
	/**
	 * This method save the user choice of multiple type answer in member variable.
	 * 
	 * @return boolean
	 */
	private boolean saveMultipleAnswer() {
		input_cnt = survivalevent_cnt - 1;
		my_answer[input_cnt] = your_multiple_answer;
		
		m_survivalEventQuizAdapter.setAnswerList(my_answer);
		m_survivalEventQuizAdapter.notifyDataSetChanged();
		
		if (your_multiple_answer.equals("")) {
			CommonUtil.showAlert(m_context, CommonUtil.ALERT_TITLE_NOTICE, CommonUtil.ALERT_MESSAGE_OBJECTIVE, CommonUtil.ALERT_POSITIVE_BUTTON);
			return false;
		} else {
			//Toast.makeText(m_context, R.string.realquiz_store_answer, Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}
	
	/**
	 * This method save the user choice of OX type answer in member variable.
	 * 
	 * @return boolean
	 */
	private boolean saveOXAnswer() {
		input_cnt = survivalevent_cnt - 1;
		my_answer[input_cnt] = Temp_answer_ox;

		m_survivalEventQuizAdapter.setAnswerList(my_answer);
		m_survivalEventQuizAdapter.notifyDataSetChanged();

		if (Temp_answer_ox.equals("")) {
			CommonUtil.showAlert(m_context, CommonUtil.ALERT_TITLE_NOTICE, CommonUtil.ALERT_MESSAGE_OXQUIZ, CommonUtil.ALERT_POSITIVE_BUTTON);
			return false;
		} else {
			//Toast.makeText(m_context, R.string.realquiz_store_answer, Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}
	
	/**
	 * This method save the user choice of short answer type answer in member variable.
	 * 
	 * @return boolean
	 */
	private boolean saveShortAnswer() {
		input_cnt = survivalevent_cnt - 1;
		str_cnt2 = Integer.toString(survivalevent_cnt);
		EditText et = (EditText) findViewById(R.id.youranswer_survivalevent_edittext);
		your_shortanswer = et.getText().toString();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
		my_answer[input_cnt] = your_shortanswer;

		m_survivalEventQuizAdapter.setAnswerList(my_answer);
		m_survivalEventQuizAdapter.notifyDataSetChanged();

		if ("".equals(et.getText().toString())) {
			CommonUtil.showAlert(SurvivalEventQuiz.this,CommonUtil.ALERT_TITLE_NOTICE,CommonUtil.ALERT_MESSAGE_SUBJECTIVE,CommonUtil.ALERT_POSITIVE_BUTTON);
			return false;
		} else {
			//Toast.makeText(m_context, R.string.realquiz_store_answer, Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}
	
	/**
	 * This method checks if the user choice is the correct answer then proceed to next question else finish the quiz.
	 * 
	 * @param input_cnt
	 * @param cur_q_type
	 * @return boolean
	 */
	private boolean confirmAnswer(int input_cnt, int cur_q_type) {
		if(cur_q_type!=3){    // 1,2번 유형인경우
			if (my_answer[input_cnt].equals(correct_answer[input_cnt])) {
				return true;
			}
			else {
				return false;
			}
		} else {           //3번 유형인경우
			String str_my_answer      = my_answer[input_cnt].toLowerCase().trim().replaceAll(" ",""); 
            String str_correct_answer = correct_answer[input_cnt].toLowerCase().trim().replaceAll(" ","");
            
			if ("".equals(sim_answer[input_cnt].trim())) {
				if (str_my_answer.equals(str_correct_answer)) {
					return true;
				}
				else {
					return false;
				}
			} else {
				String delimiter = ";";
				
				if (sim_answer[input_cnt].indexOf(";") != -1) {
					delimiter = ";";
				} else if (sim_answer[input_cnt].indexOf("|") != -1) {
					delimiter = "|";
				} else if (sim_answer[input_cnt].indexOf(",") != -1) {
					delimiter = ",";
				}

				String[] sim_Array = sim_answer[input_cnt].split(delimiter);

				for (int k = 0; k < sim_Array.length; k++) {
					if (str_my_answer.equalsIgnoreCase(sim_Array[k].toLowerCase().trim().replaceAll(" ",""))) {
						return true;
					} else {
						return false;
					}
				}
				if(str_my_answer.equalsIgnoreCase(str_correct_answer)) {
					return true;
				}
			}
		}
		return true;
	}
	
	/**
	 * This method register the user answers to member variables and proceed to result activity.
	 */
	private void registerEventResult() {
		String quesType = "";
		
		if (CommonUtil.showNetworkAlert(SurvivalEventQuiz.this)) {
			re_send = true;
			tcd = null;
			stopTimer();
			
			//for (int i = 0; i < m_ques_count; i++) {
			for (int i = 0; i < m_ques_attempt_count; i++) {
				quesType = (list.get(input_cnt)).get(CommonUtil.QUES_TYPE);
				int sel_type = 1;
				
				if (CommonUtil.QUES_TYPE_OX.equalsIgnoreCase(quesType)) {
					sel_type = 1;
		        } else if (CommonUtil.QUES_TYPE_MULTI_CHOICE.equalsIgnoreCase(quesType)) {
		        	sel_type = 2;
		        } else if (CommonUtil.QUES_TYPE_SHORT_ANSWER.equalsIgnoreCase(quesType)) {
		        	sel_type = 3;
		        }
				
				if(sel_type!=3){    // 1,2번 유형인경우
					if (my_answer[i].equals(correct_answer[i]))
						survivalevent_jungdab_yn[i] = 0;
				    else
				    	survivalevent_jungdab_yn[i] = 1;
				} else {           //3번 유형인경우
					String str_my_answer      = my_answer[i].toLowerCase().trim().replaceAll(" ",""); 
                    String str_correct_answer = correct_answer[i].toLowerCase().trim().replaceAll(" ","");
                    
					if ("".equals(sim_answer[i].trim())) {
						if (str_my_answer.equals(str_correct_answer))
							survivalevent_jungdab_yn[i] = 0;
						else
							survivalevent_jungdab_yn[i] = 1;
					} else {
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
							if (str_my_answer.equalsIgnoreCase(sim_Array[k].toLowerCase().trim().replaceAll(" ",""))) {
								survivalevent_jungdab_yn[i] = 0;
								break;
							} else
								survivalevent_jungdab_yn[i] = 1;
						}// 중첩for문 끝
						if(str_my_answer.equalsIgnoreCase(str_correct_answer))
							survivalevent_jungdab_yn[i] = 0;
					}
				}
			}// for문 끝

			Intent intent = new Intent(SurvivalEventQuiz.this, SurvivalEventQuizResult.class);
			Bundle survivalevent_result = new Bundle();
			
			survivalevent_result.putIntArray(CommonUtil.SURVIVAL_EVENT_JUNGDAB, survivalevent_jungdab_yn);
			survivalevent_result.putStringArray(CommonUtil.SURVIVAL_EVENT_SEQ, survivalevent_seq_no);
			survivalevent_result.putString(CommonUtil.USER_ID, m_user_id);
			survivalevent_result.putString(CommonUtil.USER_DEPT, m_user_dept);
			survivalevent_result.putString(CommonUtil.COMP_CD, m_comp_cd);
			survivalevent_result.putString(CommonUtil.EVENT_ID, m_event_id);
			survivalevent_result.putString(CommonUtil.EVENT_NAME, m_event_name);
			survivalevent_result.putString(CommonUtil.EVENT_TYPE_NAME, m_event_type);
			survivalevent_result.putString(CommonUtil.QUIZ_ID, m_quiz_id);
			survivalevent_result.putString(CommonUtil.QUES_COUNT, String.valueOf(m_ques_count));
			survivalevent_result.putString(CommonUtil.QUES_ATTEMPT_COUNT, String.valueOf(m_ques_attempt_count));
			survivalevent_result.putString(CommonUtil.VIRTUAL_TEAM_NAME, m_event_virtual_team_name);
			
			intent.putExtras(survivalevent_result);
			startActivity(intent);
			finish();
		}
	}
	
	/**
	 * This private class is used to download the question and answers of a quiz for a particular event 
	 * and initialize initial data as an asynchronous task.
	 * 
	 */
	private class ValuesToGeneralEventQuizAsyncTask extends	AsyncTask<String, Void, HashMap<String, ?>> {
		
		/**
		 * This method show a progress bar while the task in progress of downloading data.
		 */
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}
		
		/**
		 * This method does the required job in background.
		 */
		protected HashMap<String, ?> doInBackground(String... params) {
			String eventId = "";
			if (params[0] != null)
				eventId = params[0];
			
			List<HashMap<String,String>> generaleventQuizList = new ArrayList<HashMap<String, String>>();
			List<HashMap<String,String>> generaleventQuizAnswerList = new ArrayList<HashMap<String, String>>();
		
			generaleventQuizList = (ArrayList<HashMap<String, String>>) XmlParser.getValuesFromXML(getString(R.string.base_uri) + GENERAL_EVENT_QUIZ + "?" + CommonUtil.EVENT_ID + "=" + eventId);
			List<HashMap<String,String>> quesList = new ArrayList<HashMap<String, String>>();
			
			for(int i=0; i<generaleventQuizList.size(); i++){
				HashMap<String, String> quesMap = new HashMap<String, String>();
				quesMap.put(CommonUtil.SEQ_NO, ((HashMap<String, String>) generaleventQuizList.get(i)).get(CommonUtil.SEQ_NO));
				quesList.add(quesMap);
			}
			
			String quesListStr = MyStudyRoom_Common.ListToXml2(quesList);
			generaleventQuizAnswerList = XmlParser.getValuesFromXML(getString(R.string.base_uri) + GENERAL_EVENT_QUIZ_ANSWERS + "?" + CommonUtil.QUES_LIST_STR + "=" + quesListStr);
			
			HashMap quizMap = new HashMap();
			quizMap.put(CommonUtil.QUES_LIST, generaleventQuizList);
			quizMap.put(CommonUtil.ANSWER_LIST, generaleventQuizAnswerList);
			
			return quizMap;
		}
		
		/**
		 * This method perform postexecute actions after downloading quiz data for event.
		 */
		@Override
		protected void onPostExecute(HashMap<String, ?> quizMap) {
			List<HashMap<String,String>> map = (List<HashMap<String,String>>)quizMap.get(CommonUtil.QUES_LIST);
			answerList = (List<HashMap<String,String>>) quizMap.get(CommonUtil.ANSWER_LIST);
			
			if (map.size() == m_ques_count) {
				list = map;
				m_viewAnswerList = list;
				dismissProgressDialog();
				
				// 내답지 초기화 & 정답지 세팅
				for (int i = 0; i < m_ques_count; i++) {
					my_answer[i] = "";
					survivalevent_jungdab_yn[i] = 0;
					sim_answer[i] = "";
					correct_answer[i] = (list.get(i)).get(CommonUtil.Q_ANSWER);
					survivalevent_seq_no[i] = (list.get(i)).get(CommonUtil.SEQ_NO);
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
		
				handler = new TimeHandler(survivalevent_clock, time);
				survivalevent_clock.setText(handler.getTimeFormat(time));
				startTimer();
				
				refreshList();
				survivalevent_view_select(intro_qtype, survivalevent_cnt);
			} else {
				AlertDialog alert = new AlertDialog.Builder(m_context)
						.setIcon(R.drawable.sk_logo)
						.setTitle(R.string.realquiz_download_list_retry_alert_title)
						.setMessage(R.string.realquiz_download_list_retry_alert_message)
						.setPositiveButton(R.string.alert_dialog_ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										new ValuesToGeneralEventQuizAsyncTask().execute();
									}
								})
						.setNegativeButton(R.string.alert_dialog_cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int whichButton) {
										m_activity.finish();
									}
								}).create();
		
				alert.show();
			}
		}
	}
	
	/**
	 * This method selects a particular question view based on question type.
	 * @param v
	 */
	public void monclick(View v) {
		survivalevent_view_select(sel_qType[v.getId()-1], v.getId());
	}
	
	/**
	 * This method refresh the question list.
	 */
	private void refreshList() {
		if (m_viewAnswerList == null) {
			m_viewAnswerList = new ArrayList<HashMap<String, String>>();
			m_viewAnswerList = list;
		}

		m_survivalEventQuizAdapter.setSurvivalEventQuizAnswerList(m_viewAnswerList);
		m_survivalEventQuizAdapter.notifyDataSetChanged();
	}
	
	/**
	 * This method initialize the initial layout for survival event quiz.
	 */
	public void survivalevent_view_init() {
		findViewById(R.id.survivalevent_quiz_ox).setVisibility(LinearLayout.GONE);
		findViewById(R.id.survivalevent_quiz_multiple).setVisibility(LinearLayout.GONE);
		findViewById(R.id.survivalevent_quiz_shortanswer).setVisibility(LinearLayout.GONE);
		findViewById(R.id.survivalevent_quiz_answerlist).setVisibility(LinearLayout.GONE);
	}
	
	/**
	 * This method set the layout for quiz question when the quiz is traversed.
	 * 
	 * @param q_type
	 * @param cur_cnt
	 */
	public void survivalevent_view_select(int q_type, int cur_cnt) {
		survivalevent_view_init();

		/**
		 * 현재 타이머가 살아 있을때만 문제 이동 가능 그렇지 않으면 답안지로 이동
		 */
		if (isAlive) {
			LinearLayout curView = null;
			
			if (q_type == 1) {
				cur_q_type = q_type;
				survivalevent_cnt = cur_cnt;
				input_cnt = cur_cnt - 1;
				curView = (LinearLayout) findViewById(R.id.survivalevent_quiz_ox);
				
				findViewById(R.id.survivalevent_quiz_ox).setVisibility(LinearLayout.VISIBLE);
				findViewById(R.id.survivalevent_quiz_multiple).setVisibility(LinearLayout.GONE);
				findViewById(R.id.survivalevent_quiz_shortanswer).setVisibility(LinearLayout.GONE);
				findViewById(R.id.survivalevent_quiz_answerlist).setVisibility(LinearLayout.GONE);

				cnt_string = CommonUtil.QUESTION_STRING + Integer.toString(cur_cnt);
				
				if (cur_cnt == 1 && cur_cnt < m_ques_count) {
					findViewById(R.id.btn_survivalevent_next_ox).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_survivalevent_summitsheet_ox).setVisibility(LinearLayout.GONE);
				} else if (cur_cnt == 1 && cur_cnt == m_ques_count) {
					findViewById(R.id.btn_survivalevent_next_ox).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_survivalevent_summitsheet_ox).setVisibility(LinearLayout.VISIBLE);
				} else if (cur_cnt == m_ques_count) {
					findViewById(R.id.btn_survivalevent_next_ox).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_survivalevent_summitsheet_ox).setVisibility(LinearLayout.VISIBLE);
				} else {
					findViewById(R.id.btn_survivalevent_next_ox).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_survivalevent_summitsheet_ox).setVisibility(LinearLayout.GONE);
				}
				
				String q_ox = String.valueOf((list.get(input_cnt)).get(CommonUtil.Q_QUESTION));
				TextView ox_tv1 = ((TextView) findViewById(R.id.survivalevent_quiz_1_question));
				TextView ox_tv2 = ((TextView) findViewById(R.id.survivalevent_quiz_num1));

				ox_tv1.setText(q_ox);
				ox_tv2.setText(cnt_string);

				Temp_answer_ox = my_answer[input_cnt];
				setOXButtonImage();
			} else if (q_type == 2) {
				cur_q_type = q_type;
				RadioGroup rGroup = (RadioGroup) findViewById(R.id.btn_survivalevent_quiz_answer_radio);
				rGroup.clearCheck();

				findViewById(R.id.btn_survivalevent_quiz_answer_radio).setVisibility(LinearLayout.GONE);
				survivalevent_cnt = cur_cnt;
				input_cnt = cur_cnt - 1;
				curView = (LinearLayout) findViewById(R.id.survivalevent_quiz_multiple);
				
				findViewById(R.id.survivalevent_quiz_ox).setVisibility(LinearLayout.GONE);
				findViewById(R.id.survivalevent_quiz_multiple).setVisibility(LinearLayout.VISIBLE);
				findViewById(R.id.survivalevent_quiz_shortanswer).setVisibility(LinearLayout.GONE);
				findViewById(R.id.survivalevent_quiz_answerlist).setVisibility(LinearLayout.GONE);
				findViewById(R.id.btn_survivalevent_quiz_answer_radio).setVisibility(LinearLayout.VISIBLE);
				
				if (cur_cnt == 1 && cur_cnt < m_ques_count) {
					findViewById(R.id.btn_survivalevent_next_multiple).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_survivalevent_summitsheet_multiple).setVisibility(LinearLayout.GONE);
				} else if (cur_cnt == 1 && cur_cnt == m_ques_count) {
					findViewById(R.id.btn_survivalevent_next_multiple).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_survivalevent_summitsheet_multiple).setVisibility(LinearLayout.VISIBLE);
				} else if (cur_cnt == m_ques_count) {
					findViewById(R.id.btn_survivalevent_next_multiple).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_survivalevent_summitsheet_multiple).setVisibility(LinearLayout.VISIBLE);
				} else {
					findViewById(R.id.btn_survivalevent_next_multiple).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_survivalevent_summitsheet_multiple).setVisibility(LinearLayout.GONE);
				}
				
				cnt_string = CommonUtil.QUESTION_STRING + Integer.toString(cur_cnt);

				Random rgen = new Random(); // Random number generator
				int[] cards = new int[4]; // 보기문제 개수

				for (int i = 0; i < cards.length; i++) {
					cards[i] = i;
				}
				
				for (int i = 0; i < cards.length; i++) {
					int randomPosition = rgen.nextInt(cards.length);
					int temp = cards[i];
					cards[i] = cards[randomPosition];
					cards[randomPosition] = temp;
				}
				
				String quesId = list.get(input_cnt).get(CommonUtil.SEQ_NO);
				List<HashMap<String,String>> lAnswerList = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> map = new HashMap<String, String>();
				
				for(int i=0;i<answerList.size();i++){
					map = answerList.get(i);
					if (quesId.equals((String)(map.get(CommonUtil.QUESTION_ID))))
							lAnswerList.add(answerList.get(i));
				}
				
				((TextView) findViewById(R.id.btn_survivalevent_quiz_answer_radio1)).setText(lAnswerList.get(0).get(CommonUtil.Q_ANSWER));
				((TextView) findViewById(R.id.btn_survivalevent_quiz_answer_radio2)).setText(lAnswerList.get(1).get(CommonUtil.Q_ANSWER));
				((TextView) findViewById(R.id.btn_survivalevent_quiz_answer_radio3)).setText(lAnswerList.get(2).get(CommonUtil.Q_ANSWER));
				((TextView) findViewById(R.id.btn_survivalevent_quiz_answer_radio4)).setText(lAnswerList.get(3).get(CommonUtil.Q_ANSWER));

				String q_multiple = String.valueOf((list.get(input_cnt)).get(CommonUtil.Q_QUESTION));
				TextView multiple_tv1 = ((TextView) findViewById(R.id.survivalevent_quiz_2_question));
				TextView multiple_tv2 = ((TextView) findViewById(R.id.survivalevent_quiz_num2));
				multiple_tv1.setText(q_multiple);
				multiple_tv2.setText(cnt_string);

				your_multiple_answer = my_answer[input_cnt];
				setRadioButton(your_multiple_answer);
			} else if (q_type == 3) {
				cur_q_type = q_type;
				survivalevent_cnt = cur_cnt;
				input_cnt = cur_cnt - 1;
				curView = (LinearLayout) findViewById(R.id.survivalevent_quiz_shortanswer);
				
				findViewById(R.id.survivalevent_quiz_ox).setVisibility(LinearLayout.GONE);
				findViewById(R.id.survivalevent_quiz_multiple).setVisibility(LinearLayout.GONE);
				findViewById(R.id.survivalevent_quiz_shortanswer).setVisibility(LinearLayout.VISIBLE);
				findViewById(R.id.survivalevent_quiz_answerlist).setVisibility(LinearLayout.GONE);

				if (cur_cnt == 1 && cur_cnt < m_ques_count) {
					findViewById(R.id.btn_survivalevent_next_shortanswer).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_survivalevent_summitsheet_shortanswer).setVisibility(LinearLayout.GONE);
				} else if (cur_cnt == 1 && cur_cnt == m_ques_count) {
					findViewById(R.id.btn_survivalevent_next_shortanswer).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_survivalevent_summitsheet_shortanswer).setVisibility(LinearLayout.VISIBLE);
				} else if (cur_cnt == m_ques_count) {
					findViewById(R.id.btn_survivalevent_next_shortanswer).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_survivalevent_summitsheet_shortanswer).setVisibility(LinearLayout.VISIBLE);
				} else {
					findViewById(R.id.btn_survivalevent_next_shortanswer).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_survivalevent_summitsheet_shortanswer).setVisibility(LinearLayout.GONE);
				}
				
				cnt_string = CommonUtil.QUESTION_STRING + Integer.toString(cur_cnt);
				
				String q_shortanswer = String.valueOf((list.get(input_cnt)).get(CommonUtil.Q_QUESTION));
				TextView short_tv1 = ((TextView) findViewById(R.id.survivalevent_quiz_3_question));
				TextView shotr_tv2 = ((TextView) findViewById(R.id.survivalevent_quiz_num3));
				short_tv1.setText(q_shortanswer);
				shotr_tv2.setText(cnt_string);

				your_shortanswer = my_answer[input_cnt];
				EditText et = (EditText) findViewById(R.id.youranswer_survivalevent_edittext);
				et.setText(your_shortanswer);
			}
			
			android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE);         
			imm.hideSoftInputFromWindow(curView.getWindowToken(), 0); 
		} else {
			Toast.makeText(m_context, "제한시간이 만료되었습니다.", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * This method set the image for OX Type question in view.
	 */
	private void setOXButtonImage() {
		ImageButton oButton = (ImageButton) findViewById(R.id.btn_survivalevent_quiz_answer_o);
		ImageButton xButton = (ImageButton) findViewById(R.id.btn_survivalevent_quiz_answer_x);
		
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
	 * This method check the radio button for user selection.
	 *  
	 * @param answer
	 */
	private void setRadioButton(String answer) {
		RadioButton rg1 = (RadioButton) findViewById(R.id.btn_survivalevent_quiz_answer_radio1);
		String rg1_answer = rg1.getText().toString();
		RadioButton rg2 = (RadioButton) findViewById(R.id.btn_survivalevent_quiz_answer_radio2);
		String rg2_answer = rg2.getText().toString();
		RadioButton rg3 = (RadioButton) findViewById(R.id.btn_survivalevent_quiz_answer_radio3);
		String rg3_answer = rg3.getText().toString();
		RadioButton rg4 = (RadioButton) findViewById(R.id.btn_survivalevent_quiz_answer_radio4);
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
	
	/**
	 * This method stop the timer when the activity is destroyed.
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("", "onDestroy()");
		super.onDestroy();

		stopTimer();
	}
	
	/**
	 * This method start the timer when the quiz question is displayed.
	 */
	private void startTimer() {
		if (!isThread) {
			this.isAlive = true;
			isThread = true;
			tcd = new TimeCountDown(handler);
			tcd.start();
		}
	}
	
	/**
	 * This method stop the timer when the quiz next button is clicked.
	 */
	private void stopTimer() {
		this.isAlive = false;
		this.isThread = false;
		// tcd.interrupt();
		tcd = null;
	}
	
	/**
	 * This class is used to handle timer for Survival Event Quiz in SKMS Quiz Application.
	 */
	private class TimeHandler extends Handler {
		TextView target;
		long startTime;
		long limitTime = 2 * 60 * 1000;
		
		/**
		 * Constructor method
		 * 
		 * @param target
		 */
		public TimeHandler(TextView target) {
			this.target = target;
			this.startTime = Calendar.getInstance().getTimeInMillis();

		}
		
		/**
		 * Constructor method.
		 * 
		 * @param target
		 * @param time
		 */
		public TimeHandler(TextView target, long time) {
			this.target = target;
			this.startTime = Calendar.getInstance().getTimeInMillis();

			limitTime = time;
		}
		
		/**
		 * This method set the timer for attempting one question in survival quiz.
		 *  
		 * @param time
		 */
		public void setTime(long time) {
			this.startTime = Calendar.getInstance().getTimeInMillis();
			limitTime = time; 
		}
		
		/**
		 * This method handles the timer message to pause, restart or stop.
		 * 
		 * @param msg
		 */
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			long spandTime = startTime + limitTime	- Calendar.getInstance().getTimeInMillis();

			if (spandTime < 0) {
				if (isAlive) {
					isAlive = false;
					isThread = false;
					tcd = null;
					target.setText(CommonUtil.TIMER_FINISH_TEXT);
					showSurvivalEventTimeCompleteAlertDialog();
					
					return;
				}
			} else {
				target.setText(getTimeFormat(spandTime));
			}
		}

		/**
		 * This method get the time format for timer object.
		 * 
		 * @param spandTime
		 * @return String
		 */
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
	
	/**
	 * This method is used to alert popup when the quiz completion time is over.
	 */
	private void showSurvivalEventTimeCompleteAlertDialog() {
		Dialog dialog;
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(R.string.survival_event_time_complete_alert_title);
		builder.setMessage(R.string.survival_event_time_complete_alert_message);
		builder.setCancelable(false);		
		builder.setPositiveButton(
				R.string.quizmain_alert_finsh_positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						registerEventResult();
						dialog.dismiss(); // 닫기
					}
				});

		dialog = builder.create();
		dialog.show();
	}
	
	/**
	 * This private class handles the timer countdown as a thread to manage timer for quiz.
	 *
	 */
	private class TimeCountDown extends Thread {
		Handler handler;
		
		/**
		 * Constructor Method.
		 * 
		 * @param handler
		 */
		public TimeCountDown(Handler handler) {
			this.handler = handler;
		}
		
		/**
		 * This method is thread run method to start, pause, restart or stop the timer.
		 */
		public void run() {
			while (isAlive) {
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				handler.sendEmptyMessage(0);
			}
			handler.sendEmptyMessage(0);
		}
	}
	
	/**
	 * This method is used to alert popup when the attempted question is correct.
	 */
	private void showSurvivalEventCorrectAnswerAlertDialog() {
		Dialog dialog;
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(R.string.survival_event_correct_answer_alert_title);
		builder.setMessage(R.string.survival_event_correct_answer_alert_message);
		builder.setCancelable(false);		
		builder.setPositiveButton(
				R.string.quizmain_alert_finsh_positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 닫기
					}
				});

		dialog = builder.create();
		dialog.show();
	}
	
	/**
	 * This method is used to alert popup when the attempted question is incorrect and save the result and quit the quiz.
	 */
	private void showSurvivalEventWrongAnswerAlertDialog() {
		Dialog dialog;
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(R.string.survival_event_incorrect_answer_alert_title);
		builder.setMessage(R.string.survival_event_incorrect_answer_alert_message);
		builder.setCancelable(false);		
		builder.setPositiveButton(
				R.string.quizmain_alert_finsh_positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						registerEventResult();
						dialog.dismiss(); // 닫기
					}
				});

		dialog = builder.create();
		dialog.show();
	}

}
