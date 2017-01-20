package org.free.chat.others;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.free.chat.R;
import org.free.chat.utils.SharedPreferencesUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对键盘的封装，支持表情键盘
 * M 点击显示表情界面的图标
 */
public abstract class AbsCustomKeyboard<T extends LinearLayout, V extends View, K extends View, M extends ImageView> {

    private final static String TAG = "KeyboardWrapper";
    private static final String SHARE_PREFERENCE_NAME = "EmotionKeyboard";
    private static final String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "soft_input_height";

    protected Activity mActivity;
    protected InputMethodManager mInputManager;//软键盘管理类
    protected T mEmotionLayout; // 表情布局界面
    protected V mEditText;// 如果是表情键盘则代表EditText
    protected K mContentView;//内容布局view,即除了表情布局或者软键盘布局以外的布局，用于固定bar的高度，防止跳闪
    protected M mEmotionButton;//内容布局view,即除了表情布局或者软键盘布局以外的布局，用于固定bar的高度，防止跳闪


    private static int mScreenHeight;

    protected Drawable mEmotionBtnDrawable;



    /**
     * @param activity
     * @param emotionLayout 表情界面
     * @param editText      输入框
     * @param contentView   上面布局界面
     * @param emotionButton 点击显示表情界面的按钮
     */
    public AbsCustomKeyboard(Activity activity, T emotionLayout, V editText, K contentView, M emotionButton) {
        mActivity = activity;
        mEmotionLayout = emotionLayout;
        mEditText = editText;
        mContentView = contentView;
        mEmotionButton = emotionButton;
        mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mEmotionBtnDrawable = emotionButton.getDrawable();
        initView();
    }

    /**
     * 负责对界面的初始化
     */
    protected void initView() {

        // 内容界面被点击
        onContentClicked();
        onEmotionButtonClicked();
    }

    /**
     * 内容界面被点击
     * 可以处理是否隐藏电盘
     */
    private void onContentClicked() {
        if (mContentView != null) {
           /**
            mContentView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            onContentViewClicked(mContentView, AbsCustomKeyboard.this);
                            return true;
                    }
                    return false;
                }
            });**/
        }
    }

    private void onEmotionButtonClicked() {
        if (mEmotionButton != null) {
            mEmotionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onEmotionButtonClicked((ImageView) v, AbsCustomKeyboard.this);

                }
            });
        }
    }

    /**
     * 重置mEmotionButton的图片
     */
    public void resetEmotionBtnDrawable() {
        if (mEmotionButton != null && mEmotionBtnDrawable != null) {
            mEmotionButton.setImageDrawable(mEmotionBtnDrawable);
        }
    }

    /**
     * 隐藏电盘
     */
    public abstract void hideKeyboard();

    /**
     * 显示电盘
     */
    public abstract void showKeyboard();


    /**
     * 编辑框获取焦点，并显示软件盘
     */
    protected void showSoftInput() {
        if (mEmotionLayout != null) {
            mEmotionLayout.requestFocus();
            mEmotionLayout.post(new Runnable() {
                @Override
                public void run() {
                    mEditText.requestFocus();
                    mInputManager.showSoftInput(mEditText, 0);
                }
            });
        }
    }

    /**
     * 隐藏软件盘
     */
    protected void hideSoftInput() {
        if (mEditText != null) {
            mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }

    }

    /**
     * 是否显示软件盘
     *
     * @return
     */
    public boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    /**
     * 获取软件盘的高度
     *
     * @return
     */
    protected int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        mScreenHeight = screenHeight;
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;

        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }

        //存一份到本地
        if (softInputHeight > 0) {
            int oldHeight = SharedPreferencesUtils.getInstance().getParam(mActivity, SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 0);
            // 判断如果电盘的高度发生变化则调用onKeyboardHeightChanged
            if (softInputHeight != oldHeight || oldHeight == 0) {
                SharedPreferencesUtils.getInstance().setParam(mActivity, SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight);
            }
        }

        if (softInputHeight <= 0) {
            Log.w(TAG, "EmotionKeyboard--Warning: value of softInputHeight is below zero!");
            // 如果电盘没显示，则默认高度为屏幕的一半
            // softInputHeight = screenHeight / 2;
        }
        return softInputHeight;
    }


    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 获取软键盘高度，由于第一次直接弹出表情时会出现小问题，787是一个均值，作为临时解决方案
     *
     * @return
     */
    public int getKeyBoardHeight() {
        int height = SharedPreferencesUtils.getInstance().getParam(mActivity, SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, mScreenHeight / 2);
        Log.d(TAG, "height :" + height);
        return height;
    }

    /**
     * 调整界面的高度
     */
    protected void unlockContentHeightDelayed() {
        if (mEditText != null) {
            mEditText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
                }
            }, 200L);
        }

    }

    /**
     * 锁定内容输入框高度，防止跳闪
     */
    protected void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        // 处理第一次如果点击是表情时mContentView的高度
        int delta = 0;
        int softInputHeight = getSupportSoftInputHeight();
        if (mEmotionLayout.getHeight() != softInputHeight) {
            delta = mEmotionLayout.getHeight() - getKeyBoardHeight();
        }
        params.height = mContentView.getHeight() + delta;
        params.weight = 0.0F;
    }


    /**
     * @param btn      显示电盘的图片被点击
     * @param keyBoard MutiKeyboard
     */
    public abstract void onEmotionButtonClicked(ImageView btn, AbsCustomKeyboard keyBoard);

    /**
     * 内容界面被点击后回调的方法，比如说聊天界面上的listView
     *
     * @param k        聊天界面，如listView
     * @param keyBoard MutiKeyboard
     */
    public abstract void onContentViewClicked(K k, AbsCustomKeyboard keyBoard);


    /**
     * 电盘隐藏前回调
     *
     * @param keyBoard
     */
    public abstract void beforeKeyboardHide(AbsCustomKeyboard keyBoard);

    /**
     * 电盘隐藏后回调
     *
     * @param keyBoard
     */
    public abstract void afterKeyboardHide(AbsCustomKeyboard keyBoard);


    public abstract void beforeKeyboardShow(AbsCustomKeyboard keyBoard);


    public abstract void afterKeyboardShow(AbsCustomKeyboard keyBoard);

    public abstract boolean isShow();


    public boolean isEmotionKeyBoardShow() {
        if (mEmotionLayout != null) {
            return mEmotionLayout.isShown();
        }
        return false;
    }

    public M getEmotionButton() {
        return mEmotionButton;
    }
}
