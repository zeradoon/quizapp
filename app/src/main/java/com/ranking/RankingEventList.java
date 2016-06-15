package com.ranking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonUtil;
import com.common.async.AbstractAsyncActivity;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.skcc.portal.skmsquiz.R;

public class RankingEventList extends AbstractAsyncActivity implements OnItemClickListener {

	private static final String RANKING_EVENT_LIST_URL = "getRankingEventList.do";
	private static final String LOG_TAG = RankingEventList.class.getSimpleName();
	
	public static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREF_USERID = "userid";
	private static final String PREF_USERDEPT = "userdept";
	
	public int Cnt1 = 0;
	public int Cnt2 = 0;

	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	TextView tv_Title;

	ListView lv_RankingEventList;
	
	ArrayList<HashMap<String, String>> mRankingEventList;
	private Context m_context = this;
	private ImageView m_btn_home;
	private RankingEventListAdapter mAdapter;
	private String m_user_id;
	private String m_user_dept;
	private String m_comp_cd;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranking);
		
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
		
		lv_RankingEventList = (ListView)findViewById(R.id.ranking_listiview);
		lv_RankingEventList.setOnItemClickListener(this);
		tv_Title = (TextView) findViewById(R.id.header_title_id);
		tv_Title.setText(R.string.ranking_event_list_title);
		
		mAdapter = new RankingEventListAdapter(m_context, mRankingEventList);
		lv_RankingEventList.setAdapter(mAdapter);

		downloadEventList(m_user_id, m_comp_cd);
	}

	private void downloadEventList(String m_user_id, String m_comp_cd) {
		Log.d(LOG_TAG, "downloadEventList()");
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
						Log.e(LOG_TAG, "downloadEventList" + list.get(0).get(CommonUtil.ERROR));
						return true;
					}
				} else 
					return true;
				
				mRankingEventList = list;
				
				mAdapter.setRankingDataList(mRankingEventList);
				mAdapter.notifyDataSetChanged();
				
				return true;
			}
			
		};
		
		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
		parameters, id, m_context , callable, progressVisible);
		task.execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent;
		Bundle b = new Bundle();
		b.putString(CommonUtil.USER_ID, m_user_id);
		b.putString(CommonUtil.USER_DEPT, m_user_dept);
		b.putString(CommonUtil.COMP_CD, m_comp_cd);
		b.putString(CommonUtil.EVENT_ID, mRankingEventList.get(position).get(CommonUtil.EVENT_ID));
		b.putString(CommonUtil.EVENT_NAME, mRankingEventList.get(position).get(CommonUtil.EVENT_NAME));
		b.putString(CommonUtil.EVENT_TYPE_NAME, mRankingEventList.get(position).get(CommonUtil.EVENT_TYPE_NAME));
					
		if (mRankingEventList.get(position).get(CommonUtil.EVENT_TYPE_NAME).equalsIgnoreCase(CommonUtil.EVENT_TYPE_GENERAL)) {
			intent =  new Intent(RankingEventList.this, RankingEventBasedIndividual.class);
		} else {
			intent =  new Intent(RankingEventList.this, RankingEventBasedTeam.class);
		}

		intent.putExtras(b);
		startActivity(intent);
	}
	
}
