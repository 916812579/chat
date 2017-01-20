package org.free.chat.pages;

import android.content.Context;
import android.view.View;

import org.free.chat.actvities.BaseActivity;
import org.free.chat.presenter.MainPresenter;

/**
 * Created by Administrator on 2017/1/6.
 */
public abstract class Page {

    protected BaseActivity mContext;
    MainPresenter mPresenter;

    protected View mView;

    public Page(BaseActivity context, MainPresenter presenter) {
        this.mContext = context;
        this.mPresenter = presenter;
        initView();
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        this.mView = view;
    }

    public abstract void initView();

    public abstract void refresh();
}
