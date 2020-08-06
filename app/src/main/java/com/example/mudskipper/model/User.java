package com.example.mudskipper.model;

public class User {

    private String uid;
    private String username;
    private String search;

    public User(String id, String username, String search) {
        this.uid = id;
        this.username = username;
        this.search = search;
    }
    public User(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String id) {
        this.uid = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
