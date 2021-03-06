package com.example.ronny_xie.gdcp.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ronny_xie.gdcp.Fragment.chat.CreateGroupSelectUser;
import com.example.ronny_xie.gdcp.Fragment.chat.adapter.MessageListAdapter;
import com.example.ronny_xie.gdcp.Fragment.chat.page.ChatPage;
import com.example.ronny_xie.gdcp.Fragment.chat.page.NotifyListPage;
import com.example.ronny_xie.gdcp.Fragment.chat.page.SearchPage;
import com.example.ronny_xie.gdcp.R;
import com.example.ronny_xie.gdcp.util.DialogUtil;
import com.example.ronny_xie.gdcp.util.OnDialogListener;
import com.example.ronny_xie.gdcp.util.ProgressDialogUtil;
import com.example.ronny_xie.gdcp.util.ToastUtil;
import com.example.ronny_xie.gdcp.view.SwipeMenu;
import com.example.ronny_xie.gdcp.view.SwipeMenuCreator;
import com.example.ronny_xie.gdcp.view.SwipeMenuItem;
import com.example.ronny_xie.gdcp.view.SwipeMenuListView;
import com.example.ronny_xie.gdcp.view.SwipeMenuListView.OnMenuItemClickListener;
import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeChatTargetType;
import com.gotye.api.GotyeCustomerService;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMedia;
import com.gotye.api.GotyeRoom;
import com.gotye.api.GotyeUser;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.List;

//import android.app.Fragment;


