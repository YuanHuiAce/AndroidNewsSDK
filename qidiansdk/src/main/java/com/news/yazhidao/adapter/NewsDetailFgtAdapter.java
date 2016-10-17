package com.news.yazhidao.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;

import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.entity.RelatedItemEntity;
import com.news.yazhidao.pages.NewsDetailWebviewAty;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.widget.TextViewExtend;

//import com.news.yazhidao.pages.NewsDetailWebviewAty;

/**
 * Created by Administrator on 2016/4/22.
 */
public class NewsDetailFgtAdapter extends CommonAdapter<RelatedItemEntity> {

    private Activity mContext;
    public static final int REQUEST_CODE = 1030;

    public NewsDetailFgtAdapter(Activity context) {
        super(R.layout.item_news_detail_relate_attention, context, null);
        this.mContext = context;
    }


    @Override
    public void convert(CommonViewHolder holder, RelatedItemEntity relatedItemEntity, int position) {
        if (relatedItemEntity.getUrl().equals("-1")) {//没有数据时也可以让listView滑动
            Logger.e("aaa", "没有数据时的状况！！！！！！！！！！！！！");
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
            holder.getView(R.id.attentionlayout).setLayoutParams(layoutParams);
            return;
        }
        onAttentionItemClickListener((RelativeLayout) holder.getView(R.id.attentionlayout), relatedItemEntity);
        String form = relatedItemEntity.getFrom();
        TextViewExtend title = (TextViewExtend) holder.getView(R.id.attention_Title);
        title.setText(Html.fromHtml(relatedItemEntity.getTitle()));


        holder.setTextViewExtendText(R.id.attention_Source, relatedItemEntity.getPname());

        if (getCount() == position + 1) {//去掉最后一条的线
            holder.getView(R.id.attention_bottomLine).setVisibility(View.GONE);
        }else {
            holder.getView(R.id.attention_bottomLine).setVisibility(View.VISIBLE);
        }
    }

    public void onAttentionItemClickListener(RelativeLayout mAttentionlayout, final RelatedItemEntity relatedItemEntity) {
        mAttentionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.i("aaa ", "onClick: onAttentionItemClickListener");
                Intent webviewIntent = new Intent(mContext, NewsDetailWebviewAty.class);
                String zhihuUrl = relatedItemEntity.getUrl();
                webviewIntent.putExtra(NewsDetailWebviewAty.KEY_URL, zhihuUrl);
                mContext.startActivity(webviewIntent);
////               MobclickAgent.onEvent(mContext,"yazhidao_user_view_relate_point");

            }
        });
    }


}
