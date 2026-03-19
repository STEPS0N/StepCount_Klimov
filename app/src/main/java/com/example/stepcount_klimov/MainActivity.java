package com.example.stepcount_klimov;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public boolean active = true;
    private SensorManager sensorManager;
    private int count = 0;
    private double calories = 0.00;
    private TextView text;
    private TextView caloriesText;
    private long lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        caloriesText = findViewById(R.id.textView4);
        caloriesText.setText(Double.valueOf(calories) + " ккал");
        text = findViewById(R.id.textView2);
        text.setText(String.valueOf(count));
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorManager.registerListener((SensorEventListener) this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        lastUpdate = System.currentTimeMillis();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener((SensorEventListener) this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener((SensorEventListener) this);
    }

    public void onStoped(View view){
        active = !active;
        if (!active){
            Button button = findViewById(R.id.button);
            button.setText("ВОЗОБНОВИТЬ");
            onPause();
        } else {
            Button button = findViewById(R.id.button);
            button.setText("ПАУЗА");
            onResume();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            float accelationSquareRoot = (x * x + y * y + z * z)
                    / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

            long actualTime = System.currentTimeMillis();

            if (accelationSquareRoot >= 2){
                if (actualTime - lastUpdate < 200){
                    return;
                }
                lastUpdate = actualTime;

                count ++;
                text.setText(String.valueOf(count));
                calculateCallory();
            }
        }
    }

    private void calculateCallory(){
        calories = (count * 0.04);
        caloriesText.setText(String.valueOf(calories) + " ккал");
    }

    public void onClear(View view){
        count = 0;
        calories = 0.00;
        text.setText(String.valueOf(count));
        caloriesText.setText(Double.valueOf(calories) + " ккал");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}