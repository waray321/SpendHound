package com.waray.spendhound;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.waray.spendhound.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navView;
    private TextView yesterday3TextView;
    private TextView yesterday2TextView;
    private TextView yesterday1TextView;
    private TextView todayTextView;
    private TextView tomorrow1TextView;
    private TextView tomorrow2TextView;
    private TextView tomorrow3TextView;
    public FirebaseAuth mAuth;
    public int totalMonthSpends;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = DeclareDatabase.getAuth();

        yesterday3TextView = findViewById(R.id.yesterday3);
        yesterday2TextView = findViewById(R.id.yesterday2);
        yesterday1TextView = findViewById(R.id.yesterday1);
        todayTextView = findViewById(R.id.today);
        tomorrow1TextView = findViewById(R.id.tomorrow1);
        tomorrow2TextView = findViewById(R.id.tomorrow2);
        tomorrow3TextView = findViewById(R.id.tomorrow3);

        // Update the text of each TextView
        setTextViews();

        getTotalMonthSpends();

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
        calendar.add(Calendar.DAY_OF_YEAR, -3); // Start with 3 days ago

        // Set the text for each TextView
        yesterday3TextView.setText(getFormattedDay(calendar));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        yesterday2TextView.setText(getFormattedDay(calendar));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        yesterday1TextView.setText(getFormattedDay(calendar));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        todayTextView.setText(getFormattedDay(calendar));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        tomorrow1TextView.setText(getFormattedDay(calendar));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        tomorrow2TextView.setText(getFormattedDay(calendar));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        tomorrow3TextView.setText(getFormattedDay(calendar));
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


}
