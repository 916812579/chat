package org.free.chat.pages;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.free.chat.R;
import org.free.chat.actvities.BaseActivity;
import org.free.chat.actvities.ChatActivity;
import org.free.chat.adapters.RichAdapter;
import org.free.chat.bean.ContactInfo;
import org.free.chat.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeImageView;

/**
 * Created by Administrator on 2017/1/6.
 */
public class ChatsPage extends Page {

    ListView mChats;
    List<ContactInfo> mDatas;
    RichAdapter<ContactInfo> mAdapter;

    public ChatsPage(BaseActivity context, MainPresenter presenter) {
        super(context, presenter);
    }

    @Override
    public void initView() {
        mView = View.inflate(mContext, R.layout.page_chats, null);
        mChats = (ListView) mView.findViewById(R.id.lv_chats);
        mAdapter = new RichAdapter<ContactInfo>(mContext, mDatas) {
            @Override
            public void setValues(ContactInfo chatMsg, BGABadgeImageView imageView, TextView title, TextView extraTitle, TextView subTitle) {
                title.setText(chatMsg.toName);
                subTitle.setText(chatMsg.lastMsg);
                if (chatMsg.count > 0) {
                    imageView.showCirclePointBadge();
                }
            }
        };
        mChats.setAdapter(mAdapter);
        mChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactInfo msg = mDatas.get(position);
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("to", msg.toName);
                mContext.startActivity(intent);
            }
        });
        initData();
    }

    @Override
    public void refresh() {
        mContext.load();
        loadData();
    }

    private void initData() {
        loadData();
    }

    private void loadData() {
        mDatas = mPresenter.loadAllChats();
        mContext.success();
        if (mDatas == null || mDatas.size() == 0) {
            mContext.setEmptyText(R.string.empty_chats);
            mContext.empty();
            mDatas = new ArrayList<>();
        }
        List<ContactInfo> adapterDatas = mAdapter.getDatas();
        adapterDatas.clear();
        adapterDatas.addAll(mDatas);
        mAdapter.notifyDataSetChanged();
    }


}
