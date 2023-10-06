package com.waray.spendhound;

public class BorrowTransaction {
    private String date, borrower, borrowee;
    private int borrowedAmount;

    public BorrowTransaction(String date, String borrower, String borrowee, int borrowedAmount) {
        this.date = String.valueOf(date);
        this.borrower = borrower;
        this.borrowee = borrowee;
        this.borrowedAmount = borrowedAmount;
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

    public String getBorrowee() {
        return borrowee;
    }

    public void setBorrowee(String borrowee) {
        this.borrowee = borrowee;
    }

    public int getBorrowedAmount() {
        return borrowedAmount;
    }

    public void setBorrowedAmount(int borrowedAmount) {
        this.borrowedAmount = borrowedAmount;
    }
}
