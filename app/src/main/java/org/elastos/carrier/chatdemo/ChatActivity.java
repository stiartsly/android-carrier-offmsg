package org.elastos.carrier.chatdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import sj.keyboard.XhsEmoticonsKeyBoard;

public class ChatActivity extends AppCompatActivity  implements MessageService.IMessageCallback {
    public static final String NAME = "user_name";
    public static final String USERID = "user_id";

    private XhsEmoticonsKeyBoard mEKeyBoard;
    private ListView mListView;
    private MessageAdapter mAdapter;

    private String mUserId;
    private EMConversation mEMConversation;
    private ChatManager mChatManager;
    private SimpleCarrier mNetUtils;
    private MessageService mMessageService;
    private ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        setUpView();
    }

    @Override
    public void processMessage(Message msg) {
        switch (msg.what) {
            case SimpleCarrier.STATE.FRIENDMESSAGE: {
                //A message is coming.
                Bundle data = msg.getData();
                if (data != null) {
                    String userId = data.getString(SimpleCarrier.STATE.FROM);
                    if (userId != null && userId.equals(mUserId)) {
                        String message = data.getString(SimpleCarrier.STATE.MSG);
                        onReceive(message);
                    }
                }

                break;
            }
            case SimpleCarrier.STATE.FRIENDCONNECTION: {
                break;
            }
            default:{
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        Is_Start_Chat = false;
    }

    private static boolean Is_Start_Chat = false;
    private static String Active_UserId = "";
    protected void initView() {
        String name = getIntent().getStringExtra(NAME);
        mUserId = getIntent().getStringExtra(USERID);

        Is_Start_Chat = true;
        Active_UserId = mUserId;
        mServiceConnection = new MessageServiceConn();
        bindService(new Intent(this, MessageService.class), mServiceConnection, BIND_AUTO_CREATE);

        mNetUtils = SimpleCarrier.getInstance();
        mChatManager = ChatManager.getInstance();
        mChatManager.resetUnReadCount(mUserId);

        mEMConversation = mChatManager.getChat(mUserId);
        TextView title = findViewById(R.id.txt_title);
        title.setText(name);

        findViewById(R.id.img_back).setVisibility(View.VISIBLE);
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mListView = findViewById(R.id.list);

        mEKeyBoard = findViewById(R.id.ek_bar);
        mEKeyBoard.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText(mEKeyBoard.getEtChat().getText().toString().trim());
            }
        });
    }

    private void setUpView() {
        mAdapter = new MessageAdapter(this, mEMConversation);
        mListView.setAdapter(mAdapter);
        int count = mListView.getCount();
        if (count > 0) {
            mListView.setSelection(count);
        }
    }

    private void onReceive(String friendMessage) {
        TextMessageBody txtBody = new TextMessageBody(friendMessage);
        EMMessage message = new EMMessage();
        message.direct = EMMessage.Direct.RECEIVE;
        // Set the message body
        message.setBody(txtBody);

        mChatManager.addMessage(mUserId, message);
        mEMConversation = mChatManager.getChat(mUserId);
        mAdapter.refresh();

        mListView.setSelection(mListView.getCount() - 1);
    }

    private void sendText(final String content) {
        if (content.length() > 0) {
            EMMessage message = new EMMessage();
            message.direct = EMMessage.Direct.SEND;
            TextMessageBody txtBody = new TextMessageBody(content);
            // Set the message body
            message.setBody(txtBody);

            mChatManager.addMessage(mUserId, message);
            mEMConversation = mChatManager.getChat(mUserId);
            mAdapter.refresh();

            mEKeyBoard.getEtChat().setText("");

            mListView.setSelection(mListView.getCount() - 1);
            mNetUtils.sendMessage(mUserId, content);

            setResult(RESULT_OK);
        }
    }

    public static boolean isActiveChat(String userId) {
        if (userId != null) {
            return Is_Start_Chat && userId.equals(Active_UserId);
        }

        return false;
    }

    private class MessageServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mMessageService = ((MessageService.LocalBinder) binder).getService();
            mMessageService.addMessageCallback(ChatActivity.this);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessageService = null;
        }
    }
}
