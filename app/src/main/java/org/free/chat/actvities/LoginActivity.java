package org.free.chat.actvities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.free.chat.R;
import org.free.chat.bean.User;
import org.free.chat.presenter.LoginPresenter;
import org.free.chat.utils.SharedPreferencesUtils;
import org.free.chat.utils.StringUtils;
import org.free.chat.view.LoginView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;


public class LoginActivity extends BaseActivity implements LoginView {

    @BindView(R.id.et_account)
    EditText mAccount;
    @BindView(R.id.et_password)
    EditText mPassword;
    @BindView(R.id.btn_login)
    Button mLoginButton;
    @BindView(R.id.cp_login_progressbar)
    CircularProgressView loginProgressView;


    LoginPresenter presenter = new LoginPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mTitle.setText(R.string.login);
        String userStr = SharedPreferencesUtils.getInstance().getParam(this, "user", "{}");
        // new TypeToken<List<User>>(){}.getType()
        User user = new Gson().fromJson(userStr, User.class);
        if (user != null && !StringUtils.isBlank(user.account)&& !StringUtils.isBlank(user.password)) {
            setAccount(user.account);
            setPassword(user.password);
            mLoginButton.callOnClick();
        }
        success();
    }


    @Override
    public String getAccount() {
        return mAccount.getText().toString();
    }

    @Override
    public void setAccount(String account) {
        mAccount.setText(account);
    }

    @Override
    public String getPassword() {
        return mPassword.getText().toString();
    }

    @Override
    public void setPassword(String password) {
        mPassword.setText(password);
    }

    @OnClick(R.id.btn_login)
    public void login() {
        presenter.login();
        hideKeyBoard();
    }

    @Override
    public boolean validate() {
        return !(StringUtils.isBlank(mAccount) || StringUtils.isBlank(mPassword));
    }

    @Override
    public void showLoginView() {
        loginProgressView.setVisibility(View.VISIBLE);
        loginProgressView.startAnimation();
        mLoginButton.setVisibility(View.GONE);
    }

    @Override
    public void hideLoginView() {
        loginProgressView.setVisibility(View.GONE);
        loginProgressView.startAnimation();
        mLoginButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @OnTextChanged({R.id.et_account, R.id.et_password})
    public void onTextChanged() {
        mLoginButton.setEnabled(validate());
    }
}
