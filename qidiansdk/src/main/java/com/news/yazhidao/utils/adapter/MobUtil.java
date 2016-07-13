package com.news.yazhidao.utils.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.lang.reflect.Field;

/***
 * 手机特性工具类
 * 
 * @author Administrator
 *
 */
public class MobUtil {

	private Context context;
	private float scale;
	public float screenWidth;
	public float screenHeight;
	public float appHeight;

	public static int getStatusBarHeight(Context context) {

		Class<?> c = null;

		Object obj = null;

		Field field = null;

		int

		x = 0, statusBarHeight = 0;

		try

		{

			c = Class.forName("com.android.internal.R$dimen");

			obj = c.newInstance();

			field = c.getField("status_bar_height");

			x = Integer.parseInt(field.get(obj).toString());

			statusBarHeight = context.getResources().getDimensionPixelSize(x);

		} catch

		(Exception e1) {

			e1.printStackTrace();

		}

		return

		statusBarHeight;

	}

	public float getScaledX(float ratio) {
		return getScaled(false, ratio);
	}

	public float getScaledY(float ratio) {
		return getScaled(true, ratio);
	}

	public float getScaled(boolean isx, float ratio) {
		float length = isx ? screenWidth : screenHeight;
		return length * ratio;

	}

	public MobUtil(Context c) {
		context = c;
		instance = this;
		scale = context.getResources().getDisplayMetrics().density;
		setScreenWidth(context.getResources().getDisplayMetrics().widthPixels);
		setScreenHeight(context.getResources().getDisplayMetrics().heightPixels);
		statusbarHeight = getStatusBarHeight(context);
		appHeight = getScreenHeight()-statusbarHeight;
	}

	public int statusbarHeight;

	public float getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(float screenWidth) {
		this.screenWidth = screenWidth;
	}

	public float getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(float screenHeight) {
		this.screenHeight = screenHeight;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public static MobUtil instance;

	/** �Ƿ��п��õ����� */
	public boolean isNetWorkEnable() {
		return isWifi() || isMobile();
	}

	/**
	 * @Description Checking if wifi is available
	 * @param Base
	 *            Context
	 * @return true: wifi network is available, false: wifi network is
	 *         unavailable.
	 */
	public boolean isWifi() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isWifiAvail = ni.isAvailable();
		boolean isWifiConnect = ni.isConnected();
		return isWifiAvail && isWifiConnect;
	}

	/**
	 * @Description Checking if Mobile network is available
	 * @param Base
	 *            Context
	 * @return true: mobile network is available, false: mobile network is
	 *         unavailable.
	 */
	private boolean isMobile() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileAvail = ni.isAvailable();
		boolean isMobileConnect = ni.isConnected();
		return isMobileAvail && isMobileConnect;
	}

	/** �Ƿ���sd�� */
	public static boolean isSdcardExist() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ��ȡ��Ļ�ֱ���
	 * */
	public static int getResolution(Activity act) {
		int Width = 0;
		int Height = 0;
		Width = act.getWindowManager().getDefaultDisplay().getWidth();
		Height = act.getWindowManager().getDefaultDisplay().getHeight();
		return Width * Height;
	}

	public static boolean bChineseVersion() {
		return instance.context.getResources().getConfiguration().locale
				.toString().contains("zh");
	}
}
