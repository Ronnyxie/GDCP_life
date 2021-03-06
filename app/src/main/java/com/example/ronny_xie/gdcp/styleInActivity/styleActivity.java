package com.example.ronny_xie.gdcp.styleInActivity;

import java.util.ArrayList;
import java.util.List;


import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ronny_xie.gdcp.R;
import com.example.ronny_xie.gdcp.util.ToastUtil;
import com.example.ronny_xie.gdcp.util.menu_backgroundUtils;
import com.example.ronny_xie.gdcp.view.SearchView;

public class styleActivity extends Activity implements View.OnClickListener {
    private ListView mlv;
//    private ArrayList<String> picUrl;
//    private GridViewAdapter adapter;
//    private Handler handler;
//    private int width;
//    private int height;
    private SearchView searchView;
    private LinearLayout llContent;
//    private boolean isLoadDataSucess = false;//判断请求数据是否成功
//    private int count;//重复请求了多少次数据
    private ArrayList<Photos> mPhotos = new ArrayList<Photos>();
    private ArrayList<PhotoType> mPhotoType = new ArrayList<PhotoType>();
    private MyListViewAdapter listViewAdapter;
    private ArrayList<Photos> filterList;
    private TextView tvData;
    private GridView mGv;
    public viewState currentState =viewState.load;//控件的状态
    private FrameLayout flFail;
    private FrameLayout flRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style);
        initBmob();
        initView();
//        getScreenWidth();
        initData();
    }

    private void initData() {
        getPhotosDataForBmob();
        getPhotosTypeForBmob();


    }

    public ArrayList<Photos> getTitleData() {

        return mPhotos;
    }

    private void getPhotosTypeForBmob() {
        BmobQuery<PhotoType> query = new BmobQuery<PhotoType>();
        query.setLimit(50);
        query.findObjects(new FindListener<PhotoType>() {
            @Override
            public void done(List<PhotoType> object, BmobException e) {
                if (e == null) {
                    ToastUtil.show(styleActivity.this, "查询成功请稍等...");
                    for (PhotoType photoType : object) {
                        mPhotoType.add(photoType);
                    }

                    if (mPhotos.size() > 0) {
                        listViewAdapter = new MyListViewAdapter(styleActivity.this, mPhotos, mPhotoType);
                        mlv.setAdapter(listViewAdapter);

                    }
                    currentState=viewState.init;
                    changeView();
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    currentState =viewState.fail;
                    changeView();
                }
            }
        });
    }

    private void getPhotosDataForBmob() {
        BmobQuery<Photos> query = new BmobQuery<Photos>();
        query.setLimit(50);
        query.findObjects(new FindListener<Photos>() {
            @Override
            public void done(List<Photos> object, BmobException e) {
                if (e == null) {
                    for (Photos photos : object) {
                        mPhotos.add(photos);
                    }
                    if (mPhotoType.size() > 0) {
                        listViewAdapter = new MyListViewAdapter(styleActivity.this, mPhotos, mPhotoType);
                        mlv.setAdapter(listViewAdapter);
                    }
                    currentState=viewState.init;
                    changeView();
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    currentState =viewState.fail;
                    changeView();
                }
            }
        });
    }

