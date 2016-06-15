package com.real;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.common.CommonUtil;
import com.skcc.portal.skmsquiz.R;

public class real_quiz_ox  extends Activity{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	     setContentView(R.layout.real_quiz_ox);
	     Intent intent = getIntent(); 
	     int real_cnt=0;
	     //getIntent()메소드는 현재 자신을 호출했던 Intent를 반환
	     //Intent로부터 데이터를 가져온다.
	      intent.getExtras();
	      String question = intent.getExtras().get(CommonUtil.REAL_QUESTION).toString();
	      real_cnt = (Integer) intent.getExtras().get(CommonUtil.REAL_QUESTION_CNT);
	      TextView tv =((TextView)findViewById(R.id.real_quiz_1_question));
	      tv.setText(question);
	      real_cnt ++ ;
	}
}