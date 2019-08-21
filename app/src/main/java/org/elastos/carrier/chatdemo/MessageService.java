package org.elastos.carrier.chatdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageService extends Service {
	private static List<IMessageCallback> sMessageCallbacks = new ArrayList<>();

	@Override
	public IBinder onBind(Intent arg0) {
		return new LocalBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	final class LocalBinder extends Binder {
		MessageService getService() {
			return MessageService.this;
		}
	}

	public interface IMessageCallback {
		void processMessage(Message message);
	}

	public void addMessageCallback(IMessageCallback callback) {
		sMessageCallbacks.add(callback);
	}

	public static class NetHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			for (int i = 0; i < sMessageCallbacks.size(); i++) {
				sMessageCallbacks.get(i).processMessage(msg);
			}
		}
	}
}
