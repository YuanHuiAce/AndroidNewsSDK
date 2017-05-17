package com.news.sdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.sdk.R;
import com.news.sdk.entity.ChannelItem;
import com.news.sdk.utils.ImageUtil;
import com.news.sdk.utils.TextUtil;

import java.util.List;

/**
 * Created by fengjigang on 15/10/28.
 * 新闻频道不可拖拽 gridview 适配器
 */
public class ChannelNormalAdapter extends BaseAdapter {
    private Context context;
    public List<ChannelItem> channelList;
    private TextView item_text;
    private ImageView icon;
    private boolean isItemShow = false;
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
     * 要删除的position
     */
    public int remove_position = -1;

    public ChannelNormalAdapter(Context context, List<ChannelItem> channelList) {
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
        ImageUtil.setAlphaImage(icon, R.drawable.bg_channel_add);
        int height = context.getResources().getDrawable(R.drawable.bg_channel_add).getMinimumHeight();
        RelativeLayout.LayoutParams rlItemText = (RelativeLayout.LayoutParams) item_text.getLayoutParams();
        rlItemText.topMargin = height / 2;
        item_text.setLayoutParams(rlItemText);
        TextUtil.setLayoutBgResource(context, item_text, R.drawable.subscribe_item_bg);
        TextUtil.setTextColor(context, item_text, R.color.color2);
        icon.setVisibility(View.VISIBLE);
        ChannelItem channel = getItem(position);
        item_text.setText(channel.getCname());
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
     * 获取频道列表
     */
    public List<ChannelItem> getChannnelLst() {
        return channelList;
    }

    /**
     * 添加频道列表
     */
    public void addItem(ChannelItem channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    /**
     * 设置删除的position
     */
    public synchronized void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public synchronized void remove() {
        synchronized (channelList) {
            channelList.remove(remove_position);
            remove_position = -1;
            notifyDataSetChanged();
        }
    }

    /**
     * 设置频道列表
     */
    public void setListDate(List<ChannelItem> list) {
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

    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }
}