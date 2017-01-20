package org.free.chat.model.impl;

import org.free.chat.bean.Result;
import org.free.chat.model.LoginModel;
import org.free.chat.utils.CallBack;
import org.free.chat.utils.SmackUtils;

/**
 * Created by Administrator on 2017/1/3.
 */
public class LoginModelImpl implements LoginModel {

    @Override
    public void login(String account, String password, CallBack callBack) {
        callBack.onStart();
        SmackUtils.login(account, password, callBack);
    }
}
