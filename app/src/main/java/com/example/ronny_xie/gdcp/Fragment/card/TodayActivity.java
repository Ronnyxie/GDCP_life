package com.example.ronny_xie.gdcp.Fragment.card;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.ronny_xie.gdcp.Fragment.card.javabean.todayData_javabean;
import com.example.ronny_xie.gdcp.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Ronny on 2017/5/8.
 */

public class TodayActivity extends Activity{
    private int NOTIFYDATAGETSUCCESS = 1001;
    private todayData_javabean personData_javabean;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.card_today_activity);
        super.onCreate(savedInstanceState);
        initBar();
        initData();
        initHanler();
    }

    private void initHanler() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what==NOTIFYDATAGETSUCCESS){
                    initView();
                }
                return true;
            }
        });
    }

    private void initView() {
        TextView tv_time = (TextView) findViewById(R.id.card_activity_today_time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        tv_time.setText(format.format(System.currentTimeMillis()));
        TextView tv_money = (TextView) findViewById(R.id.card_activity_today_tv);
//        Log.i("aaaaaa", "initView: "+todayData_javabean.class.getName());
    }

    private void initData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String todayData = cardClient.getTodayData(cardClient.getHttpClient());
                try {
                    Gson gson = new Gson();
                    ArrayList<Object> arr = new ArrayList<Object>();
                    JSONArray array = new JSONArray(todayData);
                    for (int i = 0; i < array.length()-1; i++) {
                        Object fromJson = gson.fromJson(array.get(i).toString(), todayData_javabean.class);
                        arr.add(fromJson);
                    }
                    Log.i("aaa",array.get(array.length()-1).toString());//Todo 拿到数组最后一个数据
                    handler.sendEmptyMessage(NOTIFYDATAGETSUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void initBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
}