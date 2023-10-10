package com.waray.spendhound;

public class BorrowTransaction {
    private String date, borrowee, borrowedAmountStr, status;

    public BorrowTransaction() {
        // Default constructor required by Firebase Realtime Database
    }

    public BorrowTransaction(String date, String borrowee, String borrowedAmountStr, String status) {
        this.date = String.valueOf(date);
        this.borrowee = borrowee;
        this.borrowedAmountStr = borrowedAmountStr;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBorrowee() {
        return borrowee;
    }

    public void setBorrowee(String borrowee) {
        this.borrowee = borrowee;
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
