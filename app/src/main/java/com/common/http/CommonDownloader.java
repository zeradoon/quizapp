package com.common.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

public class CommonDownloader {

	public static void DownloadFile(String fileURL, String dir, String fileName) {
		try {
			File root = Environment.getExternalStorageDirectory(); 
			
			String[] temps = dir.split("/");
			
			File rootdir = new File(root +"/"+ temps[1]);
			if (!rootdir.exists()) {
				rootdir.mkdir();
			}
			
			File Directory = new File(root + dir);
			
			if (!Directory.exists()) {
				Directory.mkdir();
			}

			URL u = new URL(fileURL);
			HttpURLConnection c = (HttpURLConnection) u.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();
			FileOutputStream f = new FileOutputStream(new File(Directory, fileName));

			InputStream in = c.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = in.read(buffer)) > 0) {
				f.write(buffer, 0, len1);
			}
			f.close();
		} catch (Exception e) {
			Log.e("Downloader", e.getMessage());
		}
	}
}