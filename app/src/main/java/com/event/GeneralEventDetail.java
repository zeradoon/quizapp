package com.event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.common.CommonUtil;
import com.ranking.RankingEventBasedIndividual;
import com.ranking.RankingEventBasedTeam;
import com.ranking.RankingReal;
import com.skcc.portal.skmsquiz.R;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to publish General Event Details in SKMS Quiz Application.
 */
public class GeneralEventDetail extends Activity implements OnClickListener {
	/* GeneralEventDetail class context */
	private Context m_context = this;

	/* logging variable */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = GeneralEventDetail.class.getSimpleName();
	
	/* member representing user id */
	private String m_user_id;
	
	/* member representing user dept */
	private String m_user_dept;
	
	/* member representing user company code */
	private String m_comp_cd;
	
	/* member representing event code */
	private String m_event_id;
	
	/* member representing quiz code */
	private String m_quiz_id;
	
	/* member representing quiz type */
	private String m_quiz_type;
	
	/* member representing question count */
	private String m_ques_count;
	
	/* member representing quiz completion time */
	private String m_quiz_comp_time;
	
	/* member representing event type */
	private String m_event_type;
	
	/* member representing event name */
	private String m_event_name;
	
	/**
	 * This method is called once on creation of the GeneralEvent Detail Activity.
	 * It is used for initialization of required variables.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generaleventdetail);

		/* set values input by user to member variables */
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_user_dept = getIntent().getStringExtra(CommonUtil.USER_DEPT);
		m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		m_event_id = getIntent().getStringExtra(CommonUtil.EVENT_ID);
		m_quiz_id = getIntent().getStringExtra(CommonUtil.QUIZ_ID);
		m_quiz_type = getIntent().getStringExtra(CommonUtil.QUIZ_TYPE_NM);
		m_ques_count = getIntent().getStringExtra(CommonUtil.QUES_COUNT);
		m_quiz_comp_time = getIntent().getStringExtra(CommonUtil.QUIZ_COMPLETION_TIME);
		m_event_type = getIntent().getStringExtra(CommonUtil.EVENT_TYPE_NAME);
		m_event_name = getIntent().getStringExtra(CommonUtil.EVENT_NAME);
		
		((TextView) findViewById(R.id.generalevent_detail_entry_name)).setText(getIntent().getStringExtra(CommonUtil.EVENT_NAME));
		((TextView) findViewById(R.id.generalevent_detail_entry_time)).setText(CommonUtil.GENERAL_EVENT_DETAIL_SCHEDULE + getIntent().getStringExtra(CommonUtil.START_DT) + " ~ " + getIntent().getStringExtra(CommonUtil.END_DT));
		((TextView) findViewById(R.id.generalevent_detail_entry_desc)).setText(getIntent().getStringExtra(CommonUtil.EVENT_DESC));
	}
	
	/**
	 * This method is used to start General Event Activity when escape key is pressed.
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
			Intent intent = new Intent(GeneralEventDetail.this, GeneralEvent.class);
			Bundle generalEventDetail = new Bundle();
			
			generalEventDetail.putString(CommonUtil.USER_ID, m_user_id);
			generalEventDetail.putString(CommonUtil.USER_DEPT, m_user_dept);
			generalEventDetail.putString(CommonUtil.COMP_CD, m_comp_cd);
			
			intent.putExtras(generalEventDetail);
			startActivity(intent);
			finish();
			
			return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * This method is called when user click anything on GeneralEventDetail screen.
	 * 
	 * @param genEventDetailViewObj
	 */
	@Override
	public void onClick(View genEventDetailViewObj) {
		switch (genEventDetailViewObj.getId()) {
			case R.id.GeneralEventJoinBtn:				// If the object is Join Event button then invoke event join operation.
				joinGeneralEvent();
				break;
			case R.id.GeneralEventRankingBtn:			// If the object is ranking button then invoke ranking screen.
				viewTeamRanking();
				break;
		}
	}
	
	/**
	 * This method is used to navigate to team ranking for particular event.
	 */
	private void viewTeamRanking() {
		Intent intent = null;
		
		if (m_event_type.equalsIgnoreCase(CommonUtil.EVENT_TYPE_GENERAL)) {
			intent =  new Intent(GeneralEventDetail.this, RankingEventBasedIndividual.class);
		} else {
			intent =  new Intent(GeneralEventDetail.this, RankingEventBasedTeam.class);
		}
		
		Bundle generalEventDetail = new Bundle();
		
		generalEventDetail.putString(CommonUtil.EVENT_ID, m_event_id);
		generalEventDetail.putString(CommonUtil.EVENT_NAME, m_event_name);
		generalEventDetail.putString(CommonUtil.EVENT_TYPE_NAME, m_event_type);
		generalEventDetail.putString(CommonUtil.USER_ID, m_user_id);
		generalEventDetail.putString(CommonUtil.USER_DEPT, m_user_dept);
		generalEventDetail.putString(CommonUtil.COMP_CD, m_comp_cd);
		
		intent.putExtras(generalEventDetail);
		startActivity(intent);
		finish();
	}
	
	/**
	 * This method is used to navigate to general event quiz and join the quiz.
	 */
	private void joinGeneralEvent() {
		Bundle generalEventDetail = new Bundle();
		
		generalEventDetail.putString(CommonUtil.EVENT_ID, m_event_id);
		generalEventDetail.putString(CommonUtil.EVENT_TYPE_NAME, m_event_type);
		generalEventDetail.putString(CommonUtil.QUIZ_ID, m_quiz_id);
		generalEventDetail.putString(CommonUtil.QUIZ_TYPE_NM, m_quiz_type);
		generalEventDetail.putString(CommonUtil.QUES_COUNT, m_ques_count);
		generalEventDetail.putString(CommonUtil.QUIZ_COMPLETION_TIME, m_quiz_comp_time);
		generalEventDetail.putString(CommonUtil.USER_ID, m_user_id);
		generalEventDetail.putString(CommonUtil.USER_DEPT, m_user_dept);
		generalEventDetail.putString(CommonUtil.COMP_CD, m_comp_cd);
		
		if (m_quiz_type.equalsIgnoreCase(CommonUtil.QUIZ_TYPE_SURVIVAL)) {
			Intent intent = new Intent(GeneralEventDetail.this, SurvivalEventQuiz.class);
			intent.putExtras(generalEventDetail);
			
			startActivity(intent);
			finish();
		} else {
			Intent intent = new Intent(GeneralEventDetail.this, GeneralEventQuiz.class);
			intent.putExtras(generalEventDetail);
			
			startActivity(intent);
			finish();
		}
	}

}
