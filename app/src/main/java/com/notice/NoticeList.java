/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.notice;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.login.UserLogin;
import com.ranking.RankingSelect;
import com.skcc.portal.skmsquiz.R;

/**
 * Demonstrates expandable lists using a custom {@link ExpandableListAdapter}
 * from {@link BaseExpandableListAdapter}.
 */
public class NoticeList extends Activity implements OnScrollListener, OnTouchListener {

	private static final String DOWNLOAD_NOTICE_LIST_PATH = "selectNoticeList.do";
	private static final String DOWNLOAD_BOARD_LIST_PATH = "selectBoardList.do";
	private static final String LOG_TAG = NoticeList.class.getSimpleName();
	private static final int DOWNLOAD_FRESH = 1;
	private static final int DOWNLOAD_MORE = 2;
	
	private String m_user_id;
	private String m_comp_cd;
	private String m_user_dept;

	public static final String PREFS_NAME = "MyPrefsFile"; 
	private static final String PREF_USERID = "userid";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_REMEMBERME = "true";
	
	// private ExpandableListAdapter mAdapter;
	private NoticeExpandableListAdapter mAdapter;
	private Context m_context = this;

	private boolean justOnce = false;
	private boolean isHeader = false;
//	private boolean isFooter = false;
	// private boolean isMove = false;

	private ArrayList<HashMap<String, String>> m_noticeList;
	private ArrayList<HashMap<String, String>> m_boardList;
	private ArrayList<HashMap<String, String>> m_viewList;

	private View vHeader;
	private View vFooter;

	private ExpandableListView elvNoticeList;
	private LinearLayout llHeader;
	private LinearLayout llFooter;
	
	private ImageView m_btn_home;

