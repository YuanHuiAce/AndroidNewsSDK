package com.news.sdk.adapter.abslistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by fengjigang on 16/4/14.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected ArrayList<T> mDatas;
    protected LayoutInflater mInflater;
    protected int mLayoutId;

    public CommonAdapter(int layoutId, Context mContext, ArrayList<T> mDatas) {
        this.mLayoutId = layoutId;
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return (mDatas == null) ? 0:mDatas.size();
    }

    public void setNewsFeed(ArrayList<T> arrNewsFeed) {
        mDatas = arrNewsFeed;
    }

    public ArrayList<T> getNewsFeed() {
        return mDatas;
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder viewHolder = CommonViewHolder.get(mContext, convertView, parent, mLayoutId, position);
        convert(viewHolder,getItem(position),position);
        return viewHolder.getConvertView();
    }

    public abstract void convert(CommonViewHolder holder, T t, int position);
}
