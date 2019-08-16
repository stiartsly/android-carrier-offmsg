package org.elastos.carrier.chatdemo;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class NewMsgAdapter extends BaseAdapter {
    private Context context;
    private List<EMConversation> conversationList;

    NewMsgAdapter(Context ctx, List<EMConversation> objects) {
        context = ctx;
        conversationList = objects;
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.layout_item_msg, parent, false);
        }

        TextView txt_name = ViewHolder.get(convertView, R.id.txt_name);
	    TextView txt_state = ViewHolder.get(convertView, R.id.txt_state);
        TextView txt_content = ViewHolder.get(convertView, R.id.txt_content);
        TextView txt_time = ViewHolder.get(convertView, R.id.txt_time);
        TextView unreadLabel = ViewHolder.get(convertView, R.id.unread_msg_number);
        final EMConversation conversation = conversationList.get(position);
        txt_name.setText(conversation.getUsername());

        if (conversation.getUnreadMsgCount() > 0) {
            unreadLabel.setText(String.valueOf(conversation
                    .getUnreadMsgCount()));
            unreadLabel.setVisibility(View.VISIBLE);
        } else {
            unreadLabel.setVisibility(View.INVISIBLE);
        }

        if (conversation.getMsgCount() != 0) {
            EMMessage lastMessage = conversation.getLastMessage();
            txt_content.setText(getLastMessage(lastMessage));
            txt_time.setText(DateUtils.getTimestampString(new Date(
                    lastMessage.getMsgTime())));
        }

        if (conversation.getOnline()) {
            txt_state.setText("online");
            txt_state.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }
        else {
            txt_state.setText("offline");
            txt_state.setTextColor(Color.parseColor("#C69978"));
        }

        return convertView;
    }

    private String getLastMessage(EMMessage message) {
        TextMessageBody txtBody = message.getBody();
        String last = txtBody.getMessage();
        return last;
    }
}
