package com.zhl.tabindicatorview.util;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View.MeasureSpec;

/**
 * 测绘工具类
 * @since 2014/11/19
 */
public final class MeasureUtil {
	public static final int RATION_WIDTH = 0;
	public static final int RATION_HEIGHT = 1;
	
	/**
	 * 获取屏幕尺寸
	 * 
	 * @param activity
	 *            Activity
	 * @return 屏幕尺寸像素值，下标为0的值为宽，下标为1的值为高
	 */
	public static int[] getScreenSize(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return new int[] { metrics.widthPixels, metrics.heightPixels };
	}

	/**
	 * 自定义控件获取测量后的尺寸方法
	 * 
	 * @param measureSpec
	 *            测量规格
	 * @param ratio
	 *            宽高标识
	 * @param resSize
	 *            资源（图片bitmap）的宽或高
	 * @param paddings
	 * 			 自定义控件的padding值int[]{left,top,right,bottom}
	 *
	 * @return 宽或高的测量值
	 */
	public static int getMeasureSize(int measureSpec, int ratio,int resSize,int[] paddings) {
		// 声明临时变量保存测量值
		int result = 0;
		/*
		 * 获取测量mode和size
		 */
		int mode = MeasureSpec.getMode(measureSpec);
		int size = MeasureSpec.getSize(measureSpec);
		/*
		 * 判断mode的具体值
		 */
		switch (mode) {
		case MeasureSpec.EXACTLY:// EXACTLY时直接赋值
			result = size;
			break;
		default:// 默认情况下将UNSPECIFIED和AT_MOST一并处理
			result+=resSize;
			if (ratio == RATION_WIDTH) {
				if(paddings!=null){
					result+=( paddings[0] + paddings[2]);
				}
			} else if (ratio == RATION_HEIGHT) {
				if(paddings!=null){
					result+=( paddings[1] + paddings[3]);
				}
			}
			/*
			 * AT_MOST时判断size和result的大小取小值
			 */
			if (mode == MeasureSpec.AT_MOST) {
				result = Math.min(result, size);
			}
			break;
		}
		return result;
	}
	public static Bitmap changeColor(Bitmap src, int keyColor, int replColor, int tolerance) {
		Bitmap copy = src.copy(Bitmap.Config.ARGB_8888, true);
		int width = copy.getWidth();
		int height = copy.getHeight();
		int[] pixels = new int[width * height];
		src.getPixels(pixels, 0, width, 0, 0, width, height);
		int sR = Color.red(keyColor);
		int sG = Color.green(keyColor);
		int sB = Color.blue(keyColor);
		int tR = Color.red(replColor);
		int tG = Color.green(replColor);
		int tB = Color.blue(replColor);
		float[] hsv = new float[3];
		Color.RGBToHSV(tR, tG, tB, hsv);
		float targetHue = hsv[0];
		float targetSat = hsv[1];
		float targetVal = hsv[2];

		for (int i = 0; i < pixels.length; ++i) {
			int pixel = pixels[i];

			if (pixel == keyColor) {
				pixels[i] = replColor;
			} else {
				int pR = Color.red(pixel);
				int pG = Color.green(pixel);
				int pB = Color.blue(pixel);

				int deltaR = Math.abs(pR - sR);
				int deltaG = Math.abs(pG - sG);
				int deltaB = Math.abs(pB - sB);

				if (deltaR <= tolerance && deltaG <= tolerance && deltaB <= tolerance) {
					Color.RGBToHSV(pR, pG, pB, hsv);
					hsv[0] = targetHue;
					hsv[1] = targetSat;
					hsv[2] *= targetVal;

					int mixTrgColor = Color.HSVToColor(Color.alpha(pixel), hsv);
					pixels[i] = mixTrgColor;
				}
			}
		}

		copy.setPixels(pixels, 0, width, 0, 0, width, height);

		return copy;
	}

	public static int dp2px(Context context,int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
	public static int sp2px(Context context,int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, context.getResources().getDisplayMetrics());
	}

	/**
	 * 测量文字的高度
	 * @param textsize px
	 * @return
     */
	public static int measureTextHeight(int textsize){
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(textsize);
		Paint.FontMetrics metrics = paint.getFontMetrics();
		return (int) Math.ceil(metrics.descent-metrics.ascent);
	}

	/**
	 * 测量文字的宽度
	 * @param text
	 * @param textsize px
     * @return
     */
	public static int measureTextWidth(String text,int textsize){
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(textsize);
		return (int) paint.measureText(text);
	}
}
