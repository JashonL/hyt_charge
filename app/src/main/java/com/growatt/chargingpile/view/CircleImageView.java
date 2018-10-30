package com.growatt.chargingpile.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


public class CircleImageView extends AppCompatImageView {

	private Context context;

	private int whiteSide = 5;

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	public CircleImageView(Context context) {
		super(context, null);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public void setWhiteSide(int whiteSide) {
		this.whiteSide = whiteSide;
	}
	 @Override  
	    protected void onDraw(Canvas canvas) {  
	        try {  
	            super.onDraw(canvas);  
	        } catch (Exception e) {  
	            System.out.println("Canvas: trying to use a recycled bitmap");  
	        }  
	    }  
	@Override
	public void setImageResource(int resId) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				resId);
		setImageBitmap(bitmap);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		if (bm == null) {
			return;
		}

		Bitmap roundBitmap = toRoundBitmap(bm);
		int width = roundBitmap.getWidth();
		int newWidth = width + whiteSide * 2;
		Bitmap output = Bitmap.createBitmap(newWidth, newWidth,
				Config.ARGB_8888);

		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
		paint.setColor(Color.WHITE);
		canvas.drawCircle(newWidth / 2, newWidth / 2, newWidth / 2,
				paint);
		canvas.drawBitmap(
				roundBitmap,
				new Rect(0, 0, width, width),
				new RectF(whiteSide, whiteSide, width
						+ whiteSide, width + whiteSide),
				paint);
		paint.setStyle(Paint.Style.STROKE);

		paint.setARGB(130, 130, 130,0);
		canvas.drawCircle(newWidth / 2, newWidth / 2, newWidth / 2,
				paint);
		if(bm != null && !bm.isRecycled()){ 
	        // 回收并且置为null
			bm.recycle(); 
			bm = null; 
	      } 
		if(roundBitmap != null && !roundBitmap.isRecycled()){ 
	        // 回收并且置为null
			roundBitmap.recycle(); 
			roundBitmap = null; 
	      } 
	     System.gc();
		super.setImageBitmap(output);
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *                传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;

			left = 0;
			top = 0;
			right = width;
			bottom = width;

			height = width;

			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;

			float clip = (width - height) / 2;

			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;

			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output=null;
		try {
			output = Bitmap.createBitmap(width, height,
					Config.ARGB_8888);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

		// 以下有两种方法画圆,drawRounRect和drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
		if(bitmap != null && !bitmap.isRecycled()){ 
	        // 回收并且置为null
			bitmap.recycle(); 
			bitmap = null; 
	      } 
	     System.gc();
		return output;
	}

}
