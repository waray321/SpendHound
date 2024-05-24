package com.waray.spendhound;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.waray.spendhound.ui.borrow.BorrowFragment;

import java.util.ArrayList;
import java.util.Objects;

public class PendingStatusActivity extends AppCompatActivity {
    private TextView borrowerListTV, payerListTV;
    private ScrollView borrowerListScrollView, payerListScrollView;
    private boolean borrowerPayerClicked;
    private ImageView backBtn;
    private LinearLayout borrowerListLinearLayout, payerListLinearLayout;
    public ArrayList<OwedTransaction> borrowerList = new ArrayList<OwedTransaction>();
    public int borrowerNum;
    public String currentNickname;
    private RecyclerView borrowerListRecyclerView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_status);

        borrowerListTV = findViewById(R.id.borrowerListTV);
        payerListTV = findViewById(R.id.payerListTV);
        borrowerListScrollView = findViewById(R.id.borrowerListScrollView);
        payerListScrollView = findViewById(R.id.payerListScrollView);
        backBtn = findViewById(R.id.backBtn);
        borrowerListLinearLayout = findViewById(R.id.borrowerListLinearLayout);
        payerListLinearLayout = findViewById(R.id.payerListLinearLayout);

        borrowerPayerClicked = true;

        BorrowerListTVClicked();
        PayerListTVClicked();
        BackButtonCLicked();

        MainActivity mainActivity = new MainActivity();
        mainActivity.getCurrentNickname(new MainActivity.CurrentNicknameCallback() {
            @Override
            public void onCurrentNicknameReceived(String currentNickname) {
                showToast(currentNickname);
            }
        });

        BorrowerList();

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
                borrowerListScrollView.setVisibility(View.VISIBLE);
                payerListScrollView.setVisibility(View.GONE);

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
                borrowerListScrollView.setVisibility(View.GONE);
                payerListScrollView.setVisibility(View.VISIBLE);

                payerListTV.setEnabled(false);
                borrowerListTV.setEnabled(true);
                borrowerPayerClicked = false;
            }
        });
    }

    private void BorrowerList(){
        borrowerList.clear();

        DatabaseReference databaseReference = DeclareDatabase.getDBRefBorrows();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            MainActivity mainActivity = new MainActivity();

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                        for (DataSnapshot currentUserRef : daySnapshot.getChildren()) {
                            String currentUserStr = currentUserRef.getKey();
                            if (!Objects.equals(currentUserStr, currentNickname)) {
                                for (DataSnapshot timeSnapshot : currentUserRef.getChildren()) {
                                    BorrowTransaction borrowTransaction = timeSnapshot.getValue(BorrowTransaction.class);
                                    if (borrowTransaction != null) {
                                        String status = borrowTransaction.getStatus();
                                        if (Objects.equals(status, "Pending Borrow Approval")) {
                                            String borrower = borrowTransaction.getBorrowee();
                                            String date = borrowTransaction.getDate();
                                            String borrowedAmount = String.valueOf(borrowTransaction.getBorrowedAmountStr());

                                            mainActivity.changeFormatDate(date);

                                            // Create a RecentTransaction object and add it to the list
                                            OwedTransaction borrowerTrans = new OwedTransaction(
                                                    date,
                                                    borrower,
                                                    borrowedAmount,
                                                    status
                                            );
                                            borrowerList.add(borrowerTrans);
                                        } else {
                                            showToast("No Data" + status);
                                        }

                                    } else {
                                        showToast("No data");
                                    }
                                    RecyclerView recyclerView = findViewById(R.id.borrowerListRecyclerView);
                                    RecyclerView.Adapter<OwedTransactionAdapter.ViewHolder> adapter = new OwedTransactionAdapter(borrowerList);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();

                                    // Set the RecyclerView.LayoutManager
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PendingStatusActivity.this);
                                    recyclerView.setLayoutManager(layoutManager);
                                }
                            }
                        }
                    }
                }
                borrowerNum = borrowerList.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database read error
                String errorMessage = "Database read error occurred: " + databaseError.getMessage();
                Log.e("FirebaseDatabase", errorMessage);
            }
        });
    }
    private void BackButtonCLicked(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Optionally add any additional logic here if needed
    }

    public void showToast(String message) {
        Toast.makeText(PendingStatusActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
