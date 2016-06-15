package com.notice;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.CommonUtil;
import com.common.http.CommonDownloader;
import com.skcc.portal.skmsquiz.R;

/**
 * 클래스설명 :
 * 
 * @version : 2011. 9. 16.
 * @author : jungungi
 * @분류 : QuizApp / package com.notice;
 */

public class NoticeExpandableListAdapter extends BaseExpandableListAdapter {
	private ArrayList<HashMap<String, String>> noticeList;
	private Context context;

	public NoticeExpandableListAdapter(Context context, ArrayList<HashMap<String, String>> noticeList) {
		this.context = context;
		this.noticeList = noticeList;
	}
	
	public ArrayList<HashMap<String, String>> getNoticeList() {
		return noticeList;
	}

	public void setNoticeList(ArrayList<HashMap<String, String>> noticeList) {
		this.noticeList = noticeList;
	}

	@Override
	public Object getChild(int groupPos, int childPos) {
		return this.noticeList.get(groupPos).get(CommonUtil.CONTENT);
	}

	@Override
	public long getChildId(int groupPos, int childPos) {
		return childPos;
	}

	@Override
	public int getChildrenCount(int groupPos) {
		return 1;
	}

	@Override
	public View getChildView(int groupPos, int childPos, boolean isLastChild,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.noticelist_child_row,
					null);
		}

		TextView txtView = (TextView) convertView
				.findViewById(R.id.noticelist_child_row_content);
		if (txtView != null)
			txtView.setText(Html.fromHtml(getChild(groupPos, childPos).toString()));


		convertView.setFocusableInTouchMode(true);
		return convertView;
	}

	@Override
	public Object getGroup(int groupPos) {
		return this.noticeList.get(groupPos).get(CommonUtil.TITLE);
	}

	@Override
	public int getGroupCount() {
		int cnt = 0;
		if (this.noticeList != null) 
			cnt = this.noticeList.size();
		
		return cnt;
	}

	@Override
	public long getGroupId(int groupPos) {
		return groupPos;
	}

	@Override
	public View getGroupView(int groupPos, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.noticelist_group_row,
					null);
		}

		TextView txtView = (TextView) convertView
				.findViewById(R.id.noticelist_group_row_title);
		ImageView imgView = (ImageView) convertView.findViewById(R.id.noticelist_group_row_notice_img);
		
		String subject = getGroup(groupPos).toString();
		
		if (noticeList.get(groupPos).get(CommonUtil.FIX_YN).equalsIgnoreCase(CommonUtil.FLAG_Y)){
//			subject = "<b><font color=red>공지</font></b>" + "<b> " + subject +"</b>";
			subject = "<b> " + subject +"</b>";
			imgView.setVisibility(View.VISIBLE);
		}
		else {
			imgView.setVisibility(View.GONE);
		}
		
		txtView.setText(Html.fromHtml(subject));
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPos, int childPos) {
		return true;
	}

}
