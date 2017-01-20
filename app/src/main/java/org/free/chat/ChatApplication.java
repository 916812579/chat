package org.free.chat;

import android.app.Application;
import android.content.Context;

import org.free.chat.utils.SmackUtils;

/**
 * Created by Administrator on 2016/12/30.
 */
public class ChatApplication extends Application {
    private static ChatApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    /**
     * @return 返回上限文实例
     */
    public static Application getContext() {
        return mInstance;
    }

    /**
     * @param resId
     * @return 返回字符串资源
     */
    public static String getStringRes(int resId) {
        return getContext().getString(resId);
    }


}
