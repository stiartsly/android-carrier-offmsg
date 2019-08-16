package org.elastos.carrier.chatdemo;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class QrAddressActivity extends AppCompatActivity {
	public static final String QRCODETYPE = "QrCode_Address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);

		init(getIntent());
    }

    private void init(Intent intent) {
	    TextView title = findViewById(R.id.txt_title);
	    title.setText("Carrier Address");

	    findViewById(R.id.img_back).setVisibility(View.VISIBLE);
	    findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
			    finish();
		    }
	    });

	    if (intent != null) {
		    String qrcodeString = intent.getStringExtra(QRCODETYPE);
		    ImageView mQrcode = findViewById(R.id.qrcode);

		    int width = getQrcWidth();
		    mQrcode.setImageBitmap(QRCodeUtils.createQRCodeBitmap(qrcodeString, width, width));
	    }
    }

	private int getQrcWidth() {
		Point point = new Point();
		getWindowManager().getDefaultDisplay().getSize(point);
		return point.x * 6 / 10;
	}
}
