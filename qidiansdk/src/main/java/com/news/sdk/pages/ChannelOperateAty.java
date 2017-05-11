package com.news.sdk.pages;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.sdk.R;
import com.news.sdk.adapter.ChannelNormalAdapter;
import com.news.sdk.adapter.ChannelSelectedAdapter;
import com.news.sdk.common.BaseActivity;
import com.news.sdk.common.ThemeManager;
import com.news.sdk.database.ChannelItemDao;
import com.news.sdk.entity.ChannelItem;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.widget.channel.NormalGridView;
import com.news.sdk.widget.channel.SelectedGridView;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * 新闻频道管理
 */
public class ChannelOperateAty extends BaseActivity implements OnItemClickListener, ThemeManager.OnThemeChangeListener {
    public static final String KEY_USER_SELECT = "key_user_select";
    /**
     * 用户栏目的GRIDVIEW
     */
    private SelectedGridView userGridView;
    /**
     * 其它栏目的GRIDVIEW
     */
    private NormalGridView otherGridView;
    /**
     * 用户栏目对应的适配器，可以拖动
     */
    ChannelSelectedAdapter userAdapter;
    /**
     * 其它栏目对应的适配器
     */
    ChannelNormalAdapter otherAdapter;
    /**
     * 其它栏目列表
     */
    ArrayList<ChannelItem> otherChannelList = new ArrayList<ChannelItem>();
    /**
     * 用户栏目列表
     */
    ArrayList<ChannelItem> selectedChannelList = new ArrayList<ChannelItem>();
    ArrayList<ChannelItem> selectedChannelListCurrent = new ArrayList<ChannelItem>();
    /**
     * 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。
     */
    boolean isMove = false;
    private ChannelItemDao mDao;

