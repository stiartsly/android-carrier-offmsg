package org.elastos.carrier.chatdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Date;

public class MessageAdapter extends BaseAdapter {
    private final static String TAG = "msg";

    private LayoutInflater inflater;

    // reference to conversation object in chatsdk
    private EMConversation conversation;

    MessageAdapter(Context context, EMConversation conversation) {
        inflater = LayoutInflater.from(context);
        this.conversation = conversation;
    }

    public int getCount() {
        return conversation.getMsgCount();
    }

    void refresh() {
        notifyDataSetChanged();
    }

    public EMMessage getItem(int position) {
        return conversation.getMessage(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getViewTypeCount() {
        return 16;
    }

    private View createViewByMessage(EMMessage message, int position) {
        Log.d(TAG, position + " " + message.direct);
        return message.direct == EMMessage.Direct.RECEIVE ? inflater
                .inflate(R.layout.row_received_message, null) : inflater
                .inflate(R.layout.row_sent_message, null);
    }

    @SuppressLint("NewApi")
    public View getView(final int position, View convertView, ViewGroup parent) {
        final EMMessage message = getItem(position);
        final ViewHolder holder;
        holder = new ViewHolder();
        convertView = createViewByMessage(message, position);

        try {
            holder.head_iv = convertView.findViewById(R.id.iv_userhead);
            holder.tv = convertView.findViewById(R.id.tv_chatcontent);
            holder.tv_userId = convertView.findViewById(R.id.tv_userid);
        } catch (Exception e) {
            e.printStackTrace();
        }


        convertView.setTag(holder);

        holder.tv.setText(message.getBody().getMessage());

        TextView timestamp = convertView.findViewById(R.id.timestamp);

        if (position == 0) {
            timestamp.setText(DateUtils.getTimestampString(new Date(message
                    .getMsgTime())));
            timestamp.setVisibility(View.VISIBLE);
        } else {
            if (DateUtils.isCloseEnough(message.getMsgTime(), conversation
                    .getMessage(position - 1).getMsgTime())) {
                timestamp.setVisibility(View.GONE);
            } else {
                timestamp.setText(DateUtils.getTimestampString(new Date(message
                        .getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView tv;
        ImageView head_iv;
        TextView tv_userId;
    }
}