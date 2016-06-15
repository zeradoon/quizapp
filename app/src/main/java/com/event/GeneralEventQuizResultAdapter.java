package com.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.common.CommonUtil;
import com.skcc.portal.skmsquiz.R;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to set view in General Event Quiz Result in SKMS Quiz Application.
 */
public class GeneralEventQuizResultAdapter extends BaseAdapter {
	/* member representing general event quiz result layout inflater to inflate data. */
	private LayoutInflater m_inflater;
	
	/* GeneralEventQuizResultAdapter class context */
	private Context m_context;
	
	/* member representing general event quiz result array . */
	private int[] m_generalevent_jungdab;
	
	/* member representing color code for correct and wrong answer. */
	int resultColor = 0xFFFF0000;
	int black = 0xFF000000;
	
	/**
	 * This method set the general event answer array member variable.
	 * 
	 * @param generalevent_jungdab
	 */
	public void setGeneralEventList(int[] generalevent_jungdab) {
		this.m_generalevent_jungdab = generalevent_jungdab;
	}
	
	/**
	 * Constructor Method.
	 * 
	 * @param context
	 */
	public GeneralEventQuizResultAdapter(Context context) {
		m_inflater = LayoutInflater.from(context);
		m_context = context;
	}
	
	/**
	 * Constructor Method.
	 * 
	 * @param generalevent_jungdab
	 * @param context
	 */
	public GeneralEventQuizResultAdapter(int[] generalevent_jungdab, Context context) {
		m_inflater = LayoutInflater.from(context);
		m_context = context;
		m_generalevent_jungdab = generalevent_jungdab;
	}
	
	/**
	 * This method returns the number of items in the general event answer array member variable.
	 * 
	 * @return int
	 */
	public int getCount() {
		return m_generalevent_jungdab.length;
	}
	
	/**
	 * This method returns the value of an item in the general event answer array member variable.
	 * 
	 * @return String 
	 */
	public String getItem(int position) {
		return String.valueOf(m_generalevent_jungdab[position]);
	}
	
	/**
	 * This method returns the position of an item in the general event answer array member variable.
	 * 
	 * @return long
	 */
	public long getItemId(int position) {
		return m_generalevent_jungdab[position];
	}
	
	/**
	 * This method returns the inflated view of the General Event Quiz Result Activity.
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
			convertView = m_inflater.inflate(R.layout.generalevent_quiz_resultlist_entry, null);
			holder = new ViewHolder();
			
			holder.generaleventquiz_listview_ques = (TextView) convertView.findViewById(R.id.generaleventquiz_listview_ques);
			holder.generaleventquiz_listview_answer = (TextView) convertView.findViewById(R.id.generaleventquiz_listview_answer);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// Bind the data efficiently with the holder.
		holder.generaleventquiz_listview_ques.setText(CommonUtil.QUESTION_STRING + (position+1));
		holder.generaleventquiz_listview_answer.setText(CommonUtil.ANSWER_WRONG);
		
		holder.generaleventquiz_listview_ques.setId(Integer.parseInt("999" + position+1));
		holder.generaleventquiz_listview_answer.setId(Integer.parseInt("9999" + position+1));
		
		if (m_generalevent_jungdab[position] == 1) {
			holder.generaleventquiz_listview_ques.setTextColor(ColorStateList.valueOf(resultColor));
			holder.generaleventquiz_listview_answer.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			holder.generaleventquiz_listview_ques.setTextColor(ColorStateList.valueOf(black));
			holder.generaleventquiz_listview_answer.setTextColor(ColorStateList.valueOf(black));
			holder.generaleventquiz_listview_answer.setText(CommonUtil.ANSWER_CORRECT);
		}
		
		return convertView;
	}
	
	/**
	 * @author sati
     * @version 1.0, 2012-05-05
     * 
	 * Inner class representing one layout entry in the General Event Quiz Result Activity.
	 */
	class ViewHolder {
		TextView generaleventquiz_listview_ques;
		TextView generaleventquiz_listview_answer;
	}

}
