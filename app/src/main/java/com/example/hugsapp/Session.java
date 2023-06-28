package com.example.hugsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hugsapp.models.RemoteConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Session extends AppCompatActivity {

    ImageView clock, status;
    TextView timer;
    Button abort;
    long timerDuration;
    int handChoice = -1;
    int duration;
    RemoteConfiguration config;

    CountDownTimer countDownTimer;

    String results= "";
    MediaPlayer mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        config = new RemoteConfiguration(this);

        mp = MediaPlayer.create(this, R.raw.alarm);
        clock = findViewById(R.id.clockIcon);
        status = findViewById(R.id.statusDot);
        timer = findViewById(R.id.timerText);
        abort = findViewById(R.id.abortSession);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (MainActivity.mBluetoothLeService != null) {
            final boolean result = MainActivity.mBluetoothLeService.connect(MainActivity.mDeviceAddress);
            Log.d("BT_STATUS", "Connect request result=" + result);
        }

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            handChoice = 0;
            duration = config.getSessionDuration();
            //Set Header
            results = "Date & Time: " + getTime() + "\nHand: Right\nSession Duration: " + duration + " seconds\n\nData:\n";
        } else {
            handChoice = extras.getInt("hand");
            duration = extras.getInt("duration");
            if (handChoice == 0){
                results = "Date & Time: " + getTime() + "\nHand: Right\nSession Duration: " + duration + " seconds\n\nData:\n";
            }
            else{
                results = "Date & Time: " + getTime() + "\nHand: Left\nSession Duration: " + duration + " seconds\n\nData:\n";
            }
        }

        timerDuration = TimeUnit.SECONDS.toMillis(duration);

        countDownTimer = new CountDownTimer(timerDuration, 1000) {
            @Override
            public void onTick(long l) {
                //convert mills to minute and seccond
                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                timer.setText(sDuration);

            }

            @Override
            public void onFinish() {
                animateView(getApplicationContext(), clock, R.anim.shake, 0);
                animateView(getApplicationContext(), timer, R.anim.pow, 0);
                mp.start();
                vibrate(Session.this);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent validate = new Intent(getApplicationContext(), Validation.class);
                        validate.putExtra("hand", handChoice);
                        validate.putExtra("duration", duration);
                        validate.putExtra("results", results);
                        mp.stop();
                        startActivity(validate);
                        finish();
                    }
                }, 3000);

            }
        }.start();

        abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                startActivity(new Intent(getApplicationContext(), BeginSession.class));
                finish();
            }
        });
    }

    private void animateView(Context mContext, final View view, int animationType, long delay){
        Animation animation = AnimationUtils.loadAnimation(mContext, animationType);
        animation.setStartOffset(delay);
        view.startAnimation(animation);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                mConnected = true;
//                updateConnectionState("Connected");
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mConnected = false;
//                updateConnectionState("Disconnected");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

                // Show all the supported services and characteristics.
                for (BluetoothGattService gattService : MainActivity.mBluetoothLeService.getSupportedGattServices()) {
                    Log.i("BT_SERVICES_CHARS", "Service: "+gattService.toString());
                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        String uuid = gattCharacteristic.getUuid().toString();
                        Log.i("BT_SERVICES_CHARS", "UUID characteristic: "+ uuid);
                        if (uuid.equals("19b10001-e8f2-537e-4f6c-d104768a1214")) {
                            MainActivity.mBluetoothLeService.readCharacteristic(gattCharacteristic);
                            MainActivity.mNotifyCharacteristic = gattCharacteristic;
                            MainActivity.mBluetoothLeService.setCharacteristicNotification(
                                    MainActivity.mNotifyCharacteristic, true);
                        }
                    }
                }

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                byte[] data = intent.getExtras().getByteArray(BluetoothLeService.EXTRA_DATA);
                int size = (int) Math.ceil(data.length/2);
                int[] channel = new int[size];
                int j = 0;
                for (int i = 0; i < size; i+=2) {
                    channel[j] = (data[i]<<8) + (data[i+1] & 0xFF);
                    results += channel[j] + "\n";
                    j++;
                }
                //results += "\n";
                MainActivity.mBluetoothLeService.readCharacteristic(MainActivity.mNotifyCharacteristic); //trigger another read characteristic
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }



    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        finish();
    }

    public static String getTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm:ss aa");
            return sdf.format(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return "";
        }

    }

    public void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(1000);
        }
    }

}