package com.ranking;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.common.CommonUtil;
import com.skcc.portal.skmsquiz.R;

public class RankingEventListAdapter extends BaseAdapter {
	ArrayList<HashMap<String, String>> m_rankingEventList;
	private LayoutInflater m_inflater;
	Context m_context;

	public RankingEventListAdapter(Context context,
			ArrayList<HashMap<String, String>> rankingEventList) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		m_inflater = LayoutInflater.from(context);
		m_rankingEventList = rankingEventList;
		m_context = context;
	}

	/**
	 * The number of items in the list is determined by the number of speeches
	 * in our array.
	 * 
	 * @see android.widget.ListAdapter#getCount()
	 */
	public int getCount() {
		int cnt = 0;
		if (m_rankingEventList != null)
			cnt = m_rankingEventList.size();
		return cnt;
	}

	/**
	 * Since the data comes from an array, just returning the index is sufficent
	 * to get at the data. If we were using a more complex data structure, we
	 * would return whatever object represents one row in the list.
	 * 
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	public Object getItem(int position) {
		return position;
	}

	/**
	 * Use the array index as a unique id.
	 * 
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
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
		// A ViewHolder keeps references to children views to avoid
		// unneccessary calls
		// to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is
		// no need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = m_inflater.inflate(
					R.layout.ranking_event_listview_entry, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.event_State = (TextView) convertView
					.findViewById(R.id.ranking_event_listview_entry_event_state);
			holder.event_Title = (TextView) convertView
					.findViewById(R.id.ranking_event_listview_entry_event_title);
			holder.event_Title.setSelected(true); // artlim 2012-10-29
			holder.event_Duration = (TextView) convertView
					.findViewById(R.id.ranking_event_listview_entry_event_duration);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		// holder.text.setText(DATA[position]);
		// holder.icon.setImageBitmap((position & 1) == 1 ? mIcon1 :
		// mIcon2);

		holder.event_State.setText(m_rankingEventList.get(position).get(
				CommonUtil.EVENT_STATE));
		holder.event_Title.setText(m_rankingEventList.get(position).get(
				CommonUtil.EVENT_NAME));
		holder.event_Duration.setText(
				CommonUtil.GENERAL_EVENT_SCHEDULE
				+ m_rankingEventList.get(position).get(CommonUtil.START_DURATION)
				+ " ~ "
				+ m_rankingEventList.get(position).get(CommonUtil.END_DURATION));

		return convertView;
	}

	static class ViewHolder {
		TextView event_State;
		TextView event_Title;
		TextView event_Duration;
	}

	public void setRankingDataList(
			ArrayList<HashMap<String, String>> rankingEventList) {
		m_rankingEventList = rankingEventList;
	}

}
