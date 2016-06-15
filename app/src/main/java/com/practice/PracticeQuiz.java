package com.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Semaphore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonUtil;
import com.common.XmlParser;
import com.common.async.AbstractAsyncActivity;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.login.UserLogin;
import com.main.QuizMain;
import com.mystudy.MyStudyRoom_Common;
import com.skcc.portal.skmsquiz.R;

public class PracticeQuiz extends AbstractAsyncActivity {
	/* PracticeQuiz class context */
	private Context m_context = this;
	
	int solvingCount = 0;
	
	private String m_user_id;
	private String m_user_dept;
	private String m_comp_cd;
	
	public static final String PREFS_NAME = "MyPrefsFile"; 
	private static final String PREF_USERID = "userid";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_REMEMBERME = "true";
	
	private static final String PRACTICE_QUIZ = "practiceQuiz.do";
	private static final String PRACTICE_QUIZ_ANSWERS = "practiceQuizAnswers.do";
	private static final String PRACTICE_QUIZ_COUNT = "practiceQuizCnt.do";
	private static final String INSERT_PRACTICE_BOOKMARK = "insertPracticeBookmark.do";
	private static final String GET_PRACTICE_BOOKMARKS = "getPracticeBookmarks.do";
	
	private static final int PRACTICE_QUIZ_ALERT_FINISH = 1;
	
	private static TabHost tabhost;
	private static String curTab = "1";
	private static int tab1RowNum = 1;
	private static int tab2RowNum = 1;
	private static int tab3RowNum = 1;
	
	private static int tab1TotalQues = 1;
	private static int tab2TotalQues = 1;
	private static int tab3TotalQues = 1;
	
