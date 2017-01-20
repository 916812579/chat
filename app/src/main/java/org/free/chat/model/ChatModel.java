package org.free.chat.model;

import org.free.chat.bean.ChatContent;
import org.free.chat.bean.ContactInfo;
import org.free.chat.bean.PageInfo;
import org.jivesoftware.smack.packet.Message;

import java.util.List;

/**
 * Created by Administrator on 2017/1/10.
 */
public interface ChatModel {

    void sendChatMsg(Message msg);

    void batchInsert(List<ChatContent> contents);

    void insert(ChatContent contents);

    PageInfo<ChatContent> selectByPage(String to, int page, int size);

    PageInfo<ChatContent> selectByPage(String to, int page);

    int selectTotalCount(String to);

    void updateContactInfo(ContactInfo msg);

    void updateContactInfoRead(String to);

    ContactInfo selectByTo(String to);
}
