package com.example.background_sarvice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyService extends Service {

    MediaPlayer player;
    TextView textView;
    Context context;

    DatabaseReference ref;
    public static  int datas;
    public static String chack;
    SharedPreferences sharedpreferences;
    SharedPreferences sh;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences.Editor editor;
    public static int flag=0;
    public static Double finalans = 0.0;
    public static int mds = 0;
    public static double d;


    public final AtomicBoolean running = new AtomicBoolean(false);
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mds==1){
//        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
//        player.setLooping(true);
//        player.start();
        }
//        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        editor = sharedpreferences.edit();
//        editor.commit();
        new Thread(new ControlSubThread(5000)).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //new Thread(new Task()).interrupt();

        int e = 1;

        running.set(false);
        flag=0;
        if (mds==1){
            System.out.println("System Terminate"+mds);
            try{

                ControlSubThread aa = new ControlSubThread();
                aa.ff(mds);

            }
            catch (Exception ex){
                System.out.println(ex);
            }
            mds = 0;
        }
    }

    public class ControlSubThread implements Runnable {

        private Thread worker;

        private int interval;

        public ControlSubThread(int sleepInterval) {
            interval = sleepInterval;
            //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // View view = inflater.inflate(R.layout.activity_main,null);
            // textView = view.findViewById(R.id.laval);

        }
        public ControlSubThread() {}



        public void run() {
            running.set(true);
            int i=0;
            while (running.get()) {
                try {
                    i++;
                    Thread.sleep(interval);
                    dd(i);

                    ref = FirebaseDatabase.getInstance().getReference().child("beds");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String Value = dataSnapshot.child("1").getValue().toString();
                            //    textView.setText("100%");
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



                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, Failed to complete operation");
                }
                // do something here
            }
        }

        public void dd(int i){
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();

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
            System.out.println(x+" "+y+" "+finalans);
            //Toast.makeText(this, "Value is"+ z, Toast.LENGTH_SHORT).show();
//            textView.setText(finalans.toString());

            //  System.out.println(finalans);
            if(finalans<=10){
                System.out.println("enterd");
                if(mds == 0) {
                    System.out.println("enterd"+mds);
                    ff(mds);
                }
            }
        }

        public void ff(int u){
            if(u==0){
                player = MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_RINGTONE_URI);
                player.setLooping(true);
                player.start();
                mds = 1;}
            if (u==1){
                player.stop();
            }
        }

    }
}
