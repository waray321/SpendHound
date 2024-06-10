package com.waray.spendhound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PayerListTransactionAdapter extends RecyclerView.Adapter<PayerListTransactionAdapter.ViewHolder> {
    private List<BorrowerListTransaction> transactionList;
    private final List<String[]> pathList;
    private Context context;
    private OnTransactionStatusUpdatedListener statusUpdatedListener;

    public interface OnTransactionStatusUpdatedListener {
        void onTransactionStatusUpdated();
    }

    public PayerListTransactionAdapter(Context context, List<BorrowerListTransaction> transactionList, List<String[]> pathList, PendingStatusActivity statusUpdatedListener, Button confirmAllPayerBtn, Button denyAllPayerBtn) {
        this.context = context;
        this.transactionList = transactionList;
        this.pathList = pathList;
        this.statusUpdatedListener = statusUpdatedListener;

        if (confirmAllPayerBtn != null) {
            confirmAllPayerBtn.setOnClickListener(v -> handleAllTransactions("Confirm"));
        } else {
            Log.e("PayerListTransactionAdapter", "confirmAllPayerBtn is null");
        }

        if (denyAllPayerBtn != null) {
            denyAllPayerBtn.setOnClickListener(v -> handleAllTransactions("Deny"));
        } else {
            Log.e("PayerListTransactionAdapter", "denyAllPayerBtn is null");
        }
    }

    public BorrowerListTransaction getBorrowerListTransaction(int position) {
        return transactionList.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.payer_row_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BorrowerListTransaction transaction = transactionList.get(position);
        holder.hoursAgoTV.setText(transaction.getDate());
        holder.payerNameTV.setText(transaction.getBorrowee());
        holder.amountPaidTV.setText(transaction.getBorrowedAmountStr());

        // Set a placeholder image and a tag for the ImageView
        holder.payerImg.setImageResource(R.drawable.placeholder_profile_image);
        holder.payerNameTV.setTag(transaction.getBorrowee());

        // Load the profile image asynchronously
        setProfileImage(holder.payerImg, transaction.getBorrowee());

        if (holder.confirmAllPayerBtn != null) {
            holder.confirmAllPayerBtn.setOnClickListener(v -> showConfirmationDialog("Confirm", transaction, pathList.get(position), position));
        } else {
            Log.e("PayerListTransactionAdapter", "confirmAllPayerBtn is null in onBindViewHolder");
        }

        if (holder.denyAllPayerBtn != null) {
            holder.denyAllPayerBtn.setOnClickListener(v -> showConfirmationDialog("Deny", transaction, pathList.get(position), position));
        } else {
            Log.e("PayerListTransactionAdapter", "denyAllPayerBtn is null in onBindViewHolder");
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    private void showConfirmationDialog(String action, BorrowerListTransaction transaction, String[] path, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_borrowerlistconfirmation, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        TextView confirmAction = dialogView.findViewById(R.id.confirmAction);
        Button payNowConfirmBtn = dialogView.findViewById(R.id.payNowConfirmBtn);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

        confirmAction.setText(action);

        AlertDialog dialog = builder.create();

        payNowConfirmBtn.setOnClickListener(v -> {
            if ("Accept".equalsIgnoreCase(action)) {
                updateTransactionStatus(transaction, path, position, "Unpaid");
            } else if ("Decline".equalsIgnoreCase(action)) {
                updateTransactionStatus(transaction, path, position, "Declined");
            }
            dialog.dismiss();
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateTransactionStatus(BorrowerListTransaction transaction, String[] path, int position, String status) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("borrows")
                .child(path[0]).child(path[1]).child(path[2]).child(path[3]);

        transaction.setStatus(status);
        userRef.child("status").setValue(status)
                .addOnSuccessListener(aVoid -> {
                    transactionList.set(position, transaction);
                    notifyDataSetChanged();
                    if (statusUpdatedListener != null) {
                        statusUpdatedListener.onTransactionStatusUpdated();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView payerImg;
        public TextView hoursAgoTV;
        public TextView payerNameTV;
        public TextView amountPaidTV;
        public Button confirmPayerBtn, denyPayerBtn, confirmAllPayerBtn, denyAllPayerBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            payerImg = itemView.findViewById(R.id.payerImg);
            hoursAgoTV = itemView.findViewById(R.id.hoursAgoTV);
            payerNameTV = itemView.findViewById(R.id.payerNameTV);
            amountPaidTV = itemView.findViewById(R.id.amountPaidTV);
            confirmPayerBtn = itemView.findViewById(R.id.confirmPayerBtn);
            denyPayerBtn = itemView.findViewById(R.id.denyPayerBtn);
            confirmAllPayerBtn = itemView.findViewById(R.id.confirmAllPayerBtn);
            denyAllPayerBtn = itemView.findViewById(R.id.denyAllPayerBtn);
        }
    }

    private void handleAllTransactions(String action) {
        showConfirmationDialog(action, transactionList.get(0), pathList.get(0), 0, true);
    }

    private void showConfirmationDialog(String action, BorrowerListTransaction transaction, String[] path, int position, boolean isAll) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_borrowerlistconfirmation, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        TextView confirmAction = dialogView.findViewById(R.id.confirmAction);
        Button payNowConfirmBtn = dialogView.findViewById(R.id.payNowConfirmBtn);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

        confirmAction.setText(action);

        AlertDialog dialog = builder.create();

        payNowConfirmBtn.setOnClickListener(v -> {
            if (isAll) {
                updateAllTransactionStatus(action.equals("Confirm") ? "Paid" : "Denied Payment");
            } else {
                if ("Confirm".equalsIgnoreCase(action)) {
                    updateTransactionStatus(transaction, path, position, "Paid");
                } else if ("Deny".equalsIgnoreCase(action)) {
                    updateTransactionStatus(transaction, path, position, "Denied Payment");
                }
            }
            dialog.dismiss();
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateAllTransactionStatus(String status) {
        for (int i = 0; i < transactionList.size(); i++) {
            BorrowerListTransaction transaction = transactionList.get(i);
            String[] path = pathList.get(i);
            final int index = i;
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("borrows")
                    .child(path[0]).child(path[1]).child(path[2]).child(path[3]);

            transaction.setStatus(status);
            userRef.child("status").setValue(status)
                    .addOnSuccessListener(aVoid -> {
                        transactionList.set(index, transaction);
                        if (index == transactionList.size() - 1) {
                            notifyDataSetChanged();
                            if (statusUpdatedListener != null) {
                                statusUpdatedListener.onTransactionStatusUpdated();
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void setProfileImage(ImageView imageView, String payerNameTV) {
        if (imageView == null || payerNameTV == null) {
            Log.e("PayerListTransactionAdapter", "ImageView or payerNameTV is null.");
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = usersRef.orderByChild("username").equalTo(payerNameTV);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey(); // Assuming the key is the userId
                        if (userId != null) {
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images").child(userId);
                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Check if the tag is still valid before loading the image
                                Log.d("PayerListTransactionAdapter", "Fetched profile image URL: " + uri.toString());
                                if (payerNameTV.equals(imageView.getTag()) && context != null) {
                                    Glide.with(context)
                                            .load(uri)
                                            .placeholder(R.drawable.placeholder_profile_image)
                                            .into(imageView);
                                }
                            }).addOnFailureListener(e -> {
                                Log.e("FirebaseStorage", "Failed to get download URL: " + e.getMessage());
                                if (payerNameTV.equals(imageView.getTag())) {
                                    imageView.setImageResource(R.drawable.placeholder_profile_image); // default image
                                }
                            });
                        }
                    }
                } else {
                    Log.e("PayerListTransactionAdapter", "No user found with username: " + payerNameTV);
                    if (payerNameTV.equals(imageView.getTag())) {
                        imageView.setImageResource(R.drawable.placeholder_profile_image); // default image
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDatabase", "Profile image query cancelled: " + databaseError.getMessage());
                if (payerNameTV.equals(imageView.getTag())) {
                    imageView.setImageResource(R.drawable.placeholder_profile_image); // default image
                }
            }
        });
    }

}
