package com.growatt.chargingpile.activity;

import android.Manifest;
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
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.Myadapter;
import com.growatt.chargingpile.util.Constant;
import com.growatt.chargingpile.util.GlideUtils;
import com.growatt.chargingpile.util.ImagePathUtil;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.PermissionCodeUtil;
import com.growatt.chargingpile.util.PhotoUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Myadapter adapter;
    private View mRvHeaderView;


    //拍照相关变量
    private Uri imageUri;

    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private ImageView ivHead;

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

    private void initViews() {
        try {
            File file  = new File(Constant.IMAGE_FILE_LOCATION);
            if (file.exists()){
                GlideUtils.getInstance().showImageActNoCache(this, R.drawable.default_head, R.drawable.default_head, Constant.IMAGE_FILE_LOCATION, ivHead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListners() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0:
                        if (SmartHomeUtil.isFlagUser()) {
                            toast(getString(R.string.m66你的账号没有操作权限));
                            return;
                        }
                        jumpTo(UserActivity.class, false);
                        break;
                    case 1:
                        toast(getString(R.string.m56暂未开放));
                        break;
                    case 2:

                        jumpTo(AboutActivity.class, false);
                        break;
                }
            }

        });
    }

    private void initResource() {
        int[] titles = new int[]{R.string.m51账号管理, R.string.m52消息中心, R.string.m53关于};
        int[] images = new int[]{R.drawable.manager_center, R.drawable.message_center, R.drawable.about};
        list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", getResources().getString(titles[i]));
            map.put("image", images[i]);
            list.add(map);
        }
        mRvHeaderView = LayoutInflater.from(this).inflate(R.layout.header_me_recyclerview_header, null);
        TextView name = mRvHeaderView.findViewById(R.id.textView_name);
        if (SmartHomeUtil.isFlagUser()) {
            name.setText(getText(R.string.m浏览账户));
        } else {
            name.setText(SmartHomeUtil.getUserName());
        }
        TextPaint tp = name.getPaint();
        tp.setFakeBoldText(true);
        ivHead = mRvHeaderView.findViewById(R.id.imageView2);
        ivHead.setImageResource(R.drawable.default_head);
        ivHead.setOnClickListener(v -> {
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
                                        EasyPermissions.requestPermissions(MeActivity.this,String.format("%s:%s",getString(R.string.m权限获取某权限说明),getString(R.string.m相机)),PermissionCodeUtil.PERMISSION_CAMERA_CODE, PermissionCodeUtil.PERMISSION_CAMERA);
                                    }
                                    break;
                                case 1:
                                    if (EasyPermissions.hasPermissions(MeActivity.this, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE)) {
                                        choseHeadImageFromGallery();
                                    } else {
                                        EasyPermissions.requestPermissions(MeActivity.this,String.format("%s:%s",getString(R.string.m权限获取某权限说明),getString(R.string.m存储)),PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE);
                                    }
                                    break;
                            }
                        }
                    })
                    .setNegative(getString(R.string.m7取消), null)
                    .show(fragmentManager);
        });
    }


    /**
     * 拍照
     */
    private void choseHeadImageFromCameraCapture() {
        imageUri = PhotoUtil.getImageUri(this, PhotoUtil.getFile());
        PhotoUtil.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
    }

    /**
     * 相册
     */
    private void choseHeadImageFromGallery() {
        PhotoUtil.openPic(this, CODE_GALLERY_REQUEST);
    }


    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvTitle.setText(getString(R.string.m50我));
    }


    private void initRecycleView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new Myadapter(list);
        adapter.addHeaderView(mRvHeaderView);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);
        View footerView = getLayoutInflater().inflate(R.layout.footer_me_activity, recyclerView, false);
        Button btnLogout = footerView.findViewById(R.id.logout);
        btnLogout.setOnClickListener(v -> {
            FragmentManager fragmentManager = MeActivity.this.getSupportFragmentManager();
            new CircleDialog.Builder().setTitle(getString(R.string.m27温馨提示))
                    .setText(getString(R.string.m19是否退出当前账户))
                    .setPositive(getString(R.string.m9确定), v1 -> LoginUtil.logout(MeActivity.this))
                    .setNegative(getString(R.string.m7取消), null)
                    .show(fragmentManager);
        });
        initViews();
        adapter.addFooterView(footerView);
    }


    /**
     * 权限请求结果处理
     *
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
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                Uri cropImageUri;
                if (resultCode == RESULT_OK) {
                    if (intent != null) {
                        PhotoUtil.startCropImageAct(this, intent.getData());
                    }
                }
                break;
            case CODE_CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    cropImageUri = Uri.fromFile(PhotoUtil.getFile());
                    PhotoUtil.startCrop(this, imageUri, cropImageUri);
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    handleCropResult(intent);
                }
                break;
        }
    }


    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            setImageToHeadView(resultUri);
        } else {
            toast(R.string.m失败);
        }
    }


    private void setImageToHeadView(Uri uri) {
        try {
            String plantPath = ImagePathUtil.getRealPathFromUri(this, uri);
            GlideUtils.getInstance().showImageAct(this, R.drawable.default_head, R.drawable.default_head, plantPath, ivHead);
            saveBitmap(plantPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveBitmap(String path) throws IOException {
        Bitmap bitmap = ImagePathUtil.decodeBitmapFromResource(path, ivHead.getWidth(), ivHead.getHeight());
        File f = new File(Environment.getExternalStorageDirectory()+"/Project EV/");
        File file = new File(Constant.IMAGE_FILE_LOCATION);
        if (!f.exists()) {
            boolean mkdirs = f.mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
        }
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
