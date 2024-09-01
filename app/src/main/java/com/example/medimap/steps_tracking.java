package com.example.medimap;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class steps_tracking extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager=null;
    private Sensor stepSensor;
    private int totalSteps=0;
    private int previousTotalSteps=0;
    private ProgressBar progressBar;
    private TextView steps;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_steps_tracking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressBar=findViewById(R.id.progressBar);
        steps=findViewById(R.id.steps);
        mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        stepSensor=mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        loadData();
        resetSteps();
        //test
    }


    protected void onResume() {
        super.onResume();
        if(stepSensor==null){
            Toast.makeText(this,"No sensor In this Device",Toast.LENGTH_SHORT).show();
        }
        else {
            mSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_STEP_COUNTER) {
            totalSteps = (int) event.values[0];
            int currentSteps = totalSteps - previousTotalSteps;
            progressBar.setProgress(currentSteps/totalSteps);
            steps.setText(String.valueOf(currentSteps));
        }

    }
    private void resetSteps() {
        steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(steps_tracking.this, "Long tap to reset steps", Toast.LENGTH_SHORT).show();

            }
        });



        steps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                previousTotalSteps = totalSteps;
                steps.setText("0");
                progressBar.setProgress(0);
                saveData();
                return true;
            }
        });
    }
    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("key1", String.valueOf(previousTotalSteps));
        editor.apply();
    }
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        int savedNumber = (int)sharedPreferences.getFloat("key1", 0f);
        previousTotalSteps = savedNumber;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}