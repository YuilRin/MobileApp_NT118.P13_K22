package com.example.mobileapp.ui.budget.Custom;

import java.util.Objects;
import java.util.UUID;

public class AllowanceItem {
    private int avatarResId;
    private String content;
    private String money;
    private String moneyTitle;
    private String id;
    private String date;
    private String YearMonth;

    public AllowanceItem() {}

    public AllowanceItem(int avatarResId, String content, String money, String moneyTitle, String date, String YearMonth ) {
        this.avatarResId = avatarResId;
        this.content = content;
        this.money = money;
        this.moneyTitle = moneyTitle;
        this.id = UUID.randomUUID().toString();
        this.date = date;
        this.YearMonth = YearMonth;
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public int getAvatarResId() {
        return avatarResId;
    }
    public  void  setAvatarResId(int avatarResId) {
        this.avatarResId = avatarResId;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getMoney() {
        return money;
    }
    public void setMoney(String money) {
        this.money = money;
    }

    public String getmoneyTitle() {
        return moneyTitle;
    }
    public void setMoneyTitle(String moneyTitle) {
        this.moneyTitle = moneyTitle;
    }

    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}

    public void setYearMonth(String yearMonth) {
        this.YearMonth = yearMonth;
    }
    public String getYearMonth() {
        return YearMonth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AllowanceItem that = (AllowanceItem) o;

        if (this.avatarResId != that.avatarResId) return false;
        if (!Objects.equals(this.id, that.id)) return false;
        if (!Objects.equals(this.content, that.content)) return false;
        if (!Objects.equals(this.money, that.money)) return false;
        if (!Objects.equals(this.moneyTitle, that.moneyTitle)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, avatarResId, content, money, moneyTitle);
    }



}