    private View mDetailLeftBack;
    private LinearLayout bgLayout;
    private LinearLayout bgMyChannel, bgMoreChannel;
    private TextView tvCategory, tvMoreCategory, tvTitle;
    private RelativeLayout rlTitle;

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_channnel_operate);
    }

    @Override
    protected void initializeViews() {
        bgLayout = (LinearLayout) findViewById(R.id.subscribe_main_layout);
        rlTitle = (RelativeLayout) findViewById(R.id.title_bar);
        bgMyChannel = (LinearLayout) findViewById(R.id.my_channel_layout);
        bgMoreChannel = (LinearLayout) findViewById(R.id.more_channel_layout);
        userGridView = (SelectedGridView) findViewById(R.id.userGridView);
        otherGridView = (NormalGridView) findViewById(R.id.otherGridView);
        tvTitle = (TextView) findViewById(R.id.title);
        tvCategory = (TextView) findViewById(R.id.my_category_text);
        tvMoreCategory = (TextView) findViewById(R.id.more_category_text);
        mDetailLeftBack = findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                ChannelOperateAty.this.finish();
            }
        });
        ThemeManager.registerThemeChangeListener(this);
        TextUtil.setLayoutBgResource(this, rlTitle, R.color.white);
        TextUtil.setLayoutBgResource(this, bgLayout, R.color.white);
        TextUtil.setLayoutBgResource(this, bgMyChannel, R.color.white);
        TextUtil.setLayoutBgResource(this, bgMoreChannel, R.color.white);
        TextUtil.setTextColor(this, tvCategory, R.color.new_color7);
        TextUtil.setTextColor(this, tvMoreCategory, R.color.new_color7);
        TextUtil.setTextColor(this, tvTitle, R.color.newsFeed_titleColor);
    }

    @Override
    protected void loadData() {
        mDao = new ChannelItemDao(this);
        selectedChannelList = mDao.queryForSelected();
        selectedChannelListCurrent = mDao.queryForSelected();
        otherChannelList = mDao.queryForNormal();
        userAdapter = new ChannelSelectedAdapter(this, selectedChannelList);
        userGridView.setAdapter(userAdapter);
        otherAdapter = new ChannelNormalAdapter(this, otherChannelList);
        otherGridView.setAdapter(this.otherAdapter);
        //设置GRIDVIEW的ITEM的点击监听
        otherGridView.setOnItemClickListener(this);
        userGridView.setOnItemClickListener(this);
    }

    @Override
    public void onThemeChanged() {
        TextUtil.setLayoutBgResource(this, rlTitle, R.color.white);
        TextUtil.setLayoutBgResource(this, bgLayout, R.color.white);
        TextUtil.setLayoutBgResource(this, bgMyChannel, R.color.white);
        TextUtil.setLayoutBgResource(this, bgMoreChannel, R.color.white);
        TextUtil.setTextColor(this, tvCategory, R.color.new_color7);
        TextUtil.setTextColor(this, tvMoreCategory, R.color.new_color7);
        TextUtil.setTextColor(this, tvTitle, R.color.newsFeed_titleColor);
        userAdapter.notifyDataSetChanged();
        otherAdapter.notifyDataSetChanged();
    }

    /**
     * GRIDVIEW对应的ITEM点击监听接口
     */
    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
        //如果点击的时候，之前动画还没结束，那么就让点击事件无效
        if (isMove) {
            return;
        }
        if (parent.getId() == R.id.userGridView) {
            //position为 0，1 的不可以进行任何操作
            if (position != 0) {
                isMove = true;
                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final ChannelItem channel = ((ChannelSelectedAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                    otherAdapter.setVisible(false);
                    //添加到最后一个
                    otherAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, channel, userGridView);
                                userAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
            }
        } else if (parent.getId() == R.id.otherGridView) {
            final ImageView moveImageView = getView(view);
            if (moveImageView != null) {
                isMove = true;
                TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                final int[] startLocation = new int[2];
                newTextView.getLocationInWindow(startLocation);
                final ChannelItem channel = ((ChannelNormalAdapter) parent.getAdapter()).getItem(position);
                userAdapter.setVisible(false);
                //添加到最后一个
                userAdapter.addItem(channel);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        try {
                            int[] endLocation = new int[2];
                            //获取终点的坐标
                            userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                            MoveAnim(moveImageView, startLocation, endLocation, channel, otherGridView);
                            otherAdapter.setRemove(position);
                        } catch (Exception localException) {
                        }
                    }
                }, 50L);
            }
        }
    }


    /**
     * 点击ITEM移动动画
     *
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param moveChannel
     * @param clickGridView
     */
    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final ChannelItem moveChannel,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (moveViewGroup != null && mMoveView != null) {
                    moveViewGroup.removeView(mMoveView);
                }

                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof SelectedGridView) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                } else {
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     *
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 获取点击的Item的对应View，
     *
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    /**
     * 退出时候保存选择后数据库的设置
     */
    private void saveChannel() throws SQLException {
        mDao.deletaForAll();
        mDao.insertSelectedList(userAdapter.getChannnelList());
        mDao.insertNormalList(otherAdapter.getChannnelLst());
    }


    @Override
    public void onBackPressed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    saveChannel();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        ArrayList<ChannelItem> channelItems = userAdapter.getChannnelList();
        if (selectedChannelListCurrent.size() != channelItems.size()) {
            Intent data = new Intent();
            data.putExtra(KEY_USER_SELECT, channelItems);
            setResult(MainAty.REQUEST_CODE, data);
        } else {
            for (int i = 0; i < selectedChannelListCurrent.size(); i++) {
                if (selectedChannelListCurrent.get(i).getId() != channelItems.get(i).getId()) {
                    Intent data = new Intent();
                    data.putExtra(KEY_USER_SELECT, channelItems);
                    setResult(MainAty.REQUEST_CODE, data);
                    break;
                }
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        ThemeManager.unregisterThemeChangeListener(this);
        super.onDestroy();
    }

}
