package org.free.chat.pages;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import org.free.chat.R;
import org.free.chat.actvities.BaseActivity;
import org.free.chat.actvities.ChatActivity;
import org.free.chat.adapters.ImageTextAdapter;
import org.free.chat.bean.ContactInfo;
import org.free.chat.others.IndexableListView;
import org.free.chat.presenter.MainPresenter;
import org.free.chat.utils.CommonUtils;
import org.free.chat.utils.SmackUtils;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/1/6.
 */
public class ContactsPage extends Page {

    IndexableListView mContacts;
    List<RosterEntry> entries;
    ImageTextAdapter mAdapter;

    public ContactsPage(BaseActivity context, MainPresenter presenter) {
        super(context, presenter);
    }

    @Override
    public void initView() {
        mView = View.inflate(mContext, R.layout.page_contacts, null);
        mContacts = (IndexableListView) mView.findViewById(R.id.lv_contacts);
        mContacts.setFastScrollEnabled(true);
        initData();
        mContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RosterEntry entry =  entries.get(position);
                ContactInfo msg = new ContactInfo();
                msg.toName = entry.getName();
                mPresenter.updateMsg(msg);
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("to", msg.toName);
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public void refresh() {
        mContext.load();
        loadData();
    }

    private void initData() {
        loadData();
    }

    public void loadData() {
        entries = SmackUtils.getAllEntries();
        if (entries.size() <= 0) {
            mContext.empty();
            mContext.setEmptyText(R.string.empty_contacts);
        } else {
            mAdapter = new IndexAdapter(mContext, entries);
            mContacts.setAdapter(mAdapter);
        }
        mContext.success();
    }

    class IndexAdapter extends ImageTextAdapter implements SectionIndexer

    {
        private String[] sections;
        public IndexAdapter(Context context, List<RosterEntry> datas) {
            super(context, datas);
            sortData();
        }

        private void sortData() {
            Collections.sort(mDatas, new Comparator() {
                @Override
                public int compare(Object lhs, Object rhs) {
                    RosterEntry lhsEntry = (RosterEntry) lhs;
                    RosterEntry rhsEntry = (RosterEntry) rhs;
                    if (lhsEntry != null && rhsEntry != null) {
                        String lhsName = lhsEntry.getName();
                        String rhsName = rhsEntry.getName();
                        if (lhsName != null && rhsName != null) {
                            return CommonUtils.converterToFirstSpell(lhsName).compareTo(rhsName);
                        }
                    }
                    return 0;
                }
            });
        }

        protected void setValues(Object o, ImageView imageView, TextView textView) {
            RosterEntry rosterEntry = (RosterEntry)o;
            textView.setText(rosterEntry.getName());
        }

        @Override
        // #ABCDEFGHIJKLMNOPQRSTUVWXYZ
        public String[] getSections(){

            List<String> result = new ArrayList<>();
            for (Object o : mDatas) {
                RosterEntry entry = (RosterEntry)o;
                String name = entry.getName();
                String firstChar = CommonUtils.converterToFirstSpell(name);
                if (!result.contains(firstChar)) {
                    result.add(firstChar);
                }
            }
            sections = new String[result.size()];
            result.toArray(sections);
            return sections;
        }

        @Override
        // 返回的位置就是listview会跳转到的位置
        public int getPositionForSection(int sectionIndex) {
            if (sections != null) {
                String sectionChar = sections[sectionIndex];
                for (int i = 0; i < mDatas.size(); i++) {
                    RosterEntry entry = (RosterEntry)mDatas.get(i);
                    String name = entry.getName();
                    String firstChar = CommonUtils.converterToFirstSpell(name);
                    if (firstChar.toUpperCase().equals(sectionChar)) {
                        return i;
                    }
                }
            }

            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

    }

}
