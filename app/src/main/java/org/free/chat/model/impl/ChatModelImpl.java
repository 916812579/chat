package org.free.chat.model.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.free.chat.bean.ChatContent;
import org.free.chat.bean.ContactInfo;
import org.free.chat.bean.PageInfo;
import org.free.chat.model.ChatModel;
import org.free.chat.utils.DBManager;
import org.free.chat.utils.SmackUtils;
import org.jivesoftware.smack.packet.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/10.
 */
public class ChatModelImpl implements ChatModel {

    private Context mContext;

    public ChatModelImpl(Context context) {
        mContext = context;
    }

    public ChatModelImpl() {

    }

    @Override
    public void sendChatMsg(Message msg) {
        try {
            SmackUtils.sendChatMessage(msg);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public void batchInsert(List<ChatContent> contents) {
        SQLiteDatabase db = DBManager.openDatabase(mContext);
        db.beginTransaction();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = "insert into t_chat_content(toName, content, msgType, sendType, path, isRead, length, msgTime) values(?, ?, ?, ?, ?, ?, ?, ?)";
            for (ChatContent content : contents) {
                db.execSQL(sql, new Object[]{content.toName, content.content, content.msgType, content.sendType, content.path, content.isRead ? ChatContent.READ : ChatContent.UNREAD, content.length, df.format(content.msgTime)});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {

        } finally {
            db.endTransaction();
            DBManager.closeDatabase();
        }
    }

    @Override
    public void insert(ChatContent content) {
        List<ChatContent> contents = new ArrayList<>(1);
        contents.add(content);
        batchInsert(contents);
    }


    /**
     * SQL:Select * From TABLE_NAME Limit 9 Offset 10;
     * 表示从TABLE_NAME表获取数据，跳过10行，取9行
     */
    @Override
    public PageInfo<ChatContent> selectByPage(String to, int page, int size) {
        SQLiteDatabase db = DBManager.openDatabase(mContext);
        PageInfo<ChatContent> pageInfo = new PageInfo<>();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sql = "select _id, toName, content, msgType, sendType, path, isRead, length, msgTime from t_chat_content where toName = ? order by msgTime asc limit ?, ?";
            int totalCount = selectTotalCount(to);
            List<ChatContent> contents = new ArrayList<>();
            pageInfo.setTotal(totalCount);
            pageInfo.setCurrent(page);
            pageInfo.setPageSize(size);
            pageInfo.setDatas(contents);
            if (page == 0) {
                page = pageInfo.getTotalPage();
            }
            Cursor cursor = db.rawQuery(sql, new String[]{to, ((page - 1) * size) + "", size + ""});
            while (cursor.moveToNext()) {
                ChatContent chatContent = new ChatContent();
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String toName = cursor.getString(cursor.getColumnIndex("toName"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                int msgType = cursor.getInt(cursor.getColumnIndex("msgType"));
                int sendType = cursor.getInt(cursor.getColumnIndex("sendType"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                int isRead = cursor.getInt(cursor.getColumnIndex("isRead"));
                int length = cursor.getInt(cursor.getColumnIndex("length"));
                String msgTime = cursor.getString(cursor.getColumnIndex("msgTime"));
                chatContent.id = id;
                chatContent.toName = toName;
                chatContent.content = content;
                chatContent.sendType = sendType;
                chatContent.msgType = msgType;
                chatContent.path = path;
                chatContent.isRead = (isRead == ChatContent.READ ? true : false);
                chatContent.length = length;
                chatContent.msgTime = df.parse(msgTime);
                contents.add(chatContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBManager.closeDatabase();
        }
        return pageInfo;
    }

    @Override
    public PageInfo<ChatContent> selectByPage(String to, int page) {
        return selectByPage(to, page, PageInfo.DEFAULT_PAGE_SIZE);
    }


    @Override
    public int selectTotalCount(String to) {
        String sql = "select count(1) c from  t_chat_content where toName = ?";
        SQLiteDatabase db = DBManager.openDatabase(mContext);
        Cursor cursor = db.rawQuery(sql, new String[]{to});
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("c"));
        DBManager.closeDatabase();
        return count;
    }

    @Override
    public void updateContactInfo(ContactInfo msg) {
        SQLiteDatabase db = DBManager.openDatabase(mContext);
        String sql = "update t_contact_info set toName = ?, lastMsg = ?, count = ?, lastMsgTime = ? where _id = ?";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        db.execSQL(sql, new Object[]{msg.toName, msg.lastMsg, msg.count, df.format(new Date()), msg.id});
        DBManager.closeDatabase();
    }

    @Override
    public void updateContactInfoRead(String to) {
        SQLiteDatabase db = DBManager.openDatabase(mContext);
        String sql = "update t_chat_content set isRead = ? where toName = ?";
        db.execSQL(sql, new Object[]{ChatContent.READ, to});
        DBManager.closeDatabase();
    }

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
}
