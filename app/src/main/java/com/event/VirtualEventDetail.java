package com.event;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.ranking.RankingEventBasedIndividual;
import com.ranking.RankingEventBasedTeam;
import com.ranking.RankingReal;
import com.skcc.portal.skmsquiz.R;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to publish Virtual Event Details in SKMS Quiz Application.
 */
public class VirtualEventDetail extends Activity implements OnClickListener {
	/* VirtualEventDetail class context */
	private Context m_context = this;

	/* logging variable */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = VirtualEventDetail.class.getSimpleName();
	
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
	
	private ArrayList<HashMap<String, String>> m_EventVirtualTeamsList;
	
	private static final String DOWNLOAD_EVENT_VIRTUAL_TEAM_LIST = "getEventVirtualTeamList.do";
	
	/**
	 * This method is called once on creation of the VirtualEvent Detail Activity.
	 * It is used for initialization of required variables.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.virtualeventdetail);
		
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
		
		((TextView) findViewById(R.id.virtualevent_detail_entry_name)).setText(getIntent().getStringExtra(CommonUtil.EVENT_NAME));
		((TextView) findViewById(R.id.virtualevent_detail_entry_time)).setText(CommonUtil.GENERAL_EVENT_DETAIL_SCHEDULE + getIntent().getStringExtra(CommonUtil.START_DT) + " ~ " + getIntent().getStringExtra(CommonUtil.END_DT));
		((TextView) findViewById(R.id.virtualevent_detail_entry_desc)).setText(getIntent().getStringExtra(CommonUtil.EVENT_DESC));
		
		downloadEventVirtualTeamsData(m_event_id);
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
			Intent intent = new Intent(VirtualEventDetail.this, GeneralEvent.class);
			Bundle virtualEventDetail = new Bundle();
			
			virtualEventDetail.putString(CommonUtil.USER_ID, m_user_id);
			virtualEventDetail.putString(CommonUtil.USER_DEPT, m_user_dept);
			virtualEventDetail.putString(CommonUtil.COMP_CD, m_comp_cd);
			
			intent.putExtras(virtualEventDetail);
			startActivity(intent);
			finish();
			
			return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * This method set the virtual team name details on the VirtualTeamDetail Activity Screen.
	 */
	private void setVirtualTeamsDetails() {
		RadioGroup rg = (RadioGroup)findViewById(R.id.virtualTeamNameRadioGroup);
		RadioButton rb = null;

		for (int i = 0; i < m_EventVirtualTeamsList.size(); i++) {
			if (m_EventVirtualTeamsList.get(i) != null) {
				rb = new RadioButton(this);
				rb.setId(i);
				rb.setText(m_EventVirtualTeamsList.get(i).get(CommonUtil.VIRTUAL_TEAM_NAME) + "     " + m_EventVirtualTeamsList.get(i).get(CommonUtil.MEMBER_COUNT) + " / " + m_EventVirtualTeamsList.get(i).get(CommonUtil.MAX_MEMBERS));
				rb.setTextColor(Color.BLACK);
				rb.setTextSize(14);
				rg.addView(rb); 
			}
		}
	}
	
	/**
	 * This method download the virtual team data from the database as an async task.
	 * 
	 * @param m_event_id
	 */
	private void downloadEventVirtualTeamsData(String m_event_id) {
		String url = getString(R.string.base_uri) + DOWNLOAD_EVENT_VIRTUAL_TEAM_LIST;
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.EVENT_ID, m_event_id);
		
