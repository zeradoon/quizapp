/* Copyright (c) 2009 Matthias Käppler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.common.concurrent;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.common.http.HttpConnection;
import com.skcc.portal.skmsquiz.R;

/**
 * Works in a similar way to AsyncTask but provides extra functionality.
 * 
 * 1) It keeps track of the active instance of each Context, ensuring that the
 * correct instance is reported to. This is very useful if your Activity is
 * forced into the background, or the user rotates his device.
 * 
 * 2) A progress dialog is automatically shown. See useCustomDialog()
 * disableDialog()
 * 
 * 3) If an Exception is thrown from inside doInBackground, this is now handled
 * by the handleError method.
 * 
 * 4) You should now longer override onPreExecute(), doInBackground() and
 * onPostExecute(), instead you should use before(), doCheckedInBackground() and
 * after() respectively.
 * 
 * These features require that the Application extends DroidFuApplication.
 * 
 * @param <ParameterT>
 * @param <ProgressT>
 * @param <ReturnT>
 */
public class HttpPostParameterAsyncTask extends
		AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

	// Android Project의 자원 접근을 위한 Context
	private Context context;
	// AsyncTask 의 결과를 넘겨줄 CallBack 용 
	private BetterAsyncTaskCallable callable;

	// HTTP Post 방식의 파라미터 저장
	private HashMap<String, String> parameters;
	// HTTP URL
	private String url;

	// ProgressDialog 객체
	private ProgressDialog progressDialog;
	
	// ProgressDialog 를 보여줄지 여부 결정
	// 디폴트는 true
	private boolean progressVisible = true;

	// ProgressDialog Cancel 가능 여부
	private boolean progressCancelable = true;
	
	// AsyncTask 구분자 결과값 구분
	private int id;

	public HttpPostParameterAsyncTask(String url, HashMap<String, String> parameters, int id,
			Context context, BetterAsyncTaskCallable callable,
			boolean progressVisible) {
		this.context = context;
		this.callable = callable;
		this.url = url;
		this.parameters = parameters;
		this.id = id;
		this.progressVisible = progressVisible;
	}
	
	public HttpPostParameterAsyncTask(String url, HashMap<String, String> parameters, int id,
			Context context, BetterAsyncTaskCallable callable,
			boolean progressVisible, boolean progressCancelable) {
		this.context = context;
		this.callable = callable;
		this.url = url;
		this.parameters = parameters;
		this.id = id;
		this.progressVisible = progressVisible;
		this.progressCancelable = progressCancelable;
	}

	@Override
	protected void onPreExecute() {
		if (progressVisible)
			showLoadingProgressDialog();
	}

	@Override
	protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
		// 실제 작업이 일어나는 위치 url과 parameter를 넘겨준다.
		return HttpConnection.getResultPostParameter(url, parameters);
		
//		String strParameters = "";
//		if (parameters != null) {
//			for (Iterator<Entry<String, String>> it = parameters.entrySet()
//					.iterator(); it.hasNext();) {
//				HashMap.Entry<String, String> entry = it.next();
//				
//				// 통신을 위해서 UTF-8로 인코딩한다.
//				try {
//					strParameters += URLEncoder.encode(entry.getKey(), "UTF-8") + "="
//							+ URLEncoder.encode(entry.getValue(), "UTF-8");
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//				if (it.hasNext())
//					strParameters += "&";
//			}
////			Log.d(TAG, "parameters : " + strParameters);
//			
//			return (ArrayList<HashMap<String, String>>) XmlParser.getValuesFromXML(url + "?" + strParameters);	
//		} else {
//			return (ArrayList<HashMap<String, String>>) XmlParser.getValuesFromXML(url);
//		}
	}

	@Override
	protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
		if (progressVisible)
			dismissProgressDialog();
		// 실행결과를 Callback 객체의 getAsyncTaskResult 메서드에 작업 구분자 ID와 같이 넘겨준다.
		callable.getAsyncTaskResult(result, id);
	}

	// ***************************************
	// Public methods
	// ***************************************
	public void showLoadingProgressDialog() {
		this.showProgressDialog(context.getString(R.string.progress_message));
	}

	public void showProgressDialog(CharSequence message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(progressCancelable);
		}

		progressDialog.setMessage(message);
		progressDialog.show();
	}

	public void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
}
