package com.ranking;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

import com.common.CommonUtil;
import com.skcc.portal.skmsquiz.R;

public class RankingCommon {
	private static final String LOG_TAG = RankingCommon.class.getSimpleName(); 
	
	public static String httpHelperRequest(HttpResponse response) {
		String result = "";
		try {
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder str = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				str.append(line + "\n");
			}
			in.close();
			result = str.toString();
		} catch (Exception ex) {
			result = CommonUtil.ERROR;
		}
		return result;
	}
	
	public static String longDouble2String(int size, double value) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(size);
		nf.setGroupingUsed(false);
		return nf.format(value);
	}

	public static String getGradeName(Context context, String ranking,
			String totla_cnt) {
		String result = "";

		try {
			double percent = Double.parseDouble(ranking)
					/ Double.parseDouble(totla_cnt) * 100;

			if (percent <= 3) {
				result = context.getString(R.string.ranking_grade_first);
			} else if (percent <= 10) {
				result = context.getString(R.string.ranking_grade_second);
			} else if (percent <= 25) {
				result = context.getString(R.string.ranking_grade_third);
			} else if (percent <= 70) {
				result = context.getString(R.string.ranking_grade_forth);
			} else if (percent <= 100) {
				result = context.getString(R.string.ranking_grade_fifth);
			}

		} catch (NumberFormatException e) {
			result = context.getString(R.string.ranking_grade_anonymous);
		}
		return result;
	}

	public static int getGradeImage(Context context, String ranking,
			String totla_cnt) {
		int result = 0;

		try {
			double percent = Double.parseDouble(ranking)
					/ Double.parseDouble(totla_cnt) * 100;

			if (percent <= 3) {
				result = R.drawable.user_grade_0;
			} else if (percent <= 10) {
				result = R.drawable.user_grade_1;
			} else if (percent <= 25) {
				result = R.drawable.user_grade_2;
			} else if (percent <= 70) {
				result = R.drawable.user_grade_3;
			} else if (percent <= 100) {
				result = R.drawable.user_grade_4;
			}

		} catch (NumberFormatException e) {
			result = R.drawable.user_grade_anonymous;
		}
		return result;
	}

	public static boolean isStringDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static InputStream getInputStream(String para_url) {
		while (true) {
			try {
				URL url = new URL(para_url);
				URLConnection con = url.openConnection();
				InputStream is = con.getInputStream();
				return is;
			} catch (Exception e) {
				Log.d("mytag", e.getMessage());
			}
		}
	}

	public static List<String> getPosts(String url) {
		List<String> stringList = new ArrayList<String>();

		try {

			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			InputStream stream = getInputStream(url);
			xpp.setInput(stream, "UTF-8");

			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					Log.d("mytag", "Start document");
					break;
				case XmlPullParser.END_DOCUMENT:
					Log.d("mytag", "End document");
					break;
				case XmlPullParser.START_TAG:
					Log.d("mytag", "Start tag " + xpp.getName());
					if (xpp.getName().equalsIgnoreCase(CommonUtil.BODY)) {
						eventType = xpp.next();
						stringList.add(xpp.getText());
					}
					break;
				case XmlPullParser.END_TAG:
					Log.d("mytag", "End tag " + xpp.getName());
					break;
				case XmlPullParser.TEXT:
					Log.d("mytag", "Text " + xpp.getText());
					break;
				}
				eventType = xpp.next();
			}

			return stringList;
		} catch (Exception e) {
			Log.d("mytag", e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static JSONObject getJSON(Map map) {
		Iterator iter = map.entrySet().iterator();
		JSONObject holder = new JSONObject();

		while (iter.hasNext()) {
			Map.Entry pairs = (Map.Entry) iter.next();
			String key = (String) pairs.getKey();
			Map m = (Map) pairs.getValue();
			JSONObject data = new JSONObject();

			try {
				Iterator iter2 = m.entrySet().iterator();
				while (iter2.hasNext()) {
					Map.Entry pairs2 = (Map.Entry) iter2.next();
					data.put((String) pairs2.getKey(),
							(String) pairs2.getValue());
				}
				holder.put(key, data);
			} catch (JSONException e) {
				Log.e("Transforming", "There was an error packaging JSON", e);
			}
		}

		return holder;
	}

	public static JSONObject getJSON(ArrayList<HashMap<String, String>> list) {
		JSONObject holder = new JSONObject();
		int i = 1;
		for (HashMap<String, String> hashmap : list) {
			JSONObject data = new JSONObject();
			try {
				for (String key : hashmap.keySet()) {
					data.put(key, hashmap.get(key));
				}
				holder.put(Integer.toString(i++), data);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return holder;
	}

	public static JSONArray getJSONArray(ArrayList<HashMap<String, String>> list) {
		JSONArray jsonArray = new JSONArray();
		for (HashMap<String, String> hashmap : list) {
			jsonArray.put(hashmap);
		}
		jsonArray.toString();
		return jsonArray;
	}

	public static JSONObject getJSON1(ArrayList<HashMap<String, String>> list) {

		JSONArray jsonArray = getJSONArray(list);
		
		JSONObject holder = new JSONObject();
		
		try {
			holder.put(CommonUtil.ITEM, jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return holder;
	}

	public static ArrayList<HashMap<String, String>> getArrayListHashMapString(
			JSONObject jsondata) {
		ArrayList<HashMap<String, String>> holder = new ArrayList<HashMap<String, String>>();

		return holder;
	}
	
	public static ArrayList<HashMap<String, String>> getArrayLIstHashMapFromJSONObject(JSONObject jsonObject) {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
		try {
			JSONArray jsonArray = jsonObject.getJSONArray(CommonUtil.ITEM);
			
			for (int i = 0 ; i < jsonArray.length() ; i++) {
//				JSONObject data = jsonArray.get(i).toString();
				@SuppressWarnings("unchecked")
				HashMap<String, String> obj = (HashMap<String, String>) jsonArray.get(i);
				list.add(obj);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return list;
		
	}
	
	public static String getHtmlToText(String content) {  
	    Log.d(LOG_TAG, "getHtmlToText source : " + content);
	    return Jsoup.parse(content).text();
	} 

}
