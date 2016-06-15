package com.event;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.login.UserLogin;
import com.notice.NoticeList;
import com.ranking.RankingEventBasedIndividual;
import com.ranking.RankingEventBasedTeam;
import com.ranking.RankingReal;
import com.skcc.portal.skmsquiz.R;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to publish General Events in SKMS Quiz Application.
 */
public class GeneralEvent extends Activity implements OnScrollListener, OnTouchListener, OnItemClickListener {
	/* GeneralEvent class context */
	private Context m_context = this;

	/* logging variable */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = GeneralEvent.class.getSimpleName();
	
	/* member representing user id */
	private String m_user_id;
	
	/* member representing user dept */
	private String m_user_dept;
	
	/* member representing user company code */
	private String m_comp_cd;
	
	public static final String PREFS_NAME = "MyPrefsFile"; 
	private static final String PREF_USERID = "userid";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_REMEMBERME = "true";
	
	/* member representing services to get general event list */
	private static final String DOWNLOAD_GENERAL_EVENT_LIST = "getGeneralEvents.do";
	private static final int DOWNLOAD_GENERAL_EVENT_LIST_REFRESH = 1;
	private static final int DOWNLOAD_GENERAL_EVENT_LIST_MORE = 2;
	
	/* members representing events list. */
	private ArrayList<HashMap<String, String>> m_viewList;
	private ArrayList<HashMap<String, String>> m_GeneralEventList;
	private ListView m_listView;
	
	/* members representing adapter to inflate events list. */
	private GeneralEventAdapter m_generalEventAdapter;
	
	/* members representing event number and item number. */
	protected int totalListNum;
	protected int itemNum;
	
	/* members representing header and footer variables . */
	private boolean justOnce = false;
	private boolean isHeader = false;
	private View vHeader;
	private View vFooter;
	private LinearLayout llHeader;
	private LinearLayout llFooter;
	
	/* members for scrolling events list. */
	private float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
	private String direction = "";
	private ImageView m_btn_home;

	
	/**
	 * This method is called once on creation of the GeneralEvent Activity.
	 * It is used for initialization of required variables.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generalevent);
		
		/* set values input by user to member variables */
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_user_dept = getIntent().getStringExtra(CommonUtil.USER_DEPT);
		m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		
		m_btn_home = (ImageView) findViewById(R.id.btn_home);
		m_btn_home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		TextView tvTitle = (TextView) findViewById(R.id.header_title_id);
		tvTitle.setText(getString(R.string.general_event_join_title));
		
		m_listView = (ListView) findViewById(R.id.general_event_list);

		vHeader = getLayoutInflater().inflate(R.layout.generalevent_listview_header, null, false);
		vFooter = getLayoutInflater().inflate(R.layout.generalevent_listview_footer, null, false);

		llHeader = (LinearLayout) vHeader.findViewById(R.id.generalevent_listview_header_entry_linearlayout);
		llFooter = (LinearLayout) vFooter.findViewById(R.id.generalevent_listview_footer_entry_linearlayout);

		m_listView.addHeaderView(vHeader);
		m_listView.addFooterView(vFooter);
		m_listView.setOnItemClickListener(this);
		m_listView.setOnScrollListener(this);
		m_listView.setOnTouchListener(this);

		m_generalEventAdapter = new GeneralEventAdapter(m_viewList, m_context);
		m_listView.setAdapter(m_generalEventAdapter);

		downloadGeneralEventData(m_user_id, m_user_dept, m_comp_cd, DOWNLOAD_GENERAL_EVENT_LIST_REFRESH);

		justOnce = false;
		isHeader = false;

