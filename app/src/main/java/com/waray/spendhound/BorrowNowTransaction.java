package com.waray.spendhound;

public class BorrowNowTransaction {
    private String borrowerID, lenderID, date, lender, borrowedAmountStr, status;

    public BorrowNowTransaction() {
        // Default constructor required by Firebase Realtime Database
    }

    public BorrowNowTransaction(String borrowerID,String lenderID,String date, String borrowee, String borrowedAmountStr, String status) {
        this.borrowerID = borrowerID;
        this.lenderID = lenderID;
        this.date = String.valueOf(date);
        this.lender = borrowee;
        this.borrowedAmountStr = borrowedAmountStr;
        this.status = status;
    }

    public String getBorrowerID() {
        return borrowerID;
    }

    public void setBorrowerID(String borrowerID) {
        this.borrowerID = borrowerID;
    }

    public String getLenderID() {
        return lenderID;
    }

    public void setLenderID(String lenderID) {
        this.lenderID = lenderID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLender() {
        return lender;
    }

    public void setLender(String lender) {
        this.lender = lender;
    }

    public String getBorrowedAmountStr() {
        return borrowedAmountStr;
    }

    public void setBorrowedAmountStr(String borrowedAmountStr) {
        this.borrowedAmountStr = borrowedAmountStr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
