package com.gospelware.compassview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gospelware.compassviewlib.CompassView;
import com.gospelware.compassviewlib.OnRotationChangeListener;

public class MainActivity extends AppCompatActivity {

    private CompassView compassView;
    private TextView txtRotation;
    private int angle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compassView = (CompassView) findViewById(R.id.compass);
//        compassView.startScan();
        compassView.setRotationChangedListener(rotationChangeListener);

        txtRotation = (TextView)findViewById(R.id.txtRotation);

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
                compassView.setCompassRotation(angle);
            }
        });
    }

    private OnRotationChangeListener rotationChangeListener = new OnRotationChangeListener() {
        @Override
        public void rotationChanged(int oldRotation, int newRotation) {
            txtRotation.setText(newRotation + "°");
        }
    };
}
