/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.ranking;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.CommonUtil;
import com.skcc.portal.skmsquiz.R;

public class RankingRealAdapter extends BaseAdapter {
	private static final String DOWNLOAD_USER_IMAGE_PATH = "downloadUserImage.do";
	
	private ImageDownloader imageDownloader = new ImageDownloader();
	ArrayList<HashMap<String, String>> rankingDataList;
	private LayoutInflater m_inflater;
	Context m_context;

	public ArrayList<HashMap<String, String>> getRankingDataList() {
		return rankingDataList;
	}

	public void setRankingDataList(
			ArrayList<HashMap<String, String>> rankingDataList) {
		this.rankingDataList = rankingDataList;
	}

	public RankingRealAdapter(ArrayList<HashMap<String, String>> m_rankinkDataList,
			Context context) {

		this.rankingDataList = m_rankinkDataList;
		m_inflater = LayoutInflater.from(context);
		m_context = context;
	}

	public int getCount() {
		int cnt = 0;
		if (rankingDataList != null)
			cnt = rankingDataList.size();
		return cnt;
	}

	public String getItem(int position) {
		return rankingDataList.get(position).get(CommonUtil.RANKING);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid unneccessary
		// calls
		// to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is no
		// need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = m_inflater.inflate(R.layout.ranking_listview_entry,
					null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.ranking_listview_entry_ranking = (TextView) convertView
					.findViewById(R.id.ranking_listview_entry_ranking);
			holder.ranking_listview_entry_ranking_grade = (TextView) convertView
					.findViewById(R.id.ranking_listview_entry_ranking_grade);
			holder.ranking_listview_entry_user_nickname = (TextView) convertView
					.findViewById(R.id.ranking_listview_entry_user_nickname);
			holder.ranking_listview_entry_ranking_score_detail = (TextView) convertView
					.findViewById(R.id.ranking_listview_entry_ranking_score_detail);
			holder.ranking_listview_entry_ranking_grade_img = (ImageView) convertView
					.findViewById(R.id.ranking_listview_entry_ranking_grade_img);
			holder.ranking_listview_entry_user_img = (ImageView) convertView
					.findViewById(R.id.ranking_listview_entry_user_img);
			holder.ranking_listview_entry_medal_img = (ImageView) convertView
					.findViewById(R.id.ranking_listview_entry_medal_img);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}
		String str_ranking = rankingDataList.get(position).get(CommonUtil.RANKING);
		String str_totalUserCnt = rankingDataList.get(position).get(
				CommonUtil.TOTAL_USER_CNT);

		// Bind the data efficiently with the holder.
		holder.ranking_listview_entry_ranking.setText(str_ranking
				+ m_context.getString(R.string.profile_main_rank_suffix));

		String str_gradeName = RankingCommon.getGradeName(m_context,
				str_ranking, str_totalUserCnt);

		int str_gradeImage = RankingCommon.getGradeImage(m_context,
				str_ranking, str_totalUserCnt);

		holder.ranking_listview_entry_ranking_grade_img
				.setImageResource(str_gradeImage);

		holder.ranking_listview_entry_ranking_grade.setText(str_gradeName);

		holder.ranking_listview_entry_user_nickname.setText(rankingDataList
				.get(position).get(CommonUtil.USER_NM));

		//String score_detail = rankingDataList.get(position).get(CommonUtil.T_SCORE)	+ "점 (" + rankingDataList.get(position).get(CommonUtil.T_PARTICIPATE) + "회)";
		String score_detail = rankingDataList.get(position).get(CommonUtil.INDIVIDUAL_SCORE)	+ "점 (" + rankingDataList.get(position).get(CommonUtil.JOIN_CNT) + "회)";
		holder.ranking_listview_entry_ranking_score_detail
				.setText(score_detail);

		if (rankingDataList.get(position).get(CommonUtil.USER_IMG).lastIndexOf(".png") > 1) {
			String imgUrl = m_context.getString(R.string.base_uri) + DOWNLOAD_USER_IMAGE_PATH + "?" + CommonUtil.F_DOWNLOAD + "=" + rankingDataList.get(position).get(CommonUtil.USER_IMG);

			imageDownloader.download(imgUrl,
					holder.ranking_listview_entry_user_img);
		} else {
			holder.ranking_listview_entry_user_img
					.setImageResource(R.drawable.photo_ranking_noimage);
		}
		
		if (str_ranking.equals("1")) {
			holder.ranking_listview_entry_medal_img.setImageResource(R.drawable.medal_gold);
		} else if (str_ranking.equals("2")){
			holder.ranking_listview_entry_medal_img.setImageResource(R.drawable.medal_silver);
		} else if (str_ranking.equals("3")) {
			holder.ranking_listview_entry_medal_img.setImageResource(R.drawable.medal_bronze);
		} else {
			holder.ranking_listview_entry_medal_img.setImageBitmap(null);
		}

		return convertView;
	}

	class ViewHolder {
		TextView ranking_listview_entry_ranking;
		TextView ranking_listview_entry_ranking_grade;
		TextView ranking_listview_entry_user_nickname;
		TextView ranking_listview_entry_ranking_score_detail;
		ImageView ranking_listview_entry_ranking_grade_img;
		ImageView ranking_listview_entry_user_img;
		ImageView ranking_listview_entry_medal_img;
	}

	public ImageDownloader getImageDownloader() {
		return imageDownloader;
	}

}
