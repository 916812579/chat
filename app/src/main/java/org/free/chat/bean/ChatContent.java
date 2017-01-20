package org.free.chat.bean;

import java.util.Date;

/**
 * Created by Administrator on 2017/1/12.
 */
public class ChatContent {

    // 已读、未读
    public static final int READ = 1;
    public static final int UNREAD = 2;

    // 接收的消息、发送的消息
    public static final int SEND = 1;
    public static final int RECIVED = 2;

    // 消息的类型
    public static final int TEXT = 1;
    public static final int IMAGE = 2;
    public static final int VOICE = 3;


    public int id;
    public String toName;
    public String content;
    public int msgType;
    public int sendType;
    public String path;
    public Date msgTime;
    public boolean isRead;
    public float length;
}
