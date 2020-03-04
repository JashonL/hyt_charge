package com.growatt.chargingpile.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by Administrator on 2019/7/24.
 */

public class SolarBean extends ParamsBeanLeveItem1 implements MultiItemEntity {
    private int mode;
    private int current;
    private int type;
    private String key;
    private String value;
    private String sfield;
    private boolean isAuthority;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSfield() {
        return sfield;
    }

    public void setSfield(String sfield) {
        this.sfield = sfield;
    }

    public boolean isAuthority() {
        return isAuthority;
    }

    public void setAuthority(boolean authority) {
        isAuthority = authority;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
