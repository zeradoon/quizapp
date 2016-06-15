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
 * This class is used to set view in General Event Quiz in SKMS Quiz Application.
 */
public class GeneralEventQuizAdapter extends BaseAdapter {
	/* member representing general event quiz answer list to inflate data. */
	private List<HashMap<String, String>> m_generalEventQuizAnswerList;
	
	/* member representing general event quiz layout inflater to inflate data. */
	private LayoutInflater m_inflater;
	
	/* GeneralEventQuizAdapter class context */
	private Context m_context;
	
	/* member representing general event quiz question count. */
	private int m_ques_count;
	
	/* member representing general event quiz answers from user. */
	private String[] m_my_answer;
	
	/* member representing color code for correct and wrong answer. */
	int resultColor = 0xFFFF0000;
	int black = 0xFF000000;
	
	/**
	 * This method return the general event answer list member variable.
	 * 
	 * @return
	 */
	public List<HashMap<String, String>> getGeneralEventQuizAnswerList() {
		return m_generalEventQuizAnswerList;
	}
	
	
	/**
	 * This method set the general event answer list member variable.
	 * 
	 * @param generalEventQuizAnswerList
	 */
	public void setGeneralEventQuizAnswerList(List<HashMap<String, String>> generalEventQuizAnswerList) {
		this.m_generalEventQuizAnswerList = generalEventQuizAnswerList;
	}
	
	/**
	 * 	This method set the user answer list member variable.
	 * 
	 * @param my_answer
	 */
	public void setAnswerList(String[] my_answer) {
		this.m_my_answer = my_answer;
	}
	
	/**
	 * Constructor Method.
	 * 
	 * @param generalEventQuizAnswerList
	 * @param context
	 */
	public GeneralEventQuizAdapter(List<HashMap<String, String>> generalEventQuizAnswerList, Context context) {
		this.m_generalEventQuizAnswerList = generalEventQuizAnswerList;
		m_inflater = LayoutInflater.from(context);
		m_context = context;
	}
	
	/**
	 * Constructor Method.
	 * 
	 * @param generalEventQuizAnswerList
	 * @param ques_count
	 * @param my_answer
	 * @param context
	 */
	public GeneralEventQuizAdapter(List<HashMap<String, String>> generalEventQuizAnswerList, int ques_count, String[] my_answer, Context context) {
		this.m_generalEventQuizAnswerList = generalEventQuizAnswerList;
		m_inflater = LayoutInflater.from(context);
		m_context = context;
		m_ques_count = ques_count;
		m_my_answer = my_answer;
	}
	
	/**
	 * This method return the general event quiz answer list member count.
	 * 
	 * @return int
	 */
	public int getCount() {
		int cnt = 0;
		
		if (m_generalEventQuizAnswerList != null)
			cnt = m_generalEventQuizAnswerList.size();
		
		return cnt;
	}
	
	/**
	 * This method return the answerlist seq no. value from the answer list at specified position.
	 * 
	 * @param position
	 * @return String 
	 */
	public String getItem(int position) {
		return m_generalEventQuizAnswerList.get(position).get(CommonUtil.SEQ_NO);
	}
	
	/**
	 * This method returns the position of an item in the general event quiz answer list member variable.
	 */
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * This method returns the inflated view of the General Event Quiz  List.
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
			convertView = m_inflater.inflate(R.layout.generalevent_quiz_answerlist_entry, null);

			holder = new ViewHolder();
			holder.generaleventquiz_listview_ques = (TextView) convertView.findViewById(R.id.generaleventquiz_listview_ques);
			holder.generaleventquiz_listview_answer = (TextView) convertView.findViewById(R.id.generaleventquiz_listview_answer);
			holder.generaleventquiz_listview_navQues = (Button) convertView.findViewById(R.id.generaleventquiz_listview_navQues);
			
			holder.generaleventquiz_listview_navQues.setClickable(true);
			holder.generaleventquiz_listview_navQues.setFocusable(true);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.generaleventquiz_listview_ques.setText(CommonUtil.QUESTION_STRING + (position+1));
		holder.generaleventquiz_listview_answer.setText(CommonUtil.ANSWER_STRING);
		//holder.generaleventquiz_listview_navQues.setId(Integer.parseInt(m_generalEventList.get(position).get("SEQ_NO")));
		
		holder.generaleventquiz_listview_ques.setId(Integer.parseInt("999" + position+1));
		holder.generaleventquiz_listview_answer.setId(Integer.parseInt("9999" + position+1));
		holder.generaleventquiz_listview_navQues.setId((position+1));
		
		if (m_my_answer[position] == "") {
			holder.generaleventquiz_listview_ques.setTextColor(ColorStateList.valueOf(resultColor));
			holder.generaleventquiz_listview_answer.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			holder.generaleventquiz_listview_ques.setTextColor(ColorStateList.valueOf(black));
			holder.generaleventquiz_listview_answer.setTextColor(ColorStateList.valueOf(black));
			holder.generaleventquiz_listview_answer.setText(CommonUtil.ANSWER_SAVED);
		}
		
		return convertView;
	}
	
	/**
	 * @author sati
     * @version 1.0, 2012-05-05
     * 
	 * Inner class representing one layout entry in the General Event Quiz List.
	 */
	class ViewHolder {
		TextView generaleventquiz_listview_ques;
		TextView generaleventquiz_listview_answer;
		Button generaleventquiz_listview_navQues;
	}

}
