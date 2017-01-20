package org.free.chat.actvities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.free.chat.R;
import org.free.chat.adapters.EmotionPagerAdapter;
import org.free.chat.adapters.HorizontalRecyclerViewAdapter;
import org.free.chat.adapters.NoHorizontalScrollerVPAdapter;
import org.free.chat.bean.ChatContent;
import org.free.chat.bean.ImageModel;
import org.free.chat.bean.PageInfo;
import org.free.chat.bean.ValuePair;
import org.free.chat.fragments.EmotionCompleteFragment;
import org.free.chat.fragments.Fragment1;
import org.free.chat.fragments.FragmentFactory;
import org.free.chat.others.EmojiIndicatorView;
import org.free.chat.others.FixedHeightSubKeyboard;
import org.free.chat.others.Keyboard;
import org.free.chat.others.NoHorizontalScrollerViewPager;
import org.free.chat.others.ResizeHeightSubKeyboard;
import org.free.chat.presenter.ChatPresenter;
import org.free.chat.utils.AudioRecordButton;
import org.free.chat.utils.DisplayUtils;
import org.free.chat.utils.EmotionUtils;
import org.free.chat.utils.GlobalOnItemClickManagerUtils;
import org.free.chat.utils.MediaManager;
import org.free.chat.utils.SharedPreferencesUtils;
import org.free.chat.utils.SpanStringUtils;
import org.free.chat.utils.StringUtils;
import org.free.chat.view.ChatView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;

/**
 * Created by Administrator on 2017/1/10.
 */
public class ChatActivity extends BaseActivity implements ChatView {

    private final static String EMOTION_LAST_COUNT = "EMOTION_LAST_COUNT";
    private final static String TAG = "ChatActivity";

    @BindView(R.id.tv_send)
    TextView send;
    @BindView(R.id.et_msg)
    EditText msg;
    @BindView(R.id.iv_more)
    ImageButton more;
    @BindView(R.id.lv_chat_content)
    ListView mContent;
    @BindView(R.id.iv_emotion)
    ImageButton emotion;
    @BindView(R.id.vp_emotion_layout)
    NoHorizontalScrollerViewPager mViewPager;
    @BindView(R.id.rv_indicator)
    RecyclerView mRecyclerView;
    @BindView(R.id.art_recordButton)
    AudioRecordButton mRecordButton;
    @BindView(R.id.iv_record)
    ImageButton ivRecord;
    @BindView(R.id.ll_emotion_layout)
    LinearLayout mEmotionLayout;
    @BindView(R.id.ll_more_layout)
    LinearLayout mMoreLayout;
    @BindView(R.id.vp_more_layout)
    ViewPager mMoreViewPager;
    @BindView(R.id.eiv_more)
    EmojiIndicatorView emojiIndicatorView;


    private ResizeHeightSubKeyboard mEmotionKeyboard;
    private FixedHeightSubKeyboard mVoiceKeyboard;
    private ResizeHeightSubKeyboard mMoreKeyboard;
    private Keyboard mKeyboard;

    private HorizontalRecyclerViewAdapter emotionAdapter;

    private HorizontalRecyclerViewAdapter moreAdapter;

    List<Fragment> fragments;


    String to;

    ChatPresenter mPresenter;

    List<ChatContent> mContents = new ArrayList<>();

    PageInfo<ChatContent> mCurrentPage;

    int mNextPage;

    ChatAdapter mAdapter;

