package com.news.yazhidao.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.ChannelItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fengjigang on 15/10/28.
 * 新闻频道可拖拽gridview 适配器
 */
public class ChannelSelectedAdapter extends BaseAdapter {
    /**
     * TAG
     */
    private final static String TAG = "DragAdapter";
    /**
     * 是否显示底部的ITEM
     */
    private boolean isItemShow = false;
    private Context context;
    /**
     * 控制的postion
     */
    private int holdPosition;
    /**
     * 是否改变
     */
    private boolean isChanged = false;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    /**
     * 可以拖动的列表（即用户选择的频道列表）
     */
    public List<ChannelItem> channelList;
    /**
     * TextView 频道内容
     */
    private TextView item_text;
    /**
     * 要删除的position
     */
    public int remove_position = -1;

    private ImageView icon;

    public ChannelSelectedAdapter(Context context, List<ChannelItem> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @Override
    public int getCount() {
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public ChannelItem getItem(int position) {
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.subscribe_category_item, null);
        item_text = (TextView) view.findViewById(R.id.text_item);
        icon = (ImageView) view.findViewById(R.id.icon_new);
        icon.setBackgroundResource(R.drawable.bg_channel_delete);
        int height = context.getResources().getDrawable(R.drawable.bg_channel_delete).getMinimumHeight();
        RelativeLayout.LayoutParams rlItemText = (RelativeLayout.LayoutParams) item_text.getLayoutParams();
        rlItemText.topMargin = height / 2;
        item_text.setLayoutParams(rlItemText);

        ChannelItem channel = getItem(position);
        item_text.setText(channel.getName());
        if (position == 0) {
            item_text.setTextColor(context.getResources().getColor(R.color.subscribe_item_drag_stroke));
            item_text.setEnabled(false);
            icon.setVisibility(View.GONE);
        } else {
            icon.setVisibility(View.VISIBLE);
        }
        if (isChanged && (position == holdPosition) && !isItemShow) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
            isChanged = false;
            icon.setVisibility(View.GONE);
        }
        if (!isVisible && (position == -1 + channelList.size())) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
            icon.setVisibility(View.GONE);
        }
        if (remove_position == position) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
            icon.setVisibility(View.GONE);
        }
        return view;
    }

    /**
     * 添加频道列表
     */
    public void addItem(ChannelItem channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    /**
     * 拖动变更频道排序
     */
    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        ChannelItem dragItem = getItem(dragPostion);
        Log.d(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
        if (dragPostion < dropPostion) {
            channelList.add(dropPostion + 1, dragItem);
            channelList.remove(dragPostion);
        } else {
            channelList.add(dropPostion, dragItem);
            channelList.remove(dragPostion + 1);
        }
        isChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 获取频道列表
     */
    public List<ChannelItem> getChannnelLst() {
        return channelList;
    }

    /**
     * 获取频道列表
     */
    public ArrayList<ChannelItem> getChannnelList() {
        if (channelList != null) {
            return new ArrayList<>(channelList);
        }
        return null;
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public void remove() {
        channelList.remove(remove_position);
        remove_position = -1;
        notifyDataSetChanged();
    }

    /**
     * 设置频道列表
     */
    public void setListData(List<ChannelItem> list) {
        channelList = list;
    }

    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * 显示放下的ITEM
     */
    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }
}