//此页面为回话历史页面，由客户端自己实现
@SuppressLint("NewApi")
public class messageFragment extends Fragment {
    private SwipeMenuListView listView;
    private MessageListAdapter adapter;
    private static final String TAG = "messageFragment";
    public static final String fixName = "通知列表";
    private GotyeAPI api = GotyeAPI.getInstance();
    private FloatingActionMenu actionMenu;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_page, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GotyeAPI.getInstance().addListener(mDelegate);
        initView();
        initFABButton();
    }

    private void initFABButton() {
        ImageView icon = new ImageView(getActivity());
        icon.setImageDrawable(getResources().getDrawable(R.drawable.fab_floatmain));
        final FloatingActionButton actionButton = new FloatingActionButton
                .Builder(getActivity()).setContentView(icon).build();

        SubActionButton.Builder itemBUilder = new SubActionButton.Builder(getActivity());

        ImageView itemIcon1 = new ImageView(getActivity());
        itemIcon1.setImageDrawable(getResources().getDrawable(R.drawable.fab_addfriend));
        SubActionButton button1 = itemBUilder.setContentView(itemIcon1).build();

        ImageView itemIcon2 = new ImageView(getActivity());
        SubActionButton button2 = itemBUilder.setContentView(itemIcon2).build();
        itemIcon2.setImageDrawable(getResources().getDrawable(R.drawable.fab_groupchat));

        ImageView itemIcon3 = new ImageView(getActivity());
        SubActionButton button3 = itemBUilder.setContentView(itemIcon3).build();
        itemIcon3.setImageDrawable(getResources().getDrawable(R.drawable.fab_moresearch));

        ImageView itemIcon4 = new ImageView(getActivity());
        SubActionButton button4 = itemBUilder.setContentView(itemIcon4).build();
        itemIcon4.setImageDrawable(getResources().getDrawable(R.drawable.fab_searchfriend));

        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addSingelUser();
                actionMenu.close(true);
            }
        });

        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCreateGroup = new Intent(getActivity(), CreateGroupSelectUser.class);
                startActivity(toCreateGroup);
            }
        });
        button3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addChatView();
            }
        });
        button4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSreach = new Intent(getActivity(), SearchPage.class);
                toSreach.putExtra("search_type", 0);
                startActivity(toSreach);
            }
        });

        actionMenu = new FloatingActionMenu
                .Builder(getActivity())
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .addSubActionView(button4)
                .attachTo(actionButton).build();
        final Thread[] thread = {null};
        final boolean[] isThread_run = {false};
        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!actionMenu.isOpen()) {
                    actionMenu.open(true);
                } else {
                    actionMenu.close(true);
                    if (!isThread_run[0] && thread[0] != null) {
                        thread[0] = null;
                        return;
                    }
                }
                isThread_run[0] = true;
                thread[0] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (actionMenu.isOpen()) {
                                        actionMenu.close(true);
                                        isThread_run[0] = false;
                                    }
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread[0].start();
            }
        });
    }

    private void initView() {
        ImageView imageView_back = (ImageView) getView().findViewById(R.id.back);
        imageView_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TextView title = (TextView) getView().findViewById(R.id.title);
        title.setText("消息");
        listView = (SwipeMenuListView) getView().findViewById(R.id.listview);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                switch (menu.getViewType()) {
                    case 0:
                        createMenu1(menu);
                        break;
                    case 1:
                        createMenu2(menu);
                        break;
                }
            }
        };
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(int position, SwipeMenu menu,
                                           int index) {
                GotyeChatTarget target = adapter.getItem(position);
                api.deleteSession(target, false);
                updateList();
                return false;
            }
        });
        int state = api.isOnline();
        if (state != 1) {
            setErrorTip(0);
        } else {
            setErrorTip(1);
        }
        updateList();
        setListener();
    }

    private void createMenu1(SwipeMenu menu) {

    }

    private void createMenu2(SwipeMenu menu) {
        SwipeMenuItem item2 = new SwipeMenuItem(getActivity());
        item2.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
        item2.setWidth(dp2px(70));
        item2.setIcon(R.drawable.ic_action_discard);
        menu.addMenuItem(item2);
    }

    private void setListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                GotyeChatTarget target = (GotyeChatTarget) adapter
                        .getItem(arg2);
                if (target.getName().equals(fixName)) {
                    Intent i = new Intent(getActivity(), NotifyListPage.class);
                    startActivity(i);
                } else {
                    GotyeAPI.getInstance().markMessagesAsRead(target, true);
                    if (target.getType() == GotyeChatTargetType.GotyeChatTargetTypeUser) {
                        Intent toChat = new Intent(getActivity(),
                                ChatPage.class);
                        toChat.putExtra("user", (GotyeUser) target);
                        startActivity(toChat);
                        // updateList();
                    } else if (target.getType() == GotyeChatTargetType.GotyeChatTargetTypeRoom) {
                        Intent toChat = new Intent(getActivity(),
                                ChatPage.class);
                        toChat.putExtra("room", (GotyeRoom) target);
                        startActivity(toChat);

                    } else if (target.getType() == GotyeChatTargetType.GotyeChatTargetTypeGroup) {
                        Intent toChat = new Intent(getActivity(),
                                ChatPage.class);
                        toChat.putExtra("group", (GotyeGroup) target);
                        startActivity(toChat);
                    } else if (target.getType() == GotyeChatTargetType.GotyeChatTargetTypeCustomerService) {
                        Intent toChat = new Intent(getActivity(),
                                ChatPage.class);
                        toChat.putExtra("cserver",
                                (GotyeCustomerService) target);
                        startActivity(toChat);
                    }
                    refresh();
                }
            }
        });
    }

    private void updateList() {
        List<GotyeChatTarget> sessions = api.getSessionList();
        Log.d("onSession", "List--sessions" + sessions);
        GotyeChatTarget target = new GotyeUser(fixName);

        if(sessions == null){
            sessions = new ArrayList<GotyeChatTarget>();
            sessions.add(target);
        }else{
            if(api.getUnreadNotifyCount() != 0){
                sessions.add(0, target);
            }
        }

        if (adapter == null) {
            adapter = new MessageListAdapter(messageFragment.this, sessions);
            listView.setAdapter(adapter);
        } else {
            adapter.setData(sessions);
        }

    }

    public void refresh() {
        updateList();
    }

    @Override
    public void onDestroy() {
        GotyeAPI.getInstance().removeListener(mDelegate);
        super.onDestroy();

    }

    private void setErrorTip(int code) {
        // code=api.getOnLineState();
        if (code == 1) {
            getView().findViewById(R.id.error_tip).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.error_tip).setVisibility(View.VISIBLE);
            if (code == -1) {
                getView().findViewById(R.id.loading)
                        .setVisibility(View.VISIBLE);
                ((TextView) getView().findViewById(R.id.showText))
                        .setText("连接中...");
                getView().findViewById(R.id.error_tip_icon).setVisibility(
                        View.GONE);
            } else {
                getView().findViewById(R.id.loading).setVisibility(View.GONE);
                ((TextView) getView().findViewById(R.id.showText))
                        .setText("未连接");
                getView().findViewById(R.id.error_tip_icon).setVisibility(
                        View.VISIBLE);
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private GotyeDelegate mDelegate = new GotyeDelegate() {

        @Override
        public void onDownloadMedia(int code, GotyeMedia media) {
            // TODO Auto-generated method stub
            if (getActivity().isTaskRoot()) {
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onLogout(int code) {
            setErrorTip(0);
        }

        @Override
        public void onLogin(int code, GotyeUser currentLoginUser) {
            setErrorTip(1);
        }

        @Override
        public void onReconnecting(int code, GotyeUser currentLoginUser) {
            setErrorTip(-1);
        }
    };

    private void addChatView() {
        new DialogUtil(getActivity(),"请输入对方ID",R.layout.inflate_chat_stanger,"取消","确定")
                .setDialogListener(new OnDialogListener() {
                    @Override
                    public void dialogNegativeListener(View customView, DialogInterface dialogInterface, int which) {

                    }
                    @Override
                    public void dialogPositiveListener(View customView, DialogInterface dialogInterface, int which) {
                        EditText tv = (EditText) customView.findViewById(R.id.chat_stanger_edittext);
                        chat_stanger_ID = tv.getText().toString().trim();
                        if (chat_stanger_ID.equals("")){
                            ToastUtil.show(getActivity(),"请输入Id");
                        }else {
                            Intent tochat = new Intent(getActivity(),
                                    ChatPage.class);
                            tochat.putExtra("user", new GotyeUser(chat_stanger_ID));
                            startActivity(tochat);
                        }
                    }
                }).showDialog();

    }

    private PopupWindow tools;

//    private void showTools(View v) {
//        View toolsLayout = LayoutInflater.from(getActivity()).inflate(
//                R.layout.layout_tools, null);
//        toolsLayout.findViewById(R.id.tools_add).setOnClickListener(this);
//        toolsLayout.findViewById(R.id.tools_add_single)
//                .setOnClickListener(this);
//        toolsLayout.findViewById(R.id.tools_group_chat)
//                .setOnClickListener(this);
//        tools = new PopupWindow(toolsLayout, LayoutParams.WRAP_CONTENT,
//                LayoutParams.WRAP_CONTENT, true);
//        tools.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        tools.setOutsideTouchable(false);
//        tools.showAsDropDown(v, 0, 20);
//        tools.setAnimationStyle(R.style.mypopwindow_anim_style);
//        tools.update();
//    }

    private void addUser() {
        if (tools.isShowing()) {
            tools.dismiss();
            Intent toSreach = new Intent(getActivity(), SearchPage.class);
            toSreach.putExtra("search_type", 0);
            startActivity(toSreach);
        }
    }

    private void addSingelUser() {
        new DialogUtil(getActivity(),"添加好友",R.layout.inflate_chat_stanger,"取消","确定")
                .setDialogListener(new OnDialogListener() {
                    @Override
                    public void dialogNegativeListener(View customView, DialogInterface dialogInterface, int which) {

                    }

                    @Override
                    public void dialogPositiveListener(View customView, DialogInterface dialogInterface, int which) {
                        EditText tv = (EditText) customView.findViewById(R.id.chat_stanger_edittext);
                        String name = tv.getText().toString();
                        if (!TextUtils.isEmpty(name)) {
                            if (name.equals(currentLoginName)) {
                                ToastUtil.show(getActivity(),
                                        "不能添加自己");
                                return;
                            }
                            ProgressDialogUtil.showProgress(
                                    getActivity(), "正在添加好友...");
                            api.reqAddFriend(new GotyeUser(name));
                            showAddFriendTip = true;
                        }
                    }
                }).showDialog();
    }

    public String currentLoginName;
    private boolean showAddFriendTip = false;
    private String chat_stanger_ID;

    // 背景透明度
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }
}
