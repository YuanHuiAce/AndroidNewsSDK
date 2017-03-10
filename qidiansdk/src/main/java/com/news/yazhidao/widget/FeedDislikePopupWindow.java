package com.news.yazhidao.widget;

import android.content.Context;
import android.nfc.Tag;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.widget.tag.TagBaseAdapter;
import com.news.yazhidao.widget.tag.TagCloudLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengjigang on 16/4/5.
 */
public class FeedDislikePopupWindow extends RelativeLayout {
    int mLayoutHeight = 500;
    int mLayoutWidth;
    int mScreeHeight;
    int mScreeWidth;
    int mMarginLorR = 24;
    int mClickX = -1;
    int mClickY = -1;

    int mTriangleWidth = 61;
    int mTriangleHeight = 27;
    int mInPopMargin = 24;
    int mTitleHeight;
    int mNewsId;

    //图片的方向 true向上，false向下
    boolean isDirection = true;


    LinearLayout mPopWindowLayout;
    LinearLayout mAllLayout;
    ImageView mTriangle;
    TextView mTitle, mLine;

    private TagCloudLayout mContainer;
    private TagBaseAdapter mAdapter;
    private List<String> mList;

    private final List<Tag> mTags = new ArrayList<Tag>();
    private final String[] titles = {"不喜欢", "重复、旧闻", "内容质量差", "来源：地球在线"};

    private Context mContext;

    public FeedDislikePopupWindow(Context context) {
        super(context);
        initView(context);
    }

    public FeedDislikePopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FeedDislikePopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context) {
        mContext = context;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setLayoutParams(params);

        mPopWindowLayout = new LinearLayout(context);
        mPopWindowLayout.setOrientation(LinearLayout.VERTICAL);
        mPopWindowLayout.setPadding(DensityUtil.dip2px(mContext,10), DensityUtil.dip2px(mContext,15), DensityUtil.dip2px(mContext,10), DensityUtil.dip2px(mContext,15));

        mPopWindowLayout.setBackgroundResource(R.drawable.popwindow_linear_bg);
        mPopWindowLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAllLayout = new LinearLayout(context);
        mAllLayout.setBackgroundColor(0x50000000);
        mAllLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(View.GONE);
            }
        });


        mTriangle = new ImageView(context);
        mTriangle.setImageResource(R.drawable.qd_triangle_downward);

        mTitle = new TextView(context);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f);
        mTitle.setIncludeFontPadding(false);
        mTitle.setText("请选择不感兴趣原因:");
        mTitle.setTextColor(getResources().getColor(R.color.new_color1));
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mTitle.measure(w, h);
        mTitleHeight = mTitle.getMeasuredHeight();


        mLine = new TextView(context);
        LinearLayout.LayoutParams params0 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(mContext,0.55f));
        params1.setMargins(0, DensityUtil.dip2px(mContext,15), 0, 0);
        mLine.setBackgroundColor(getResources().getColor(R.color.new_color5));

        mContainer = new TagCloudLayout(context);
        mList = new ArrayList<>();
        mList.add("不喜欢");
        mList.add("重复、旧闻");
        mList.add("内容质量差");
        mList.add("");
        mAdapter = new TagBaseAdapter(context, mList);
        mContainer.setAdapter(mAdapter);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(DeviceInfoUtil.getScreenWidth(context) - mInPopMargin * 2 - mMarginLorR * 2
                , mLayoutHeight);//这里给他一个固定的大小
        params2.setMargins(0, DensityUtil.dip2px(mContext,15), 0, 0);

        mPopWindowLayout.addView(mTitle, params0);
        mPopWindowLayout.addView(mLine, params1);
        mPopWindowLayout.addView(mContainer, params2);
        addView(mAllLayout);
        addView(mPopWindowLayout);
        addView(mTriangle);


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout();
//        super.onLayout(changed, l, t, r, b);
        if(isVisity){//每次打开应用会闪。防止闪
            return;
        }

        if(getVisibility() == VISIBLE){
            isVisity = true;
        }

        mLayoutWidth = r - mMarginLorR * 2;
        mScreeHeight = b;
        mScreeWidth = r;
        mLayoutHeight = mContainer.getLayoutHeight() + DensityUtil.dip2px(mContext,15) * 4 + mTitleHeight + 10;


        mAllLayout.layout(l, t, mScreeWidth, mScreeHeight);
        if (mClickY == -1 && mClickX == -1) {
            mPopWindowLayout.layout(mMarginLorR, mMarginLorR, mScreeWidth - mMarginLorR, mLayoutHeight);
            mTriangle.layout(mMarginLorR, mMarginLorR, mMarginLorR + mTriangleWidth, mMarginLorR + mTriangleHeight);
        } else {
            if (mScreeHeight - mClickY >= mLayoutHeight) {
                isDirection = true;
                mTriangle.setImageResource(R.drawable.qd_triangle_downward);
                mPopWindowLayout.layout(mMarginLorR, mClickY + mTriangleHeight, mScreeWidth - mMarginLorR, mClickY + mLayoutHeight + mTriangleHeight);
                mTriangle.layout(mClickX - mTriangleWidth, mClickY, mClickX, mClickY + mTriangleHeight);

            } else {
                isDirection = false;
                mTriangle.setImageResource(R.drawable.qd_triangle_upward);
                mPopWindowLayout.layout(mMarginLorR, mClickY - mLayoutHeight - mTriangleHeight, mScreeWidth - mMarginLorR, mClickY - mTriangleHeight);
                mTriangle.layout(mClickX - mTriangleWidth, mClickY - mTriangleHeight, mClickX, mClickY);

            }
        }
        float aminX = (float) (mClickX - mMarginLorR) / mLayoutWidth;
        int scale = 2;//设置位数
        int roundingMode = 4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
        BigDecimal bd = new BigDecimal((double) aminX);
        bd = bd.setScale(scale, roundingMode);
        aminX = bd.floatValue();

        ScaleAnimation scaleAnim = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, aminX,
                Animation.RELATIVE_TO_SELF, isDirection ? 0f : 1f);
        scaleAnim.setDuration(200);
        ScaleAnimation scaleAnim2 = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, isDirection ? 1f : 0f);
        scaleAnim2.setDuration(200);


        mPopWindowLayout.startAnimation(scaleAnim);
        mTriangle.startAnimation(scaleAnim2);
        Logger.e("aaa", "==============");
    }

    boolean isVisity;

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if(visibility ==GONE){
            isVisity = false;
        }
    }

    public void showView(int clickX, int clickY) {
        setVisibility(View.VISIBLE);
        mClickX = clickX;
        mClickY = clickY;
        invalidate();
    }
    public void setSourceList(String tagName){
        mList.set(3, tagName);
        mAdapter.notifyDataSetChanged();
    }
    public void setItemClickListerer(TagCloudLayout.TagItemClickListener listerer){
        mContainer.setItemClickListener(listerer);
    }


    public int getNewsId() {
        return mNewsId;
    }

    public void setNewsId(int mNewsId) {
        this.mNewsId = mNewsId;
    }
}
