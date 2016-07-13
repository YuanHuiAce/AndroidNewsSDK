package com.news.yazhidao.widget.imagewall;

import android.graphics.BitmapFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class BitmapUtil {
	public static BitmapFactory.Options getBitmapFactoryOption(int width,
															   int height, int twidth, int theight) {

		// Find the correct scale value. It should be the power of 2.
		final int REQUIRED_SIZE = 30;
		int width_tmp = width;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			scale *= 2;
		}

		// decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		int thumbnailHeight = (int) (height * new Float(twidth) / width);
		o2.outHeight = thumbnailHeight;
		o2.outWidth = (int) (twidth);
		return o2;
	}


	public static BitmapFactory.Options getBitmapFactoryOptions(String url)
			throws FileNotFoundException {
		try {
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, o);
			return o;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
