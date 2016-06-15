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

public class RankingEventBasedTeamAdapter extends BaseAdapter {

	private ImageDownloader imageDownloader = new ImageDownloader();
	ArrayList<HashMap<String, String>> rankingEventBasedOnTeamDataList;
	private LayoutInflater m_inflater;
	Context m_context;

	public ArrayList<HashMap<String, String>> getRankingDataList() {
		return rankingEventBasedOnTeamDataList;
	}

	public void setRankingDataList(
			ArrayList<HashMap<String, String>> rankingDataList) {
		this.rankingEventBasedOnTeamDataList = rankingDataList;
	}

	public RankingEventBasedTeamAdapter(
			ArrayList<HashMap<String, String>> m_rankinkDataList,
			Context context) {

		this.rankingEventBasedOnTeamDataList = m_rankinkDataList;
		m_inflater = LayoutInflater.from(context);
		m_context = context;
	}

	public int getCount() {
		int cnt = 0;
		if (rankingEventBasedOnTeamDataList != null)
			cnt = rankingEventBasedOnTeamDataList.size();
		return cnt;
	}

	public String getItem(int position) {
		return rankingEventBasedOnTeamDataList.get(position).get(CommonUtil.RANKING);
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
			convertView = m_inflater.inflate(
					R.layout.ranking_list_event_based_team_entry, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.ranking_list_event_based_team_entry_ranking = (TextView) convertView
					.findViewById(R.id.ranking_list_event_based_team_entry_ranking);
			holder.ranking_list_event_based_team_entry_dept_name = (TextView) convertView
					.findViewById(R.id.ranking_list_event_based_team_entry_dept_name);
			holder.ranking_list_event_based_team_entry_member_cnt = (TextView) convertView
					.findViewById(R.id.ranking_list_event_based_team_entry_member_cnt);
			holder.ranking_list_event_based_team_entry_join_cnt = (TextView) convertView
					.findViewById(R.id.ranking_list_event_based_team_entry_join_cnt);
			holder.ranking_list_event_based_team_entry_dept_score = (TextView) convertView
					.findViewById(R.id.ranking_list_event_based_team_entry_dept_score);
			holder.ranking_list_event_based_team_entry_dept_avg = (TextView) convertView
					.findViewById(R.id.ranking_list_event_based_team_entry_dept_avg);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}
		// String str_ranking =
		// rankingEventBasedOnTeamDataList.get(position).get("RANKING");
		// String str_totalUserCnt =
		// rankingEventBasedOnTeamDataList.get(position).get(
		// "TOTAL_USER_CNT");

		// Bind the data efficiently with the holder.
		holder.ranking_list_event_based_team_entry_ranking
				.setText(rankingEventBasedOnTeamDataList.get(position).get(
						CommonUtil.RANKING)
						+ m_context
								.getString(R.string.ranking_list_entry_ranking_suffix));

		holder.ranking_list_event_based_team_entry_dept_name
				.setText(rankingEventBasedOnTeamDataList.get(position).get(
						CommonUtil.USER_TEAM_NAME));

		
		holder.ranking_list_event_based_team_entry_member_cnt
		.setText(rankingEventBasedOnTeamDataList.get(position).get(
				CommonUtil.MEMBER_CNT)+ m_context
				.getString(R.string.ranking_list_entry_member_cnt_suffix));
		
		holder.ranking_list_event_based_team_entry_join_cnt
		.setText(rankingEventBasedOnTeamDataList.get(position).get(
				CommonUtil.JOIN_CNT)+ m_context
				.getString(R.string.ranking_list_entry_join_cnt_suffix));
		
		holder.ranking_list_event_based_team_entry_dept_score
		.setText(rankingEventBasedOnTeamDataList.get(position).get(
				CommonUtil.DEPT_SCORE)+ m_context
				.getString(R.string.ranking_list_entry_score_suffix));
		
		holder.ranking_list_event_based_team_entry_dept_avg
		.setText(rankingEventBasedOnTeamDataList.get(position).get(
				CommonUtil.DEPT_AVG)+ m_context
				.getString(R.string.ranking_list_entry_score_suffix));
		
		// if (str_ranking.equals("1")) {
		// holder.ranking_listview_entry_medal_img.setImageResource(R.drawable.medal_gold);
		// } else if (str_ranking.equals("2")){
		// holder.ranking_listview_entry_medal_img.setImageResource(R.drawable.medal_silver);
		// } else if (str_ranking.equals("3")) {
		// holder.ranking_listview_entry_medal_img.setImageResource(R.drawable.medal_bronze);
		// } else {
		// holder.ranking_listview_entry_medal_img.setImageBitmap(null);
		// }

		return convertView;
	}

	class ViewHolder {
		TextView ranking_list_event_based_team_entry_ranking;
		TextView ranking_list_event_based_team_entry_dept_name;
		TextView ranking_list_event_based_team_entry_member_cnt;
		TextView ranking_list_event_based_team_entry_join_cnt;
		TextView ranking_list_event_based_team_entry_dept_score;
		TextView ranking_list_event_based_team_entry_dept_avg;
	}

	public ImageDownloader getImageDownloader() {
		return imageDownloader;
	}

}
