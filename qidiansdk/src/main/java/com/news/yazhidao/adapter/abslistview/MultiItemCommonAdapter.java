package com.news.yazhidao.adapter.abslistview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.news.yazhidao.utils.Logger;

import java.util.ArrayList;

public abstract class MultiItemCommonAdapter<T> extends CommonAdapter<T> {

    protected MultiItemTypeSupport<T> mMultiItemTypeSupport;

    public MultiItemCommonAdapter(Context context, ArrayList<T> datas,
                                  MultiItemTypeSupport<T> multiItemTypeSupport) {
        super(-1, context, datas);
        mMultiItemTypeSupport = multiItemTypeSupport;
        if (mMultiItemTypeSupport == null)
            throw new IllegalArgumentException("the mMultiItemTypeSupport can not be null.");
    }

    @Override
    public int getViewTypeCount() {
        if (mMultiItemTypeSupport != null)
            return mMultiItemTypeSupport.getViewTypeCount();
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (mMultiItemTypeSupport != null)
            return mMultiItemTypeSupport.getItemViewType(position,
                    mDatas.get(position));
        return super.getItemViewType(position);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        long start = System.currentTimeMillis();
        if (mMultiItemTypeSupport == null)
            return super.getView(position, convertView, parent);
        int layoutId = mMultiItemTypeSupport.getLayoutId(position,
                getItem(position));
        CommonViewHolder viewHolder = CommonViewHolder.get(mContext, convertView, parent,
                layoutId, position);
        convert(viewHolder, getItem(position),position);
        Logger.e("jigang","consume time="+(System.currentTimeMillis()-start));
        return viewHolder.getConvertView();
    }

}
