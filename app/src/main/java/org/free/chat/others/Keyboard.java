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
import android.widget.LinearLayout;
import android.widget.ListView;

import org.free.chat.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义电盘
 */
public class Keyboard {

    private final static String TAG = "Keyboard";
    private static final String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "soft_input_height";

    protected Activity mActivity;
    private EditText mEditText;
    private ListView mContentView;
    private List<SubKeyboard> mSubKeyboards;
    protected InputMethodManager mInputManager;//软键盘管理类


    private static int mScreenHeight;
    private boolean mIsContentViewHeightFixed;

    private SubKeyboard mCurrentKeyboard;


    public Keyboard(Activity activity, EditText editText, ListView contentView) {
        mActivity = activity;
        mEditText = editText;
        mContentView = contentView;
        mSubKeyboards = new ArrayList<>();
        mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setOnEditTextClicked();
    }

    /**
     * 输入框被点击时回调，此时应该是显示系统的软键盘，我们自定义的键盘退出
     *
     * @return
     */
    public Keyboard setOnEditTextClicked() {
        if (mEditText != null) {
            mEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 判断是否存在我们自定义的键盘
                    if (mCurrentKeyboard != null) {
                        mCurrentKeyboard.hidden();
                    }
                    if (mCurrentKeyboard instanceof ResizeHeightSubKeyboard) {
                        lockContentHeight();
                    }
                    showSoftKeyboard();
                    if (mCurrentKeyboard instanceof ResizeHeightSubKeyboard) {
                        unlockContentHeightDelayed();
                    }
                    return true;
                }
            });
        }
        return this;
    }

    public Keyboard addKeyboard(SubKeyboard keyboard) {
        mSubKeyboards.add(keyboard);
        return this;
    }


    public EditText getEditText() {
        return mEditText;
    }

    public void setEditText(EditText mEditText) {
        this.mEditText = mEditText;
    }

    public List<SubKeyboard> getSubKeyboards() {
        return mSubKeyboards;
    }

    public void setSubKeyboards(List<SubKeyboard> mSubKeyboards) {
        this.mSubKeyboards = mSubKeyboards;
    }


    /**
     * 获取软件盘的高度
     *
     * @return
     */
    public int getSoftKeyboardHeight() {
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

    public int getKeyboardHeight() {
        int height = SharedPreferencesUtils.getInstance().getParam(mActivity, SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, mScreenHeight / 2);
        Log.d(TAG, "height :" + height);
        return height;
    }


    /**
     * 调整界面的高度
     */
    protected void unlockContentHeightDelayed() {
        mIsContentViewHeightFixed = false;
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
        mIsContentViewHeightFixed = true;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    /**
     * 软键盘是否显示
     *
     * @return
     */
    public boolean isSoftKeyboardShown() {
        return getSoftKeyboardHeight() != 0;
    }


    /**
     * 显示软键盘
     */
    public void showSoftKeyboard() {
        if (mActivity != null) {
            mEditText.requestFocus();
            mEditText.post(new Runnable() {
                @Override
                public void run() {
                    mEditText.requestFocus();
                    mInputManager.showSoftInput(mEditText, 0);
                }
            });
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard() {
        if (mEditText != null) {
            mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }

    }

    /**
     * 内容界面的高度是否被固定
     *
     * @return
     */
    public boolean isContentViewHeightFixed() {
        return mIsContentViewHeightFixed;
    }

    /**
     * 显示当前自定义的键盘
     *
     * @param keyboard
     */
    public void showCurrentKeyboard(SubKeyboard keyboard) {
        // 首先判断当前是否存在我们自定义的键盘,如果存在并且不是同一个
        if (mCurrentKeyboard != null && mCurrentKeyboard != keyboard) {
            // 如果都是ResizeHeightSubKeyboard类型则不需要调整内容的高度，否则需要调整内容的高度
            if ((mCurrentKeyboard instanceof ResizeHeightSubKeyboard || mCurrentKeyboard instanceof FixedHeightSubKeyboard) && keyboard instanceof ResizeHeightSubKeyboard) {
                lockContentHeight();
                mEditText.setVisibility(View.VISIBLE);
            }
            if (mCurrentKeyboard instanceof ResizeHeightSubKeyboard && keyboard instanceof FixedHeightSubKeyboard) {
                hideSoftKeyboard();
                mEditText.setVisibility(View.GONE);
            }

            // 隐藏前一个键盘
            mCurrentKeyboard.hidden();
            mCurrentKeyboard = keyboard;
            mCurrentKeyboard.show();
            if (mCurrentKeyboard instanceof ResizeHeightSubKeyboard) {
                unlockContentHeightDelayed();
            }
        } else if (mCurrentKeyboard != null && mCurrentKeyboard == keyboard) { // 系统键盘和我们自己定义键盘的切换
            mCurrentKeyboard.hidden();
            if (keyboard instanceof FixedHeightSubKeyboard) {
                mEditText.setVisibility(View.VISIBLE);
            } else {
                lockContentHeight();
            }
            showSoftKeyboard();
            if (keyboard instanceof ResizeHeightSubKeyboard) {
                unlockContentHeightDelayed();
            }
            mCurrentKeyboard = null;
        } else {
            if (keyboard instanceof ResizeHeightSubKeyboard) {
                lockContentHeight();
            }

            if (keyboard instanceof FixedHeightSubKeyboard) {
                mEditText.setVisibility(View.GONE);
            }
            if (isSoftKeyboardShown()) {
                hideSoftKeyboard();
            }
            mCurrentKeyboard = keyboard;
            mCurrentKeyboard.show();
            if (mCurrentKeyboard instanceof ResizeHeightSubKeyboard) {
                unlockContentHeightDelayed();
            }
        }
    }

}
