package com.news.yazhidao.utils.scn;

import android.app.Activity;

public class ScreenSchema {
	private static int w;
	private static int h;
	private static int densityDpi;
	private static float density;
	private static boolean b;

	
public static void init(Activity ctx){
	if (!b) {

		b = true;


		ScreenSchema.w = ctx.getWindowManager().getDefaultDisplay()
				.getWidth();
		ScreenSchema.h = ctx.getWindowManager().getDefaultDisplay()
				.getHeight();
		ScreenSchema.densityDpi = ctx.getResources().getDisplayMetrics().densityDpi;
		ScreenSchema.density = ctx.getResources().getDisplayMetrics().density;

		Float densityDpi = new Float(
				Cfg.densityDpi * new Float(ScreenSchema.w) / Cfg.width);

		Float density = new Float(densityDpi / 160);
		ScreenSchema.densityDpi = densityDpi.intValue();

		ScreenSchema.density = density;

	}
	ctx.getResources().getDisplayMetrics().densityDpi = ScreenSchema.densityDpi;
	ctx.getResources().getDisplayMetrics().density = ScreenSchema.density;

}
}
