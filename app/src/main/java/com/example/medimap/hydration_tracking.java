package com.example.medimap;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class hydration_tracking extends AppCompatActivity {
    Button editAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hydration_tracking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editAmount = findViewById(R.id.editButton);

        editAmount.setOnClickListener(v -> showEditAmountDialog());
    }

    private void showEditAmountDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            builder.setTitle("Enter Amount");

            // Set up the layout for the dialog
            View dialogView = getLayoutInflater().inflate(R.layout.edit_amount_dialog, null);
            builder.setView(dialogView);

            // Find the EditText and Spinner in the dialog layout
            final EditText input = dialogView.findViewById(R.id.amountInput);

            // Find the buttons in the dialog layout
            Button addButton = dialogView.findViewById(R.id.Save);
            Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle button clicks
        addButton.setOnClickListener(v -> {
            String amount = input.getText().toString();
            if (!amount.isEmpty()) {
                //updateDefaultAmount(locationName, difficulty);
                dialog.dismiss();
            }
            else{
                Toast.makeText(this, "please enter amount", Toast.LENGTH_SHORT).show();
            }
        });
        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }
}