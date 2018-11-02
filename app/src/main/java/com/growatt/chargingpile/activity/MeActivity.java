package com.growatt.chargingpile.activity;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.Myadapter;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.LoadLocalImageUtil;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.PermissionCodeUtil;
import com.growatt.chargingpile.view.CircleImageView;
import com.mylhyl.circledialog.CircleDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class MeActivity extends BaseActivity {

    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<Map<String, Object>> list;
    private LinearLayoutManager mLinearLayoutManager;
    private int[] titles;
    private int[] images;
    private Myadapter adapter;
    private View mRvHeaderView;


    //拍照相关变量
    private File fileUri;
    private File fileCropUri;
    private Uri imageUri;
    private Uri cropImageUri;

    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private ImageView ivHead;
    private String picName;
    private View footerView;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        ButterKnife.bind(this);
        initHeaderView();
        initResource();
        initRecycleView();
        initListners();
    }

    private void initListners() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position){
                    case 0:
                        jumpTo(UserActivity.class,false);
                        break;
                    case 1:
                        toast(getString(R.string.m56暂未开放));
                        break;
                    case 2:
                        jumpTo(AboutActivity.class,false);
                        break;
                }
            }

        });
    }

    private void initResource() {
        titles = new int[]{R.string.m51账号管理, R.string.m52消息中心, R.string.m53关于};
        images = new int[]{R.drawable.manager_center, R.drawable.message_center, R.drawable.about};
        list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", getResources().getString(titles[i]));
            map.put("image", images[i]);
            list.add(map);
        }
        mRvHeaderView = LayoutInflater.from(this).inflate(R.layout.header_me_recyclerview_header, null);
        TextView name = (TextView) mRvHeaderView.findViewById(R.id.textView_name);
        name.setText(Cons.userBean.getAccountName());
        TextPaint tp = name.getPaint();
        tp.setFakeBoldText(true);
        ivHead = (ImageView) mRvHeaderView.findViewById(R.id.imageView2);
        ivHead.setImageResource(R.drawable.default_head);
        ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                new CircleDialog.Builder()
                        .setTitle(getString(R.string.m请选择))
                        .setItems(new String[]{getString(R.string.m5拍照), getString(R.string.m6从相册选取)}, new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                switch (position) {
                                    case 0:
                                        //请求拍照权限
                                        if (EasyPermissions.hasPermissions(MeActivity.this, PermissionCodeUtil.PERMISSION_CAMERA)) {
                                            choseHeadImageFromCameraCapture();
                                        } else {
                                            EasyPermissions.requestPermissions(MeActivity.this, String.format(getString(R.string.m权限获取某权限说明), getString(R.string.m相机)), PermissionCodeUtil.PERMISSION_CAMERA_CODE, PermissionCodeUtil.PERMISSION_CAMERA);
                                        }
                                        break;
                                    case 1:
                                        if (EasyPermissions.hasPermissions(MeActivity.this, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE)) {
                                            choseHeadImageFromGallery();
                                        } else {
                                            EasyPermissions.requestPermissions(MeActivity.this, String.format(getString(R.string.m权限获取某权限说明), getString(R.string.m存储)), PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE);
                                        }
                                        break;
                                }
                            }
                        })
                        .setNegative(getString(R.string.m7取消), null)
                        .show(fragmentManager);
            }
        });
    }


    /**
     * 拍照
     */
    private void choseHeadImageFromCameraCapture() {
         picName=System.currentTimeMillis()+".png";
        fileUri = new File(Environment.getExternalStorageDirectory().getPath(), picName);
        fileCropUri = new File(Environment.getExternalStorageDirectory().getPath(), "crop_photo.jpg");
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (MyUtil.hasSdcard()) {
            imageUri = Uri.fromFile(fileUri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0 以上版本
                imageUri = FileProvider.getUriForFile(MeActivity.this, "com.growatt.chargingpile", fileUri);
                intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                // 授予目录临时共享权限
                intentFromCapture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } else {
                intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
    }

    /**
     * 相册
     */
    private void choseHeadImageFromGallery() {
        picName=System.currentTimeMillis()+".png";
        fileUri = new File(Environment.getExternalStorageDirectory().getPath(),picName);
        fileCropUri = new File(Environment.getExternalStorageDirectory().getPath(), "crop_photo.jpg");
        Intent intentFromGallery = new Intent("android.intent.action.PICK");
        // �����ļ�����
        intentFromGallery.setType("image/*");
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }



    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPileActivity();
            }
        });
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvTitle.setText(getString(R.string.m50我));
    }


    private void initRecycleView() {
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new Myadapter(list);
        adapter.addHeaderView(mRvHeaderView);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);
        footerView = getLayoutInflater().inflate(R.layout.footer_me_activity, recyclerView, false);
        btnLogout = (Button) footerView.findViewById(R.id.logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = MeActivity.this.getSupportFragmentManager();
                new CircleDialog.Builder().setTitle(getString(R.string.m27温馨提示))
                        .setText(getString(R.string.m19是否退出当前账户))
                        .setPositive(getString(R.string.m9确定), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LoginUtil.logout(MeActivity.this);
                            }
                        })
                        .setNegative(getString(R.string.m7取消),null)
                        .show(fragmentManager);
            }
        });

        adapter.addFooterView(footerView);
    }


    /**
     * 权限请求结果处理
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case PermissionCodeUtil.PERMISSION_CAMERA_CODE:
                if (EasyPermissions.hasPermissions(MeActivity.this, PermissionCodeUtil.PERMISSION_CAMERA)) {
                    choseHeadImageFromCameraCapture();
                }
                break;
            case PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE:
                if (EasyPermissions.hasPermissions(MeActivity.this, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE)) {
                    choseHeadImageFromGallery();
                }
                break;
        }
    }

    /**
     * 拍照、去照片结果返回处理
     * @param requestCode
     * @param resultCode
     * @param intent
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == MeActivity.this.RESULT_CANCELED) {
            Toast.makeText(MeActivity.this, getString(R.string.m7取消), Toast.LENGTH_LONG).show();
            return;
        }

        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                if (MyUtil.hasSdcard()) {
                    cropImageUri = Uri.fromFile(fileCropUri);
                    Uri newUri = Uri.parse(getPath(MeActivity.this, intent.getData()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        newUri = FileProvider.getUriForFile(MeActivity.this, "com.growatt.chargingpile", new File(newUri.getPath()));
                    }
                    cropRawPhoto(newUri, cropImageUri);
                } else {
                    Toast.makeText(MeActivity.this, MeActivity.this.getString(R.string.m没有SD卡), Toast.LENGTH_LONG)
                            .show();
                }
                break;

            case CODE_CAMERA_REQUEST://相机返回
                cropImageUri = Uri.fromFile(fileCropUri);
                cropRawPhoto(imageUri, cropImageUri);
                break;

            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    setImageToHeadView(cropImageUri);
                }
                break;
        }
    }

    private void setImageToHeadView(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            saveBitmap(bitmap);
            bitmap = compressBitmap(ivHead);
            ivHead.setImageBitmap(CircleImageView.toRoundBitmap(bitmap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveBitmap(Bitmap avatar) {
        File f = new File(
                Environment.getExternalStorageDirectory(),
                picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            avatar.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Bitmap compressBitmap(ImageView iv) {
        Bitmap addbmp = null;
        try {
            InputStream in = new FileInputStream(Environment.getExternalStorageDirectory() + "/" + picName);
            addbmp = LoadLocalImageUtil.compress(in, this, iv);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return addbmp;
    }



    private static int output_X = 80;
    private static int output_Y = 80;
    public void cropRawPhoto(Uri orgUri, Uri desUri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(orgUri, "image/*");
        //发送裁剪信号
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
        //1-false用uri返回图片
        //2-true直接用bitmap返回图片（此种只适用于小图片，返回图片过大会报错）
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }


    /**
     * @param context 上下文对象
     * @param uri     当前相册照片的Uri
     * @return 解析后的Uri对应的String
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String pathHead = "file:///";
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (MyUtil.isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (MyUtil.isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return pathHead + MyUtil.getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (MyUtil.isMediaDocument(uri)) {
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

                return pathHead + MyUtil.getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + MyUtil.getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + uri.getPath();
        }
        return null;
    }


    private void backPileActivity(){
        Intent intent = new Intent();
        intent.putExtra("activity",MeActivity.this.getClass().getName());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        backPileActivity();
        return super.onKeyDown(keyCode, event);
    }
}
