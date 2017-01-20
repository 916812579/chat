package org.free.chat.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/1/10.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(Context context) {
        super(context, "db_chat", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 聊天联系人信息
        db.execSQL("CREATE TABLE IF NOT EXISTS t_contact_info" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, toName VARCHAR, lastMsg VARCHAR, count INTEGER, lastMsgTime VARCHAR)");

        // 和具体某个人的聊天信息 toName和谁的聊天信息  content 聊天内容  msgType消息类型 sendType 消息的收发类型 path 消息的存放路径 msgTime消息时间 isRead 是否已读 length 语音的话代表时长
        db.execSQL("CREATE TABLE IF NOT EXISTS t_chat_content" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, toName VARCHAR, content VARCHAR, msgType INTEGER, sendType INTEGER, path VARCHAR, isRead INTEGER, length DECIMAL, msgTime VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
