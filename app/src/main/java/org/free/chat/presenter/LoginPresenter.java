package org.free.chat.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.free.chat.bean.Result;
import org.free.chat.bean.User;
import org.free.chat.model.LoginModel;
import org.free.chat.model.impl.LoginModelImpl;
import org.free.chat.utils.CallBack;
import org.free.chat.utils.SharedPreferencesUtils;
import org.free.chat.utils.SmackUtils;
import org.free.chat.view.LoginView;

/**
 * Created by Administrator on 2016/12/30.
 */
public class LoginPresenter {

    private final static String TAG = "LoginPresenter";

    private LoginView mView;
    private LoginModel mModel;

    public LoginPresenter(LoginView view) {
        this.mView = view;
        mModel = new LoginModelImpl();
    }


    public void login() {
        final String account = mView.getAccount();
        final String password = mView.getPassword();
        mModel.login(account, password, new CallBack() {

            @Override
            public void onStart() {
                super.onStart();
                mView.showLoginView();
            }

            public void onSuccess() {
                User user = new User();
                user.account = account;
                user.password = password;
                SharedPreferencesUtils.getInstance().setParam((Context)mView, "user", new Gson().toJson(user));
                mView.startMainActivity();
            }

            public void onFailure(Result result) {
                mView.hideLoginView();
                mView.showToast(result.toString());
            }
        });
    }

}
