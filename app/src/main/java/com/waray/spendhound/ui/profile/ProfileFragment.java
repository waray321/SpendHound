package com.waray.spendhound.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.waray.spendhound.AddTranscationActivity;
import com.waray.spendhound.DeclareDatabase;
import com.waray.spendhound.MainActivity;
import com.waray.spendhound.R;
import com.waray.spendhound.RecentTransaction;
import com.waray.spendhound.RecentTransactionAdapter;
import com.waray.spendhound.SpinnerItem;
import com.waray.spendhound.SpinnerItemMonths;
import com.waray.spendhound.Transaction;
import com.waray.spendhound.ui.home.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private TextView nicknameTextView;
    private EditText nicknameEditText;
    private ImageView editNickname;
    private ImageView saveNickname;
    private Spinner monthSpinner;

    private String currentNickname = "";

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

        loadNickname();
        EditNickname();
        SaveNickname();
        MonthlyFilter();

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

        nicknameTextView.setText(currentNickname);
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

    private void MonthlyFilter(){
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
                List<String> sortedMonths = new ArrayList<>(uniqueMonths);
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
}