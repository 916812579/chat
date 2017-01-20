package org.free.chat.others;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * 防止快速点击
 */
public class NoFastClickImageButton extends ImageButton {

    private long lastClickTime;
    private static final long SPAN = 500; // 两次点击允许的间隙时间

    public NoFastClickImageButton(Context context) {
        super(context);
    }

    public NoFastClickImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoFastClickImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NoFastClickImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean performClick() {
        long curClickTime = System.currentTimeMillis();
        if (curClickTime - lastClickTime > SPAN) {
            lastClickTime = curClickTime;
            return super.performClick();
        }
        return true;
    }
}
