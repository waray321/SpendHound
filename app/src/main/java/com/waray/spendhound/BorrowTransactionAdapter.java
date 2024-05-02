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

public class BorrowTransactionAdapter extends RecyclerView.Adapter<BorrowTransactionAdapter.ViewHolder> {
    private final ArrayList<BorrowTransaction> borrowTransactionList;
    private ArrayList<Integer> checkedPositions;

    public BorrowTransactionAdapter(ArrayList<BorrowTransaction> borrowTransactionList) {
        this.borrowTransactionList = borrowTransactionList;
        checkedPositions = new ArrayList<>();
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

        // Set click listener for payCheckBox
        holder.payCheckBox.setOnCheckedChangeListener(null); // To prevent triggering listener for recycled views
        holder.payCheckBox.setChecked(checkedPositions.contains(position));
        holder.payCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Add position to checkedPositions
                    if (!checkedPositions.contains(position)) {
                        checkedPositions.add(position);
                    }
                } else {
                    // Remove position from checkedPositions
                    checkedPositions.remove((Integer) position);
                }
            }
        });
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
        public CheckBox payCheckBox;
        public Button payNowBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            debtDateTV = itemView.findViewById(R.id.debtDateTV);
            debtBorroweeTV = itemView.findViewById(R.id.debtBorroweeTV);
            debtAmountBorrowedTV = itemView.findViewById(R.id.debtAmountBorrowedTV);
            debtStatusTV = itemView.findViewById(R.id.debtStatusTV);
            payCheckBox = itemView.findViewById(R.id.payCheckBox);
            payNowBtn = itemView.findViewById(R.id.payNowBtn);
        }
    }
}
