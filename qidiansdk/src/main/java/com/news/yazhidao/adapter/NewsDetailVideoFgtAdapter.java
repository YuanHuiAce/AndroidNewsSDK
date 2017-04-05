package com.news.yazhidao.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.entity.RelatedItemEntity;
import com.news.yazhidao.pages.NewsDetailVideoAty;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;

/**
 * Created by Administrator on 2016/4/22.
 */
public class NewsDetailVideoFgtAdapter extends CommonAdapter<RelatedItemEntity> {

    private Activity mContext;
    public static final int REQUEST_CODE = 1030;
    private int mCardWidth, mCardHeight;
    private int mScreenWidth;

    public NewsDetailVideoFgtAdapter(Activity context) {
        super(R.layout.item_news_detail_video_relate_attention, context, null);
        this.mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mCardWidth = (int) ((mScreenWidth - DensityUtil.dip2px(mContext, 32)) / 3.0f);
        mCardHeight = (int) (mCardWidth * 213 / 326.0f);
    }


    @Override
    public void convert(CommonViewHolder holder, RelatedItemEntity relatedItemEntity, int position) {
        if (relatedItemEntity.getUrl().equals("-1")) {//没有数据时也可以让listView滑动
            holder.getView(R.id.attentionlayout).setVisibility(View.GONE);
            Logger.e("aaa", "没有数据时的状况！！！！！！！！！！！！！");
//            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(0, 0);
//            holder.getView(R.id.attentionlayout).setLayoutParams(layoutParams);
            return;
        }
        onAttentionItemClickListener((RelativeLayout) holder.getView(R.id.attentionlayout), relatedItemEntity);
        TextView title = holder.getView(R.id.attention_Title);
        String strTitle =  relatedItemEntity.getTitle().replace("<font color='#0091fa' >","").replace("</font>","");
        title.setText(strTitle);
        holder.setTextViewExtendText(R.id.attention_Source, relatedItemEntity.getPname());

        if (getCount() == position + 1) {//去掉最后一条的线
            holder.getView(R.id.attention_bottomLine).setVisibility(View.GONE);
        } else {
            holder.getView(R.id.attention_bottomLine).setVisibility(View.VISIBLE);
        }
        ImageView ivCard = holder.getView(R.id.title_img_View);
        String imgUrl = relatedItemEntity.getImgUrl();
        if (TextUtil.isEmptyString(imgUrl)) {
            ivCard.setVisibility(View.GONE);
        } else {
            ivCard.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
            lpCard.width = mCardWidth;
            lpCard.height = mCardHeight;
            ivCard.setLayoutParams(lpCard);
            Glide.with(mContext).load(imgUrl).centerCrop().placeholder(R.drawable.bg_load_default_small).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivCard);
        }
        setVideoDuration((TextView) holder.getView(R.id.tv_video_duration),relatedItemEntity.getDuration());
    }

    public void setVideoDuration(TextView durationView, int duration) {
        if (duration != 0) {
            String time = TextUtil.secToTime(duration);
            durationView.setText(time);
        } else {
            durationView.setText("");
        }
    }

    public void onAttentionItemClickListener(RelativeLayout mAttentionlayout, final RelatedItemEntity relatedItemEntity) {
        mAttentionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
//                intent.putExtra(NewsFeedFgt.KEY_NEWS_ID, "10060188");
                intent.putExtra(NewsFeedFgt.KEY_NEWS_ID, relatedItemEntity.getNid()+"");
                mContext.startActivity(intent);
                mContext.finish();
//                MobclickAgent.onEvent(mContext, "qidian_user_view_relate_point");

            }
        });
    }
}
