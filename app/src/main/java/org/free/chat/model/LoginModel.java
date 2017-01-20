package org.free.chat.model;

import org.free.chat.utils.CallBack;

/**
 * Created by Administrator on 2016/12/30.
 */
public interface LoginModel {

    void login(String account, String password, CallBack callBack);

}
