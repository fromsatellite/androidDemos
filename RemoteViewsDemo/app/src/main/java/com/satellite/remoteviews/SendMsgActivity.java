package com.satellite.remoteviews;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

public class SendMsgActivity extends AppCompatActivity {

    private Button btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);
        btn_send = findViewById(R.id.btn_send);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRemoteViewsMsg();
                finish();
            }
        });
    }

    private void sendRemoteViewsMsg() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_simulated_notification);
        remoteViews.setImageViewResource(R.id.iv_icon, R.mipmap.ic_launcher_round);
        remoteViews.setTextViewText(R.id.tv_msg, "msg from process : "+ Process.myPid());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SendMsgActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_icon, pendingIntent);

        Intent intent = new Intent(MyConstants.REMOTE_ACTION);
        intent.putExtra(MyConstants.EXTRA_REMOTE_VIEWS, remoteViews);
        sendBroadcast(intent);
    }
}
