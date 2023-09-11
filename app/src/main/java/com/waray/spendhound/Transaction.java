package com.waray.spendhound;

import java.lang.reflect.Array;
import java.util.List;

public class Transaction {
    private String transactionType;
    private int paymentAmount;
    private List<String> payorsList;
    private List<Integer> amountsPaidList;
    private String usernamePost;

    public Transaction(String transactionType, int paymentAmount, List<String> payorsList, List<Integer> amountsPaidList, String usernamePost) {
        this.transactionType = transactionType;
        this.paymentAmount = paymentAmount;
        this.payorsList = payorsList;
        this.amountsPaidList = amountsPaidList;
        this.usernamePost= usernamePost;
    }

    // Add an empty constructor
    public Transaction() {
        // Default constructor required for Firebase
    }

    public Transaction(int paymentAmount, List<String> payorsList, List<Integer> amountsPaidList, String usernamePost) {
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(int paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public List<String> getPayorsList() {
        return payorsList;
    }

    public void setPayorsList(List<String> payorsList) {
        this.payorsList = payorsList;
    }

    public List<Integer> getAmountsPaidList() {
        return amountsPaidList;
    }

    public void setAmountsPaidList(List<Integer> amountsPaidList) {
        this.amountsPaidList = amountsPaidList;
    }

    public String getUsernamePost() {
        return usernamePost;
    }

    public void setUsernamePost(String usernamePost) {
        this.usernamePost = usernamePost;
    }
}
