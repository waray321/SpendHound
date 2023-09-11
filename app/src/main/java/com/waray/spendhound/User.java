package com.waray.spendhound;

public class User {

    private String username;
    private String email;
    private String password;
    private String profileImageUrl;


    public User(String email, String password, String profileImageUrl,  String username) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImageUrl= profileImageUrl;
    }

    public User() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

