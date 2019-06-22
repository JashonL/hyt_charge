package com.growatt.chargingpile.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by Administrator on 2018/10/23.
 */

public class ParamsSetBean extends AbstractExpandableItem<ChargingBean.DataBean.PriceConfBean> implements MultiItemEntity {

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

    @Override
    public int getLevel() {
        return 0;
    }
}
