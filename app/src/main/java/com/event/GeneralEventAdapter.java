package com.event;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.common.CommonUtil;
import com.skcc.portal.skmsquiz.R;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to set view in General Event in SKMS Quiz Application.
 */
public class GeneralEventAdapter extends BaseAdapter {
	
	/* member representing general events list to inflate data. */
	private ArrayList<HashMap<String, String>> m_generalEventList;
	
	/* member representing general events layout inflater to inflate data. */
	private LayoutInflater m_inflater;
	
	/* GeneralEventAdapter class context */
	private Context m_context;
	
	/**
	 * This method returns the general event list member variable.
	 * 
	 * @return ArrayList<HashMap<String, String>>
	 */
	public ArrayList<HashMap<String, String>> getGeneralEventList() {
		return m_generalEventList;
	}
	
	/**
	 * This method set the general event list member variable.
	 * 
	 * @param ArrayList<HashMap<String, String>>
	 */
	public void setGeneralEventList(ArrayList<HashMap<String, String>> generalEventList) {
		this.m_generalEventList = generalEventList;
	}
	
	/**
	 * This is a GeneralEventAdapter constructor to initialize member variables.
	 * 
	 * @param generalEventList
	 * @param context
	 */
	public GeneralEventAdapter(ArrayList<HashMap<String, String>> generalEventList,	Context context) {
		this.m_generalEventList = generalEventList;
		this.m_inflater = LayoutInflater.from(context);
		this.m_context = context;
	}
	
	/**
	 * This method returns the number of items in the general event list member variable.
	 * 
	 * @return int
	 */
	public int getCount() {
		int cnt = 0;
		
		if (m_generalEventList != null)
			cnt = m_generalEventList.size();
		
		return cnt;
	}
	
	/**
	 * This method returns the EVENT_ID value of an item in the general event list member variable.
	 * 
	 * @return String 
	 */
	public String getItem(int position) {
		return m_generalEventList.get(position).get(CommonUtil.EVENT_ID);
	}
	
	/**
	 * This method returns the position of an item in the general event list member variable.
	 * 
	 * @return long
	 */
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * This method returns the inflated view of the General Event List.
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * 
	 * @return View
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = m_inflater.inflate(R.layout.generalevent_listview_entry, null);
			convertView.setSelected(true); // 2012-10-29 artlim
			holder = new ViewHolder();
			

			holder.generalevent_listview_entry_status = (TextView) convertView.findViewById(R.id.generalevent_listview_entry_status);
			holder.generalevent_listview_entry_name = (TextView) convertView.findViewById(R.id.generalevent_listview_entry_name);
			holder.generalevent_listview_entry_time = (TextView) convertView.findViewById(R.id.generalevent_listview_entry_time);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String eventStatus = m_generalEventList.get(position).get(CommonUtil.EVENT_STATUS);
		
		if (eventStatus.equalsIgnoreCase(CommonUtil.EVENT_STATUS_OPEN)) {
			eventStatus = CommonUtil.EVENT_STATUS_OPEN_STR;
		} else if (eventStatus.equalsIgnoreCase(CommonUtil.EVENT_STATUS_FINISHED)) {
			eventStatus = CommonUtil.EVENT_STATUS_FINISHED_STR;
		} else if (eventStatus.equalsIgnoreCase(CommonUtil.EVENT_STATUS_UPCOMING)) {
			eventStatus = CommonUtil.EVENT_STATUS_UPCOMING_STR;
		}
		
		holder.generalevent_listview_entry_status.setText(eventStatus);
		holder.generalevent_listview_entry_name.setText(m_generalEventList.get(position).get(CommonUtil.EVENT_NAME));
		holder.generalevent_listview_entry_time.setText(CommonUtil.GENERAL_EVENT_SCHEDULE + m_generalEventList.get(position).get(CommonUtil.START_DT) + " ~ " + m_generalEventList.get(position).get(CommonUtil.END_DT));

		return convertView;
	}
	
	/**
	 * @author sati
     * @version 1.0, 2012-05-05
     * 
	 * Inner class representing one layout entry in the General Event List.
	 */
	class ViewHolder {
		TextView generalevent_listview_entry_status;
		TextView generalevent_listview_entry_name;
		TextView generalevent_listview_entry_time;
		TextView generalevent_listview_entry_id;
	}

}
