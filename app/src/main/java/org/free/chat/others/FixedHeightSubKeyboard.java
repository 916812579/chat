package org.free.chat.others;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 需要调整内容高度
 */
public abstract class FixedHeightSubKeyboard extends SubKeyboard {

    protected Keyboard mKeyboard;
    protected View mLayout;
    protected ImageButton mIcon;
    protected Drawable mIconDrawable;

    public FixedHeightSubKeyboard(final Keyboard keyboard, View layout, ImageButton icon) {
        mKeyboard = keyboard;
        mLayout = layout;
        mIcon = icon;
        mIconDrawable = icon.getDrawable();
        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboard.showCurrentKeyboard(FixedHeightSubKeyboard.this);
            }
        });
    }

    public abstract void switchDrawable();

    @Override
    void resetDrawable() {
        mIcon.setImageDrawable(mIconDrawable);
    }

    @Override
    void hidden() {
        resetDrawable();
        mLayout.setVisibility(View.GONE);
    }

    @Override
    void show() {
        mLayout.setVisibility(View.VISIBLE);
        switchDrawable();
    }
}