		llHeader.setVisibility(View.GONE);
		llFooter.setVisibility(View.GONE);
	}
	
	protected void signOutUser() {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().remove(PREF_USERID).remove(PREF_PASSWORD).remove(PREF_REMEMBERME).commit();
		Intent intent = new Intent(GeneralEvent.this, UserLogin.class);	// create intent to invoke user login activity.
		startActivity(intent);	// start the intent to open user login screen.
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.generalevent, menu);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		
	    switch (item.getItemId()) {
	    	case R.id.home:
	    		intent = new Intent(GeneralEvent.this, com.main.QuizMain.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();
	            return true;
	        case R.id.profile:
	        	intent = new Intent(GeneralEvent.this, com.profile.ProfileMain.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();
	            return true;
	        case R.id.ranking:
	        	intent = new Intent(GeneralEvent.this, com.ranking.RankingSelect.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();

	            return true;
	        case R.id.events:
	        	intent = new Intent(GeneralEvent.this, com.event.GeneralEvent.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();

	            return true;
	        case R.id.signout:
	            signOutUser();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * 	This method is used to resume the GeneralEvent Activity.
	 */
	@Override
	protected void onResume() {
		Log.i(LOG_TAG, "onResume");
		super.onResume();
	}

	/**
	 * This method download the event list from database on configured page size at a time.
	 * 
	 * @param userId
	 * @param userDept
	 * @param compCd
	 * @param flag
	 */
	private void downloadGeneralEventData(String userId, String userDept, String compCd, final int flag) {
		String url = getString(R.string.base_uri) + DOWNLOAD_GENERAL_EVENT_LIST;
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_ID, userId);
		parameters.put(CommonUtil.USER_DEPT, userDept);
		parameters.put(CommonUtil.COMP_CD, compCd);
		parameters.put(CommonUtil.PAGE_SIZE, getString(R.string.general_event_list_page_size));

		if (flag == DOWNLOAD_GENERAL_EVENT_LIST_MORE) {
			int lastPosition = m_GeneralEventList.size() - 1;
			parameters.put(CommonUtil.LAST_EVENT, m_GeneralEventList.get(lastPosition).get(CommonUtil.EVENT_ID));
		}
		
		Context context = m_context;
		boolean progressVisible = false;
		int id = 222;
		
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {
			@Override
			public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0) {
					m_GeneralEventList = list;
					refreshList(flag);
				} else {
					Toast.makeText(m_context, R.string.generalevent_list_empty_message, Toast.LENGTH_SHORT).show();
					llFooter.setVisibility(View.GONE);
					justOnce = false;
				}
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url, parameters, id, context, callable, progressVisible);
		task.execute();
	}
	
	/**
	 * This method perform action when an item is clicked in GeneralEvent List.
	 * 
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = null;
		Bundle generalEventBundle = new Bundle();
		
		generalEventBundle.putString(CommonUtil.EVENT_ID, m_viewList.get(position - 1).get(CommonUtil.EVENT_ID));
		generalEventBundle.putString(CommonUtil.EVENT_NAME, m_viewList.get(position - 1).get(CommonUtil.EVENT_NAME));
		generalEventBundle.putString(CommonUtil.EVENT_TYPE_NAME, m_viewList.get(position - 1).get(CommonUtil.EVENT_TYPE_NAME));
		generalEventBundle.putString(CommonUtil.QUIZ_ID, m_viewList.get(position - 1).get(CommonUtil.QUIZ_ID));
		generalEventBundle.putString(CommonUtil.QUIZ_TYPE_NM, m_viewList.get(position - 1).get(CommonUtil.QUIZ_TYPE_NM));
		generalEventBundle.putString(CommonUtil.QUES_COUNT, m_viewList.get(position - 1).get(CommonUtil.QUES_COUNT));
		generalEventBundle.putString(CommonUtil.START_DT, m_viewList.get(position - 1).get(CommonUtil.START_DT));
		generalEventBundle.putString(CommonUtil.END_DT, m_viewList.get(position - 1).get(CommonUtil.END_DT));
		generalEventBundle.putString(CommonUtil.QUIZ_COMPLETION_TIME, m_viewList.get(position - 1).get(CommonUtil.QUIZ_COMPLETION_TIME));
		generalEventBundle.putString(CommonUtil.EVENT_DESC, m_viewList.get(position - 1).get(CommonUtil.EVENT_DESC));
		generalEventBundle.putString(CommonUtil.USER_ID, m_user_id);
		generalEventBundle.putString(CommonUtil.USER_DEPT, m_user_dept);
		generalEventBundle.putString(CommonUtil.COMP_CD, m_comp_cd);
		
		String eventType = m_viewList.get(position - 1).get(CommonUtil.EVENT_TYPE_NAME);
		
		if (m_viewList.get(position - 1).get(CommonUtil.EVENT_STATUS).equalsIgnoreCase(CommonUtil.EVENT_STATUS_OPEN)) {
			if ( eventType.equalsIgnoreCase(CommonUtil.EVENT_TYPE_GENERAL)) {
				if (!m_viewList.get(position - 1).get(CommonUtil.USER_CAN_JOIN).equalsIgnoreCase(CommonUtil.USER_CAN_JOIN_YES)) {
					Toast.makeText(m_context, R.string.generalevent_list_event_taken_message,	Toast.LENGTH_SHORT).show();
				} else {
					intent = new Intent(GeneralEvent.this, GeneralEventDetail.class);
					intent.putExtras(generalEventBundle);
		
					startActivity(intent);
					finish();
				}
			} else if (eventType.equalsIgnoreCase(CommonUtil.EVENT_TYPE_TEAM) || eventType.equalsIgnoreCase(CommonUtil.EVENT_TYPE_VIRTUAL_TEAM)) {
				if (m_viewList.get(position - 1).get(CommonUtil.USER_TEAM_MEMBER_YN).equalsIgnoreCase(CommonUtil.USER_TEAM_MEMBER_YN_NO)) {
					Toast.makeText(m_context, R.string.generalevent_list_event_not_a_member_message,	Toast.LENGTH_SHORT).show();
				} else if (!m_viewList.get(position - 1).get(CommonUtil.USER_CAN_JOIN).equalsIgnoreCase(CommonUtil.USER_CAN_JOIN_YES)) {
					Toast.makeText(m_context, R.string.generalevent_list_event_taken_message,	Toast.LENGTH_SHORT).show();
				} else if (m_viewList.get(position - 1).get(CommonUtil.USER_TEAM_MEMBER_YN).equalsIgnoreCase(CommonUtil.USER_TEAM_MEMBER_YN_YES) && m_viewList.get(position - 1).get(CommonUtil.USER_CAN_JOIN).equalsIgnoreCase(CommonUtil.USER_CAN_JOIN_YES) ) {
					if (eventType.equalsIgnoreCase(CommonUtil.EVENT_TYPE_TEAM)) {
						intent = new Intent(GeneralEvent.this, GeneralEventDetail.class);
					}
					else if (eventType.equalsIgnoreCase(CommonUtil.EVENT_TYPE_VIRTUAL_TEAM)) {
						intent = new Intent(GeneralEvent.this, VirtualEventDetail.class);
					}
					
					intent.putExtras(generalEventBundle);
					startActivity(intent);
					finish();
				}
			}
		} else if (m_viewList.get(position - 1).get(CommonUtil.EVENT_STATUS).equalsIgnoreCase(CommonUtil.EVENT_STATUS_FINISHED)) {
			if (eventType.equalsIgnoreCase(CommonUtil.EVENT_TYPE_GENERAL)) {
				intent =  new Intent(GeneralEvent.this, RankingEventBasedIndividual.class);
			} else {
				intent =  new Intent(GeneralEvent.this, RankingEventBasedTeam.class);
			}
			
			intent.putExtras(generalEventBundle);

			startActivity(intent);
			finish();
		} else {
			Toast.makeText(m_context, R.string.generalevent_list_upcoming_message, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * This method perform action when an item is touched in GeneralEvent List.
	 * 
	 * @param view
	 * @param event
	 * 
	 * @return boolean
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		/*
		switch (event.getAction()) {
			case (MotionEvent.ACTION_DOWN):
				x1 = event.getX();
				y1 = event.getY();
				
				break;
			case (MotionEvent.ACTION_UP):
				x2 = event.getX();
				y2 = event.getY();
				float dx = x2 - x1;
				float dy = y2 - y1;
	
				direction = "";
				if (Math.abs(dx) > Math.abs(dy)) {
					if (dx > 0) {
						direction = "right";
					} else if (dx < 0) {
						direction = "left";
					}
				} else {
					if (dy > 0) {
						direction = "down";
					} else if (dy < 0) {
						direction = "up";
					}
				}
				if (!justOnce) {
					if (direction.equals("down") && isHeader && Math.abs(dy) > 2) {
						justOnce = true;
						llHeader.setVisibility(View.VISIBLE);
						//downloadGeneralEventData(DOWNLOAD_GENERAL_EVENT_LIST_REFRESH);
					}
				}
				
				break;
		}
		*/
		return false;
	}
	
	/**
	 * This method perform action when GeneralEvent List is scrolled.
	 * 
	 * @param view
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 * @paramtotalItemCount
	 * 
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		/*
		if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
			if (!justOnce && m_viewList != null && direction.equals("up")) {
				justOnce = true;
				direction = "";
				llFooter.setVisibility(View.VISIBLE);
				//downloadGeneralEventData(DOWNLOAD_GENERAL_EVENT_LIST_MORE);
			}
		} else if (firstVisibleItem == 0) {
			isHeader = true;
		} else {
			isHeader = false;
		}
		*/
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}
	
	/**
	 * This method refresh the GeneralEvents List.
	 * 
	 * @param flag
	 */
	private void refreshList(int flag) {
		if (flag == DOWNLOAD_GENERAL_EVENT_LIST_REFRESH) {
			m_viewList = new ArrayList<HashMap<String, String>>();
		}
		
		if (m_viewList == null) {
			m_viewList = m_GeneralEventList;
		} else {
			m_viewList.addAll(m_GeneralEventList);
		}

		llHeader.setVisibility(View.GONE);
		llFooter.setVisibility(View.GONE);

		m_generalEventAdapter.setGeneralEventList(m_viewList);
		m_generalEventAdapter.notifyDataSetChanged();

		justOnce = false;
	}
	

}
