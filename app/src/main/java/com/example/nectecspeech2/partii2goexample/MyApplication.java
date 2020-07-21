package com.example.nectecspeech2.partii2goexample;

import android.app.Application;
import com.karumi.dexter.Dexter;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(getApplicationContext());
    }
}
