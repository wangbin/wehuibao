package com.wehuibao.util.net;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;

public class ImageDownloader {
	public static String downloadImage(Context context, String image_url,
			String directory) {
		String root = context.getExternalFilesDir(null).toString();
		File avatarDir = new File(root + directory);
		String image_name = image_url.substring(image_url.lastIndexOf('/') + 1);
		if (image_name.indexOf('?') != -1) {
			image_name = image_name.substring(0, image_name.lastIndexOf('?'));
		}
		File img = new File(avatarDir.toString() + '/' + image_name);
		if (img.exists()) {
			return img.getAbsolutePath();
		}
		if (!avatarDir.exists()) {
			avatarDir.mkdirs();
		}

		try {
			URL url = new URL(image_url);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setReadTimeout(50000);
			connection.connect();
			InputStream in = connection.getInputStream();
			FileOutputStream fos = new FileOutputStream(img.getPath());
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			byte[] buffer = new byte[1024];

			try {
				while (in.read(buffer) > 0) {
					bos.write(buffer);
				}
				bos.flush();
			} finally {
				in.close();
				fos.getFD().sync();
				bos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return img.getAbsolutePath();
	}
}
