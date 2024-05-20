package com.waray.spendhound;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PendingStatusActivity extends AppCompatActivity {
    private TextView borrowerListTV, payerListTV;
    private ScrollView borrowerListScrollView, payerListScrollView;
    private boolean borrowerPayerClicked;
    private ImageView backBtn;
    private LinearLayout borrowerListLinearLayout, payerListLinearLayout;
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
}
