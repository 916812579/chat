package org.free.chat.actvities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import org.free.chat.R;
import org.free.chat.pages.ChatsPage;
import org.free.chat.pages.ContactsPage;
import org.free.chat.pages.Page;
import org.free.chat.presenter.MainPresenter;
import org.free.chat.view.MainView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.badgeview.BGABadgeRadioButton;
import cn.bingoogolapple.badgeview.BGABadgeView;

public class MainActivity extends BaseActivity implements MainView {

    @BindView(R.id.brb_chats)
    BGABadgeRadioButton chatBadgeView;

    @BindView(R.id.brb_contacts)
    BGABadgeRadioButton contactsBadgeView;

    @BindView(R.id.brb_discover)
    BGABadgeRadioButton discoverBadgeView;

    @BindView(R.id.brb_me)
    BGABadgeRadioButton meBadgeView;

    @BindView(R.id.vp_content)
    ViewPager mViewPager;

    private MainPageViewAdapter mAdapter;
    private List<Page> pages;

    private MainPresenter mPresenter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPresenter = new MainPresenter(this);
        mToolbar.setVisibility(View.VISIBLE);
        mTitle.setText(R.string.wechat);
        cross.setVisibility(View.VISIBLE);
        chatBadgeView.showTextBadge(12 + "");
        pages = new ArrayList<>();
        // success();
        // 会话
        pages.add(new ChatsPage(this, mPresenter));
        // 通讯录
        pages.add(new ContactsPage(this, mPresenter));

        pages.add(new ContactsPage(this, mPresenter));
        pages.add(new ContactsPage(this, mPresenter));
        mAdapter = new MainPageViewAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BGABadgeRadioButton button = (BGABadgeRadioButton) mToolbar.findViewWithTag(position + "");
                if (button != null) {
                    button.setChecked(true);
                    pages.get(position).refresh();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        chatBadgeView.setChecked(true);
    }

    @OnClick({R.id.brb_chats, R.id.brb_contacts, R.id.brb_discover, R.id.brb_me})
    public void radioButtonOnclick(BGABadgeRadioButton button) {
        int curCount = Integer.parseInt((String)button.getTag());
        mViewPager.setCurrentItem(curCount, true);
        pages.get(curCount).refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pages.get(mViewPager.getCurrentItem()).refresh();
    }

    class MainPageViewAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = pages.get(position).getView();
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(pages.get(position).getView());
        }
    }

    @Override
    public void onCrossClicked() {
        super.onCrossClicked();
    }
}
