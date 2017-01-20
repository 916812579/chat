package org.free.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/1/9.
 */
public class SharedPreferencesUtils {
    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "share_data";

    private static SharedPreferencesUtils mInstance = new SharedPreferencesUtils();

    private SharedPreferencesUtils(){

    }

    public static SharedPreferencesUtils getInstance() {
        return mInstance;
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public void setParam(Context context, String key, Object object) {

        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }

        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public <T>  T getParam(Context context, String key, T defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        Object result = null;
        if ("String".equals(type)) {
            result = sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            result = sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            result = sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            result = sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            result = sp.getLong(key, (Long) defaultObject);
        }
        return (T)result;
    }
}
