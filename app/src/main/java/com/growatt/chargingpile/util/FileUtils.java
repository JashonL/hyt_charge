package com.growatt.chargingpile.util;


import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static boolean hasSdcard() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }


    public static void delete(String path, FilenameFilter filenameFilter) {
        File file=new File(path);
        File[] files = file.listFiles(filenameFilter);
        if (files!=null&&files.length>0){
            for (File f:
                    files) {
                f.delete();
            }
        }
    }



    /*
     * Java文件操作 获取不带扩展名的文件名
     *
     *  Created on: 2011-8-2
     *      Author: blueeagle
     */
    public static String getFileNameWithoutExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }


    /**
     * @param is       需要输出的流
     * @param filePath 保存文件路径
     * @return
     */
    public static String fileOut(InputStream is, File filePath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath,true));
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] bytes = new byte[1024];
            int len;
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes,0,len);
            }
            is.close();
            bis.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath.getAbsolutePath();
    }


    public static int copyStream(InputStream input, OutputStream output) throws Exception {
        byte[] buffer = new byte[1024*2];

        BufferedInputStream in = new BufferedInputStream(input, 1024*2);
        BufferedOutputStream out = new BufferedOutputStream(output, 1024*2);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024*2)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), e.toString());
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), e.toString());
            }
        }
        return count;
    }



}
