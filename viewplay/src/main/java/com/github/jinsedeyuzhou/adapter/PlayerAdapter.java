package com.github.jinsedeyuzhou.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.jinsedeyuzhou.R;
import com.github.jinsedeyuzhou.bean.PlayerFeed;

import java.util.List;

/**
 * Created by Berkeley on 5/3/17.
 */

public class PlayerAdapter extends BaseAdapter {

    private Context mContext;
    private List<PlayerFeed> list;

    public PlayerAdapter(List<PlayerFeed> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        PlayerFeed playerFeed = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_player, null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.ivImageView = (ImageView) convertView.findViewById(R.id.iv_img_view);
            viewHolder.tvNextPlay= (TextView) convertView.findViewById(R.id.tv_next_playing);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (playerFeed.getTypeSelected()==1)
        {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.bg_playing));
            viewHolder.tvNextPlay.setVisibility(View.GONE);

        }else if (playerFeed.getTypeSelected()==2)
        {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.bg_next));
            viewHolder.tvNextPlay.setVisibility(View.VISIBLE);
        }
        else
        {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.bg_normal));
            viewHolder.tvNextPlay.setVisibility(View.GONE);
        }
        viewHolder.tvTitle.setText(playerFeed.getTitle());
        setGlideDrawView(viewHolder.ivImageView, playerFeed.getImg());


        return convertView;
    }


    /**
     * 更新数据，替换原有数据
     */
    public void updateItems(List<PlayerFeed> lists) {
        this.list= lists;
        notifyDataSetChanged();
    }


    final static class ViewHolder {
        ImageView ivImageView;
        TextView tvTitle;
        TextView tvNextPlay;
    }

    public void setGlideDrawView(ImageView drawView, String strImg) {
        if (!TextUtils.isEmpty(strImg)) {
            Glide.with(mContext).load(Uri.parse(strImg)).placeholder(R.drawable.ic_user_default).diskCacheStrategy(DiskCacheStrategy.ALL).into(drawView);
        }
    }

}
