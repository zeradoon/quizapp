package com.event;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import android.view.Menu;
import android.view.MenuItem;
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
import com.real.real_quiz_resultlist;
import com.skcc.portal.skmsquiz.R;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to join General Event Quiz in SKMS Quiz Application.
 */
@SuppressWarnings("unused")
public class GeneralEventQuiz extends AbstractAsyncActivity  {
	/* GeneralEventQuiz class context */
	private Context m_context = this;

	/* logging variable */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = GeneralEventQuiz.class.getSimpleName();
	
	int generalevent_cnt = 1;
	int q_type = 0;
	int GENERALEVENT_QUIZ_TOTAL_CNT = 0;
	int[] sel_qType = new int[0];	
	String[] my_answer = new String[0];

	String[] correct_answer = new String[0];
	int[] generalevent_jungdab_yn = new int[0];
	String[] generalevent_seq_no = new String[0];
	String[] sim_answer = new String[0];	
	long time = 1 * 60 * 1000;
	
	int intro_q_type = 0;
	int sel_q_type = 0;

	int generalevent_qtype = 0;
	int intro_qtype = 0;
	int input_cnt = 0;
	String cnt_string = "";

	String your_shortanswer = "";
	String str_cnt2 = "";
	String str = "";
	String Temp_answer_ox = "";
	String your_multiple_answer = "";
	int resultColor = 0xFFFF0000;
	int black = 0xFF000000;
	boolean generalevent_trial = false;
	boolean loop = true;
	String phone_num = "";
	
	HttpURLConnection conn;
	URL url;
	
	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> answerList = new ArrayList<HashMap<String, String>>();
	
	int hours, mins, secs;
	double oo, xx, eee, temp;
	public String answer[] = { "Q_ANSWER", "Q_WR_ANSWER1", "Q_WR_ANSWER2","Q_WR_ANSWER3" };
	
	TextView generalevent_clock;
	boolean isAlive = false;
	boolean isThread = false;
	TimeCountDown tcd;
	TimeHandler handler;
	boolean re_send = false;
	// boolean real_solving_stop = false;
	int list_size = 0;

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
	
	/* member representing event type name */
	private String m_event_type;
	
	/* member representing quiz code */
	private String m_quiz_id;
	
	/* member representing question count */
	private int m_ques_count;
	
	/* member representing event virtual team name */
	private String m_event_virtual_team_name;
	
	private ListView m_answerListView;
	private List<HashMap<String, String>> m_viewAnswerList;
	private GeneralEventQuizAdapter m_generalEventQuizAdapter;

	private static final String INSERT_EVENT_VIRTUAL_TEAM_MEMBER = "insertEventVirtualTeamMember.do";
	private static final String GENERAL_EVENT_QUIZ = "generalEventQuiz.do"; 
	private static final String GENERAL_EVENT_QUIZ_ANSWERS = "generalEventQuizAnswers.do";
	
	private static final int GENERAL_QUIZ_ALERT_FINISH = 1;
	
	/**
	 * This method is called once on creation of the GeneralEventQuiz Activity.
	 * It is used for initialization of required variables.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generalevent_quiz_main);
		
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
		time = (compTime.getHours()*60*60*1000) + (compTime.getMinutes()*60*1000) + (compTime.getSeconds()*1000);
		
		GENERALEVENT_QUIZ_TOTAL_CNT = m_ques_count;
		sel_qType = new int[m_ques_count];	
		my_answer = new String[m_ques_count];
		correct_answer = new String[m_ques_count];
		generalevent_jungdab_yn = new int[m_ques_count];
		generalevent_seq_no = new String[m_ques_count];
		sim_answer = new String[m_ques_count];	
		generalevent_clock = (TextView) findViewById(R.id.generalevent_clock);

		ImageView m_btn_home = (ImageView)findViewById(R.id.btn_home);
		m_btn_home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(GENERAL_QUIZ_ALERT_FINISH);
				//finish();
			}
		});

		m_context = this;
		m_activity = this;

		m_answerListView = (ListView) findViewById(R.id.generalevent_answerlistview);
		//m_answerListView.setOnItemClickListener(this);

		m_generalEventQuizAdapter = new GeneralEventQuizAdapter(m_viewAnswerList, m_ques_count, my_answer,  m_context);
		m_answerListView.setAdapter(m_generalEventQuizAdapter);

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
			showDialog(GENERAL_QUIZ_ALERT_FINISH);

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
			case GENERAL_QUIZ_ALERT_FINISH:
				dialog = showGeneralQuizAlertFinish();
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
	private Dialog showGeneralQuizAlertFinish() {
		Dialog dialog;
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(R.string.generalquiz_alert_finish_title);
		builder.setMessage(R.string.generalquiz_alert_finish_message);
		builder.setPositiveButton(
				R.string.generalquiz_alert_finish_positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						registerEventResult();
						dialog.dismiss(); // 닫기
						finish();
					}
				});
		builder.setNegativeButton(
				R.string.generalquiz_alert_finish_negative_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 닫기

					}
				});
		dialog = builder.create();
		return dialog;
	}
	
	/**
	 * This method selects a particular question view based on question type.
	 * @param v
	 */
	public void monclick(View v) {
		generalevent_view_select(sel_qType[v.getId()-1], v.getId());
	}
	
