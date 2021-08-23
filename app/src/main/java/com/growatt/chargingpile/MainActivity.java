package com.growatt.chargingpile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.chargingpile.activity.AboutActivity;
import com.growatt.chargingpile.activity.MeActivity;
import com.growatt.chargingpile.activity.UserActivity;
import com.growatt.chargingpile.adapter.Myadapter;
import com.growatt.chargingpile.util.Constant;
import com.growatt.chargingpile.util.GlideUtils;
import com.growatt.chargingpile.util.ImagePathUtil;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.PermissionCodeUtil;
import com.growatt.chargingpile.util.PhotoUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;
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

public class MainActivity extends BaseActivity {

    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    private List<Map<String, Object>> list;
    private Myadapter adapter;

    //拍照相关变量
    private Uri imageUri;

    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private ImageView ivHead;

    /*充电桩列表*/
    @BindView(R.id.rv_charging)
    RecyclerView mRvCharging;
    private List<ChargingBean.DataBean> mChargingList = new ArrayList<>();
    private ChargingListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        findViewById(R.id.ic_close_drawer).setOnClickListener(v -> {
            drawerLayout.closeDrawers();
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
        TextView name = findViewById(R.id.tv_name);
        if (SmartHomeUtil.isFlagUser()) {
            name.setText(getText(R.string.m浏览账户));
        } else {
            name.setText(SmartHomeUtil.getUserName());
        }
        TextPaint tp = name.getPaint();
        tp.setFakeBoldText(true);
        ivHead = findViewById(R.id.iv_login);
        ivHead.setImageResource(R.drawable.default_head);
        ivHead.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            new CircleDialog.Builder()
                    .setTitle(getString(R.string.m请选择))
                    .setItems(new String[]{getString(R.string.m5拍照), getString(R.string.m6从相册选取)}, new OnLvItemClickListener() {
                        @Override
                        public boolean onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0:
                                    //请求拍照权限
                                    if (EasyPermissions.hasPermissions(MainActivity.this, PermissionCodeUtil.PERMISSION_CAMERA)) {
                                        try {
                                            choseHeadImageFromCameraCapture();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        EasyPermissions.requestPermissions(MainActivity.this,String.format("%s:%s",getString(R.string.m权限获取某权限说明),getString(R.string.m相机)),PermissionCodeUtil.PERMISSION_CAMERA_CODE, PermissionCodeUtil.PERMISSION_CAMERA);
                                    }
                                    break;
                                case 1:
                                    if (EasyPermissions.hasPermissions(MainActivity.this, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE)) {
                                        choseHeadImageFromGallery();
                                    } else {
                                        EasyPermissions.requestPermissions(MainActivity.this,String.format("%s:%s",getString(R.string.m权限获取某权限说明),getString(R.string.m存储)),PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE);
                                    }
                                    break;
                            }
                            return true;
                        }
                    })
                    .setNegative(getString(R.string.m7取消), null)
                    .show(fragmentManager);
        });
    }

    /**
     * 拍照
     */
    private void choseHeadImageFromCameraCapture() throws IOException {
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
        setHeaderImage(headerView, R.drawable.ic_menu, Position.LEFT, v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        setHeaderImage(headerView, R.drawable.ic_add_charg, Position.RIGHT, v -> {
        });

        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        tvTitle.setText(getString(R.string.m102充电桩));
    }


    private void initRecycleView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new Myadapter(list);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(adapter);
        Button btnLogout = findViewById(R.id.logout);
        btnLogout.setOnClickListener(v -> {
            FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
            new CircleDialog.Builder().setTitle(getString(R.string.m27温馨提示))
                    .setText(getString(R.string.m19是否退出当前账户))
                    .setPositive(getString(R.string.m9确定), v1 -> LoginUtil.logout(MainActivity.this))
                    .setNegative(getString(R.string.m7取消), null)
                    .show(fragmentManager);
        });
        initViews();
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
                if (EasyPermissions.hasPermissions(MainActivity.this, PermissionCodeUtil.PERMISSION_CAMERA)) {
                    try {
                        choseHeadImageFromCameraCapture();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE:
                if (EasyPermissions.hasPermissions(MainActivity.this, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE)) {
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
                        try {
                            PhotoUtil.startCropImageAct(this, intent.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case CODE_CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        cropImageUri = Uri.fromFile(PhotoUtil.getFile());
                        PhotoUtil.startCrop(this, imageUri, cropImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
}
