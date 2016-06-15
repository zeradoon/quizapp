package com.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class CommonUtil {

	public static final String ALERT_TITLE_NOTICE  = "알림";
	public static final String ALERT_TITLE_WARNING = "경고";
	
	public static final String ALERT_MESSAGE_NETWORK_ERROR = "3G 혹은 Wifi 망에 연결되지 않았거나 접속이 원활하지 않습니다.\n\n네트워크 확인후 다시 접속해 주세요!";
	public static final String ALERT_MESSAGE_3G_ERROR      = "3G 망에 연결되지 않았거나 접속이 원활하지 않습니다.\n\n네트워크 확인후 다시 접속해 주세요!";
	public static final String ALERT_MESSAGE_WIFI_ERROR    = "Wifi 망에 연결되지 않았거나 접속이 원활하지 않습니다.\n\n네트워크 확인후 다시 접속해 주세요!";
	public static final String ALERT_MESSAGE_OBJECTIVE     = "객관식 답을 선택하여 주세요";
	public static final String ALERT_MESSAGE_SUBJECTIVE    = "주관식 답을 입력하여 주세요";
	public static final String ALERT_MESSAGE_OXQUIZ    	   = "OX 답을 선택하여 주세요";
	public static final String ALERT_MESSAGE_CHOOSE_VIRTUAL_TEAM = "팀을 선택하여 주세요."; //Please select your team
	public static final String ALERT_MESSAGE_CHOOSE_VIRTUAL_TEAM_TITLE = "팀을 선택하여 주세요."; //Select Team Message
	
	public static final String ALERT_MESSAGE_DELETE_NOTE    = "삭제하면 더 이상  오답노트에서 확인할 수 없습니다.";
	
	public static final String ALERT_POSITIVE_BUTTON = "확인";
	public static final String ALERT_NEGATIVE_BUTTON = "취소";
	
	public static final String GENERAL_EVENT_SCHEDULE = "기간: ";
	public static final String GENERAL_EVENT_DETAIL_SCHEDULE = "기간 : ";
	
	public static final String USER_CAN_JOIN_NO = "1";
	public static final String USER_CAN_JOIN_YES = "0";
	
	public static final String USER_TEAM_MEMBER_YN_NO = "0";
	public static final String USER_TEAM_MEMBER_YN_YES = "1";
	
	public static final String ID = "ID";
	public static final String USER_ID = "USER_ID";
	public static final String LAST_USER_ID = "LAST_USER_ID";
	public static final String RANKING = "RANKING";
	public static final String REAL_RANKING = "실전퀴즈 랭킹"; //REAL QUIZ RANKING
	public static final String EVENT_RANKING = "이벤트 랭킹"; //EVENT RANKING
	public static final String LAST_RANKING = "LAST_RANKING";
	
	
	public static final String BOOKMARK_SAVED_SUCCESSFULLY = "북마크가 저장되었습니다.";
	public static final String INVALID_QUES_NUMBER = "This question number is invalid.";
	
	public static final String ROWNUM = "ROWNUM";
	public static final String BOOKMARK_QUESTION_NO = "BOOKMARK_QUESTION_NO";
	public static final String BOOKMARK_QUES_ROWNUM = "BOOKMARK_QUES_ROWNUM";
	public static final String TOTAL_QUES = "TOTAL_QUES";
	
	public static final String USER_EMAIL = "USER_EMAIL";
	public static final String USER_PASSWORD = "USER_PASSWORD";
	public static final String USER_DEPT = "USER_DEPT";
	public static final String DEPT_NAME = "DEPT_NAME";
	
	public static final String COMP_CD = "COMP_CD";
	public static final String EVENT_ID = "EVENT_ID";
	public static final String PAGE_SIZE = "PAGE_SIZE";
	public static final String LAST_EVENT = "LAST_EVENT";
	public static final String EVENT_STATUS = "EVENT_STATUS";
	public static final String EVENT_STATE = "EVENT_STATE";
	public static final String EVENT_STATUS_OPEN = "OPEN"; //OPEN
	public static final String EVENT_STATUS_FINISHED = "FINISHED"; //FINISHED
	public static final String EVENT_STATUS_UPCOMING = "UPCOMING"; //FINISHED
	public static final String EVENT_TYPE_NM = "EVENT_TYPE_NM";
	public static final String EVENT_TYPE_NAME = "EVENT_TYPE_NAME";
	public static final String EVENT_TYPE_GENERAL = "GENERAL";
	public static final String EVENT_TYPE_TEAM = "TEAM";
	public static final String EVENT_TEAM_NAME = "EVENT_TEAM_NAME";
	
	public static final String EVENT_STATUS_OPEN_STR = "진행중"; //OPEN
	public static final String EVENT_STATUS_FINISHED_STR = "마감"; //FINISHED
	public static final String EVENT_STATUS_UPCOMING_STR = "예정"; //FINISHED
	
	public static final String EVENT_TYPE_VIRTUAL_TEAM = "VIRTUAL TEAM";
	public static final String USER_CAN_JOIN = "USER_CAN_JOIN";
	public static final String USER_TEAM_MEMBER_YN = "USER_TEAM_MEMBER_YN";
	public static final String OPEN_YN = "OPEN_YN";
	public static final String USER_IMG = "USER_IMG";
	
	public static final String QUIZ_TYPE_SURVIVAL = "SURVIVAL";
	
	public static final String START_DURATION = "START_DURATION";
	public static final String END_DURATION = "END_DURATION";
	
	
	public static final String EVENT_NAME = "EVENT_NAME";
	public static final String QUIZ_ID = "QUIZ_ID";
	public static final String QUIZ_TYPE_NM = "QUIZ_TYPE_NM";
	public static final String QUES_COUNT = "QUES_COUNT";
	public static final String QUES_ATTEMPT_COUNT = "QUES_ATTEMPT_COUNT";
	public static final String START_DT = "START_DT";
	public static final String END_DT = "END_DT";
	public static final String QUIZ_COMPLETION_TIME = "QUIZ_COMPLETION_TIME";
	public static final String EVENT_DESC = "EVENT_DESC";
	public static final String VIRTUAL_TEAM_ID = "VIRTUAL_TEAM_ID";
	public static final String VIRTUAL_TEAM_NAME = "VIRTUAL_TEAM_NAME";
	public static final String MEMBER_COUNT = "MEMBER_COUNT";
	public static final String MAX_MEMBERS = "MAX_MEMBERS";
	
	public static final String GENERAL_EVENT_QUESTION = "GENERALEVENT_QUESTION";
	
	public static final String QUES_TYPE = "Q_TYPE";
	public static final String QUES_TYPE_OX = "OX";
	public static final String QUES_TYPE_OX_TEXT = "O/X";
	public static final String QUES_TYPE_MULTI_CHOICE = "객관식";
	public static final String QUES_TYPE_SHORT_ANSWER = "주관식";
	
	public static final String QUES_TYPE_OX_ANSWER_O = "O";
	public static final String QUES_TYPE_OX_ANSWER_X = "X";
	
	public static final String GENERAL_EVENT_JUNGDAB = "GENERALEVENT_JUNGDAB";
	public static final String GENERAL_EVENT_SEQ = "GENERALEVENT_SEQ";
	public static final String SURVIVAL_EVENT_JUNGDAB = "SURVIVALEVENT_JUNGDAB";
	public static final String SURVIVAL_EVENT_SEQ = "SURVIVALEVENT_SEQ";
	
	public static final String QUESTION_STRING = "문제";
	public static final String ANSWER_STRING = "미저장";
	public static final String ANSWER_SAVED = "저장";
	public static final String ANSWER_WRONG = "오답";
	public static final String ANSWER_CORRECT = "정답";
	public static final String Q_QUESTION = "Q_QUESTION";
	public static final String QUESTION_ID = "QUESTION_ID";
	public static final String Q_ANSWER = "Q_ANSWER";
	public static final String Q_EXPLANATION = "Q_EXPLANATION";
	
	public static final String SEQ_NO = "SEQ_NO";
	public static final String TIMER_FINISH_TEXT = "00:00:00";
	public static final String QUES_LIST_STR = "QUESLISTSTR";
	public static final String QUES_LIST = "QUESLIST";
	public static final String ANSWER_LIST = "ANSWERLIST";
	public static final String IS_INITIAIZATION = "IS_INITIAIZATION";
	
	public static final String SIMILAR_ANSWER = "SIMILAR_ANSWER";
	
	public static final String REAL_SCORE = "REAL_SCORE";
	public static final String FINAL_SCORE = "FINAL_SCORE";
	public static final String MESSAGE = "MESSAGE";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String BACKGROUND = "BACKGROUND";
	
	public static final String SCREEN_CONTROL_TYPE_EDIT_TEXT = "EditText";
	public static final String SCREEN_CONTROL_TYPE_TEXT_VIEW = "TextView";
	
	public static final String CONFIRM_VALUE = "CONFIRM_VALUE";
	public static final String USER_NAME = "USER_NAME";
	public static final String USER_TEAM_NAME = "USER_TEAM_NAME";
	public static final String NAME = "NAME";
	public static final String USER_NM = "USER_NM";
	public static final String USER_COMP_NM = "USER_COMP_NM";
	public static final String USER_COMP_CD = "USER_COMP_CD";
	public static final String USER_NICKNM = "USER_NICKNM";
	public static final String USER_PHONE = "USER_PHONE";
	public static final String IMEI = "IMEI";
	public static final String USER_COMPANY = "USER_COMPANY";
	public static final String UPDATE_ID = "UPDATE_ID";
	
	public static final String EMAIL_DOMAIN_SK_COM = "SK.COM";
	public static final String EMAIL_DOMAIN_INFOSEC_COM = "INFOSEC.COM";
	
	public static final String COMP_NM = "COMP_NM";
	
	public static final String LIST = "LIST";
	public static final String ERROR = "ERROR";
	
	public static final String NOTE_TYPE = "NOTE_TYPE";
	public static final String NOTE_TYPE_C = "C";
	public static final String NOTE_TYPE_S = "S";
	
	public static final String RESULT = "RESULT";
	public static final String CNT = "CNT";
	public static final String CHK = "CHK";
	public static final String CURR_SEQ = "CURR_SEQ";
	public static final String TAG1 = "tag1";
	public static final String TAG2 = "tag2";
	public static final String TAG3 = "tag3";
	
	public static final String SEL_TAB = "SEL_TAB";
	
	public static final String RANKING_TAG1 = "ranking_tag1";
	public static final String RANKING_TAG2 = "ranking_tag2";
	
	public static final String IS_DIALOG_HAS_PARAMS = "IS_DIALOG_HAS_PARAMS";
	public static final String ACTIVITY_CLASS = "ACTIVITY_CLASS";
	public static final String MAIN_ACTIVITY_CLASS = "com.main.QuizMain";
	public static final String PROFILE_ACTIVITY_CLASS = "com.profile.ProfileMain";
	public static final String RANKING_ACTIVITY_CLASS = "com.ranking.RankingSelect";
	public static final String GENERAL_EVENT_ACTIVITY_CLASS = "com.event.GeneralEvent";
	
	public static final String FAIL_CNT = "FAIL_CNT";
	public static final String REG_CNT = "REG_CNT";
	public static final String REG_BEFORE_CNT = "REG_BEFORE_CNT";
	
	public static final String CONTENT = "CONTENT";
	public static final String TITLE = "TITLE";
	public static final String FIX_YN = "FIX_YN";
	
	public static final String FLAG_Y = "Y";
	public static final String FLAG_N = "N";
	
	public static final String BOOLEAN_TRUE = "TRUE";
	public static final String BOOLEAN_FALSE = "FALSE";
	
	public static final String LAST_ARTICLE_NO = "LAST_ARTICLE_NO";
	public static final String ARTICLE_NO = "ARTICLE_NO";
	
	public static final String LAST_DEPT_NAME = "LAST_DEPT_NAME";
	
	public static final String QUIZ_TYPE = "QUIZ_TYPE";
	
	public static final String QUES_MAP = "QUESMAP";
	public static final String IS_CORRECT = "IS_CORRECT";
	public static final String NOTE_RESULT = "NOTERESULT";
	public static final String NOTE_RESULT_S = "S";
	public static final String NOTE_RESULT_D = "D";
	public static final String NOTE_RESULT_E = "E";
	public static final String NOTE_RESULT_F = "F";
	
	
	public static final String CNT_TOTAL = "CNT_TOTAL";
	public static final String CNT_CORRECT = "CNT_CORRECT";
	
	public static final String T_SCORE = "T_SCORE";
	public static final String T_PARTICIPATE = "T_PARTICIPATE";
	public static final String RANK = "RANK";
	public static final String TOTAL_CNT = "TOTAL_CNT";
	public static final String JOIN_CNT = "JOIN_CNT";
	
	public static final String INDIVIDUAL_SCORE = "INDIVIDUAL_SCORE";
	
	
	public static final String ERROR_IDX_M_USER_1 = "IDX_M_USER_1";
	
	public static final String REAL_QUESTION = "REAL_QUESTION";
	public static final String REAL_QUESTION_CNT = "REAL_CORRECT_CNT";
	
	public static final String REAL_JUNGDAB = "REAL_JUNGDAB";
	public static final String REAL_SEQ = "REAL_SEQ";
	
	public static final String COMP_CODE = "COMPCODE";
	
	public static final String FAULT_MESSAGE = "Validation failure";
	public static final String REQUIRED_MESSAGE = "This field is mandatory.";
	public static final String PASSWORDS_NOT_MATCH = "비밀번호가 일치하지 않습니다."; //Passwords don't match
	
	public static final String REGEX_PATTERN_USER_ID = "^[A-Za-z][A-Za-z0-9]{3,20}$";
	public static final String REGEX_PATTERN_PASSWORD = "^[A-Za-z0-9\\+\\.\\_\\%\\-\\+]{5,20}$";
	public static final String REGEX_PATTERN_EMAIL = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}";
	public static final String REGEX_PATTERN_PHONE = "[\\+]{0,1}[0-9]{1,20}";
	
	public static final String SCREEN_CONTROL_TYPE_EDIT_TEXT_CLASSNAME = "android.widget.EditText";
	public static final String SCREEN_CONTROL_TYPE_TEXT_VIEW_CLASSNAME = "android.widget.TextView";
	
	public static final String TOTAL_USER_CNT = "TOTAL_USER_CNT";
	public static final String MEMBER_CNT = "MEMBER_CNT";
	public static final String DEPT_SCORE = "DEPT_SCORE";
	public static final String DEPT_AVG = "DEPT_AVG";
	
	public static final String SCORE = "SCORE";
	public static final String QUESTION_ID_LIST = "QUESTION_ID_LIST";
	public static final String USER_ANSWER_LIST = "USER_ANSWER_LIST";
	public static final String ANSWER_RESULT_LIST = "ANSWER_RESULT_LIST";
	public static final String QUESTION_TYPE_NM = "QUESTION_TYPE_NM";
	
	
	public static final String BODY = "BODY";
	public static final String ITEM = "item";
	
	public static final String F_DOWNLOAD = "f_download";
	
	public static final String SIM_ANSWER_SEPERATOR = ";";
	public static final String MULTI_ANSWER_TEXT_COLOR = "#000000";
	public static final String ANSWER_TYPE_CODE = "ANSWER_TYPE_CODE";
	public static final String ANSWER = "ANSWER";
	public static final String QUESTIONTEXT = "QUESTIONTEXT";
	public static final String ANSWER_TYPE_PASS = "6";
	
	
	/*
	 * 네트워크 사용 가능 여부에 따라 Alert 표시 및 true/false 리턴
	 * param : context
	 * return : boolean 
	 */
	public static boolean showNetworkAlert(Context context) {
		boolean isNetworkAvailable = false;
		isNetworkAvailable = NetworkUtil.isNetworkAvailable(context);
		
		if(isNetworkAvailable == false){
			showAlert(context, ALERT_TITLE_NOTICE, ALERT_MESSAGE_NETWORK_ERROR, ALERT_POSITIVE_BUTTON);
		}
		return isNetworkAvailable;
	}
	
	/* 
	 * Alert 표시
	 * param : context, 제목, 메시지, 버튼명
	 */
	public static void showAlert(Context context, String title, String message, String button) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss(); // 닫기
			}
		});
        ad.show();
	}
	
	public static void showAlertAndFinish(Context context, String title, String message, String button) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss(); // 닫기
				// finish();
			}
		});
        ad.show();
	}
}