	private float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
	private String direction = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noticelist);
		
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_user_dept = getIntent().getStringExtra(CommonUtil.USER_DEPT);
		m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		
		m_btn_home = (ImageView)findViewById(R.id.btn_home);
		m_btn_home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		TextView tvTitle = (TextView) findViewById(R.id.header_title_id);
		tvTitle.setText(getString(R.string.notice_list_title));

		elvNoticeList = (ExpandableListView) findViewById(R.id.noticelist_expandable_list);

		vHeader = getLayoutInflater().inflate(R.layout.noticelist_header, null,
				false);
		vFooter = getLayoutInflater().inflate(R.layout.noticelist_footer, null,
				false);

		llHeader = (LinearLayout) vHeader
				.findViewById(R.id.noticelist_header_entry_linearlayout);
		llFooter = (LinearLayout) vFooter
				.findViewById(R.id.noticelist_footer_entry_linearlayout);

		elvNoticeList.addHeaderView(vHeader);
		elvNoticeList.addFooterView(vFooter);

		mAdapter = new NoticeExpandableListAdapter(m_context, m_viewList);

		elvNoticeList.setAdapter(mAdapter);

		// Set up our adapter
		elvNoticeList.setOnScrollListener(this);
    	elvNoticeList.setOnTouchListener(this);

	}
	
	protected void signOutUser() {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().remove(PREF_USERID).remove(PREF_PASSWORD).remove(PREF_REMEMBERME).commit();
		Intent intent = new Intent(NoticeList.this, UserLogin.class);	// create intent to invoke user login activity.
		startActivity(intent);	// start the intent to open user login screen.
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.noticelist, menu);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		
	    switch (item.getItemId()) {
	    	case R.id.home:
	    		intent = new Intent(NoticeList.this, com.main.QuizMain.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();
	            return true;
	        case R.id.profile:
	        	intent = new Intent(NoticeList.this, com.profile.ProfileMain.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();
	            return true;
	        case R.id.ranking:
	        	intent = new Intent(NoticeList.this, com.ranking.RankingSelect.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();

	            return true;
	        case R.id.events:
	        	intent = new Intent(NoticeList.this, com.event.GeneralEvent.class);
	        	
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


	@Override
	protected void onResume() {
		super.onResume();
		downloadNoticeList();
		justOnce = false;
		isHeader = true;
//		isFooter = false;
		// isMove = false;

		llHeader.setVisibility(View.VISIBLE);
		llFooter.setVisibility(View.GONE);

	}

	private void downloadNoticeList() {
		Log.d(LOG_TAG, "downloadUserDetail()");

		String url = getString(R.string.base_uri) + DOWNLOAD_NOTICE_LIST_PATH;
		HashMap<String, String> parameters = null;

		Context context = m_context;
		boolean progressVisible = false;
		int id = 111;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {

				m_boardList = null;
				m_viewList = new ArrayList<HashMap<String, String>>();

				if (list.size() > 0) {
					m_noticeList = list;
					m_viewList.addAll(m_noticeList);
				}

				downloadBoardList(DOWNLOAD_FRESH);
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible);
		task.execute();
	}

	private void downloadBoardList(int flag) {
		Log.d(LOG_TAG, "downloadUserDetail()");

		String url = getString(R.string.base_uri) + DOWNLOAD_BOARD_LIST_PATH;

		HashMap<String, String> parameters = null;
		parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.PAGE_SIZE, getString(R.string.notice_page_size));

		if (flag == DOWNLOAD_MORE) {
			if (m_viewList != null) {
				parameters.put(CommonUtil.LAST_ARTICLE_NO, m_viewList.get(m_viewList.size() - 1).get(CommonUtil.ARTICLE_NO));
			}
		}

		Context context = m_context;
		boolean progressVisible = false;
		int id = 222;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0) {
					m_boardList = list;
					refreshList();
				} else {
					Toast.makeText(m_context,
							R.string.notice_list_empty_message,
							Toast.LENGTH_SHORT).show();
					m_boardList = null;
					refreshList();
				}

//				isFooter = false;
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible);
		task.execute();
	}

	private void refreshList() {
		Log.d(LOG_TAG, "refreshList()");
		if (m_viewList == null) {
			m_viewList = new ArrayList<HashMap<String, String>>();
		}
		if (m_boardList != null)
			m_viewList.addAll(m_boardList);

		llHeader.setVisibility(View.GONE);
		llFooter.setVisibility(View.GONE);

		mAdapter.setNoticeList(m_viewList);
		mAdapter.notifyDataSetChanged();

		justOnce = false;
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
				downloadBoardList(DOWNLOAD_MORE);
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

	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction()) {
		case (MotionEvent.ACTION_DOWN):
			x1 = event.getX();
			// x3 = x1;
			y1 = event.getY();
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
				} else if (dx < 0){
					direction = "left";
				}
			} else {
				if (dy > 0) {
					direction = "down";

				} else if (dy < 0) {
					direction = "up";
				}
			}
			if (!justOnce && Math.abs(dy) > 2) {
				if (direction.equals("down") && isHeader) {
					justOnce = true;					
					llHeader.setVisibility(View.VISIBLE);
					downloadNoticeList();
				}
				// else if (direction.equals("up") && isFooter) {
				// justOnce = true;
				// llFooter.setVisibility(View.VISIBLE);
				// downloadBoardList("more");
				// }
			}

			Log.d(LOG_TAG, "ACTION_UP : " + direction);
			break;
		// case (MotionEvent.ACTION_MOVE):
		// x2 = event.getX();
		// y2 = event.getY();
		// float dy2 = y2 - y3;
		//
		// // x3 = event.getX();
		// y3 = event.getY();
		//
		// // Use dx1 and dy1 to determine the direction
		// if (Math.abs(dy2) > 2) {
		// if (dy2 > 0) {
		// direction = "down";
		// } else {
		// direction = "up";
		// }
		//
		// } else {
		// direction = "stay";
		// }
		// Log.d(LOG_TAG, "ACTION_MOVE : " + direction
		// + " event.getX() : " + event.getX()
		// + " event.getY() : " + event.getY());
		// break;
		}

		Log.d(LOG_TAG, direction);

		return false;
	}
}
