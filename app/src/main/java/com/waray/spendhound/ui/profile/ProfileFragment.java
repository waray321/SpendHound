package com.waray.spendhound.ui.profile;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.waray.spendhound.DeclareDatabase;
import com.waray.spendhound.R;
import com.waray.spendhound.SpinnerItemMonths;
import com.waray.spendhound.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private TextView nicknameTextView, totalBalancedTextView, balanceTextView, unpaidTextView, oweTextView, debtTextView;
    private EditText nicknameEditText;
    private ImageView editNickname;
    private ImageView saveNickname;
    private Spinner monthSpinner;
    public FirebaseAuth mAuth;
    public List<String> sortedMonths;
    private String currentNickname = "";
    public String monthYear;
    private int totalIndividualPayment, totalPaymentList, balance, unpaid, owe, debt;
    private int i, e, o, currentBalance, currentUnpaid, currentOwe, currentDebt;
    private View balanceUnpaidLayout, oweDebtLayout;
    private Drawable balanceUnpaidDrawable, oweDebtDrawable, balanceUnpaidDrawableTransparent, oweDebtDrawableTransparent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = view.findViewById(R.id.profileImageView);
        setProfileImage(profileImageView);
        nicknameTextView = view.findViewById(R.id.nicknameTextView);
        nicknameEditText = view.findViewById(R.id.nicknameEditText);
        editNickname = view.findViewById(R.id.editNickname);
        saveNickname = view.findViewById(R.id.saveNickname);
        monthSpinner = view.findViewById(R.id.monthSpinner);
        totalBalancedTextView = view.findViewById(R.id.totalBalancedTextView);
        balanceTextView = view.findViewById(R.id.balanceTextView);
        unpaidTextView = view.findViewById(R.id.unpaidTextView);
        oweTextView = view.findViewById(R.id.oweTextView);
        debtTextView = view.findViewById(R.id.debtTextView);
        balanceUnpaidLayout = view.findViewById(R.id.balanceUnpaidLayout);
        oweDebtLayout = view.findViewById(R.id.oweDebtLayout);

        balanceUnpaidDrawable = ContextCompat.getDrawable(getContext(), R.drawable.round_border_glassy);
        balanceUnpaidDrawableTransparent = ContextCompat.getDrawable(getContext(), R.drawable.transparent_background);
        oweDebtDrawable = ContextCompat.getDrawable(getContext(), R.drawable.round_border_glassy);
        oweDebtDrawableTransparent = ContextCompat.getDrawable(getContext(), R.drawable.transparent_background);
        balanceUnpaidLayout.setForeground(balanceUnpaidDrawable);


        balanceTextView.setBackgroundResource(R.drawable.button_background_visible);
        balanceTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.yellow));

        loadNickname();
        EditNickname();
        SaveNickname();
        MonthlyFilter();
        TotalBalanceUnpaid();
        UnpaidButton();
        BalanceButton();
        OweButton();
        DebtButton();

        // Get the hosting Activity and remove the ActionBar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().hide();
        }



        return view;
    }

    public void setProfileImage(ImageView imageView) {
        // Check if the fragment is attached to an activity
        if (isAdded()) {
            String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images").child(userId);
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri downloadUri) {
                    // Set the retrieved image to the provided ImageView
                    Glide.with(requireContext()).load(downloadUri).into(imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle image retrieval failure
                }
            });
        } else {
            // Handle the case when the fragment is not attached to an activity
        }
    }
    private void EditNickname(){
        editNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to edit mode
                switchToEditMode();
            }
        });
    }

    private void SaveNickname(){
        saveNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the updated nickname to your data source
                saveNickname();

                // Switch back to display mode
                switchToDisplayMode();
            }
        });
    }

    private void loadNickname() {
        String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference usersRef = DeclareDatabase.getDatabaseReference().child(currentUserID);
        usersRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the username from the dataSnapshot and assign it to usernamePost
                    currentNickname = dataSnapshot.getValue(String.class);
                    Log.d("FirebaseDatabase", "Nickname loaded: " + currentNickname);

                    // Update the TextView with the loaded nickname
                    nicknameTextView.setText(currentNickname);
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

    private void switchToEditMode() {
        // Hide the TextView and show the EditText for editing
        nicknameTextView.setVisibility(View.GONE);
        nicknameEditText.setVisibility(View.VISIBLE);

        // Set the EditText text to the current nickname
        nicknameEditText.setText(currentNickname);

        // Show the Save button and hide the Edit button
        editNickname.setVisibility(View.GONE);
        saveNickname.setVisibility(View.VISIBLE);
    }

    private void switchToDisplayMode() {
        // Show the TextView and hide the EditText
        nicknameTextView.setVisibility(View.VISIBLE);
        nicknameEditText.setVisibility(View.GONE);

        // Update the TextView with the edited nickname
        currentNickname = nicknameEditText.getText().toString();
        nicknameTextView.setText(currentNickname);

        // Show the Edit button and hide the Save button
        editNickname.setVisibility(View.VISIBLE);
        saveNickname.setVisibility(View.GONE);
    }

    private void saveNickname() {
        // Save the updated nickname to your data source (e.g., Firebase database)
        // Implement your database update logic here
        String updatedNickname = nicknameEditText.getText().toString();
        currentNickname = updatedNickname;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = DeclareDatabase.getDatabaseReference().child(userId);
        userRef.child("username").setValue(updatedNickname);

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

    public void TotalBalanceUnpaid() {
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                DatabaseReference databaseReference = DeclareDatabase.getDBRefTransaction();
                String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());

                String selectedMonth = sortedMonths.get(position);
                monthYear = selectedMonth + "-" + currentYear;
                DatabaseReference monthYearRef = databaseReference.child(monthYear);

                i = 0;
                e = 1;
                o = 0;

                monthYearRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot daySnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot timeSnapshot : daySnapshot.getChildren()) {
                                Transaction transaction = timeSnapshot.getValue(Transaction.class);
                                if (transaction != null) {
                                    int individualPayment = transaction.getTotalIndividualPayment();
                                    totalIndividualPayment += individualPayment;
                                }
                                DataSnapshot payorsSnapshot = timeSnapshot.child("payorsList");
                                for (DataSnapshot payorSnapshot : payorsSnapshot.getChildren()) {
                                    String payorUsername = payorSnapshot.getValue(String.class);
                                    if (payorUsername != null && payorUsername.equals(currentNickname)) {
                                        i++;
                                        o = i;
                                    } else {
                                        i++;
                                    }
                                }
                                DataSnapshot amountsPaidListSnapshot = timeSnapshot.child("amountsPaidList");
                                for (DataSnapshot amountSnapshot : amountsPaidListSnapshot.getChildren()) {
                                    Integer paymentAmount = amountSnapshot.getValue(Integer.class);
                                    if (e == o) {
                                        totalPaymentList += paymentAmount;
                                        e = 100;
                                    } else{
                                        e++;
                                    }
                                }
                                i = 0;
                                e = 1;
                                o = 0;
                            }
                        }
                        if (totalPaymentList == totalIndividualPayment ) {
                            balance = 0;
                            unpaid = 0;
                        } else if (totalIndividualPayment > totalPaymentList){
                            unpaid = totalIndividualPayment - totalPaymentList;
                        } else if (totalIndividualPayment < totalPaymentList){
                            balance = totalPaymentList - totalIndividualPayment;
                        } else {
                            balance = 0;
                            unpaid = 0;
                        }

                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference userRef = DeclareDatabase.getDatabaseReference().child(userId);
                        userRef.child("balanced").setValue(balance);
                        userRef.child("unpaid").setValue(unpaid);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database read error
                        String errorMessage = "Database read error occurred: " + databaseError.getMessage();
                        Log.e("FirebaseDatabase", errorMessage);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void BalanceButton(){
        balanceTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                balanceUnpaidLayout.setForeground(balanceUnpaidDrawable);
                oweDebtLayout.setForeground(oweDebtDrawableTransparent);

                balanceTextView.setBackgroundResource(R.drawable.button_background_visible);
                balanceTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.yellow));
                unpaidTextView.setBackgroundResource(R.drawable.button_background_invisible);
                unpaidTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.whitest));
                oweTextView.setBackgroundResource(R.drawable.button_background_invisible);
                oweTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.whitest));
                debtTextView.setBackgroundResource(R.drawable.button_background_invisible);
                debtTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.whitest));

                getBalance();
                String currentBalanceStr = String.valueOf(currentBalance);
                totalBalancedTextView.setText("₱ " + currentBalanceStr + ".00");
            }
        });

    }

    private void UnpaidButton(){
        unpaidTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                balanceUnpaidLayout.setForeground(balanceUnpaidDrawable);
                oweDebtLayout.setForeground(oweDebtDrawableTransparent);

                balanceTextView.setBackgroundResource(R.drawable.button_background_invisible);
                balanceTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.whitest));
                unpaidTextView.setBackgroundResource(R.drawable.button_background_visible);
                unpaidTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.yellow));
                oweTextView.setBackgroundResource(R.drawable.button_background_invisible);
                oweTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.whitest));
                debtTextView.setBackgroundResource(R.drawable.button_background_invisible);
                debtTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.whitest));

                getUnpaid();
                String currentUnpaidStr = String.valueOf(currentUnpaid);
                totalBalancedTextView.setText("₱ " + currentUnpaidStr + ".00");
            }
        });

    }

    private void OweButton(){
        oweTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                oweDebtLayout.setForeground(oweDebtDrawable);
                balanceUnpaidLayout.setForeground(balanceUnpaidDrawableTransparent);

                balanceTextView.setBackgroundResource(R.drawable.button_background_invisible);
                balanceTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.whitest));
                unpaidTextView.setBackgroundResource(R.drawable.button_background_invisible);
                unpaidTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.whitest));
                oweTextView.setBackgroundResource(R.drawable.button_background_visible);
                oweTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.yellow));
                debtTextView.setBackgroundResource(R.drawable.button_background_invisible);
                debtTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.whitest));

                getOwe();
                String currentOweStr = String.valueOf(currentOwe);
                totalBalancedTextView.setText("₱ " + currentOweStr + ".00");
            }
        });

    }

    private void DebtButton(){
        debtTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                oweDebtLayout.setForeground(oweDebtDrawable);
                balanceUnpaidLayout.setForeground(balanceUnpaidDrawableTransparent);

                balanceTextView.setBackgroundResource(R.drawable.button_background_invisible);
                balanceTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.whitest));
                unpaidTextView.setBackgroundResource(R.drawable.button_background_invisible);
                unpaidTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.whitest));
                oweTextView.setBackgroundResource(R.drawable.button_background_invisible);
                oweTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.whitest));
                debtTextView.setBackgroundResource(R.drawable.button_background_visible);
                debtTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.yellow));

                getDebt();
                String currebtDebtStr = String.valueOf(currentDebt);
                totalBalancedTextView.setText("₱ " + currebtDebtStr + ".00");
            }
        });

    }

    private void getBalance() {
        String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference usersRef = DeclareDatabase.getDatabaseReference().child(currentUserID);
        usersRef.child("balanced").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentBalance = dataSnapshot.getValue(Integer.class);
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

    private void getUnpaid() {
        String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference usersRef = DeclareDatabase.getDatabaseReference().child(currentUserID);
        usersRef.child("unpaid").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUnpaid = dataSnapshot.getValue(Integer.class);
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

    private void getOwe() {
        String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference usersRef = DeclareDatabase.getDatabaseReference().child(currentUserID);
        usersRef.child("owed").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentOwe = dataSnapshot.getValue(Integer.class);
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

    private void getDebt() {
        String currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference usersRef = DeclareDatabase.getDatabaseReference().child(currentUserID);
        usersRef.child("debt").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentDebt = dataSnapshot.getValue(Integer.class);
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
}