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
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Activity2 extends AppCompatActivity  implements SensorEventListener {

    float xAccel, yAccel, zAccel;
    float xPreviousAccel, yPreviousAccel, zPreviousAccel;

    boolean firstUpdate = true;
    boolean shakeInitiated = false;
    float shakeThreshold = 12.5F;
    private TextView textx;
    private TextView texty;
    private TextView textz;
    private ImageView photo;
    private Bitmap bit;
    private Boolean camera = true;
    private StorageReference mStorageRef;


    String mCurrentPhotoPath;

    Sensor accelerometer;
    SensorManager sm;

    public IBinder onBind(Intent intent) {
        return null;
    }

        public void onCreate(Bundle savedInstanceState)
        {
            mStorageRef = FirebaseStorage.getInstance().getReference();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_2);

            sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sm.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);


            textx = (TextView) findViewById(R.id.textView1);
            texty = (TextView) findViewById(R.id.textView2);
            textz = (TextView) findViewById(R.id.textView3);
            photo=(ImageView) findViewById(R.id.imageView1);


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


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


        private void updateAccelParameters(float xNewAccel, float yNewAccel, float zNewAccel) {
            if (firstUpdate) {
                xPreviousAccel = xNewAccel;
                yPreviousAccel = yNewAccel;
                zPreviousAccel = zNewAccel;
                firstUpdate = false;
            } else {
                xPreviousAccel = xAccel;
                yPreviousAccel = yAccel;
                zPreviousAccel = zAccel;
            }
            xAccel = xNewAccel;
            yAccel = yNewAccel;
            zAccel = zNewAccel;

            if (yNewAccel >=8 || xNewAccel >=8 && camera == true) {
                //On prend la photo

                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
                camera = false;*/
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 0);
                        StorageReference riversRef = mStorageRef.child("images/rivers.jpg");

                        riversRef.putFile(photoURI)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        // ...
                                    }
                                });
                    }

                        //startActivityForResult(intent, 0);
                        camera = false;

                    }
                }
                textx.setText("x = " + Float.toString(Math.round(xNewAccel)));
                texty.setText("y = " + Float.toString(Math.round(yNewAccel)));
                textz.setText("z = " + Float.toString(Math.round(zNewAccel)));

        }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

         protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            //super.onActivityResult(requestCode, resultCode, data);
             /*bit = (Bitmap) data.getExtras().get("data");
             img.setImageBitmap(bit);*/
             /*if (requestCode == 1 && resultCode == RESULT_OK) {
                 bit = (Bitmap) data.getExtras().get("data");
                 img.setImageBitmap(bit);
             }*/
            //galleryAddPic();
            //camera = true
             
        }
    }


