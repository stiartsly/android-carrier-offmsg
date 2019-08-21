package org.elastos.carrier.chatdemo;

import java.io.Serializable;

class EMMessage implements Serializable {
    private TextMessageBody body;
    private long msgTime;
    Direct direct;

    EMMessage() {
        this.msgTime = System.currentTimeMillis();
    }

    TextMessageBody getBody() {
        return body;
    }

    void setBody(TextMessageBody body) {
        this.body = body;
    }

    long getMsgTime() {
        return msgTime;
    }

    public enum Direct {
        SEND,
        RECEIVE;
    }
}
