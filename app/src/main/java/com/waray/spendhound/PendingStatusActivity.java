package com.waray.spendhound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.waray.spendhound.ui.borrow.BorrowFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PendingStatusActivity extends AppCompatActivity implements BorrowerListTransactionAdapter.OnTransactionStatusUpdatedListener, PayerListTransactionAdapter.OnTransactionStatusUpdatedListener {
    private TextView borrowerListTV, payerListTV, allTV;
    private ScrollView borrowerListScrollView, payerListScrollView;
    private boolean borrowerPayerClicked;
    private ImageView backBtn, borrowerImg, payerImg;
    private LinearLayout borrowerListLinearLayout, payerListLinearLayout, borrowerListBtn, payerListBtn;
    public int borrowerNum, payerNum;
    public String currentNickname, currentNickname2;
    private RecyclerView borrowerListRecyclerView, payerListRecyclerView;
    private BorrowerListTransactionAdapter adapter;
    private PayerListTransactionAdapter adapterPayer;
    private List<BorrowerListTransaction> borrowerListTransactions, payerListTransactions;
    private List<String[]> borrowerListPath, payerListPath;
    List<BorrowerListTransaction> transactionList;
    List<String[]> pathList;
    private Context context;
    public Button acceptAllBorrowerBtn, declineAllBorrowerBtn, acceptBorrowerBtn, declineBorrowerBtn, confirmPayerBtn,denyPayerBtn,confirmAllPayerBtn,denyAllPayerBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_status);

        context = this;

        borrowerListTV = findViewById(R.id.borrowerListTV);
        payerListTV = findViewById(R.id.payerListTV);
        borrowerListScrollView = findViewById(R.id.borrowerListScrollView);
        payerListScrollView = findViewById(R.id.payerListScrollView);
        backBtn = findViewById(R.id.backBtn);
        borrowerListLinearLayout = findViewById(R.id.borrowerListLinearLayout);
        payerListLinearLayout = findViewById(R.id.payerListLinearLayout);
        borrowerImg = findViewById(R.id.borrowerImg);
        acceptBorrowerBtn = findViewById(R.id.acceptBorrowerBtn);
        declineBorrowerBtn = findViewById(R.id.declineBorrowerBtn);
        acceptAllBorrowerBtn = findViewById(R.id.acceptAllBorrowerBtn);
        declineAllBorrowerBtn = findViewById(R.id.declineAllBorrowerBtn);
        allTV = findViewById(R.id.allTV);
        payerListBtn = findViewById(R.id.payerListBtn);
        borrowerListBtn = findViewById(R.id.borrowerListBtn);
        payerImg = findViewById(R.id.payerImg);
        confirmPayerBtn = findViewById(R.id.confirmPayerBtn);
        denyPayerBtn = findViewById(R.id.denyPayerBtn);
        confirmAllPayerBtn = findViewById(R.id.confirmAllPayerBtn);
        denyAllPayerBtn = findViewById(R.id.denyAllPayerBtn);

        borrowerPayerClicked = true;

        BorrowerListTVClicked();
        PayerListTVClicked();
        BackButtonCLicked();

        MainActivity mainActivity = new MainActivity();
        mainActivity.getCurrentNickname(new MainActivity.CurrentNicknameCallback() {
            @Override
            public void onCurrentNicknameReceived(String currentNickname) {
                currentNickname2 = currentNickname;
            }
        });

        BorrowerList();
        PayerList();
    }

    private void BorrowerListTVClicked() {
        borrowerListTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payerListTV.setBackgroundResource(R.drawable.button_background_invisible);
                borrowerListTV.setBackgroundResource(R.drawable.top_round_border);
                payerListTV.setTextColor(ContextCompat.getColor(PendingStatusActivity.this, R.color.whitest));
                borrowerListTV.setTextColor(ContextCompat.getColor(PendingStatusActivity.this, R.color.darkBlue));
                borrowerListScrollView.setVisibility(View.VISIBLE);
                payerListScrollView.setVisibility(View.GONE);
                borrowerListLinearLayout.setVisibility(View.VISIBLE);
                payerListLinearLayout.setVisibility(View.GONE);
                borrowerListBtn.setVisibility(View.VISIBLE);
                payerListBtn.setVisibility(View.GONE);

                borrowerListTV.setEnabled(false);
                payerListTV.setEnabled(true);
                borrowerPayerClicked = true;
            }
        });
    }

    private void PayerListTVClicked() {
        payerListTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrowerListTV.setBackgroundResource(R.drawable.button_background_invisible);
                payerListTV.setBackgroundResource(R.drawable.top_round_border);
                borrowerListTV.setTextColor(ContextCompat.getColor(PendingStatusActivity.this, R.color.whitest));
                payerListTV.setTextColor(ContextCompat.getColor(PendingStatusActivity.this, R.color.darkBlue));
                payerListScrollView.setVisibility(View.VISIBLE);
                borrowerListScrollView.setVisibility(View.GONE);
                borrowerListLinearLayout.setVisibility(View.GONE);
                payerListLinearLayout.setVisibility(View.VISIBLE);
                borrowerListBtn.setVisibility(View.GONE);
                payerListBtn.setVisibility(View.VISIBLE);

                payerListTV.setEnabled(false);
                borrowerListTV.setEnabled(true);
                borrowerPayerClicked = false;
            }
        });
    }

    private void BorrowerList() {
        borrowerListTransactions = new ArrayList<>();
        borrowerListPath = new ArrayList<String[]>();
        DatabaseReference databaseReference = DeclareDatabase.getDBRefBorrows();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            MainActivity mainActivity = new MainActivity();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    String month = monthSnapshot.getKey();
                    for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                        String day = daySnapshot.getKey();
                        for (DataSnapshot currentUserRef : daySnapshot.getChildren()) {
                            String currentUserStr = currentUserRef.getKey();
                            if (!Objects.equals(currentUserStr, currentNickname2)) {
                                for (DataSnapshot timeSnapshot : currentUserRef.getChildren()) {
                                    String time = timeSnapshot.getKey();
                                    BorrowerListTransaction borrowerListTransaction = timeSnapshot.getValue(BorrowerListTransaction.class);
                                    if (borrowerListTransaction != null) {
                                        String status = borrowerListTransaction.getStatus();
                                        if (Objects.equals(status, "Pending Approval")) {
                                            String borrowee = currentUserStr;
                                            String borrowedAmountStr = borrowerListTransaction.getBorrowedAmountStr();
                                            borrowedAmountStr = "₱" + borrowedAmountStr;
                                            String date = borrowerListTransaction.getDate();


                                            String formatPattern = "MMMM-dd-yyyy HH:mm:ss";
                                            long secondsSinceDate = 0;

                                            try {
                                                // Combine date and time string
                                                String dateTime = date + " " + time;  // Assuming 'time' is in "HH:mm:ss" format
                                                DateFormat dateFormat = new SimpleDateFormat(formatPattern, Locale.ENGLISH);
                                                Date pastDate = dateFormat.parse(dateTime);

                                                Date currentDate = new Date();

                                                long timeDifferenceMillis = currentDate.getTime() - pastDate.getTime();

                                                secondsSinceDate = timeDifferenceMillis / 1000;

                                                String timeDifferenceStr;
                                                // Convert seconds to appropriate units
                                                if (secondsSinceDate >= 60 * 60 * 24 * 365) { // More than or equal to a year
                                                    long years = secondsSinceDate / (60 * 60 * 24 * 365);
                                                    timeDifferenceStr = years + "y";
                                                } else if (secondsSinceDate >= 60 * 60 * 24 * 30) { // More than or equal to a month
                                                    long months = secondsSinceDate / (60 * 60 * 24 * 30);
                                                    timeDifferenceStr = months + "mo";
                                                } else if (secondsSinceDate >= 60 * 60 * 24) { // More than or equal to a day
                                                    long days = secondsSinceDate / (60 * 60 * 24);
                                                    timeDifferenceStr = days + "d";
                                                } else if (secondsSinceDate >= 60 * 60) { // More than or equal to an hour
                                                    long hours = secondsSinceDate / (60 * 60);
                                                    timeDifferenceStr = hours + "h";
                                                } else if (secondsSinceDate >= 60) { // More than or equal to a minute
                                                    long minutes = secondsSinceDate / 60;
                                                    timeDifferenceStr = minutes + "m";
                                                } else { // Less than a minute
                                                    timeDifferenceStr = secondsSinceDate + "s";
                                                }

                                                date = timeDifferenceStr;
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            BorrowerListTransaction borrowerTrans = new BorrowerListTransaction(
                                                    date,
                                                    borrowee,
                                                    borrowedAmountStr,
                                                    status
                                            );
                                            borrowerListTransactions.add(borrowerTrans);

                                            borrowerListPath.add(new String[]{month, day, currentUserStr, time});

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                adapter = new BorrowerListTransactionAdapter(context, borrowerListTransactions, borrowerListPath, PendingStatusActivity.this,  acceptAllBorrowerBtn, declineAllBorrowerBtn);
                borrowerListRecyclerView = findViewById(R.id.borrowerListRecyclerView);
                borrowerListRecyclerView.setAdapter(adapter);
                borrowerListRecyclerView.setLayoutManager(new LinearLayoutManager(PendingStatusActivity.this));
                adapter.notifyDataSetChanged();

                borrowerNum = borrowerListTransactions.size();
                if (borrowerNum < 2){
                    acceptAllBorrowerBtn.setEnabled(false);
                    declineAllBorrowerBtn.setEnabled(false);
                    acceptAllBorrowerBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(PendingStatusActivity.this, R.color.grey)));
                    declineAllBorrowerBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(PendingStatusActivity.this, R.color.grey)));
                } else {
                    acceptAllBorrowerBtn.setEnabled(true);
                    declineAllBorrowerBtn.setEnabled(true);
                    acceptAllBorrowerBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(PendingStatusActivity.this, R.color.yellow)));
                    declineAllBorrowerBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(PendingStatusActivity.this, R.color.red)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDatabase", "Database read error: " + databaseError.getMessage());
            }
        });
    }

    private void PayerList() {
        payerListTransactions = new ArrayList<>();
        payerListPath = new ArrayList<String[]>();
        DatabaseReference databaseReference = DeclareDatabase.getDBRefBorrows();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            MainActivity mainActivity = new MainActivity();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    String month = monthSnapshot.getKey();
                    for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                        String day = daySnapshot.getKey();
                        for (DataSnapshot currentUserRef : daySnapshot.getChildren()) {
                            String currentUserStr = currentUserRef.getKey();
                            if (!Objects.equals(currentUserStr, currentNickname2)) {
                                for (DataSnapshot timeSnapshot : currentUserRef.getChildren()) {
                                    String time = timeSnapshot.getKey();
                                    BorrowerListTransaction borrowerListTransaction = timeSnapshot.getValue(BorrowerListTransaction.class);
                                    if (borrowerListTransaction != null) {
                                        String status = borrowerListTransaction.getStatus();
                                        if (Objects.equals(status, "Payment Pending")) {
                                            String borrowee = currentUserStr;
                                            String borrowedAmountStr = borrowerListTransaction.getBorrowedAmountStr();
                                            borrowedAmountStr = "₱" + borrowedAmountStr;
                                            String date = borrowerListTransaction.getDate();


                                            String formatPattern = "MMMM-dd-yyyy HH:mm:ss";
                                            long secondsSinceDate = 0;

                                            try {
                                                // Combine date and time string
                                                String dateTime = date + " " + time;  // Assuming 'time' is in "HH:mm:ss" format
                                                DateFormat dateFormat = new SimpleDateFormat(formatPattern, Locale.ENGLISH);
                                                Date pastDate = dateFormat.parse(dateTime);

                                                Date currentDate = new Date();

                                                long timeDifferenceMillis = currentDate.getTime() - pastDate.getTime();

                                                secondsSinceDate = timeDifferenceMillis / 1000;

                                                String timeDifferenceStr;
                                                // Convert seconds to appropriate units
                                                if (secondsSinceDate >= 60 * 60 * 24 * 365) { // More than or equal to a year
                                                    long years = secondsSinceDate / (60 * 60 * 24 * 365);
                                                    timeDifferenceStr = years + "y";
                                                } else if (secondsSinceDate >= 60 * 60 * 24 * 30) { // More than or equal to a month
                                                    long months = secondsSinceDate / (60 * 60 * 24 * 30);
                                                    timeDifferenceStr = months + "mo";
                                                } else if (secondsSinceDate >= 60 * 60 * 24) { // More than or equal to a day
                                                    long days = secondsSinceDate / (60 * 60 * 24);
                                                    timeDifferenceStr = days + "d";
                                                } else if (secondsSinceDate >= 60 * 60) { // More than or equal to an hour
                                                    long hours = secondsSinceDate / (60 * 60);
                                                    timeDifferenceStr = hours + "h";
                                                } else if (secondsSinceDate >= 60) { // More than or equal to a minute
                                                    long minutes = secondsSinceDate / 60;
                                                    timeDifferenceStr = minutes + "m";
                                                } else { // Less than a minute
                                                    timeDifferenceStr = secondsSinceDate + "s";
                                                }

                                                date = timeDifferenceStr;
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            BorrowerListTransaction borrowerTrans = new BorrowerListTransaction(
                                                    date,
                                                    borrowee,
                                                    borrowedAmountStr,
                                                    status
                                            );
                                            payerListTransactions.add(borrowerTrans);

                                            payerListPath.add(new String[]{month, day, currentUserStr, time});

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                adapterPayer = new PayerListTransactionAdapter(context, payerListTransactions, payerListPath, PendingStatusActivity.this,  confirmAllPayerBtn, denyAllPayerBtn);
                payerListRecyclerView = findViewById(R.id.payerListRecyclerView);
                payerListRecyclerView.setAdapter(adapterPayer);
                payerListRecyclerView.setLayoutManager(new LinearLayoutManager(PendingStatusActivity.this));
                adapterPayer.notifyDataSetChanged();

                payerNum = payerListTransactions.size();
                if (payerNum < 2){
                    confirmAllPayerBtn.setEnabled(false);
                    denyAllPayerBtn.setEnabled(false);
                    confirmAllPayerBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(PendingStatusActivity.this, R.color.grey)));
                    denyAllPayerBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(PendingStatusActivity.this, R.color.grey)));
                } else {
                    confirmAllPayerBtn.setEnabled(true);
                    denyAllPayerBtn.setEnabled(true);
                    confirmAllPayerBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(PendingStatusActivity.this, R.color.yellow)));
                    denyAllPayerBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(PendingStatusActivity.this, R.color.red)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDatabase", "Database read error: " + databaseError.getMessage());
            }
        });
    }

    private void BackButtonCLicked() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, BorrowFragment.class);
        startActivity(intent);
    }

    @Override
    public void onTransactionStatusUpdated() {
        BorrowerList();
    }


    private void AcceptDeclineBtnClicked(){
        acceptBorrowerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allTV.setVisibility(View.GONE);
            }
        });
        declineBorrowerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allTV.setVisibility(View.GONE);
            }
        });
    }

    public void showToast(String message) {
        Toast.makeText(PendingStatusActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
