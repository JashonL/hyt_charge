package com.growatt.chargingpile;

import static com.growatt.chargingpile.jpush.TagAliasOperatorHelper.sequence;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.growatt.chargingpile.EventBusMsg.AddDevMsg;
import com.growatt.chargingpile.EventBusMsg.SearchDevMsg;
import com.growatt.chargingpile.activity.AboutActivity;
import com.growatt.chargingpile.activity.AddChargingActivity;
import com.growatt.chargingpile.activity.GunActivity;
import com.growatt.chargingpile.activity.UserActivity;
import com.growatt.chargingpile.adapter.ChargingListAdapter;
import com.growatt.chargingpile.adapter.MeAdapter;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.jpush.TagAliasOperatorHelper;
import com.growatt.chargingpile.util.Constant;
import com.growatt.chargingpile.util.GlideUtils;
import com.growatt.chargingpile.util.ImagePathUtil;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.PermissionCodeUtil;
import com.growatt.chargingpile.util.PhotoUtil;
import com.growatt.chargingpile.util.SearchUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity {

    private static String TAG = MainActivity.class.getSimpleName();

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
    @BindView(R.id.et_search)
    EditText editSearch;

    private List<Map<String, Object>> list;
    private MeAdapter mMeAdapter;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    //拍照相关变量
    private Uri imageUri;

    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private ImageView ivHead;

    /*充电桩列表*/
    @BindView(R.id.rv_charging)
    RecyclerView mRvCharging;
    private List<ChargingBean.DataBean> mChargingList = new ArrayList<>();
    private List<ChargingBean.DataBean> mTempChargingList = new ArrayList<>();
    private ChargingListAdapter mChargingAdapter;

    private String searchId;
    private String jumpId;

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
        initClick();
        initPermission();
        initCharging();
        initRecyclerListeners();
        Set<String> tags = new HashSet<String>();
        tags.add(SmartHomeUtil.getUserName());
        setJpushAliasTag(tags, SmartHomeUtil.getUserName());
        initPullView();
        freshData();
        handleSearch();
    }

    private long TOUCH_TIME = 0;

    @Override
    public void onBackPressed() {
        long time = System.currentTimeMillis();
        if (time - TOUCH_TIME > com.growatt.chargingpile.constant.Constant.WAIT_TIME) {
            toast(getString(R.string.exit_program));
            TOUCH_TIME = time;
        } else {
            MyApplication.getInstance().exit();
        }
    }

    /**
     * 处理搜索
     */
    private void handleSearch() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // TODO: 2021/8/23 数据量大考虑用线程
                List<ChargingBean.DataBean> dataBeanList = SearchUtils.search(charSequence, mTempChargingList);
                Log.d(TAG, "onTextChanged: " + dataBeanList.size());
                if (dataBeanList.size() > 0) {
                    mChargingAdapter.replaceData(dataBeanList);
                } else {
                    mChargingAdapter.replaceData(mChargingList);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void initPullView() {
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.maincolor_2));
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            freshData();
        });
    }

    /**
     * 刷新列表数据
     * position :刷新列表时选中第几项
     * millis
     */
    private void freshData() {
        mSwipeRefreshLayout.setRefreshing(true);
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
                            mTempChargingList = chargingListBean.getData();
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

                        if (mSwipeRefreshLayout != null) {
                            mChargingAdapter.replaceData(charginglist);
//                            if (isFirst) {
//                                //mChargingAdapter.setNowSelectPosition(currentPos);
//                                isFirst = false;
//                            }
                        }

                        //MyUtil.hideAllView(View.GONE, emptyPage);
                        //MyUtil.showAllView(rlCharging, linearlayout);
                        //refreshChargingUI();
                    } else {
                        if (mSwipeRefreshLayout != null) {
                            mChargingAdapter.replaceData(charginglist);
                        }
                        //MyUtil.hideAllView(View.GONE, rlCharging, linearlayout);
                        //MyUtil.showAllView(emptyPage);
                    }

                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
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
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    /**
     * 初始化列表
     */
    private void initCharging() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChargingAdapter = new ChargingListAdapter(mChargingList, this);
        mRvCharging.setLayoutManager(mLinearLayoutManager);
        mRvCharging.setAdapter(mChargingAdapter);
    }

    private void initRecyclerListeners() {
        mChargingAdapter.setOnItemClickListener((adapter, view, position) -> {
            Log.d(TAG, "initRecyclerListeners: " + position);
            ChargingBean.DataBean bean = mChargingAdapter.getItem(position);
            Intent intent = new Intent(MainActivity.this, GunActivity.class);
            intent.putExtra("chargingBean", bean);
            jumpTo(intent, false);
        });

        mChargingAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            ChargingBean.DataBean bean = mChargingAdapter.getItem(position);
            if (bean == null) return false;
            int type = bean.getDevType();
            if (type != ChargingBean.ADD_DEVICE) {
                requestDelete(bean);
            }
            return true;
        });
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

    private void initClick() {
        mMeAdapter.setOnItemClickListener((adapter, view, position) -> {
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

        headerView.findViewById(R.id.ivRight).setOnClickListener(view -> addChargingPile());
    }

    private void initRecycleView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMeAdapter = new MeAdapter(list);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mMeAdapter);
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
