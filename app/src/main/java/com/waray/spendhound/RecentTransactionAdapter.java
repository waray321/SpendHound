package com.waray.spendhound;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecentTransactionAdapter extends RecyclerView.Adapter<RecentTransactionAdapter.ViewHolder> {
    private ArrayList<RecentTransaction> recentTransactionList;

    public RecentTransactionAdapter(ArrayList<RecentTransaction> recentTransactionList) {
        this.recentTransactionList = recentTransactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_transaction, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentTransaction transaction = recentTransactionList.get(position);

        // Bind data to the ViewHolder's views
        holder.dateTextView.setText(transaction.getMostRecentDate());
        holder.typeTextView.setText(transaction.getMostRecentTransactionType());
        holder.amountTextView.setText(String.valueOf(transaction.getMostRecentPaymentAmount()));
        holder.iconImageView.setImageResource(transaction.getIconResource());
    }

    @Override
    public int getItemCount() {
        return recentTransactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView typeTextView;
        public TextView amountTextView;
        public ImageView iconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            typeTextView = itemView.findViewById(R.id.transactionTypeTextView);
            amountTextView = itemView.findViewById(R.id.paymentAmountTextView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }
    }
}