	private static int quizType = 0;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.practice_tab_main);	    

	    initializeActivity();
	}

	protected void initializeActivity() {
		TabHost tabs = (TabHost)findViewById(R.id.tabhost);
	    TabHost.TabSpec spec;
	    tabs.setup();

	    spec = tabs.newTabSpec(CommonUtil.TAG1);
	    spec.setContent(R.id.tab1);
	    spec.setIndicator(CommonUtil.QUES_TYPE_OX_TEXT);
	    tabs.addTab(spec);

	    spec = tabs.newTabSpec(CommonUtil.TAG2);
	    spec.setContent(R.id.tab2);
	    spec.setIndicator(CommonUtil.QUES_TYPE_MULTI_CHOICE);
	    tabs.addTab(spec);
	    
	    spec = tabs.newTabSpec(CommonUtil.TAG3);
	    spec.setContent(R.id.tab3);
	    spec.setIndicator(CommonUtil.QUES_TYPE_SHORT_ANSWER);
	    tabs.addTab(spec);
	    
	    tabhost = tabs;
	    
	    m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_user_dept = getIntent().getStringExtra(CommonUtil.USER_DEPT);
		quizType = getIntent().getIntExtra(CommonUtil.QUIZ_TYPE, 0);
		curTab = getIntent().getStringExtra(CommonUtil.SEL_TAB);
		
	    tabs.setCurrentTab(quizType);
	    
	    
        // 상단 탭 영역 높이 축소를 위한 설정
	    for(int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
	    	tabs.getTabWidget().getChildAt(i).getLayoutParams().height = 64;
	    	tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
	    	       @Override
	    	      public void onTabChanged(String arg0) {
	    	       if (tabhost.getCurrentTabTag().equals(CommonUtil.TAG1)) {
		            	curTab = "1";
		            	((EditText)findViewById(R.id.etNavQuesNo)).setText("");
		            	new setValuesToQuizAsyncTask().execute("1", m_comp_cd, String.valueOf(tab1RowNum));
		            } else if (tabhost.getCurrentTabTag().equals(CommonUtil.TAG2)) {
		            	curTab = "2";
		            	((EditText)findViewById(R.id.etNavQuesNo)).setText("");
		            	new setValuesToQuizAsyncTask().execute("2", m_comp_cd, String.valueOf(tab2RowNum));
		            } else if (tabhost.getCurrentTabTag().equals(CommonUtil.TAG3)) {
		            	curTab = "3";
		            	((EditText)findViewById(R.id.etNavQuesNo)).setText("");
		            	new setValuesToQuizAsyncTask().execute("3", m_comp_cd, String.valueOf(tab3RowNum));
		            }
	    	      }  
	    	});
	    }
	    
	    setUserBoomarks();

    	Button prac_NavQuesBtn = (Button)findViewById(R.id.prac_NavQuesBtn);
    	prac_NavQuesBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(CommonUtil.showNetworkAlert(PracticeQuiz.this)) {
					String navQues = ((EditText)findViewById(R.id.etNavQuesNo)).getText().toString();
					if (!navQues.equalsIgnoreCase("")) {
						int curRowNum =  tab1RowNum;
						if (curTab.equalsIgnoreCase("1")) {
							tab1RowNum = Integer.parseInt(((EditText)findViewById(R.id.etNavQuesNo)).getText().toString());
							if (tab1RowNum <= tab1TotalQues) {
								new setValuesToQuizAsyncTask().execute(curTab, m_comp_cd, String.valueOf(tab1RowNum));
							} else {
								tab1RowNum = curRowNum;
								((EditText)findViewById(R.id.etNavQuesNo)).setText("");
								Toast.makeText(m_context, CommonUtil.INVALID_QUES_NUMBER, Toast.LENGTH_SHORT).show();
							}
						} else if (curTab.equalsIgnoreCase("2")) {
							tab2RowNum = Integer.parseInt(((EditText)findViewById(R.id.etNavQuesNo)).getText().toString());
							if (tab2RowNum <= tab2TotalQues) {
								new setValuesToQuizAsyncTask().execute(curTab, m_comp_cd, String.valueOf(tab2RowNum));
							} else {
								tab1RowNum = curRowNum;
								((EditText)findViewById(R.id.etNavQuesNo)).setText("");
								Toast.makeText(m_context, CommonUtil.INVALID_QUES_NUMBER, Toast.LENGTH_SHORT).show();
							}
						} else if (curTab.equalsIgnoreCase("3")) {
							tab3RowNum = Integer.parseInt(((EditText)findViewById(R.id.etNavQuesNo)).getText().toString());
							if (tab3RowNum <= tab3TotalQues) {
								new setValuesToQuizAsyncTask().execute(curTab, m_comp_cd, String.valueOf(tab3RowNum));
							} else {
								tab1RowNum = curRowNum;
								((EditText)findViewById(R.id.etNavQuesNo)).setText("");
								Toast.makeText(m_context, CommonUtil.INVALID_QUES_NUMBER, Toast.LENGTH_SHORT).show();
							}
						}
					}
					
					android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) m_context.getSystemService(Context.INPUT_METHOD_SERVICE);         
					imm.hideSoftInputFromWindow(PracticeQuiz.this.getWindow().getCurrentFocus().getWindowToken(), 0); 
				}
			}
		});
    	
    	Button prac_BookmarkQuesBtn = (Button)findViewById(R.id.prac_BookmarkQuesBtn);
    	prac_BookmarkQuesBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if(CommonUtil.showNetworkAlert(PracticeQuiz.this)) {
					if (curTab.equalsIgnoreCase("1")) {
						saveBookMark(curTab, m_user_id, String.valueOf(tab1RowNum));
					} else if (curTab.equalsIgnoreCase("2")) {
						saveBookMark(curTab, m_user_id, String.valueOf(tab2RowNum));
					} else if (curTab.equalsIgnoreCase("3")) {
						saveBookMark(curTab, m_user_id, String.valueOf(tab3RowNum));
					}
				}
			}

			
		});
    	
    	
        /******* 1. OX유형 퀴즈 처리 부분 *******/
	    // O 버튼
		Button btn_o = (Button)findViewById(R.id.practice_quiz_1_btn_o);
        btn_o.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
				if(CommonUtil.showNetworkAlert(PracticeQuiz.this)) {
					((TextView)findViewById(R.id.practice_quiz_1_your_answer)).setText(CommonUtil.QUES_TYPE_OX_ANSWER_O);
					new updateQuizResultAsyncTask().execute("1", m_user_id);
				}
			}
		});
        // X 버튼
		Button btn_x = (Button)findViewById(R.id.practice_quiz_1_btn_x);
        btn_x.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
				if(CommonUtil.showNetworkAlert(PracticeQuiz.this)) {
					((TextView)findViewById(R.id.practice_quiz_1_your_answer)).setText(CommonUtil.QUES_TYPE_OX_ANSWER_X);
					new updateQuizResultAsyncTask().execute("1", m_user_id);
				}
			}
		});
        // 스크랩 버튼
	    Button btn_1_snote = (Button)findViewById(R.id.practice_result_1_snote);
	    btn_1_snote.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
				if(CommonUtil.showNetworkAlert(PracticeQuiz.this)) {
			    	new insertScrapNoteAsyncTask().execute("1", m_user_id);
				}
			}
		});
        // 퀴즈 계속 버튼
	    Button btn_1_cont = (Button)findViewById(R.id.practice_result_1_cont);
	    btn_1_cont.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
				if(CommonUtil.showNetworkAlert(PracticeQuiz.this)) {
					tab1RowNum += 1;
			    	new setValuesToQuizAsyncTask().execute("1", m_comp_cd, String.valueOf(tab1RowNum));
				}
			}
		});
        // 퀴즈 종료 버튼
	    Button btn_1_quit = (Button)findViewById(R.id.practice_result_1_quit);
	    btn_1_quit.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) { goMainActivity(); } });
	    
	    

	    /******* 2. 객관식 유형 퀴즈 처리 부분 *******/
	    // Radio 버튼 처리
	    RadioGroup rGroup = (RadioGroup)findViewById(R.id.practice_quiz_2_radiogroup);
	    rGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    	public void onCheckedChanged(RadioGroup rGroup, int id){
	    		RadioButton rButton;
	    		String      selectedAnswer = "";
	    		if (id != -1){	// 라디오 선택지가 클리어 되었을때 (.clearCheck()호출시) -1을 리턴하므로, if문이 없을경우 NullPointerException발생
	    			rButton        = (RadioButton)findViewById(id);
	    			selectedAnswer = rButton.getText().toString();

	    			 // 명시적으로 Radio버튼을 선택하지 않거나 Clear되는 경우에도 본 Listener가 호출되므로, 원치 않는 빈값 입력을 피하려면 if문 안에 아래문장 위치 
	    			((TextView)findViewById(R.id.practice_quiz_2_your_answer)).setText(selectedAnswer);
	    		}
	    	}
	    });
	    // 정답확인 버튼
		Button btn_2 = (Button) findViewById(R.id.practice_quiz_2_submit);
		btn_2.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				RadioGroup rGroup = (RadioGroup)findViewById(R.id.practice_quiz_2_radiogroup);

				if (rGroup.getCheckedRadioButtonId() != -1) {
					// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
					if(CommonUtil.showNetworkAlert(PracticeQuiz.this)) {
						new updateQuizResultAsyncTask().execute("2", m_user_id);
						rGroup.clearCheck();	// 정답 확인 이후에 다음문제를 위해 라디오 선택지 클리어 (이때 자동으로 OnCheckedChangeListener 호출됨)
					}
				} else {
					CommonUtil.showAlert(PracticeQuiz.this, CommonUtil.ALERT_TITLE_NOTICE,
							CommonUtil.ALERT_MESSAGE_OBJECTIVE, CommonUtil.ALERT_POSITIVE_BUTTON);
				}
			}
		});
        // 스크랩 버튼
	    Button btn_2_snote = (Button)findViewById(R.id.practice_result_2_snote);
	    btn_2_snote.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
				if(CommonUtil.showNetworkAlert(PracticeQuiz.this)) {
			    	new insertScrapNoteAsyncTask().execute("2", m_user_id);
				}
			}
		});		
		// 퀴즈 계속 버튼
	    Button btn_2_cont = (Button)findViewById(R.id.practice_result_2_cont);
	    btn_2_cont.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
				if(CommonUtil.showNetworkAlert(PracticeQuiz.this)) {
					tab2RowNum += 1;
			    	new setValuesToQuizAsyncTask().execute("2", m_comp_cd, String.valueOf(tab2RowNum));
				}
			}
		});
        // 퀴즈 종료 버튼
	    Button btn_2_quit = (Button)findViewById(R.id.practice_result_2_quit);
	    btn_2_quit.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) { goMainActivity(); } });
	    
        
	    
	    /******* 3. 주관식 유형 퀴즈 처리 부분 *******/
	    // 정답확인 버튼
		Button btn_3 = (Button) findViewById(R.id.practice_quiz_3_submit);
        btn_3.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				EditText et = (EditText)findViewById(R.id.practice_quiz_3_your_answer);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);

				if (!"".equals( et.getText().toString() )) {
					// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
					if(CommonUtil.showNetworkAlert(PracticeQuiz.this)) {
						new updateQuizResultAsyncTask().execute("3", m_user_id);
					}
				} else {
					CommonUtil.showAlert(PracticeQuiz.this, CommonUtil.ALERT_TITLE_NOTICE,
							CommonUtil.ALERT_MESSAGE_SUBJECTIVE, CommonUtil.ALERT_POSITIVE_BUTTON);
				}
			}
		});
        // 스크랩 버튼
	    Button btn_3_snote = (Button)findViewById(R.id.practice_result_3_snote);
	    btn_3_snote.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
				if(CommonUtil.showNetworkAlert(PracticeQuiz.this)) {
			    	new insertScrapNoteAsyncTask().execute("3", m_user_id);
				}
			}
		});
        // 퀴즈 계속 버튼
	    Button btn_3_cont = (Button)findViewById(R.id.practice_result_3_cont);
	    btn_3_cont.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// 네트워크 사용 가능 true 일 경우에만 진행, false 일 경우 alert 표시
				if(CommonUtil.showNetworkAlert(PracticeQuiz.this)) {
					tab3RowNum += 1;
			    	new setValuesToQuizAsyncTask().execute("3", m_comp_cd, String.valueOf(tab3RowNum));
				}
			}
		});
        // 퀴즈 종료 버튼
	    Button btn_3_quit = (Button)findViewById(R.id.practice_result_3_quit);
	    btn_3_quit.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) { goMainActivity(); } });
        

	    
	    /******* 4. 공통영역 처리 부분 *******/
	    ImageView btn_home = (ImageView)findViewById(R.id.btn_home);
	    btn_home.setOnClickListener(new ImageView.OnClickListener() {
			public void onClick(View v) { 
				goMainActivity(); 
			} 
		});
	    
	}

	protected void saveBookMark(String curTab, String m_user_id, String rowNum) {
		String url = getString(R.string.base_uri) + INSERT_PRACTICE_BOOKMARK;
		
		int seqnoId      = getResources().getIdentifier("practice_quiz_"   + curTab + "_seqno", "id", getPackageName());
		String seqno      = ((TextView)findViewById(seqnoId)).getText().toString();
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_ID, m_user_id);
		parameters.put(CommonUtil.QUES_TYPE, curTab);
		parameters.put(CommonUtil.QUESTION_ID, seqno);
		
		if (curTab.equalsIgnoreCase("1")) {
			parameters.put(CommonUtil.ROWNUM, String.valueOf(tab1RowNum));
		} else if (curTab.equalsIgnoreCase("2")) {
			parameters.put(CommonUtil.ROWNUM, String.valueOf(tab2RowNum));
		} else if (curTab.equalsIgnoreCase("3")) {
			parameters.put(CommonUtil.ROWNUM, String.valueOf(tab3RowNum));
		}
		
		Context context = m_context;
		boolean progressVisible = true;
		int id = 222;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {
			@Override
			public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list, int id) {
				ArrayList<HashMap<String, String>> m_ResultList = list;
				Toast.makeText(m_context, CommonUtil.BOOKMARK_SAVED_SUCCESSFULLY, Toast.LENGTH_SHORT).show();
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url, parameters, id, context, callable, progressVisible); 
		task.execute();
	}

	protected void signOutUser() {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().remove(PREF_USERID).remove(PREF_PASSWORD).remove(PREF_REMEMBERME).commit();
		Intent intent = new Intent(PracticeQuiz.this, UserLogin.class);	// create intent to invoke user login activity.
		startActivity(intent);	// start the intent to open user login screen.
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.practicequiz, menu);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Bundle args = new Bundle();
		
	    switch (item.getItemId()) {
	    	case R.id.home:
	        	args = new Bundle();
	        	args.putString(CommonUtil.IS_DIALOG_HAS_PARAMS, CommonUtil.BOOLEAN_TRUE);
	        	args.putString(CommonUtil.ACTIVITY_CLASS, CommonUtil.MAIN_ACTIVITY_CLASS);
	        	
	        	showDialog(PRACTICE_QUIZ_ALERT_FINISH, args);
	            return true;
	        case R.id.profile:
	        	args = new Bundle();
	        	args.putString(CommonUtil.IS_DIALOG_HAS_PARAMS, CommonUtil.BOOLEAN_TRUE);
	        	args.putString(CommonUtil.ACTIVITY_CLASS, CommonUtil.PROFILE_ACTIVITY_CLASS);
	        	
	        	showDialog(PRACTICE_QUIZ_ALERT_FINISH, args);
	            return true;
	        case R.id.ranking:
	        	args = new Bundle();
	        	args.putString(CommonUtil.IS_DIALOG_HAS_PARAMS, CommonUtil.BOOLEAN_TRUE);
	        	args.putString(CommonUtil.ACTIVITY_CLASS, CommonUtil.RANKING_ACTIVITY_CLASS);
	        	
	        	showDialog(PRACTICE_QUIZ_ALERT_FINISH, args);
	            return true;
	        case R.id.events:
	        	args = new Bundle();
	        	args.putString(CommonUtil.IS_DIALOG_HAS_PARAMS, CommonUtil.BOOLEAN_TRUE);
	        	args.putString(CommonUtil.ACTIVITY_CLASS, CommonUtil.GENERAL_EVENT_ACTIVITY_CLASS);
	        	
	        	showDialog(PRACTICE_QUIZ_ALERT_FINISH, args);
	            return true;
	        case R.id.bookmark:
	        	if (curTab.equalsIgnoreCase("1")) {
					saveBookMark(curTab, m_user_id, String.valueOf(tab1RowNum));
				} else if (curTab.equalsIgnoreCase("2")) {
					saveBookMark(curTab, m_user_id, String.valueOf(tab2RowNum));
				} else if (curTab.equalsIgnoreCase("3")) {
					saveBookMark(curTab, m_user_id, String.valueOf(tab3RowNum));
				}
	            return true;
	        case R.id.signout:
	            signOutUser();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void setUserBoomarks() {
		String url = getString(R.string.base_uri) + GET_PRACTICE_BOOKMARKS;

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_ID, m_user_id);
		parameters.put(CommonUtil.COMP_CD, m_comp_cd);
		
		Context context = m_context;
		boolean progressVisible = true;
		boolean progressCancellable = false;
		int id = 222;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {
			@Override
			public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list, int id) {
				ArrayList<HashMap<String, String>> m_ResultList = list;
				String bookmarkQuesSeqNo = "";
				String quesType = "";
				
				for(int i=0;i<m_ResultList.size();i++){
					bookmarkQuesSeqNo = String.valueOf(list.get(i).get(CommonUtil.BOOKMARK_QUES_ROWNUM));
					quesType = String.valueOf(list.get(i).get(CommonUtil.QUES_TYPE));
					
					if (!bookmarkQuesSeqNo.equalsIgnoreCase("") && quesType.equalsIgnoreCase(CommonUtil.QUES_TYPE_OX)) {
						tab1RowNum = Integer.parseInt(bookmarkQuesSeqNo);
					} else if (!bookmarkQuesSeqNo.equalsIgnoreCase("") && quesType.equalsIgnoreCase(CommonUtil.QUES_TYPE_MULTI_CHOICE)) {
						tab2RowNum = Integer.parseInt(bookmarkQuesSeqNo);
					} else if (!bookmarkQuesSeqNo.equalsIgnoreCase("") && quesType.equalsIgnoreCase(CommonUtil.QUES_TYPE_SHORT_ANSWER)) {
						tab3RowNum = Integer.parseInt(bookmarkQuesSeqNo);
					}
				}
				
				initializePracticeQues();
				
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url, parameters, id, context, callable, progressVisible, progressCancellable); 
		task.execute();
	}

	protected void initializePracticeQues() {
		if (quizType == 0 ) {
			new setValuesToQuizAsyncTask().execute("1", m_comp_cd, String.valueOf(tab1RowNum));
		} else if (quizType == 1 ) {
			new setValuesToQuizAsyncTask().execute("2", m_comp_cd, String.valueOf(tab2RowNum));
		} else if (quizType == 2 ) {
			new setValuesToQuizAsyncTask().execute("3", m_comp_cd, String.valueOf(tab3RowNum));
		} 
	}



	// 메인화면 액티비티 호출
	public void goMainActivity(){
//		Intent intent = new Intent(practice_tab_main.this, QuizMain.class);
//		startActivity(intent);
		showDialog(PRACTICE_QUIZ_ALERT_FINISH);
	}
	
	/**
	 * This method is used to alert quiz finish dialog box when escape key is pressed.
	 * 
	 * @param keyCode
	 * @param event
	 * 
	 * @return boolean
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog(PRACTICE_QUIZ_ALERT_FINISH);

			return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * This method is used to create quiz finish dialog box when escape key is pressed.
	 * 
	 * @param id
	 * 
	 * @return Dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle savedDialogInstanceState) {
		Dialog dialog;
		
		switch (id) {
			case PRACTICE_QUIZ_ALERT_FINISH:
				dialog = showPracticeQuizAlertFinish(savedDialogInstanceState);
				break;
			default:
				dialog = null;
		}
		
		return dialog;
	}
	
	/**
	 * This method is used to display quiz finish dialog box when escape/home key is pressed.
	 * 
	 * @return Dialog
	 */
	private Dialog showPracticeQuizAlertFinish(final Bundle savedDialogInstanceState) {
		Dialog dialog;
		AlertDialog.Builder builder;
		
		builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.sk_logo);
		builder.setTitle(R.string.practicequiz_alert_finish_title);
		builder.setMessage(R.string.practicequiz_alert_finish_message);
		builder.setPositiveButton(
				R.string.practicequiz_alert_finish_positive_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (savedDialogInstanceState != null) {
							String hasParams = savedDialogInstanceState.getString(CommonUtil.IS_DIALOG_HAS_PARAMS);
							String className = savedDialogInstanceState.getString(CommonUtil.ACTIVITY_CLASS);
							
							Intent intent = null;
							Bundle args = new Bundle();
							
							if (hasParams != null) {
								try {
									intent = new Intent(PracticeQuiz.this, Class.forName(className));
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
					        	
								intent.putExtra(CommonUtil.USER_ID, m_user_id);
								intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
								intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
					        	
								startActivity(intent);
								dialog.dismiss(); // 닫기
								finish();
							}
						} else {
							dialog.dismiss(); // 닫기
							finish();
						}
						
					}
				});
		builder.setNegativeButton(
				R.string.practicequiz_alert_finish_negative_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss(); // 닫기
					}
				});
		dialog = builder.create();
		return dialog;
	}


	/*
	 * 문제 유형별 새 문제 세팅 : setValuesToQuiz 에 대한 AsyncTask 수행 클래스
	 * 참고 : http://tigerwoods.tistory.com/28
	 */
    private class setValuesToQuizAsyncTask extends AsyncTask<String, Void, HashMap<String, ?>> {
    	@Override
    	protected void onPreExecute(){
    		showLoadingProgressDialog();
    	}
    	
		@Override
		protected HashMap<String, ?> doInBackground(String ... params) {
	    	String qType = "";
	    	String compCd = "";
	    	String rowNum = "1";
	    	
	    	HashMap quizMap = null;
	    	
	    	if (params[0] != null)
	    		qType = params[0];
	    	if (params[1] != null)
	    		compCd = params[1];
	    	if (params[2] != null)
	    		rowNum = params[2];

			List<HashMap<String,String>> quizList = new ArrayList<HashMap<String, String>>();
			List<HashMap<String,String>> answerList = new ArrayList<HashMap<String, String>>();
	    	
			HashMap<String, String> quesMap = new HashMap<String, String>();
			
	    	quizList = XmlParser.getValuesFromXML(getString(R.string.base_uri) + PRACTICE_QUIZ + "?" + CommonUtil.QUES_TYPE + "=" + qType + "&" + CommonUtil.COMP_CD + "=" + compCd + "&" + CommonUtil.ROWNUM + "=" + rowNum);
	    	String SeqNo = "";
	    	String msgCode = "";
			
			if (quizList.size() > 0) {
				for (int i = 0; i < quizList.size(); i++) {
					if (quizList.get(i) != null)
						msgCode = quizList.get(i).get(CommonUtil.MESSAGE);
				}
				if (msgCode!= null && msgCode.equalsIgnoreCase(CommonUtil.FAILURE)) {
					//CommonUtil.showAlert(m_context, "", "No Such Question available", CommonUtil.ALERT_POSITIVE_BUTTON);

				}
				else {
					quesMap = quizList.get(0);
					SeqNo = quesMap.get(CommonUtil.SEQ_NO);
					answerList = XmlParser.getValuesFromXML(getString(R.string.base_uri) + PRACTICE_QUIZ_ANSWERS + "?" + CommonUtil.SEQ_NO + "=" + SeqNo);
					quizMap = new HashMap();
					quizMap.put(CommonUtil.QUES_MAP, quesMap);
					quizMap.put(CommonUtil.ANSWER_LIST, answerList);
					quizMap.put(CommonUtil.ROWNUM, rowNum);
				}
			}
			
	    	return quizMap;
		}

		@Override
		protected void onPostExecute(HashMap<String, ?> quizMap){
			if (quizMap != null) {
				setValuesToQuiz(quizMap);
			}
			
			dismissProgressDialog();
		}
    }
	
	
	// 문제 유형별 새 문제 세팅
    public void setValuesToQuiz(HashMap<String, ?> quizMap) {
    	@SuppressWarnings("unchecked")
		HashMap<String,String> map = (HashMap<String,String>)quizMap.get(CommonUtil.QUES_MAP);
    	List<HashMap<String,String>> answerList = (List<HashMap<String,String>>) quizMap.get(CommonUtil.ANSWER_LIST);
    	int rowNum = (Integer.parseInt(quizMap.get(CommonUtil.ROWNUM).toString()));
		String quesType = map.get(CommonUtil.QUES_TYPE);
		String qType = "";

        // 1. 퀴즈 유형별 별도 처리할 부분
        if (CommonUtil.QUES_TYPE_OX.equalsIgnoreCase(quesType)) {
        	// 1.1 OX유형 문제
        	// Do nothing
        	qType = "1";
        	tab1TotalQues = Integer.parseInt(map.get(CommonUtil.TOTAL_QUES));
        	
        	if (rowNum == tab1TotalQues) {
        		findViewById(R.id.practice_result_1_cont).setVisibility(View.GONE);
        	} else {
        		findViewById(R.id.practice_result_1_cont).setVisibility(View.VISIBLE);
        	}
        } else if (CommonUtil.QUES_TYPE_MULTI_CHOICE.equalsIgnoreCase(quesType)) {
        	qType = "2";
        	tab2TotalQues = Integer.parseInt(map.get(CommonUtil.TOTAL_QUES));
        	
        	if (rowNum == tab2TotalQues) {
        		findViewById(R.id.practice_result_2_cont).setVisibility(View.GONE);
        	} else {
        		findViewById(R.id.practice_result_2_cont).setVisibility(View.VISIBLE);
        	}
        	// 1.2 객관식 유형 문제
			// 1.2.1  보기 섞기
			Random rgen = new Random();  // Random number generator
			int[] cards = new int[4];    // 보기문제 개수
			
			for (int i=0; i<cards.length; i++) {	//--- Initialize the array to the ints 0-3
			    cards[i] = i + 1;
			}
			for (int i=0; i<cards.length; i++) {	//--- Shuffle by exchanging each element randomly
			    int randomPosition = rgen.nextInt(cards.length);
			    int temp = cards[i];
			    cards[i] = cards[randomPosition];
			    cards[randomPosition] = temp;
			}

			// 1.2.2 보기 값 세팅
			int radio1 = getResources().getIdentifier("practice_quiz_2_radio" + cards[0], "id", getPackageName());
			int radio2 = getResources().getIdentifier("practice_quiz_2_radio" + cards[1], "id", getPackageName());
			int radio3 = getResources().getIdentifier("practice_quiz_2_radio" + cards[2], "id", getPackageName());
			int radio4 = getResources().getIdentifier("practice_quiz_2_radio" + cards[3], "id", getPackageName());
			
    	    ((TextView)findViewById(radio1)).setText(answerList.get(0).get(CommonUtil.Q_ANSWER));
    	    ((TextView)findViewById(radio2)).setText(answerList.get(1).get(CommonUtil.Q_ANSWER));
    	    ((TextView)findViewById(radio3)).setText(answerList.get(2).get(CommonUtil.Q_ANSWER));
    	    ((TextView)findViewById(radio4)).setText(answerList.get(3).get(CommonUtil.Q_ANSWER));

        } else if (CommonUtil.QUES_TYPE_SHORT_ANSWER.equalsIgnoreCase(quesType)) {
        	qType = "3";
        	tab3TotalQues = Integer.parseInt(map.get(CommonUtil.TOTAL_QUES));
        	
        	if (rowNum == tab3TotalQues) {
        		findViewById(R.id.practice_result_3_cont).setVisibility(View.GONE);
        	} else {
        		findViewById(R.id.practice_result_3_cont).setVisibility(View.VISIBLE);
        	}
    		// 1.3 주관식 유형 문제
        	// 1.3.1 유사답안
            int simAnswerLayoutId = getResources().getIdentifier("practice_result_" + qType + "_sim_answer_layout", "id", getPackageName());
        	int simAnswerId = getResources().getIdentifier("practice_result_" + qType + "_sim_answer", "id", getPackageName());
        	
            String simAnswer = "";
            if (answerList.get(0).get(CommonUtil.SIMILAR_ANSWER) != null) {
            	simAnswer = answerList.get(0).get(CommonUtil.SIMILAR_ANSWER);
            }
            
            // 1.3.2.1 유사 답안이 없을 경우
        	if("null".equals(simAnswer.toLowerCase().trim()) || "".equals(simAnswer.trim())) {
        		findViewById(simAnswerLayoutId).setVisibility(View.GONE);
	        	((TextView)findViewById(simAnswerId)).setText("없음");
        	} else { // 1.3.2.2 유사 답안이 있을 경우
        		String delimiter = ";";
        		if(simAnswer.indexOf(";") != -1){
        			delimiter = ";";
        		} else if(simAnswer.indexOf("|") != -1){
        			delimiter = "|";
        		} else if(simAnswer.indexOf(",") != -1){
        			delimiter = ",";
        		}
    			String[] simArray = simAnswer.split(delimiter);
        		
				StringBuffer sb = new StringBuffer();

				for (int i = 0; i < simArray.length; i++) {
					sb.append(simArray[i].trim());
					if(i != (simArray.length - 1)){
						sb.append(" / ");
					}
				}
		        ((TextView)findViewById(simAnswerId)).setText(sb.toString());
        		findViewById(simAnswerLayoutId).setVisibility(View.VISIBLE);
        	}        	

    	} else {
        	Toast.makeText(PracticeQuiz.this, "qTypeException1", Toast.LENGTH_SHORT).show();
    	}
        

        // 2. 공통 항목 별 값 세팅
        int practiceQuiz            = getResources().getIdentifier("practice_quiz_"   + qType, "id", getPackageName());
        int practiceResult          = getResources().getIdentifier("practice_result_" + qType, "id", getPackageName());
        int practiceQuizSeqno       = getResources().getIdentifier("practice_quiz_"   + qType + "_seqno",       "id", getPackageName());
        int practiceQuizQuesSeqno   = getResources().getIdentifier("practice_quiz_"   + qType + "_ques_seqno",  "id", getPackageName());
        int practiceQuizQuestion    = getResources().getIdentifier("practice_quiz_"   + qType + "_question",    "id", getPackageName());
        int practiceQuizAnswer      = getResources().getIdentifier("practice_result_" + qType + "_answer",      "id", getPackageName());
        int practiceQuizExplanation = getResources().getIdentifier("practice_result_" + qType + "_explanation", "id", getPackageName());
		int yourAnswerId            = getResources().getIdentifier("practice_quiz_"   + qType + "_your_answer", "id", getPackageName());
        
		((TextView)findViewById(R.id.prac_tvTotalQues)).setText(" / " + Integer.parseInt(map.get(CommonUtil.TOTAL_QUES)));
		((TextView)findViewById(practiceQuizSeqno)).setText(map.get(CommonUtil.SEQ_NO));
		((TextView)findViewById(practiceQuizQuesSeqno)).setText(String.valueOf(Float.valueOf(map.get(CommonUtil.ROWNUM)).intValue()));
	    ((TextView)findViewById(practiceQuizQuestion)).setText(map.get(CommonUtil.Q_QUESTION));
		((TextView)findViewById(practiceQuizAnswer)).setText(map.get(CommonUtil.Q_ANSWER));
		((TextView)findViewById(practiceQuizExplanation)).setText(map.get(CommonUtil.Q_EXPLANATION));
    	((TextView)findViewById(yourAnswerId)).setText(""); // 이전에 입력한 답 초기화

    	// 3. 문제 레이아웃으로 변경
        findViewById(practiceQuiz).setVisibility(View.VISIBLE);
		findViewById(practiceResult).setVisibility(View.GONE);
    }



    /*
	 * 문제풀이 결과 표시 + 업데이트 : updateQuizResult 에 대한 AsyncTask 수행 클래스
	 * 참고 : http://tigerwoods.tistory.com/28
	 */
    private class updateQuizResultAsyncTask extends AsyncTask<String, Void, HashMap<String, String>> {
    	@Override
    	protected void onPreExecute(){
    		showProgressDialog("정답 확인 중입니다.");
    	}
    	
		@Override
		protected HashMap<String, String> doInBackground(String ... params) {

			List<HashMap<String,String>> quizList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> quizMap = new HashMap<String, String>();
			
			String noteResult = "E";
			String isCorrect  = "N";
			//int    qType      = 1;
			String qType = "";
			String    quesType      = "";
			String    userid      = "";
	    	if (params[0] != null)
	    		//quesType = params[0];
	    		qType = params[0];
	    	if (params[1] != null)
	    		userid = params[1];
	    	
	    	
	    	/*
	    	if ("OX".equals(quesType)) {
	        	qType = 1;
	        } else if ("객관식".equals(quesType)) {
	        	qType = 2;
	        } else if ("주관식".equals(quesType)) {
	        	qType = 3;
	        }
	        	*/
	        	
			// 정답과 입력한 답
			int seqnoId      = getResources().getIdentifier("practice_quiz_"   + qType + "_seqno", "id", getPackageName());
			int corrAnswerId = getResources().getIdentifier("practice_result_" + qType + "_answer", "id", getPackageName());
			int yourAnswerId = getResources().getIdentifier("practice_quiz_"   + qType + "_your_answer", "id", getPackageName());
			
			String seqno      = ((TextView)findViewById(seqnoId)).getText().toString();
			String corrAnswer = ((TextView)findViewById(corrAnswerId)).getText().toString().trim();
			String yourAnswer = ((TextView)findViewById(yourAnswerId)).getText().toString().trim();


			// 정답일 경우 #1
		    if(corrAnswer.toLowerCase().trim().equalsIgnoreCase(yourAnswer.toLowerCase().trim())){
		    	isCorrect = CommonUtil.FLAG_Y;
		    } 
			// 정답일 경우 #2 : 주관식 문제의 경우 유사답안에 대해 추가 정답처리
		    //if(qType == 3) {
		    if(qType.equals("3")) {
				int simAnswerId = getResources().getIdentifier("practice_result_" + qType + "_sim_answer", "id", getPackageName());
				String simAnswer = ((TextView)findViewById(simAnswerId)).getText().toString();
				
				String[] simArray = simAnswer.split(" / ");
				
				for (int i = 0; i < simArray.length; i++) {
					if(yourAnswer.toLowerCase().trim().equalsIgnoreCase(simArray[i].toLowerCase().trim())) {
						isCorrect = CommonUtil.FLAG_Y;
					}
				}
			}
		    
			// 오답일 경우(isCorrect="N") -> 오답노트에 추가("C")
		    if(!CommonUtil.FLAG_Y.equalsIgnoreCase(isCorrect)){
				quizMap.put(CommonUtil.SEQ_NO, seqno);
				quizList.add(quizMap);
				quizList = MyStudyRoom_Common.insert_note(PracticeQuiz.this, quizList, CommonUtil.NOTE_TYPE_C, getString(R.string.base_uri), userid);
				
				if(quizList.size() > 0){
					quizMap = quizList.get(0);

					String regCnt     = quizMap.get(CommonUtil.REG_CNT);        // 신규 등록한 노트수
					String failCnt    = quizMap.get(CommonUtil.FAIL_CNT);       // 등록 실패한 노트수
					String prevRegCnt = quizMap.get(CommonUtil.REG_BEFORE_CNT); // 기 등록된 노트 수

					if(!"0".equalsIgnoreCase(regCnt) && "0".equalsIgnoreCase(failCnt) && "0".equalsIgnoreCase(prevRegCnt)){
						noteResult = CommonUtil.NOTE_RESULT_S; //오답 노트에 저장되었습니다.
					} else if("0".equalsIgnoreCase(regCnt) && !"0".equalsIgnoreCase(failCnt) && !"0".equalsIgnoreCase(prevRegCnt)){
						noteResult = CommonUtil.NOTE_RESULT_D; //이미 저장된 문제입니다.
					} else if("0".equalsIgnoreCase(regCnt) && !"0".equalsIgnoreCase(failCnt) && "0".equalsIgnoreCase(prevRegCnt)){
						noteResult = CommonUtil.NOTE_RESULT_F; //오답 노트가 이미 가득 찼습니다.
					} else {
						noteResult = CommonUtil.NOTE_RESULT_E; //그 외 (예외)
					}
				}
			    quizList.clear();
				quizMap.clear();
		    }

		    quizList = XmlParser.getValuesFromXML(getString(R.string.base_uri) + PRACTICE_QUIZ_COUNT + "?" + CommonUtil.SEQ_NO + "=" + seqno + "&" + CommonUtil.USER_ID + "=" + userid + "&" + CommonUtil.IS_CORRECT + "=" + isCorrect);

	    	if(quizList.size() > 0){
				quizMap = quizList.get(0);
			}
			
	    	quizMap.put(CommonUtil.IS_CORRECT, isCorrect);
	    	quizMap.put(CommonUtil.NOTE_RESULT, noteResult);
			
			return quizMap;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> map){
			dismissProgressDialog();
			updateQuizResult(map);
		}
    }


    // 문제풀이 결과 표시 + 업데이트
	public synchronized void updateQuizResult(HashMap<String, String> map) {
		try {
			String qType       = "1";
			String quesType       = "1";
			String resultText  = "오답입니다.";
			int    resultColor = 0xFFFF0000;
			
			if(map.get(CommonUtil.QUES_TYPE) != null)
				quesType = map.get(CommonUtil.QUES_TYPE);
			
			if (CommonUtil.QUES_TYPE_OX.equalsIgnoreCase(quesType)) {
	        	qType = "1";
	        } else if (CommonUtil.QUES_TYPE_MULTI_CHOICE.equalsIgnoreCase(quesType)) {
	        	qType = "2";
	        } else if (CommonUtil.QUES_TYPE_SHORT_ANSWER.equalsIgnoreCase(quesType)) {
	        	qType = "3";
	        }
	
	
			// 1. 변수 세팅
		    int yourResultId  = getResources().getIdentifier("practice_result_" + qType + "_your_result", "id", getPackageName());
			int blueBarId     = getResources().getIdentifier("practice_result_" + qType + "_bluebar", "id", getPackageName());
			int redBarId      = getResources().getIdentifier("practice_result_" + qType + "_redbar", "id", getPackageName());
			int blueTxtId     = getResources().getIdentifier("practice_result_" + qType + "_bluetxt", "id", getPackageName());
			int redTxtId      = getResources().getIdentifier("practice_result_" + qType + "_redtxt", "id", getPackageName());
			int quizLayerId   = getResources().getIdentifier("practice_quiz_"   + qType, "id", getPackageName());
	    	int resultLayerId = getResources().getIdentifier("practice_result_" + qType, "id", getPackageName());
	    	int cnoteId       = getResources().getIdentifier("practice_result_" + qType + "_cnote", "id", getPackageName());
	    	int cnoteFullId   = getResources().getIdentifier("practice_result_" + qType + "_cnote_full", "id", getPackageName());
	
	    	
	    	// 2. 정답/오답에 따른 동작
			if(CommonUtil.FLAG_Y.equalsIgnoreCase(map.get(CommonUtil.IS_CORRECT))) { // 정답일 경우
		    	resultText  = "정답입니다.";
		    	resultColor = 0xFF0000FF;
				findViewById(cnoteId).setVisibility(View.GONE);
				findViewById(cnoteFullId).setVisibility(View.GONE);
			} else {                               // 오답일 경우
				Vibrator vi = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				vi.vibrate(200);
				
				// 오답노트 저장 상태별 표시
				if(CommonUtil.NOTE_RESULT_S.equalsIgnoreCase(map.get(CommonUtil.NOTE_RESULT)) || CommonUtil.NOTE_RESULT_D.equalsIgnoreCase(map.get(CommonUtil.NOTE_RESULT))){ 
					findViewById(cnoteId).setVisibility(View.VISIBLE);
					findViewById(cnoteFullId).setVisibility(View.GONE);
				} else if(CommonUtil.NOTE_RESULT_F.equalsIgnoreCase(map.get(CommonUtil.NOTE_RESULT))){
					findViewById(cnoteId).setVisibility(View.GONE);
					findViewById(cnoteFullId).setVisibility(View.VISIBLE);
				} else {
					findViewById(cnoteId).setVisibility(View.GONE);
					findViewById(cnoteFullId).setVisibility(View.GONE);
				}
			}
	
	    	((TextView)findViewById(yourResultId)).setText(resultText);
			((TextView)findViewById(yourResultId)).setTextColor(ColorStateList.valueOf(resultColor));
	
	
			// 3. 풀이 문제의 현재까지 정답/오답 비율 표시
			// 막대 그래프 가로길이를 조정할때, xml에는 dp로 디자인되어 있지만, runtime에서는 무조건 px 단위의 int형만 지원된다.
			// 따라서 HDPI머신의 density인 240을 이용하여, dp = px * (160/density) 로 변환한 수치로 계산하여야 하므로 반드시 주의한다.
			// 예) 막대 전체 길이는 200dp(xml) = 300px(runtime) 으로 환산된다.
		    double cntTotal   = Double.parseDouble(map.get(CommonUtil.CNT_TOTAL));
		    double cntCorrect = Double.parseDouble(map.get(CommonUtil.CNT_CORRECT));
		    double blueRate   = Math.round((cntCorrect / cntTotal) * 1000.0) / 10.0;
		    double redRate    = Math.round((100.0 - blueRate) * 10.0) / 10.0;
		    int blueBar       = (int) Math.round((blueRate / 2) * 3 * 2);
		    int redBar        = (int) (300 - Math.round((blueRate / 2) * 3 * 2));
			
			((TextView)findViewById(blueTxtId)).setText(blueRate + "%");
			((TextView)findViewById(blueBarId)).setLayoutParams(new LinearLayout.LayoutParams(blueBar, ViewGroup.LayoutParams.WRAP_CONTENT, 0.0f));
			((TextView)findViewById(redTxtId)).setText(redRate + "%");
			((TextView)findViewById(redBarId)).setLayoutParams(new LinearLayout.LayoutParams(redBar, ViewGroup.LayoutParams.WRAP_CONTENT, 0.0f));				
	
	
			// 4. 해답 레이아웃으로 변경
			findViewById(quizLayerId).setVisibility(View.GONE);
			findViewById(resultLayerId).setVisibility(View.VISIBLE);
	
	
			// 5. 퀴즈 풀이 수 갱신
			solvingCount = solvingCount + 1;
			((TextView)findViewById(R.id.TextView02)).setText(String.valueOf(solvingCount));
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}



    /*
	 * 스크랩 노트에 추가 AsyncTask
	 */
	private class insertScrapNoteAsyncTask extends AsyncTask<String, Void, String> {
    	@Override
    	protected void onPreExecute(){
    		showLoadingProgressDialog();
    	}
    	
		@Override
		protected String doInBackground(String...params) {
			String scrapResult = "";
	    	String    qType = "1";
	    	String    userId = "";
	    	if (params[0] != null)
	    		qType = params[0];
	    	if (params[1] != null)
	    		userId = params[1];
			
			int seqnoId      = getResources().getIdentifier("practice_quiz_"   + qType + "_seqno", "id", getPackageName());
			String seqno     = ((TextView)findViewById(seqnoId)).getText().toString();
			
			List<HashMap<String,String>> sNoteList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> sNoteMap = new HashMap<String, String>();
			sNoteMap.put(CommonUtil.SEQ_NO, seqno);
			sNoteList.add(sNoteMap);
			
			sNoteList = MyStudyRoom_Common.insert_note(PracticeQuiz.this, sNoteList, "S", getString(R.string.base_uri), userId);
			
			if(sNoteList.size() > 0){
				sNoteMap = sNoteList.get(0);
				
				String regCnt     = sNoteMap.get(CommonUtil.REG_CNT);        // 신규 등록한 노트수
				String failCnt    = sNoteMap.get(CommonUtil.FAIL_CNT);       // 등록 실패한 노트수
				String prevRegCnt = sNoteMap.get(CommonUtil.REG_BEFORE_CNT); // 기 등록된 노트 수

				if(!"0".equalsIgnoreCase(regCnt) && "0".equalsIgnoreCase(failCnt) && "0".equalsIgnoreCase(prevRegCnt)){
					scrapResult = CommonUtil.NOTE_RESULT_S; //스크랩 노트에 저장되었습니다.
				} else if("0".equalsIgnoreCase(regCnt) && !"0".equalsIgnoreCase(failCnt) && !"0".equalsIgnoreCase(prevRegCnt)){
					scrapResult = CommonUtil.NOTE_RESULT_D; //이미 저장된 문제입니다.
				} else if("0".equalsIgnoreCase(regCnt) && !"0".equalsIgnoreCase(failCnt) && "0".equalsIgnoreCase(prevRegCnt)){
					scrapResult = CommonUtil.NOTE_RESULT_F; //스크랩 노트가 이미 가득 찼습니다.
				} else {
					scrapResult = CommonUtil.NOTE_RESULT_E; //그 외 (예외)
				}
			}
			return scrapResult;
		}

		@Override
		protected void onPostExecute(String scrapResult){
			dismissProgressDialog();
			insertScrapNotePostProcess(scrapResult);
		}
    }
	
    public void insertScrapNotePostProcess(String scrapResult) {
		if(CommonUtil.NOTE_RESULT_S.equalsIgnoreCase(scrapResult)){
			CommonUtil.showAlert(PracticeQuiz.this, CommonUtil.ALERT_TITLE_NOTICE,
				"스크랩 노트에 저장되었습니다.", CommonUtil.ALERT_POSITIVE_BUTTON);
		} else if(CommonUtil.NOTE_RESULT_D.equalsIgnoreCase(scrapResult)){
			CommonUtil.showAlert(PracticeQuiz.this, CommonUtil.ALERT_TITLE_NOTICE,
				"이미 저장된 문제입니다.", CommonUtil.ALERT_POSITIVE_BUTTON);
		} else if(CommonUtil.NOTE_RESULT_F.equalsIgnoreCase(scrapResult)){
			CommonUtil.showAlert(PracticeQuiz.this, CommonUtil.ALERT_TITLE_NOTICE,
				"스크랩 노트가 이미 가득 찼습니다.\n노트에는 100문제까지 저장이 가능합니다.\n\n스크랩된 기존 문제 일부를 삭제하시고 다시 시도하여 주시기 바랍니다.", CommonUtil.ALERT_POSITIVE_BUTTON);
		} else {
			CommonUtil.showAlert(PracticeQuiz.this, CommonUtil.ALERT_TITLE_NOTICE,
				"*^^*", CommonUtil.ALERT_POSITIVE_BUTTON);
		}
    }

    
	
	/*
	 * XML형태로 되어있는 URL과 Tag명을 입력받아, 그 값을 리턴하는 메소드
	 */
    /*
    public HashMap<String, String> getValuesFromXML(String addr) {
    	HashMap<String, String> map = new HashMap<String, String>();
    	
    	try {
    		URL url = new URL(addr);
    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();

    		if (conn != null) {
    	    	conn.setConnectTimeout(7000);
    			conn.setUseCaches(false);
    			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
    				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    				for (;;) {
    					String line     = br.readLine();
    					String tagName  = null;
    					String tagValue = null;

    					if(line == null) break;
    					
    					if(line.indexOf("<") != line.lastIndexOf("<")){
    						line     = line.trim();
    						tagName  = line.substring(line.indexOf("<") + 1, line.indexOf(">"));
    						tagValue = line.substring(tagName.length() + 2, line.lastIndexOf(tagName) - 2);

    						map.put(tagName, tagValue);
    					}
    				}
    				br.close();
    			}
    			conn.disconnect();
    		}
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	return map;
    }
    */
}
