package com.growatt.chargingpile.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;


import com.growatt.chargingpile.application.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AppUtils {
    private static String s;
    public static int a = 600;

    /**
     * ��ȡ��ǰ�ֻ��ϰ�װӦ�ó���İ���
     *
     * @param context
     * @return
     */
    public static List<String> obtainPhoneAppPkList(Context context) {
        final PackageManager packageManager = context.getPackageManager();// ��ȡpackagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// ��ȡ�����Ѱ�װ����İ���Ϣ
        List<String> pName = new ArrayList<String>();// ���ڴ洢�����Ѱ�װ����İ���
        // ��pinfo�н���������һȡ����ѹ��pName list��
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName;
    }

    /**
     * ʵ���ı����ƹ��� add by wangqianzhou
     *
     * @param content
     */
    @SuppressLint("NewApi")
    public static void copyText(String content, Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
        } else {
            // �õ������������
            ClipboardManager cmb = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(content.trim());

        }

    }

    /**
     * ��ȡ�ֻ���ʶ
     *
     * @param mContext
     * @return
     */
    public static String obtainDeviceId(Context mContext) {
        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceid = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceid)) {
            WifiManager manager = (WifiManager) mContext
                    .getSystemService(Context.WIFI_SERVICE);
            deviceid = manager.getConnectionInfo().getMacAddress();
        }
        return deviceid;
    }

    /**
     * �������� ���� 123456 ---456123. 1234567 5671234.
     *
     * @param str
     * @return
     */
    public static String reverseString(String str) {
        int length = str.length();
        if (TextUtils.isEmpty(str) || length == 1) {
            return str;
        }
        for (int i = 0; i < length / 2; i++) {
            str = str.substring(length - 1) + str.substring(0, length - 1);
        }
        return str;
    }

    /**
     * �õ����������������
     *
     * @param context �����activity
     * @param url
     */
    public static void openUrl(Context context, String url) {
        Intent intent = new Intent("android.intent.action.VIEW");
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static void grade(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static int getScreenWidth(Activity act) {
        return getScreen(act).widthPixels;
    }

    public static int getScreenHeight(Activity act) {
        return getScreen(act).heightPixels;
    }

    public static DisplayMetrics getScreen(Activity act) {
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static String getVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0";
    }

    /**
     * ��ȡ��ǰʱ��
     *
     * @return
     */
    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(true, true, tz.getRawOffset());
    }

    public static String createGmtOffsetString(boolean includeGmt,
                                               boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }

    /**
     * ��ȡ��ǰʱ���map
     */
    public static long newtime;

    public static Map<String, Object> Timemap(long l, long ll) {
        Map<String, Object> map = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        newtime = l + ll;
        String sd = sdf.format(new Date(l + ll));
        System.out.println(sd);
        map.put("year", sd.substring(0, 4));
        map.put("month", sd.substring(5, 7));
        map.put("day", sd.substring(8, sd.length()));
        return map;
    }

    //	Calendar c = Calendar.getInstance();
    //
    //	����ȡ��ϵͳ����:year = c.get(Calendar.YEAR)
    //
    //	����month = c.grt(Calendar.MONTH)
    //
    //	����day = c.get(Calendar.DAY_OF_MONTH)
    //
    //	����ȡ��ϵͳʱ�䣺hour = c.get(Calendar.HOUR_OF_DAY);
    //
    //	����minute = c.get(Calendar.MINUTE)
    //��SN�ŵ�Ч����
    public static String validateWebbox(String serialNum) {
        if (serialNum == null || "".equals(serialNum.trim())) {
            return "";
        }
        byte[] snBytes = serialNum.getBytes();
        int sum = 0;
        for (byte snByte : snBytes) {
            sum += snByte;
        }
        int B = sum % 8;
        String text = Integer.toHexString(sum * sum);
        int length = text.length();
        String resultTemp = text.substring(0, 2) + text.substring(length - 2, length) + B;
        String result = "";
        char[] charArray = resultTemp.toCharArray();
        for (char c : charArray) {
            if (c == 0x30 || c == 0x4F || c == 0x6F) {
                c++;
            }
            result += c;
        }
        return result.toUpperCase();
    }

    //	С������汣��λ��
    public static String getFormat(String s) {
        if ("0".equals(s)) {
            return "0";
        }
        if (TextUtils.isEmpty(s)) {
            return "0";
        }
        if (s.contains(",")) {
            s.replace(",", ".");
        }
        try {
            double d = ((int) (Double.parseDouble(s) * 100 + 0.5)) / 100.0;
            return d + "";
        } catch (Exception e) {
            e.printStackTrace();
            return s;
        }
    }

    public static Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);
