package com.practice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.common.CommonUtil;
import com.skcc.portal.skmsquiz.R;

public class PracticeSelect extends Activity {
	private String m_user_id;
	private String m_comp_cd;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.practice_selectquiz);

	    m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		
		findViewById(R.id.prac_BookmarkQuesBtn).setVisibility(View.GONE);
		
        Button btn_ox = (Button) findViewById(R.id.practice_selectbtn01);
        btn_ox.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
				if(CommonUtil.showNetworkAlert(PracticeSelect.this)) {
					Intent intent = new Intent(PracticeSelect.this, PracticeQuiz.class);
					intent.putExtra(CommonUtil.QUIZ_TYPE, 0);
					intent.putExtra(CommonUtil.SEL_TAB, "1");
					intent.putExtra(CommonUtil.USER_ID, m_user_id);
					intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
					startActivity(intent);
					finish();
				}
			}
        });

        Button btn_objective = (Button) findViewById(R.id.practice_selectbtn02);
        btn_objective.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
				if(CommonUtil.showNetworkAlert(PracticeSelect.this)) {
					Intent intent = new Intent(PracticeSelect.this, PracticeQuiz.class);
					intent.putExtra(CommonUtil.QUIZ_TYPE, 1);
					intent.putExtra(CommonUtil.SEL_TAB, "2");
					intent.putExtra(CommonUtil.USER_ID, m_user_id);
					intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
					startActivity(intent);
					finish();
				}
			}
        });
        
        Button btn_subjective = (Button) findViewById(R.id.practice_selectbtn03);
        btn_subjective.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
				if(CommonUtil.showNetworkAlert(PracticeSelect.this)) {
					Intent intent = new Intent(PracticeSelect.this, PracticeQuiz.class);
					intent.putExtra(CommonUtil.QUIZ_TYPE, 2);
					intent.putExtra(CommonUtil.SEL_TAB, "3");
					intent.putExtra(CommonUtil.USER_ID, m_user_id);
					intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
					startActivity(intent);
					finish();
				}
			}
        });

	    // 메인페이지로 가기
	    ImageView btn_home = (ImageView)findViewById(R.id.btn_home);
	    btn_home.setOnClickListener(new ImageView.OnClickListener() {
			public void onClick(View v) {
//				Intent intent = new Intent(practice_selectquiz.this, QuizMain.class);
//				startActivity(intent);
				finish();
			}
		});

	}

}
