package org.free.chat.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2017/1/10.
 */
public class DBManager {
    private static  SQLiteHelper helper;
    private static  SQLiteDatabase db;

    /**
     * 打开数据库
     * @param context
     * @return
     */
    public static SQLiteDatabase openDatabase(Context context) {
        helper = new SQLiteHelper(context);
        db = helper.getWritableDatabase();
        return db;
    }

    /**
     * 关闭数据库
     */
    public static void closeDatabase() {
        if (db != null && db.isOpen()) {
            db.close();
            helper = null;
            db = null;
        }
    }
}
