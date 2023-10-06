package com.waray.spendhound.ui.borrow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.waray.spendhound.AddTranscationActivity;
import com.waray.spendhound.BorrowNowActivity;
import com.waray.spendhound.BorrowNowActivity;
import com.waray.spendhound.DeclareDatabase;
import com.waray.spendhound.R;
import com.waray.spendhound.SpinnerItemMonths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BorrowFragment extends Fragment {

    private Spinner monthSpinner;
    public List<String> sortedMonths;
    private Button borrowNowBtn, payNowBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrow, container, false);
        monthSpinner = view.findViewById(R.id.monthSpinner);
        borrowNowBtn = view.findViewById(R.id.borrowNowBtn);
        payNowBtn = view.findViewById(R.id.payNowBtn);

        MonthlyFilter();
        BorrowNow();

        // Get the hosting Activity and remove the ActionBar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().hide();
        }
        return view;
    }

    public void MonthlyFilter(){
        DatabaseReference transRef = DeclareDatabase.getDBRefTransaction();
        // Initialize an empty set to store unique months
        Set<String> uniqueMonths = new HashSet<>();
        transRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    // Assuming the month is the key (e.g., "September-2023")
                    String monthYear = monthSnapshot.getKey();
                    String[] parts = monthYear.split("-");
                    String finalCurrentMonth = parts[0];
                    uniqueMonths.add(finalCurrentMonth);
                }

                // Convert the Set to a List for sorting (if needed)
                sortedMonths = new ArrayList<>(uniqueMonths);
                Collections.sort(sortedMonths);

                monthSpinner.setBackgroundResource(R.drawable.transparent_background);

                SpinnerItemMonths adapter = new SpinnerItemMonths(getActivity(), sortedMonths);
                monthSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database read error
                String errorMessage = "Database read error occurred: " + databaseError.getMessage();
                Log.e("FirebaseDatabase", errorMessage);
            }
        });
    }

    private void BorrowNow(){
        borrowNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to NewActivity
                Intent intent = new Intent(getActivity(), BorrowNowActivity.class);
                startActivity(intent);
            }
        });
    }
}