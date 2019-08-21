package org.elastos.carrier.chatdemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class EMConversation implements Serializable {
    private List<EMMessage> mMessages;
    private int mUnreadMsgCount = 0;
    private String mUsername;
    private String mUserId;
    private boolean mIsOnline = false;

    EMConversation(String username, String userId) {
        this.mUsername = username;
        this.mUserId = userId;
        this.mMessages = new ArrayList<>();
    }

    int getUnreadMsgCount() {
        return mUnreadMsgCount;
    }

    void setUnreadMsgCount(int unreadMsgCount) {
        this.mUnreadMsgCount = unreadMsgCount;
    }

    String getUsername() {
        return mUsername;
    }

    void setUsername(String username) {
        this.mUsername = username;
    }

    String getUserId() {
        return mUserId;
    }

    void setOnline(boolean isOnline) {
        mIsOnline = isOnline;
    }

    boolean getOnline() {
        return mIsOnline;
    }

    int getMsgCount() {
        return this.mMessages.size();
    }

    EMMessage getMessage(int position) {
        return mMessages.get(position);
    }

    void addMessage(EMMessage emMessage) {
        mMessages.add(emMessage);
    }

    EMMessage getLastMessage() {
        return this.mMessages.size() == 0 ? null : this.mMessages.get(this.mMessages.size() - 1);
    }
}
