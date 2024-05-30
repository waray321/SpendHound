package com.waray.spendhound;

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

public class BorrowerListTransactionAdapter extends RecyclerView.Adapter<BorrowerListTransactionAdapter.ViewHolder> {
    private List<BorrowerListTransaction> transactionList;
    private Context context;

    public BorrowerListTransactionAdapter(Context context, List<BorrowerListTransaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.borrower_row_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BorrowerListTransaction transaction = transactionList.get(position);
        holder.hoursAgoTV.setText(transaction.getDate());
        holder.borrowerNameTV.setText(transaction.getBorrowee());
        holder.amountBorrowedTV.setText(transaction.getBorrowedAmountStr());

        // Set a placeholder image and a tag for the ImageView
        holder.borrowerImg.setImageResource(R.drawable.placeholder_profile_image);
        holder.borrowerImg.setTag(transaction.getBorrowee());

        // Load the profile image asynchronously
        setProfileImage(holder.borrowerImg, transaction.getBorrowee());

        holder.acceptBorrowerBtn.setOnClickListener(v -> showConfirmationDialog("Accept", transaction));
        holder.declineBorrowerBtn.setOnClickListener(v -> showConfirmationDialog("Decline", transaction));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    private void showConfirmationDialog(String action, BorrowerListTransaction transaction) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_borrowerlistconfirmation, null);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Find views in the custom layout
        TextView confirmAction = dialogView.findViewById(R.id.confirmAction);
        Button payNowConfirmBtn = dialogView.findViewById(R.id.payNowConfirmBtn);
        Button closeButton = dialogView.findViewById(R.id.closeButton);

        // Set the action text
        confirmAction.setText(action);

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set button click listeners
        payNowConfirmBtn.setOnClickListener(v -> {
            // Handle the action (Accept/Decline)
            if ("Accept".equalsIgnoreCase(action)) {
                transaction.setStatus("Unpaid");
                Toast.makeText(context, "Accepted: " + transaction.getBorrowee(), Toast.LENGTH_SHORT).show();
            } else if ("Decline".equalsIgnoreCase(action)) {
                transaction.setStatus("Declined");
                Toast.makeText(context, "Declined: " + transaction.getBorrowee(), Toast.LENGTH_SHORT).show();
            }

            // Notify the adapter that the data has changed
            notifyDataSetChanged();

            // Close the dialog
            dialog.dismiss();
        });

        closeButton.setOnClickListener(v -> {
            dialog.dismiss(); // Close the dialog
        });

        // Show the dialog
        dialog.show();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView borrowerImg;
        public TextView hoursAgoTV;
        public TextView borrowerNameTV;
        public TextView amountBorrowedTV;
        public Button acceptBorrowerBtn;
        public Button declineBorrowerBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            borrowerImg = itemView.findViewById(R.id.borrowerImg);
            hoursAgoTV = itemView.findViewById(R.id.hoursAgoTV);
            borrowerNameTV = itemView.findViewById(R.id.borrowerNameTV);
            amountBorrowedTV = itemView.findViewById(R.id.amountBorrowedTV);
            acceptBorrowerBtn = itemView.findViewById(R.id.acceptBorrowerBtn);
            declineBorrowerBtn = itemView.findViewById(R.id.declineBorrowerBtn);
        }
    }

    private void setProfileImage(ImageView imageView, String borrowerNameTV) {
        if (imageView == null || borrowerNameTV == null) {
            Log.e("BorrowerListTransactionAdapter", "ImageView or borrowerNameTV is null.");
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = usersRef.orderByChild("username").equalTo(borrowerNameTV);

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
                                if (borrowerNameTV.equals(imageView.getTag()) && context != null) {
                                    Glide.with(context).load(uri).placeholder(R.drawable.placeholder_profile_image).into(imageView);
                                }
                            }).addOnFailureListener(e -> {
                                Log.e("FirebaseStorage", "Failed to get download URL: " + e.getMessage());
                                if (borrowerNameTV.equals(imageView.getTag())) {
                                    imageView.setImageResource(R.drawable.placeholder_profile_image); // default image
                                }
                            });
                        }
                    }
                } else {
                    if (borrowerNameTV.equals(imageView.getTag())) {
                        imageView.setImageResource(R.drawable.placeholder_profile_image); // default image
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDatabase", "Profile image query cancelled: " + databaseError.getMessage());
                if (borrowerNameTV.equals(imageView.getTag())) {
                    imageView.setImageResource(R.drawable.placeholder_profile_image); // default image
                }
            }
        });
    }
}
