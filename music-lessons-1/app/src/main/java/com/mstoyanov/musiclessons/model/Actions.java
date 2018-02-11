package com.mstoyanov.musiclessons.model;

public class Actions {
    private String label;
    private String data;
    private int type;
    public static final int ACTION_CALL = 1;
    public static final int ACTION_SMS = 2;
    public static final int ACTION_EMAIL = 3;

    public Actions(String label, String data, int type) {
        this.label = label;
        this.data = data;
        this.type = type;
    }

    String getLabel() {
        return label;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }
}