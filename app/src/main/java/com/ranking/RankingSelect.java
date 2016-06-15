package com.ranking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonUtil;
import com.common.async.AbstractAsyncActivity;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.login.UserLogin;
import com.mystudy.MyStudyRoom_Tab_Main;
import com.skcc.portal.skmsquiz.R;

public class RankingSelect extends AbstractAsyncActivity implements OnClickListener, OnItemClickListener, OnScrollListener, OnTouchListener {

	public int Cnt1 = 0;
	public int Cnt2 = 0;

	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	private String m_user_id;
	private String m_comp_cd;
	private String m_user_dept;

	public static final String PREFS_NAME = "MyPrefsFile"; 
	private static final String PREF_USERID = "userid";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_REMEMBERME = "true";
	
	TextView tv_Title;

	Button btn_Real;
	Button btn_Event;
	ImageView btn_Home;
	
	private Context m_context = this;
	
	private static TabHost tabhost;
	private static String curTab = "1";
	
	ListView lv_realRankingList;
	ListView lv_eventRankingList;
	ArrayList<HashMap<String, String>> mRankingEventList;
	private RankingRealAdapter m_realRankingAdapter;
	private RankingEventListAdapter m_eventRankingAdapter;
	
	private static final String DOWNLOAD_RANKING_PATH = "getUserRankPaing.do";
	private static final String DOWNLOAD_USER_RANKING_PATH = "getUserRank.do";
	private static final String RANKING_EVENT_LIST_URL = "getRankingEventList.do";
	
	private static final int DOWNLOAD_RANKING_RFRESH = 1;
	private static final int DOWNLOAD_RANKING_MORE = 2;
	
	private ArrayList<HashMap<String, String>> m_viewList;
	private ArrayList<HashMap<String, String>> m_rankingDataList;
	private ArrayList<HashMap<String, String>> m_rankingUserDataList;
	
	private float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
	private String direction = "";
	private boolean justOnce = false;
	private boolean isHeader = false;
	private View vHeader;
	private View vFooter;
	private LinearLayout llHeader;
	private LinearLayout llFooter;
	
