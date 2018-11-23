package com.example.karimm7mad.voiceeventsreminder;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    public ImageButton micBtn = null;
    public ImageButton helpBtn = null;
    public ImageButton optionsBtn = null;
    public ImageButton exitBtn = null;
    public TextView txt = null;
    public Intent goToFormActivity = null;

    public AlertDialog.Builder builder = null;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.txt = findViewById(R.id.txt1);
        this.micBtn = findViewById(R.id.micBtn);
        this.micBtn.setImageResource(R.raw.mic);
        this.micBtn.setScaleType(ImageView.ScaleType.FIT_XY);
        this.micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechRecognition();
            }
        });


        this.helpBtn = findViewById(R.id.helpBtn);
        this.helpBtn.setImageResource(R.raw.help);
        this.helpBtn.setScaleType(ImageView.ScaleType.FIT_XY);
        this.helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Help");
                builder.setMessage("Contact:- 01012223157\nVersion:- 1.0.0");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }

        });
        this.optionsBtn = findViewById(R.id.optionsBtn);
        this.optionsBtn.setImageResource(R.raw.option);
        this.optionsBtn.setScaleType(ImageView.ScaleType.FIT_XY);
        this.optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Options");
                builder.setMessage("Erase All Events");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });

        this.exitBtn = findViewById(R.id.exitBtn);
        this.exitBtn.setImageResource(R.raw.exit);
        this.exitBtn.setScaleType(ImageView.ScaleType.FIT_XY);
        this.exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });






        this.goToFormActivity = new Intent(this, FormActivity.class);
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
    public void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.app_name));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), getString(R.string.app_name), Toast.LENGTH_SHORT).show();
        }
    }
    /*Receiving speech input*/
    @Override
    // regex used for event Reminding is "eventname weekday month dayNum year at hr:min am/pm"
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String whatisSaid = result.get(0).toString();
                    Log.d("asdasd","lastT: |"+whatisSaid+"|");
                    String split1 = "at";
                    String split2 = "day";
                    String split3 = " (Mon|Tues|Wednes|Thurs|Fri|Sat|Sun)";
                    //split 1 : time split (separate time from the rest of the sentence) and add it to intent
                    Pattern p = Pattern.compile(" " + split1 + " ", Pattern.CASE_INSENSITIVE);
                    String[] separateTimeFromRestSplit = p.split(whatisSaid);
                    goToFormActivity.putExtra("time", separateTimeFromRestSplit[1]);
                    //split 2 : split day word(separate Date from eventName except day name)
                    p = Pattern.compile("" + split2 + " ", Pattern.CASE_INSENSITIVE);
                    String[] separateNameFromDate = p.split(separateTimeFromRestSplit[0]);
                    goToFormActivity.putExtra("date", separateNameFromDate[1]);
                    //split Day Name from event Name
                    p = Pattern.compile(split3);
                    String[] splitDayfromName = p.split(separateNameFromDate[0]);
                    goToFormActivity.putExtra("eventName", splitDayfromName[0]);
                    goToFormActivity.putExtra("eventDay", (separateNameFromDate[0].substring(separateNameFromDate[0].lastIndexOf(" ") + 1, separateNameFromDate[0].length())) + "day");
                    startActivity(this.goToFormActivity);
                }
                break;
            }

        }

    }
    //to see the splits results (debug Purpose)
    public void previewSplit(String[] x, int splitNum) {
        for (int i = 0; i < x.length; i++)
            Log.d("ddd", "debug:-" + splitNum + ")Split no" + i + "->" + x[i]);
    }

}




