package com.real;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.common.CommonUtil;
import com.skcc.portal.skmsquiz.R;

public class real_quiz_multiple extends Activity{

    real_intro sel = new real_intro();


public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.real_quiz_multiple);
     Intent intent = getIntent(); 
    //getIntent()메소드는 현재 자신을 호출했던 Intent를 반환
    //Intent로부터 데이터를 가져온다.
     intent.getExtras();
     String question = intent.getExtras().get(CommonUtil.REAL_QUESTION).toString();
     TextView tv =((TextView)findViewById(R.id.real_quiz_2_question));
     tv.setText(question);
 //    sel.real_quiz_activity_select(1 , 1);
 }
    
}
