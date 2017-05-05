package com.news.sdk.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.news.sdk.R;
import com.news.sdk.adapter.abslistview.CommonAdapter;
import com.news.sdk.adapter.abslistview.CommonViewHolder;
import com.news.sdk.entity.HistoryEntity;
import com.news.sdk.widget.TextViewExtend;

/**
 * Created by Administrator on 2016/5/11.
 */
public class SearchListViewOpenAdapter extends CommonAdapter<HistoryEntity> {
    private onSearchListViewOpenItemClick mOnSearchListViewOpenItemClick;
    private onFocusItemClick mOnFocusItemClick;

    public SearchListViewOpenAdapter(Context mContext) {
        super(R.layout.search_listviewopen_item, mContext, null);
    }

    @Override
    public void convert(CommonViewHolder holder, HistoryEntity historyEntity, int position) {
        if (historyEntity.getPosition() == -1) {
            holder.getView(R.id.search_listviewopen_itemLayout).setVisibility(View.GONE);
        } else {
            if (holder.getView(R.id.search_listviewopen_itemLayout).getVisibility() == View.GONE) {
                holder.getView(R.id.search_listviewopen_itemLayout).setVisibility(View.VISIBLE);
            }
        }
        holder.setTextViewExtendText(R.id.search_listviewopen_item_content, historyEntity.getContent());
        setItemClick((RelativeLayout) holder.getView(R.id.search_listviewopen_itemLayout), historyEntity.getContent());
        setFocusClick((TextViewExtend) holder.getView(R.id.focus_item_content), historyEntity);
    }

    public void setItemClick(RelativeLayout layout, final String content) {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnSearchListViewOpenItemClick.listener(content);
            }
        });
    }

    public void setFocusClick(TextViewExtend layout, final HistoryEntity historyEntity) {
        boolean isFocus = historyEntity.isFocus();
        if (isFocus) {
            layout.setSelected(true);
        } else {
            layout.setSelected(false);
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnFocusItemClick.listener(historyEntity);
            }
        });
    }


    public void setOnSearchListViewOpenItemClick(onSearchListViewOpenItemClick mOnSearchListViewOpenItemClick) {
        this.mOnSearchListViewOpenItemClick = mOnSearchListViewOpenItemClick;
    }

    public void setOnFocusItemClick(onFocusItemClick onFocusItemClick) {
        mOnFocusItemClick = onFocusItemClick;
    }

    public interface onSearchListViewOpenItemClick {
        public void listener(String content);
    }

    public interface onFocusItemClick {
        public void listener(HistoryEntity historyEntity);
    }
}
