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
 * This class is used to set view in Survival Event Quiz in SKMS Quiz Application.
 */
public class SurvivalEventQuizAdapter extends BaseAdapter {
	
	/* member representing survival event quiz answer list to inflate data. */
	private List<HashMap<String, String>> m_survivalEventQuizAnswerList;
	
	/* member representing survival event quiz layout inflater to inflate data. */
	private LayoutInflater m_inflater;
	
	/* SurvivalEventQuizAdapter class context */
	private Context m_context;
	
	/* member representing survival event quiz question count. */
	private int m_ques_count;
	
	/* member representing survival event quiz answers from user. */
	private String[] m_my_answer;
	
	/* member representing color code for correct and wrong answer. */
	int resultColor = 0xFFFF0000;
	int black = 0xFF000000;
	
	
	/**
	 * This method return the survival event answer list member variable.
	 * 
	 * @return
	 */
	public List<HashMap<String, String>> getSurvivalEventQuizAnswerList() {
		return m_survivalEventQuizAnswerList;
	}
	
	/**
	 * This method set the survival event answer list member variable.
	 * 
	 * @param generalEventQuizAnswerList
	 */
	public void setSurvivalEventQuizAnswerList(List<HashMap<String, String>> survivalEventQuizAnswerList) {
		this.m_survivalEventQuizAnswerList = survivalEventQuizAnswerList;
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
	 * @param survivalEventQuizAnswerList
	 * @param context
	 */
	public SurvivalEventQuizAdapter(List<HashMap<String, String>> survivalEventQuizAnswerList,	Context context) {
		this.m_survivalEventQuizAnswerList = survivalEventQuizAnswerList;
		m_inflater = LayoutInflater.from(context);
		m_context = context;
	}
	
	/**
	 * Constructor Method.
	 * 
	 * @param survivalEventQuizAnswerList
	 * @param ques_count
	 * @param my_answer
	 * @param context
	 */
	public SurvivalEventQuizAdapter(List<HashMap<String, String>> survivalEventQuizAnswerList, int ques_count, String[] my_answer, Context context) {
		this.m_survivalEventQuizAnswerList = survivalEventQuizAnswerList;
		m_inflater = LayoutInflater.from(context);
		m_context = context;
		m_ques_count = ques_count;
		m_my_answer = my_answer;
	}
	
	/**
	 * This method return the survival event quiz answer list member count.
	 * 
	 * @return int
	 */
	public int getCount() {
		int cnt = 0;
		
		if (m_survivalEventQuizAnswerList != null)
			cnt = m_survivalEventQuizAnswerList.size();
		
		return cnt;
	}
	
	/**
	 * This method return the answerlist seq no. value from the answer list at specified position.
	 * 
	 * @param position
	 * @return String 
	 */
	public String getItem(int position) {
		return m_survivalEventQuizAnswerList.get(position).get(CommonUtil.SEQ_NO);
	}
	
	/**
	 * This method returns the position of an item in the survival event quiz answer list member variable.
	 */
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * This method returns the inflated view of the survival Event Quiz  List.
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
			convertView = m_inflater.inflate(R.layout.survivalevent_quiz_answerlist_entry, null);

			holder = new ViewHolder();
			holder.survivaleventquiz_listview_ques = (TextView) convertView.findViewById(R.id.survivaleventquiz_listview_ques);
			holder.survivaleventquiz_listview_answer = (TextView) convertView.findViewById(R.id.survivaleventquiz_listview_answer);
			holder.survivaleventquiz_listview_navQues = (Button) convertView.findViewById(R.id.survivaleventquiz_listview_navQues);
			
			holder.survivaleventquiz_listview_navQues.setClickable(true);
			holder.survivaleventquiz_listview_navQues.setFocusable(true);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// Bind the data efficiently with the holder.
		holder.survivaleventquiz_listview_ques.setText(CommonUtil.QUESTION_STRING + (position+1));
		holder.survivaleventquiz_listview_answer.setText(CommonUtil.ANSWER_STRING);
		//holder.survivaleventquiz_listview_navQues.setId(Integer.parseInt(m_survivaleventList.get(position).get("SEQ_NO")));
		
		holder.survivaleventquiz_listview_ques.setId(Integer.parseInt("999" + position+1));
		holder.survivaleventquiz_listview_answer.setId(Integer.parseInt("9999" + position+1));
		holder.survivaleventquiz_listview_navQues.setId((position+1));
		
		if (m_my_answer[position] == "") {
			holder.survivaleventquiz_listview_ques.setTextColor(ColorStateList.valueOf(resultColor));
			holder.survivaleventquiz_listview_answer.setTextColor(ColorStateList.valueOf(resultColor));
		} else {
			holder.survivaleventquiz_listview_ques.setTextColor(ColorStateList.valueOf(black));
			holder.survivaleventquiz_listview_answer.setTextColor(ColorStateList.valueOf(black));
			holder.survivaleventquiz_listview_answer.setText(CommonUtil.ANSWER_SAVED);
		}
		
		return convertView;
	}
	
	/**
	 * @author sati
     * @version 1.0, 2012-05-05
     * 
	 * Inner class representing one layout entry in the Survival Event Quiz List.
	 */
	class ViewHolder {
		TextView survivaleventquiz_listview_ques;
		TextView survivaleventquiz_listview_answer;
		Button survivaleventquiz_listview_navQues;
	}

}
