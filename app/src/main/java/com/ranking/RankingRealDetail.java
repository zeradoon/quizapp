package com.ranking;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.skcc.portal.skmsquiz.R;

public class RankingRealDetail extends Activity {

	private static final String LOG_TAG = RankingRealDetail.class.getSimpleName();
	private static final String DOWNLOAD_USER_DETAIL_PATH = "getProfileUserDetail.do";
	private static final String DOWNLOAD_USER_REAL_RANKING_PATH = "getProfileUserRank.do";
	private static final String DOWNLOAD_USER_IMAGE_PATH = "downloadUserImage.do";
	
	private static final String DOWNLOAD_GENERAL_USER_RANKING_PATH = "getListPagingEventRankingBasedIndividualDescription.do";
	private static final String DOWNLOAD_TEAM_USER_RANKING_PATH = "getListPagingEventRankingBasedTeamIndividualDescription.do";

	private ImageView m_ranking_detail_user_img;
	private ImageView m_ranking_detail_user_rank_img;
	private ImageView m_ranking_title_rank_info_img;

	private TextView m_ranking_detail_user_nickname;
	private TextView m_ranking_detail_user_company;
	private TextView m_ranking_detail_open_yn;
	private TextView m_ranking_detail_user_rank_name;
	private TextView m_ranking_detail_user_score;
	private TextView m_ranking_detail_user_participate_cnt;
	private TextView m_ranking_detail_user_rank_percent;
	private TextView m_ranking_detail_user_rank_detail;

//	private ArrayList<HashMap<String, String>> m_companyList;

	private String m_companyCode = "";
	private String m_openYN = "";
	private String m_userNickName = "";
	private String m_userID = "";
	private String m_eventID = "";
	private String m_eventType = "";
	private String m_eventTeamName;
	private String m_companyName = "";
	
	private Context m_context = this;
	private final ImageDownloader imageDownloader = new ImageDownloader();
	protected String m_departmentName;
	protected TextView m_ranking_detail_user_department;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreate");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.ranking_detail);
		
		TextView tvTitle = (TextView) findViewById(R.id.header_title_id);
		tvTitle.setText(getString(R.string.ranking_detail_title));
		
		m_userID = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_eventID = getIntent().getStringExtra(CommonUtil.EVENT_ID);
		m_eventType = getIntent().getStringExtra(CommonUtil.EVENT_TYPE_NAME);
		m_eventTeamName = getIntent().getStringExtra(CommonUtil.EVENT_TEAM_NAME);
		
		m_ranking_detail_user_img = (ImageView) findViewById(R.id.ranking_detail_user_img);
		m_ranking_detail_user_nickname = (TextView) findViewById(R.id.ranking_detail_user_nickname);
		m_ranking_detail_user_company = (TextView) findViewById(R.id.ranking_detail_user_company);
		m_ranking_detail_open_yn = (TextView) findViewById(R.id.ranking_detail_open_yn);

		m_ranking_detail_user_rank_img = (ImageView) findViewById(R.id.ranking_detail_user_rank_img);
		m_ranking_detail_user_rank_name = (TextView) findViewById(R.id.ranking_detail_user_rank_name);
		m_ranking_detail_user_score = (TextView) findViewById(R.id.ranking_detail_user_score);
		m_ranking_detail_user_participate_cnt = (TextView) findViewById(R.id.ranking_detail_user_participate_cnt);
		m_ranking_detail_user_rank_percent = (TextView) findViewById(R.id.ranking_detail_user_rank_percent);
		m_ranking_detail_user_rank_detail = (TextView) findViewById(R.id.ranking_detail_user_rank_detail);
	
		m_ranking_detail_user_department = (TextView) findViewById(R.id.ranking_detail_user_department);
		
		m_ranking_title_rank_info_img = (ImageView) findViewById(R.id.img_title_rank_info);
		
