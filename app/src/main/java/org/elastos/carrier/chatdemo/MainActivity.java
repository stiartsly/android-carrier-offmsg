package org.elastos.carrier.chatdemo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.elastos.carrier.chatdemo.ChatActivity.NAME;
import static org.elastos.carrier.chatdemo.ChatActivity.USERID;

public class MainActivity extends AppCompatActivity implements MessageService.IMessageCallback, PopupMenu.OnMenuItemClickListener {
	private final static String TAG = "MainActivity";
	private MessageService.NetHandler mHandler = null;
	private SimpleCarrier mSimpleCarrier = null;
	private MessageService mMessageService;
	private ServiceConnection mServiceConnection;
	private ListView mContactListView;
	private TextView mTitle;
	private final static String USERNAME = "userName";
	private boolean isReady = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}

	@Override
	public void processMessage(Message msg) {
		int what = msg.what;
		switch (what) {
			case SimpleCarrier.STATE.READY: {
				//set user info
				UserInfo self = mSimpleCarrier.getSelfInfo();
				String name = PreferenceUtil.getString(this, USERNAME);
				self.setName(name);
				mSimpleCarrier.setSelfInfo(self);

				showText("Carrier is ready. name: " + name);

				isReady = true;

				break;
			}
			case SimpleCarrier.STATE.FRIENDADDED: {
				FriendInfo info = (FriendInfo) msg.obj;
				String name = info.getName();
				String id = info.getUserId();
				mChatManager.addFriend(name, id);
				updateList();

				showText(String.format("A friend is added. name=[%s]", name));

				break;
			}
			case SimpleCarrier.STATE.FRIENDREQUEST: {
				showText("A friend request");
				Bundle data = msg.getData();
				String userId = data.getString(SimpleCarrier.STATE.USERID);
				SimpleCarrier.getInstance().accept(userId);
				break;
			}
			case SimpleCarrier.STATE.FRIENDMESSAGE: {
				Bundle data = msg.getData();
				if (data != null) {
					String userId = data.getString(SimpleCarrier.STATE.FROM);
					String recvMessage = data.getString(SimpleCarrier.STATE.MSG);
					showText("onFriendMessage, message: " + recvMessage);

					if (ChatActivity.isActiveChat(userId)) {
						return;
					}

					TextMessageBody txtBody = new TextMessageBody(recvMessage);
					EMMessage message = new EMMessage();
					message.direct = EMMessage.Direct.RECEIVE;
					// Set the message body
					message.setBody(txtBody);
					mChatManager.addMessage(userId, message);
					mChatManager.updateUnReadCount(userId);
					updateList();
				}

				break;
			}
			case SimpleCarrier.STATE.FRIENDINFOCHANGED: {
				FriendInfo info = (FriendInfo) msg.obj;
				Bundle data = msg.getData();
				if (info != null) {
					String userId = data.getString(SimpleCarrier.STATE.USERID);
					mChatManager.updateFriend(info.getName(), userId);
					updateList();

					showText("onFriendInfoChanged, name: " + info.getName());
				}

				break;
			}
			case SimpleCarrier.STATE.FRIENDCONNECTION: {
				String userId = (String) msg.obj;
				boolean isOnline = msg.arg1 == 0;
				mChatManager.updateFriendState(userId, isOnline);
				updateList();

				String state = "offline";
				if (isOnline) {
					state = "online";
				}
				showText("onFriendConnection : " + state);

				break;
			}
			default: {
				break;
			}
		}
	}

	private void init() {
		mChatManager = ChatManager.getInstance(this);

		String name = PreferenceUtil.getString(this, USERNAME);
		if (name == null) {
			name = UUID.randomUUID().toString().substring(0, 8);
			PreferenceUtil.saveString(this, USERNAME, name);
		}

		mTitle = findViewById(R.id.txt_title);
		mTitle.setText(R.string.app_name);
		ImageView moreTools = findViewById(R.id.img_right);
		moreTools.setVisibility(View.VISIBLE);
		moreTools.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PopupMenu popup = new PopupMenu(MainActivity.this, view);
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.menu_main, popup.getMenu());
				popup.setOnMenuItemClickListener(MainActivity.this);
				popup.show();
			}
		});

		mContactListView = findViewById(R.id.listview);

		mServiceConnection = new MessageServiceConn();
		bindService(new Intent(this, MessageService.class), mServiceConnection, BIND_AUTO_CREATE);

		mHandler = new MessageService.NetHandler();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				mSimpleCarrier = SimpleCarrier.getInstance(MainActivity.this, mHandler);
			}
		});
		thread.start();

		updateList();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_scan: {
				showCamera();
				break;
			}
			case R.id.action_address: {
				if (isReady) {
					Intent intent = new Intent(MainActivity.this, QrAddressActivity.class);
					intent.putExtra(QrAddressActivity.QRCODETYPE, mSimpleCarrier.getAddress());
					startActivity(intent);
				}
				else {
					showText("the Carrier is not ready");
				}

				break;
			}
			default: {
				break;
			}
		}

		return false;
	}

	private List<EMConversation> mConversationList = new ArrayList<>();
	private ChatManager mChatManager = null;
	private void updateList() {
		List<EMConversation> list  = mChatManager.getChatList();

		if (list != null) {
			mConversationList.clear();
			mConversationList.addAll(list);
			NewMsgAdapter mAdapter = new NewMsgAdapter(this, mConversationList);
			mContactListView.setAdapter(mAdapter);
			ChatItemAction action = new ChatItemAction();
			mContactListView.setOnItemClickListener(action);
			mAdapter.notifyDataSetChanged();
		}
	}

	private static final int Chat_RequestCode = 1000;
	private class ChatItemAction implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
			EMConversation conversation = mConversationList.get(position);
			Intent intent = new Intent(MainActivity.this, ChatActivity.class);
			intent.putExtra(NAME, conversation.getUsername());
			intent.putExtra(USERID, conversation.getUserId());
			startActivityForResult(intent, Chat_RequestCode);
		}
	}

	private void showText(String value) {
		if (value != null && !value.isEmpty()) {
			Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
		}
	}

	private class MessageServiceConn implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			mMessageService = ((MessageService.LocalBinder) binder).getService();
			mMessageService.addMessageCallback(MainActivity.this);
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mMessageService = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
	}

	public void showCamera() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
				!= PackageManager.PERMISSION_GRANTED) {
			requestCameraPermission();
		} else {
			IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
			integrator.initiateScan();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode,resultCode,data);

		if (requestCode == Chat_RequestCode) {
			updateList();
		}
		else {
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
			if (scanResult != null) {
				String result = scanResult.getContents();
				if (result != null && !result.isEmpty()) {
					mSimpleCarrier.addFriend(result, "hello");
					Log.d(TAG, String.format("onActivityResult==scanResult=[%s]",result));
					Toast.makeText(this,result, Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private static final int REQUESTCAMERA = 0;
	private void requestCameraPermission() {
		if (ActivityCompat.shouldShowRequestPermissionRationale(this,
				Manifest.permission.CAMERA)) {
			Snackbar.make(mTitle, "获取摄像头权限",
					Snackbar.LENGTH_INDEFINITE)
					.setAction("OK", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							ActivityCompat.requestPermissions(MainActivity.this,
									new String[]{Manifest.permission.CAMERA},
									REQUESTCAMERA);
						}
					})
					.show();
		} else {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
					REQUESTCAMERA);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_scan) {
			showCamera();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
