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
        // 同应用处理方法
//        View apply = remoteViews.apply(this, remote_views_content);
//        remote_views_content.addView(apply);

        // 跨应用处理方法
        // 假如RemoteViews跨应用显示,那么就不能通过id来加载layout了，需要根据名称来加载布局
        // 注意：第三个参数：包名，一定要写RemoteViews来源的应用包名
        int layoutId = getResources().getIdentifier("layout_simulated_notification",
                "layout", getPackageName());
        View view = getLayoutInflater().inflate(layoutId, remote_views_content, false);
        // reapply方法不需要加载layout
        remoteViews.reapply(this, view);

        remote_views_content.addView(view);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(remoteViewsReceiver);
        super.onDestroy();
    }

    private void test(){
//        Notification notification = new Notification();
//        notification.icon = R.mipmap.ic_launcher;
//        notification.tickerText = "hello notification";
//        notification.when = System.currentTimeMillis();
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        Intent intent = new Intent(this, RemoteViewsActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);//RemoveViews所加载的布局文件
//        remoteViews.setTextViewText(R.id.tv, "这是一个Test");//设置文本内容
//        remoteViews.setTextColor(R.id.tv, Color.parseColor("#abcdef"));//设置文本颜色
//        remoteViews.setImageViewResource(R.id.iv, R.mipmap.ic_launcher);//设置图片
//        PendingIntent openActivity2Pending = PendingIntent.getActivity (this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);//设置RemoveViews点击后启动界面
//        remoteViews.setOnClickPendingIntent(R.id.tv, openActivity2Pending);
//        notification.contentView = remoteViews;
//        notification.contentIntent = pendingIntent;
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(2, notification);
    }
}
