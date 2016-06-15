package com.common.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public class HttpConnection {

	//Log 를 사용할 때 붙는 Tag 상수
	private static final String TAG = HttpConnection.class.getSimpleName();

	/**
	 * Method ID  : getResultPostParameter
	 * Method 설명 : 기존 URL과 Parameter를 넘겨주면 ArrayList<HashMap<String, String>> 의 결과를 반환한다.
	 * 최초작성일  : 2011. 9. 22. 
	 * 작성자 : jungungi
	 * 변경이력 : 
	 * @param urlPath : Http Connection을 위한 URL을 담음
	 * @param params : Http Connection Parameter를 담음
	 * @return 
	 */
	public static ArrayList<HashMap<String, String>> getResultPostParameter(
			String urlPath, HashMap<String, String> params) {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

		try {
			// 파라미터 저장
			String strParameters = "";
			if (params != null) {
				for (Iterator<Entry<String, String>> it = params.entrySet()
						.iterator(); it.hasNext();) {
					HashMap.Entry<String, String> entry = it.next();
					
					// 통신을 위해서 UTF-8로 인코딩한다.
					strParameters += URLEncoder.encode(entry.getKey(), "UTF-8") + "="
							+ URLEncoder.encode(entry.getValue(), "UTF-8");

					if (it.hasNext())
						strParameters += "&";
				}
				Log.d(TAG, "parameters : " + strParameters);
			}

			URL url = new URL(urlPath);
			
			// url Connection 연결
			URLConnection conn = url.openConnection();
			
			// Post 방식 설정
			conn.setDoOutput(true);
			
			// 파라미터를 넘겨주기 위한 Output 스트림을 뺀다. 
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());

			// 파라미터를 Output 스트림에 쓴다.
			if (strParameters.length() > 0)
				wr.write(strParameters);

			// Output 스트림을 Connection에 넘겨준다.
			wr.flush();

			// 결과 Input 스트림을 받아서 ArrayList<HashMap<String, String>> 데이터 타입으로 변환하는 함
			result = parsingInputStream(conn.getInputStream());

			// OutPut 스트림을 닫는다.
			wr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static ArrayList<HashMap<String, String>> getResultGetParameter(
			String urlPath, HashMap<String, String> params) {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

		try {
			String data = "?";
			if (params != null) {
				for (Iterator<Entry<String, String>> it = params.entrySet()
						.iterator(); it.hasNext();) {
					HashMap.Entry<String, String> entry = it.next();

					data += URLEncoder.encode(entry.getKey(), "UTF-8") + "="
							+ URLEncoder.encode(entry.getValue(), "UTF-8");

					if (it.hasNext())
						data += "&";
				}
				Log.d(TAG, "parameters : " + data);
			}

			URL url = new URL(urlPath + data);

			URLConnection conn = url.openConnection();

			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			if (data.length() > 0)
				wr.write(data);
			wr.flush();

			// Get Response
			result = parsingInputStream(conn.getInputStream());

			wr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Method ID  : parsingInputStream
	 * Method 설명 : InputStream을 받아서 Parsing을 통하여 ArrayList<HashMap<String, String>> 데이터 타입으로 변환 후 반환한다.
	 * 최초작성일  : 2011. 9. 22. 
	 * 작성자 : jungungi
	 * 변경이력 : 
	 * @param stream : Input 스트림으로 받는다.
	 * @return ArrayList<HashMap<String, String>> 타엡으로 변환한다.
	 */
	public static ArrayList<HashMap<String, String>> parsingInputStream(
			InputStream stream) {
		// 결과를 저장용 변수 선언 및 생성
		ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();

		try {
			
			DocumentBuilderFactory dbf;
			dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();			
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(stream), 80);
			StringBuffer strbuf = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				strbuf.append(line);
				Log.d(TAG, "parsingInputStream : " + line);
			}

			Document dom = db.parse(new InputSource(new StringReader(strbuf
					.toString())));
			// Document dom = db.parse(in); //이부분에서 바로 SAException catch됨
			Element root = dom.getDocumentElement();
			NodeList rootNodeList = root.getElementsByTagName("item");
			for (int i = 0; i < rootNodeList.getLength(); i++) {
				NodeList nodeList = rootNodeList.item(i).getChildNodes();
				HashMap<String, String> hashmap = new HashMap<String, String>();
				for (int j = 0; j < nodeList.getLength(); j++) {
					String key = nodeList.item(j).getNodeName();
					String value = "";
					if (nodeList.item(j).hasChildNodes()) {
						value = nodeList.item(j).getFirstChild().getNodeValue();
					}
					hashmap.put(key, value);
					
					Log.d(TAG, "key : " + key);
					Log.d(TAG, "value: " + value);
					Log.d(TAG, "i : " + i  + ", j : " + j);
				}
				dataList.add(hashmap);
			}

			stream.close();

			return dataList;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "Exception : " + e.toString());
			return null;
		}
	}

}
