package com.mystudy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.common.CommonUtil;
import com.common.NetworkUtil;
import com.common.XmlParser;
import com.common.async.AbstractAsyncActivity;
import com.login.UserLogin;
import com.practice.PracticeQuiz;
import com.skcc.portal.skmsquiz.R;

public class MyStudyRoom_Tab_Main extends AbstractAsyncActivity {
	
	
	// 노트별 총 문제수용 변수
	public int correct_cnt;
	public int scrap_cnt;
	private String m_user_id;
	private String m_comp_cd;
	private String m_user_dept;
	
	public static final String PREFS_NAME = "MyPrefsFile"; 
	private static final String PREF_USERID = "userid";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_REMEMBERME = "true";
	private static final String PREF_USERCOMPCD = "usercompcd";
	
	// 현재 문제위치 확인용.
	public int curr_correct_cnt = 1;	// 오답노트
	public int curr_scrap_cnt = 1;	// 스크랩노트
	public int curr_note_seq = 0;	// 바로가기 처리용
	
	// 현재 리스트내 문제위치 확인용.
	public int correct_pos = 0;
	public int scrap_pos = 0;
	
	// 객관식 보기 랜덤으로 뿌리기용 변수
	public String answer[] = {"Q_ANSWER","Q_WR_ANSWER1","Q_WR_ANSWER2","Q_WR_ANSWER3"};
	
	// AysncTask 실행용 변수
	public int chkAsync = 0;
	
	// 퀴즈 데이터 저장용.
	List<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
	List<HashMap<String,String>> answerList = new ArrayList<HashMap<String, String>>();
	
	int chk = 0;
	
	private static final String MY_STUDYROOM = "myStudyRoom.do";
	private static final String MY_STUDYROOM_SEL_QUIZ = "myStudyRoomSelQuiz.do";
	private static final String MY_STUDYROOM_SEL_QUIZ_QUES_ANSWERS = "myStudyRoomSelQuizQuesAnswers.do";
	private static final String MY_STUDYROOM_DEL_QUIZ = "myStudyRoomDelQuiz.do";

	private ImageView m_btn_home;
	
 	@Override
	public void onCreate(Bundle savedInstanceState) {			
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mystudyroom_tab_main);
	    
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
		
	    Intent intent = getIntent();	// Intent를 통해 선택한 탭 정보 전달.
	    chk = intent.getIntExtra(CommonUtil.CHK,0);	// 오답노트 / 스크랩 노트 선택경로 체크.
	    //user_id = NetworkUtil.getPhoneNumber(MyStudyRoom_Tab_Main.this);
	    
	    
	    // 바로가기 위치 확인.
	    if(intent.getIntExtra(CommonUtil.CURR_SEQ,0) != 0)
	    	curr_note_seq = intent.getIntExtra(CommonUtil.CURR_SEQ,0);
	    
	    
	    TabHost tabs= (TabHost) findViewById(R.id.tabhost);
	    tabs.setup();
	    TabHost.TabSpec spec = tabs.newTabSpec(CommonUtil.TAG1);
	    spec.setContent(R.id.tab_main_correct_note);
	    TabHost.TabSpec spec2 = tabs.newTabSpec(CommonUtil.TAG2);
	    spec2.setContent(R.id.tab_main_scrap_note);
	    
	    spec.setIndicator(getString(R.string.mystudyroom_correct_note));
	    tabs.addTab(spec);
	    spec2.setIndicator(getString(R.string.mystudyroom_scrap_note));
	    tabs.addTab(spec2);
	    
