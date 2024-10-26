package com.example.piechart.ui.budget.Custom;

public class AllowanceItem {
    private int avatarResId;
    private String position;
    private String allowance;
    private String color;

    public AllowanceItem(int avatarResId, String position, String allowance) {
        this.avatarResId = avatarResId;
        this.position = position;
        this.allowance = allowance;

    }

    public AllowanceItem(int avatarResId, String position, String allowance, String color) {
        this.avatarResId = avatarResId;
        this.position = position;
        this.allowance = allowance;
        this.color = color;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public String getPosition() {
        return position;
    }

    public String getAllowance() {
        return allowance;
    }
}

