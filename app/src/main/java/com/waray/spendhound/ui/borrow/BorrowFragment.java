package com.waray.spendhound.ui.borrow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.waray.spendhound.BorrowNowActivity;
import com.waray.spendhound.BorrowTransaction;
import com.waray.spendhound.DeclareDatabase;
import com.waray.spendhound.MainActivity;
import com.waray.spendhound.R;
import com.waray.spendhound.SpinnerItemMonths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BorrowFragment extends Fragment {

    private Spinner monthYearSpinner;
    public List<String> debtSortedMonths, owedSortedMonths;
    private Button borrowNowBtn, payNowBtn;
    private TextView owedTV, debtTV, noOwedTextView, noDebtTextView;
    private LinearLayout debtButtons;
    private ScrollView debtScrollView, owedScrollView;
    public String selectedMonth;
    private boolean monthFilter;
    public String currentNickname = "";

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow, container, false);
        monthYearSpinner = view.findViewById(R.id.monthYearSpinner);
        borrowNowBtn = view.findViewById(R.id.borrowNowBtn);
        payNowBtn = view.findViewById(R.id.payNowBtn);
        owedTV = view.findViewById(R.id.owedTV);
        debtTV = view.findViewById(R.id.debtTV);
        debtButtons = view.findViewById(R.id.debtButtons);
        debtScrollView = view.findViewById(R.id.debtScrollView);
        owedScrollView = view.findViewById(R.id.owedScrollView);
        noOwedTextView = view.findViewById(R.id.noOwedTextView);
        noDebtTextView = view.findViewById(R.id.noDebtTextView);
        monthFilter = true;


        getCurrentNickname();
        BorrowNow();
        OwedTVClicked();
        DebtTVClicked();
        monthFilterSelected();

        OwedMonthlyFilterList();


        // Get the hosting Activity and remove the ActionBar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().hide();
        }
        return view;
    }

    public void DebtMonthlyFilterList() {
        DatabaseReference transRef = DeclareDatabase.getDBRefBorrows();
        // Initialize an empty set to store unique months
        Set<String> debtUniqueMonthYear = new HashSet<>();
        transRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                debtUniqueMonthYear.add("All");
                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    String monthYear = monthSnapshot.getKey();
                    for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                        for (DataSnapshot currentUserRef : daySnapshot.getChildren()) {
                            String currentUserStr = currentUserRef.getKey();
                            if (Objects.equals(currentUserStr, currentNickname)) {
                                debtUniqueMonthYear.add(monthYear);
                            }
                        }
                    }
                }

                // Convert the Set to a List for sorting (if needed)
                debtSortedMonths = new ArrayList<>(debtUniqueMonthYear);
                Collections.sort(debtSortedMonths);

                monthYearSpinner.setBackgroundResource(R.drawable.transparent_background);

                SpinnerItemMonths adapter = new SpinnerItemMonths(getActivity(), debtSortedMonths);
                monthYearSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database read error
                String errorMessage = "Database read error occurred: " + databaseError.getMessage();
                Log.e("FirebaseDatabase", errorMessage);
            }
        });
    }

    public void OwedMonthlyFilterList() {
        DatabaseReference transRef = DeclareDatabase.getDBRefBorrows();
        // Initialize an empty set to store unique months
        Set<String> owedUniqueMonthYear = new HashSet<>();
        transRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                owedUniqueMonthYear.add("All");
                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    String monthYear = monthSnapshot.getKey();
                    for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                        for (DataSnapshot currentUserRef : daySnapshot.getChildren()) {
                            String currentUserStr = currentUserRef.getKey();
                            if (!Objects.equals(currentUserStr, currentNickname)) {
                                for (DataSnapshot timeSnapshot : currentUserRef.getChildren()) {
                                    BorrowTransaction borrowTransaction = timeSnapshot.getValue(BorrowTransaction.class);
                                    if (borrowTransaction != null) {
                                        String borrower = borrowTransaction.getBorrowee();
                                        if (Objects.equals(borrower, currentNickname)) {
                                            owedUniqueMonthYear.add(monthYear);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Convert the Set to a List for sorting (if needed)
                owedSortedMonths = new ArrayList<>(owedUniqueMonthYear);
                Collections.sort(owedSortedMonths);

                monthYearSpinner.setBackgroundResource(R.drawable.transparent_background);

                SpinnerItemMonths adapter = new SpinnerItemMonths(getActivity(), owedSortedMonths);
                monthYearSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database read error
                String errorMessage = "Database read error occurred: " + databaseError.getMessage();
                Log.e("FirebaseDatabase", errorMessage);
            }
        });
    }

    public void getCurrentNickname() {
        String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference usersRef = DeclareDatabase.getDatabaseReference().child(currentUserID);
        usersRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the username from the dataSnapshot and assign it to usernamePost
                    currentNickname = dataSnapshot.getValue(String.class);
                    Log.d("FirebaseDatabase", "Nickname loaded: " + currentNickname);
                } else {
                    Log.d("FirebaseDatabase", "Nickname not found in database.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database read error
                String errorMessage = "Database read error occurred: " + databaseError.getMessage();
                Log.e("FirebaseDatabase", errorMessage);
            }
        });
    }

    private void BorrowNow() {
        borrowNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to NewActivity
                Intent intent = new Intent(getActivity(), BorrowNowActivity.class);
                startActivity(intent);
            }
        });
    }

    private void OwedTVClicked() {
        owedTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debtTV.setBackgroundResource(R.drawable.button_background_invisible);
                owedTV.setBackgroundResource(R.drawable.top_round_border);
                debtTV.setTextColor(ContextCompat.getColor(getContext(), R.color.whitest));
                owedTV.setTextColor(ContextCompat.getColor(getContext(), R.color.darkBlue));
                debtButtons.setVisibility(View.INVISIBLE);
                owedScrollView.setVisibility(View.VISIBLE);
                debtScrollView.setVisibility(View.GONE);

                owedTV.setEnabled(false);
                debtTV.setEnabled(true);
                monthFilter = true;

                monthYearSpinner.setSelection(0);
                OwedMonthlyFilterList();
            }
        });
    }

    private void DebtTVClicked() {
        debtTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owedTV.setBackgroundResource(R.drawable.button_background_invisible);
                debtTV.setBackgroundResource(R.drawable.top_round_border);
                owedTV.setTextColor(ContextCompat.getColor(getContext(), R.color.whitest));
                debtTV.setTextColor(ContextCompat.getColor(getContext(), R.color.darkBlue));
                debtButtons.setVisibility(View.VISIBLE);
                owedScrollView.setVisibility(View.GONE);
                debtScrollView.setVisibility(View.VISIBLE);

                debtTV.setEnabled(false);
                owedTV.setEnabled(true);
                monthFilter = false;

                monthYearSpinner.setSelection(0);
                DebtMonthlyFilterList();
            }
        });
    }

    public void monthFilterSelected() {

        monthYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item (e.g., the selected month)
                selectedMonth = (String) parentView.getItemAtPosition(position);

                if (monthFilter) {
                    if (Objects.equals(selectedMonth, "All")) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        assert mainActivity != null;
                        boolean owedListIsEmpty = mainActivity.isOwedListEmpty();

                        if (owedListIsEmpty){
                            noOwedTextView.setVisibility(View.VISIBLE);
                            noDebtTextView.setVisibility(View.GONE);
                        }
                        else {
                            noOwedTextView.setVisibility(View.GONE);
                            noDebtTextView.setVisibility(View.GONE);
                        }

                        mainActivity.getOwedList();

                    } else {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        assert mainActivity != null;
                        mainActivity.getOwedListMonthly(selectedMonth);
                    }
                } else {

                    if (Objects.equals(selectedMonth, "All")) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        assert mainActivity != null;
                        boolean debtListIsEmpty = mainActivity.isDebtListEmpty();

                        if (debtListIsEmpty){
                            noDebtTextView.setVisibility(View.VISIBLE);
                            noOwedTextView.setVisibility(View.GONE);
                        }
                        else {
                            noDebtTextView.setVisibility(View.GONE);
                            noOwedTextView.setVisibility(View.GONE);
                        }

                        mainActivity.getDebtList();

                    } else {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        assert mainActivity != null;
                        mainActivity.getDebtListMonthly(selectedMonth);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected (if needed)
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.getDebtList();

    }

    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}