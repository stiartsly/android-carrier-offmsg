package org.elastos.carrier.chatdemo;

import java.io.Serializable;

class TextMessageBody extends MessageBody implements Serializable {
    private String message;

    TextMessageBody(String message) {
        this.message = message;
    }

    String getMessage() {
        return message;
    }
}
