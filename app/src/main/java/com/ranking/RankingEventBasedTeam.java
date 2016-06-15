package com.ranking;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
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
import com.event.GeneralEvent;
import com.skcc.portal.skmsquiz.R;

public class RankingEventBasedTeam extends Activity implements OnScrollListener,
		OnTouchListener, OnItemClickListener {

	private static final String LIST_PAGING_EVENT_RANKING_BASED_TEAM_URL = "getListPagingEventRankingBasedTeam.do";
	private static final String LIST_PAGING_EVENT_RANKING_BASED_TEAM_DESCRIPTION_URL = "getListPagingEventRankingBasedTeamDescription.do";

	private static final int DOWNLOAD_RANKING_RFRESH = 1;
	private static final int DOWNLOAD_RANKING_MORE = 2;

	/* members to store userid and password for auto login */
	public static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREF_USERID = "userid";
	private static final String PREF_USERDEPT = "userdept";
	
	private Context mContext = this;
	private ArrayList<HashMap<String, String>> mViewList;
	private ArrayList<HashMap<String, String>> mEventRankingBasedTeamList;
	private ArrayList<HashMap<String, String>> mEventRankingBeasedTeamDescription;
	private static final String LOG_TAG = RankingEventBasedTeam.class.getSimpleName();

	private ListView lvRankingEventBasedTeamList;
	private TextView mRankingEventBasedOnTeamDetail;
	private TextView mRankingEventBasedOnEventName;

	private RankingEventBasedTeamAdapter mRankingEventBasedTeamAdapter;

	protected int mTotalListNum;
	protected int mItemNum;

	private boolean justOnce = false;
	private boolean isHeader = false;
//	private boolean isFooter = false;

	private View vHeader;
	private View vFooter;
	private LinearLayout llHeader;
	private LinearLayout llFooter;

	private float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
	private String direction = "";
	private ImageView btnHome;
	private String mUserId;
	private String mUserDept;
	private String mCompCd;
	private String mEventId;
	private String mEventName;
	private String mEventType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ranking_list_event_based_team);
		
		Bundle extras = getIntent().getExtras();
		mUserId = getIntent().getStringExtra(CommonUtil.USER_ID);
		mUserDept = getIntent().getStringExtra(CommonUtil.USER_DEPT);
		mCompCd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		mEventId = extras.getString(CommonUtil.EVENT_ID);
		mEventName = extras.getString(CommonUtil.EVENT_NAME);
		mEventType = extras.getString(CommonUtil.EVENT_TYPE_NAME);

		btnHome = (ImageView) findViewById(R.id.btn_home);
		btnHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		TextView tvTitle = (TextView) findViewById(R.id.header_title_id);
		tvTitle.setText(getString(R.string.ranking_event_based_team));

		
		// 리스트뷰에 어댑터 연결
		lvRankingEventBasedTeamList = (ListView) findViewById(R.id.ranking_event_based_team_listiview);

		vHeader = getLayoutInflater().inflate(R.layout.ranking_listview_header,
				null, false);
		vFooter = getLayoutInflater().inflate(R.layout.ranking_listview_footer,
				null, false);

		llHeader = (LinearLayout) vHeader
				.findViewById(R.id.ranking_listview_header_entry_linearlayout);
		llFooter = (LinearLayout) vFooter
				.findViewById(R.id.ranking_listview_footer_entry_linearlayout);

		lvRankingEventBasedTeamList.addHeaderView(vHeader);
		lvRankingEventBasedTeamList.addFooterView(vFooter);
		lvRankingEventBasedTeamList.setOnItemClickListener(this);
		lvRankingEventBasedTeamList.setOnScrollListener(this);
		lvRankingEventBasedTeamList.setOnTouchListener(this);

		mRankingEventBasedTeamAdapter = new RankingEventBasedTeamAdapter(mViewList, mContext);
		lvRankingEventBasedTeamList.setAdapter(mRankingEventBasedTeamAdapter);

		
		mRankingEventBasedOnTeamDetail = (TextView)findViewById(R.id.ranking_event_based_team_event_detail);
		mRankingEventBasedOnEventName= (TextView)findViewById(R.id.ranking_event_based_team_event_name);

		mRankingEventBasedOnEventName.setText(mEventName);
		mRankingEventBasedOnTeamDetail.setText("참여이력이 없습니다.");
		
		downListPagingEventRankingBasedTeam(mEventId, DOWNLOAD_RANKING_RFRESH);
		downEventRankingBasedTeamDescription(mUserId, mEventId);

		justOnce = false;
		isHeader = false;
