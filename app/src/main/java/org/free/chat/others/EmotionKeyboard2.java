package org.free.chat.others;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/1/13.
 */
public abstract class EmotionKeyboard2<T extends LinearLayout, V extends View, K extends View, M extends ImageView> extends AbsCustomKeyboard {



    /**
     * @param activity
     * @param emotionLayout 表情界面
     * @param editText      输入框
     * @param contentView   上面布局界面
     * @param emotionButton 点击显示表情界面的按钮
     */
    public EmotionKeyboard2(Activity activity, T emotionLayout, V editText, K contentView, M emotionButton) {
        super(activity, emotionLayout, editText, contentView, emotionButton);

    }

    @Override
    protected void initView() {
        super.initView();
        onEditTextClick();
    }

    private void onEditTextClick() {
        if (mEditText != null && mEditText instanceof EditText) {
            mEditText.requestFocus();
            mEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mEmotionLayout.isShown()) {
                        lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                        hideEmotionLayout(false);//隐藏表情布局，显示软件盘

                        //软件盘显示后，释放内容高度
                        mEditText.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                unlockContentHeightDelayed();
                            }
                        }, 200L);
                    }
                    return false;
                }
            });
        }
    }

    /**
     * 不调整bar的高度
     *
     * @param flag
     */
    public void hideKeyboard(boolean flag) {
        beforeKeyboardHide(this);
        if (mEmotionLayout != null) { // 隐藏表情电盘
            mEmotionLayout.setVisibility(View.GONE);
        }
        if (isSoftInputShown()) {
            hideSoftInput(); // 隐藏软键盘
        }
        afterKeyboardHide(this);
        resetEmotionBtnDrawable();
        if (flag) {
            unlockContentHeightDelayed();
        }
    }

    public void hideKeyboard() {
        beforeKeyboardHide(this);
        if (mEmotionLayout != null) { // 隐藏表情电盘
            mEmotionLayout.setVisibility(View.GONE);
        }
        if (isSoftInputShown()) {
            hideSoftInput(); // 隐藏软键盘
        }
        afterKeyboardHide(this);
        resetEmotionBtnDrawable();

        unlockContentHeightDelayed();

    }

    @Override
    public void showKeyboard() {
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

    /**
     * 隐藏表情布局
     *
     * @param showSoftInput 是否显示软件盘
     */
    private void hideEmotionLayout(boolean showSoftInput) {
        beforeKeyboardShow(this);
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            } else {
                unlockContentHeightDelayed();
                hideSoftInput();
            }
            afterKeyboardHide(this);
        }
    }

    private void showEmotionLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = getKeyBoardHeight();
        }
        final int fSoftInputHeight = softInputHeight;
        hideSoftInput();
        beforeKeyboardShow(this);
        if (mEmotionLayout != null) {
            mEmotionLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEmotionLayout.getLayoutParams().height = fSoftInputHeight;
                    mEmotionLayout.setVisibility(View.VISIBLE);
                    afterKeyboardShow(EmotionKeyboard2.this);
                }
            }, 200);
        }

    }


    @Override
    public void onEmotionButtonClicked(ImageView btn, AbsCustomKeyboard keyBoard) {
        if (mEmotionLayout.isShown()) {
            beforeKeyboardHide(this);
            lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
            hideEmotionLayout(true);//隐藏表情布局，显示软件盘
            unlockContentHeightDelayed();//软件盘显示后，释放内容高度
            afterKeyboardHide(this);
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

    @Override
    public void onContentViewClicked(View view, AbsCustomKeyboard keyBoard) {
        hideKeyboard();
        resetEmotionBtnDrawable();
    }


    @Override
    public void beforeKeyboardHide(AbsCustomKeyboard keyBoard) {

    }

    @Override
    public void afterKeyboardHide(AbsCustomKeyboard keyBoard) {

    }

    @Override
    public void beforeKeyboardShow(AbsCustomKeyboard keyBoard) {

    }

    @Override
    public void afterKeyboardShow(AbsCustomKeyboard keyBoard) {

    }

    @Override
    public boolean isShow() {
        return isSoftInputShown() || mEmotionLayout.isShown();
    }

    public static interface OnEditTextClicked {

        void onClicked();
    }
}
