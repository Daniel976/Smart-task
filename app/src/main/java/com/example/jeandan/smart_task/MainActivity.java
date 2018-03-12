package com.example.jeandan.smart_task;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.jeandan.smart_task.R.layout.activity_main;



public class MainActivity extends AppCompatActivity {
        //TextView txt;
        private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //txt = (TextView) findViewById(R.id.textView3);
        btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();

            }
        });
        /*Intent myIntent = new Intent(MainActivity.this,Activity2.class);
        startService(myIntent);**/

    }

        public void openActivity2(){
            Intent intent = new Intent(this, Activity2.class);
            startActivity(intent);

    }
}
