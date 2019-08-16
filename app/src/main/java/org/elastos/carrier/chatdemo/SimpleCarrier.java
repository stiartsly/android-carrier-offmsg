package org.elastos.carrier.chatdemo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.Carrier.Options;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.exceptions.CarrierException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SimpleCarrier {
	private static SimpleCarrier sInstance = null;
	private Carrier mCarrier = null;
	private Context mContext;
	private Handler mHandler;

	static class STATE {
		static final int READY = 0;
		static final int CONNECTION = 1;
		static final int FRIENDCONNECTION = 2;
		static final int SELFINFOCHANGED = 3;
		static final int FRIENDINFOCHANGED = 4;
		static final int FRIENDREQUEST = 5;
		static final int FRIENDADDED = 6;
		static final int FRIENDMESSAGE = 8;

		static final String USERID = "userId";
		static final String HELLO = "hello";
		static final String FROM = "from";
		static final String MSG = "message";
	}

	static SimpleCarrier getInstance() {
		return sInstance;
	}

	static SimpleCarrier getInstance(Context context, Handler msghandler) {
		if (sInstance == null) {
			sInstance = new SimpleCarrier(context, msghandler);
		}
		return sInstance;
	}

	private SimpleCarrier(Context context, Handler msghandler) {
		mContext = context;
		mHandler = msghandler;
		ChatOptions options = new ChatOptions(getAppPath());

		ChatHandler handler = new ChatHandler();

		try {
			Carrier.initializeInstance(options, handler);
			mCarrier = Carrier.getInstance();

			mCarrier.start(0);
			synchronized(mCarrier) {
				mCarrier.wait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void addFriend(String chatID, String hello) {
		try {
			mCarrier.addFriend(chatID, hello);
		}
		catch (CarrierException e) {
			e.printStackTrace();
		}
	}

	void accept(String userId) {
		try {
			mCarrier.acceptFriend(userId);
		}
		catch (CarrierException e) {
			e.printStackTrace();
		}
	}

	void sendMessage(String userId, String message) {
		try {
			mCarrier.sendFriendMessage(userId, message);
		}
		catch (CarrierException e) {
			e.printStackTrace();
		}
	}

	String getAddress() {
		try {
			return mCarrier.getAddress();
		}
		catch (CarrierException e) {
			e.printStackTrace();
		}
		return null;
	}

	void setSelfInfo(UserInfo info) {
		try {
			mCarrier.setSelfInfo(info);
		}
		catch (CarrierException e) {
			e.printStackTrace();
		}
	}

	UserInfo getSelfInfo() {
		try {
			return mCarrier.getSelfInfo();
		}
		catch (CarrierException e) {
			e.printStackTrace();
		}
		return null;
	}

	public class ChatHandler extends AbstractCarrierHandler {
		@Override
		public void onReady(Carrier carrier) {
			synchronized(mCarrier) {
				mCarrier.notify();
			}
			mHandler.sendEmptyMessage(STATE.READY);
		}

		@Override
		public void onConnection(Carrier carrier, ConnectionStatus status) {
			mHandler.sendEmptyMessage(STATE.CONNECTION);
		}

		@Override
		public void onSelfInfoChanged(Carrier carrier, UserInfo info) {
			Message msg = new Message();
			msg.what = STATE.SELFINFOCHANGED;
			msg.obj = info;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status) {
			Message msg = new Message();
			msg.what = STATE.FRIENDCONNECTION;
			msg.arg1 = status.value();
			msg.obj = friendId;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String hello) {
			Message msg = new Message();
			msg.what = STATE.FRIENDREQUEST;
			msg.obj = info;
			Bundle data = new Bundle();
			data.putString(STATE.USERID, userId);
			data.putString(STATE.HELLO, hello);
			msg.setData(data);
			mHandler.sendMessage(msg);
		}

		@Override
		public void onFriendAdded(Carrier carrier, FriendInfo info) {
			Message msg = new Message();
			msg.what = STATE.FRIENDADDED;
			msg.obj = info;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onFriendMessage(Carrier carrier, String from, byte[] message) {
			Message msg = new Message();
			msg.what = STATE.FRIENDMESSAGE;
			Bundle data = new Bundle();
			data.putString(STATE.FROM, from);
			data.putString(STATE.MSG, new String(message));
			msg.setData(data);
			mHandler.sendMessage(msg);
		}

		@Override
		public void onFriendInfoChanged(Carrier carrier, String friendId, FriendInfo info) {
			Message msg = new Message();
			msg.what = STATE.FRIENDINFOCHANGED;
			msg.obj = info;
			Bundle data = new Bundle();
			data.putString(STATE.USERID, friendId);
			msg.setData(data);
			mHandler.sendMessage(msg);
		}
	}

	private String getAppPath() {
		return mContext.getFilesDir().getAbsolutePath();
	}

	private class ChatOptions extends Options {
		ChatOptions(String path) {
			super();

			File file = new File(path);
			if (file.exists())
				file.delete();
			file.mkdir();

			try {
				setUdpEnabled(true);
				setPersistentLocation(path);

				ArrayList<BootstrapNode> arrayList = new ArrayList<>();
				BootstrapNode node = new BootstrapNode();
				node.setIpv4("13.58.208.50");
				node.setPort("33445");
				node.setPublicKey("89vny8MrKdDKs7Uta9RdVmspPjnRMdwMmaiEW27pZ7gh");
				arrayList.add(node);

				node = new BootstrapNode();
				node.setIpv4("18.216.102.47");
				node.setPort("33445");
				node.setPublicKey("G5z8MqiNDFTadFUPfMdYsYtkUDbX5mNCMVHMZtsCnFeb");
				arrayList.add(node);

				node = new BootstrapNode();
				node.setIpv4("18.216.6.197");
				node.setPort("33445");
				node.setPublicKey("H8sqhRrQuJZ6iLtP2wanxt4LzdNrN2NNFnpPdq1uJ9n2");
				arrayList.add(node);

				node = new BootstrapNode();
				node.setIpv4("52.83.171.135");
				node.setPort("33445");
				node.setPublicKey("5tuHgK1Q4CYf4K5PutsEPK5E3Z7cbtEBdx7LwmdzqXHL");
				arrayList.add(node);

				node = new BootstrapNode();
				node.setIpv4("52.83.191.228");
				node.setPort("33445");
				node.setPublicKey("3khtxZo89SBScAMaHhTvD68pPHiKxgZT6hTCSZZVgNEm");
				arrayList.add(node);

				setBootstrapNodes(arrayList);

				//Hive
				ArrayList<HiveBootstrapNode> hiveArrayList = new ArrayList<>();
				HiveBootstrapNode hiveNode = new HiveBootstrapNode();
				hiveNode.setIpv4("52.83.159.189");
				hiveNode.setPort("9094");
				hiveArrayList.add(hiveNode);

				hiveNode = new HiveBootstrapNode();
				hiveNode.setIpv4("52.83.119.110");
				hiveNode.setPort("9094");
				hiveArrayList.add(hiveNode);

				hiveNode = new HiveBootstrapNode();
				hiveNode.setIpv4("3.16.202.140");
				hiveNode.setPort("9094");
				hiveArrayList.add(hiveNode);

				hiveNode = new HiveBootstrapNode();
				hiveNode.setIpv4("18.217.147.205");
				hiveNode.setPort("9094");
				hiveArrayList.add(hiveNode);

				hiveNode = new HiveBootstrapNode();
				hiveNode.setIpv4("18.219.53.133");
				hiveNode.setPort("9094");
				hiveArrayList.add(hiveNode);

				setHiveBootstrapNodes(hiveArrayList);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
