package org.free.chat.others;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 需要调整内容高度
 */
public abstract class ResizeHeightSubKeyboard extends SubKeyboard {

    private static final String TAG = "ResizeHeightSubKeyboard";
    protected Keyboard mKeyboard;
    protected LinearLayout mLayout;
    protected ImageView mIcon;
    protected Drawable mIconDrawable;

    public ResizeHeightSubKeyboard(final Keyboard keyboard, LinearLayout layout, ImageView icon) {
        mKeyboard = keyboard;
        mLayout = layout;
        mIcon = icon;
        mIconDrawable = icon.getDrawable();
        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboard.showCurrentKeyboard(ResizeHeightSubKeyboard.this);
            }
        });
    }

    @Override
    public void resetDrawable() {
        mIcon.setImageDrawable(mIconDrawable);
    }


    @Override
    void hidden() {
        resetDrawable();
        mLayout.setVisibility(View.GONE);
    }

    @Override
    void show() {
        if (mKeyboard.isSoftKeyboardShown()) {
            mKeyboard.hideSoftKeyboard();
        }
        switchDrawable();
        int height = mKeyboard.getSoftKeyboardHeight();
        if (height == 0) {
            height = mKeyboard.getKeyboardHeight();
        }
        final int fHeight = height;
        Log.i(TAG, height + "");
        mLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLayout.getLayoutParams().height = fHeight;
                mLayout.setVisibility(View.VISIBLE);
            }
        }, 200);
    }
}