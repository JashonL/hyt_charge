package com.growatt.chargingpile.EventBusMsg;

/**
 * Created by Administrator on 2018/10/15.
 */

public class UnitMsg {

    public UnitMsg(String symbol) {
        this.symbol = symbol;
    }

    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
