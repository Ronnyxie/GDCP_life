package com.example.ronny_xie.gdcp.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ronny_xie.gdcp.Fragment.card.CardActivity;
import com.example.ronny_xie.gdcp.Fragment.card.cardClient;
import com.example.ronny_xie.gdcp.Fragment.card.javabean.personData;
import com.example.ronny_xie.gdcp.R;
import com.example.ronny_xie.gdcp.loginActivity.LoginPage;
import com.example.ronny_xie.gdcp.util.ToastUtil;


/**
 * Created by ronny_xie on 2017/3/31.
 */

public class cardFragment extends Activity{

    private Handler handler;
    private final int SHOW_TOAST = 1001;
    private final int ERROR = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_card_login);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOW_TOAST:
                        ToastUtil.show(cardFragment.this, (String) msg.obj);
                        break;
                    case ERROR:
                        ToastUtil.show(cardFragment.this, "无法登陆");
                        break;
                }
                return false;
            }
        });

        initView();
    }

    private void initView() {
        ImageView imgCode = (ImageView) findViewById(R.id.fragment03_image);
        final EditText edtCode = (EditText) findViewById(R.id.fragment03_edittext);
        TextView tv_title = (TextView) findViewById(R.id.title);
        cardClient.getCardCode(imgCode);
        tv_title.setText("金融一卡通系统");
        final Button button = (Button) findViewById(R.id.fragment_card_login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[] userInfo = LoginPage.getUser(cardFragment.this);
                        if(!userInfo[0].equals("") && !userInfo[1].equals("")){
                            String name = userInfo[0];
                            String psd = userInfo[1].substring(userInfo[1].length() - 6);
                            personData data = cardClient.checkInCardSystem(name, psd, edtCode.getText().toString(), handler);
                            if (data != null) {
                                Intent intent = new Intent(cardFragment.this, CardActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }).start();
            }
        });
    }
}
