package org.free.chat.model.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.free.chat.bean.ContactInfo;
import org.free.chat.model.MainModel;
import org.free.chat.utils.DBManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/10.
 */
public class MainModelImpl implements MainModel {
    private Context mContext;

    public MainModelImpl(Context context) {
        mContext = context;

    }

    @Override
    public void insert(ContactInfo msg) {
        SQLiteDatabase db = DBManager.openDatabase(mContext);
        String sql = "insert into t_contact_info(toName, lastMsgTime) values(?, ?)";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        db.execSQL(sql, new Object[]{msg.toName, df.format(new Date())});
        DBManager.closeDatabase();
    }

    @Override
    public void delete(int id) {
        SQLiteDatabase db = DBManager.openDatabase(mContext);
        String sql = "delete from t_contact_info where _id = ?";
        db.execSQL(sql, new Object[]{id});
        DBManager.closeDatabase();
    }

    @Override
    public void update(ContactInfo msg) {
        SQLiteDatabase db = DBManager.openDatabase(mContext);
        String sql = "update t_contact_info set toName = ?, lastMsg = ?, count = ?, lastMsgTime = ? where _id = ?";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        db.execSQL(sql, new Object[]{msg.toName, msg.lastMsg, msg.count, df.format(new Date()), msg.id});
        DBManager.closeDatabase();
    }

    @Override
    public ContactInfo selectByTo(String to) {
        List<ContactInfo> result = new ArrayList<>();
        SQLiteDatabase db = DBManager.openDatabase(mContext);
        String sql = "select _id, lastMsg, lastMsgTime, count, toName from t_contact_info where toName = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{to});
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (cursor.moveToNext()) {
            ContactInfo msg = new ContactInfo();
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String lastMsg = cursor.getString(cursor.getColumnIndex("lastMsg"));
            String lastMsgTime = cursor.getString(cursor.getColumnIndex("lastMsgTime"));
            int count = cursor.getInt(cursor.getColumnIndex("count"));
            msg.id = id;
            msg.toName = to;
            msg.lastMsg = lastMsg;
            msg.count = count;
            try {
                msg.lastMsgTime = df.parse(lastMsgTime);
            } catch (Exception e) {

            }

            return msg;
        }
        return null;
    }

    @Override
    public List<ContactInfo> selectAll() {
        List<ContactInfo> result = new ArrayList<>();
        SQLiteDatabase db = DBManager.openDatabase(mContext);
        String sql = "select _id, toName, lastMsg, (select count(1) from t_chat_content b where b.toName = a.toName and b.isRead = 2) as count, lastMsgTime from t_contact_info a order by lastMsgTime desc";
        Cursor cursor = db.rawQuery(sql, null);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (cursor.moveToNext()) {
            ContactInfo msg = new ContactInfo();
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String to = cursor.getString(cursor.getColumnIndex("toName"));
            String lastMsg = cursor.getString(cursor.getColumnIndex("lastMsg"));
            String lastMsgTime = cursor.getString(cursor.getColumnIndex("lastMsgTime"));
            int count = cursor.getInt(cursor.getColumnIndex("count"));
            msg.id = id;
            msg.toName = to;
            msg.lastMsg = lastMsg;
            msg.count = count;
            try {
                msg.lastMsgTime = df.parse(lastMsgTime);
            } catch (Exception e) {

            }
            result.add(msg);
        }
        cursor.close();
        DBManager.closeDatabase();
        return result;
    }
}
