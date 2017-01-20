package org.free.chat.utils;

import org.free.chat.bean.Result;

/**
 * Created by Administrator on 2016/12/30.
 */
public abstract class CallBack {

    public void onStart() {}
    public abstract void onSuccess();
    public abstract void onFailure(Result result);
    public void onFinished(Object t) {

    }
}
