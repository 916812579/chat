package org.free.chat.bean;

import org.free.chat.ChatApplication;
import org.free.chat.R;


/**
 * Created by Administrator on 2016/12/30.
 */
public class Result {
    // 连接服务器异常
    public static final int CONNECT_ERROR = 1000;

    // 登录异常
    public static final int LOGIN_ERROR = 1001;
    public static final int LOGIN_USERNAME_OR_PASSWORD_ERROR = 1002;
    private int code;
    private String msg;

    public Result(int code) {
        this(code, null);
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return ChatApplication.getStringRes(R.string.error_code) + code + ", " + msg;
    }
}
