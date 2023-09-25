package com.waray.spendhound;

public class User {

    private String username;
    private String email;
    private String password;
    private String profileImageUrl;
    private int balanced;
    private int unpaid;
    private int owed;
    private int debt;

    public User(String username, String email, String profileImageUrl,  String password, int balanced, int unpaid, int owed, int debt) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImageUrl= profileImageUrl;
        this.balanced = balanced;
        this.unpaid = unpaid;
        this.owed = owed;
        this.debt= debt;
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

    public int getBalanced() {
        return balanced;
    }

    public void setBalanced(int balanced) {
        this.balanced = balanced;
    }

    public int getUnpaid() {
        return unpaid;
    }

    public void setUnpaid(int unpaid) {
        this.unpaid = unpaid;
    }

    public int getOwed() {
        return owed;
    }

    public void setOwed(int owed) {
        this.owed = owed;
    }

    public int getDebt() {
        return debt;
    }

    public void setDebt(int debt) {
        this.debt = debt;
    }
}

