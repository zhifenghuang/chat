package com.alsc.chat.bean;

import android.content.ContentValues;

import com.alsc.chat.db.IDBItemOperation;
import com.alsc.chat.manager.DataManager;


public class GroupBean extends IDBItemOperation {
    private long id;
    private long groupId;
    private int groupRole;
    private String name;
    private String icon;
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public int getGroupRole() {
        return groupRole;
    }

    public void setGroupRole(int groupRole) {
        this.groupRole = groupRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String getPrimaryKeyName() {
        return "id";
    }

    @Override
    public String getTableName() {
        return "chat_group";
    }

    @Override
    public ContentValues getValues() {
        UserBean myInfo = DataManager.getInstance().getUser();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("groupId", groupId);
        values.put("groupRole", groupRole);
        values.put("name", name);
        values.put("icon", icon);
        values.put("status", status);
        values.put("isdel", 0);
        values.put("owerId", myInfo.getUserId());
        return values;
    }
}
