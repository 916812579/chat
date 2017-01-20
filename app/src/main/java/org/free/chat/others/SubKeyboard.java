package org.free.chat.others;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 自定义键盘
 */
public abstract class SubKeyboard {

    /**
     * 修改按钮的图片
     */
    public abstract void switchDrawable();

    /**
     * 重置按钮的图片
     */
    abstract void resetDrawable();

    /**
     * 隐藏
     */
    abstract void hidden();

    /**
     * 显示
     */
    abstract void show();
}
