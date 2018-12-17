package com.satellite.eventbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.satellite.eventbus.model.EventBean;
import com.satellite.library.eventbus.EventBus;
import com.satellite.library.eventbus.Subscribe;
import com.satellite.library.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessage (EventBean bean) {
        Log.e("@@@", "current thread is " + Thread.currentThread().getName());
        Log.e("@@@", "data = " + bean.getId() + ", " + bean.getMessage());
        Toast.makeText(this, "接收到数据 = " + bean.getId() + ", " + bean.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    public void jump(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unRegister(this);
        super.onDestroy();
    }
}
