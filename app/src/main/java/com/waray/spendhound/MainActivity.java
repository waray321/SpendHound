package com.waray.spendhound;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.waray.spendhound.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
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
    public int dailySpend;
    public int i;
    int desiredHeightInPixels;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                @SuppressLint("ResourceType") PopupMenu popupMenu = new PopupMenu(MainActivity.this, cardViewProfile, Gravity.END, androidx.transition.R.attr.popupMenuStyle, 0);
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

    private void getEverydaySpends() {
        // Create a reference to the "transactions" node
        DatabaseReference databaseReference = DeclareDatabase.getDBRefTransaction();

        // Get the current month in the format "MMMM-yyyy" (e.g., "September-2023")
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());

        // Initialize an array to store daily spends
        int[] last7DaysSpends = new int[7];

        // Loop through the last 7 days
        for (int i = 0; i < 7; i++) {
            String currentDate = dateFormat.format(calendar.getTime());

            // Create a child reference for the current date
            DatabaseReference dateRef = databaseReference.child(currentDate);

            // Capture the current value of i in a local variable
            int dayNum = i;

            // Add a listener to retrieve data for the current date
            dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    last7DaysSpends[dayNum] = dailySpend;
                    // Calculate the desired height based on the daily spend



                    // Debug: Log daily spend
                    Log.d("DailySpend", "Day " + dayNum + ": " + last7DaysSpends[dayNum]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database read error
                    String errorMessage = "Database read error occurred: " + databaseError.getMessage();
                    Log.e("FirebaseDatabase", errorMessage);
                }
            });

            // Move to the previous day
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        // Set the height of the corresponding view
        setViewHeightForDay(last7DaysSpends);
    }

    private void setViewHeightForDay(int[] last7DaysSpends) {
        // Replace "your_view_id" with the actual ID of the corresponding view
        int viewId = 0;

        /*switch (day) {
            case 0:
                viewId = R.id.day1_bar;
                break;
            case 1:
                viewId = R.id.day2_bar;
                break;
            case 2:
                viewId = R.id.day3_bar;
                break;
            case 3:
                viewId = R.id.day4_bar;
                break;
            case 4:
                viewId = R.id.day5_bar;
                break;
            case 5:
                viewId = R.id.day6_bar;
                break;
            case 6:
                viewId = R.id.day7_bar;
                break;
        }

        if (viewId != 0) {
            View view = findViewById(viewId);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = heightInPixels;
            view.setLayoutParams(layoutParams);
        }*/
    }



    // Utility method to show a toast message
    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
