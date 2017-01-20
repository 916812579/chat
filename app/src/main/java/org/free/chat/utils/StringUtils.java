package org.free.chat.utils;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by Administrator on 2016/12/30.
 */
public class StringUtils {

    public static boolean isBlank(String str) {
        if (str == null || TextUtils.isEmpty(str) || TextUtils.isEmpty(str.trim())) {
            return true;
        }
        return false;
    }

    public static boolean isBlank(EditText et) {
        String str = et.getText().toString();
        return isBlank(str);
    }
}
