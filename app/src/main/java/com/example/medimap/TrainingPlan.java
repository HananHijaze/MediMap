package com.example.medimap;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TrainingPlan extends AppCompatActivity {
    Button go_to;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_training_plan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        go_to = findViewById(R.id.go_to);
        go_to.setOnClickListener(v -> showCalendarDialog());
    }

    private void showCalendarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("Calender Date");

        // Set up the layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_calendar_date, null);
        builder.setView(dialogView);

        // Find the EditText and Spinner in the dialog layout
        //final EditText month = dialogView.findViewById(R.id.cardview);

        // Find the buttons in the dialog layout
        Button saveButton = dialogView.findViewById(R.id.Save);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle button clicks
        saveButton.setOnClickListener(v -> {
            //String month = input.getText().toString();
            //String year = input.getText().toString();
//            if (!month.isEmpty()) {
//                //updateCalendarDate(locationName, difficulty);
//                dialog.dismiss();
//            }
//            else{
//                Toast.makeText(this, "please select a month", Toast.LENGTH_SHORT).show();
//            }
        });
        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }
}