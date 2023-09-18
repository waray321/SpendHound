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

    private BottomNavigationView navView;
    private TextView day7TextView;
    private TextView day6TextView;
    private TextView day5TextView;
    private TextView day4TextView;
    private TextView day3TextView;
    private TextView day2TextView;
    private TextView day1TextView;
    public FirebaseAuth mAuth;
    public int totalMonthSpends;
    private ProgressBar progressBar;
    public int dailySpend;
    private ArrayList<RecentTransaction> recentTransactionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressBar = findViewById(R.id.progressBar);

        mAuth = DeclareDatabase.getAuth();

        day7TextView = findViewById(R.id.day7);
        day6TextView = findViewById(R.id.day6);
        day5TextView = findViewById(R.id.day5);
        day4TextView = findViewById(R.id.day4);
        day3TextView = findViewById(R.id.day3);
        day2TextView = findViewById(R.id.day2);
        day1TextView = findViewById(R.id.day1);

        // Update the text of each TextView
        setTextViews();

        getTotalMonthSpends();

        getEverydaySpends();

        getRecentTransaction();

        navView = findViewById(R.id.navView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);

        FloatingActionButton fab_addTransaction = findViewById(R.id.fab_addTransaction);

        fab_addTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to NewActivity
                Intent intent = new Intent(MainActivity.this, AddTranscationActivity.class);
                startActivity(intent);
            }
        });

        CardView cardViewProfile = findViewById(R.id.cardView_profile);

        cardViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, cardViewProfile, Gravity.END, androidx.transition.R.attr.popupMenuStyle, 0);
                popupMenu.inflate(R.menu.dropdown_menu);

                // Set a click listener for menu items
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_logout) {
                            // Handle logout action
                            Toast.makeText(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            mAuth.signOut();
                            return true;
                        } else {
                            // Handle other menu item clicks
                            return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });
    }

    public void setTextViews() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -6); // Start with 6 days ago

        // Set the text for each TextView
        day7TextView.setText(getFormattedDay(calendar));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        day6TextView.setText(getFormattedDay(calendar));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        day5TextView.setText(getFormattedDay(calendar));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        day4TextView.setText(getFormattedDay(calendar));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        day3TextView.setText(getFormattedDay(calendar));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        day2TextView.setText(getFormattedDay(calendar));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        day1TextView.setText(getFormattedDay(calendar));
    }

    private String getFormattedDay(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void getTotalMonthSpends() {
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
    private void getEverydaySpends() {
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

    private void setViewHeightForDay(int day, int dailySpends) {
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
        // Initialize an array to store daily spends for each day of the week
        int[] mostRecent = new int[5];

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());

        String currentMonthYear = dateFormat.format(calendar.getTime());
        String currentDay = dayFormat.format(calendar.getTime());

        // Loop through the last 5 days
        for (int i = 0; i < 3; i++) {
            // Create a reference to the "transactions" node
            DatabaseReference databaseReference = DeclareDatabase.getDBRefTransaction();
            // Create a child with the format "YYYY-MM" (year-month)
            DatabaseReference monthYearRef = databaseReference.child(currentMonthYear);
            // Create a child with the current day
            DatabaseReference dayRef = monthYearRef.child(currentDay);

            // Add a listener to retrieve data for the current date
            final int days = i; // Store the day index for use inside the listener
            String finalCurrentDay = currentDay;
            dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot timeSnapshot : dataSnapshot.getChildren()) {
                        Transaction transaction = timeSnapshot.getValue(Transaction.class);
                        if (transaction != null) {
                            String mostRecentDate = currentMonthYear + " - " + finalCurrentDay;
                            String mostRecentTransactionType = transaction.getTransactionType();
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
    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