//    private void getScreenWidth() {
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        width = metrics.widthPixels;
//        height = metrics.heightPixels;
//    }

    private void initView() {
//        picUrl = new ArrayList<String>();
        mlv = (ListView) findViewById(R.id.lv_style);
        tvData = (TextView) findViewById(R.id.tv_data);
        mGv = (GridView) findViewById(R.id.gv);
        flFail = (FrameLayout) findViewById(R.id.fl_fail);
        flFail.setOnClickListener(this);
        flRefresh = (FrameLayout) findViewById(R.id.fl_refrsh);
        mGv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               if (filterList!=null&&filterList.size()>0){
                   menu_backgroundUtils.setMenuBackground(styleActivity.this,filterList.get(i).getUrl());
                   hideKeyBoard();
                   styleActivity.this.finish();
               }
            }
        });
        findViewById(R.id.seartch).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        llContent = (LinearLayout) findViewById(R.id.content);

        searchView = (SearchView) findViewById(R.id.sv);
        searchView.setOnSearchListener(new SearchView.SearchListener() {



            @Override
            public void loadData(String text) {
                //对搜索框传来的数据进行检索
                filterPhotoData(text);
            }
        });
    }
    //隐藏键盘
    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void filterPhotoData(String text) {
        if (mPhotos != null) {
            filterList = new ArrayList<Photos>();
            for (Photos mPhoto : mPhotos) {
                if (mPhoto.getTitle().contains(text)) {
                    filterList.add(mPhoto);
                }
            }
        }
        if (filterList != null && filterList.size() > 0) {
            //查找到数据
            currentState =viewState.data;
            changeView();
            mGv.setAdapter(new MyGridViewAdapter(styleActivity.this, filterList));
        } else {
            //没找到数据
            currentState =viewState.noData;
            changeView();
        }
    }



    private void initBmob() {
        Bmob.initialize(this, "cd3366d572012e733dec8de33f65ded5");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.seartch:
                currentState=viewState.search;
                changeView();
                searchView.initData();
                break;
            case R.id.fl_fail:
                currentState =viewState.load;
                changeView();
                initData();
                break;
        }

    }
    public enum viewState{
        init,data,noData,search,fail,load;

    }
    public   void changeView(){
        switch (currentState){
            case init:
                //初始状态
                searchView.setVisibility(View.GONE);
                searchView.getListView().setVisibility(View.GONE);
                mGv.setVisibility(View.GONE);
                tvData.setVisibility(View.GONE);
                mlv.setVisibility(View.VISIBLE);
                llContent.setVisibility(View.VISIBLE);
                flFail.setVisibility(View.GONE);
                flRefresh.setVisibility(View.GONE);
                break;
            case noData:
                //没有任何数据状态
                searchView.setVisibility(View.VISIBLE);
                searchView.getListView().setVisibility(View.GONE);
                mGv.setVisibility(View.GONE);
                tvData.setVisibility(View.VISIBLE);
                mlv.setVisibility(View.GONE);
                llContent.setVisibility(View.GONE);
                flFail.setVisibility(View.GONE);
                flRefresh.setVisibility(View.GONE);
                break;
            case data:
                //有数据
                searchView.setVisibility(View.VISIBLE);
                searchView.getListView().setVisibility(View.GONE);
                mGv.setVisibility(View.VISIBLE);
                tvData.setVisibility(View.GONE);
                mlv.setVisibility(View.GONE);
                llContent.setVisibility(View.GONE);
                flFail.setVisibility(View.GONE);
                flRefresh.setVisibility(View.GONE);
                break;
            case search:
                //进入搜索状态
                searchView.setVisibility(View.VISIBLE);
                searchView.getListView().setVisibility(View.VISIBLE);
                mGv.setVisibility(View.GONE);
                tvData.setVisibility(View.GONE);
                mlv.setVisibility(View.GONE);
                llContent.setVisibility(View.GONE);
                flFail.setVisibility(View.GONE);
                flRefresh.setVisibility(View.GONE);
                break;
            case  fail:
                searchView.setVisibility(View.GONE);
                searchView.getListView().setVisibility(View.GONE);
                mGv.setVisibility(View.GONE);
                tvData.setVisibility(View.GONE);
                mlv.setVisibility(View.GONE);
                llContent.setVisibility(View.GONE);
                flFail.setVisibility(View.VISIBLE);
                flRefresh.setVisibility(View.GONE);
                break;
            case  load:
                searchView.setVisibility(View.GONE);
                searchView.getListView().setVisibility(View.GONE);
                mGv.setVisibility(View.GONE);
                tvData.setVisibility(View.GONE);
                mlv.setVisibility(View.GONE);
                llContent.setVisibility(View.GONE);
                flFail.setVisibility(View.GONE);
                flRefresh.setVisibility(View.VISIBLE);
                break;
        }

    }

}
