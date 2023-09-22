package com.waray.spendhound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddTranscationActivity extends AppCompatActivity {

    private LinearLayout container;
    private Button btnAdd;
    private Button addTransactionbtn;
    private Spinner payorSpinner;
    private Spinner transactionTypeSpinner;
    private String transactionType;
    public String paymentAmountStr;
    public String multilineStr;
    private ProgressBar progressBar;
    public List<String> usernames;
    public FirebaseAuth mAuth;
    private List<View> rows;
    public List<String> payorsList;
    public List<Integer> amountsPaidList;
    public Integer totalAmaountPaid = 0;
    public Integer paymentAmount;
    private EditText paymentAmountEditText;
    private EditText editTextTextMultiLine;
    private TextView individualPayment;
    public String usernamePost;
    private ArrayList<RecentTransaction> recentTransactionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transcation);

        // Get the Firebase Authentication instance
        mAuth = DeclareDatabase.getAuth();

        // Check the user's authentication state
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User is not authenticated, you can redirect them to the login activity
            Intent intent = new Intent(AddTranscationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finish this activity to prevent returning to it when pressing back
            return;
        }

        transactionTypeSpinner = findViewById(R.id.transactionType);
        // Create an ArrayAdapter using the string array and a default spinner layout
        String[] transactionTypes = getResources().getStringArray(R.array.transactionTypes_String);
        SpinnerItem adapter = new SpinnerItem(this, Arrays.asList(transactionTypes));
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        transactionTypeSpinner.setAdapter(adapter);

        container = findViewById(R.id.container);
        btnAdd = findViewById(R.id.btnAdd);

        rows = new ArrayList<>();

        addRow();

        progressBar = findViewById(R.id.progressBar);

        btnAdd.setOnClickListener(v -> {
            // Check if the number of rows added is less than the number of users
            if (rows.size() < usernames.size() - 1) {
                addRow();
            } else {
                // Display a message or handle the case where you can't add more rows
                Toast.makeText(AddTranscationActivity.this, "You can't add more payor.", Toast.LENGTH_SHORT).show();
            }
        });


        addTransactionbtn = findViewById(R.id.addTransactionbtn);

        addTransactionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTransaction();
            }
        });

        //Calculate Individual Payment
        paymentAmountEditText = findViewById(R.id.paymentAmount);
        individualPayment = findViewById(R.id.individualPayment);
        CalculateIndividualPayment();

        detailsCharacterCount();
        exitEditText();

    }

    private void addRow() {
        // Get a reference to the Firebase Realtime Database
        DatabaseReference databaseReference = DeclareDatabase.getDatabaseReference();

        // Retrieve usernames from the database and populate the spinner
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usernames = new ArrayList<>();
                usernames.add("Select a payor:"); // Add the default value
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    if (username != null) {
                        usernames.add(username);
                    }
                }
                SpinnerItem adapter = new SpinnerItem(AddTranscationActivity.this, usernames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                payorSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database read error
                String errorMessage = "Database read error occurred: " + databaseError.getMessage();
                Log.e("FirebaseDatabase", errorMessage);

                // You can also display a Toast message to inform the user
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        LayoutInflater inflater = LayoutInflater.from(this);
        View row = inflater.inflate(R.layout.row_layout, container, false);

        payorSpinner = row.findViewById(R.id.payor);

        Button btnMinus = row.findViewById(R.id.closeBtn);
        btnMinus.setOnClickListener(v -> removeRow(row));


        Drawable roundedDrawable = getResources().getDrawable(R.drawable.rounded_alternating_row);
        ViewCompat.setBackground(row, roundedDrawable);



        rows.add(row);
        container.addView(row);
    }


    private void removeRow(View row) {
        container.removeView(row);
        rows.remove(row);
    }

    private void addTransaction() {
        progressBar.setVisibility(View.VISIBLE);
        // Get values from UI components
        transactionType = transactionTypeSpinner.getSelectedItem().toString();
        paymentAmountEditText = findViewById(R.id.paymentAmount);
        editTextTextMultiLine = findViewById(R.id.editTextTextMultiLine);

        // Get the text from the EditText
        paymentAmountStr = paymentAmountEditText.getText().toString();
        multilineStr = editTextTextMultiLine.getText().toString();
        if (paymentAmountStr.equals("")){
            paymentAmount = 0;
        }else {
            paymentAmount = Integer.parseInt(paymentAmountStr);
        }
        // Create a list to store payors and amounts
        payorsList = new ArrayList<>();
        amountsPaidList = new ArrayList<>();

        // Check for null or empty values
        if ("Select a transaction:".equals(transactionType) || paymentAmount == 0) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }else {
            // Create a HashSet to store payors
            HashSet<String> uniquePayors = new HashSet<>();

            // Iterate through each row to collect payors and amounts paid
            for (View row : rows) {
                Spinner payorSpinner = row.findViewById(R.id.payor);
                EditText amountPaidEditText = row.findViewById(R.id.amountPaid);

                String payor = payorSpinner.getSelectedItem().toString();
                String amountPaidStr = amountPaidEditText.getText().toString().trim();

                // Check for null or empty values
                if ("Select a payor:".equals(payor) || amountPaidStr.equals("")) {
                    Toast.makeText(AddTranscationActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return; // Exit the method if "Select a Payor" is selected
                } else if (uniquePayors.contains(payor)) {
                    Toast.makeText(AddTranscationActivity.this, "Duplicate payor detected: " + payor, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return; // Exit the method if a duplicate payor is detected
                }

                uniquePayors.add(payor); // Add the payor to the HashSet to check for duplicates

                try {
                    int amountPaid = Integer.parseInt(amountPaidStr);
                    payorsList.add(payor);
                    amountsPaidList.add(amountPaid);
                    totalAmaountPaid = totalAmaountPaid + amountPaid;
                } catch (NumberFormatException e) {
                    Toast.makeText(AddTranscationActivity.this, "Invalid amount format for payor: " + payor, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return; // Exit the method if the amount cannot be parsed as an integer
                }
            }
            if (!paymentAmount.equals(totalAmaountPaid)){
                Toast.makeText(AddTranscationActivity.this, "Please input payment", Toast.LENGTH_SHORT).show();
                totalAmaountPaid = 0;
                progressBar.setVisibility(View.GONE);
                return;
            }else{
                totalAmaountPaid = 0;
            }
            // Get the current user's unique ID (UID)
            String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Create a reference to the "users" node in the database
            DatabaseReference usersRef = DeclareDatabase.getDatabaseReference().child(currentUserID);

            // Read the username from the database
            usersRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Get the username from the dataSnapshot and assign it to usernamePost
                        usernamePost = dataSnapshot.getValue(String.class);

                        try {
                            // Get the current date and time
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault());
                            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                            String currentMonthYear = dateFormat.format(calendar.getTime());
                            String currentDay = dayFormat.format(calendar.getTime());
                            String currentTime = timeFormat.format(calendar.getTime());

                            // Create a reference to the "transactions" node
                            DatabaseReference databaseReference = DeclareDatabase.getDBRefTransaction();
                            // Create a child with the format "YYYY-MM" (year-month)
                            DatabaseReference monthYearRef = databaseReference.child(currentMonthYear);
                            // Create a child with the current day
                            DatabaseReference dayRef = monthYearRef.child(currentDay);
                            // Create a child with the current timestamp
                            DatabaseReference timestampRef = dayRef.child(currentTime);


                            // Create a Transaction object with the data
                            Transaction transaction = new Transaction(transactionType, paymentAmount, multilineStr, payorsList, amountsPaidList, usernamePost);

                            // Set the value of the transaction in the database under the timestamp
                            timestampRef.setValue(transaction)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(AddTranscationActivity.this, "Transaction added successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(AddTranscationActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(AddTranscationActivity.this, "Failed to add transaction", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } catch (NumberFormatException e) {
                            // Handle invalid number format for paymentAmount or amountPaid
                            Toast.makeText(AddTranscationActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        // Handle the case where the username doesn't exist in the database
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
    }

    private void CalculateIndividualPayment(){
        // Add this code inside your onCreate method after initializing the views
        paymentAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is not used, but it's required by the TextWatcher interface
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is not used, but it's required by the TextWatcher interface
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Get the payment amount entered by the user as a String
                String paymentAmountStr = s.toString().trim();

                // Check if the payment amount is not empty
                if (!TextUtils.isEmpty(paymentAmountStr)) {
                    try {
                        // Convert the payment amount to an integer
                        int paymentAmount = Integer.parseInt(paymentAmountStr);

                        // Calculate the individual payment
                        int numberOfUsers = usernames.size()-1; // Replace 'usernames' with your list of usernames
                        if (numberOfUsers > 0) {
                            int totalindividualPayment = paymentAmount / numberOfUsers;
                            individualPayment.setText("₱ " + totalindividualPayment + ".00");
                        } else {
                            // Handle the case where there are no users
                            individualPayment.setText("No Users");
                        }
                    } catch (NumberFormatException e) {
                        // Handle invalid number format for paymentAmount
                        individualPayment.setText("Invalid Amount");
                    }
                } else {
                    // Clear the individual payment TextView if the payment amount is empty
                    individualPayment.setText("₱ 00.00");
                }
            }
        });

    }

    public void detailsCharacterCount(){
        EditText editText = findViewById(R.id.editTextTextMultiLine);
        final TextView characterCount = findViewById(R.id.characterCount);
        final int maxLength = 50; // Maximum character limit

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Calculate the current character count
                int currentCount = s.length();

                // Update the character count TextView
                characterCount.setText(currentCount + "/" + maxLength);

                // Check if the current character count exceeds the limit
                if (currentCount > maxLength) {
                    // Truncate the input text to the maximum length
                    String truncatedText = s.subSequence(0, maxLength).toString();
                    editText.setText(truncatedText);
                    editText.setSelection(maxLength); // Move cursor to the end
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void exitEditText(){
        final EditText editText = findViewById(R.id.editTextTextMultiLine);
        final EditText editTextPaymentAmount = findViewById(R.id.paymentAmount);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Consume the touch event on the EditText to prevent it from being intercepted
                v.performClick();
                return false;
            }
        });

        editTextPaymentAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Consume the touch event on the EditText to prevent it from being intercepted
                v.performClick();
                return false;
            }
        });

        // Add an OnTouchListener to the root layout (or any other layout that covers the whole screen)
        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Hide the keyboard when the user touches outside the EditText
                hideKeyboard(editText);
                hideKeyboardPaymentAmount(editTextPaymentAmount);
                return false;
            }
        });
    }

    // Helper method to hide the keyboard
    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
    private void hideKeyboardPaymentAmount(EditText editTextPaymentAmount) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextPaymentAmount.getWindowToken(), 0);
    }
}