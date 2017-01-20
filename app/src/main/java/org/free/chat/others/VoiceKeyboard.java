package org.free.chat.others;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/1/13.
 */
public class VoiceKeyboard<T extends LinearLayout, V extends View, K extends View, M extends ImageView> extends AbsCustomKeyboard {

    /**
     * @param activity
     * @param editText      输入框，没有，代替的是点击开始录音的按钮
     * @param contentView   上面布局界面
     * @param emotionButton 点击显示表情界面的按钮
     */
    public VoiceKeyboard(Activity activity, View editText, View contentView, ImageView emotionButton) {
        super(activity, null, editText, contentView, emotionButton);
    }

    @Override
    public void hideKeyboard() {
        beforeKeyboardHide(this);
       if (mEditText != null) {
           mEditText.setVisibility(View.GONE);
       }
        afterKeyboardHide(this);
    }

    @Override
    public void showKeyboard() {

    }

    @Override
    public void onEmotionButtonClicked(ImageView btn, AbsCustomKeyboard keyBoard) {
        if (mEditText.isShown()) {
            beforeKeyboardHide(this);
            mEditText.setVisibility(View.GONE);
            afterKeyboardHide(this);
        } else {
            beforeKeyboardShow(this);
            mEditText.setVisibility(View.VISIBLE);
            afterKeyboardShow(this);
        }
    }

    @Override
    public void onContentViewClicked(View view, AbsCustomKeyboard keyBoard) {

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
        return mEditText.isShown();
    }
}