	/**
	 * This method refresh the question list.
	 */
	private void refreshList() {
		if (m_viewAnswerList == null) {
			m_viewAnswerList = new ArrayList<HashMap<String, String>>();
			m_viewAnswerList = list;
		}

		m_generalEventQuizAdapter.setGeneralEventQuizAnswerList(m_viewAnswerList);
		m_generalEventQuizAdapter.notifyDataSetChanged();
		
	}
	
	/**
	 * This method is used to navigate to next question when next button is clicked.
	 */
	private void nextQuestion() {
		String quesType = "";
		generalevent_cnt++;
		input_cnt = generalevent_cnt - 1;
		
		quesType = (list.get(input_cnt)).get(CommonUtil.QUES_TYPE);
		
		if (CommonUtil.QUES_TYPE_OX.equalsIgnoreCase(quesType)) {
			sel_q_type = 1;
        } else if (CommonUtil.QUES_TYPE_MULTI_CHOICE.equalsIgnoreCase(quesType)) {
        	sel_q_type = 2;
        } else if (CommonUtil.QUES_TYPE_SHORT_ANSWER.equalsIgnoreCase(quesType)) {
        	sel_q_type = 3;
        }
		
		str_cnt2 = Integer.toString(generalevent_cnt);
		str = Integer.toString(sel_q_type);
		
		generalevent_view_select(sel_q_type, generalevent_cnt);
	}
	
