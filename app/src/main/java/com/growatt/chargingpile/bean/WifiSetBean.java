package com.growatt.chargingpile.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by Administrator on 2018/10/23.
 */

public class WifiSetBean  extends AbstractExpandableItem<SolarBean> implements MultiItemEntity {
    private String key;
    private Object value;
    private String title;
    private int type;
    private int index;

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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj)return true;
        if (!(obj instanceof WifiSetBean)){
            throw new ClassCastException("类型错误");
        }
        WifiSetBean p = (WifiSetBean) obj;
        return this.index==p.getIndex();
    }

    @Override
    public int getItemType() {
        return type;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
