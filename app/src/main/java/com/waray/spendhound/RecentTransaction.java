package com.waray.spendhound;

public class RecentTransaction {
    private String mostRecentDate;
    private String mostRecentTransactionType;
    private int mostRecentPaymentAmount;
    private int iconResource;

    public RecentTransaction(String mostRecentDate, String mostRecentTransactionType, int mostRecentPaymentAmount, int iconResource) {
        this.mostRecentDate = mostRecentDate;
        this.mostRecentTransactionType = mostRecentTransactionType;
        this.mostRecentPaymentAmount = mostRecentPaymentAmount;
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

    public int getMostRecentPaymentAmount() {
        return mostRecentPaymentAmount;
    }

    public void setMostRecentPaymentAmount(int mostRecentPaymentAmount) {
        this.mostRecentPaymentAmount = mostRecentPaymentAmount;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }
}