		if (m_eventID != null && m_eventType.equalsIgnoreCase(CommonUtil.EVENT_TYPE_GENERAL)) {
			m_ranking_title_rank_info_img.setImageResource(R.drawable.title_generalrank_info);
		} else if (m_eventID != null && (m_eventType.equalsIgnoreCase(CommonUtil.EVENT_TYPE_TEAM) || m_eventType.equalsIgnoreCase(CommonUtil.EVENT_TYPE_VIRTUAL_TEAM))) {
			m_ranking_title_rank_info_img.setImageResource(R.drawable.title_teamrank_info);
		} 
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		downloadUserDetail(m_userID);
		downloadRanking(m_userID, m_eventID, m_eventType, m_eventTeamName);
	}

	private void downloadUserDetail(String m_userID) {
		String url = getString(R.string.base_uri)
				+ DOWNLOAD_USER_DETAIL_PATH;
		HashMap<String, String> parameters = new HashMap<String, String>();	
		parameters.put(CommonUtil.USER_ID, m_userID);
		
		Context context = m_context;
		boolean progressVisible = true;
		int id = 111;
		BetterAsyncTaskCallable callable  = new BetterAsyncTaskCallable() {
			
			@Override
			public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list,
					int id) {
				if (list.size() > 0) {
					m_userNickName = list.get(0).get(CommonUtil.USER_NM);
					m_companyCode = list.get(0).get(CommonUtil.USER_COMP_CD);
					m_companyName = list.get(0).get(CommonUtil.COMP_NM);
					m_openYN = list.get(0).get(CommonUtil.OPEN_YN);
					m_departmentName = list.get(0).get(CommonUtil.USER_DEPT);
					
					m_ranking_detail_user_nickname.setText(m_userNickName);
					if (m_companyCode.equals("")) {
						m_ranking_detail_user_company
								.setText(getString(R.string.profile_main_user_default_text));
					} else {
						m_ranking_detail_user_company.setText(m_companyName);
					}
					if (m_openYN.equalsIgnoreCase(CommonUtil.FLAG_Y)) {
						m_ranking_detail_open_yn
								.setText(getString(R.string.profile_main_open_yn_yes));
					} else {
						m_ranking_detail_open_yn
								.setText(getString(R.string.profile_main_open_yn_no));
					}
					if (m_departmentName.equals("")) {
						m_ranking_detail_user_department.setText(R.string.profile_main_user_default_text);
					} else {
						m_ranking_detail_user_department.setText(m_departmentName);
					}
					if (list.get(0).get(CommonUtil.USER_IMG).lastIndexOf(".png") > 1) {
						String imgUrl = getString(R.string.base_uri)
								+ DOWNLOAD_USER_IMAGE_PATH + "?" + CommonUtil.F_DOWNLOAD + "="
								+ list.get(0).get(CommonUtil.USER_IMG);

						imageDownloader.download(imgUrl, m_ranking_detail_user_img);
					}

				} else {
					Toast.makeText(m_context,
							R.string.profile_main_user_info_empty_message,
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		};
		
		HttpPostParameterAsyncTask task = new  HttpPostParameterAsyncTask(url, parameters, id, context, callable, progressVisible);
		task.execute();
	}
	
	private void downloadRanking(String m_UserId, String m_eventID, String m_eventType, String m_eventTeamName) {
		String rankingPath = "";
		
		if (m_eventID == null) {
			rankingPath = DOWNLOAD_USER_REAL_RANKING_PATH;
		} else if (m_eventType.equalsIgnoreCase(CommonUtil.EVENT_TYPE_GENERAL)) {
			rankingPath = DOWNLOAD_GENERAL_USER_RANKING_PATH;
		} else if (m_eventType.equalsIgnoreCase(CommonUtil.EVENT_TYPE_TEAM) || m_eventType.equalsIgnoreCase(CommonUtil.EVENT_TYPE_VIRTUAL_TEAM)) {
			rankingPath = DOWNLOAD_TEAM_USER_RANKING_PATH;
		}
		
		getRankingData(m_UserId, m_eventID, m_eventType, m_eventTeamName, rankingPath);
	}

	private void getRankingData(String m_UserId, String m_eventID, String m_eventType, String m_eventTeamName, String rankingPath) {
		String url = getString(R.string.base_uri) + rankingPath;
		HashMap<String, String> parameters = new HashMap<String, String>();	
		parameters.put(CommonUtil.USER_ID, m_userID);
		
		if (rankingPath.equalsIgnoreCase(DOWNLOAD_GENERAL_USER_RANKING_PATH)) {
			parameters.put(CommonUtil.EVENT_ID, m_eventID);
		} else if (rankingPath.equalsIgnoreCase(DOWNLOAD_TEAM_USER_RANKING_PATH)) {
			parameters.put(CommonUtil.EVENT_ID, m_eventID);
			parameters.put(CommonUtil.EVENT_TEAM_NAME, m_eventTeamName);
		}
		
		Context context = m_context;
		boolean progressVisible = true;
		int id = 111;
		BetterAsyncTaskCallable callable  = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list,
					int id) {
				if (list.size() > 0) {
					//String strScore = list.get(0).get(CommonUtil.T_SCORE) + " 점";
					String strScore = list.get(0).get(CommonUtil.INDIVIDUAL_SCORE) + " 점";
					//String strParticipate = list.get(0).get(CommonUtil.T_PARTICIPATE) + " 회";
					String strParticipate = list.get(0).get(CommonUtil.JOIN_CNT) + " 회";
					
					String strRank = list.get(0).get(CommonUtil.RANKING);
					String strTotalCnt = list.get(0).get(CommonUtil.TOTAL_USER_CNT);

					m_ranking_detail_user_score.setText(strScore);
					m_ranking_detail_user_participate_cnt.setText(strParticipate);

					// 상위
					double d_Rank = Double.parseDouble(strRank);
					double d_TotalCnt = Double.parseDouble(strTotalCnt);

					String s_rankPercent = "상위 "
							+ RankingCommon.longDouble2String(1, d_Rank / d_TotalCnt * 100) + " %";
					m_ranking_detail_user_rank_percent.setText(s_rankPercent);

					String s_rankDetail = strRank + " 위" + " (총" + strTotalCnt + " 명)";
					m_ranking_detail_user_rank_detail.setText(s_rankDetail);

					String str_gradeName = RankingCommon.getGradeName(m_context, strRank, strTotalCnt);
					m_ranking_detail_user_rank_name.setText(str_gradeName);
					
					int str_gradeImage = RankingCommon.getGradeImage(m_context, strRank, strTotalCnt);
					m_ranking_detail_user_rank_img.setImageResource(str_gradeImage);
				}
				return true;
			}
		};
		
		HttpPostParameterAsyncTask task = new  HttpPostParameterAsyncTask(url, parameters, id, context, callable, progressVisible);
		task.execute();
	}

	
}