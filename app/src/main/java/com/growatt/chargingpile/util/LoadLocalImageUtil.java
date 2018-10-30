package com.growatt.chargingpile.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 *
 * @author tony
 * 
 */
public class LoadLocalImageUtil {
    private LoadLocalImageUtil() {
    }

    private static LoadLocalImageUtil instance = null;

    public static synchronized LoadLocalImageUtil getInstance() {
        if (instance == null) {
            instance = new LoadLocalImageUtil();
        }
        return instance;
    }


	public static Bitmap compress(InputStream is,Context c, View iv) {
		Bitmap bitmap = null;
		try{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buff = new byte[4096];
			int len = -1;
			while((len=is.read(buff))!=-1){
				out.write(buff, 0, len);
			}
			
			Options opts = new Options();
			opts.inJustDecodeBounds = true;

			BitmapFactory.decodeByteArray(out.toByteArray(),0, out.toByteArray().length, opts);
			int width = opts.outWidth;
			int height = opts.outHeight;

			int targetWidth  = iv.getWidth();
			int targetHeight = iv.getHeight();

			if(targetHeight==0||targetWidth==0){

				targetWidth = c.getResources().
						      getDisplayMetrics().
						      widthPixels;
				targetHeight = c.getResources().
						      getDisplayMetrics().
						      heightPixels;
			}
			
			int sampleSize = 1;
			
			if(width*1.0/targetWidth>1||height*1.0/targetHeight>1){
				sampleSize = (int) Math.ceil(
						Math.max(width*1.0/targetWidth, 
								 height*1.0/targetHeight));
			}
			
			opts.inSampleSize = sampleSize;
			opts.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().length,opts);
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return bitmap;
	}

}