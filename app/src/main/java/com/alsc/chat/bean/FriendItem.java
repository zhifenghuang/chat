package com.alsc.chat.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class FriendItem implements MultiItemEntity {


    public static final int VIEW_TYPE_0 = 0;
    public static final int VIEW_TYPE_1 = 1;
    public static final int VIEW_TYPE_2 = 2;
    public static final int VIEW_TYPE_3 = 3;

    private int itemType = VIEW_TYPE_3;
    private int iconRes;
    private String name;
    private UserBean friend;

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public UserBean getFriend() {
        return friend;
    }

    public void setFriend(UserBean friend) {
        this.friend = friend;
    }
}


