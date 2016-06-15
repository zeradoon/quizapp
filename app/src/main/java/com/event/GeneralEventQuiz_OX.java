package com.event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.common.CommonUtil;
import com.skcc.portal.skmsquiz.R;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class represent OX Choice Question Activity.
 */
public class GeneralEventQuiz_OX  extends Activity{
	
	/**
	 * This method is called once on creation of the GeneralEventQuiz_OX Activity.
	 * It is used for initialization of required variables.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generalevent_quiz_ox);
		Intent intent = getIntent(); 
		intent.getExtras();
		
		String question = intent.getExtras().get(CommonUtil.GENERAL_EVENT_QUESTION).toString();
		TextView tv =((TextView)findViewById(R.id.generalevent_quiz_1_question));
		tv.setText(question);
	}
}