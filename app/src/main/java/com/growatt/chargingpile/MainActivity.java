package com.growatt.chargingpile;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.growatt.chargingpile.EventBusMsg.SearchDevMsg;
import com.growatt.chargingpile.activity.AboutActivity;
import com.growatt.chargingpile.activity.AddChargingActivity;
import com.growatt.chargingpile.activity.UserActivity;
import com.growatt.chargingpile.adapter.ChargingListAdapter;
import com.growatt.chargingpile.adapter.Myadapter;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.bean.GunBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.jpush.TagAliasOperatorHelper;
import com.growatt.chargingpile.util.Constant;
import com.growatt.chargingpile.util.GlideUtils;
import com.growatt.chargingpile.util.ImagePathUtil;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.PermissionCodeUtil;
import com.growatt.chargingpile.util.PhotoUtil;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.mylhyl.circledialog.view.listener.OnLvItemClickListener;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

import static com.growatt.chargingpile.jpush.TagAliasOperatorHelper.sequence;

public class MainActivity extends BaseActivity {

    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.ivRight)
    ImageView ivRight;

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

    private String searchId;
    private String jumpId;
    private boolean isFirst = true;

    private Animation animation;

    private Timer mTimer;//涂鸦设备操作定时器
    private TimerTask timerTask;//涂鸦设备定时任务
    private long period = 1000;//刷新任务的间隔时间
    private String moneyUnit = "";

    //充电桩当前状态
    private String currenStatus = GunBean.NONE;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void searchFresh(SearchDevMsg msg) {
        searchId = msg.getDevSn();
        freshData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addDev(AddDevMsg msg) {
        freshData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initHeaderView();
        initResource();
        initRecycleView();
        initListners();
        initPermission();
        initCharging();
        initListeners();
        Set<String> tags = new HashSet<String>();
        tags.add(SmartHomeUtil.getUserName());
        setJpushAliasTag(tags, SmartHomeUtil.getUserName());
        freshData();
    }

    /**
     * 刷新列表数据
     * position :刷新列表时选中第几项
     * millis
     */
    private void freshData() {
        Mydialog.Show(MainActivity.this);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("userId", SmartHomeUtil.getUserName());//测试id
        if (!TextUtils.isEmpty(searchId)) jsonMap.put("chargeId", searchId);
        jsonMap.put("lan", getLanguage());
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        //LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postGetChargingList(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                //srlPull.setRefreshing(false);
                try {
                    Log.d("postJson", "success: " + json);
                    List<ChargingBean.DataBean> charginglist = new ArrayList<>();
                    JSONObject object = new JSONObject(json);
                    int currentPos = 0;
                    if (object.getInt("code") == 0) {
                        ChargingBean chargingListBean = new Gson().fromJson(json, ChargingBean.class);
                        if (chargingListBean != null) {
                            jumpId = getIntent().getStringExtra("chargeId");
                            charginglist = chargingListBean.getData();
                            if (charginglist == null) charginglist = new ArrayList<>();
                            for (int i = 0; i < charginglist.size(); i++) {
                                ChargingBean.DataBean bean = charginglist.get(i);
                                bean.setDevType(ChargingBean.CHARGING_PILE);
                                bean.setName(bean.getName());
                                String chargeId = bean.getChargeId();
                                if (chargeId.equals(jumpId)) currentPos = i;
                            }

                        }

                    }
                    //默认选中第一项
                    if (charginglist.size() > 0) {
                        //HeadRvAddButton(charginglist);
                        mAdapter.replaceData(charginglist);
                        if (isFirst) {
                            mAdapter.setNowSelectPosition(currentPos);
                            isFirst = false;
                        }
                        //MyUtil.hideAllView(View.GONE, emptyPage);
                        //MyUtil.showAllView(rlCharging, linearlayout);
                        //refreshChargingUI();
                    } else {
                        mAdapter.replaceData(charginglist);
                        //MyUtil.hideAllView(View.GONE, rlCharging, linearlayout);
                        //MyUtil.showAllView(emptyPage);
                    }

                } catch (Exception e) {
                    //srlPull.setRefreshing(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {
                //Mydialog.Dismiss();
                //srlPull.setRefreshing(false);
                //MyUtil.hideAllView(View.GONE, rlCharging);
                //MyUtil.showAllView(emptyPage);
            }
        });

    }

    /**
     * 初始化列表
     */
    private void initCharging() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ChargingListAdapter(mChargingList);
        mRvCharging.setLayoutManager(mLinearLayoutManager);
        mRvCharging.setAdapter(mAdapter);
    }

    /**
     * 停止定时器
     */
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            timerTask.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }


    private void initListeners() {
//        mAdapter.setOnItemClickListener((adapter, view, position) -> {
//            ChargingBean.DataBean bean = mAdapter.getItem(position);
//            if (bean == null) return;
//            int type = bean.getDevType();
//            if (type == ChargingBean.ADD_DEVICE) {
//                addChargingPile();
//            } else {
//                animation = null;
//                stopTimer();
//                currenStatus = GunBean.NONE;
//                mAdapter.setNowSelectPosition(position);
//                //refreshChargingUI();
//            }
//        });
//
//        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
//            ChargingBean.DataBean bean = mAdapter.getItem(position);
//            if (bean == null) return false;
//            int type = bean.getDevType();
//            if (type != ChargingBean.ADD_DEVICE) {
//                requestDelete(bean);
//            }
//            return false;
//        });
    }

    private void requestDelete(final ChargingBean.DataBean bean) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        new CircleDialog.Builder()
                .setWidth(0.75f)
                .setTitle(getString(R.string.m8警告))
                .setText(getString(R.string.m确认删除))
                .setGravity(Gravity.CENTER).setPositive(getString(R.string.m9确定), v -> {
            //LogUtil.d("删除充电桩");
            Mydialog.Show(MainActivity.this);
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("sn", bean.getChargeId());
            jsonMap.put("userId", bean.getUserName());
            jsonMap.put("lan", getLanguage());
            String json = SmartHomeUtil.mapToJsonString(jsonMap);
            LogUtil.i(json);
            PostUtil.postJson(SmartHomeUrlUtil.postRequestDeleteCharging(), json, new PostUtil.postListener() {
                @Override
                public void Params(Map<String, String> params) {

                }

                @Override
                public void success(String json) {
                    Mydialog.Dismiss();
                    try {
                        JSONObject object = new JSONObject(json);
                        if (object.getInt("code") == 0) {
                            toast(getString(R.string.m135删除成功));
                            //删除之后,重新刷新
                            freshData();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void LoginError(String str) {
                }


            });
        })
                .setNegative(getString(R.string.m7取消), null)
                .show(fragmentManager);


    }


    private void initPermission() {
        if (!EasyPermissions.hasPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, String.format("%s:%s", getString(R.string.m权限获取某权限说明), getString(R.string.m存储)), 11001, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void initViews() {
        try {
            File file = new File(Constant.IMAGE_FILE_LOCATION);
            if (file.exists()) {
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
                                        EasyPermissions.requestPermissions(MainActivity.this, String.format("%s:%s", getString(R.string.m权限获取某权限说明), getString(R.string.m相机)), PermissionCodeUtil.PERMISSION_CAMERA_CODE, PermissionCodeUtil.PERMISSION_CAMERA);
                                    }
                                    break;
                                case 1:
                                    if (EasyPermissions.hasPermissions(MainActivity.this, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE)) {
                                        choseHeadImageFromGallery();
                                    } else {
                                        EasyPermissions.requestPermissions(MainActivity.this, String.format("%s:%s", getString(R.string.m权限获取某权限说明), getString(R.string.m存储)), PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE_CODE, PermissionCodeUtil.PERMISSION_EXTERNAL_STORAGE);
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

        headerView.findViewById(R.id.ivRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addChargingPile();
            }
        });


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
        File f = new File(Environment.getExternalStorageDirectory() + "/Project EV/");
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

    private void setJpushAliasTag(Set<String> tags, String alias) {
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.action = TagAliasOperatorHelper.ACTION_SET;
        tagAliasBean.alias = alias;
        tagAliasBean.tags = tags;
        tagAliasBean.isAliasAction = true;
        sequence++;
        TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(), sequence, tagAliasBean);
    /*    tagAliasBean.isAliasAction = false;
        sequence++;
        TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(), sequence, tagAliasBean);*/
    }

    private void addChargingPile() {
        if (SmartHomeUtil.isFlagUser()) {//浏览账户
            FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
            new CircleDialog.Builder()
                    .setTitle(getString(R.string.m27温馨提示))
                    //添加标题，参考普通对话框
                    .setInputHint(getString(R.string.m26请输入密码))//提示
                    .setInputCounter(1000, (maxLen, currentLen) -> "")
                    .configInput(params -> {
                        params.gravity = Gravity.CENTER;
//                        params.textSize = 45;
//                            params.backgroundColor=ContextCompat.getColor(ChargingPileActivity.this, R.color.preset_edit_time_background);
                        params.strokeColor = ContextCompat.getColor(MainActivity.this, R.color.preset_edit_time_background);
                    })
                    .setPositiveInput(getString(R.string.m9确定), new OnInputClickListener() {
                        @Override
                        public boolean onClick(String text, View v) {
                            Map<String, Object> params = new HashMap<>();
                            params.put("code", text);
                            params.put("userId", SmartHomeUtil.getUserName());
                            params.put("lan", getLanguage());
                            String json = SmartHomeUtil.mapToJsonString(params);
                            PostUtil.postJson(SmartHomeUrlUtil.postGetDemoCode(), json, new PostUtil.postListener() {
                                @Override
                                public void Params(Map<String, String> params) {

                                }

                                @Override
                                public void success(String json) {
                                    try {
                                        JSONObject object = new JSONObject(json);
                                        int code = object.getInt("code");
                                        if (code == 0) {
                                            Intent intent = new Intent(MainActivity.this, AddChargingActivity.class);
                                            jumpTo(intent, false);
                                        } else {
                                            String data = object.getString("data");
                                            toast(data);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void LoginError(String str) {

                                }
                            });
                            return true;
                        }
                    })
                    //添加取消按钮，参考普通对话框
                    .setNegative(getString(R.string.m7取消), v -> {

                    })
                    .show(fragmentManager);
        } else {
            Intent intent = new Intent(MainActivity.this, AddChargingActivity.class);
            jumpTo(intent, false);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchId = null;
    }
}
