package org.free.chat.presenter;

import android.content.Context;

import org.free.chat.bean.ContactInfo;
import org.free.chat.model.MainModel;
import org.free.chat.model.impl.MainModelImpl;
import org.free.chat.view.MainView;

import java.util.List;

/**
 * Created by Administrator on 2016/12/30.
 */
public class MainPresenter {

    private final static String TAG = "ChatPresenter";

    private MainView mView;
    private MainModel mModel;

    public MainPresenter(MainView view) {
        mView = view;
        mModel = new MainModelImpl((Context) view);
    }


    public List<ContactInfo> loadAllChats() {
        return mModel.selectAll();
    }

    // 插入消息
    public void insertMsg(ContactInfo msg) {
        mModel.insert(msg);
    }

    public void updateMsg(ContactInfo msg) {
        if (mModel.selectByTo(msg.toName) != null) {
            mModel.update(msg);
        } else {
            mModel.insert(msg);
        }
    }


}
