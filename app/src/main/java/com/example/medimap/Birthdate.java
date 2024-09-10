package com.example.medimap;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class Birthdate extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 11;
    private CalendarView calendarView;
    private long selectedDate = -1;
    private static final String PREFS_NAME = "UserSignUpData";
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthdate);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupViews();
    }

    private void setupViews() {
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();

        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(this::onDateChange);

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this::onNextClicked);

        Button goToButton = findViewById(R.id.go_to);
        goToButton.setOnClickListener(this::showGoToDateDialog);
    }

    private void onDateChange(CalendarView view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        selectedDate = calendar.getTimeInMillis();
        view.setDate(selectedDate, true, true); // To highlight the selected date
    }

    private void onNextClicked(View v) {
        if (selectedDate != -1) {
            saveBirthdate();
            Intent intent = new Intent(this, Gender.class);
            intent.putExtra("currentPage", currentPage + 1);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please select your birthdate", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;
        circularProgressBar.setProgress(progress);
    }

    private void saveBirthdate() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("birthdate", selectedDate);
        editor.apply();
        Toast.makeText(this, "Birthdate saved: " + getFormattedDate(selectedDate), Toast.LENGTH_SHORT).show();
    }

    private String getFormattedDate(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
    }

    private void showGoToDateDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_calendar_date, null);
        builder.setView(dialogView);

        Spinner yearSpinner = dialogView.findViewById(R.id.yearSpinner);
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getYearList());
        yearSpinner.setAdapter(yearAdapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        setupMonthListeners(dialogView, yearSpinner, dialog); // Setup month listeners

        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> dialog.dismiss());
    }

    private void setupMonthListeners(View dialogView, Spinner yearSpinner, AlertDialog dialog) {
        int[] monthCardIds = {
                R.id.Jan, R.id.Feb, R.id.Mar, R.id.Apr, R.id.May, R.id.Jun,
                R.id.Jul, R.id.Aug, R.id.Sep, R.id.Oct, R.id.Nov, R.id.Dec
        };

        for (int i = 0; i < monthCardIds.length; i++) {
            final int month = i;
            View monthCard = dialogView.findViewById(monthCardIds[i]);
            monthCard.setOnClickListener(view -> {
                int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                updateSelectedDate(year, month);
            });
        }

        Button saveButton = dialogView.findViewById(R.id.Save);
        saveButton.setOnClickListener(view -> {
            applySelectedDate();
            dialog.dismiss();
        });
    }

    private void updateSelectedDate(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        selectedDate = calendar.getTimeInMillis();
    }

    private void applySelectedDate() {
        calendarView.setDate(selectedDate, true, true);
    }

    private Integer[] getYearList() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[100];
        for (int i = 0; i < 100; i++) {
            years[i] = currentYear - i;
        }
        return years;
    }
}
