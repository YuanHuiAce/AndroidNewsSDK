package com.news.yazhidao.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.sdk.adapter.abslistview.CommonAdapter;
import com.news.sdk.adapter.abslistview.CommonViewHolder;
import com.news.sdk.database.NewsDetailCommentDao;
import com.news.sdk.entity.NewsDetailComment;
import com.news.sdk.pages.NewsDetailAty2;
import com.news.sdk.pages.NewsDetailVideoAty;
import com.news.sdk.pages.NewsFeedFgt;
import com.news.sdk.utils.DensityUtil;
import com.news.sdk.utils.TextUtil;
import com.news.sdk.utils.ToastUtil;
import com.news.yazhidao.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xiao on 2016/5/9.
 */
public class NewsDetailCommentAdapter extends CommonAdapter<NewsDetailComment> {
    private int daoHeight;
    private TextView clip_pic;
    public static final String KEY_NEWS_FEED = "key_news_feed";

    public void setClip_pic(TextView clip_pic) {
        this.clip_pic = clip_pic;
    }

    private OnDataIsNullListener onDataIsNullListener;


    private ArrayList<NewsDetailComment> mDatas = new ArrayList<NewsDetailComment>();
    private Context mContext;
    private NewsDetailCommentDao newsDetailCommentDao = null;

    public NewsDetailCommentDao getNewsDetailCommentDao() {
        return newsDetailCommentDao;
    }

    public void setNewsDetailCommentDao(NewsDetailCommentDao newsDetailCommentDao) {
        this.newsDetailCommentDao = newsDetailCommentDao;
    }

    public NewsDetailCommentAdapter(int layoutId, Context context, ArrayList<NewsDetailComment> datas) {
        super(layoutId, context, datas);
        mDatas = datas;
        mContext = context;
    }

    @Override
    public void convert(CommonViewHolder holder, final NewsDetailComment newsDetailCommentItem, int positon) {
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
//        if(newsDetailCommentItem.getUpflag() == 1){
//            love_imagebt.setImageResource(R.drawable.list_icon_gif_nor_icon_heart_selected);
//        }else {
//            love_imagebt.setImageResource(R.drawable.list_icon_gif_nor_icon_heart_nor);
//        }
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

//        ImageView del_icon = holder.getView(R.id.del_icon);
        original.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtil.isEmptyString(strTitle)) {
                    int nid = newsDetailCommentItem.getNid();
                    Intent intent;
                    if (newsDetailCommentItem.getRtype() == 6) {
                        intent = new Intent(mContext, NewsDetailVideoAty.class);
                    } else {
                        intent = new Intent(mContext, NewsDetailAty2.class);
                    }
                    intent.putExtra(NewsFeedFgt.KEY_NEWS_ID, nid+"");
                    mContext.startActivity(intent);
                }
            }
        });

        love_imagebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (newsDetailCommentItem.getUpflag() == 1) {//用户是否能对该条评论点赞，0、1 对应 可点、不可点
                    ToastUtil.toastShort("不可以给自己点赞！");
                } else {
                    ToastUtil.toastShort("不可以给自己点赞！");
                    //点赞动画
//                    newsDetailCommentItem.setPraise(!newsDetailCommentItem.isPraise());
//                    if (newsDetailCommentItem.isPraise()) {
//                        newsDetailCommentItem.setCommend(newsDetailCommentItem.getCommend() + 1);
//                        ((ImageButton) view).setImageResource(R.drawable.list_icon_gif_nor_icon_heart_selected);
//                        int[] location = new int[2];
//                        view.getLocationOnScreen(location);
//                        AnimatorSet set = new AnimatorSet();
//                        set.playTogether(ObjectAnimator.ofFloat(view,
//                                "scaleX", 1, 2, 1), ObjectAnimator
//                                .ofFloat(view, "scaleY", 1, 2, 1));
//                        set.setDuration(1 * 1000).start();
//                        clip_pic.setVisibility(View.VISIBLE);
//                        int l = location[0] + view.getMeasuredWidth() / 2;
//                        int t = location[1] - daoHeight - 60;
//                        int r = location[0] + view.getMeasuredWidth() / 2 + clip_pic.getMeasuredWidth();
//                        int b = location[1] + clip_pic.getMeasuredHeight() - daoHeight - 30;
////                    Toast.makeText(mContext, "l="+l+"  t="+t+"  r="+r+"  b="+b, Toast.LENGTH_SHORT).show();
////                    Log.e("xzj","l="+l+"  t="+t+"  r="+r+"  b="+b);
//                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        layoutParams.leftMargin = l;
//                        layoutParams.topMargin = t;
//                        clip_pic.setLayoutParams(layoutParams);
//                        clip_pic.requestLayout();
////                    clip_pic.layout(l,t,r,b);
////                    clip_pic.requestLayout();
//
////                    clip_pic.layout(
////                            location[0] + view.getMeasuredWidth()
////                                    / 2,
////                            location[1] - daoHeight,
////                            location[0]
////                                    + view.getMeasuredWidth()
////                                    / 2
////                                    + clip_pic
////                                    .getMeasuredWidth(),
////                            location[1]
////                                    + clip_pic
////                                    .getMeasuredHeight()
////                                    - daoHeight);
//
//                        AnimatorSet set1 = new AnimatorSet();
//                        set1.addListener(new Animator.AnimatorListener() {
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//                                //动画开始时将按钮设置成不可点击，防止用户频繁点击
//                                view.setClickable(false);
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                //动画结束后恢复按钮可点击
//                                notifyDataSetChanged();
//                                view.setClickable(true);
//                            }
//
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animator animation) {
//
//                            }
//                        });
//                        set1.playTogether(ObjectAnimator.ofFloat(
//                                clip_pic, "translationY", 0, -100),
//                                ObjectAnimator.ofFloat(clip_pic,
//                                        "alpha", 1, 0));
//                        set1.setInterpolator(new DecelerateInterpolator());
//                        set1.setDuration(1 * 1000).start();
//
//
//                        set1 = null;
//
//
//                    } else {
//                        newsDetailCommentItem.setCommend(newsDetailCommentItem.getCommend() - 1);
//
//                        ((ImageButton) view).setImageResource(R.drawable.list_icon_gif_nor_icon_heart_nor);
//                        notifyDataSetChanged();
//                    }
//
//
//                    newsDetailCommentDao.update(newsDetailCommentItem);
                }

            }
        });

        deleteCommentItem((ImageView) holder.getView(R.id.del_icon), positon);

