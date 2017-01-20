package org.free.chat.view;

/**
 * Created by Administrator on 2016/12/30.
 */
public interface LoginView extends BaseView {
    String getAccount();
    void setAccount(String account);
    String getPassword();
    void setPassword(String password);

    void login();
    boolean validate();

    void showLoginView();

    void hideLoginView();

    void startMainActivity();

}