    // 音频动画View
    View voiceAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mPresenter = new ChatPresenter(this);
        to = getIntent().getStringExtra("to");
        mTitle.setText(to);
        success();
        fragments = new ArrayList<>();
        msg.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtils.isBlank(msg)) {
                    send.setVisibility(View.VISIBLE);
                    more.setVisibility(View.GONE);
                } else {
                    send.setVisibility(View.GONE);
                    more.setVisibility(View.VISIBLE);
                }
            }
        });

        mKeyboard = new Keyboard(this, msg, mContent);
        mEmotionKeyboard = new ResizeHeightSubKeyboard(mKeyboard, mEmotionLayout, emotion) {

            @Override
            public void switchDrawable() {
                mIcon.setImageDrawable(getResources().getDrawable(R.drawable.keybord));
            }
        };
        mMoreKeyboard = new ResizeHeightSubKeyboard(mKeyboard, mMoreLayout, more) {

            @Override
            public void switchDrawable() {
                mIcon.setImageDrawable(getResources().getDrawable(R.drawable.keybord));
            }
        };

        mVoiceKeyboard = new FixedHeightSubKeyboard(mKeyboard, mRecordButton, ivRecord) {

            @Override
            public void switchDrawable() {
                mIcon.setImageDrawable(getResources().getDrawable(R.drawable.keybord));
            }
        };

        mAdapter = new ChatAdapter();
        mContent.setAdapter(new ChatAdapter());
        mContent.setItemsCanFocus(true);
        initView();
        initData();

        // 录音完毕回调方法
        mRecordButton.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
            @Override
            public void onFinished(float seconds, String filePath) {
                ChatContent content = new ChatContent();
                content.path = filePath;
                content.length = Math.round(seconds);
                content.sendType = ChatContent.SEND;
                content.msgType = ChatContent.VOICE;
                content.msgTime = new Date();
                content.toName = to;
                mPresenter.insertChatContent(content);
                mContents.add(content);
                mAdapter.notifyDataSetChanged();
                mContent.setSelection(mContent.getBottom());
            }
        });
    }

    /**
     * 播放音频
     *
     * @param chatContent
     * @param newVoiceAnimView
     */
    public void playVoice(ChatContent chatContent, View newVoiceAnimView) {
        if (MediaManager.getMediaPlayer() != null && MediaManager.getMediaPlayer().isPlaying()) {
            MediaManager.getMediaPlayer().stop();
            voiceAnimView.setBackgroundResource(R.drawable.adj);
            if (newVoiceAnimView == voiceAnimView) {
                return;
            }
        }
        voiceAnimView = newVoiceAnimView;
        newVoiceAnimView.setBackgroundResource(R.drawable.play);
        AnimationDrawable drawable = (AnimationDrawable) voiceAnimView
                .getBackground();
        drawable.start();

        // 播放音频
        MediaManager.playSound(chatContent.path,
                new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        voiceAnimView.setBackgroundResource(R.drawable.adj);
                        voiceAnimView = null;
                    }
                });


    }

    private void initData() {
        loadData();
    }

    private void loadData() {
        if (mNextPage != 0) {
            mNextPage = mCurrentPage.nextPage();
        }
        mCurrentPage = mPresenter.query(to, mNextPage);

        mContents.addAll(mContents.size(), mCurrentPage.getDatas());
        mAdapter.notifyDataSetChanged();
        mContent.setSelection(mContent.getBottom());

    }


    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.updateChatContentRead(to);
        MediaManager.pause();
    }

    private void initView() {
        // emotion
        ImageModel model = new ImageModel();
        model.icon = getResources().getDrawable(R.drawable.ic_emotion);
        model.flag = "经典笑脸";
        model.isSelected = true;

        ImageModel plus = new ImageModel();
        plus.icon = getResources().getDrawable(R.drawable.ic_plus);
        plus.flag = "添加表情";
        plus.isSelected = false;
        List<ImageModel> models = new ArrayList<>();
        models.add(model);
        models.add(plus);

        emotionAdapter = new HorizontalRecyclerViewAdapter(this, models);
        mRecyclerView.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能
        mRecyclerView.setAdapter(emotionAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        //初始化recyclerview_horizontal监听器
        emotionAdapter.setOnClickItemListener(new HorizontalRecyclerViewAdapter.OnClickItemListener() {
            @Override
            public void onItemClick(View view, int position, List<ImageModel> datas) {
                int oldPosition = SharedPreferencesUtils.getInstance().getParam(ChatActivity.this, EMOTION_LAST_COUNT, 0);
                SharedPreferencesUtils.getInstance().setParam(ChatActivity.this, EMOTION_LAST_COUNT, position);
                datas.get(position).isSelected = true;
                datas.get(oldPosition).isSelected = false;
                //通知更新，这里我们选择性更新就行了
                emotionAdapter.notifyItemChanged(oldPosition);
                emotionAdapter.notifyItemChanged(position);
                //viewpager界面切换
                mViewPager.setCurrentItem(position, false);
            }

            @Override
            public void onItemLongClick(View view, int position, List<ImageModel> datas) {
            }
        });
        initPageView();
        GlobalOnItemClickManagerUtils globalOnItemClickManager = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickManager.attachToEditText(msg);


    }

    private void initPageView() {
        //创建fragment的工厂类
        FragmentFactory factory = FragmentFactory.getSingleFactoryInstance();
        //创建修改实例
        EmotionCompleteFragment f1 = (EmotionCompleteFragment) factory.getFragment(EmotionUtils.EMOTION_CLASSIC_TYPE);
        fragments.add(f1);
        Bundle b = null;
        for (int i = 0; i < 7; i++) {
            b = new Bundle();
            b.putString("Integer", "Fragment-" + i);
            Fragment1 fg = Fragment1.newInstance(Fragment1.class, b);
            fragments.add(fg);
        }

        NoHorizontalScrollerVPAdapter adapter = new NoHorizontalScrollerVPAdapter(this.getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);

        // more
        initMoreView();

    }

    private void initMoreView() {
        int itemSize = DisplayUtils.dp2px(this, 40);
        // 获取屏幕宽度
        int screenWidth = DisplayUtils.getScreenWidthPixels(this);
        // item的间距
        int spacing = DisplayUtils.dp2px(this, 5);
        List<GridView> gridViews = new ArrayList<>();
        // List<Map<String, Integer>> icons = new ArrayList<>();
        List<ValuePair<String, Integer>> tools = new ArrayList<>();
        ValuePair<String, Integer> picEntry = new ValuePair<>(getResources().getString(R.string.picture), R.drawable.pick_photo);
        tools.add(picEntry);
        gridViews.add(createGridView(tools, screenWidth, spacing, itemSize));
        EmotionPagerAdapter adapter = new EmotionPagerAdapter(gridViews);
        mMoreViewPager.setAdapter(adapter);
        emojiIndicatorView.initIndicator(adapter.getCount());
    }

    private GridView createGridView(List<ValuePair<String, Integer>> tools, int screenWidth, int spacing, int itemSize) {

        int count = (DisplayUtils.px2dp(this, screenWidth) - spacing) / (itemSize + spacing);

        showToast(count + "");

        // 创建GridView
        GridView gv = new GridView(this);
        //设置点击背景透明
        gv.setSelector(android.R.color.black);
        //设置7列
        gv.setNumColumns(count);
        // gv.setHorizontalSpacing(spacing);
        // gv.setVerticalSpacing(spacing * 2);
        //设置GridView的宽高
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(itemSize, itemSize);
        gv.setLayoutParams(params);
        // gv.setGravity(Gravity.CENTER);
        // 给GridView设置表情图片
        MoreGridViewAdapter adapter = new MoreGridViewAdapter(tools);
        gv.setAdapter(adapter);
        //设置全局点击事件
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start(ChatActivity.this, PhotoPicker.REQUEST_CODE);

            }
        });
        return gv;

    }

    class MoreGridViewAdapter extends BaseAdapter {
        List<ValuePair<String, Integer>> mTools;

        public MoreGridViewAdapter(List<ValuePair<String, Integer>> tools) {
            mTools = tools;
        }

        @Override
        public int getCount() {
            return mTools.size();
        }

        @Override
        public ValuePair<String, Integer> getItem(int position) {
            return mTools.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(ChatActivity.this, R.layout.image_text, null);
            }
            Button btn = (Button) convertView;
            ValuePair<String, Integer> valuePair = mTools.get(position);
            btn.setText(valuePair.key);
            Drawable drawable = getResources().getDrawable(valuePair.value);
            drawable.setBounds(0, 0, DisplayUtils.dp2px(ChatActivity.this, 40), DisplayUtils.dp2px(ChatActivity.this, 40));
            btn.setCompoundDrawables(null, drawable, null, null);
            return convertView;
        }
    }

    @OnClick({R.id.tv_send})
    public void sendMsg(View v) {
        if (!StringUtils.isBlank(msg)) {
            String text = msg.getText().toString();
            mPresenter.sendMsg(to, text);
            msg.setText("");
            // 发送消息
            ChatContent content = new ChatContent();
            content.sendType = ChatContent.SEND;
            content.msgType = ChatContent.TEXT;
            content.toName = to;
            content.msgTime = new Date();
            content.content = text;
            mPresenter.insertChatContent(content);
            mContents.add(content);
            mAdapter.notifyDataSetChanged();
            mContent.setSelection(mContent.getBottom());
        }
    }

    public void onEventMainThread(Object event) {
        Log.i(TAG, Thread.currentThread().getName());

        if (event instanceof ChatContent) {
            mContents.add((ChatContent) event);
            mAdapter.notifyDataSetChanged();
            mContent.setSelection(mContent.getBottom());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.iv_record)
    public void record(ImageView v) {
        // emotionKeyboard.interceptBackPress();
        if (msg.isShown()) {
            msg.setVisibility(View.GONE);
            mRecordButton.setVisibility(View.VISIBLE);
            ivRecord.setImageDrawable(getResources().getDrawable(R.drawable.keybord));
        } else {
            msg.setVisibility(View.VISIBLE);
            showKeyBoard(msg);
            mRecordButton.setVisibility(View.GONE);
            ivRecord.setImageDrawable(getResources().getDrawable(R.drawable.sound));
        }

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        MediaManager.release();
    }

    class ChatAdapter extends BaseAdapter {

        private int mMinItemWith;// 设置对话框的最大宽度和最小宽度
        private int mMaxItemWith;

        public ChatAdapter() {
            // 获取系统宽度
            WindowManager wManager = (WindowManager) ChatActivity.this
                    .getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wManager.getDefaultDisplay().getMetrics(outMetrics);
            mMaxItemWith = (int) (outMetrics.widthPixels * 0.7f);
            mMinItemWith = (int) (outMetrics.widthPixels * 0.2f);
        }

        @Override
        public int getCount() {
            return mContents.size();
        }

        @Override
        public ChatContent getItem(int position) {
            return mContents.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(ChatActivity.this, R.layout.listview_chat_content, null);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
                viewHolder.chatLeft = convertView.findViewById(R.id.ll_chat_left);
                viewHolder.chatRight = convertView.findViewById(R.id.ll_chat_right);
                viewHolder.chatLeftContent = (TextView) convertView.findViewById(R.id.tv_chat_content_l);
                viewHolder.chatRightContent = (TextView) convertView.findViewById(R.id.tv_chat_content_r);
                viewHolder.voiceRight = (RelativeLayout) convertView.findViewById(R.id.fl_chat_voice_r);
                viewHolder.recordAnim = (View) convertView.findViewById(R.id.v_recorder_anim);
                viewHolder.recordTime = (TextView) convertView.findViewById(R.id.tv_recorder_time);
                viewHolder.recorderLength = (FrameLayout) convertView.findViewById(R.id.recorder_length);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ChatContent content = mContents.get(position);
            setValues(content, viewHolder);
            return convertView;
        }

        private void setValues(final ChatContent content, final ViewHolder viewHolder) {

            switch (content.sendType) {
                case ChatContent.SEND:
                    viewHolder.chatLeft.setVisibility(View.GONE);
                    viewHolder.chatRight.setVisibility(View.VISIBLE);
                    break;
                case ChatContent.RECIVED:
                    viewHolder.chatLeft.setVisibility(View.VISIBLE);
                    viewHolder.chatRight.setVisibility(View.GONE);
                    break;
            }

            switch (content.msgType) {
                case ChatContent.TEXT:
                    SpannableString spannableString = null;
                    switch (content.sendType) {
                        case ChatContent.SEND:
                            spannableString = SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE, ChatActivity.this, viewHolder.chatRightContent, content.content);
                            viewHolder.voiceRight.setVisibility(View.GONE);
                            viewHolder.chatRightContent.setText(spannableString);
                            break;
                        case ChatContent.RECIVED:
                            spannableString = SpanStringUtils.getEmotionContent(EmotionUtils.EMOTION_CLASSIC_TYPE, ChatActivity.this, viewHolder.chatLeftContent, content.content);
                            viewHolder.chatLeftContent.setText(spannableString);
                            break;
                    }
                    break;
                case ChatContent.VOICE:
                    switch (content.sendType) {
                        case ChatContent.SEND:
                            viewHolder.voiceRight.setVisibility(View.VISIBLE);
                            viewHolder.chatRightContent.setVisibility(View.GONE);
                            viewHolder.recordTime.setText(Math.round(content.length) + "\"");
                            ViewGroup.LayoutParams lParams = viewHolder.recorderLength.getLayoutParams();
                            lParams.width = (int) (mMinItemWith + mMaxItemWith / 60f * content.length);
                            viewHolder.recorderLength.setLayoutParams(lParams);
                            viewHolder.recorderLength.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    playVoice(content, viewHolder.recordAnim);
                                }
                            });
                            break;
                        case ChatContent.RECIVED:

                            break;
                    }
                    break;
            }

        }

        class ViewHolder {
            View chatLeft;
            ImageView chatLeftImage;
            TextView chatLeftContent;
            View chatRight;
            ImageView chatRightImage;
            TextView chatRightContent;
            FrameLayout voiceLeft;
            RelativeLayout voiceRight;
            View recordAnim;
            TextView recordTime;
            FrameLayout recorderLength;


        }
    }

}
