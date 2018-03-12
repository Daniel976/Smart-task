package com.example.jeandan.smart_task;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class Activity2 extends AppCompatActivity  implements SensorEventListener {

    float xAccel, yAccel, zAccel;
    float xPreviousAccel, yPreviousAccel, zPreviousAccel;

    boolean firstUpdate = true;
    boolean shakeInitiated = false;
    float shakeThreshold = 12.5F;
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private ImageView img;
    private android.graphics.Bitmap bit;
    private Boolean camera = true;


    Sensor accelerometer;
    SensorManager sm;

    public IBinder onBind(Intent intent) {
        return null;
    }

        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_2);

            sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sm.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);


            text1 = (TextView) findViewById(R.id.textView1);
            text2 = (TextView) findViewById(R.id.textView2);
            text3 = (TextView) findViewById(R.id.textView3);
            img=(ImageView) findViewById(R.id.imageView1);


            /*text1.setText("x = "+Float.toString(xAccel));
            text2.setText("y = "+Float.toString(yAccel));
            text3.setText("z = "+Float.toString(zAccel));*/

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }




        @Override
        public void onSensorChanged(SensorEvent event)
        {
            updateAccelParameters (event.values[0], event.values[1], event.values[2]);

            if ((!shakeInitiated)  && isAccelerationChanged())
            {
                shakeInitiated = true;
            }
            else if ((shakeInitiated) && isAccelerationChanged())
            {
                executeShakeAction();
            }
            else if ((shakeInitiated) && !isAccelerationChanged())
            {
                shakeInitiated = false;
            }




        }

        private void executeShakeAction()
        {
            Intent ii = new Intent(this, Activity2.class);
            ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(ii);
        }

        private boolean isAccelerationChanged()
        {
            float deltaX = Math.abs(xPreviousAccel - xAccel);
            float deltaY = Math.abs(yPreviousAccel - yAccel);
            float deltaZ = Math.abs(zPreviousAccel - zAccel);

            return (deltaX > shakeThreshold && deltaY > shakeThreshold)
                    || (deltaX > shakeThreshold && deltaZ > shakeThreshold)
                    || (deltaY > shakeThreshold && deltaZ > shakeThreshold);

        }




        private void updateAccelParameters(float xNewAccel, float yNewAccel, float zNewAccel) {
            if (firstUpdate) {
                xPreviousAccel = xNewAccel;
                yPreviousAccel = yNewAccel;
                zPreviousAccel = zNewAccel;
                firstUpdate = false;
            }
            else
            {
                xPreviousAccel = xAccel;
                yPreviousAccel = yAccel;
                zPreviousAccel = zAccel;
            }
            xAccel = xNewAccel;
            yAccel = yNewAccel;
            zAccel = zNewAccel;

            if(yNewAccel > 8 && camera == true) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
                camera = false;
            }
            text1.setText("x = "+Float.toString(Math.round(xNewAccel)));
            text2.setText("y = "+Float.toString(Math.round(yNewAccel)));
            text3.setText("z = "+Float.toString(Math.round(zNewAccel)));
        }

         protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            bit = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(bit);
            //camera = true;

        }
    }


