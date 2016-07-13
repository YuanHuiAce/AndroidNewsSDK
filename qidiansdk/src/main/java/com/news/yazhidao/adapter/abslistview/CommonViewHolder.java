package com.news.yazhidao.adapter.abslistview;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.widget.TextViewExtend;

/**
 * Created by fengjigang on 16/4/14.
 * ListView GridView 通用ViewHolder
 */
public class CommonViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private Context mContext;
    private View mConvertView;
    private int mLayoutId;
    private SharedPreferences mSharedPreferences;
    public CommonViewHolder(Context mContext, View mConvertView, ViewGroup mViewGroup, int mPosition) {
        this.mContext = mContext;
        this.mConvertView = mConvertView;
        this.mPosition = mPosition;
        this.mViews = new SparseArray<>();
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mConvertView.setTag(this);
    }

    public static CommonViewHolder get(Context mContext, View mConvertView, ViewGroup mViewGroup, int mLayoutId, int mPosition){
        if (mConvertView == null){
            View itemView = LayoutInflater.from(mContext).inflate(mLayoutId, mViewGroup, false);
            CommonViewHolder viewHolder = new CommonViewHolder(mContext, itemView, mViewGroup, mPosition);
            viewHolder.mLayoutId = mLayoutId;
            return viewHolder;
        }else {
            CommonViewHolder viewHolder = (CommonViewHolder) mConvertView.getTag();
            viewHolder.mPosition = mPosition;
            return viewHolder;
        }
    }

    public <T extends View> T getView(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
    public ImageView getImageView(int ViewID){
        ImageView image = getView(ViewID);
        return image;
    }
    public void setTextViewText(int ViewID ,String content){
        TextView text = getView(ViewID);
        text.setText(content);
    }
    public void setTextViewTextBackgroundResource(int ViewID ,int resource){
        TextView text = getView(ViewID);
        text.setBackgroundResource(resource);
    }

    public void setTextViewExtendText(int ViewID ,String content){
        TextViewExtend text = getView(ViewID);
        text.setText(content);
    }
    public void setTextViewExtendTextandTextSice(int ViewID ,String content){
        TextViewExtend text = getView(ViewID);
        text.setText(content);
        text.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
    }
    public void setTextViewExtendTextColor(int ViewID ,int color){
        TextViewExtend text = getView(ViewID);
        text.setTextColor(mContext.getResources().getColor(color));
    }
    public void setTextViewExtendTextBackground(int ViewID ,int color){
        TextViewExtend text = getView(ViewID);
        text.setBackgroundColor(mContext.getResources().getColor(color));
    }
    public void setTextViewExtendTextBackgroundResource(int ViewID ,int resource){
        TextViewExtend text = getView(ViewID);
        text.setBackgroundResource(resource);
    }


    public View getConvertView()
    {
        return mConvertView;
    }

    public int getLayoutId()
    {
        return mLayoutId;
    }

    public void setSimpleDraweeViewURI(int draweeView, String strImg) {
        SimpleDraweeView imageView = (SimpleDraweeView)getView(draweeView);
        if (!TextUtil.isEmptyString(strImg)) {
            imageView.setImageURI(Uri.parse(strImg));
            imageView.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0.4F));
        }
    }
}
