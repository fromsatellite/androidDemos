package com.satellite.remoteviews;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

public class FirstActivity extends Activity {

    private LinearLayout remote_views_content;

    private BroadcastReceiver remoteViewsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RemoteViews remoteViews = intent.getParcelableExtra(MyConstants.EXTRA_REMOTE_VIEWS);
            if (remoteViews != null){
                updateUI(remoteViews);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initView();
    }

    private void initView() {
        remote_views_content = findViewById(R.id.remote_views_content);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyConstants.REMOTE_ACTION);
        registerReceiver(remoteViewsReceiver, intentFilter);

        remote_views_content.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(FirstActivity.this, SendMsgActivity.class);
                FirstActivity.this.startActivity(intent);
            }
        }, 1000);
    }

    private void updateUI(RemoteViews remoteViews) {
        View apply = remoteViews.apply(this, remote_views_content);
        remote_views_content.addView(apply);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(remoteViewsReceiver);
        super.onDestroy();
    }
}
