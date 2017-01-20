package org.free.chat.utils;

import android.media.Image;
import android.util.Log;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import org.free.chat.ChatApplication;
import org.free.chat.R;
import org.free.chat.bean.ChatContent;
import org.free.chat.bean.Result;
import org.free.chat.model.ChatModel;
import org.free.chat.model.impl.ChatModelImpl;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.stringencoder.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Handler;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/12/30.
 */
public class SmackUtils {

    private static final String TAG = "SmackUtils";
    private static final String HOST = "192.168.1.124";
    private static final int PORT = 5222; // default port

    private static XMPPTCPConnection conn;

    private static int count;
    // 最大连接次数
    private static final int MAX_CONNECT = 3;

    // 当前登陆的用户
    private static String user = "";


    /**
     * 连接服务器
     *
     * @return XMPPTCPConnection
     */
    public static void connect(final String account, final String password, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
                builder.setHost(HOST);
                builder.setPort(PORT);
                builder.setServiceName("");
                // 禁用SSL连
                builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                builder.setDebuggerEnabled(true);
                // 设置是否发送状态
                builder.setSendPresence(true);
                builder.setUsernameAndPassword(account, password);
                conn = new XMPPTCPConnection(builder.build());
                try {
                    conn.connect();
                    Run.onUiAsync(new Action() {
                        @Override
                        public void call() {
                            callBack.onFinished(conn);
                        }
                    });
                } catch (Exception e) {
                    Log.w(TAG, e.getMessage());
                    Run.onUiAsync(new Action() {
                        @Override
                        public void call() {
                            callBack.onFinished(null);
                        }
                    });

                }
            }
        }).start();

    }

    /**
     * 登录
     *
     * @param account
     * @param password
     * @param callBack
     */
    public static void login(final String account, final String password, final CallBack callBack) {
        if (conn != null && conn.isConnected()) {
            conn.disconnect();
        }
        user = account;
        connect(account, password, new CallBack() {
            @Override
            public void onSuccess() {
                doLogin(callBack);
                count = 0;
            }

            @Override
            public void onFailure(Result result) {
                if (count < MAX_CONNECT) {
                    login(account, password, callBack);
                    count++;
                } else {
                    count = 0;
                    result = new Result(Result.CONNECT_ERROR, ChatApplication.getStringRes(R.string.connect_error));
                    callBack.onFailure(result);
                }
            }

            @Override
            public void onFinished(Object t) {
                if (t != null && ((XMPPTCPConnection) t).isConnected()) {
                    onSuccess();
                } else {
                    onFailure(null);
                }
            }
        });

    }

    /**
     * 处理请求
     *
     * @param callBack
     */
    private static void doLogin(CallBack callBack) {
        try {
            conn.login();
            callBack.onSuccess();
            // 监听发送来的消息
            conn.addAsyncStanzaListener(new StanzaListener() {
                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                    if (packet instanceof Message) {
                        Message msg = (Message) packet;
                        if (!StringUtils.isBlank(msg.getBody())) {
                            Log.i(TAG, Thread.currentThread().getName());
                            ChatContent content = new ChatContent();
                            content.sendType = ChatContent.RECIVED;
                            content.msgType = ChatContent.TEXT;
                            content.isRead = false;
                            content.content = msg.getBody();
                            content.msgTime = new Date();
                            content.toName = msg.getFrom().split("@")[0];
                            ChatModel chatModel = new ChatModelImpl(ChatApplication.getContext());
                            chatModel.insert(content);
                            EventBus.getDefault().post(content);
                        }
                    }
                }
            }, null);
            return;
        } catch (Exception e) {
            Result result;
            if (e instanceof SASLErrorException) {
                result = new Result(Result.LOGIN_USERNAME_OR_PASSWORD_ERROR, ChatApplication.getStringRes(R.string.error_1002));
            } else {
                result = new Result(Result.LOGIN_ERROR, e.getMessage());
            }
            callBack.onFailure(result);
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public static List<RosterEntry> getAllEntries() {
        Roster roster = Roster.getInstanceFor(conn);
        List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();
        Collection<RosterEntry> rosterEntry = roster.getEntries();
        Iterator<RosterEntry> i = rosterEntry.iterator();
        while (i.hasNext())
            EntriesList.add(i.next());
        return EntriesList;
    }

    /**
     * 添加好友
     *
     * @param user
     * @param name
     * @param groups
     * @return ture添加成功 false添加失败
     */
    public boolean addFriend(String user, String name, String[] groups) {
        Roster roster = Roster.getInstanceFor(conn);
        try {
            roster.createEntry(user, name, new String[]{"friends"});

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<RosterEntry> searchUser(String key) {


        return null;
    }

    /**
     * 发送聊天消息
     * @param packet
     * @throws Exception
     */
    public static void sendChatMessage(Message packet) throws Exception{
        packet.setType(Message.Type.chat);
        sendMessage(packet);
    }

    /**
     * 发送群聊消息
     * @param packet
     * @throws Exception
     */
    public static void sendGroupChatMessage(Message packet) throws Exception{
        packet.setType(Message.Type.groupchat);
        sendMessage(packet);
    }

    private static void sendMessage(Message packet) throws Exception{
        // 设置消息的来源
        packet.setFrom(user + "@" + conn.getServiceName());
        packet.setTo(packet.getTo() + "@" + conn.getServiceName() );
        conn.sendStanza(packet);
    }


}