//        addorDeleteLoveItem((RelativeLayout) holder.getView(R.id.love_layout), positon,newsDetailCommentItem);
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
//        deleteImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clickDeleteCommentItemListener.delete(position);
//            }
//        });

    }
//    public void addorDeleteLoveItem(RelativeLayout loveLayout,final int position,final NewsDetailComment newsDetailCommentItem){
//        loveLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int upflag = newsDetailCommentItem.getUpflag();
//                clickAddorDeleteLoveItemListener.addorDele(position, upflag);
//            }
//        });
//
//    }

    ClickDeleteCommentItemListener clickDeleteCommentItemListener;

    public void setClickDeleteCommentItemListener(ClickDeleteCommentItemListener clickDeleteCommentItemListener) {
        this.clickDeleteCommentItemListener = clickDeleteCommentItemListener;
    }

//    ClickAddorDeleteLoveItemListener clickAddorDeleteLoveItemListener;
//    public void setClickAddorDeleteLoveItemListener(ClickAddorDeleteLoveItemListener clickAddorDeleteLoveItemListener){
//        this.clickAddorDeleteLoveItemListener = clickAddorDeleteLoveItemListener;
//    }

    public interface ClickDeleteCommentItemListener {
        public void delete(int position);
    }
//    public interface  ClickAddorDeleteLoveItemListener{
//        public void addorDele(int upflag,int position);
//    }


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

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    public interface OnDataIsNullListener {
        void onChangeLayout();
    }

    public void setOnDataIsNullListener(OnDataIsNullListener onDataIsNullListener) {
        this.onDataIsNullListener = onDataIsNullListener;
    }

    public int getDaoHeight() {
        return daoHeight;
    }

    public void setDaoHeight(int daoHeight) {
        this.daoHeight = daoHeight;
    }

}
