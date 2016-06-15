package com.event;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonUtil;
import com.common.async.AbstractAsyncActivity;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.ranking.RankingEventBasedIndividual;
import com.ranking.RankingEventBasedTeam;
import com.ranking.RankingReal;
import com.skcc.portal.skmsquiz.R;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to display the result and store the result of Survival Event Quiz in SKMS Quiz Application .
 */
public class SurvivalEventQuizResult extends AbstractAsyncActivity {
	private Context m_context = this;

	/* member representing user score string */
	private String score_str = "";
	
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
	
	/* member representing question count */
	private int m_ques_attempt_count;
	
	/* member representing event virtual team name */
	private String m_event_virtual_team_name;
	
	/* member representing user correct questions. */
	private int[] survivalevent_jungdab;
	
	/* member representing service to validate user input */
	private static final String INSERT_USER_SCORE = "insertSurvivalEventQuizUserScore.do";
	
	
	/**
	 * This method is called once on creation of the SurvivalEventQuizResult Activity.
	 * It is used for initialization of required variables.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.survivalevent_quiz_resultlist);
		
		/* set values input by user to member variables */
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_user_dept = getIntent().getStringExtra(CommonUtil.USER_DEPT);
		m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		m_event_id = getIntent().getStringExtra(CommonUtil.EVENT_ID);
		m_event_name = getIntent().getStringExtra(CommonUtil.EVENT_NAME);
		m_event_type = getIntent().getStringExtra(CommonUtil.EVENT_TYPE_NAME);
		m_quiz_id = getIntent().getStringExtra(CommonUtil.QUIZ_ID);
		m_ques_count = Integer.parseInt(getIntent().getStringExtra(CommonUtil.QUES_COUNT));
		m_event_virtual_team_name = getIntent().getStringExtra(CommonUtil.VIRTUAL_TEAM_NAME);
		m_ques_attempt_count = Integer.parseInt(getIntent().getStringExtra(CommonUtil.QUES_ATTEMPT_COUNT));
		
		survivalevent_jungdab = new int[m_ques_count];
		
		ImageView m_btn_home = (ImageView)findViewById(R.id.btn_home);
		m_btn_home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		survivalevent_jungdab = getIntent().getIntArrayExtra(CommonUtil.SURVIVAL_EVENT_JUNGDAB);
		saveSurvivalEventQuizResult(m_user_id, m_user_dept, m_event_id, m_event_virtual_team_name, survivalevent_jungdab);
	}
	
	/**
	 * This method store the quiz result into the database.
	 * 
	 * @param m_user_id
	 * @param m_user_dept
	 * @param m_event_id
	 * @param m_event_virtual_team_name
	 * @param survivalevent_jungdab
	 */
	private void saveSurvivalEventQuizResult(String m_user_id, String m_user_dept, String m_event_id, String m_event_virtual_team_name, int[] survivalevent_jungdab) {
		
		int user_score = 0;
		for (int i=0; i< m_ques_attempt_count; i++) {
			if (survivalevent_jungdab[i] == 0) {
				user_score = user_score + 10;
			}
		}
		
		final String userScore =  Integer.toString(user_score);
		String url = getString(R.string.base_uri) + INSERT_USER_SCORE;

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_ID, m_user_id);
		parameters.put(CommonUtil.EVENT_ID, m_event_id);
		parameters.put(CommonUtil.FINAL_SCORE, userScore);
		
		if (m_event_type.equalsIgnoreCase(CommonUtil.EVENT_TYPE_VIRTUAL_TEAM)) 
			parameters.put(CommonUtil.USER_DEPT, m_event_virtual_team_name);
		else 
			parameters.put(CommonUtil.USER_DEPT, m_user_dept);
		
		Context context = m_context;
		boolean progressVisible = true;
		int id = 222;
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
					
					if (msgCode.equalsIgnoreCase(CommonUtil.SUCCESS)) {
						String score_str = "시도한 문제 : " + Integer.toString(m_ques_attempt_count) + "문제.  당신의 점수는 " + userScore + "점" ;
						((TextView) findViewById(R.id.survivalevent_result_score)).setText(score_str);
					}
					else 
						Toast.makeText(m_context, getString(R.string.general_event_register_score_failure_alert_message), Toast.LENGTH_SHORT).show();
				}
				
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url, parameters, id, context, callable, progressVisible); 
		task.execute();
	}
	
	/**
	 * This method activates a particular view on quiz result activity based on user selection.
	 * 
	 * @param v
	 */
	public void onclick(View v) {
		Intent intent = null;
		Bundle survivalevent_result = new Bundle();
		
		survivalevent_result.putString(CommonUtil.USER_ID, m_user_id);
		survivalevent_result.putString(CommonUtil.USER_DEPT, m_user_dept);
		survivalevent_result.putString(CommonUtil.COMP_CD, m_comp_cd);
		survivalevent_result.putString(CommonUtil.EVENT_ID, m_event_id);
		survivalevent_result.putString(CommonUtil.EVENT_NAME, m_event_name);
		survivalevent_result.putString(CommonUtil.QUIZ_ID, m_quiz_id);
		survivalevent_result.putString(CommonUtil.QUES_COUNT, String.valueOf(m_ques_count));
		survivalevent_result.putString(CommonUtil.EVENT_TYPE_NAME, m_event_type);
		
		switch (v.getId()) {
			case R.id.survivalevent_quit:
				intent =  new Intent(SurvivalEventQuizResult.this, GeneralEvent.class);
				intent.putExtras(survivalevent_result);
				startActivity(intent);
				finish();
				
				break;
			case R.id.survivalevent_view_ranking: {
				if (m_event_type.equalsIgnoreCase(CommonUtil.EVENT_TYPE_GENERAL)) {
					intent =  new Intent(SurvivalEventQuizResult.this, RankingEventBasedIndividual.class);
				} else {
					intent =  new Intent(SurvivalEventQuizResult.this, RankingEventBasedTeam.class);
				}
				
				intent.putExtras(survivalevent_result);
				startActivity(intent);
				finish();
				
				break;
			}
		}
	}

	
}