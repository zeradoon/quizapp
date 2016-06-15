package com.login;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.common.CommonUtil;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.http.CommonDownloader;
import com.skcc.portal.skmsquiz.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;

/**
 * @author sati
 * @version 1.0, 2012-05-05
 * 
 * This class is used to check latest version of SKMS Quiz Application on startup.
 * If the current version is latest version then start login activity else ask user to upgrade.
 * 
 */
public class AppVersionCheck extends Activity {
	
	/* AppVersionCheck class context */
	private Context m_context = this;
	
	/* logging variable */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = AppVersionCheck.class.getSimpleName();
	
	/* member representing download complete dialog alert box id */
	private static int QUIZMAIN_DOWNLOAD_COMPLETE = 1;
	
	/* member representing current app version */
	private boolean m_IsLatestVersion = false;
	
	/* member representing download latest App version url */
	private static final String DOWNLOAD_APP_DO_F_DOWNLOAD = "downloadApp.do";
	
	/* member representing service to fetch latest App Info from DB */
	private static final String GET_APP_INFO = "getAppInfo.do";
	
	/* member representing handler to show App upgrade dialog box. */
	private Handler m_Handler = null;
	
	/* member representing URL for query web app. */
	private String m_Url = null;
	
	/* member representing progress bar dialog box. */
	private ProgressDialog progressDialog;
	
	
	/**
	 * This method is called once on creation of the AppVersionCheck Activity.
	 * It is used for initialization of required variables.
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_version_check);
		
		m_Handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == QUIZMAIN_DOWNLOAD_COMPLETE) {
					dismissProgressDialog();

					File root = Environment.getExternalStorageDirectory();
					String filepath = root + m_context.getString(R.string.app_file_path);
					Intent intent = new Intent();
					
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(new File(filepath)),	"application/vnd.android.package-archive");

					m_context.startActivity(intent);
				}
				super.handleMessage(msg);
			}
		};
		
		/* check if the version upgrade is required or not. */
		isLatestVersion();
		
		if (m_IsLatestVersion) {
			
		} 
	}
	
	/**
	 * This method checks if the application installed is latest version or not. 
	 * If not latest then ask user to upgrade.
	 */
	private void isLatestVersion() {
			String url = getString(R.string.base_uri) + GET_APP_INFO;
			HashMap<String, String> parameters = new HashMap<String, String>();

			Context context = m_context;
			boolean progressVisible = true;
			int id = 111;
			
			BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {
				@Override
				public boolean getAsyncTaskResult(ArrayList<HashMap<String, String>> list, int id) {
					int localVersion = 0;
					int ServerVersion = 0;
					ArrayList<HashMap<String, String>> m_ResultList = list;
					
					try {
						m_Url = getString(R.string.base_uri) + DOWNLOAD_APP_DO_F_DOWNLOAD;
						PackageInfo pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
						localVersion = pkgInfo.versionCode;
						
						if (m_ResultList.size() > 0) {
							for (int i = 0; i < m_ResultList.size(); i++) {
								if (m_ResultList.get(i) != null)
									ServerVersion = Integer.parseInt(m_ResultList.get(i).get(CommonUtil.MESSAGE));
							}
						}
						
						if (localVersion != ServerVersion)
							showVersionUpAlert();
						else {
							m_IsLatestVersion = true;
							finish();
							Intent intent = new Intent(AppVersionCheck.this, UserLogin.class);	// create intent to invoke user login activity.
							startActivity(intent);	// start the intent to open user login screen.
						}
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					
					return true;
				}
			};

			HttpGetParameterAsyncTask task = new HttpGetParameterAsyncTask(url,	parameters, id, context, callable, progressVisible);
			task.execute();
	}
	
	/**
	 * This method pop up dialog box to upgrade the app version.
	 */
	private void showVersionUpAlert() {
		new AlertDialog.Builder(m_context)
				.setTitle(R.string.app_version_up_alert_title)
				.setMessage(R.string.app_version_up_alert_message)
				.setPositiveButton(R.string.alert_dialog_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								showLoadingProgressDialog();

								Thread mThread = new Thread(null, doBackgoundThreadprocessing, CommonUtil.BACKGROUND);
								mThread.start();
							}
						})
				.setNegativeButton(R.string.alert_dialog_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								finish();
							}
		}).create().show();
	}
	
	/**
	 * This method show the progress bar while App is upgrading.
	 */
	private void showLoadingProgressDialog() {
		this.showProgressDialog(getString(R.string.progress_message));
	}
	
	/**
	 * This method is used to show the progress bar while the App is upgrading.
	 * 
	 * @param message
	 */
	private void showProgressDialog(CharSequence message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
		}

		progressDialog.setMessage(message);
		progressDialog.show();
	}
	
	
	/**
	 * This method dismiss the progress bar when the App is done upgrading.
	 */
	private void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	Runnable doBackgoundThreadprocessing = new Runnable() {
		public void run() {
			try {
				CommonDownloader.DownloadFile(m_Url, m_context.getString(R.string.app_file_dir),m_context.getString(R.string.app_file_name));
			} catch (Exception e) {
				e.printStackTrace();
			}

			m_Handler.sendEmptyMessage(QUIZMAIN_DOWNLOAD_COMPLETE);
		}
	};

}
