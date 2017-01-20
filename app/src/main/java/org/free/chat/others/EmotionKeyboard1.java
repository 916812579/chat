package org.free.chat.others;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
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

/**
 * description :源码来自开源项目https://github.com/dss886/Android-EmotionInputDetector
 * 本人仅做细微修改以及代码解析
 *
 */
public class EmotionKeyboard1 {

    private final static String TAG = "EmotionKeyboard";
    private static final String SHARE_PREFERENCE_NAME = "EmotionKeyboard";
    private static final String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "soft_input_height";

    private Activity mActivity;
    private InputMethodManager mInputManager;//软键盘管理类
    private View mEmotionLayout;//表情布局
    private EditText mEditText;//
    private View mContentView;//内容布局view,即除了表情布局或者软键盘布局以外的布局，用于固定bar的高度，防止跳闪

    private ImageView emotionButton;

    private static int mScreenHeight;

    private EmotionKeyboard1() {

    }

    /**
     * 外部静态调用
     *
     * @param activity
     * @return
     */
    public static EmotionKeyboard1 with(Activity activity) {
        EmotionKeyboard1 emotionInputDetector = new EmotionKeyboard1();
        emotionInputDetector.mActivity = activity;
        emotionInputDetector.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return emotionInputDetector;
    }

    /**
     * 绑定内容view，此view用于固定bar的高度，防止跳闪
     *
     * @param contentView
     * @return
     */
    public EmotionKeyboard1 bindToContent(View contentView) {
        mContentView = contentView;
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        if (mEmotionLayout.isShown() || isSoftInputShown()) {
                            interceptBackPress();
                            emotionButton.setImageResource(R.drawable.face_image);
                            return true;
                        }
                }
                return false;
            }
        });
        return this;
    }

    /**
     * 绑定编辑框
     *
     * @param editText
     * @return
     */
    public EmotionKeyboard1 bindToEditText(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mEmotionLayout.isShown()) {
                    lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    hideEmotionLayout(true);//隐藏表情布局，显示软件盘
                    //软件盘显示后，释放内容高度
                    mEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            unlockContentHeightDelayed();
                        }
                    }, 200L);
                    getSupportSoftInputHeight();
                }
                return false;
            }
        });
        return this;
    }

    /**
     * 绑定表情按钮
     *
     * @param emotionButton
     * @return
     */
    public EmotionKeyboard1 bindToEmotionButton(final ImageView emotionButton) {
        this.emotionButton = emotionButton;
        emotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout.isShown()) {
                    lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    hideEmotionLayout(true);//隐藏表情布局，显示软件盘
                    unlockContentHeightDelayed();//软件盘显示后，释放内容高度
                } else {
                    if (isSoftInputShown()) {//同上
                        lockContentHeight();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showEmotionLayout();//两者都没显示，直接显示表情布局
                    }
                }
            }
        });
        return this;
    }

    private void changeEmotionImage() {
        if (mEmotionLayout.isShown()) {
            emotionButton.setImageResource(R.drawable.keybord);
        } else {
            emotionButton.setImageResource(R.drawable.face_image);
        }
    }

    /**
     * 设置表情内容布局
     *
     * @param emotionView
     * @return
     */

    public EmotionKeyboard1 setEmotionView(View emotionView) {
        mEmotionLayout = emotionView;
        return this;
    }

    public EmotionKeyboard1 build() {
        //设置软件盘的模式：SOFT_INPUT_ADJUST_RESIZE  这个属性表示Activity的主窗口总是会被调整大小，从而保证软键盘显示空间。
        //从而方便我们计算软件盘的高度
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //隐藏软件盘
        hideSoftInput();
        return this;
    }

    /**
     * 点击返回键时先隐藏表情布局
     *
     * @return
     */
    public boolean interceptBackPress() {
        boolean flag = false;
        if (mEmotionLayout.isShown() || isSoftInputShown()) {
            flag = true;
            if (mEmotionLayout.isShown()) {
                hideEmotionLayout(false);
            }
            if (isSoftInputShown()) {
                hideSoftInput();
            }
            unlockContentHeightDelayed();
        }
        return flag;
    }


    private void showEmotionLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = getKeyBoardHeight();
        }
        final int fSoftInputHeight = softInputHeight;
        hideSoftInput();
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                mEmotionLayout.getLayoutParams().height = fSoftInputHeight;
                mEmotionLayout.setVisibility(View.VISIBLE);
                changeEmotionImage();
            }
        }, 500);

    }

    /**
     * 隐藏表情布局
     *
     * @param showSoftInput 是否显示软件盘
     */
    private void hideEmotionLayout(boolean showSoftInput) {
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            } else {
                unlockContentHeightDelayed();
                hideSoftInput();
            }
            changeEmotionImage();
        }
    }

    /**
     * 锁定内容输入框高度，防止跳闪
     */
    private void lockContentHeight() {
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
     * 释放被锁定的内容高度
     */
    private void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
            }
        });
    }

    /**
     * 隐藏软件盘
     */
    private void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 是否显示软件盘
     *
     * @return
     */
    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    /**
     * 获取软件盘的高度
     *
     * @return
     */
    private int getSupportSoftInputHeight() {
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
     * 键盘高度发生变化 TODO
     */
    private void onKeyboardHeightChanged() {

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

}
