package com.example.mobileapp.ui.budget.Custom;

public class AllowanceItem {
    private int avatarResId;
    private String position;
    private String money;
    private String titlemoney;

    public AllowanceItem(int avatarResId, String position, String money, String titlemoney) {
        this.avatarResId = avatarResId;
        this.position = position;
        this.money = money;
        this.titlemoney = titlemoney;
    }


    public int getAvatarResId() {
        return avatarResId;
    }

    public String getPosition() {
        return position;
    }

    public String getMoney() {
        return money;
    }

    public String getMoneyTitle() {
        return titlemoney;
    }

}

