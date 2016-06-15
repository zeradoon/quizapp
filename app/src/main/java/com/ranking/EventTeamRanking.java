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
import com.common.NetworkUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.skcc.portal.skmsquiz.R;

public class EventTeamRanking extends Activity implements OnScrollListener,
		OnTouchListener, OnItemClickListener {

	private static final String DOWNLOAD_RANKING_PATH = "getUserRankPaing.do";
	private static final String DOWNLOAD_USER_RANKING_PATH = "getUserRank.do";

	private static final int DOWNLOAD_RANKING_RFRESH = 1;
	private static final int DOWNLOAD_RANKING_MORE = 2;

	/* members to store userid and password for auto login */
	public static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREF_USERID = "userid";
	private static final String PREF_USERDEPT = "userdept";
	
	private Context m_context = this;
	private ArrayList<HashMap<String, String>> m_viewList;
	private ArrayList<HashMap<String, String>> m_rankingDataList;
	private ArrayList<HashMap<String, String>> m_rankingUserDataList;
	private static final String LOG_TAG = EventTeamRanking.class.getSimpleName();

	private ListView m_listView;
	private TextView m_rankingUserDetail;

	private EventTeamRankingAdapter m_rankingAdapter;

	protected int totalListNum;
	protected int itemNum;

	private boolean justOnce = false;
	private boolean isHeader = false;
//	private boolean isFooter = false;

	private View vHeader;
	private View vFooter;
	private LinearLayout llHeader;
	private LinearLayout llFooter;

	private float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
	private String direction = "";
	private ImageView m_btn_home;
	private String mUserId;
	private String mUserDept;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ranking);

		m_btn_home = (ImageView) findViewById(R.id.btn_home);
		m_btn_home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		TextView tvTitle = (TextView) findViewById(R.id.header_title_id);
		tvTitle.setText(getString(R.string.ranking_title));

		// 리스트뷰에 어댑터 연결
		m_listView = (ListView) findViewById(R.id.ranking_listiview);

		vHeader = getLayoutInflater().inflate(R.layout.ranking_listview_header,
				null, false);
		vFooter = getLayoutInflater().inflate(R.layout.ranking_listview_footer,
				null, false);

		llHeader = (LinearLayout) vHeader
				.findViewById(R.id.ranking_listview_header_entry_linearlayout);
		llFooter = (LinearLayout) vFooter
				.findViewById(R.id.ranking_listview_footer_entry_linearlayout);

		m_listView.addHeaderView(vHeader);
		m_listView.addFooterView(vFooter);
		m_listView.setOnItemClickListener(this);
		m_listView.setOnScrollListener(this);
		m_listView.setOnTouchListener(this);

		m_rankingAdapter = new EventTeamRankingAdapter(m_viewList, m_context);
		m_listView.setAdapter(m_rankingAdapter);

		m_rankingUserDetail = (TextView) findViewById(R.id.ranking_user_detail);

		loadUserInfo();
		
		downloadRankingData(DOWNLOAD_RANKING_RFRESH);

		downloadUserRankingData(mUserId);

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

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if ((firstVisibleItem + visibleItemCount) == totalItemCount)// footer
		{
			Log.d(LOG_TAG,
					"Last position!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			isFooter = true;
			if (!justOnce && m_viewList != null && direction.equals("up")) {
				justOnce = true;
				direction = "";
				llFooter.setVisibility(View.VISIBLE);
				downloadRankingData(DOWNLOAD_RANKING_MORE);
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
					downloadRankingData(DOWNLOAD_RANKING_RFRESH);
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
		if (m_viewList.get(position - 1).get(CommonUtil.OPEN_YN).equalsIgnoreCase(CommonUtil.FLAG_Y)) {
			Intent intent = new Intent(EventTeamRanking.this, RankingRealDetail.class);
			Bundle b = new Bundle();

			// Your id
			b.putString(CommonUtil.USER_ID, m_viewList.get(position - 1).get(CommonUtil.USER_ID));

			// Put your id to your next Intent
			intent.putExtras(b);
			startActivity(intent);
		} else {
			Toast.makeText(m_context, R.string.ranking_open_yn_click_no,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void refreshList(int flag) {
		Log.d(LOG_TAG, "refreshList()");
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

		m_rankingAdapter.setRankingDataList(m_viewList);
		m_rankingAdapter.notifyDataSetChanged();

		justOnce = false;
	}
	
	private void loadUserInfo() {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

		mUserId = prefs.getString(PREF_USERID, "");
		mUserDept = prefs.getString(PREF_USERDEPT, "");
	}
}
