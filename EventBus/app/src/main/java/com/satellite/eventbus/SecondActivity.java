package com.satellite.eventbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.satellite.eventbus.model.EventBean;
import com.satellite.library.eventbus.EventBus;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void post(View view) {
        EventBus.getDefault().post(new EventBean(111, "I am second"));
        finish();
    }
}
