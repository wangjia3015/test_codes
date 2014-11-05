package com.example.DataBaseGenerator;

import android.app.Activity;
import android.os.Bundle;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        byte[] b = new byte[] { 0, 1, 2, 3, 4, 5} ;
    }
}
