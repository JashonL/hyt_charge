package com.growatt.chargingpile.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import android.text.TextUtils;

import com.growatt.chargingpile.application.MyApplication;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author zhengzhong on 2016/8/6 16:16
 *         Email zheng_zhong@163.com
 */
public class PhotoUtil {
//    public static final String authority = ShineApplication.getInstance().getApplicationInfo().packageName ;
//    public static final String authority = "com.growatt.shinephone.fileprovider"  ;
    public static final String authority = AppUtils.getPackageName(MyApplication.context) + ".fileProvider";
    /**
     * @param fragment    当前activity
     * @param imageUri    拍照后照片存储路径
     * @param requestCode 调用系统相机请求码
     */
    public static void takePicture(Fragment fragment, Uri imageUri, int requestCode) {
        //调用系统相机
        Intent intentCamera = new Intent();
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        fragment.startActivityForResult(intentCamera, requestCode);
    }

    /**
     * @param activity    当前fragment
     * @param imageUri    拍照后照片存储路径
     * @param requestCode 调用系统相机请求码
     */
    public static void takePicture(Activity activity, Uri imageUri, int requestCode) {
        //调用系统相机
        Intent intentCamera = new Intent();
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        }*/
        activity.startActivityForResult(intentCamera, requestCode);
    }


    /**
     * @param fragment    当前fragment
     * @param requestCode 打开相册的请求码
     */
    public static void openPic(Fragment fragment, int requestCode) {
//        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        fragment.startActivityForResult(albumIntent, requestCode);
    }

    /**
     * @param requestCode 打开相册的请求码
     */
    public static void openPic(Activity act, int requestCode) {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        act.startActivityForResult(albumIntent, requestCode);
    }

    /**
     * @param fragment    当前fragment
     * @param orgUri      剪裁原图的Uri
     * @param desUri      剪裁后的图片的Uri
     * @param aspectX     X方向的比例
     * @param aspectY     Y方向的比例
     * @param width       剪裁图片的宽度
     * @param height      剪裁图片高度
     * @param requestCode 剪裁图片的请求码
     */
    public static void cropImageUri(Fragment fragment, Uri orgUri, Uri desUri, int aspectX, int aspectY, int width, int height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(orgUri, "image/*");
        //发送裁剪信号
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
        //1-false用uri返回图片
        //2-true直接用bitmap返回图片（此种只适用于小图片，返回图片过大会报错）
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        fragment.startActivityForResult(intent, requestCode);
    }


    /**
     * @param activity    当前activity
     * @param orgUri      剪裁原图的Uri
     * @param desUri      剪裁后的图片的Uri
     * @param aspectX     X方向的比例
     * @param aspectY     Y方向的比例
     * @param width       剪裁图片的宽度
     * @param height      剪裁图片高度
     * @param requestCode 剪裁图片的请求码
     */
    public static void cropImageUri(Activity activity, Uri orgUri, Uri desUri, int aspectX, int aspectY, int width, int height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(orgUri, "image/*");
        //发送裁剪信号
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
        //1-false用uri返回图片
        //2-true直接用bitmap返回图片（此种只适用于小图片，返回图片过大会报错）
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 读取uri所在的图片
     *
     * @param uri      图片对应的Uri
     * @param mContext 上下文对象
     * @return 获取图像的Bitmap
     */
    public static Bitmap getBitmapFromUri(Uri uri, Context mContext) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getFilePathFromURI(Context context, Uri contentUri) throws IOException {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = getFile();
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            FileUtils.copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param context 上下文对象
     * @param uri     当前相册照片的Uri
     * @return 解析后的Uri对应的String
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String pathHead = "file:///";
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return pathHead + getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return pathHead + getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 返回安全的Uri
     *
     * @param act
     * @param fileUri
     * @return
     */
    public static Uri getImageUri(FragmentActivity act, File fileUri) {
        Uri imageUri;
      /*  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            imageUri = Uri.fromFile(fileUri);
        } else {*/
        imageUri = FileProvider.getUriForFile(act, authority, fileUri);
//        }
        return imageUri;
    }

    private static UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);
        options.setCircleDimmedLayer(true);
        return uCrop.withOptions(options);
    }

    /**
     * 开始截图:Activity
     *
     * @param uri
     * @param cropImageUri 裁剪后uri
     */
    public static void startCrop(FragmentActivity act, @NonNull Uri uri, Uri cropImageUri) {
        try {
            UCrop uCrop = UCrop.of(uri, cropImageUri);
            uCrop = advancedConfig(uCrop);
//            uCrop.withMaxResultSize(MyUtils.getScreenWidth(act), MyUtils.getScreenHeight(act));
            uCrop.start(act);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开相册后的截图
     *
     * @param uri intent.getData
     * @return 返回截取后的uri
     */
    public static Uri startCropImageAct(FragmentActivity context, Uri uri) throws IOException {
        String filePathFromURI = getFilePathFromURI(context, uri);
        if (!TextUtils.isEmpty(filePathFromURI)) {
            File imagePath = new File(filePathFromURI);
            Uri newUri = Uri.fromFile(imagePath);
            Uri cropImageUri = Uri.fromFile(PhotoUtil.getFile());
            startCrop(context, newUri, cropImageUri);
            return cropImageUri;
        }

       /* Uri cropImageUri = Uri.fromFile(PhotoUtil.getFile(true));
        Uri newUri = Uri.parse(PhotoUtil.getPath(context, uri));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newUri = FileProvider.getUriForFile(context, PhotoUtil.authority, new File(newUri.getPath()));
        }*/
        return null;
    }

    public static Uri startCropImageAct(Fragment context, Uri uri) throws IOException {
        String filePathFromURI = getFilePathFromURI(context.getActivity(), uri);
        if (!TextUtils.isEmpty(filePathFromURI)) {
            File imagePath = new File(filePathFromURI);
            Uri newUri = Uri.fromFile(imagePath);
            Uri cropImageUri = Uri.fromFile(PhotoUtil.getFile());
            startCrop(context, newUri, cropImageUri);
            return cropImageUri;
        }

      /*        Uri cropImageUri = Uri.fromFile(PhotoUtil.getFile());
        Uri newUri = Uri.parse(PhotoUtil.getPath(context.getActivity(), uri));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newUri = FileProvider.getUriForFile(context.getActivity(), PhotoUtil.authority, new File(newUri.getPath()));
        }
        startCrop(context, newUri, cropImageUri);
        return cropImageUri;*/
        return null;

    }

    /**
     * 开始截图:Fragment
     *
     * @param uri
     * @param cropImageUri 裁剪后uri
     */
    public static void startCrop(Fragment fragment, @NonNull Uri uri, Uri cropImageUri) {
        try {
            Activity act = fragment.getActivity();
            UCrop uCrop = UCrop.of(uri, cropImageUri);
            uCrop = advancedConfig(uCrop);
//            uCrop.withMaxResultSize(MyUtils.getScreenWidth(act), MyUtils.getScreenHeight(act));
            uCrop.start(act, fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安卓7.0裁剪根据文件路径获取uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 发起剪裁图片的请求
     *
     * @param activity    上下文
     * @param srcFile     原文件的File
     * @param output      输出文件的File
     * @param requestCode 请求码
     */
    public static void startPhotoZoom(Activity activity, File srcFile, File output, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        //主要修改这行代码,不再使用Uri.fromFile()方法获取文件的Uri
        intent.setDataAndType(getImageContentUri(activity, srcFile), "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 480);
        intent.putExtra("return-data", false);// true:不返回uri，false：返回uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, requestCode);
    }



    /*获取文件*/
    public static File getFile() throws IOException {
        File file = getFile("images" + System.currentTimeMillis() + ".jpg");
        return file;
    }

    /*获取文件*/
    public static File getFile(String childPath) throws IOException {
        String parentPath = MyApplication.getInstance().getFilesDir().getPath();
        File file = new File(parentPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File picFile = new File(parentPath, childPath);
        picFile.createNewFile();
        return new File(parentPath, childPath);
    }

}