//		if( baos.toByteArray().length / 1024>1024) {//�ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���	
//			baos.reset();//����baos�����baos
//			image.compress(Bitmap.CompressFormat.JPEG, 90, baos);//����ѹ��50%����ѹ��������ݴ�ŵ�baos��
//		}
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
        float hh = 800f;//�������ø߶�Ϊ800f
        float ww = 480f;//�������ÿ��Ϊ480f
        //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
        int be = 1;//be=1��ʾ������
        if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//�������ű���
        //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        if (isBm != null) {
            try {
                isBm.close();
                isBm = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (image != null && !image.isRecycled()) {
            image.recycle();
            image = null;
        }
        System.gc();
        return compressImage(bitmap, a);//ѹ���ñ�����С���ٽ�������ѹ��
    }

    private static Bitmap compressImage(Bitmap image, int in) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
        int options = 90;
        while (baos.toByteArray().length / 1024 > in || options == 60) {    //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��
            baos.reset();//����baos�����baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��
            options -= 5;//ÿ�ζ�����10
            //			System.out.println("����="+baos.toByteArray().length);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ
        if (image != null && !image.isRecycled()) {
            image.recycle();
            image = null;
        }
        if (isBm != null) {
            try {
                isBm.close();
                isBm = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.gc();
        return bitmap;
    }

    //ɾ�������ļ��з���
    public static boolean deleteSDFile(File file) {

        //fileĿ���ļ��о���·��

        if (file.exists()) { //ָ���ļ��Ƿ����
            if (file.isFile()) { //��·������ʾ���ļ��Ƿ���һ����׼�ļ�
                file.delete(); //ɾ�����ļ�
            } else if (file.isDirectory()) { //��·������ʾ���ļ��Ƿ���һ��Ŀ¼���ļ��У�
                File[] files = file.listFiles(); //�г���ǰ�ļ����µ������ļ�
                for (File f : files) {
                    deleteSDFile(f); //�ݹ�ɾ��
                    //Log.d("fileName", f.getName()); //��ӡ�ļ���
                }
            }
            file.delete(); //ɾ���ļ��У�song,art,lyric��
        }
        return true;
    }

    /**
     * ��ȡ�ļ�ָ���ļ���ָ����λ�Ĵ�С
     *
     * @param filePath �ļ�·��
     * @param sizeType ��ȡ��С������1ΪB��2ΪKB��3ΪMB��4ΪGB
     * @return doubleֵ�Ĵ�С
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("��ȡ�ļ���С", "��ȡʧ��!");
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * ���ô˷����Զ�����ָ���ļ���ָ���ļ��еĴ�С
     *
     * @param filePath �ļ�·��
     * @return ����õĴ�B��KB��MB��GB���ַ���
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("��ȡ�ļ���С", "��ȡʧ��!");
        }
        return FormetFileSize(blockSize);
    }

    /**
     * ��ȡָ���ļ���С
     *
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
            if (fis != null) {
                fis.close();
            }
        } else {
            file.createNewFile();
            Log.e("��ȡ�ļ���С", "�ļ�������!");
        }
        return size;
    }

    /**
     * ��ȡָ���ļ���
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * ת���ļ���С
     *
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * ת���ļ���С,ָ��ת��������
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    public static final int SIZETYPE_B = 1;//��ȡ�ļ���С��λΪB��doubleֵ
    public static final int SIZETYPE_KB = 2;//��ȡ�ļ���С��λΪKB��doubleֵ
    public static final int SIZETYPE_MB = 3;//��ȡ�ļ���С��λΪMB��doubleֵ
    public static final int SIZETYPE_GB = 4;//��ȡ�ļ���С��λΪGB��doubleֵ


    public static Map<String, Object> toHashMap(String json) {
        JSONObject jsonObject;
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            jsonObject = new JSONObject(json);
            // ��json�ַ���ת����jsonObject
            Iterator it = jsonObject.keys();
            // ����jsonObject���ݣ���ӵ�Map����
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                String value;
                value = (String) jsonObject.get(key);
                data.put(key, value);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return data;
    }


    public static int getLocale() {
        Locale locale = MyApplication.context.getResources().getConfiguration().locale;
        String language = locale.getLanguage().toLowerCase();
        int a;
        if (language.contains("cn") || language.contains("zh")) {
            a = 0;
            if (!locale.getCountry().toLowerCase().equals("cn")){
                a = 2;
            }
        } else if (language.contains("en")) {
            a = 1;

        } else {
            a = 2;
        }
        return a;
    }
//	private static DisplayImageOptions options=null;
//	public static DisplayImageOptions Options(){
//		if(options==null){
//			 options = new DisplayImageOptions.Builder()
//			.showImageOnLoading(R.drawable.loading) // ����ͼƬ�����ڼ���ʾ��ͼƬ
//			.showImageForEmptyUri(R.drawable.loading) // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
//			.showImageOnFail(R.drawable.loading) // ����ͼƬ���ػ��������з���������ʾ��ͼƬ
//			.cacheInMemory(false) // default  �������ص�ͼƬ�Ƿ񻺴����ڴ���
//			.cacheOnDisk(true) // default  �������ص�ͼƬ�Ƿ񻺴���SD����
//			.bitmapConfig(Bitmap.Config.RGB_565)
//			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//			.build();
//			}
//		return options;
//	}

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * ��ȡ������Ƶʱ��
     *
     * @param mUri ������Ƶ·��
     * @return ʱ����00:00����
     */
    public static SimpleDateFormat sdf = null;

    public static String getVideoDuration(String mUri) {
        String duration = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                mmr.setDataSource(mUri);
            }
            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (duration != null) {
                if (sdf == null) {
                    sdf = new SimpleDateFormat("mm:ss");
                }
                duration = sdf.format(Double.parseDouble(duration));
            }
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        return duration;
    }

    /**
     * ��ȡ�����ļ��б�
     *
     * @param Path        ����Ŀ¼
     * @param Extension   ��չ��(�жϵ��ļ����͵ĺ�׺��)
     * @param IsIterative �Ƿ�������ļ���
     * @return �б�
     */
    public static List<String> getFilesList(String Path, String Extension,
                                            boolean IsIterative) // ����Ŀ¼����չ��(�жϵ��ļ����͵ĺ�׺��)���Ƿ�������ļ���
    {
        List<String> list = new ArrayList<String>(); // ��� List
        File[] files = new File(Path).listFiles();
        if (files == null) return list;
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile()) {
                if (f.getPath()
                        .substring(f.getPath().length() - Extension.length())
                        .equals(Extension)) // �ж���չ��
                    list.add(f.getPath());
                if (!IsIterative)
                    break;  //����������Ӽ�Ŀ¼������
            } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) // ���Ե��ļ��������ļ�/�ļ��У�
                getFilesList(f.getPath(), Extension, IsIterative);  //����Ϳ�ʼ�ݹ���
        }
        return list;
    }

    /**
     * ��ȡ��Ƶ������ͼ
     * ��ͨ��ThumbnailUtils������һ����Ƶ������ͼ��Ȼ��������ThumbnailUtils������ָ����С������ͼ��
     * �����Ҫ������ͼ�Ŀ�͸߶�С��MICRO_KIND��������Ҫʹ��MICRO_KIND��Ϊkind��ֵ���������ʡ�ڴ档
     *
     * @param videoPath ��Ƶ��·��
     * @param width     ָ�������Ƶ����ͼ�Ŀ��
     * @param height    ָ�������Ƶ����ͼ�ĸ߶ȶ�
     * @param kind      ����MediaStore.Images.Thumbnails���еĳ���MINI_KIND��MICRO_KIND��
     *                  ���У�MINI_KIND: 512 x 384��MICRO_KIND: 96 x 96
     * @return ָ����С����Ƶ����ͼ
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                           int kind) {
        Bitmap bitmap = null;
        // ��ȡ��Ƶ������ͼ  
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * sdcard�Ƿ����
     *
     * @return
     */
    public static boolean sdcardIsExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * [获取应用程序包名称信息]
     *
     * @param context
     * @return 当前应用的包名
     */
    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断应用是否已经启动 * * @param context 上下文对象 * @param packageName 要判断应用的包名 * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
            for (int i = 0; i < processInfos.size(); i++) {
                if (processInfos.get(i).processName.equals(packageName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断本应用是否已经位于最前端
     *
     * @param context
     * @return 本应用已经位于最前端时，返回 true；否则返回 false
     */
    public static boolean isRunningForeground(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
            String processName = context.getApplicationInfo().processName;
            /**枚举进程*/
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
                if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (appProcessInfo.processName.equals(processName)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 返回app运行状态
     *
     * @param context
     *            一个context
     * @param packageName
     *            要判断应用的包名
     * @return int 1:前台 2:后台 0:不存在
     */
    public static int isAppAliveNew(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> listInfos = activityManager
                .getRunningTasks(20);
        // 判断程序是否在栈顶
        if (listInfos.get(0).topActivity.getPackageName().equals(packageName)) {
            return 1;
        } else {
            // 判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : listInfos) {
                if (info.topActivity.getPackageName().equals(packageName)) {
                    return 2;
                }
            }
            return 0;// 栈里找不到，返回3
        }
    }
}

