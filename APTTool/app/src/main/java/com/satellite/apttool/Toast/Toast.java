package com.satellite.apttool.Toast;

import android.content.Context;
import android.content.res.Resources;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.satellite.apttool.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Toast {

    @IntDef({LENGTH_SHORT, LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {}

    /**
     * Show the view or text notification for a short period of time.  This time
     * could be user-definable.  This is the default.
     * @see #setDuration
     */
    public static final int LENGTH_SHORT = 0;

    /**
     * Show the view or text notification for a long period of time.  This time
     * could be user-definable.
     * @see #setDuration
     */
    public static final int LENGTH_LONG = 1;

    static final String TAG = "Toast";
    static final boolean localLOGV = false;

    final Context mContext;
    final TN mTN;
    int mDuration;
    View mNextView;

    public Toast(Context context) {
        this(context, null);
    }

    /**
     * Constructs an empty Toast object.  If looper is null, Looper.myLooper() is used.
     *
     */
    public Toast(@NonNull Context context, @Nullable Looper looper) {
        mContext = context;
        mTN = new TN(context.getPackageName(), looper);
        mTN.mY = context.getResources().getDimensionPixelSize(
                R.dimen.toast_y_offset);
        mTN.mGravity = context.getResources().getInteger(
                R.integer.config_toastDefaultGravity);
    }

    /**
     * Show the view for the specified duration.
     */
    public void show() {
        if (mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }

//        INotificationManager service = getService();
//        String pkg = mContext.getOpPackageName();
        TN tn = mTN;
        tn.mNextView = mNextView;
        mTN.show();

//        try {
//            service.enqueueToast(pkg, tn, mDuration);
//        } catch (RemoteException e) {
//            // Empty
//        }
    }

    /**
     * Close the view if it's showing, or don't show it if it isn't showing yet.
     * You do not normally have to call this.  Normally view will disappear on its own
     * after the appropriate duration.
     */
    public void cancel() {
        mTN.cancel();
    }

    /**
     * Set the view to show.
     * @see #getView
     */
    public void setView(View view) {
        mNextView = view;
    }

    /**
     * Return the view.
     * @see #setView
     */
    public View getView() {
        return mNextView;
    }

    /**
     * Set how long to show the view for.
     * @see #LENGTH_SHORT
     * @see #LENGTH_LONG
     */
    public void setDuration(@Duration int duration) {
        mDuration = duration;
        mTN.mDuration = duration;
    }

    /**
     * Return the duration.
     * @see #setDuration
     */
    @Duration
    public int getDuration() {
        return mDuration;
    }

    public void setMargin(float horizontalMargin, float verticalMargin) {
        mTN.mHorizontalMargin = horizontalMargin;
        mTN.mVerticalMargin = verticalMargin;
    }

    /**
     * Return the horizontal margin.
     */
    public float getHorizontalMargin() {
        return mTN.mHorizontalMargin;
    }

    /**
     * Return the vertical margin.
     */
    public float getVerticalMargin() {
        return mTN.mVerticalMargin;
    }

    /**
     * Set the location at which the notification should appear on the screen.
     * @see android.view.Gravity
     * @see #getGravity
     */
    public void setGravity(int gravity, int xOffset, int yOffset) {
        mTN.mGravity = gravity;
        mTN.mX = xOffset;
        mTN.mY = yOffset;
    }

    /**
     * Get the location at which the notification should appear on the screen.
     * @see android.view.Gravity
     * @see #getGravity
     */
    public int getGravity() {
        return mTN.mGravity;
    }

    /**
     * Return the X offset in pixels to apply to the gravity's location.
     */
    public int getXOffset() {
        return mTN.mX;
    }

    /**
     * Return the Y offset in pixels to apply to the gravity's location.
     */
    public int getYOffset() {
        return mTN.mY;
    }

    /**
     * Gets the LayoutParams for the Toast window.
     *
     */
    public WindowManager.LayoutParams getWindowParams() {
        return mTN.mParams;
    }


    public static Toast makeText(Context context, @StringRes int resId, @Duration int duration)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    public static Toast makeText(Context context, CharSequence text, @Duration int duration) {
        return makeText(context, null, text, duration);
    }

    public static Toast makeText(@NonNull Context context, @Nullable Looper looper,
                                                @NonNull CharSequence text, @Duration int duration) {
        Toast result = new Toast(context, looper);

        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.transient_notification, null);
        TextView tv = (TextView)v.findViewById(R.id.message);
        tv.setText(text);

        result.mNextView = v;
        result.mDuration = duration;

        return result;
    }

//    public void setText(@StringRes int resId) {
//        setText(mContext.getText(resId));
//    }

//    public void setText(CharSequence s) {
//        if (mNextView == null) {
//            throw new RuntimeException("This Toast was not created with Toast.makeText()");
//        }
//        TextView tv = mNextView.findViewById(com.android.internal.R.id.message);
//        if (tv == null) {
//            throw new RuntimeException("This Toast was not created with Toast.makeText()");
//        }
//        tv.setText(s);
//    }
}
