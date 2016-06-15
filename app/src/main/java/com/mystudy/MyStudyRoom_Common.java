package com.mystudy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import android.content.Context;

import com.common.CommonUtil;
import com.common.NetworkUtil;
import com.common.XmlParser;

public class MyStudyRoom_Common {
	
	private static final String MY_STUDYROOM_REG_QUIZ = "/myStudyRoomRegQuiz.do";
	
	public static List<HashMap<String,String>> insert_note(Context context, List<HashMap<String,String>> list, String note_type, String base_url, String userId){
		String result = "";
		
		if(note_type == null)
			note_type = CommonUtil.NOTE_TYPE_C;
		
		if(list == null){
			list = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map2 = new HashMap<String, String>();
			map2.put(CommonUtil.REG_CNT, "0");
			map2.put(CommonUtil.REG_BEFORE_CNT, "0");
			map2.put(CommonUtil.FAIL_CNT, "0");
			list.add(map2);	
		}else{
			// 데이터 전달.
			result = ListToXml2(list);
			//String user_id = NetworkUtil.getPhoneNumber(context);
			String addr = base_url + MY_STUDYROOM_REG_QUIZ + "?" + CommonUtil.USER_ID + "=" + userId + "&" + CommonUtil.NOTE_TYPE + "="+note_type+"&" + CommonUtil.RESULT + "=" + result;	
			list = new ArrayList<HashMap<String, String>>();	// 초기화.
			list = XmlParser.getValuesFromXML(addr);
		}	
		return list;
	}
	
	public static String ListToXml2(List<HashMap<String,String>> list){
		StringBuffer returnXml = new StringBuffer();
		String keyName = "";
		String keyValue = "";
		Set key = null;
		for(int i=0;i<list.size();i++){
			key = list.get(i).keySet();
			for(Iterator iterator = key.iterator(); iterator.hasNext();){
				if(i != 0)
					returnXml.append(",");
				keyName  = iterator.next().toString();
				keyValue = String.valueOf(list.get(i).get(keyName));
				returnXml.append(keyValue);
			}
		}
		
		return returnXml.toString();
	}

}
