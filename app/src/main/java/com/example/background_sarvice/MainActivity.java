package com.example.background_sarvice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {

    Button start,stop;
    TextView textView;
    DatabaseReference ref;
    public static  int datas;
    public static String chack;
    SharedPreferences sharedpreferences;
    SharedPreferences sh;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences.Editor editor;
    public static int flag=0;
    public static Double finalans = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.laval);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();



        start = findViewById(R.id.buttonStart);
        stop = findViewById(R.id.buttonStop);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Enter();
                startService(new Intent(MainActivity.this, MyService.class));
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, MyService.class));
             //  exit();
            }
        });



    }


    public  void Enter(){
        ref = FirebaseDatabase.getInstance().getReference().child("beds");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String Value = dataSnapshot.child("1").getValue().toString();
                textView.setText("100%");
                chack = Value;

                if(flag == 0){
                    new Handler().postDelayed(new Runnable() {
                        public void run() {

                            editor.putString("data",Value);
                            editor.commit();
                        }
                    }, 6000);
                    flag++;
                }
                else {

                    DataDiscovry(chack);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        start.setEnabled(false);
    }
    public void exit(){
        flag=0;
        start.setEnabled(true);
    }



    public void DataDiscovry(String chk){
        sh = getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);
        String a = sharedpreferences.getString("data","no found");
        Double x = Double.valueOf(a);
        Double y = Double.valueOf(chk);
        Double z = (y*100)/x;
         finalans = BigDecimal.valueOf(z)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        //Toast.makeText(this, "Value is"+ z, Toast.LENGTH_SHORT).show();
        textView.setText(finalans.toString());

    }


}


