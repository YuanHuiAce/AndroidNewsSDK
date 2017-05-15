package com.news.yazhidao.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.sdk.adapter.abslistview.CommonAdapter;
import com.news.sdk.adapter.abslistview.CommonViewHolder;
import com.news.sdk.entity.NewsDetailComment;
import com.news.sdk.pages.NewsDetailAty2;
import com.news.sdk.pages.NewsDetailVideoAty;
import com.news.sdk.pages.NewsFeedFgt;
import com.news.sdk.utils.DensityUtil;
import com.news.sdk.utils.TextUtil;
import com.news.yazhidao.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NewsDetailCommentAdapter extends CommonAdapter<NewsDetailComment> {

    private ArrayList<NewsDetailComment> mDatas = new ArrayList<>();
    private Context mContext;

    public NewsDetailCommentAdapter(int layoutId, Context context, ArrayList<NewsDetailComment> datas) {
        super(layoutId, context, datas);
        mDatas = datas;
        mContext = context;
    }

    @Override
    public void convert(CommonViewHolder holder, final NewsDetailComment newsDetailCommentItem, final int position) {
        TextView pub_time = holder.getView(R.id.pub_time);
        pub_time.setText(convertTime(newsDetailCommentItem.getCtime()));
        TextView comment_content = holder.getView(R.id.comment_item_comment_content);
        String string = newsDetailCommentItem.getContent();
        comment_content.setText(string);
        TextView original = holder.getView(R.id.original);
        final String strTitle = newsDetailCommentItem.getNtitle();
        CharSequence titleStr = Html.fromHtml("<b>【原文】</b>" + (TextUtil.isEmptyString(strTitle) ? "该新闻已不存在" : strTitle));
        original.setText(titleStr);
        ImageButton love_imagebt = holder.getView(R.id.love_imagebt);
        if (newsDetailCommentItem.getUpflag() == 0) {
            love_imagebt.setImageResource(R.mipmap.list_icon_gif_nor_icon_heart_nor);
        } else {
            love_imagebt.setImageResource(R.mipmap.list_icon_gif_nor_icon_heart_selected);
        }
        int love_num = newsDetailCommentItem.getCommend();
        final TextView love_count = holder.getView(R.id.love_count);
        if (love_num > 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) love_imagebt.getLayoutParams();
            params.rightMargin = DensityUtil.dip2px(mContext, 25);
            love_imagebt.setLayoutParams(params);
            love_count.setVisibility(View.VISIBLE);
            love_count.setText(love_num + "");
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) love_imagebt.getLayoutParams();
            params.rightMargin = DensityUtil.dip2px(mContext, 19);
            love_imagebt.setLayoutParams(params);
            love_count.setVisibility(View.GONE);
        }
        original.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtil.isEmptyString(strTitle)) {
                    int nid = newsDetailCommentItem.getNid();
                    Intent intent;
                    int pType = newsDetailCommentItem.getRtype();
                    if (pType == 6 || pType == 8) {
                        intent = new Intent(mContext, NewsDetailVideoAty.class);
                    } else {
                        intent = new Intent(mContext, NewsDetailAty2.class);
                    }
                    intent.putExtra(NewsFeedFgt.KEY_NEWS_ID, nid + "");
                    mContext.startActivity(intent);
                }
            }
        });
        love_imagebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (newsDetailCommentItem.getUpflag() == 1) {//用户是否能对该条评论点赞，0、1 对应 可点、不可点
                    clickAddorDeleteLoveItemListener.addOrDele(1, position);
                } else {
                    clickAddorDeleteLoveItemListener.addOrDele(0, position);
                }
            }
        });
        deleteCommentItem((ImageView) holder.getView(R.id.del_icon), position);
    }

    public void deleteCommentItem(ImageView deleteImage, final int position) {
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("确认删除此条评论吗?");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        clickDeleteCommentItemListener.delete(position);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    ClickDeleteCommentItemListener clickDeleteCommentItemListener;

    public void setClickDeleteCommentItemListener(ClickDeleteCommentItemListener clickDeleteCommentItemListener) {
        this.clickDeleteCommentItemListener = clickDeleteCommentItemListener;
    }

    ClickAddOrDeleteLoveItemListener clickAddorDeleteLoveItemListener;

    public void setClickAddOrDeleteLoveItemListener(ClickAddOrDeleteLoveItemListener clickAddOrDeleteLoveItemListener) {
        this.clickAddorDeleteLoveItemListener = clickAddOrDeleteLoveItemListener;
    }

    public interface ClickDeleteCommentItemListener {
        void delete(int position);
    }

    public interface ClickAddOrDeleteLoveItemListener {
        void addOrDele(int upFlag, int position);
    }

    private static String convertTime(String oldTime) {
        String temp;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date old = null;
        try {
            old = dateFormat.parse(oldTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeGap = System.currentTimeMillis() - old.getTime();
        DateFormat sdf = new SimpleDateFormat("MM月dd日");
        if (timeGap < 60000) {//一分钟
            temp = "刚刚";
        } else if (timeGap < 60 * 60000) {//一小时
            temp = (timeGap / 60000) + "分钟前";
        } else if (timeGap < 24 * 60 * 60000) {
            temp = (timeGap / (60 * 60000)) + "小时前";
        } else if (timeGap < yesterday(1)) {
            temp = "昨天";
        } else if (timeGap < yesterday(2)) {
            temp = "前天";
        } else {
            temp = sdf.format(old);
        }
        return temp;
    }

    private static long yesterday(int i) {
        long l = 0;
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -i);
        String yesterday = new SimpleDateFormat("yyyy-MM-dd ").format(calendar.getTime());
        try {
            date = sdf.parse(yesterday);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        l = System.currentTimeMillis() - date.getTime();
        return l;
    }
}