//		isFooter = false;

		llHeader.setVisibility(View.GONE);
		llFooter.setVisibility(View.GONE);

	}

	@Override
	protected void onResume() {
		Log.i(LOG_TAG, "onResume");

		super.onResume();
	}

	private void downListPagingEventRankingBasedTeam(String mEventId, final int flag) {
		String url = getString(R.string.base_uri) + LIST_PAGING_EVENT_RANKING_BASED_TEAM_URL;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.PAGE_SIZE, getString(R.string.ranking_page_size));
		parameters.put(CommonUtil.EVENT_ID, mEventId);
		

		if (flag == DOWNLOAD_RANKING_MORE) {
			int lastPosition = mEventRankingBasedTeamList.size() - 1;
			parameters.put("last_dept_name", mEventRankingBasedTeamList.get(lastPosition)
					.get(CommonUtil.DEPT_NAME));
			parameters.put("last_ranking", mEventRankingBasedTeamList.get(lastPosition)
					.get(CommonUtil.RANKING));
		}
		else {
			parameters.put(CommonUtil.LAST_DEPT_NAME, "");
			parameters.put(CommonUtil.LAST_RANKING, "0");
		}
		
		
		Context context = mContext;
		boolean progressVisible = false;
		// if (flag == DOWNLOAD_RANKING_MORE)
		// progressVisible = true;
		int id = 222;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0) {
					mEventRankingBasedTeamList = list;
					refreshList(flag);
				} else {
					Toast.makeText(mContext,
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

	private void downEventRankingBasedTeamDescription(String mUserId, String mEventId) {
		String url = getString(R.string.base_uri) + LIST_PAGING_EVENT_RANKING_BASED_TEAM_DESCRIPTION_URL;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_ID, mUserId);
		parameters.put(CommonUtil.EVENT_ID, mEventId);

		Context context = mContext;
		boolean progressVisible = true;
		int id = 111;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				mEventRankingBeasedTeamDescription = list;
				
				if (list.size() <= 0) {
						Log.e(LOG_TAG, "downloadEventList : list.size() <= 0 ");
						return true;
				} else
				{
					if (list.size() > 0 && list.get(0).containsKey("error")) {
						Log.e(LOG_TAG, "downloadEventList" + list.get(0).get("error"));
						return true;
					}
					else {
						String strEventName = mEventRankingBeasedTeamDescription.get(0).get(CommonUtil.EVENT_NAME);
						String strDetail = "<font color=\"red\"><b>"
								+ mEventRankingBeasedTeamDescription.get(0).get(CommonUtil.USER_TEAM_NAME)
								+ "</b></font>" + " 에서 " + "<font color=\"red\"><b>"
								+ mEventRankingBeasedTeamDescription.get(0).get(CommonUtil.USER_NAME)
								+ "</b></font>" + " 님은 " + "<font color=\"red\"><b>"
								+ mEventRankingBeasedTeamDescription.get(0).get(CommonUtil.RANKING)
								+ "</b></font>" + "위 입니다."; 
						
						Log.d(LOG_TAG, "EventName : "+ strEventName);
						Log.d(LOG_TAG, "Detail : " + strDetail);
						
						mRankingEventBasedOnEventName.setText(strEventName);
						mRankingEventBasedOnTeamDetail.setText(Html.fromHtml(strDetail));
						
					}
				}
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible);
		task.execute();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if ((firstVisibleItem + visibleItemCount) == totalItemCount)// footer
		{
			Log.d(LOG_TAG,
					"Last position!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			isFooter = true;
			if (!justOnce && mViewList != null && direction.equals("up")) {
				justOnce = true;
				direction = "";
				llFooter.setVisibility(View.VISIBLE);
				downListPagingEventRankingBasedTeam(mEventId, DOWNLOAD_RANKING_MORE);
			}

		} else if (firstVisibleItem == 0) // header
		{
			Log.d(LOG_TAG,
					"First position!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			isHeader = true;
//			isFooter = false;
		} else {
			isHeader = false;
//			isFooter = false;
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
					downListPagingEventRankingBasedTeam(mEventId, DOWNLOAD_RANKING_RFRESH);
				}
				// else if (direction.equals("up") && isFooter) {
				// justOnce = true;
				// llFooter.setVisibility(View.VISIBLE);
				// downloadRankingData(DOWNLOAD_RANKING_MORE);
				// }
			}

			Log.d(LOG_TAG, "ACTION_UP : " + direction);
			break;
		}

		Log.d(LOG_TAG, direction);

		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Intent intent = null;
		Bundle generalEventBundle = new Bundle();
		
		generalEventBundle.putString(CommonUtil.USER_ID, mUserId);
		generalEventBundle.putString(CommonUtil.USER_DEPT, mUserDept);
		generalEventBundle.putString(CommonUtil.COMP_CD, mCompCd);
		generalEventBundle.putString(CommonUtil.EVENT_ID, mEventId);
		generalEventBundle.putString(CommonUtil.EVENT_NAME, mEventName);
		generalEventBundle.putString(CommonUtil.EVENT_TYPE_NAME, mEventType);
		generalEventBundle.putString(CommonUtil.EVENT_TEAM_NAME, mViewList.get(position - 1).get("USER_TEAM_NAME"));
		
		intent =  new Intent(RankingEventBasedTeam.this, RankingEventBasedIndividual.class);
		intent.putExtras(generalEventBundle);

		startActivity(intent);
//		if (mViewList.get(position - 1).get("OPEN_YN").equals("Y")) {
//			Intent intent = new Intent(RankingEventBasedTeam.this, RankingRealDetail.class);
//			Bundle b = new Bundle();
//
//			// Your id
//			b.putString("user_id", mViewList.get(position - 1).get("USER_ID"));
//
//			// Put your id to your next Intent
//			intent.putExtras(b);
//			startActivity(intent);
//		} else {
//			Toast.makeText(mContext, R.string.ranking_open_yn_click_no,
//					Toast.LENGTH_SHORT).show();
//		}
	}

	private void refreshList(int flag) {
		Log.d(LOG_TAG, "refreshList()");
		if (flag == DOWNLOAD_RANKING_RFRESH) {
			mViewList = new ArrayList<HashMap<String, String>>();
		}
		if (mViewList == null) {
			mViewList = mEventRankingBasedTeamList;
		} else {
			mViewList.addAll(mEventRankingBasedTeamList);
		}

		llHeader.setVisibility(View.GONE);
		llFooter.setVisibility(View.GONE);
		
		mRankingEventBasedTeamAdapter.setRankingDataList(mViewList);
		mRankingEventBasedTeamAdapter.notifyDataSetChanged();

		justOnce = false;
	}


}