	    // 선택한 탭으로 이동.
	    tabs.setCurrentTab(chk);
	    for(int i = 0; i < tabs.getTabWidget().getChildCount(); i++)
	    	tabs.getTabWidget().getChildAt(i).getLayoutParams().height = 64;
	    
	    
	    // 초기 문제수 카운트
	    new setValuesToMyStudyRoomNoteAsyncTask().execute(m_user_id);
	}
 	
 	protected void signOutUser() {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().remove(PREF_USERID).remove(PREF_PASSWORD).remove(PREF_REMEMBERME).commit();
		Intent intent = new Intent(MyStudyRoom_Tab_Main.this, UserLogin.class);	// create intent to invoke user login activity.
		startActivity(intent);	// start the intent to open user login screen.
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mystudyroom, menu);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		
	    switch (item.getItemId()) {
	    	case R.id.home:
	    		intent = new Intent(MyStudyRoom_Tab_Main.this, com.main.QuizMain.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();
	            return true;
	        case R.id.profile:
	        	intent = new Intent(MyStudyRoom_Tab_Main.this, com.profile.ProfileMain.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();
	            return true;
	        case R.id.ranking:
	        	intent = new Intent(MyStudyRoom_Tab_Main.this, com.ranking.RankingSelect.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();

	            return true;
	        case R.id.events:
	        	intent = new Intent(MyStudyRoom_Tab_Main.this, com.event.GeneralEvent.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();

	            return true;
	        case R.id.signout:
	            signOutUser();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
 	private class setValuesToMyStudyRoomNoteAsyncTask extends AsyncTask<String, Void, List> {
    	@Override
    	protected void onPreExecute(){
    		showLoadingProgressDialog();
    	}
    	
		@Override
		protected List doInBackground(String ... params) {
			String userId = "";
			List quizList = new ArrayList();
			
	    	if (params[0] != null)
	    		userId = params[0];
			
			if(chkAsync == 0){
				// 초기 문제수 카운트 체크.
				list = XmlParser.getValuesFromXML(getString(R.string.base_uri) + MY_STUDYROOM + "?" + CommonUtil.USER_ID + "=" + userId);		
				quizList.add(list);
			}else if(chkAsync == 1){
				// 오답노트/스크랩노트 등록 문제정보 호출 및 List에 담기.
				list = XmlParser.getValuesFromXML(getString(R.string.base_uri)+ MY_STUDYROOM_SEL_QUIZ + "?" + CommonUtil.USER_ID + "=" + userId);
				answerList = XmlParser.getValuesFromXML(getString(R.string.base_uri) + MY_STUDYROOM_SEL_QUIZ_QUES_ANSWERS + "?" + CommonUtil.USER_ID + "=" + userId);
				quizList.add(list);
				quizList.add(answerList);
				
			}else if(chkAsync == 2){
				// 오답노트 삭제.
				delete_note(getString(R.string.base_uri) + MY_STUDYROOM_DEL_QUIZ + "?" + CommonUtil.USER_ID + "=" + userId + "&" + CommonUtil.SEQ_NO + "="+ (list.get(correct_pos)).get(CommonUtil.SEQ_NO) + "&" + CommonUtil.NOTE_TYPE + "=" + CommonUtil.NOTE_TYPE_C);
			}else if(chkAsync == 3){
				// 스크랩 노트 삭제.
				delete_note(getString(R.string.base_uri) + MY_STUDYROOM_DEL_QUIZ + "?" + CommonUtil.USER_ID + "=" + userId + "&" + CommonUtil.SEQ_NO + "="+ (list.get(scrap_pos)).get(CommonUtil.SEQ_NO) + "&" + CommonUtil.NOTE_TYPE + "=" + CommonUtil.NOTE_TYPE_S);
			}
			return quizList;
		}

		@Override
		protected void onPostExecute(List list){
			dismissProgressDialog();
			if(chkAsync == 0){
				setNoteStatus(list);
			}else if(chkAsync == 1){
				setNoteInfoStatus(list);
			}else if(chkAsync == 2){
				delete_list(0);
			}else if(chkAsync == 3){
				delete_list(1);
			}
		}
    }
    
    @SuppressWarnings("unchecked")
	public synchronized void setNoteStatus(List quizList) {
    	list = (List<HashMap<String, String>>)quizList.get(0);
    	for(int i = 0; i < list.size(); i++){
			if((((HashMap<String, String>)list.get(i)).get(CommonUtil.NOTE_TYPE).equalsIgnoreCase(CommonUtil.NOTE_TYPE_C)))
				correct_cnt = Integer.valueOf(((HashMap<String, String>)list.get(i)).get(CommonUtil.CNT));
			else
				scrap_cnt = Integer.valueOf(((HashMap<String, String>)list.get(i)).get(CommonUtil.CNT));
		}
    	chkAsync = 1;
    	// 노트 정보 불러오기
    	new setValuesToMyStudyRoomNoteAsyncTask().execute(m_user_id);
    }
 	
    @SuppressWarnings("unchecked")
	public synchronized void setNoteInfoStatus(List quizList) {
    	list = (List<HashMap<String, String>>)quizList.get(0);
    	answerList = (List<HashMap<String, String>>)quizList.get(1);
	    
    	if(correct_cnt == 0)
	    	curr_correct_cnt = 0;
	    if(scrap_cnt == 0)
	    	curr_scrap_cnt = 0;
	    scrap_pos = correct_cnt;
	    
	    // 바로가기 위치로 이동.
	    if(curr_note_seq != 0){
	    	int i;
	    	for(i = 0; i < correct_cnt; i++){
	    		if(Integer.valueOf((list.get(i)).get(CommonUtil.SEQ_NO)) != curr_note_seq){
	    			if(curr_correct_cnt < correct_cnt)
	    				curr_correct_cnt = curr_correct_cnt + 1;
	    			if(correct_cnt >= 0)
	    				if(correct_pos >= 0)
	    					correct_pos = curr_correct_cnt - 1;
	    		}else
	    			break;
	    	}
	    	if(i==correct_cnt){
	    		curr_correct_cnt = 1;
	    		correct_pos = 0;
	    	}
	    }
	    checkCnt();			// 상단 문제 순서 설정
	    note_select2();		// 문제 데이터 세팅
	    button_check();		// 초기 버튼설정

    }
 	
 	
	/*
	 *  버튼 클릭시 동작 수행.
	 */
	public void monclick(View v){
		switch (v.getId()) {
		// ################################################# 오답 노트
		// ############################ 이전버튼 처리
		case R.id.btn_correct_note_before_ox:
		case R.id.btn_correct_note_before_multi:
		case R.id.btn_correct_note_before_essay:
			if(curr_correct_cnt > 1){
				curr_correct_cnt=curr_correct_cnt - 1; // 현재 문제번호 이동
				checkCnt(); // 문제번호 세팅
				button_init(0);
				note_init(0);
				note_select2();
			}
			button_check();
			break;
		// ############################ 다음버튼 처리
		case R.id.btn_correct_note_next_ox:
		case R.id.btn_correct_note_next_multi:
		case R.id.btn_correct_note_next_essay:
			if(curr_correct_cnt < correct_cnt){
				curr_correct_cnt = curr_correct_cnt + 1;
				checkCnt();
				button_init(0);
				note_init(0);
				note_select2();
			}
			button_check();
			break;
		// ############################ 삭제버튼 처리
		case R.id.btn_correct_note_delete_ox:
		case R.id.btn_correct_note_delete_multi:
		case R.id.btn_correct_note_delete_essay:
			chkAsync = 2;
			delete_button(0);
			break;
		// ################################################# 스크랩 노트
		// ############################  이전버튼 처리
		case R.id.btn_scrap_note_before_ox:				
		case R.id.btn_scrap_note_before_multi: 
		case R.id.btn_scrap_note_before_essay:
			if(curr_scrap_cnt > 1){
				curr_scrap_cnt = curr_scrap_cnt - 1; // 현재 문제번호 이동
				checkCnt(); // 문제번호 세팅
				button_init(1);
				note_init(1);
				note_select2();
			}
			button_check();
			break;
		// ############################  다음버튼 처리			
		case R.id.btn_scrap_note_next_ox:
		case R.id.btn_scrap_note_next_multi:
		case R.id.btn_scrap_note_next_essay:
			if(curr_scrap_cnt < scrap_cnt){
				curr_scrap_cnt = curr_scrap_cnt + 1;
				checkCnt();
				button_init(1);
				note_init(1);
				note_select2();
			}
			button_check();
			break;
		// ############################ 삭제버튼 처리			
		case R.id.btn_scrap_note_delete_ox:
		case R.id.btn_scrap_note_delete_multi:
		case R.id.btn_scrap_note_delete_essay:
			chkAsync = 3;
			delete_button(1);
			break;
		// ################################################# HOME
		case R.id.btn_home:
			finish();
			break;
		}
	}
	
	/*
	 *  삭제버튼 클릭시 동작수행
	 *  chk : 0(오답노트) / 1(스크랩노트)
	 */
	public void delete_button(int chk){
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("확인");
        ad.setMessage(R.string.mystudyroom_delete_note);
        if(chk==0){
	        ad.setPositiveButton("예", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss(); // 닫기
					if(correct_cnt >= 1){
						correct_cnt = correct_cnt - 1;
					}
					if(curr_correct_cnt > 1){
						curr_correct_cnt = curr_correct_cnt - 1;
					}else{
						if(correct_cnt == 0)
							curr_correct_cnt = curr_correct_cnt - 1;
					}
					// 삭제 프로세스 실행 AsyncTask
					new setValuesToMyStudyRoomNoteAsyncTask().execute(m_user_id);
				}
			});
        }else{
        	 ad.setPositiveButton("예", new DialogInterface.OnClickListener() {
 				public void onClick(DialogInterface dialog, int which) {
 					dialog.dismiss(); // 닫기
 					if(scrap_cnt >= 1)
 						scrap_cnt = scrap_cnt - 1;
 					if(curr_scrap_cnt > 1)
 						curr_scrap_cnt = curr_scrap_cnt - 1;
 					else{
 						if(scrap_cnt == 0)
 							curr_scrap_cnt = curr_scrap_cnt - 1;
 					}
					// 삭제 프로세스 실행 AsyncTask
 					new setValuesToMyStudyRoomNoteAsyncTask().execute(m_user_id);
 				}
 			});
        }
        ad.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss(); // 닫기
			}
		});
        ad.show();
	}
	
	/*
	 *  탭 상단 문제수 카운트 체크.
	 *  cnt1 : OX.
	 *  cnt2 : 객관식.
	 *  cnt3 : 주관식.
	 */
	public void checkCnt(){
		// Correct Note
		if(correct_cnt >= 0){
			if(correct_pos >= 0)
				correct_pos = curr_correct_cnt - 1;
			TextView correct_cnt1 = (TextView) this.findViewById(R.id.correct_note_cnt1);
		    correct_cnt1.setText(curr_correct_cnt+" / "+correct_cnt+" pages");
		    TextView correct_cnt2 = (TextView) this.findViewById(R.id.correct_note_cnt2);
		    correct_cnt2.setText(curr_correct_cnt+" / "+correct_cnt+" pages");
		    TextView correct_cnt3 = (TextView) this.findViewById(R.id.correct_note_cnt3);
		    correct_cnt3.setText(curr_correct_cnt+" / "+correct_cnt+" pages");
		}
	    // Scrap Note
		if(scrap_cnt > 0){
			if(scrap_pos >= correct_cnt){
				scrap_pos = correct_cnt + curr_scrap_cnt - 1;
			    TextView scrap_cnt1 = (TextView) this.findViewById(R.id.scrap_note_cnt1);
			    scrap_cnt1.setText(curr_scrap_cnt+" / "+scrap_cnt+" pages");
			    TextView scrap_cnt2 = (TextView) this.findViewById(R.id.scrap_note_cnt2);
			    scrap_cnt2.setText(curr_scrap_cnt+" / "+scrap_cnt+" pages");
			    TextView scrap_cnt3 = (TextView) this.findViewById(R.id.scrap_note_cnt3);
			    scrap_cnt3.setText(curr_scrap_cnt+" / "+scrap_cnt+" pages");
			}
		}
	}
    
    /*
	 *  note 삭제.
	 *  init_chk : 0 - 오답노트 / 1 - 스크랩노트
	 */
	public void delete_list(int init_chk){
		if(CommonUtil.showNetworkAlert(MyStudyRoom_Tab_Main.this)){
			if(init_chk == 0){
				list.remove(correct_pos);
				checkCnt();
				button_init(0);
				note_init(0);
				note_select2();
				button_check();
			}else{		
				list.remove(scrap_pos);
				checkCnt();
				button_init(1);
				note_init(1);
				note_select2();
				button_check();
			}
		}
	}
	
	/*
	 *  note 삭제.
	 *  addr : 삭제처리 url.
	 */
	public void delete_note(String addr){
		try {
			URL url = new URL(addr);
    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();

    		if (conn != null) {
    	    	conn.setConnectTimeout(7000);
    			conn.setUseCaches(false);
    			conn.getResponseCode();
    			conn.disconnect();
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	/*
	 *  Before / Next Button 설정
	 *  Layout : Visible -> Gone.
	 *  Button : Gone -> Visible.
	 */
	public void button_check(){
		if(correct_cnt != 0){
			if(curr_correct_cnt <= 1){
				findViewById(R.id.btn_correct_note_before_ox).setVisibility(View.GONE);
			    findViewById(R.id.btn_correct_note_before_multi).setVisibility(View.GONE);
			    findViewById(R.id.btn_correct_note_before_essay).setVisibility(View.GONE);
			}
			if(curr_correct_cnt == correct_cnt){
				findViewById(R.id.btn_correct_note_next_ox).setVisibility(View.GONE);
				findViewById(R.id.btn_correct_note_next_multi).setVisibility(View.GONE);
				findViewById(R.id.btn_correct_note_next_essay).setVisibility(View.GONE);
			}
		}
		if(scrap_cnt != 0){
			if(curr_scrap_cnt <= 1){
				findViewById(R.id.btn_scrap_note_before_ox).setVisibility(View.GONE);
			    findViewById(R.id.btn_scrap_note_before_multi).setVisibility(View.GONE);
			    findViewById(R.id.btn_scrap_note_before_essay).setVisibility(View.GONE);
			}
			if(curr_scrap_cnt == scrap_cnt){
				findViewById(R.id.btn_scrap_note_next_ox).setVisibility(View.GONE);
				findViewById(R.id.btn_scrap_note_next_multi).setVisibility(View.GONE);
				findViewById(R.id.btn_scrap_note_next_essay).setVisibility(View.GONE);
			}
		}
	}
	
	/*
	 *  유형별 문제 노출처리.
	 *  chk_type : 0 - 오답노트 / 1 - 스크랩노트
	 *  q_type : 1 - OX / 2 - 객관식 / 3 - 주관식
	 */
	public void note_select2(){
		Random rgen = new Random();
		int[] cards = new int[4];
		
		if(list.size() == 0){
			findViewById(R.id.blank_note_correct).setVisibility(View.VISIBLE);
			findViewById(R.id.blank_note_scrap).setVisibility(View.VISIBLE);
		}else if(list.size() >= 1){
			if(curr_correct_cnt == 0){
				findViewById(R.id.blank_note_correct).setVisibility(View.VISIBLE);
			}else{
			    if(((list.get(correct_pos)).get(CommonUtil.NOTE_TYPE)).equalsIgnoreCase(CommonUtil.NOTE_TYPE_C)){
		    		if(list.get(correct_pos).get(CommonUtil.QUES_TYPE).equalsIgnoreCase(CommonUtil.QUES_TYPE_OX)){
		    			findViewById(R.id.correct_note_ox).setVisibility(View.VISIBLE);
						((TextView)findViewById(R.id.myhome_correct_ox_content)).setText((list.get(correct_pos)).get(CommonUtil.Q_QUESTION));
						((TextView)findViewById(R.id.myhome_correct_ox_answer)).setText((list.get(correct_pos)).get(CommonUtil.Q_ANSWER));
						((TextView)findViewById(R.id.myhome_correct_ox_explain)).setText((list.get(correct_pos)).get(CommonUtil.Q_EXPLANATION));
					}else if(list.get(correct_pos).get("Q_TYPE").equalsIgnoreCase(CommonUtil.QUES_TYPE_MULTI_CHOICE)){
						findViewById(R.id.correct_note_multi).setVisibility(View.VISIBLE);
						((TextView)findViewById(R.id.myhome_correct_multi_content)).setText((list.get(correct_pos)).get(CommonUtil.Q_QUESTION));
						
						for (int i=0; i<cards.length; i++) {	//--- Initialize the array to the ints 0-3
						    cards[i] = i;
						}
						for (int i=0; i<cards.length; i++) {	//--- Shuffle by exchanging each element randomly
						    int randomPosition = rgen.nextInt(cards.length);
						    int temp = cards[i];
						    cards[i] = cards[randomPosition];
						    cards[randomPosition] = temp;
						}
						
						String quesId = list.get(correct_pos).get(CommonUtil.SEQ_NO);
						List<HashMap<String,String>> lAnswerList = new ArrayList<HashMap<String, String>>();
						
						HashMap<String, String> map = new HashMap<String, String>();
						for(int i=0;i<answerList.size();i++){
							map = answerList.get(i);
							if (quesId.equalsIgnoreCase((String)(map.get(CommonUtil.SEQ_NO))))
									lAnswerList.add(answerList.get(i));
						}
						
						((TextView)findViewById(R.id.myhome_correct_radio_1)).setText(lAnswerList.get(0).get(CommonUtil.Q_ANSWER));
						if(answer[cards[0]].equalsIgnoreCase(CommonUtil.Q_ANSWER))
							((RadioButton)findViewById(R.id.myhome_correct_radio_1)).setChecked(true);
						((TextView)findViewById(R.id.myhome_correct_radio_2)).setText(lAnswerList.get(1).get(CommonUtil.Q_ANSWER));
						if(answer[cards[1]].equalsIgnoreCase(CommonUtil.Q_ANSWER))
							((RadioButton)findViewById(R.id.myhome_correct_radio_2)).setChecked(true);
						((TextView)findViewById(R.id.myhome_correct_radio_3)).setText(lAnswerList.get(2).get(CommonUtil.Q_ANSWER));
						if(answer[cards[2]].equalsIgnoreCase(CommonUtil.Q_ANSWER))
							((RadioButton)findViewById(R.id.myhome_correct_radio_3)).setChecked(true);
						((TextView)findViewById(R.id.myhome_correct_radio_4)).setText(lAnswerList.get(3).get(CommonUtil.Q_ANSWER));
						if(answer[cards[3]].equalsIgnoreCase(CommonUtil.Q_ANSWER))
							((RadioButton)findViewById(R.id.myhome_correct_radio_4)).setChecked(true);
						
						/*
						((TextView)findViewById(R.id.myhome_correct_radio_1)).setText((list.get(correct_pos)).get(answer[cards[0]]));
						if(answer[cards[0]].equals("Q_ANSWER"))
							((RadioButton)findViewById(R.id.myhome_correct_radio_1)).setChecked(true);
						((TextView)findViewById(R.id.myhome_correct_radio_2)).setText((list.get(correct_pos)).get(answer[cards[1]]));
						if(answer[cards[1]].equals("Q_ANSWER"))
							((RadioButton)findViewById(R.id.myhome_correct_radio_2)).setChecked(true);
						((TextView)findViewById(R.id.myhome_correct_radio_3)).setText((list.get(correct_pos)).get(answer[cards[2]]));
						if(answer[cards[2]].equals("Q_ANSWER"))
							((RadioButton)findViewById(R.id.myhome_correct_radio_3)).setChecked(true);
						((TextView)findViewById(R.id.myhome_correct_radio_4)).setText((list.get(correct_pos)).get(answer[cards[3]]));
						if(answer[cards[3]].equals("Q_ANSWER"))
							((RadioButton)findViewById(R.id.myhome_correct_radio_4)).setChecked(true);
						*/
						((TextView)findViewById(R.id.myhome_correct_multi_explain)).setText((list.get(correct_pos)).get(CommonUtil.Q_EXPLANATION));
					}else if(list.get(correct_pos).get(CommonUtil.QUES_TYPE).equalsIgnoreCase(CommonUtil.QUES_TYPE_SHORT_ANSWER)){
						findViewById(R.id.correct_note_essay).setVisibility(View.VISIBLE);
						((TextView)findViewById(R.id.myhome_correct_essay_content)).setText((list.get(correct_pos)).get(CommonUtil.Q_QUESTION));
						((TextView)findViewById(R.id.myhome_correct_essay_answer)).setText((list.get(correct_pos)).get(CommonUtil.Q_ANSWER));
						((TextView)findViewById(R.id.myhome_correct_essay_explain)).setText((list.get(correct_pos)).get(CommonUtil.Q_EXPLANATION));
					}
			    }
			}
			if(curr_scrap_cnt == 0){
				findViewById(R.id.blank_note_scrap).setVisibility(View.VISIBLE);
			}else{
				if(((list.get(scrap_pos)).get(CommonUtil.NOTE_TYPE)).equalsIgnoreCase(CommonUtil.NOTE_TYPE_S)){
					if(list.get(scrap_pos).get(CommonUtil.QUES_TYPE).equalsIgnoreCase(CommonUtil.QUES_TYPE_OX)){
						findViewById(R.id.scrap_note_ox).setVisibility(View.VISIBLE);
						((TextView)findViewById(R.id.myhome_scrap_ox_content)).setText((list.get(scrap_pos)).get(CommonUtil.Q_QUESTION));
						((TextView)findViewById(R.id.myhome_scrap_ox_answer)).setText((list.get(scrap_pos)).get(CommonUtil.Q_ANSWER));
						((TextView)findViewById(R.id.myhome_scrap_ox_explain)).setText((list.get(scrap_pos)).get(CommonUtil.Q_EXPLANATION));
					}else if(list.get(scrap_pos).get(CommonUtil.QUES_TYPE).equalsIgnoreCase(CommonUtil.QUES_TYPE_MULTI_CHOICE)){
						findViewById(R.id.scrap_note_multi).setVisibility(View.VISIBLE);
						((TextView)findViewById(R.id.myhome_scrap_multi_content)).setText((list.get(scrap_pos)).get(CommonUtil.Q_QUESTION));
						
						for (int i=0; i<cards.length; i++) {	//--- Initialize the array to the ints 0-3
						    cards[i] = i;
						}
						for (int i=0; i<cards.length; i++) {	//--- Shuffle by exchanging each element randomly
						    int randomPosition = rgen.nextInt(cards.length);
						    int temp = cards[i];
						    cards[i] = cards[randomPosition];
						    cards[randomPosition] = temp;
						}
						
						String quesId = list.get(scrap_pos).get(CommonUtil.SEQ_NO);
						List<HashMap<String,String>> lAnswerList = new ArrayList<HashMap<String, String>>();
						
						HashMap<String, String> map = new HashMap<String, String>();
						for(int i=0;i<answerList.size();i++){
							map = answerList.get(i);
							if (quesId.equals((String)(map.get(CommonUtil.SEQ_NO))))
									lAnswerList.add(answerList.get(i));
						}
						
						/*((TextView)findViewById(R.id.myhome_scrap_radio_1)).setText((list.get(scrap_pos)).get(answer[cards[0]]));
						if(answer[cards[0]].equals("Q_ANSWER"))
							((RadioButton)findViewById(R.id.myhome_scrap_radio_1)).setChecked(true);
						((TextView)findViewById(R.id.myhome_scrap_radio_2)).setText((list.get(scrap_pos)).get(answer[cards[1]]));
						if(answer[cards[1]].equals("Q_ANSWER"))
							((RadioButton)findViewById(R.id.myhome_scrap_radio_2)).setChecked(true);
						((TextView)findViewById(R.id.myhome_scrap_radio_3)).setText((list.get(scrap_pos)).get(answer[cards[2]]));
						if(answer[cards[2]].equals("Q_ANSWER"))
							((RadioButton)findViewById(R.id.myhome_scrap_radio_3)).setChecked(true);
						((TextView)findViewById(R.id.myhome_scrap_radio_4)).setText((list.get(scrap_pos)).get(answer[cards[3]]));
						if(answer[cards[3]].equals("Q_ANSWER"))
							((RadioButton)findViewById(R.id.myhome_scrap_radio_4)).setChecked(true);
						*/
						
						((TextView)findViewById(R.id.myhome_scrap_radio_1)).setText(lAnswerList.get(0).get(CommonUtil.Q_ANSWER));
						if(answer[cards[0]].equalsIgnoreCase(CommonUtil.Q_ANSWER))
							((RadioButton)findViewById(R.id.myhome_scrap_radio_1)).setChecked(true);
						
						((TextView)findViewById(R.id.myhome_scrap_radio_2)).setText(lAnswerList.get(1).get(CommonUtil.Q_ANSWER));
						if(answer[cards[1]].equalsIgnoreCase(CommonUtil.Q_ANSWER))
							((RadioButton)findViewById(R.id.myhome_scrap_radio_2)).setChecked(true);
						
						((TextView)findViewById(R.id.myhome_scrap_radio_3)).setText(lAnswerList.get(2).get(CommonUtil.Q_ANSWER));
						if(answer[cards[2]].equalsIgnoreCase(CommonUtil.Q_ANSWER))
							((RadioButton)findViewById(R.id.myhome_scrap_radio_3)).setChecked(true);
						
						((TextView)findViewById(R.id.myhome_scrap_radio_4)).setText(lAnswerList.get(3).get(CommonUtil.Q_ANSWER));
						if(answer[cards[3]].equalsIgnoreCase(CommonUtil.Q_ANSWER))
							((RadioButton)findViewById(R.id.myhome_scrap_radio_4)).setChecked(true);
						
						
						((TextView)findViewById(R.id.myhome_scrap_multi_explain)).setText((list.get(scrap_pos)).get(CommonUtil.Q_EXPLANATION));
					}else if(list.get(scrap_pos).get(CommonUtil.QUES_TYPE).equalsIgnoreCase(CommonUtil.QUES_TYPE_SHORT_ANSWER)){
						findViewById(R.id.scrap_note_essay).setVisibility(View.VISIBLE);
						((TextView)findViewById(R.id.myhome_scrap_essay_content)).setText((list.get(scrap_pos)).get(CommonUtil.Q_QUESTION));
						((TextView)findViewById(R.id.myhome_scrap_essay_answer)).setText((list.get(scrap_pos)).get(CommonUtil.Q_ANSWER));
						((TextView)findViewById(R.id.myhome_scrap_essay_explain)).setText((list.get(scrap_pos)).get(CommonUtil.Q_EXPLANATION));
					}
				}
			}    
		}
	}
	
	/*
	 *  View 초기화.
	 *  Layout : Visible -> Gone.
	 *  Button : Gone -> Visible.
	 *  init_chk : 0 - 오답노트 / 1 - 스크랩노트
	 */
	public void note_init(int init_chk){
		if(init_chk == 0){
			findViewById(R.id.correct_note_ox).setVisibility(View.GONE);
			findViewById(R.id.correct_note_multi).setVisibility(View.GONE);
			findViewById(R.id.correct_note_essay).setVisibility(View.GONE);
		}else{
			findViewById(R.id.scrap_note_ox).setVisibility(View.GONE);
			findViewById(R.id.scrap_note_multi).setVisibility(View.GONE);
			findViewById(R.id.scrap_note_essay).setVisibility(View.GONE);
		}
	}
	
	/*
	 *  Button 초기화.
	 *  Layout : Visible -> Gone.
	 *  Button : Gone -> Visible.
	 *  btn_chk : 0 - 오답노트 / 1 - 스크랩노트
	 */
	public void button_init(int btn_chk){
		if(btn_chk == 0){
			findViewById(R.id.btn_correct_note_before_ox).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_correct_note_before_essay).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_correct_note_before_multi).setVisibility(View.VISIBLE);
			
			findViewById(R.id.btn_correct_note_next_ox).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_correct_note_next_essay).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_correct_note_next_multi).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.btn_scrap_note_before_ox).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_scrap_note_before_essay).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_scrap_note_before_multi).setVisibility(View.VISIBLE);
			
			findViewById(R.id.btn_scrap_note_next_ox).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_scrap_note_next_essay).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_scrap_note_next_multi).setVisibility(View.VISIBLE);
		}
	}
}