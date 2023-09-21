package com.waray.spendhound;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.waray.spendhound.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView navView;
    public FirebaseAuth mAuth;
    public int totalMonthSpends;
    private ProgressBar progressBar;
    public int dailySpend;
    private ArrayList<RecentTransaction> recentTransactionList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        mAuth = DeclareDatabase.getAuth();

        navView = findViewById(R.id.navView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_borrow, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);

        progressBar.setVisibility(View.GONE);
    }

    public void getTotalMonthSpends() {
        // Create a reference to the "transactions" node
        DatabaseReference databaseReference = DeclareDatabase.getDBRefTransaction();

        // Get the current month in the format "MMMM-yyyy" (e.g., "September-2023")
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault());
        String currentMonthYear = dateFormat.format(calendar.getTime());

        // Create a child with the format "YYYY-MM" (year-month)
        DatabaseReference monthYearRef = databaseReference.child(currentMonthYear);

        totalMonthSpends = 0; // Initialize the totalMonthSpends

        // Add a listener to retrieve data for the entire month
        monthYearRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot daySnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot timeSnapshot : daySnapshot.getChildren()) {
                        Transaction transaction = timeSnapshot.getValue(Transaction.class);
                        if (transaction != null) {
                            // Retrieve the paymentAmount from the transaction
                            int paymentAmount = transaction.getPaymentAmount();

                            // Add the paymentAmount to totalMonthSpends
                            totalMonthSpends += paymentAmount;
                        }
                    }
                }

                // Now, totalMonthSpends contains the sum of all paymentAmounts in the current month
                // You can use it as needed, for example, update a TextView with this value
                String totalMonthSpendsString = String.valueOf(totalMonthSpends);
                TextView totalMonthSpendsTextView = findViewById(R.id.totalMonthSpends);
                totalMonthSpendsTextView.setText("₱ " + totalMonthSpendsString + ".00");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database read error
                String errorMessage = "Database read error occurred: " + databaseError.getMessage();
                Log.e("FirebaseDatabase", errorMessage);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    public void getEverydaySpends() {
        // Initialize an array to store daily spends for each day of the week
        int[] dailySpends = new int[7];

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());

        String currentMonthYear = dateFormat.format(calendar.getTime());
        String currentDay = dayFormat.format(calendar.getTime());

        // Loop through the last 7 days
        for (int i = 0; i < 7; i++) {
            // Create a reference to the "transactions" node
            DatabaseReference databaseReference = DeclareDatabase.getDBRefTransaction();
            // Create a child with the format "YYYY-MM" (year-month)
            DatabaseReference monthYearRef = databaseReference.child(currentMonthYear);
            // Create a child with the current day
            DatabaseReference dayRef = monthYearRef.child(currentDay);

            // Initialize daily spend for the current day
            dailySpend = 0;

            // Add a listener to retrieve data for the current date
            final int days = i; // Store the day index for use inside the listener
            dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot timeSnapshot : dataSnapshot.getChildren()) {
                        Transaction transaction = timeSnapshot.getValue(Transaction.class);
                        if (transaction != null) {
                            // Retrieve the paymentAmount from the transaction
                            int paymentAmount = transaction.getPaymentAmount();

                            // Add the paymentAmount to dailySpend
                            dailySpend += paymentAmount;

                        }
                    }

                    // Store the daily spend in the array
                    dailySpends[days] = dailySpend;
                    dailySpend = 0;
                    setViewHeightForDay(days, dailySpends[days]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database read error
                    String errorMessage = "Database read error occurred: " + databaseError.getMessage();
                    Log.e("FirebaseDatabase", errorMessage);
                }
            });
            // Convert currentDay to an integer
            int currentDayInt = Integer.parseInt(currentDay);
            currentDayInt--;
            currentDay = String.format("%02d", currentDayInt);
        }
    }

    public void setViewHeightForDay(int day, int dailySpends) {
        int[] dailySpendsArray = new int[7];
        dailySpendsArray[day] = dailySpends;
        String dailySpendString = String.valueOf(dailySpendsArray[day]);
        TextView day1SpendTextView = findViewById(R.id.totalday1);
        TextView day2SpendTextView = findViewById(R.id.totalday2);
        TextView day3SpendTextView = findViewById(R.id.totalday3);
        TextView day4SpendTextView = findViewById(R.id.totalday4);
        TextView day5SpendTextView = findViewById(R.id.totalday5);
        TextView day6SpendTextView = findViewById(R.id.totalday6);
        TextView day7SpendTextView = findViewById(R.id.totalday7);
        int viewId = 0;

        switch (day) {
            case 0:
                day1SpendTextView.setText(dailySpendString);
                viewId = R.id.day1_bar;
                break;
            case 1:
                day2SpendTextView.setText(dailySpendString);
                viewId = R.id.day2_bar;
                break;
            case 2:
                day3SpendTextView.setText(dailySpendString);
                viewId = R.id.day3_bar;
                break;
            case 3:
                day4SpendTextView.setText(dailySpendString);
                viewId = R.id.day4_bar;
                break;
            case 4:
                day5SpendTextView.setText(dailySpendString);
                viewId = R.id.day5_bar;
                break;
            case 5:
                day6SpendTextView.setText(dailySpendString);
                viewId = R.id.day6_bar;
                break;
            case 6:
                day7SpendTextView.setText(dailySpendString);
                viewId = R.id.day7_bar;
                break;
        }
        // Calculate the desired height based on the daily spend
        int desiredHeightInPixels;
        if (dailySpends >= 1000) {
            desiredHeightInPixels = 250;
        } else if (dailySpends <= 100) {
            desiredHeightInPixels = 25;
        } else {
            desiredHeightInPixels = dailySpends / 4;
        }

        if (viewId != 0) {
            View view = findViewById(viewId);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = desiredHeightInPixels;
            view.setLayoutParams(layoutParams);
        }else {
            showToast("No viewID");
        }
    }

    public void getRecentTransaction() {
        // Clear the existing transaction data
        recentTransactionList.clear();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());

        String currentMonthYear = dateFormat.format(calendar.getTime());
        String currentDay = dayFormat.format(calendar.getTime());


        // Loop through the last 3 days
        for (int i = 0; i < 3; i++) {
            // Create a reference to the "transactions" node
            DatabaseReference databaseReference = DeclareDatabase.getDBRefTransaction();
            // Create a child with the format "YYYY-MM" (year-month)
            DatabaseReference monthYearRef = databaseReference.child(currentMonthYear);
            // Create a child with the current day
            DatabaseReference dayRef = monthYearRef.child(currentDay);

            String finalCurrentDay = currentDay;
            dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot timeSnapshot : dataSnapshot.getChildren()) {
                        Transaction transaction = timeSnapshot.getValue(Transaction.class);
                        if (transaction != null) {
                            // Split the string by hyphen and keep only the first part (the month)
                            String[] parts = currentMonthYear.split("-");
                            String finalCurrentMonth = parts[0];
                            String mostRecentDate = finalCurrentMonth + " - " + finalCurrentDay;
                            String mostRecentTransactionType = transaction.getTransactionType();
                            String mostRecentDetails = transaction.getMultilineStr();
                            int mostRecentPaymentAmount = transaction.getPaymentAmount();
                            String mostRecentPaymentAmountStr = "₱ " + mostRecentPaymentAmount;
                            int iconResource;

                            if ("Electricity".equals(mostRecentTransactionType)) {
                                iconResource = R.drawable.lightning_bolt;
                            } else if ("Water Bill".equals(mostRecentTransactionType)) {
                                iconResource = R.drawable.faucet;
                            } else if ("Mineral Water".equals(mostRecentTransactionType)) {
                                iconResource = R.drawable.water;
                            } else if ("Groceries".equals(mostRecentTransactionType)) {
                                iconResource = R.drawable.groceries;
                            } else if ("Foods".equals(mostRecentTransactionType)) {
                                iconResource = R.drawable.hamburger;
                            } else if ("House Necessity".equals(mostRecentTransactionType)) {
                                iconResource = R.drawable.sofa;
                            } else if ("Transportation".equals(mostRecentTransactionType)) {
                                iconResource = R.drawable.vehicles;
                            } else {
                                iconResource = R.drawable.house;
                            }
                            // Create a RecentTransaction object and add it to the list
                            RecentTransaction recentTrans = new RecentTransaction(
                                    mostRecentDate,
                                    mostRecentTransactionType,
                                    mostRecentDetails,
                                    mostRecentPaymentAmountStr,
                                    iconResource
                            );
                            recentTransactionList.add(recentTrans);
                        }
                        // Now you have populated recentTransactionList with data
                        // You can pass this list to your RecyclerView adapter here or in onCreate
                        RecyclerView recyclerView = findViewById(R.id.transactionListRecycler);
                        RecyclerView.Adapter<RecentTransactionAdapter.ViewHolder> adapter = new RecentTransactionAdapter(recentTransactionList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        // Set the RecyclerView.LayoutManager
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                        recyclerView.setLayoutManager(layoutManager);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database read error
                    String errorMessage = "Database read error occurred: " + databaseError.getMessage();
                    Log.e("FirebaseDatabase", errorMessage);
                }
            });
            // Convert currentDay to an integer
            int currentDayInt = Integer.parseInt(currentDay);
            currentDayInt--;
            currentDay = String.format("%02d", currentDayInt);
        }
    }

    // Utility method to show a toast message
    public void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
