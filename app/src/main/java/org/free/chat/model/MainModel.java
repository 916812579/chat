package org.free.chat.model;

import org.free.chat.bean.ContactInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/1/10.
 */
public interface MainModel {
    void insert(ContactInfo msg);
    void delete(int id);
    void update(ContactInfo msg);
    ContactInfo selectByTo(String to);
    List<ContactInfo> selectAll();
}
