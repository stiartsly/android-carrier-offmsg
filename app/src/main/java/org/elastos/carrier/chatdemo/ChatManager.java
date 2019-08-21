package org.elastos.carrier.chatdemo;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

class ChatManager {
    private static final String CHAT_LIST = "chat_list";
    private static ChatManager instance = null;
    private List<EMConversation> mChatList;
    private Context mContext;

    static ChatManager getInstance(Context context) {
    	if (instance == null) {
		    instance = new ChatManager(context);
	    }
        return instance;
    }

    static ChatManager getInstance() {
        return instance;
    }

    private ChatManager(Context context) {
        mContext = context;
        mChatList = initChatList();
        if (mChatList == null) {
            mChatList = new ArrayList<>();
        }
        else {
            for (EMConversation e : mChatList) {
                e.setOnline(false);
            }
        }
    }

    void addFriend(String userName, String userId) {
        if (userName == null || userName.isEmpty()) {
            userName = "Getting ...";
        }

        if (containUser(userId)) {
            return;
        }

	    mChatList.add(new EMConversation(userName, userId));

        update();
    }

    void updateFriend(String userName, String userId) {
        List<EMConversation> list = mChatList;
        for (EMConversation e : list) {
            if (e.getUserId().equals(userId)) {
                e.setUsername(userName);
                break;
            }
        }

        update();
    }

    void updateFriendState(String userId, boolean isOnline) {
        List<EMConversation> list = mChatList;
        for (EMConversation e : list) {
            if (e.getUserId().equals(userId)) {
                e.setOnline(isOnline);
                break;
            }
        }

        update();
    }

    void updateUnReadCount(String userId) {
        List<EMConversation> list = mChatList;
        for (EMConversation e : list) {
            if (e.getUserId().equals(userId)) {
                int older = e.getUnreadMsgCount();
                e.setUnreadMsgCount(older + 1);
                break;
            }
        }

        update();
    }

    void resetUnReadCount(String userId) {
        List<EMConversation> list = mChatList;
        for (EMConversation e : list) {
            if (e.getUserId().equals(userId)) {
                e.setUnreadMsgCount(0);
                break;
            }
        }

        update();
    }

    private void update() {
        String json = new Gson().toJson(mChatList);
        PreferenceUtil.saveString(mContext, CHAT_LIST, json);
    }

    private boolean containUser(String userId) {
        for (EMConversation e : mChatList) {
            if (e.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    private List<EMConversation> initChatList() {
        String json = PreferenceUtil.getString(mContext, CHAT_LIST);
        if (TextUtils.isEmpty(json) || json.equals("[null]")) {
            return null;
        }
        return new Gson().fromJson(json, new TypeToken<List<EMConversation>>() {}.getType());
    }

    List<EMConversation> getChatList() {
        return mChatList;
    }

    EMConversation getChat(String userId) {
        EMConversation emConversation = null;

        List<EMConversation> list = getChatList();
        for (EMConversation e : list) {
            if (e.getUserId().equals(userId)) {
                emConversation = e;
                break;
            }
        }

        return emConversation;
    }

    void addMessage(String userId, EMMessage message) {
        EMConversation emConversation = null;

        List<EMConversation> list = getChatList();
        for (EMConversation e : list) {
            if (e.getUserId().equals(userId)) {
                emConversation = e;
                break;
            }
        }

        if (emConversation != null) {
            emConversation.addMessage(message);
        }

        updateChatList(emConversation);
    }

    private void updateChatList(EMConversation emConversation ) {
        EMConversation deleteEmConversation = null;

        List<EMConversation> list = getChatList();
        for (EMConversation e : list) {
            if (e.getUserId().equals(emConversation.getUserId())) {
                deleteEmConversation = e;
                break;
            }
        }

        list.remove(deleteEmConversation);
        list.add(emConversation);

        String json = new Gson().toJson(list);
        PreferenceUtil.saveString(mContext, CHAT_LIST, json);
    }
}