	/**
	 * This method save the user choice of multiple type answer in member variable.
	 * 
	 * @return boolean
	 */
	private boolean saveMultipleAnswer() {
		input_cnt = generalevent_cnt - 1;
		my_answer[input_cnt] = your_multiple_answer;
		
		m_generalEventQuizAdapter.setAnswerList(my_answer);
		m_generalEventQuizAdapter.notifyDataSetChanged();
		
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
		input_cnt = generalevent_cnt - 1;
		my_answer[input_cnt] = Temp_answer_ox;

		m_generalEventQuizAdapter.setAnswerList(my_answer);
		m_generalEventQuizAdapter.notifyDataSetChanged();

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
		input_cnt = generalevent_cnt - 1;
		str_cnt2 = Integer.toString(generalevent_cnt);
		EditText et = (EditText) findViewById(R.id.youranswer_generalevent_edittext);
		your_shortanswer = et.getText().toString();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
		my_answer[input_cnt] = your_shortanswer;

		m_generalEventQuizAdapter.setAnswerList(my_answer);
		m_generalEventQuizAdapter.notifyDataSetChanged();

		if ("".equals(et.getText().toString())) {
			CommonUtil.showAlert(GeneralEventQuiz.this,CommonUtil.ALERT_TITLE_NOTICE,CommonUtil.ALERT_MESSAGE_SUBJECTIVE,CommonUtil.ALERT_POSITIVE_BUTTON);
			return false;
		} else {
			//Toast.makeText(m_context, R.string.realquiz_store_answer, Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}
	
	
	/**
	 * This method activates a particular view of the quiz based on user selection.
	 * 
	 * @param v
	 */
	public void onclick(View v) {
		String quesType = "";
		
		switch (v.getId()) {
			case R.id.generalevent_answerlistview :
				break;
				
			case R.id.btn_generalevent_previous_multiple:
			case R.id.btn_generalevent_previous_ox:
			case R.id.btn_generalevent_previous_shortanswer:
				generalevent_cnt--;
				input_cnt = generalevent_cnt - 1;
				quesType = (list.get(input_cnt)).get(CommonUtil.QUES_TYPE);
				
				if (CommonUtil.QUES_TYPE_OX.equalsIgnoreCase(quesType)) {
					sel_q_type = 1;
		        } else if (CommonUtil.QUES_TYPE_MULTI_CHOICE.equalsIgnoreCase(quesType)) {
		        	sel_q_type = 2;
		        } else if (CommonUtil.QUES_TYPE_SHORT_ANSWER.equalsIgnoreCase(quesType)) {
		        	sel_q_type = 3;
		        }
	
				generalevent_view_select(sel_q_type, generalevent_cnt);
				
				break;
			case R.id.btn_generalevent_next_multiple:
				if (saveMultipleAnswer()) {
					nextQuestion();
				}
				break;
			case R.id.btn_generalevent_next_ox:
				if (saveOXAnswer()) {
					nextQuestion();
				}
				break;
			case R.id.btn_generalevent_next_shortanswer:
				if (saveShortAnswer()) {
					nextQuestion();
				}
				
				break;
			//case R.id.btn_generalevent_answerlist:
				
				
			//	break;
			case R.id.btn_generalevent_quiz_answer_o:
				Temp_answer_ox = CommonUtil.QUES_TYPE_OX_ANSWER_O;
				setOXButtonImage();
				
				break;
			case R.id.btn_generalevent_quiz_answer_x:
				Temp_answer_ox = CommonUtil.QUES_TYPE_OX_ANSWER_X;
				setOXButtonImage();
				
				break;
			case R.id.btn_generalevent_quiz_answer_radio1:
				RadioButton rg1 = (RadioButton) findViewById(R.id.btn_generalevent_quiz_answer_radio1);
				your_multiple_answer = rg1.getText().toString();
				
				break;
			case R.id.btn_generalevent_quiz_answer_radio2:
				RadioButton rg2 = (RadioButton) findViewById(R.id.btn_generalevent_quiz_answer_radio2);
				your_multiple_answer = rg2.getText().toString();
	
				break;
			case R.id.btn_generalevent_quiz_answer_radio3:
				RadioButton rg3 = (RadioButton) findViewById(R.id.btn_generalevent_quiz_answer_radio3);
				your_multiple_answer = rg3.getText().toString();
	
				break;
			case R.id.btn_generalevent_quiz_answer_radio4:
				RadioButton rg4 = (RadioButton) findViewById(R.id.btn_generalevent_quiz_answer_radio4);
				your_multiple_answer = rg4.getText().toString();
	
				break;
			case R.id.btn_generalevent_summitsheet_ox:
				if (saveOXAnswer()) {
					findViewById(R.id.generalevent_quiz_ox).setVisibility(LinearLayout.GONE);
					findViewById(R.id.generalevent_quiz_multiple).setVisibility(LinearLayout.GONE);
					findViewById(R.id.generalevent_quiz_shortanswer).setVisibility(LinearLayout.GONE);
					findViewById(R.id.generalevent_quiz_answerlist).setVisibility(LinearLayout.VISIBLE);
				}
				
				break;
			case R.id.btn_generalevent_summitsheet_multiple:
				if (saveMultipleAnswer()) {
					findViewById(R.id.generalevent_quiz_ox).setVisibility(LinearLayout.GONE);
					findViewById(R.id.generalevent_quiz_multiple).setVisibility(LinearLayout.GONE);
					findViewById(R.id.generalevent_quiz_shortanswer).setVisibility(LinearLayout.GONE);
					findViewById(R.id.generalevent_quiz_answerlist).setVisibility(LinearLayout.VISIBLE);
				}

				break;
			case R.id.btn_generalevent_summitsheet_shortanswer:
				if (saveShortAnswer()) {
					findViewById(R.id.generalevent_quiz_ox).setVisibility(LinearLayout.GONE);
					findViewById(R.id.generalevent_quiz_multiple).setVisibility(LinearLayout.GONE);
					findViewById(R.id.generalevent_quiz_shortanswer).setVisibility(LinearLayout.GONE);
					findViewById(R.id.generalevent_quiz_answerlist).setVisibility(LinearLayout.VISIBLE);
				}
				
				break;
			case R.id.btn_generalevent_summitsheet:
				registerEventResult();
				break;
		}
	}
	
	/**
	 * This method saves the event result to database.
	 */
	private void registerEventResult() {
		String quesType = "";
		
		if (CommonUtil.showNetworkAlert(GeneralEventQuiz.this)) {
			re_send = true;
			tcd = null;
			stopTimer();
			
			for (int i = 0; i < m_ques_count; i++) {
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
						generalevent_jungdab_yn[i] = 0;
				    else
				    	generalevent_jungdab_yn[i] = 1;
				} else {           //3번 유형인경우
					String str_my_answer      = my_answer[i].toLowerCase().trim().replaceAll(" ",""); 
                    String str_correct_answer = correct_answer[i].toLowerCase().trim().replaceAll(" ","");
                    
					if ("".equals(sim_answer[i].trim())) {
						if (str_my_answer.equals(str_correct_answer))
							generalevent_jungdab_yn[i] = 0;
						else
							generalevent_jungdab_yn[i] = 1;
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
								generalevent_jungdab_yn[i] = 0;
								break;
							} else
								generalevent_jungdab_yn[i] = 1;
						}// 중첩for문 끝
						if(str_my_answer.equalsIgnoreCase(str_correct_answer))
							generalevent_jungdab_yn[i] = 0;
					}
				}
			}// for문 끝

			Intent intent = new Intent(GeneralEventQuiz.this, GeneralEventQuizResult.class);
			Bundle generalevent_result = new Bundle();
			
			generalevent_result.putIntArray(CommonUtil.GENERAL_EVENT_JUNGDAB, generalevent_jungdab_yn);
			generalevent_result.putStringArray(CommonUtil.GENERAL_EVENT_SEQ, generalevent_seq_no);
			generalevent_result.putString(CommonUtil.USER_ID, m_user_id);
			generalevent_result.putString(CommonUtil.USER_DEPT, m_user_dept);
			generalevent_result.putString(CommonUtil.COMP_CD, m_comp_cd);
			generalevent_result.putString(CommonUtil.EVENT_ID, m_event_id);
			generalevent_result.putString(CommonUtil.EVENT_NAME, m_event_name);
			generalevent_result.putString(CommonUtil.EVENT_TYPE_NAME, m_event_type);
			generalevent_result.putString(CommonUtil.QUIZ_ID, m_quiz_id);
			generalevent_result.putString(CommonUtil.QUES_COUNT, String.valueOf(m_ques_count));
			generalevent_result.putString(CommonUtil.VIRTUAL_TEAM_NAME, m_event_virtual_team_name);
			
			intent.putExtras(generalevent_result);
			startActivity(intent);
			finish();
		}
	}
	
	/**
	 * This method set the image for OX Type question in view.
	 */
	private void setOXButtonImage() {
		ImageButton oButton = (ImageButton) findViewById(R.id.btn_generalevent_quiz_answer_o);
		ImageButton xButton = (ImageButton) findViewById(R.id.btn_generalevent_quiz_answer_x);
		
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
	 * This method set the layout for quiz question when the quiz is traversed.
	 * 
	 * @param q_type
	 * @param cur_cnt
	 */
	public void generalevent_view_select(int q_type, int cur_cnt) {
		generalevent_view_init();

		/**
		 * 현재 타이머가 살아 있을때만 문제 이동 가능 그렇지 않으면 답안지로 이동
		 */
		if (isAlive) {
			LinearLayout curView = null;
			
			if (q_type == 1) {
				generalevent_cnt = cur_cnt;
				input_cnt = cur_cnt - 1;
				curView = (LinearLayout) findViewById(R.id.generalevent_quiz_ox);
				
				findViewById(R.id.generalevent_quiz_ox).setVisibility(LinearLayout.VISIBLE);
				findViewById(R.id.generalevent_quiz_multiple).setVisibility(LinearLayout.GONE);
				findViewById(R.id.generalevent_quiz_shortanswer).setVisibility(LinearLayout.GONE);
				findViewById(R.id.generalevent_quiz_answerlist).setVisibility(LinearLayout.GONE);

				cnt_string = CommonUtil.QUESTION_STRING + Integer.toString(cur_cnt);
				
				if (cur_cnt == 1 && cur_cnt < m_ques_count) {
					findViewById(R.id.btn_generalevent_previous_ox).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_generalevent_next_ox).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_generalevent_summitsheet_ox).setVisibility(LinearLayout.GONE);
				} else if (cur_cnt == 1 && cur_cnt == m_ques_count) {
					findViewById(R.id.btn_generalevent_previous_ox).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_generalevent_next_ox).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_generalevent_summitsheet_ox).setVisibility(LinearLayout.VISIBLE);
				} else if (cur_cnt == m_ques_count) {
					findViewById(R.id.btn_generalevent_next_ox).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_generalevent_previous_ox).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_generalevent_summitsheet_ox).setVisibility(LinearLayout.VISIBLE);
				} else {
					findViewById(R.id.btn_generalevent_next_ox).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_generalevent_previous_ox).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_generalevent_summitsheet_ox).setVisibility(LinearLayout.GONE);
				}
				
				String q_ox = String.valueOf((list.get(input_cnt)).get(CommonUtil.Q_QUESTION));
				TextView ox_tv1 = ((TextView) findViewById(R.id.generalevent_quiz_1_question));
				TextView ox_tv2 = ((TextView) findViewById(R.id.generalevent_quiz_num1));

				ox_tv1.setText(q_ox);
				ox_tv2.setText(cnt_string);

				Temp_answer_ox = my_answer[input_cnt];
				setOXButtonImage();
			} else if (q_type == 2) {
				RadioGroup rGroup = (RadioGroup) findViewById(R.id.btn_generalevent_quiz_answer_radio);
				rGroup.clearCheck();

				findViewById(R.id.btn_generalevent_quiz_answer_radio).setVisibility(LinearLayout.GONE);
				generalevent_cnt = cur_cnt;
				input_cnt = cur_cnt - 1;
				curView = (LinearLayout) findViewById(R.id.generalevent_quiz_multiple);
				
				findViewById(R.id.generalevent_quiz_ox).setVisibility(LinearLayout.GONE);
				findViewById(R.id.generalevent_quiz_multiple).setVisibility(LinearLayout.VISIBLE);
				findViewById(R.id.generalevent_quiz_shortanswer).setVisibility(LinearLayout.GONE);
				findViewById(R.id.generalevent_quiz_answerlist).setVisibility(LinearLayout.GONE);
				findViewById(R.id.btn_generalevent_quiz_answer_radio).setVisibility(LinearLayout.VISIBLE);
				
				if (cur_cnt == 1 && cur_cnt < m_ques_count) {
					findViewById(R.id.btn_generalevent_previous_multiple).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_generalevent_next_multiple).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_generalevent_summitsheet_multiple).setVisibility(LinearLayout.GONE);
				} else if (cur_cnt == 1 && cur_cnt == m_ques_count) {
					findViewById(R.id.btn_generalevent_previous_multiple).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_generalevent_next_multiple).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_generalevent_summitsheet_multiple).setVisibility(LinearLayout.VISIBLE);
				} else if (cur_cnt == m_ques_count) {
					findViewById(R.id.btn_generalevent_next_multiple).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_generalevent_previous_multiple).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_generalevent_summitsheet_multiple).setVisibility(LinearLayout.VISIBLE);
				} else {
					findViewById(R.id.btn_generalevent_next_multiple).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_generalevent_previous_multiple).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_generalevent_summitsheet_multiple).setVisibility(LinearLayout.GONE);
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
				
				((TextView) findViewById(R.id.btn_generalevent_quiz_answer_radio1)).setText(lAnswerList.get(0).get(CommonUtil.Q_ANSWER));
				((TextView) findViewById(R.id.btn_generalevent_quiz_answer_radio2)).setText(lAnswerList.get(1).get(CommonUtil.Q_ANSWER));
				((TextView) findViewById(R.id.btn_generalevent_quiz_answer_radio3)).setText(lAnswerList.get(2).get(CommonUtil.Q_ANSWER));
				((TextView) findViewById(R.id.btn_generalevent_quiz_answer_radio4)).setText(lAnswerList.get(3).get(CommonUtil.Q_ANSWER));

				String q_multiple = String.valueOf((list.get(input_cnt)).get(CommonUtil.Q_QUESTION));
				TextView multiple_tv1 = ((TextView) findViewById(R.id.generalevent_quiz_2_question));
				TextView multiple_tv2 = ((TextView) findViewById(R.id.generalevent_quiz_num2));
				multiple_tv1.setText(q_multiple);
				multiple_tv2.setText(cnt_string);

				your_multiple_answer = my_answer[input_cnt];
				setRadioButton(your_multiple_answer);
			} else if (q_type == 3) {
				generalevent_cnt = cur_cnt;
				input_cnt = cur_cnt - 1;
				curView = (LinearLayout) findViewById(R.id.generalevent_quiz_shortanswer);
				
				findViewById(R.id.generalevent_quiz_ox).setVisibility(LinearLayout.GONE);
				findViewById(R.id.generalevent_quiz_multiple).setVisibility(LinearLayout.GONE);
				findViewById(R.id.generalevent_quiz_shortanswer).setVisibility(LinearLayout.VISIBLE);
				findViewById(R.id.generalevent_quiz_answerlist).setVisibility(LinearLayout.GONE);

				if (cur_cnt == 1 && cur_cnt < m_ques_count) {
					findViewById(R.id.btn_generalevent_previous_shortanswer).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_generalevent_next_shortanswer).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_generalevent_summitsheet_shortanswer).setVisibility(LinearLayout.GONE);
				} else if (cur_cnt == 1 && cur_cnt == m_ques_count) {
					findViewById(R.id.btn_generalevent_previous_shortanswer).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_generalevent_next_shortanswer).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_generalevent_summitsheet_shortanswer).setVisibility(LinearLayout.VISIBLE);
				} else if (cur_cnt == m_ques_count) {
					findViewById(R.id.btn_generalevent_next_shortanswer).setVisibility(LinearLayout.GONE);
					findViewById(R.id.btn_generalevent_previous_shortanswer).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_generalevent_summitsheet_shortanswer).setVisibility(LinearLayout.VISIBLE);
				} else {
					findViewById(R.id.btn_generalevent_next_shortanswer).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_generalevent_previous_shortanswer).setVisibility(LinearLayout.VISIBLE);
					findViewById(R.id.btn_generalevent_summitsheet_shortanswer).setVisibility(LinearLayout.GONE);
				}
				
				cnt_string = CommonUtil.QUESTION_STRING + Integer.toString(cur_cnt);
				
				String q_shortanswer = String.valueOf((list.get(input_cnt)).get(CommonUtil.Q_QUESTION));
				TextView short_tv1 = ((TextView) findViewById(R.id.generalevent_quiz_3_question));
				TextView shotr_tv2 = ((TextView) findViewById(R.id.generalevent_quiz_num3));
				short_tv1.setText(q_shortanswer);
				shotr_tv2.setText(cnt_string);

				your_shortanswer = my_answer[input_cnt];
				EditText et = (EditText) findViewById(R.id.youranswer_generalevent_edittext);
				et.setText(your_shortanswer);
			}
			
			android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE);         
			imm.hideSoftInputFromWindow(curView.getWindowToken(), 0); 
		} else {
			//generalevent_go_answerlist();
			Toast.makeText(m_context, "제한시간이 만료되었습니다.", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * This method check the radio button for user selection.
	 *  
	 * @param answer
	 */
	private void setRadioButton(String answer) {
		RadioButton rg1 = (RadioButton) findViewById(R.id.btn_generalevent_quiz_answer_radio1);
		String rg1_answer = rg1.getText().toString();
		RadioButton rg2 = (RadioButton) findViewById(R.id.btn_generalevent_quiz_answer_radio2);
		String rg2_answer = rg2.getText().toString();
		RadioButton rg3 = (RadioButton) findViewById(R.id.btn_generalevent_quiz_answer_radio3);
		String rg3_answer = rg3.getText().toString();
		RadioButton rg4 = (RadioButton) findViewById(R.id.btn_generalevent_quiz_answer_radio4);
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
	 * This method initialize the initial layout for general event quiz.
	 */
	public void generalevent_view_init() {
		findViewById(R.id.generalevent_quiz_ox).setVisibility(LinearLayout.GONE);
		findViewById(R.id.generalevent_quiz_multiple).setVisibility(LinearLayout.GONE);
		findViewById(R.id.generalevent_quiz_shortanswer).setVisibility(LinearLayout.GONE);
		findViewById(R.id.generalevent_quiz_answerlist).setVisibility(LinearLayout.GONE);
	}
	
	/**
	 * This method perform final actions when the quiz is exited.
	 */
	public void general_exit() {
		if (re_send == false) {
			for (int i = 0; i < m_ques_count; i++) {
				if (my_answer[i].equals(correct_answer[i]))
					generalevent_jungdab_yn[i] = 0;
				else
					generalevent_jungdab_yn[i] = 1;
			}
			
			Intent intent = new Intent(GeneralEventQuiz.this,real_quiz_resultlist.class);
			Bundle generalevent_result = new Bundle();

			generalevent_result.putIntArray(CommonUtil.GENERAL_EVENT_JUNGDAB, generalevent_jungdab_yn);
			generalevent_result.putStringArray(CommonUtil.GENERAL_EVENT_SEQ, generalevent_seq_no);
			generalevent_result.putString(CommonUtil.USER_ID, m_user_id);
			generalevent_result.putString(CommonUtil.USER_DEPT, m_user_dept);
			generalevent_result.putString(CommonUtil.COMP_CD, m_comp_cd);
			generalevent_result.putString(CommonUtil.EVENT_ID, m_event_id);
			generalevent_result.putString(CommonUtil.EVENT_TYPE_NAME, m_event_type);
			generalevent_result.putString(CommonUtil.QUIZ_ID, m_quiz_id);
			generalevent_result.putString(CommonUtil.QUES_COUNT, String.valueOf(m_ques_count));
			generalevent_result.putString(CommonUtil.VIRTUAL_TEAM_NAME, m_event_virtual_team_name);
			
			intent.putExtras(generalevent_result);
			startActivity(intent);
			finish();
		}
	}
	
	/**
	 * This method query a particular URL address and return values as a list of map.
	 * 
	 * @param addr
	 * @return
	 */
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
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					
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
							tagName = line.substring(line.indexOf("<") + 1, line.indexOf(">"));
							tagValue = line.substring( line.indexOf("<![CDATA[") + 9, line.indexOf("]]>"));

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
	
	/**
	 * This method apply the choice menu based on the selected item.
	 * 
	 * @param item
	 * @return boolean
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		applyMenuChoice(item);
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * This method populate the menu.
	 * 
	 * @param menu
	 */
	private void populateMenu(Menu menu) {
		for (int i=1; i<=m_ques_count; i++) {
			menu.add(Menu.NONE, i, Menu.NONE, i + "번");
		}
	}
	
	/**
	 * This method apply the choice to menu based on selection.
	 * 
	 * @param item
	 */
	private void applyMenuChoice(MenuItem item) {
		int menu_id = item.getItemId();
		Intent i = null;
		
		startActivityForResult(i, menu_id);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 3) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		Log.d("", "onDestroy()");
		super.onDestroy();

		stopTimer();
	}
	
	/**
	 * This method start the timer as soon as quiz is started.
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
	 * This method stop the timer as soon as quiz is finished or exited.
	 */
	private void stopTimer() {
		this.isAlive = false;
		this.isThread = false;
		// tcd.interrupt();
		tcd = null;
	}
	
	/**
	 * This class is used to handle timer for General Event Quiz in SKMS Quiz Application.
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
					showGeneralEventTimeCompleteAlertDialog();
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
	private void showGeneralEventTimeCompleteAlertDialog() {
		Dialog dialog;
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(this);
		
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(R.string.general_event_time_complete_alert_title);
		builder.setMessage(R.string.general_event_time_complete_alert_message);
		builder.setCancelable(false);		
		builder.setPositiveButton(R.string.quizmain_alert_finsh_positive_button,
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
					generalevent_jungdab_yn[i] = 0;
					sim_answer[i] = "";
					correct_answer[i] = (list.get(i)).get(CommonUtil.Q_ANSWER);
					generalevent_seq_no[i] = (list.get(i)).get(CommonUtil.SEQ_NO);
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
		
				handler = new TimeHandler(generalevent_clock, time);
				generalevent_clock.setText(handler.getTimeFormat(time));
				startTimer();
				
				refreshList();
				generalevent_view_select(intro_qtype, generalevent_cnt);
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

}


