package com.example.nectecspeech2.partii2goexample;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import th.or.nectec.partii.embedded.android.EmbeddedUtils.ModelUtil;
import th.or.nectec.partii.embedded.android.RecognitionListener;
import th.or.nectec.partii.embedded.android.SpeechRecognizer;
import android.util.Base64;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity /*02*/implements RecognitionListener, ModelUtil.OnReceiveStatusListener{

    private SpeechRecognizer recognizer;
    private boolean isSetupRecognizer = false;
    private ModelUtil mUtil =null;
    private Context context = null;
    private EditText edt_apikey=null;
    private TextView txt_result=null;
    private Button btn_download=null;
    private Button btn_start=null;
    private Button btn_stop=null;
    private String answer = "ช่วยด้วย";
    int record_state = 0; // 0 non record, 1 record
    int count = 0;
    int DW = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Typeface customFont = Typeface.createFromAsset(getAssets(),"fonts/Anantason-Regular.ttf");
        txt_result.setTypeface(customFont);*/

        /*hide action bar*/

        View overlay = findViewById(R.id.mylayout);

        overlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

/*03 set button id*/
        context = this;
        txt_result = (TextView) findViewById(R.id.text_result);
        btn_download = (Button) findViewById(R.id.btn_download);
        btn_start = (Button) findViewById(R.id.btn_start);
        /*btn_stop = (Button) findViewById(R.id.btn_stop);*/

/*04 set request model*/
        mUtil=new ModelUtil();
        if(mUtil.isPermissionGranted(context)) {
            if(mUtil.isSyncDir(getExternalFilesDir("")) && !isSetupRecognizer) {
                setUpRecognizer();
            }
        }else {
            mUtil.requestPermission(context);
        }
/*05 set apikey and download model*/

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String apikey = "2go-jt7SJkshdX";
                mUtil.setOnReceiveDialogStatus(MainActivity.this);
                mUtil.startDownload(context, MainActivity.this, getExternalFilesDir(""), apikey);
            }
        });
/*06 record button*/
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUtil.isSyncDir(getExternalFilesDir("")) && !isSetupRecognizer) {
                    setUpRecognizer();
                }else{
                    if (record_state == 0) {
                        /*record_state = 1;*/
                        if (isSetupRecognizer) {
                            Toast.makeText(MainActivity.this,"start",Toast.LENGTH_SHORT).show();
                            recognizer.startListening();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recognizer.stop();
                                    Toast.makeText(MainActivity.this,"stop",Toast.LENGTH_SHORT).show();
                                }
                            }, 5000);
                        }
                    }

                    /*else if (record_state == 1) {
                        record_state = 0;
                        if (isSetupRecognizer) {
                            Toast.makeText(MainActivity.this,"stop",Toast.LENGTH_SHORT).show();
                            recognizer.stop();
                        }
                    }*/
                }
            }
        });
/*07*/
        /*btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSetupRecognizer) {
                    recognizer.stop();
                    btn_start.setEnabled(true);
                    btn_stop.setEnabled(false);
                    txt_result.setText("");
                }
            }
        });*/
    }



    public void setUpRecognizer(){
        recognizer=mUtil.getRecognizer(context);
        if (recognizer.getDecoder() == null) {
            finish();
        }
        recognizer.addListener(this);
        isSetupRecognizer = true;
    }

    /*check result*/
    @Override
    public void onResult(String s) {
        /*08*/
        if (s != null) {
            if (!s.equals(SpeechRecognizer.NO_HYP) && !s.equals(SpeechRecognizer.REQUEST_NEXT)) {
                /*txt_result.setText(s);*/
                if(s.trim().equals(answer)){
                    if(count == 0) {
                        txt_result.setText("กรุณายืนยันการขอความช่วยเหลือ");
                        count++;
                        if (isSetupRecognizer) {
                            Toast.makeText(MainActivity.this,"start",Toast.LENGTH_SHORT).show();
                            recognizer.startListening();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recognizer.stop();
                                    Toast.makeText(MainActivity.this,"stop",Toast.LENGTH_SHORT).show();
                                }
                            }, 5000);
                        }
                    }
                    else if(count == 1){
                    Toast.makeText(MainActivity.this,"ยืนยันการขอความช่วยเหลือ",Toast.LENGTH_SHORT).show();
                    txt_result.setText("ขณะนี้กำลังขอความช่วยเหลือไปยังบุตรหลานของท่าน");
                    count = 0;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txt_result.setText("กรุณากดปุ่มสีแดงเพื่อขอความช่วยเหลือ");
                            }
                        }, 10000);
                    }
                }
            }
            else{
                count = 0;
                txt_result.setText("กรุณาพูดใหม่อีกครั้ง");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txt_result.setText("กรุณากดปุ่มสีแดงเพื่อขอความช่วยเหลือ");
                    }
                }, 1000);
            }
        }

    }

    @Override
    public void onReceiveDownloadComplete() {
        /*09*/
        if(isSetupRecognizer) {
            recognizer.cancel();
            //recognizer.shutdown();
            recognizer.removeListener(this);
        }

        isSetupRecognizer = false;
        setUpRecognizer();
    }

    @Override
    public void onReceiveDownloadFailed() {

    }


    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onProgress(int i) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onPartialResult(String s) {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {

    }
}
