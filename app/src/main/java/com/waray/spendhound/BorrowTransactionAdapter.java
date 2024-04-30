package com.waray.spendhound;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BorrowTransactionAdapter extends RecyclerView.Adapter<BorrowTransactionAdapter.ViewHolder> {
    private final ArrayList<BorrowTransaction> borrowTransactionList;

    public BorrowTransactionAdapter(ArrayList<BorrowTransaction> borrowTransactionList) {
        this.borrowTransactionList = borrowTransactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.debt_row_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView debtDateTV;
        public TextView debtBorroweeTV;
        public TextView debtAmountBorrowedTV;
        public TextView debtStatusTV;
        public TextView payTextView;
        public CheckBox payCheckBox;
        public Button payNowBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            debtDateTV = itemView.findViewById(R.id.debtDateTV);
            debtBorroweeTV = itemView.findViewById(R.id.debtBorroweeTV);
            debtAmountBorrowedTV = itemView.findViewById(R.id.debtAmountBorrowedTV);
            debtStatusTV = itemView.findViewById(R.id.debtStatusTV);
            payTextView = itemView.findViewById(R.id.payTextView);
            payCheckBox = itemView.findViewById(R.id.payCheckBox);
            payNowBtn = itemView.findViewById(R.id.payNowBtn);

        }
    }
}

