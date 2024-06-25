package com.waray.spendhound;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DebtTransactionAdapter extends RecyclerView.Adapter<DebtTransactionAdapter.ViewHolder> {
    private final ArrayList<BorrowTransaction> borrowTransactionList;
    private ArrayList<Integer> checkedPositions;

    public DebtTransactionAdapter(ArrayList<BorrowTransaction> borrowTransactionList) {
        this.borrowTransactionList = borrowTransactionList;
        checkedPositions = new ArrayList<>();
    }
    // Add this method to retrieve a BorrowTransaction by its position
    public BorrowTransaction getBorrowTransaction(int position) {
        return borrowTransactionList.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.debt_row_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BorrowTransaction transaction = borrowTransactionList.get(position);

        // Bind data to the ViewHolder's views
        holder.debtDateTV.setText(transaction.getDate());
        holder.debtBorroweeTV.setText(transaction.getBorrowee());
        holder.debtAmountBorrowedTV.setText(transaction.getBorrowedAmountStr());
        holder.debtStatusTV.setText(transaction.getStatus());
    }


    @Override
    public int getItemCount() {
        return borrowTransactionList.size();
    }

    public ArrayList<Integer> getCheckedPositions() {
        return checkedPositions;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView debtDateTV;
        public TextView debtBorroweeTV;
        public TextView debtAmountBorrowedTV;
        public TextView debtStatusTV;

        public ViewHolder(View itemView) {
            super(itemView);
            debtDateTV = itemView.findViewById(R.id.debtDateTV);
            debtBorroweeTV = itemView.findViewById(R.id.debtBorroweeTV);
            debtAmountBorrowedTV = itemView.findViewById(R.id.debtAmountBorrowedTV);
            debtStatusTV = itemView.findViewById(R.id.debtStatusTV);
        }
    }
}
