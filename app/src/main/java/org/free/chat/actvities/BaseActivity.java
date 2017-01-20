package org.free.chat.actvities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;


import org.free.chat.R;
import org.free.chat.common.Interactable;
import org.free.chat.view.BaseView;

import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/12/29.
 */
public class BaseActivity extends AppCompatActivity implements Interactable {

    private static final String TAG = "BaseActivity";

    @BindView(R.id.activity_loading_view)
    LinearLayout loadingView;
    @BindView(R.id.activity_error_view)
    LinearLayout errorView;
    @BindView(R.id.activity_empty_view)
    LinearLayout emptyView;
    FrameLayout successView;
    @BindView(R.id.activity_loading_progressbar)
    CircularProgressView progressBar;
    @BindView(R.id.activity_top)
    RelativeLayout header;
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_empty_view)
    TextView mEmptyTextView;

    @BindView(R.id.ll_toolbar)
    LinearLayout mToolbar;

    @BindView(R.id.iv_cross)
    ImageView cross;



    View currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        successView = (FrameLayout) findViewById(R.id.activity_success_view);
        EventBus.getDefault().register(this);
    }

    private void startLoading() {
        currentView = loadingView;
        load();
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = View.inflate(this, layoutResID, null);
        successView.addView(view);
        ButterKnife.bind(this);
        startLoading();
    }


    @Override
    public void success() {
        switchView(successView);
    }

    private void switchView(View view) {
        if (currentView != null) {
            currentView.setVisibility(View.GONE);
        }
        currentView = view;
        currentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void failed() {
        switchView(errorView);
    }

    @Override
    public void empty() {
        switchView(emptyView);
    }

    public void setEmptyText(int resId) {
        mEmptyTextView.setText(resId);
    }

    @Override
    public void setFailImage(int resId) {

    }

    @Override
    public void setFailText(int resId) {

    }

    @Override
    public void reload() {

    }

    @Override
    public void load() {
        switchView(loadingView);
    }

    @Override
    public void loadFinished() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Object event) {

    }
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }


    /**
     * 关闭电盘
     */
    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public void showKeyBoard(EditText editText) {
        editText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }



    @OnClick(R.id.iv_cross)
    public void onCrossClicked() {

    }


}