	private TextView m_rankingUserDetail;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranking_select);
		
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_user_dept = getIntent().getStringExtra(CommonUtil.USER_DEPT);
		m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		
		/*
		tv_Title = (TextView) findViewById(R.id.header_title_id);
		tv_Title.setText(R.string.ranking_select_title);

		btn_Real = (Button) findViewById(R.id.btn_ranking_select_real_ranking);
		btn_Event = (Button) findViewById(R.id.btn_ranking_select_event_ranking);
		btn_Home = (ImageView) findViewById(R.id.btn_home);

		btn_Real.setOnClickListener(this);
		btn_Event.setOnClickListener(this);
		btn_Home.setOnClickListener(this);
		*/
		
		btn_Home = (ImageView) findViewById(R.id.btn_home);
		btn_Home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		TabHost tabs = (TabHost)findViewById(R.id.ranking_tabhost);
	    TabHost.TabSpec spec;
	    tabs.setup();

	    spec = tabs.newTabSpec(CommonUtil.RANKING_TAG1);
	    spec.setContent(R.id.ranking_tab1);
	    spec.setIndicator(CommonUtil.REAL_RANKING);
	    tabs.addTab(spec);

	    spec = tabs.newTabSpec(CommonUtil.RANKING_TAG2);
	    spec.setContent(R.id.ranking_tab2);
	    spec.setIndicator(CommonUtil.EVENT_RANKING);
	    tabs.addTab(spec);
	    
	    tabhost = tabs;
	    tabs.setCurrentTab(0);
	    
	    for(int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
	    	tabs.getTabWidget().getChildAt(i).getLayoutParams().height = 64;
	    	tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
	    	       @Override
	    	      public void onTabChanged(String arg0) {
	    	       if (tabhost.getCurrentTabTag().equals(CommonUtil.RANKING_TAG1)) {
		            	curTab = "1";
		            	downloadRankingData(DOWNLOAD_RANKING_RFRESH);
		        		downloadUserRankingData(m_user_id);
		            } else if (tabhost.getCurrentTabTag().equals(CommonUtil.RANKING_TAG2)) {
		            	curTab = "2";
		            	downloadEventList(m_user_id, m_comp_cd);
		            }
	    	      }  
	    	});
	    }
	    m_rankingUserDetail = (TextView) findViewById(R.id.ranking_detail);
	    
	    lv_realRankingList = (ListView)findViewById(R.id.real_ranking_listiview);
		lv_realRankingList.setOnItemClickListener(this);
		lv_eventRankingList = (ListView)findViewById(R.id.event_ranking_listiview);
		lv_eventRankingList.setOnItemClickListener(this);
	    
	    vHeader = getLayoutInflater().inflate(R.layout.ranking_listview_header, null, false);
		vFooter = getLayoutInflater().inflate(R.layout.ranking_listview_footer, null, false);
		llHeader = (LinearLayout) vHeader.findViewById(R.id.ranking_listview_header_entry_linearlayout);
		llFooter = (LinearLayout) vFooter.findViewById(R.id.ranking_listview_footer_entry_linearlayout);
	
		lv_realRankingList.addHeaderView(vHeader);
		lv_realRankingList.addFooterView(vFooter);
		
		tv_Title = (TextView) findViewById(R.id.header_title_id);
		tv_Title.setText(R.string.ranking_list_title);
		
		m_eventRankingAdapter = new RankingEventListAdapter(m_context, mRankingEventList);
		lv_eventRankingList.setAdapter(m_eventRankingAdapter);
		
		m_realRankingAdapter = new RankingRealAdapter(m_viewList, m_context);
		lv_realRankingList.setAdapter(m_realRankingAdapter);

		downloadRankingData(DOWNLOAD_RANKING_RFRESH);
		downloadUserRankingData(m_user_id);

//		new setValuesToRankingAsyncTask().execute();
	}

	/*
	 * 문제 유형별 새 문제 세팅 : setValuesToQuiz 에 대한 AsyncTask 수행 클래스 참고 :
	 * http://tigerwoods.tistory.com/28
	 */
//	private class setValuesToRankingAsyncTask extends
//			AsyncTask<Void, Void, List<HashMap<String, String>>> {
//		@Override
//		protected void onPreExecute() {
//			showLoadingProgressDialog();
//		}
//
//		@Override
//		protected List<HashMap<String, String>> doInBackground(Void... params) {
//			list = XmlParser.getValuesFromXML(getString(R.string.base_uri)
//					+ "rankingSelect.do?comp_cd=" + m_comp_cd);
//			return list;
//		}
//
//		@Override
//		protected void onPostExecute(List<HashMap<String, String>> list) {
//			dismissProgressDialog();
//			setRankingCnt(list);
//		}
//	}

