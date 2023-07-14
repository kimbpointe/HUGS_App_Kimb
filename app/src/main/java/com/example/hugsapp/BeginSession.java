package com.example.hugsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.hugsapp.models.RemoteConfiguration;

import java.util.List;

public class BeginSession extends AppCompatActivity {

    int dataChoice = -1; //right = 0, left = 1
    ImageView video, force, back;
    Button start;
    int duration;
    RemoteConfiguration config;
    LottieAnimationView loading;
    TextView loadingText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beign_session);
        config = new RemoteConfiguration(this);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            duration = config.getSessionDuration();
        } else {
            duration = extras.getInt("duration");
        }

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (MainActivity.mBluetoothLeService != null) {
            final boolean result = MainActivity.mBluetoothLeService.connect(MainActivity.mDeviceAddress);
            Log.d("BT_STATUS", "Connect request result=" + result);
        }
        try {
            MainActivity.mBluetoothLeService.readCharacteristic(MainActivity.mNotifyCharacteristic);
        } catch (Exception e) {}

        video = findViewById(R.id.camera);
        force = findViewById(R.id.hand);
        start = findViewById(R.id.startSession);
        back = findViewById(R.id.back2);
        loading = findViewById(R.id.loadingAnimate);
        loadingText = findViewById(R.id.loadingText);

        start.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataChoice = 1;
                setData(dataChoice);
            }
        });

        force.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataChoice = 0;
                setData(dataChoice);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataChoice == 1) {
                    Intent video = new Intent(getApplicationContext(), VideoService.class);
                    startActivity(video);
                } else if (dataChoice == -1) {
                    Toast.makeText(getApplicationContext(), "Select Data", Toast.LENGTH_SHORT).show();
                }
                else {
                    // next screen
                    Intent start = new Intent(getApplicationContext(), Session.class);
                    start.putExtra("data type", dataChoice);
                    start.putExtra("duration", duration);

                    startActivity(start);
                    finish();
                }
            }
        });


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
                System.out.print("Size of Data");
                System.out.print(size);
                int j = 0;
                for (int i = 0; i < size; i+=2) {
                    channel[j] = (data[i]<<8) + (data[i+1] & 0xFF);
                    j++;
                }
                start.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
                loadingText.setVisibility(View.INVISIBLE);
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
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (MainActivity.mBluetoothLeService != null) {
            final boolean result = MainActivity.mBluetoothLeService.connect(MainActivity.mDeviceAddress);
            Log.d("BT_STATUS", "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        finish();
    }



    public void setData(int dataSelection) {
        if (dataSelection == 1) {
            video.setBackground(getApplicationContext().getDrawable(R.drawable.select_circle));
            force.setBackground(getApplicationContext().getDrawable(R.drawable.clear));
        } else if (dataSelection == 0) {
            video.setBackground(getApplicationContext().getDrawable(R.drawable.clear));
            force.setBackground(getApplicationContext().getDrawable(R.drawable.select_circle));
        }
    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unbindService(MainActivity.mServiceConnection);
//        MainActivity.mBluetoothLeService = null;
//
//        // Don't forget to unregister the ACTION_FOUND receiver.
//        unregisterReceiver(mReceiver);
//    }
}