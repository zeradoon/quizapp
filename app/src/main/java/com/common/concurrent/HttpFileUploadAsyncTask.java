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
public class HttpFileUploadAsyncTask extends
		AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

	private Context context;
	private BetterAsyncTaskCallable callable;

//	private HashMap<String, String> parameters;
//	private String url;
//	private String filePath;

	private ProgressDialog progressDialog;
	private boolean progressVisible = true;

	// AsyncTask 구분자 결과값 구분
	private int id;

	public HttpFileUploadAsyncTask(String url, HashMap<String, String> parameters, String filePath, int id,
			Context context, BetterAsyncTaskCallable callable,
			boolean progressVisible) {
		this.context = context;
		this.callable = callable;
//		this.url = url;
//		this.parameters = parameters;
//		this.filePath = filePath;
		this.id = id;
		this.progressVisible = progressVisible;
	}

	@Override
	protected void onPreExecute() {
		if (progressVisible)
			showLoadingProgressDialog();
	}

	@Override
	protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
//		return HttpConnection.updateFile(url, parameters, filePath);
		return null;
	}

	@Override
	protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
		if (progressVisible)
			dismissProgressDialog();
		callable.getAsyncTaskResult(result, id);
	}

	// ***************************************
	// Public methods
	// ***************************************
	public void showLoadingProgressDialog() {
		this.showProgressDialog("Loading. Please wait...");
	}

	public void showProgressDialog(CharSequence message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setIndeterminate(true);
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
