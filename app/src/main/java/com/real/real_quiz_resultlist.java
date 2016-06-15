package com.real;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonUtil;
import com.common.NetworkUtil;
import com.common.async.AbstractAsyncActivity;
import com.mystudy.MyStudyRoom_Common;
import com.mystudy.MyStudyRoom_Tab_Main;
import com.skcc.portal.skmsquiz.R;

public class real_quiz_resultlist extends AbstractAsyncActivity {

	int real_score = 0;
	int resultColor = 0xFFFF0000;
	int black = 0xFF000000;
	int real_jungdab[] = new int[10];
	String real_seq[] = new String[10];
	int real_intseq[] = new int[10];
	private Context m_context = this;
	String phone_num = "";
	String set_score = "";
	HttpURLConnection conn;
	URL url;
	String noteResult = "";
	String seqno = "";
	List<HashMap<String, String>> cNoteList = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> cNoteList2 = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> cNoteMap = new HashMap<String, String>();
	String score_str = "";
	
	public static final String PREFS_NAME = "MyPrefsFile"; 
	private static final String PREF_USERID = "userid";
	private static final String PREF_USERCOMPCD = "usercompcd";
	private String m_comp_cd = ""; 
	private String m_user_id = "";
	
	
	private static final String REAL_QUIZ_SCORE_UPDATE = "realQuiz_score_update.do";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.real_quiz_resultlist);
		
		ImageView m_btn_home = (ImageView)findViewById(R.id.btn_home);
		m_btn_home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
	    
		new ValuesToRealResultAsyncTask().execute(m_user_id);

	} // oncreate닫기

	public void real_insert_mystudyroom() {
		cNoteList = MyStudyRoom_Common.insert_note(real_quiz_resultlist.this, cNoteList2, CommonUtil.NOTE_TYPE_C, getString(R.string.base_uri), m_user_id);

		if (cNoteList.size() > 0) {
			cNoteMap = cNoteList.get(0);

			String regCnt = cNoteMap.get(CommonUtil.REG_CNT); // 신규 등록한 노트수
			String failCnt = cNoteMap.get(CommonUtil.FAIL_CNT); // 등록 실패한 노트수
			String prevRegCnt = cNoteMap.get(CommonUtil.REG_BEFORE_CNT); // 기 등록된 노트 수

			if (!"0".equalsIgnoreCase(regCnt) && "0".equalsIgnoreCase(failCnt)
					&& "0".equals(prevRegCnt)) {
				noteResult = "오답 노트에 저장되었습니다."; // 오답 노트에 저장되었습니다.
			} else if ("0".equalsIgnoreCase(regCnt) && !"0".equalsIgnoreCase(failCnt)
					&& !"0".equalsIgnoreCase(prevRegCnt)) {
				noteResult = "이미 저장된 문제입니다."; // 이미 저장된 문제입니다.
			} else if ("0".equalsIgnoreCase(regCnt) && !"0".equalsIgnoreCase(failCnt)
					&& "0".equalsIgnoreCase(prevRegCnt)) {
				noteResult = "오답 노트가 이미 가득 찼습니다."; // 오답 노트가 이미 가득 찼습니다.
			} else {
				noteResult = "그 외 (예외)"; // 그 외 (예외)
			}
		}

	}

	public void real_result_show() {

		if (real_jungdab[0] == 1) {
			((TextView) findViewById(R.id.real_result_q1))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_result_yn1))
					.setTextColor(ColorStateList.valueOf(resultColor));
			findViewById(R.id.real_goto_mystudyroom1).setVisibility(
					LinearLayout.VISIBLE);
		} else {
			((TextView) findViewById(R.id.real_result_q1)).setText("문제1");
			((TextView) findViewById(R.id.real_result_q1))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_result_yn1)).setText(CommonUtil.ANSWER_CORRECT);
			((TextView) findViewById(R.id.real_result_yn1))
					.setTextColor(ColorStateList.valueOf(black));
			findViewById(R.id.real_goto_mystudyroom1).setVisibility(
					LinearLayout.INVISIBLE);
		}

		if (real_jungdab[1] == 1) {
			((TextView) findViewById(R.id.real_result_q2))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_result_yn2))
					.setTextColor(ColorStateList.valueOf(resultColor));
			findViewById(R.id.real_goto_mystudyroom2).setVisibility(
					LinearLayout.VISIBLE);
		} else {
			((TextView) findViewById(R.id.real_result_q2)).setText("문제2");
			((TextView) findViewById(R.id.real_result_q2))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_result_yn2)).setText(CommonUtil.ANSWER_CORRECT);
			((TextView) findViewById(R.id.real_result_yn2))
					.setTextColor(ColorStateList.valueOf(black));
			findViewById(R.id.real_goto_mystudyroom2).setVisibility(
					LinearLayout.INVISIBLE);
		}

		if (real_jungdab[2] == 1) {
			((TextView) findViewById(R.id.real_result_q3))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_result_yn3))
					.setTextColor(ColorStateList.valueOf(resultColor));
			findViewById(R.id.real_goto_mystudyroom3).setVisibility(
					LinearLayout.VISIBLE);
		} else {
			((TextView) findViewById(R.id.real_result_q3)).setText("문제3");
			((TextView) findViewById(R.id.real_result_q3))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_result_yn3)).setText(CommonUtil.ANSWER_CORRECT);
			((TextView) findViewById(R.id.real_result_yn3))
					.setTextColor(ColorStateList.valueOf(black));
			findViewById(R.id.real_goto_mystudyroom3).setVisibility(
					LinearLayout.INVISIBLE);
		}

		if (real_jungdab[3] == 1) {
			((TextView) findViewById(R.id.real_result_q4))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_result_yn4))
					.setTextColor(ColorStateList.valueOf(resultColor));
			findViewById(R.id.real_goto_mystudyroom4).setVisibility(
					LinearLayout.VISIBLE);
		} else {
			((TextView) findViewById(R.id.real_result_q4)).setText("문제4");
			((TextView) findViewById(R.id.real_result_q4))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_result_yn4)).setText(CommonUtil.ANSWER_CORRECT);
			((TextView) findViewById(R.id.real_result_yn4))
					.setTextColor(ColorStateList.valueOf(black));
			findViewById(R.id.real_goto_mystudyroom4).setVisibility(
					LinearLayout.INVISIBLE);
		}

		if (real_jungdab[4] == 1) {
			((TextView) findViewById(R.id.real_result_q5))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_result_yn5))
					.setTextColor(ColorStateList.valueOf(resultColor));
			findViewById(R.id.real_goto_mystudyroom5).setVisibility(
					LinearLayout.VISIBLE);
		} else {
			((TextView) findViewById(R.id.real_result_q5)).setText("문제5");
			((TextView) findViewById(R.id.real_result_q5))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_result_yn5)).setText(CommonUtil.ANSWER_CORRECT);
			((TextView) findViewById(R.id.real_result_yn5))
					.setTextColor(ColorStateList.valueOf(black));
			findViewById(R.id.real_goto_mystudyroom5).setVisibility(
					LinearLayout.INVISIBLE);
		}

		if (real_jungdab[5] == 1) {
			((TextView) findViewById(R.id.real_result_q6))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_result_yn6))
					.setTextColor(ColorStateList.valueOf(resultColor));
			findViewById(R.id.real_goto_mystudyroom6).setVisibility(
					LinearLayout.VISIBLE);
		} else {
			((TextView) findViewById(R.id.real_result_q6)).setText("문제6");
			((TextView) findViewById(R.id.real_result_q6))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_result_yn6)).setText(CommonUtil.ANSWER_CORRECT);
			((TextView) findViewById(R.id.real_result_yn6))
					.setTextColor(ColorStateList.valueOf(black));
			findViewById(R.id.real_goto_mystudyroom6).setVisibility(
					LinearLayout.INVISIBLE);
		}

		if (real_jungdab[6] == 1) {
			((TextView) findViewById(R.id.real_result_q7))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_result_yn7))
					.setTextColor(ColorStateList.valueOf(resultColor));
			findViewById(R.id.real_goto_mystudyroom7).setVisibility(
					LinearLayout.VISIBLE);
		} else {
			((TextView) findViewById(R.id.real_result_q7)).setText("문제7");
			((TextView) findViewById(R.id.real_result_q7))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_result_yn7)).setText(CommonUtil.ANSWER_CORRECT);
			((TextView) findViewById(R.id.real_result_yn7))
					.setTextColor(ColorStateList.valueOf(black));
			findViewById(R.id.real_goto_mystudyroom7).setVisibility(
					LinearLayout.INVISIBLE);
		}

		if (real_jungdab[7] == 1) {
			((TextView) findViewById(R.id.real_result_q8))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_result_yn8))
					.setTextColor(ColorStateList.valueOf(resultColor));
			findViewById(R.id.real_goto_mystudyroom8).setVisibility(
					LinearLayout.VISIBLE);
		} else {
			((TextView) findViewById(R.id.real_result_q8)).setText("문제8");
			((TextView) findViewById(R.id.real_result_q8))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_result_yn8)).setText(CommonUtil.ANSWER_CORRECT);
			((TextView) findViewById(R.id.real_result_yn8))
					.setTextColor(ColorStateList.valueOf(black));
			findViewById(R.id.real_goto_mystudyroom8).setVisibility(
					LinearLayout.INVISIBLE);
		}

		if (real_jungdab[8] == 1) {
			((TextView) findViewById(R.id.real_result_q9))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_result_yn9))
					.setTextColor(ColorStateList.valueOf(resultColor));
			findViewById(R.id.real_goto_mystudyroom9).setVisibility(
					LinearLayout.VISIBLE);
		} else {
			((TextView) findViewById(R.id.real_result_q9)).setText("문제9");
			((TextView) findViewById(R.id.real_result_q9))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_result_yn9)).setText(CommonUtil.ANSWER_CORRECT);
			((TextView) findViewById(R.id.real_result_yn9))
					.setTextColor(ColorStateList.valueOf(black));
			findViewById(R.id.real_goto_mystudyroom9).setVisibility(
					LinearLayout.INVISIBLE);
		}

		if (real_jungdab[9] == 1) {
			((TextView) findViewById(R.id.real_result_q10))
					.setTextColor(ColorStateList.valueOf(resultColor));
			((TextView) findViewById(R.id.real_result_yn10))
					.setTextColor(ColorStateList.valueOf(resultColor));
			findViewById(R.id.real_goto_mystudyroom10).setVisibility(
					LinearLayout.VISIBLE);
		} else {
			((TextView) findViewById(R.id.real_result_q10)).setText("문제10");
			((TextView) findViewById(R.id.real_result_q10))
					.setTextColor(ColorStateList.valueOf(black));
			((TextView) findViewById(R.id.real_result_yn10)).setText(CommonUtil.ANSWER_CORRECT);
			((TextView) findViewById(R.id.real_result_yn10))
					.setTextColor(ColorStateList.valueOf(black));
			findViewById(R.id.real_goto_mystudyroom10).setVisibility(
					LinearLayout.INVISIBLE);
		}

	}

	public void monclick(View v) {
		Intent intent = new Intent(real_quiz_resultlist.this, MyStudyRoom_Tab_Main.class);
		intent.putExtra(CommonUtil.USER_ID, m_user_id);
		intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
		
		switch (v.getId()) {

			case R.id.real_goto_mystudyroom1:
				
				intent.putExtra(CommonUtil.CHK, 0);
				intent.putExtra(CommonUtil.CURR_SEQ, real_intseq[0]);
				
				startActivity(intent);
				finish();
				break;
			case R.id.real_goto_mystudyroom2:
				
				intent.putExtra(CommonUtil.CHK, 0);
				intent.putExtra(CommonUtil.CURR_SEQ, real_intseq[1]);
				
				startActivity(intent);
				finish();
				break;
			case R.id.real_goto_mystudyroom3:
				
				intent.putExtra(CommonUtil.CHK, 0);
				intent.putExtra(CommonUtil.CURR_SEQ, real_intseq[2]);
				startActivity(intent);
				finish();
				break;
			case R.id.real_goto_mystudyroom4:
				intent.putExtra(CommonUtil.CHK, 0);
				intent.putExtra(CommonUtil.CURR_SEQ, real_intseq[3]);
				startActivity(intent);
				finish();
				break;
	
			case R.id.real_goto_mystudyroom5:
				intent.putExtra(CommonUtil.CHK, 0);
				intent.putExtra(CommonUtil.CURR_SEQ, real_intseq[4]);
				startActivity(intent);
				finish();
				break;
			case R.id.real_goto_mystudyroom6:
				intent.putExtra(CommonUtil.CHK, 0);
				intent.putExtra(CommonUtil.CURR_SEQ, real_intseq[5]);
				startActivity(intent);
				finish();
				break;
			case R.id.real_goto_mystudyroom7:
				intent.putExtra(CommonUtil.CHK, 0);
				intent.putExtra(CommonUtil.CURR_SEQ, real_intseq[6]);
				startActivity(intent);
				finish();
				break;
			case R.id.real_goto_mystudyroom8:
				intent.putExtra(CommonUtil.CHK, 0);
				intent.putExtra(CommonUtil.CURR_SEQ, real_intseq[7]);
				startActivity(intent);
				finish();
				break;
			case R.id.real_goto_mystudyroom9:
				intent.putExtra(CommonUtil.CHK, 0);
				intent.putExtra(CommonUtil.CURR_SEQ, real_intseq[8]);
				startActivity(intent);
				finish();
				break;
			case R.id.real_goto_mystudyroom10:
				intent.putExtra(CommonUtil.CHK, 0);
				intent.putExtra(CommonUtil.CURR_SEQ, real_intseq[9]);
				startActivity(intent);
				finish();
				break;
			case R.id.real_quit:
				finish();
				break;
			case R.id.real_retry: {
				intent = new Intent(real_quiz_resultlist.this,
						real_quiz.class);
				intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
				startActivity(intent);
				finish();
				break;
			}
			case R.id.real_view_ranking: {
				intent = new Intent(real_quiz_resultlist.this,
						com.ranking.RankingReal.class);
				intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
				startActivity(intent);
				finish();
				break;
			}
		}
	}

	private class ValuesToRealResultAsyncTask extends AsyncTask<String, Void, String> {

		protected void onPreExecute() {

			showLoadingProgressDialog();
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			real_jungdab = bundle.getIntArray(CommonUtil.REAL_JUNGDAB);
			real_seq = bundle.getStringArray(CommonUtil.REAL_SEQ);

			// 점수계산 , 오답노트대입
			for (int i = 0; i < 10; i++) {
				if (real_jungdab[i] == 0) {
					real_score = real_score + 10;
				} else {

					cNoteMap = new HashMap<String, String>();
					cNoteMap.put(CommonUtil.SEQ_NO, real_seq[i]);
					cNoteList2.add(cNoteMap);
				}
			}

			for (int i = 0; i < 10; i++) {
				real_intseq[i] = Integer.parseInt(real_seq[i]);

			}

		}

		protected String doInBackground(String... params) {
			String userId = "";
			
	    	if (params[0] != null)
	    		userId = params[0];
	    	
			set_score = Integer.toString(real_score);
			phone_num = NetworkUtil.getPhoneNumber(m_context);

			try {

				url = new URL(getString(R.string.base_uri) + REAL_QUIZ_SCORE_UPDATE + "?" + CommonUtil.USER_ID + "=" + userId + "&" + CommonUtil.REAL_SCORE + "=" + set_score);

				conn = (HttpURLConnection) url.openConnection();

				if (conn != null) {
					conn.setConnectTimeout(7000);
					conn.setUseCaches(false);
					conn.getResponseCode();
					conn.disconnect();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return score_str;

		}

		protected void onPostExecute(String score) {

			dismissProgressDialog();
			real_result_show();
			real_insert_mystudyroom();
			score_str = Integer.toString(real_score) + "점";
			((TextView) findViewById(R.id.real_result_score)).setText(score_str);

		}

	}

} // 클래스 닫기