package com.example.hugsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.hugsapp.models.RemoteConfiguration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Validation extends AppCompatActivity {

    ImageView left, right;
    Button continueButton, cancel, addTime, minusTime;
    TextView leftText, rightText;
    LottieAnimationView check;
    int dataChoice = -1;
    int duration, durationStage = 0;
    RemoteConfiguration config;

    Dialog validateDialog, sessionComplete;
    TextView validationResult, changeDuration;
    String results;
    FirebaseStorage storage;
    StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        config = new RemoteConfiguration(this);
        storage = FirebaseStorage.getInstance();
        reference = FirebaseStorage.getInstance().getReference().child("Sessions");

        continueButton = findViewById(R.id.save);
        check = findViewById(R.id.checked);
        cancel = findViewById(R.id.cancel);
        validateDialog = new Dialog(this);
        sessionComplete = new Dialog(this);
        addTime = findViewById(R.id.addTime);
        minusTime = findViewById(R.id.minusTime);
        changeDuration = findViewById(R.id.changeTime);
        validationResult = findViewById(R.id.validationResult);


        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            dataChoice = 0;
            duration = config.getSessionDuration();
            results = "";
        } else {
            dataChoice = extras.getInt("data");
            duration = extras.getInt("duration");
            results = extras.getString("results");
        }

        if (duration == config.getSessionDuration()) {
            durationStage = 0;
        } else if (duration > config.getSessionDuration()) {
            durationStage = 1;
        } else {
            durationStage = -1;
        }

        if (durationStage == 1) {
            addTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.muted_button));
            minusTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.next_button));
        } else if (durationStage == 0) {
            addTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.next_button));
            minusTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.next_button));
        } else {
            addTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.next_button));
            minusTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.muted_button));
        }

        openValidateDialog();

        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (durationStage <= 0) {
                    durationStage += 1;
                    duration += 10;
                    changeDuration.setText(duration + " seconds");
                } else {
                    changeDuration.setText(duration + " seconds");
                }

                if (durationStage == 1) {
                    addTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.muted_button));
                    minusTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.next_button));
                } else if (durationStage == 0) {
                    addTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.next_button));
                    minusTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.next_button));
                } else {
                    addTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.next_button));
                    minusTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.muted_button));
                }
            }
        });

        minusTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (durationStage >= 0) {
                    durationStage -= 1;
                    duration -= 10;
                    changeDuration.setText(duration + " seconds");
                } else {
                    changeDuration.setText(duration + " seconds");
                }

                if (durationStage == 1) {
                    addTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.muted_button));
                    minusTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.next_button));
                } else if (durationStage == 0) {
                    addTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.next_button));
                    minusTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.next_button));
                } else {
                    addTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.next_button));
                    minusTime.setBackground(ActivityCompat.getDrawable(Validation.this, R.drawable.muted_button));
                }
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent start = new Intent(getApplicationContext(), BeginSession.class);
                start.putExtra("duration", duration);

                if (changeDuration.getVisibility() == View.VISIBLE) {
                    Toast.makeText(Validation.this, "Session Erased", Toast.LENGTH_SHORT).show();
                    startActivity(start);
                } else {
                    openSessionCompleteDialog();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Validation.this, "Session Erased", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), BeginSession.class));
                finish();
            }
        });

    }

    public void openValidateDialog() {
        validateDialog.setContentView(R.layout.validate_dialog);
        validateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        validateDialog.setCancelable(false);

        TextView ok = validateDialog.findViewById(R.id.validate);
        TextView fail = validateDialog.findViewById(R.id.tryAgain);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validationResult.setText("Data Validated!");
                continueButton.setText("Save & Submit");
                changeDuration.setVisibility(View.INVISIBLE);
                addTime.setVisibility(View.INVISIBLE);
                minusTime.setVisibility(View.INVISIBLE);
                validateDialog.dismiss();
                check.playAnimation();
            }
        });

        fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validationResult.setText("Make any adjustment and restart");
                continueButton.setText("Restart Session");
                changeDuration.setVisibility(View.VISIBLE);
                addTime.setVisibility(View.VISIBLE);
                minusTime.setVisibility(View.VISIBLE);

                validateDialog.dismiss();
                check.setAnimation(R.raw.error);
                check.playAnimation();
            }
        });



        validateDialog.show();
    }


    public void openSessionCompleteDialog() {
        sessionComplete.setContentView(R.layout.session_complete);
        sessionComplete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sessionComplete.setCancelable(false);

        TextView next = sessionComplete.findViewById(R.id.nextSession);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent start = new Intent(getApplicationContext(), BeginSession.class);
                start.putExtra("duration", duration);

                String stampOfApproval = "Session Submitted: " + getTime();
                results += "\n\n" + stampOfApproval;
                writeToFile(results);
                Toast.makeText(Validation.this, "Session Saved!", Toast.LENGTH_SHORT).show();

                startActivity(start);
            }
        });




        sessionComplete.show();
    }

    public void writeToFile(String data) {
        // Get the directory for the user's public pictures directory.
        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/HUGS/");

        // Make sure the path directory exists.
        if(!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, "HUGS_Session_"+System.currentTimeMillis()+".txt");
        reference.child("HUGS_Session_" + System.currentTimeMillis() + ".txt").putBytes(data.getBytes());
        // Save your stream, don't forget to flush() it before closing it.
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }

    public static String getTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm:ss aa");
            return sdf.format(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return "";
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}