		Context context = m_context;
		boolean progressVisible = false;
		int id = 222;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {
			@Override
			public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0) {
					m_EventVirtualTeamsList = list;
					setVirtualTeamsDetails();
				}
				
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url, parameters, id, context, callable, progressVisible);
		task.execute();
	}

	/**
	 * This method is called when user click anything on VirtualEventDetail screen.
	 * 
	 * @param genEventDetailViewObj
	 */
	@Override
	public void onClick(View genEventDetailViewObj) {
		switch (genEventDetailViewObj.getId()) {
			case R.id.VirtualEventJoinBtn:			// If the object is Join Event button then invoke event join operation.
				joinVirtualEvent();
				
				break;
			case R.id.VirtualEventRankingBtn:		// If the object is ranking button then invoke ranking screen.
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
			intent =  new Intent(VirtualEventDetail.this, RankingEventBasedIndividual.class);
		} else {
			intent =  new Intent(VirtualEventDetail.this, RankingEventBasedTeam.class);
		}

		Bundle virutalEventDetail = new Bundle();
		
		virutalEventDetail.putString(CommonUtil.EVENT_ID, m_event_id);
		virutalEventDetail.putString(CommonUtil.EVENT_NAME, m_event_name);
		virutalEventDetail.putString(CommonUtil.EVENT_TYPE_NAME, m_event_type);
		virutalEventDetail.putString(CommonUtil.USER_ID, m_user_id);
		virutalEventDetail.putString(CommonUtil.USER_DEPT, m_user_dept);
		virutalEventDetail.putString(CommonUtil.COMP_CD, m_comp_cd);
		
		intent.putExtras(virutalEventDetail);
		startActivity(intent);
		finish();
	}
	
	/**
	 * This method is used to navigate to virtual event quiz and join the quiz.
	 */
	private void joinVirtualEvent() {
		RadioGroup rg = (RadioGroup)findViewById(R.id.virtualTeamNameRadioGroup);
		int selectedId = rg.getCheckedRadioButtonId();
		
		if (selectedId == -1) {
			CommonUtil.showAlert(m_context, CommonUtil.ALERT_MESSAGE_CHOOSE_VIRTUAL_TEAM_TITLE, CommonUtil.ALERT_MESSAGE_CHOOSE_VIRTUAL_TEAM, CommonUtil.ALERT_POSITIVE_BUTTON);
		} else {
			int memberCount = Integer.parseInt(m_EventVirtualTeamsList.get(selectedId).get(CommonUtil.MEMBER_COUNT));
			int limitCount = Integer.parseInt(m_EventVirtualTeamsList.get(selectedId).get(CommonUtil.MAX_MEMBERS));
			String virtualTeamId = m_EventVirtualTeamsList.get(selectedId).get(CommonUtil.ID);
			String virtualTeamName = m_EventVirtualTeamsList.get(selectedId).get(CommonUtil.VIRTUAL_TEAM_NAME);
			Bundle virtualEventDetail = new Bundle();
			
			virtualEventDetail.putString(CommonUtil.EVENT_ID, m_event_id);
			virtualEventDetail.putString(CommonUtil.EVENT_TYPE_NAME, m_event_type);
			virtualEventDetail.putString(CommonUtil.QUIZ_ID, m_quiz_id);
			virtualEventDetail.putString(CommonUtil.QUIZ_TYPE_NM, m_quiz_type);
			virtualEventDetail.putString(CommonUtil.QUES_COUNT, m_ques_count);
			virtualEventDetail.putString(CommonUtil.QUIZ_COMPLETION_TIME, m_quiz_comp_time);
			virtualEventDetail.putString(CommonUtil.USER_ID, m_user_id);
			virtualEventDetail.putString(CommonUtil.USER_DEPT, m_user_dept);
			virtualEventDetail.putString(CommonUtil.COMP_CD, m_comp_cd);
			virtualEventDetail.putString(CommonUtil.VIRTUAL_TEAM_ID, virtualTeamId);
			virtualEventDetail.putString(CommonUtil.VIRTUAL_TEAM_NAME, virtualTeamName);
			
			if (memberCount < limitCount) {
				if (m_quiz_type.equalsIgnoreCase(CommonUtil.QUIZ_TYPE_SURVIVAL)) {
					Intent intent = new Intent(VirtualEventDetail.this, SurvivalEventQuiz.class);
					intent.putExtras(virtualEventDetail);
					
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(VirtualEventDetail.this, GeneralEventQuiz.class);
					intent.putExtras(virtualEventDetail);
					
					startActivity(intent);
					finish();
				}
			} else {
				Toast.makeText(m_context, getString(R.string.virtual_event_team_member_max_alert_message), Toast.LENGTH_SHORT).show();
			}
		}
	}

}
