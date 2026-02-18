package com.stellerainn.backend.entity;

import java.util.Date;

public class SQLExample {
    private int id;
    private String user_name;
    private boolean is_active;
    private Date created_date;

    public SQLExample(int id, String user_name, boolean is_active, Date created_date) {
        this.id = id;
        this.user_name = user_name;
        this.is_active = is_active;
        this.created_date = created_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }
}
