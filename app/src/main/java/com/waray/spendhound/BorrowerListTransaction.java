package com.waray.spendhound;

public class BorrowerListTransaction {
    private String date, borrower, borrowedAmountStr, status;

    public BorrowerListTransaction() {
        // Default constructor required by Firebase Realtime Database
    }

    public BorrowerListTransaction(String date, String borrower, String borrowedAmountStr, String status) {
        this.date = String.valueOf(date);
        this.borrower = borrower;
        this.borrowedAmountStr = borrowedAmountStr;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
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
