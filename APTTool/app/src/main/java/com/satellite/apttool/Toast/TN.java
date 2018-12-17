package com.satellite.apttool.Toast;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.satellite.apttool.R;

import static com.satellite.apttool.Toast.Toast.TAG;
import static com.satellite.apttool.Toast.Toast.localLOGV;

public class TN {
//        extends ITransientNotification.Stub {

//    private static INotificationManager sService;
//
//    static private INotificationManager getService() {
//        if (sService != null) {
//            return sService;
//        }
//        sService = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
//        return sService;
//    }

    final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();

    private static final int SHOW = 0;
    private static final int HIDE = 1;
    private static final int CANCEL = 2;
    final Handler mHandler;

    int mGravity;
    int mX, mY;
    float mHorizontalMargin;
    float mVerticalMargin;


    View mView;
    View mNextView;
    int mDuration;

    WindowManager mWM;

    String mPackageName;

    static final long SHORT_DURATION_TIMEOUT = 4000;
    static final long LONG_DURATION_TIMEOUT = 7000;

    TN(String packageName, @Nullable Looper looper) {
        // XXX This should be changed to use a Dialog, with a Theme.Toast
        // defined that sets up the layout params appropriately.
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
//        params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        params.windowAnimations = R.style.MyAnimation;
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mPackageName = packageName;

        if (looper == null) {
            // Use Looper.myLooper() if looper is not specified.
            looper = Looper.myLooper();
            if (looper == null) {
                throw new RuntimeException(
                        "Can't toast on a thread that has not called Looper.prepare()");
            }
        }
        mHandler = new Handler(looper, null) {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOW: {
//                        IBinder token = (IBinder) msg.obj;
//                        handleShow(token);
                        handleShow();
                        break;
                    }
                    case HIDE: {
                        handleHide();
                        // Don't do this in handleHide() because it is also invoked by
                        // handleShow()
                        mNextView = null;
                        break;
                    }
                    case CANCEL: {
                        handleHide();
                        // Don't do this in handleHide() because it is also invoked by
                        // handleShow()
                        mNextView = null;
//                        try {
//                            getService().cancelToast(mPackageName, TN.this);
//                        } catch (RemoteException e) {
//                        }
                        break;
                    }
                }
            }
        };
    }

    /**
     * schedule handleShow into the right thread
     */
//    @Override
//    public void show(IBinder windowToken) {
    public void show() {
        if (localLOGV) Log.v(TAG, "SHOW: " + this);
//        mHandler.obtainMessage(SHOW, windowToken).sendToTarget();
        mHandler.obtainMessage(SHOW).sendToTarget();
//        Message msg = mHandler.obtainMessage(SHOW);
//        mHandler.sendMessageDelayed(msg, 500);
    }

    /**
     * schedule handleHide into the right thread
     */
//    @Override
    public void hide() {
        if (localLOGV) Log.v(TAG, "HIDE: " + this);
        mHandler.obtainMessage(HIDE).sendToTarget();
    }

    public void cancel() {
        if (localLOGV) Log.v(TAG, "CANCEL: " + this);
        mHandler.obtainMessage(CANCEL).sendToTarget();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
//    public void handleShow(IBinder windowToken) {
    private void handleShow() {
        if (localLOGV) Log.v(TAG, "HANDLE SHOW: " + this + " mView=" + mView
                + " mNextView=" + mNextView);
        // If a cancel/hide is pending - no need to show - at this point
        // the window token is already invalid and no need to do any work.
        if (mHandler.hasMessages(CANCEL) || mHandler.hasMessages(HIDE)) {
            return;
        }
        if (mView != mNextView) {
            // remove the old view if necessary
            handleHide();
            mView = mNextView;
//            Context context = mView.getContext().getApplicationContext();
            Context context = null;
//            String packageName = mView.getContext().getOpPackageName();
            String packageName = mView.getContext().getPackageName();
            if (context == null) {
                context = mView.getContext();
            }
            mWM = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            // We can resolve the Gravity here by using the Locale for getting
            // the layout direction
            final Configuration config = mView.getContext().getResources().getConfiguration();
            final int gravity = Gravity.getAbsoluteGravity(mGravity, config.getLayoutDirection());
            mParams.gravity = gravity;
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                mParams.horizontalWeight = 1.0f;
            }
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                mParams.verticalWeight = 1.0f;
            }
            mParams.x = mX;
            mParams.y = mY;
            mParams.verticalMargin = mVerticalMargin;
            mParams.horizontalMargin = mHorizontalMargin;
            mParams.packageName = packageName;
            // hide???
//            mParams.hideTimeoutMilliseconds = mDuration ==
//                    Toast.LENGTH_LONG ? LONG_DURATION_TIMEOUT : SHORT_DURATION_TIMEOUT;
//            mParams.token = windowToken;
            mParams.token = null;
            if (mView.getParent() != null) {
                if (localLOGV) Log.v(TAG, "REMOVE! " + mView + " in " + this);
                mWM.removeView(mView);
            }
            if (localLOGV) Log.v(TAG, "ADD! " + mView + " in " + this);
            // Since the notification manager service cancels the token right
            // after it notifies us to cancel the toast there is an inherent
            // race and we may attempt to add a window after the token has been
            // invalidated. Let us hedge against that.
            try {
                mWM.addView(mView, mParams);
//                trySendAccessibilityEvent();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

//    private void trySendAccessibilityEvent() {
//        AccessibilityManager accessibilityManager =
//                AccessibilityManager.getInstance(mView.getContext());
//        if (!accessibilityManager.isEnabled()) {
//            return;
//        }
//        // treat toasts as notifications since they are used to
//        // announce a transient piece of information to the user
//        AccessibilityEvent event = AccessibilityEvent.obtain(
//                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);
//        event.setClassName(getClass().getName());
//        event.setPackageName(mView.getContext().getPackageName());
//        mView.dispatchPopulateAccessibilityEvent(event);
//        accessibilityManager.sendAccessibilityEvent(event);
//    }

    private void handleHide() {
        if (localLOGV) Log.v(TAG, "HANDLE HIDE: " + this + " mView=" + mView);
        if (mView != null) {
            // note: checking parent() just to make sure the view has
            // been added...  i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (mView.getParent() != null) {
                if (localLOGV) Log.v(TAG, "REMOVE! " + mView + " in " + this);
                mWM.removeViewImmediate(mView);
            }

            mView = null;
        }
    }
}
