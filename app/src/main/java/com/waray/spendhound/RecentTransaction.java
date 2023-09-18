package com.waray.spendhound;

public class RecentTransaction {
    private String mostRecentDate;
    private String mostRecentTransactionType;
    private String mostRecentPaymentAmountStr;
    private int iconResource;

    public RecentTransaction(String mostRecentDate, String mostRecentTransactionType, String mostRecentPaymentAmountStr, int iconResource) {
        this.mostRecentDate = mostRecentDate;
        this.mostRecentTransactionType = mostRecentTransactionType;
        this.mostRecentPaymentAmountStr = mostRecentPaymentAmountStr;
        this.iconResource = iconResource;
    }

    public String getMostRecentDate() {
        return mostRecentDate;
    }

    public void setMostRecentDate(String mostRecentDate) {
        this.mostRecentDate = mostRecentDate;
    }

    public String getMostRecentTransactionType() {
        return mostRecentTransactionType;
    }

    public void setMostRecentTransactionType(String mostRecentTransactionType) {
        this.mostRecentTransactionType = mostRecentTransactionType;
    }

    public String getMostRecentPaymentAmountStr() {
        return mostRecentPaymentAmountStr;
    }

    public void setMostRecentPaymentAmountStr(String mostRecentPaymentAmountStr) {
        this.mostRecentPaymentAmountStr = mostRecentPaymentAmountStr;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }
}
