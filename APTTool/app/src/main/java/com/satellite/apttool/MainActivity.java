package com.satellite.apttool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.satellite.apttool.Toast.Toast;
import com.satellite.apttool_api.ProxyTool;

import apttool.satellite.com.apttool_annotations.OnClick;
import apttool.satellite.com.apttool_annotations.ViewById;

//import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.tv_hello)
    TextView tv_hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProxyTool.bind(this);
    }

    @OnClick({R.id.tv_hello})
    public void click(View view){
        if (view.getId() == R.id.tv_hello) {
//            android.widget.Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
            //
            Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show();
        }
    }
}
