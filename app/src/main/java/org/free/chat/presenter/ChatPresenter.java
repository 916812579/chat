package org.free.chat.presenter;

import android.content.Context;

import com.google.gson.Gson;

import org.free.chat.bean.ChatContent;
import org.free.chat.bean.ContactInfo;
import org.free.chat.bean.PageInfo;
import org.free.chat.bean.Result;
import org.free.chat.bean.User;
import org.free.chat.model.ChatModel;
import org.free.chat.model.LoginModel;
import org.free.chat.model.impl.ChatModelImpl;
import org.free.chat.model.impl.LoginModelImpl;
import org.free.chat.utils.CallBack;
import org.free.chat.utils.SharedPreferencesUtils;
import org.free.chat.view.ChatView;
import org.free.chat.view.LoginView;
import org.jivesoftware.smack.packet.Message;

import java.util.Date;

/**
 * Created by Administrator on 2016/12/30.
 */
public class ChatPresenter {

    private final static String TAG = "LoginPresenter";

    private ChatView mView;
    private ChatModel mModel;

    public ChatPresenter(ChatView view) {
        this.mView = view;
        mModel = new ChatModelImpl((Context)view);
    }

    public void sendMsg(String to, String text) {
        Message msg = new Message();
        msg.setBody(text);
        msg.setTo(to);
        mModel.sendChatMsg(msg);
        ContactInfo contactInfo = mModel.selectByTo(to);
        contactInfo.lastMsg = text;
        contactInfo.lastMsgTime = new Date();
        mModel.updateContactInfo(contactInfo);
    }

    public void insertChatContent(ChatContent content) {
        mModel.insert(content);
    }

    public PageInfo<ChatContent> query(String to, int page) {
        PageInfo<ChatContent> pageInfo = mModel.selectByPage(to, page);
        return pageInfo;
    }

    public void updateChatContentRead(String to) {
        mModel.updateContactInfoRead(to);
    }


}
