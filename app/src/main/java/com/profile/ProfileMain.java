package com.profile;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.CommonUtil;
import com.common.NetworkUtil;
import com.common.async.AbstractAsyncActivity;
import com.common.concurrent.BetterAsyncTaskCallable;
import com.common.concurrent.HttpGetParameterAsyncTask;
import com.common.concurrent.HttpPostParameterAsyncTask;
import com.common.http.CommonDownloader;
import com.event.GeneralEvent;
import com.login.UserLogin;
import com.ranking.ImageDownloader;
import com.ranking.RankingCommon;
import com.ranking.RankingSelect;
import com.skcc.portal.skmsquiz.R;

/**
 * @author jungungi
 * 
 */
public class ProfileMain extends AbstractAsyncActivity implements
		OnClickListener {

	private static final String LOG_TAG = ProfileMain.class.getSimpleName();
	private static final String DOWNLOAD_USER_DETAIL_PATH = "getProfileUserDetail.do";
	private static final String DOWNLOAD_COMPANY_PATH = "getCompany.do";
	private static final String DOWNLOAD_RANKING_PATH = "getProfileUserRank.do";
	private static final String UPDATE_USER_DATA_PATH = "updateProfileUser.do";
	private static final String UPLOAD_USER_IMAGE_PATH = "uploadUserImage.do";
	private static final String DOWNLOAD_USER_IMAGE_PATH = "downloadUserImage.do";
	
	
	private static final int CAPTURE_IMAGE_SIZE = 160;

	private static final int PICK_FROM_CAMERA = 0;
	private static final int PICK_FROM_ALBUM = 1;
	private static final int CROP_FROM_CAMERA = 2;

	private static final int DIALOG_NICKNAME = 4;
	private static final int DIALOG_COMPANY = 5;
	private static final int DIALOG_OPENYN = 6;
	private static final int DIALOG_DEPARTMENT = 7;

	private Uri m_imageCaptureUri;
	private String m_userImagePath;

	private ImageView m_profile_main_user_img;
	private ImageView m_profile_main_user_rank_img;

	private TextView m_profile_main_user_nickname;
	private TextView m_profile_main_user_company;
	private TextView m_profile_main_open_yn;
	private TextView m_profile_main_user_rank_name;
	private TextView m_profile_main_user_score;
	private TextView m_profile_main_user_participate_cnt;
	private TextView m_profile_main_user_rank_percent;
	private TextView m_profile_main_user_rank_detail;
	private TextView m_profile_main_user_department;

	private LinearLayout m_profile_main_user_nickname_linearLayout;
	private LinearLayout m_profile_main_user_company_linearLayout;
	private LinearLayout m_profile_main_open_yn_linearLayout;
	private LinearLayout m_profile_main_user_department_linearLayout;
	
	private ImageView m_btn_home;

	private ArrayList<HashMap<String, String>> m_companyList;

	private String[] m_companyNameData = null;
	private String[] m_companyCodeData = null;

	private String m_openYN = "";
	private String m_userNickName = "";
	private String m_companyName = "";

	private Context m_context = this;
	private final ImageDownloader imageDownloader = new ImageDownloader();
	
	private String m_user_id;
	private String m_comp_cd;
	private String m_user_dept;
	
	public static final String PREFS_NAME = "MyPrefsFile"; 
	private static final String PREF_USERID = "userid";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_REMEMBERME = "true";
	private static final String PREF_USERDEPT = "userdept";
	private static final String PREF_USERCOMPCD = "usercompcd";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreate");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.profile_main);
		
		m_user_id = getIntent().getStringExtra(CommonUtil.USER_ID);
		m_comp_cd = getIntent().getStringExtra(CommonUtil.COMP_CD);
		m_user_dept = getIntent().getStringExtra(CommonUtil.USER_DEPT);

		TextView tvTitle = (TextView) findViewById(R.id.header_title_id);
		tvTitle.setText(getString(R.string.profile_header_title));
		
		m_btn_home = (ImageView)findViewById(R.id.btn_home);
		m_btn_home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});


		m_profile_main_user_nickname_linearLayout = (LinearLayout) findViewById(R.id.profile_main_user_nickname_linearLayout);
		m_profile_main_user_nickname_linearLayout.setOnClickListener(this);

		m_profile_main_user_company_linearLayout = (LinearLayout) findViewById(R.id.profile_main_user_company_linearLayout);
		m_profile_main_user_company_linearLayout.setOnClickListener(this);

		m_profile_main_open_yn_linearLayout = (LinearLayout) findViewById(R.id.profile_main_open_yn_linearLayout);
		m_profile_main_open_yn_linearLayout.setOnClickListener(this);

		m_profile_main_user_department_linearLayout = (LinearLayout) findViewById(R.id.profile_main_user_department_linearLayout);
		m_profile_main_user_department_linearLayout.setOnClickListener(this);

		m_profile_main_user_img = (ImageView) findViewById(R.id.profile_main_user_img);
		m_profile_main_user_img.setOnClickListener(this);

		m_profile_main_user_department = (TextView) findViewById(R.id.profile_main_user_department);

		m_profile_main_user_nickname = (TextView) findViewById(R.id.profile_main_user_nickname);
		m_profile_main_user_nickname.setOnClickListener(this);

		m_profile_main_user_company = (TextView) findViewById(R.id.profile_main_user_company);
		m_profile_main_user_company.setOnClickListener(this);

		m_profile_main_open_yn = (TextView) findViewById(R.id.profile_main_open_yn);
		m_profile_main_open_yn.setOnClickListener(this);

		m_profile_main_user_rank_img = (ImageView) findViewById(R.id.profile_main_user_rank_img);
		m_profile_main_user_rank_name = (TextView) findViewById(R.id.profile_main_user_rank_name);
		m_profile_main_user_score = (TextView) findViewById(R.id.profile_main_user_score);
		m_profile_main_user_participate_cnt = (TextView) findViewById(R.id.profile_main_user_participate_cnt);
		m_profile_main_user_rank_percent = (TextView) findViewById(R.id.profile_main_user_rank_percent);
		m_profile_main_user_rank_detail = (TextView) findViewById(R.id.profile_main_user_rank_detail);

		// new DownloadStatesTask().execute();
		// new DownloadUserDetailTask().execute();
		downloadUserDetail();
		// new DownloadRankingTask().execute();
		// new DownloadCompanyTask().execute();
		downloadRanking(m_user_id);
		downloadCompany();
	}

	@Override
	public void onClick(View v) {
		if (v.equals(m_profile_main_user_img)) {
			DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doTakePhotoAction();
				}
			};

			DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doTakeAlbumAction();
				}
			};

			DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			};

			new AlertDialog.Builder(this).setTitle("업로드할 이미지 선택")
					.setPositiveButton("사진촬영", cameraListener)
					.setNeutralButton("앨범선택", albumListener)
					.setNegativeButton("취소", cancelListener).show();
		} else if (v.equals(m_profile_main_user_nickname_linearLayout)) {
			showDialog(DIALOG_NICKNAME);
		} else if (v.equals(m_profile_main_user_company_linearLayout)) {
			showDialog(DIALOG_COMPANY);
		} else if (v.equals(m_profile_main_open_yn_linearLayout)) {
			showDialog(DIALOG_OPENYN);
		} else if (v.equals(m_profile_main_user_department_linearLayout)) {
			showDialog(DIALOG_DEPARTMENT);
		} 
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case CROP_FROM_CAMERA: {

			// 크롭이 된 이후의 이미지를 넘겨 받습니다.
			// 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
			// 임시 파일을 삭제합니다.
			new UpdateUserImageTask().execute(m_user_id);

			// 임시 파일 삭제
			File f = new File(m_imageCaptureUri.getPath());
			if (f.exists()) {
				f.delete();
			}

			break;
		}

		case PICK_FROM_ALBUM: {
			// 이후의 처리가 카메라와 같으므로 일단 break없이 진행합니다.
			// 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.

			m_imageCaptureUri = data.getData();
		}

		case PICK_FROM_CAMERA: {
			// 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
			// 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.

			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(m_imageCaptureUri, "image/*");

			intent.putExtra("outputX", CAPTURE_IMAGE_SIZE);
			intent.putExtra("outputY", CAPTURE_IMAGE_SIZE);
			//intent.putExtra("aspectX", 1);
			//intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);

			// 임시로 사용할 파일의 경로를 생성
			m_userImagePath = Environment.getExternalStorageDirectory()
					+ File.separator + "tmp1_"
					+ String.valueOf(System.currentTimeMillis()) + ".png";

			Uri userImageUri = Uri.fromFile(new File(m_userImagePath));

			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					userImageUri);

			startActivityForResult(intent, CROP_FROM_CAMERA);

			break;
		}
		}
	}

	protected void signOutUser() {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().remove(PREF_USERID).remove(PREF_PASSWORD).remove(PREF_REMEMBERME).commit();
		Intent intent = new Intent(ProfileMain.this, UserLogin.class);	// create intent to invoke user login activity.
		startActivity(intent);	// start the intent to open user login screen.
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.profilemain, menu);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		
	    switch (item.getItemId()) {
	    	case R.id.home:
	    		intent = new Intent(ProfileMain.this, com.main.QuizMain.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();
	            return true;
	        case R.id.profile:
	        	intent = new Intent(ProfileMain.this, com.profile.ProfileMain.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();
	            return true;
	        case R.id.ranking:
	        	intent = new Intent(ProfileMain.this, com.ranking.RankingSelect.class);
	        	
	    		intent.putExtra(CommonUtil.USER_ID, m_user_id);
				intent.putExtra(CommonUtil.USER_DEPT, m_user_dept);
				intent.putExtra(CommonUtil.COMP_CD, m_comp_cd);
	        	
				startActivity(intent);
				finish();

	            return true;
	        case R.id.events:
	        	intent = new Intent(ProfileMain.this, com.event.GeneralEvent.class);
	        	
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_COMPANY:

			int checkedCompanyPosition = 0;

			for (int i = 0; i < m_companyNameData.length; i++) {
				if (m_companyNameData[i].equalsIgnoreCase(m_companyName)) {
					checkedCompanyPosition = i;
					break;
				}
			}

			return new AlertDialog.Builder(m_context)
					.setIcon(R.drawable.sk_logo)
					.setTitle(R.string.profile_main_company_alert_dialog_title)
					.setSingleChoiceItems(m_companyNameData,
							checkedCompanyPosition,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									if (!m_comp_cd
											.equalsIgnoreCase(m_companyCodeData[whichButton])) {

										m_comp_cd = m_companyCodeData[whichButton];
										m_profile_main_user_company.setText(m_companyNameData[whichButton]);

										// 사용자 정보 업데이트
										updateUserData(m_user_id, m_userNickName, m_comp_cd, m_user_dept, m_openYN);
									}

									dialog.dismiss();
								}
							}).create();

		case DIALOG_OPENYN:
			int checkedOpenYNPosition;

			if (m_openYN.equalsIgnoreCase(CommonUtil.FLAG_Y))
				checkedOpenYNPosition = 0;
			else
				checkedOpenYNPosition = 1;

			return new AlertDialog.Builder(m_context)
					.setIcon(R.drawable.sk_logo)
					.setTitle(R.string.profile_main_company_alert_dialog_title)
					.setSingleChoiceItems(
							R.array.profile_main_company_alert_dialog_open_yn,
							checkedOpenYNPosition,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									int oldPosition = 0;
									if (m_openYN.equalsIgnoreCase(CommonUtil.FLAG_N)) {
										oldPosition = 1;
									}
									if (oldPosition != whichButton) {
										if (whichButton == 0) {
											m_profile_main_open_yn
													.setText(R.string.profile_main_open_yn_yes);
											m_openYN = CommonUtil.FLAG_Y;
										} else {
											m_profile_main_open_yn
													.setText(R.string.profile_main_open_yn_no);
											m_openYN = CommonUtil.FLAG_N;
										}

										// 사용자 정보 업데이트
										updateUserData(m_user_id, m_userNickName, m_comp_cd, m_user_dept, m_openYN);

									}
									dialog.dismiss();
								}
							}).create();
		case DIALOG_NICKNAME: {
			// This example shows how to add a custom layout to an AlertDialog
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(
					R.layout.alert_dialog_nickname_entry, null);
			final EditText et_AlertNickName = (EditText) textEntryView
					.findViewById(R.id.alert_dialog_nickname_entry_nickname);
			et_AlertNickName.setText(m_userNickName);

			return new AlertDialog.Builder(m_context)
					.setIcon(R.drawable.sk_logo)
					.setTitle(R.string.profile_main_nickname_alert_dailog_title)
					.setView(textEntryView)
					.setPositiveButton(R.string.alert_dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String editNickName = et_AlertNickName
											.getText().toString();
									if (!m_userNickName.equalsIgnoreCase(editNickName)) {

										m_profile_main_user_nickname
												.setText(editNickName);
										m_userNickName = editNickName;

										// 사용자 정보 업데이트
										updateUserData(m_user_id, m_userNickName, m_comp_cd, m_user_dept, m_openYN);
									}
								}
							})
					.setNegativeButton(R.string.alert_dialog_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							}).create();
		}
		case DIALOG_DEPARTMENT: {
			// This example shows how to add a custom layout to an AlertDialog
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(
					R.layout.alert_dialog_department, null);
			final EditText et_departmentName = (EditText) textEntryView
					.findViewById(R.id.alert_dialog_department_edittext);
			et_departmentName.setText(m_user_dept);

			return new AlertDialog.Builder(m_context)
					.setIcon(R.drawable.sk_logo)
					.setTitle(R.string.profile_main_department_alert_dialog_title)
					.setView(textEntryView)
					.setPositiveButton(R.string.alert_dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String str_departmentName = et_departmentName
											.getText().toString();
									if (!m_user_dept.equalsIgnoreCase(str_departmentName)) {

										m_profile_main_user_department
												.setText(str_departmentName);
										m_user_dept = str_departmentName;

										// 사용자 정보 업데이트
										updateUserData(m_user_id, m_userNickName, m_comp_cd, m_user_dept, m_openYN);
									}
								}
							})
					.setNegativeButton(R.string.alert_dialog_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							}).create();
		}
		}

		return super.onCreateDialog(id);
	}

	// ***************************************
	// Private classes
	// ***************************************
	private class UpdateUserImageTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				String userId = "";

		    	if (params[0] != null)
		    		userId = params[0];
		    	
				File file = new File(m_userImagePath);
				HttpClient client = new DefaultHttpClient();

				String postURL = getString(R.string.base_uri)
						+ UPLOAD_USER_IMAGE_PATH;

				HttpPost post = new HttpPost(postURL);
				FileBody bin = new FileBody(file, "application/octet-stream");
				bin.getMimeType();
				MultipartEntity reqEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				reqEntity.addPart("file", bin);
				reqEntity.addPart(CommonUtil.USER_ID, new StringBody(userId));
				post.setEntity(reqEntity);
				HttpResponse response = client.execute(post);
				org.apache.http.HttpEntity resEntity = response.getEntity();

				String strResponse = "";

				if (resEntity != null) {
					strResponse = RankingCommon.httpHelperRequest(response);
				}
				return strResponse;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			dismissProgressDialog();

			Log.d(LOG_TAG, "UpdateUserDataTask");
			if (result != null)
				Log.d(LOG_TAG, result);

			refreshUserImageData(result);
		}
	}

	private void refreshUserImageData(String result) {
		if (result != null) {
			// 임시 파일 삭제
			File f = new File(m_userImagePath);
			if (f.exists()) {
				f.delete();
			}
			

		} else {
			Toast.makeText(this, "I got null, something happened!",
					Toast.LENGTH_LONG).show();
		}

		// new DownloadUserDetailTask().execute();
		downloadUserDetail();
	}

	/**
	 * 카메라에서 이미지 가져오기
	 */
	private void doTakePhotoAction() {
		/*
		 * 참고 해볼곳 http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
		 * http://stackoverflow
		 * .com/questions/1050297/how-to-get-the-url-of-the-captured-image
		 * http://www.damonkohler.com/2009/02/android-recipes.html
		 * http://www.firstclown.us/tag/android/
		 */

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// 임시로 사용할 파일의 경로를 생성
		String url = "tmp_" + String.valueOf(System.currentTimeMillis())
				+ ".jpg";
		m_imageCaptureUri = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), url));

		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				m_imageCaptureUri);
		// 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
		// intent.putExtra("return-data", true);
		startActivityForResult(intent, PICK_FROM_CAMERA);
	}

	/**
	 * 앨범에서 이미지 가져오기
	 */
	private void doTakeAlbumAction() {
		// 앨범 호출
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	private String longDouble2String(int size, double value) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(size);
		nf.setGroupingUsed(false);
		return nf.format(value);
	}

	private void downloadUserDetail() {
		String url = getString(R.string.base_uri) + DOWNLOAD_USER_DETAIL_PATH;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_ID, m_user_id);
		parameters.put(CommonUtil.IMEI, NetworkUtil.getIMEI(m_context));

		Context context = m_context;
		boolean progressVisible = true;
		int id = 111;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0) {
					m_userNickName = list.get(0).get(CommonUtil.USER_NM);
					m_comp_cd = list.get(0).get(CommonUtil.USER_COMP_CD);
					m_companyName = list.get(0).get(CommonUtil.COMP_NM);
					m_openYN = list.get(0).get(CommonUtil.OPEN_YN);
					m_user_dept = list.get(0).get(CommonUtil.USER_DEPT);
					if (m_userNickName.equals("")) {
						m_profile_main_user_nickname
								.setText(R.string.profile_main_user_default_text);
					} else {
						m_profile_main_user_nickname.setText(m_userNickName);
					}

					if (m_comp_cd.equals("")) {
						m_profile_main_user_company
								.setText(R.string.profile_main_user_default_text);
					} else {
						m_profile_main_user_company.setText(m_companyName);
					}
					if (m_openYN.equalsIgnoreCase(CommonUtil.FLAG_Y)) {
						m_profile_main_open_yn
								.setText(R.string.profile_main_open_yn_yes);
					} else {
						m_profile_main_open_yn
								.setText(R.string.profile_main_open_yn_no);
					}  
					if (m_user_dept.equals("")) {
						m_profile_main_user_department.setText(R.string.profile_main_user_default_text);
					} else {
						m_profile_main_user_department.setText(m_user_dept);
					}

					if (list.get(0).get(CommonUtil.USER_IMG).lastIndexOf(".png") > 1) {
						String imgUrl = getString(R.string.base_uri) + DOWNLOAD_USER_IMAGE_PATH + "?" + CommonUtil.F_DOWNLOAD + "="	+ list.get(0).get("USER_IMG");

						imageDownloader.download(imgUrl,
								m_profile_main_user_img);
					} else {
						m_profile_main_user_img
								.setImageResource(R.drawable.photo_noimage);
					}
					
					getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
					.edit()
					.putString(PREF_USERDEPT, m_user_dept)
					.putString(PREF_USERCOMPCD, m_comp_cd).commit();
				} else {
					Toast.makeText(m_context,
							R.string.profile_main_user_info_empty_message,
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible);
		task.execute();
	}

	/**
	 * Method ID : downloadCompany Method 설명 : 계열사 정보를 가지고 오는 메서드로써 최초
	 * onCreate에서 한번만 실행된다. 최초작성일 : 2011. 9. 14. 작성자 : jungungi 변경이력 :
	 */
	private void downloadCompany() {
		String url = getString(R.string.base_uri) + DOWNLOAD_COMPANY_PATH;
		HashMap<String, String> parameters = null;

		Context context = m_context;
		boolean progressVisible = true;
		int id = 111;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				m_companyList = list;
				if (m_companyList.size() > 0) {
					m_companyNameData = new String[m_companyList.size()];
					m_companyCodeData = new String[m_companyList.size()];
					for (int i = 0; i < m_companyList.size(); i++) {
						m_companyNameData[i] = m_companyList.get(i).get(CommonUtil.COMP_NM);
						m_companyCodeData[i] = m_companyList.get(i).get(CommonUtil.COMP_CD);
					}

				} else {
					Toast.makeText(m_context,
							R.string.profile_main_company_data_empty_message,
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible);
		task.execute();
	}

	/**
	 * Method ID : downloadRanking Method 설명 : 사용자의 랭킹정보를 조회한다. onCreate()에서 최초
	 * 한번만 호출된다. 최초작성일 : 2011. 9. 14. 작성자 : jungungi 변경이력 :
	 */
	private void downloadRanking(String m_user_id) {
		String url = getString(R.string.base_uri) + DOWNLOAD_RANKING_PATH;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_ID, m_user_id);
		parameters.put(CommonUtil.IMEI, NetworkUtil.getIMEI(m_context));

		Context context = m_context;
		boolean progressVisible = true;
		int id = 111;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0) {
					String strScore = list.get(0).get(CommonUtil.INDIVIDUAL_SCORE) + " 점";
					String strParticipate = list.get(0).get(CommonUtil.JOIN_CNT)
							+ " 회";
					String strRank = list.get(0).get(CommonUtil.RANKING);
					String strTotalCnt = list.get(0).get(CommonUtil.TOTAL_USER_CNT);

					m_profile_main_user_score.setText(strScore);
					m_profile_main_user_participate_cnt.setText(strParticipate);

					// 상위
					double d_Rank = Double.parseDouble(strRank);
					double d_TotalCnt = Double.parseDouble(strTotalCnt);

					String s_rankPercent = "상위 "
							+ longDouble2String(1, d_Rank / d_TotalCnt * 100)
							+ " %";
					m_profile_main_user_rank_percent.setText(s_rankPercent);

					String s_rankDetail = strRank + " 위" + " (총 " + strTotalCnt
							+ "명)";
					m_profile_main_user_rank_detail.setText(s_rankDetail);

					String str_gradeName = RankingCommon.getGradeName(
							m_context, strRank, strTotalCnt);
					m_profile_main_user_rank_name.setText(str_gradeName);

					int str_gradeImage = RankingCommon.getGradeImage(m_context,
							strRank, strTotalCnt);
					m_profile_main_user_rank_img
							.setImageResource(str_gradeImage);

				}
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible);
		task.execute();
	}

	private void updateUserData(String m_user_id, String m_userNickName, String m_comp_cd, String m_user_dept, String m_openYN) {
		String url = getString(R.string.base_uri) + UPDATE_USER_DATA_PATH;
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(CommonUtil.USER_ID, m_user_id);
		parameters.put(CommonUtil.USER_NM, m_userNickName);

		if (!m_comp_cd.equals("")) {
			parameters.put(CommonUtil.USER_COMP_CD, m_comp_cd);
		}
		if (!m_user_dept.equals("")) {
			parameters.put(CommonUtil.USER_DEPT, m_user_dept);
		}
		parameters.put(CommonUtil.OPEN_YN, m_openYN);
		parameters.put(CommonUtil.UPDATE_ID, m_user_id);

		Context context = m_context;
		boolean progressVisible = true;
		int id = 111;
		BetterAsyncTaskCallable callable = new BetterAsyncTaskCallable() {

			@Override
			public boolean getAsyncTaskResult(
					ArrayList<HashMap<String, String>> list, int id) {
				if (list.size() > 0) {
					
					// new DownloadUserDetailTask().execute();
					if (list.get(0).containsKey(CommonUtil.ERROR)) {
						if(list.get(0).get(CommonUtil.ERROR).contains(CommonUtil.ERROR_IDX_M_USER_1)) {
							Toast.makeText(
									m_context,
									R.string.profile_main_update_user_nickname_duplicate_error_msg,
									Toast.LENGTH_SHORT).show();
							showDialog(DIALOG_NICKNAME);
						}
					} else {
						downloadUserDetail();
					}
				} 
				return true;
			}
		};

		HttpPostParameterAsyncTask task = new HttpPostParameterAsyncTask(url,
				parameters, id, context, callable, progressVisible);
		task.execute();
	}

}