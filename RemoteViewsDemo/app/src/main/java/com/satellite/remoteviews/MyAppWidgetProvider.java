package com.satellite.remoteviews;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


public class MyAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "MyAppWidgetProvider";
    private static final String CLICK_ACTION = "com.satellite.remoteviews";

    public MyAppWidgetProvider(){
        super();
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context,intent);
        Log.i(TAG,"onReceive : action = "+intent.getAction());
        if(intent.getAction().equals(CLICK_ACTION)){
            Toast.makeText(context,"click it",Toast.LENGTH_SHORT).show();
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    Bitmap srcBitmap = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    for(int i=0;i<37;i++){
                        float degree = (i*10)%360;
                        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);
                        remoteViews.setImageViewBitmap(R.id.iv_desk, rotateBitmap(srcBitmap,degree));
                        appWidgetManager.updateAppWidget(new ComponentName(context,MyAppWidgetProvider.class),remoteViews);
                        SystemClock.sleep(30);
                    }
                }
            }.start();
        }
    }
    private Bitmap rotateBitmap(Bitmap srcBitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(degree);
        Bitmap tempBitmap = Bitmap.createBitmap(srcBitmap,0,0,srcBitmap.getWidth(),
                srcBitmap.getHeight(),matrix,true);
        return tempBitmap;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i(TAG,"onUpdate");
        final int counter = appWidgetIds.length;
        Log.i(TAG,"counter = " + counter);
        for(int i=0;i<counter;i++){
            int appWidgetId = appWidgetIds[i];
            onWidgetUpdate(context,appWidgetManager,appWidgetId);
        }
    }

    private void onWidgetUpdate(Context context,AppWidgetManager appWidgetManager,int appWidgetId){
        Log.i(TAG,"appWidgetId = "+appWidgetId);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);
        Intent intentClick = new Intent();
        intentClick.setAction(CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intentClick,0);
        remoteViews.setOnClickPendingIntent(R.id.iv_desk,pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
    }
}
