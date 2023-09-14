package com.waray.spendhound;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentTransactionAdapter extends RecyclerView.Adapter<RecentTransactionAdapter.ViewHolder> {

    private List<Transaction> transactionList;

    public RecentTransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_transaction_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        /*holder.transactionTypeTextView.setText(transaction.getTransactionType());
        holder.dateTextView.setText(transaction.getDate());
        holder.paymentAmountTextView.setText(String.valueOf(transaction.getPaymentAmount()));
        holder.iconImageView.setImageResource(R.drawable.your_transaction_icon);*/
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconImageView;
        public TextView transactionTypeTextView;
        public TextView dateTextView;
        public TextView paymentAmountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            /*iconImageView = itemView.findViewById(R.id.iconImageView);
            transactionTypeTextView = itemView.findViewById(R.id.transactionTypeTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            paymentAmountTextView = itemView.findViewById(R.id.paymentAmountTextView);*/
        }
    }
}

