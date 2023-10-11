package com.waray.spendhound;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OwedTransactionAdapter extends RecyclerView.Adapter<OwedTransactionAdapter.ViewHolder> {
    private final ArrayList<OwedTransaction> owedTransactionList;

    public OwedTransactionAdapter(ArrayList<OwedTransaction> owedTransactionList) {
        this.owedTransactionList = owedTransactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.owed_row_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OwedTransaction transaction = owedTransactionList.get(position);

        // Bind data to the ViewHolder's views
        holder.owedDateTV.setText(transaction.getDate());
        holder.owedBorroweeTV.setText(transaction.getBorrower());
        holder.owedAmountBorrowedTV.setText(transaction.getBorrowedAmountStr());
        holder.owedStatusTV.setText(transaction.getStatus());
    }

    @Override
    public int getItemCount() {
        return owedTransactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView owedDateTV;
        public TextView owedBorroweeTV;
        public TextView owedAmountBorrowedTV;
        public TextView owedStatusTV;

        public ViewHolder(View itemView) {
            super(itemView);
            owedDateTV = itemView.findViewById(R.id.owedDateTV);
            owedBorroweeTV = itemView.findViewById(R.id.owedBorroweeTV);
            owedAmountBorrowedTV = itemView.findViewById(R.id.owedAmountBorrowedTV);
            owedStatusTV = itemView.findViewById(R.id.owedStatusTV);
        }
    }
}

