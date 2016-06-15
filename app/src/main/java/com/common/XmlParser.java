package com.common;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class XmlParser {
	
	private static final String LOG_TAG = XmlParser.class.getSimpleName();
	
	
    //성식 테스트
    public static List<HashMap<String,String>> getValuesFromXML(String addr) {
		List<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
		
    	HashMap<String, String> map = new HashMap<String, String>();
    	
    	
    	
    	try {
    		URL url = new URL(addr);
    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    		conn.setConnectTimeout(7000);	//접속 타임 아웃 시간(milliSec단위), 초과시 Exception 발생
			conn.setReadTimeout(7000);		//Read 타임 아웃 시간(milliSec단위), 초과시 Exception 발생
			conn.setDefaultUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");	//Define.METHOD_POST : "POST"
			conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");	//Define.CONTENT_TYPE : "content-type", Define.CONTENT_ENCODE : "application/x-www-form-urlencoded" 			
			
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));	
			conn.disconnect();
			
			
			pw.write(addr);
			pw.flush();
			
			Log.d(LOG_TAG, "addr : "+addr);

			XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserCreator.newPullParser();
			
			parser.setInput(url.openStream(), "UTF-8");

			int parseEvent = parser.getEventType();
			int itemCnt=0;
			
			while(parseEvent != XmlPullParser.END_DOCUMENT){
				switch(parseEvent){
					case XmlPullParser.START_TAG:
						String tag = parser.getName();
						
						if(tag.equals("root") || tag.equals("item")){
						}
						else{
							map.put(tag, parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						String endtag = parser.getName();
						if(endtag.equals("item")){
							list.add(itemCnt++,map);
							map = new HashMap<String, String>();
						}
						break;
					case XmlPullParser.START_DOCUMENT:
						break;
	        		case XmlPullParser.END_DOCUMENT:
				}
				parseEvent = parser.next();
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    
    /*
	 * XML형태로 되어있는 String을 입력받아 Parsing하여 List<Hashmap>에 담아 전달하는 메소드
	 */
	public static ArrayList<HashMap<String, String>> getValuesFromXMLData(String xmldata) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		int map_chk = 0;
	
		try {
			StringReader stringReader = new StringReader(xmldata);
			BufferedReader br = new BufferedReader(stringReader);
			for (;;) {
				String line = br.readLine();
				String tagName = null;
				String tagValue = null;
	
				if (line == null)
					break;
	
				if (line.indexOf("<") == line.lastIndexOf("<"))
					map_chk = 1;
	
				if (line.indexOf("<") != line.lastIndexOf("<")) {
					map_chk = 0;
	
					line = line.trim();
					tagName = line.substring(line.indexOf("<") + 1,
							line.indexOf(">"));
					tagValue = line.substring(line.indexOf("<![CDATA[") + 9,
							line.indexOf("]]>"));
	
					map.put(tagName, tagValue);
				}
	
				if ((map_chk == 1) && (map.size() > 1)) {
					list.add(map);
					map = new HashMap<String, String>();
				}
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
