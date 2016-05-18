package com.gospelware.compassview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gospelware.compassviewlib.CompassView;

public class MainActivity extends AppCompatActivity {

    private CompassView compassView;
    private int angle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compassView = (CompassView) findViewById(R.id.compass);
        compassView.startScan();

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (compassView.isScanning()) {
                    compassView.stopScan();
                } else {
                    compassView.startScan();
                }

            }
        });

        final Button btn_a=(Button)findViewById(R.id.btn_angle);
        btn_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angle+=5;
                compassView.setRotation(angle);
            }
        });
    }
}
