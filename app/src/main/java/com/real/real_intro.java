package com.real;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.CommonUtil;
import com.common.NetworkUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.skcc.portal.skmsquiz.R;

public class real_intro  extends Activity{
	private String m_user_id;
	private String m_comp_cd;
	private String m_user_dept;
	
	/** Called when the activity is first created. */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.real_intro);
	    
	    m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_user_dept = getIntent().getStringExtra(CommonUtil.USER_DEPT);
	    
	    ImageView m_btn_home = (ImageView)findViewById(R.id.btn_home);
		m_btn_home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	       
	    ImageButton btn_real_start = (ImageButton) findViewById(R.id.real_introbtn_01);
    	btn_real_start.setOnClickListener(new Button.OnClickListener(){
    		public void onClick(View v) {
			
		            Intent intent = new Intent(real_intro.this, real_quiz.class);
		            intent.putExtra(CommonUtil.USER_ID, m_user_id);
		            intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
		            intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
    		        startActivity(intent);
    		        finish();}   			
    	});
    }
}
