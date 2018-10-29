package com.growatt.chargingpile.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by Administrator on 2018/10/23.
 */

public class ParamsSetBean implements MultiItemEntity {

    public static final int PARAM_TITILE = 0;
    public static final int PARAM_ITEM = 1;


    private String key;
    private Object value;
    private String title;
    private int type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
