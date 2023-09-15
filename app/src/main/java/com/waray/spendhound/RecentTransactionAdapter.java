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

    private List<RecentTransaction> recentTransactionList;
    public String mostRecentDate;
    public String mostRecentTransactionType;
    public int mostRecentPaymentAmount;

    public RecentTransactionAdapter(List<RecentTransaction> recentTransactionList) {
        this.recentTransactionList = recentTransactionList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to your ViewHolder here
        RecentTransaction recentTransaction = recentTransactionList.get(position);
        getRecentTransaction();

        holder.dateTextView.setText(mostRecentDate);
        holder.transactionTypeTextView.setText(mostRecentTransactionType);
        holder.paymentAmountTextView.setText(String.valueOf(mostRecentPaymentAmount));
        if ("Electricity".equals(mostRecentTransactionType)){
            holder.iconImageView.setImageResource(R.drawable.lightning_bolt);
        } else if ("Water Bill".equals(mostRecentTransactionType)){
            holder.iconImageView.setImageResource(R.drawable.faucet);
        } else if ("Mineral Water".equals(mostRecentTransactionType)){
            holder.iconImageView.setImageResource(R.drawable.water);
        } else if ("Groceries".equals(mostRecentTransactionType)){
            holder.iconImageView.setImageResource(R.drawable.groceries);
        } else if ("Foods".equals(mostRecentTransactionType)){
            holder.iconImageView.setImageResource(R.drawable.hamburger);
        } else if ("House Necessity".equals(mostRecentTransactionType)){
            holder.iconImageView.setImageResource(R.drawable.sofa);
        } else if ("Transportation".equals(mostRecentTransactionType)){
            holder.iconImageView.setImageResource(R.drawable.vehicles);
        } else {
            holder.iconImageView.setImageResource(R.drawable.house);
        }
    }

    @Override
    public int getItemCount() {
        return recentTransactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconImageView;
        public TextView transactionTypeTextView;
        public TextView dateTextView;
        public TextView paymentAmountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            transactionTypeTextView = itemView.findViewById(R.id.transactionTypeTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            paymentAmountTextView = itemView.findViewById(R.id.paymentAmountTextView);
        }
    }
    public void getRecentTransaction(){
        // Create a reference to the "transactions" node
        DatabaseReference databaseReference = DeclareDatabase.getDBRefTransaction();
        DatabaseReference transactionsRef = databaseReference.child("transactions");

        // Create a query to order the data by child 'dd' in reverse order (most recent first)
        Query query = transactionsRef.orderByChild("dd").limitToLast(5);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<RecentTransaction> recentTransaction = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String mostRecentMonthYear = snapshot.child("MMMM-yyyy").getValue(String.class);
                        String mostRecentDay = snapshot.child("dd").getValue(String.class);
                        String mostRecentTransactionType = snapshot.child("transactionType").getValue(String.class);
                        int mostRecentPaymentAmount = snapshot.child("paymentAmount").getValue(Integer.class);
                        int iconResource;
                        if ("Electricity".equals(mostRecentTransactionType)){
                            iconResource = R.drawable.lightning_bolt;
                        } else if ("Water Bill".equals(mostRecentTransactionType)){
                            iconResource = R.drawable.faucet;
                        } else if ("Mineral Water".equals(mostRecentTransactionType)){
                            iconResource = R.drawable.water;
                        } else if ("Groceries".equals(mostRecentTransactionType)){
                            iconResource = R.drawable.groceries;
                        } else if ("Foods".equals(mostRecentTransactionType)){
                            iconResource = R.drawable.hamburger;
                        } else if ("House Necessity".equals(mostRecentTransactionType)){
                            iconResource = R.drawable.sofa;
                        } else if ("Transportation".equals(mostRecentTransactionType)){
                            iconResource = R.drawable.vehicles;
                        } else {
                            iconResource = R.drawable.house;
                        }
                        String mostRecentDate = mostRecentMonthYear + "-" + mostRecentDay;

                        // Create a Transaction object and add it to the list
                        RecentTransaction recentTransaction2 = new RecentTransaction(
                                mostRecentDate,
                                mostRecentTransactionType,
                                mostRecentPaymentAmount,
                                iconResource
                        );
                        recentTransaction.add(recentTransaction2);
                    }
                    // Update your RecyclerView adapter with the recent transactions
                    setData(recentTransaction);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database read error
                String errorMessage = "Database read error occurred: " + databaseError.getMessage();
                Log.e("FirebaseDatabase", errorMessage);
            }
        });
    }

    public void setData(List<RecentTransaction> recentTransaction) {
        recentTransactionList = recentTransaction;
        notifyDataSetChanged();
    }
}

