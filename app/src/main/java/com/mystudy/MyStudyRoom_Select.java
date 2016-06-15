package com.mystudy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.common.CommonUtil;
import com.common.NetworkUtil;
import com.common.XmlParser;
import com.common.async.AbstractAsyncActivity;
import com.skcc.portal.skmsquiz.R;

public class MyStudyRoom_Select extends AbstractAsyncActivity {
	
	public int Cnt1 = 0;
	public int Cnt2 = 0;
	private static final String MY_STUDYROOM = "/myStudyRoom.do";
	
	List<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
	private String m_user_id;
	private String m_comp_cd;
	private String m_user_dept;
	
	private ImageView m_btn_home;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mystudyroom_select);
	    
	    //user_id = NetworkUtil.getPhoneNumber(MyStudyRoom_Select.this);
	    m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_user_dept = getIntent().getStringExtra(CommonUtil.USER_DEPT);
		m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		
		m_btn_home = (ImageView) findViewById(R.id.btn_home);
		m_btn_home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
			
		
	    new setValuesToMyStudyRoomAsyncTask().execute(m_user_id);
	}

    /*
	 * Button OnClick시 동작 처리. 
	 */
	public void monclick(View v){
		if(CommonUtil.showNetworkAlert(MyStudyRoom_Select.this)) {
			Intent intent = new Intent(MyStudyRoom_Select.this, MyStudyRoom_Tab_Main.class);
			intent.putExtra(CommonUtil.USER_ID, m_user_id);
			intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
			intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
			
			switch (v.getId()) {
			case R.id.mystudyroom_selectbtn01:
				intent.putExtra(CommonUtil.CHK, 0);
				
				startActivity(intent);
				finish();
				break;
			case R.id.mystudyroom_selectbtn02:
				intent.putExtra(CommonUtil.CHK, 1);
				
				startActivity(intent);
				finish();
				break;
			case R.id.btn_home:
				finish();
				break;
			}
		}
	}
	/*
	 * 문제 유형별 새 문제 세팅 : setValuesToQuiz 에 대한 AsyncTask 수행 클래스
	 * 참고 : http://tigerwoods.tistory.com/28
	 */
    private class setValuesToMyStudyRoomAsyncTask extends AsyncTask<String, Void, List<HashMap<String,String>>> {
    	@Override
    	protected void onPreExecute(){
    		showLoadingProgressDialog();
    	}
    	
		@Override
		protected List<HashMap<String,String>> doInBackground(String ... params) {
			String userId = "";

	    	if (params[0] != null)
	    		userId = params[0];
	    	
			list = XmlParser.getValuesFromXML(getString(R.string.base_uri) + MY_STUDYROOM + "?" + CommonUtil.USER_ID + "=" + userId);			
			return list;
		}

		@Override
		protected void onPostExecute(List<HashMap<String,String>> list){
			dismissProgressDialog();
			setNoteCnt(list);
		}
    }
    
    public synchronized void setNoteCnt(List<HashMap<String,String>> list) {
    	for(int i = 0; i < list.size(); i++){
			if((list.get(i)).get(CommonUtil.NOTE_TYPE).equalsIgnoreCase(CommonUtil.NOTE_TYPE_C))
				Cnt1 = Integer.valueOf((list.get(i)).get(CommonUtil.CNT));
			else
				Cnt2 = Integer.valueOf((list.get(i)).get(CommonUtil.CNT));
		}
	    ((Button) findViewById(R.id.mystudyroom_selectbtn01)).setText("("+Integer.toString(Cnt1)+")");
	    ((Button) findViewById(R.id.mystudyroom_selectbtn02)).setText("("+Integer.toString(Cnt2)+")");
    }
}