//	public synchronized void setRankingCnt(List<HashMap<String, String>> list) {
//		for (int i = 0; i < list.size(); i++) {
//			if ((list.get(i)).get("RANKING_TYPE").equals("REAL"))
//				Cnt1 = Integer.valueOf((list.get(i)).get("RANKING_COUNT"));
//			else if ((list.get(i)).get("RANKING_TYPE").equals("EVENT"))
//				Cnt2 = Integer.valueOf((list.get(i)).get("RANKING_COUNT"));
//		}
//		((Button) findViewById(R.id.ranking_selectbtn01)).setText("("
//				+ Integer.toString(Cnt1) + ")");
//		((Button) findViewById(R.id.ranking_selectbtn02)).setText("("
//				+ Integer.toString(Cnt2) + ")");
//	}

	/*
	 * Button OnClick시 동작 처리.
	 */
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_ranking_select_real_ranking:
			intent = new Intent(RankingSelect.this, RankingReal.class);
			intent.putExtra(CommonUtil.CHK, 0);
			intent.putExtra(CommonUtil.USER_ID, m_user_id);
			intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
			intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
			startActivity(intent);
			//finish();
			break;
		case R.id.btn_ranking_select_event_ranking:
			intent = new Intent(RankingSelect.this, RankingEventList.class);
			intent.putExtra(CommonUtil.USER_ID, m_user_id);
			intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
			intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
			// intent.putExtra("CHK", 1);
			startActivity(intent);
			//finish();
			break;
		case R.id.btn_home:
			finish();
			break;
		}
	}
	
	private void downloadRankingData(final int flag) {
		String url = getString(R.string.base_uri) + DOWNLOAD_RANKING_PATH;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.PAGE_SIZE, getString(R.string.ranking_page_size));

		if (flag == DOWNLOAD_RANKING_MORE) {
			int lastPosition = m_rankingDataList.size() - 1;
			parameters.put(CommonUtil.LAST_USER_ID, m_rankingDataList.get(lastPosition)
					.get(CommonUtil.USER_ID));
			parameters.put(CommonUtil.LAST_RANKING, m_rankingDataList.get(lastPosition)
					.get(CommonUtil.RANKING));
		}
		Context context = m_context;
		boolean progressVisible = false;
		// if (flag == DOWNLOAD_RANKING_MORE)
		// progressVisible = true;
		int id = 222;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0) {
					m_rankingDataList = list;
					refreshList(flag);
				} else {
					Toast.makeText(m_context,
							R.string.ranking_list_empty_message,
							Toast.LENGTH_SHORT).show();
					llFooter.setVisibility(View.GONE);
					justOnce = false;
				}
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible);
		task.execute();
	}

	private void downloadUserRankingData(String mUserId) {
		String url = getString(R.string.base_uri) + DOWNLOAD_USER_RANKING_PATH;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_ID, mUserId);
		
		

		Context context = m_context;
		boolean progressVisible = true;
		int id = 111;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				m_rankingUserDataList = list;
				if (m_rankingUserDataList.size() > 0) {
					String strUserRanking = "";

					strUserRanking = " <b>"
							+ m_rankingUserDataList.get(0).get(CommonUtil.USER_NM)
							+ "</b>" + " 님은 " + "<font color=\"red\"><b>"
							+ m_rankingUserDataList.get(0).get(CommonUtil.RANKING)
							+ "</b>" + "위</font> 입니다.";

					m_rankingUserDetail.setText(Html.fromHtml(strUserRanking));
				} else {
					m_rankingUserDetail
							.setText(R.string.ranking_user_detail_empty_message);
				}
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible);
		task.execute();
	}
	
	private void refreshList(int flag) {
		if (flag == DOWNLOAD_RANKING_RFRESH) {
			m_viewList = new ArrayList<HashMap<String, String>>();
		}
		if (m_viewList == null) {
			m_viewList = m_rankingDataList;
		} else {
			m_viewList.addAll(m_rankingDataList);
		}

		llHeader.setVisibility(View.GONE);
		llFooter.setVisibility(View.GONE);

		m_realRankingAdapter.setRankingDataList(m_viewList);
		m_realRankingAdapter.notifyDataSetChanged();

		justOnce = false;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if ((firstVisibleItem + visibleItemCount) == totalItemCount)// footer
		{
			if (!justOnce && m_viewList != null && direction.equals("up")) {
				justOnce = true;
				direction = "";
				llFooter.setVisibility(View.VISIBLE);
				downloadRankingData(DOWNLOAD_RANKING_MORE);
			}

		} else if (firstVisibleItem == 0) // header
		{
			isHeader = true;
		} else {
			isHeader = false;
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction()) {
		case (MotionEvent.ACTION_DOWN):
			x1 = event.getX();
			// x3 = x1;
			y1 = event.getY();
			// y3 = y1;
			break;
		case (MotionEvent.ACTION_UP):
			x2 = event.getX();
			y2 = event.getY();
			float dx = x2 - x1;
			float dy = y2 - y1;

			direction = "";
			// Use dx and dy to determine the direction
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
					downloadRankingData(DOWNLOAD_RANKING_RFRESH);
				}
			}

			break;
		}

		return false;
	}
	
	private void downloadEventList(String m_user_id, String m_comp_cd) {
		String url = getString(R.string.base_uri)+ RANKING_EVENT_LIST_URL;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_ID, m_user_id);
		parameters.put(CommonUtil.COMP_CD, m_comp_cd);
		
		boolean progressVisible = true;
		int id = 111;
		
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0) {
					if (list.get(0).containsKey(CommonUtil.ERROR)) {
						return true;
					}
				} else 
					return true;
				
				mRankingEventList = list;
				
				m_eventRankingAdapter.setRankingDataList(mRankingEventList);
				m_eventRankingAdapter.notifyDataSetChanged();
				
				return true;
			}
			
		};
		
		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
		parameters, id, m_context , callable, progressVisible);
		task.execute();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (curTab.equalsIgnoreCase("1")) {
			if (m_viewList.get(position - 1).get(CommonUtil.OPEN_YN).equalsIgnoreCase(CommonUtil.FLAG_Y)) {
				Intent intent = new Intent(RankingSelect.this, RankingRealDetail.class);
				Bundle b = new Bundle();
				b.putString(CommonUtil.USER_ID, m_viewList.get(position - 1).get(CommonUtil.USER_ID));
				intent.putExtras(b);
				
				startActivity(intent);
			} else {
				Toast.makeText(m_context, R.string.ranking_open_yn_click_no,
						Toast.LENGTH_SHORT).show();
			}
		} else if (curTab.equalsIgnoreCase("2")) {
			Intent intent;
			Bundle b = new Bundle();
			b.putString(CommonUtil.USER_ID, m_user_id);
			b.putString(CommonUtil.USER_DEPT, m_user_dept);
			b.putString(CommonUtil.COMP_CD, m_comp_cd);
			b.putString(CommonUtil.EVENT_ID, mRankingEventList.get(position).get(CommonUtil.EVENT_ID));
			b.putString(CommonUtil.EVENT_NAME, mRankingEventList.get(position).get(CommonUtil.EVENT_NAME));
			b.putString(CommonUtil.EVENT_TYPE_NAME, mRankingEventList.get(position).get(CommonUtil.EVENT_TYPE_NAME));
						
			if (mRankingEventList.get(position).get(CommonUtil.EVENT_TYPE_NAME).equalsIgnoreCase(CommonUtil.EVENT_TYPE_GENERAL)) {
				intent =  new Intent(RankingSelect.this, RankingEventBasedIndividual.class);
			} else {
				intent =  new Intent(RankingSelect.this, RankingEventBasedTeam.class);
			}
	
			intent.putExtras(b);
			startActivity(intent);
		}
	}
	
	protected void signOutUser() {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().remove(PREF_USERID).remove(PREF_PASSWORD).remove(PREF_REMEMBERME).commit();
		Intent intent = new Intent(RankingSelect.this, UserLogin.class);	// create intent to invoke user login activity.
		startActivity(intent);	// start the intent to open user login screen.
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.rankingselect, menu);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		
	    switch (item.getItemId()) {
	    	case R.id.home:
	    		intent = new Intent(RankingSelect.this, com.main.QuizMain.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();
	            return true;
	        case R.id.profile:
	        	intent = new Intent(RankingSelect.this, com.profile.ProfileMain.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();
	            return true;
	        case R.id.ranking:
	        	intent = new Intent(RankingSelect.this, com.ranking.RankingSelect.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();

	            return true;
	        case R.id.events:
	        	intent = new Intent(RankingSelect.this, com.event.GeneralEvent.class);
	        	
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

}
