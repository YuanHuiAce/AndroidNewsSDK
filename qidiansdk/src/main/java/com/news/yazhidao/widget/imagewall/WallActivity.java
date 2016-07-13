package com.news.yazhidao.widget.imagewall;//package com.news.yazhidao.widget.imagewall;
//
//import android.animation.ObjectAnimator;
//import android.content.Intent;
//import android.net.Uri;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.text.Html;
//import android.text.method.ScrollingMovementMethod;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.facebook.drawee.drawable.ScalingUtils;
//import com.facebook.drawee.view.SimpleDraweeView;
//import com.news.yazhidao.R;
//import com.news.yazhidao.common.BaseActivity;
//import com.news.yazhidao.utils.DensityUtil;
//import com.news.yazhidao.utils.Logger;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class WallActivity extends BaseActivity implements View.OnClickListener {
//
//    public static final String KEY_IMAGE_WALL_DATA = "key_image_wall_data";
//    private ViewPager mWallVPager;
//    private PagerAdapter mPagerAdapter;
//    private ArrayList<View> mViews;
//    private ImageView mWallLeftBack;
//    private TextView mWallDesc;
//    private ArrayList<HashMap<String,String>> mImageWalls;
//    private boolean isDisplay = true;
//    private View mWallHeader;
//
//
//    @Override
//    protected boolean isNeedAnimation() {
//        return true;
//    }
//
//    @Override
//    protected boolean translucentStatus() {
//        return false;
//    }
//
//    @Override
//    protected void setContentView() {
//        setContentView(R.layout.walllayout);
//    }
//
//    @Override
//    protected void initializeViews() {
//        initVars();
//        findViews();
//    }
//
//    @Override
//    protected void loadData() {
//
//    }
//
//    private void initVars() {
//        Intent intent = getIntent();
//        mImageWalls = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(KEY_IMAGE_WALL_DATA);
//        mViews = new ArrayList<View>();
//    }
//
//    // 初始化视图
//    private void findViews() {
//        // 实例化视图控件
//        mWallVPager = (ViewPager) findViewById(R.id.mWallVPager);
//        mWallDesc = (TextView) findViewById(R.id.mWallDesc);
//        mWallLeftBack = (ImageView) findViewById(R.id.mWallLeftBack);
//        mWallHeader =  findViewById(R.id.mWallHeader);
//        mWallLeftBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                WallActivity.this.finish();
//            }
//        });
//        mWallDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
//        for (int i = 0; i < mImageWalls.size(); i++) {
//            final SimpleDraweeView imageView = new SimpleDraweeView(this);
//            ViewGroup.LayoutParams  params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            imageView.setLayoutParams(params);
//            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
//            mViews.add(imageView);
//            imageView.setImageURI(Uri.parse(mImageWalls.get(i).get("img")));
//        }
//        final int margin = DensityUtil.dip2px(this,12);
//        mWallVPager.setPadding(0, 0, 0, 0);
//        mWallVPager.setClipToPadding(false);
//        mWallVPager.setPageMargin(margin);
//        mWallVPager.setAdapter(new WallPagerAdapter(mViews));
//        mWallVPager.setOffscreenPageLimit(3);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mWallDesc.getLayoutParams();
//        params.height = (int) (mWallDesc.getLineHeight() * 4.5);
//        mWallDesc.setMaxLines(4);
//        mWallDesc.setLayoutParams(params);
//        mWallDesc.setText(Html.fromHtml(1 + "<small>" + "/" + mImageWalls.size() + "</small>" + "&nbsp;&nbsp;&nbsp;"+mImageWalls.get(0).get("note")));
//        mWallVPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
//            @Override
//            public void onPageSelected(int position) {
//                mWallDesc.setText(Html.fromHtml(position + 1 + "<small>" + "/" + mImageWalls.size() + "</small>" + "&nbsp;&nbsp;&nbsp;"+mImageWalls.get(position).get("note")));
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mWallDesc.getLayoutParams();
//                params.height = (int) (mWallDesc.getLineHeight() * 4.5);
//                mWallDesc.setMaxLines(4);
//                mWallDesc.setLayoutParams(params);
//                Logger.e("jigang","change =" + mWallDesc.getHeight());
//            }
//        });
//        mWallDesc.setOnTouchListener(new View.OnTouchListener() {
//            float startY;
//            int defaultH;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (defaultH == 0){
//                    defaultH = mWallDesc.getHeight();
//                }
//                Logger.e("jigang","default =" + defaultH);
//                int lineCount = mWallDesc.getLineCount();
//                int maxHeight = mWallDesc.getLineHeight() * lineCount;
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        Logger.e("jigang","---down");
//                        startY = event.getRawY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        float deltaY = event.getRawY() - startY;
//                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mWallDesc.getLayoutParams();
//                        int height = mWallDesc.getHeight();
//                        Logger.e("jigang","height="+height + ",maxHeight="+maxHeight);
//                        if (Math.abs(deltaY) > 1 && lineCount > 4){
//                            height -= deltaY;
//                            if (deltaY > 0){
//                                if (height < defaultH){
//                                    height = defaultH;
//                                }
//                            }else {
//                                if (height > maxHeight){
//                                    height = maxHeight + DensityUtil.dip2px(WallActivity.this,6 * 2 + 4);
//                                }
//                            }
//                            params.height = height;
//                            mWallDesc.setMaxLines(Integer.MAX_VALUE);
//                            mWallDesc.setLayoutParams(params);
//                        }
//                        Logger.e("jigang",event.getRawY() + "---move " + deltaY);
//                        startY = event.getRawY();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        Logger.e("jigang","---up");
//                        break;
//                }
//                return true;
//            }
//        });
//        final GestureDetector tapGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                if (isDisplay){
//                    isDisplay = false;
//                    ObjectAnimator.ofFloat(mWallHeader,"alpha",1.0f,0).setDuration(200).start();
//                    ObjectAnimator.ofFloat(mWallDesc,"alpha",1.0f,0).setDuration(200).start();
//                }else {
//                    isDisplay = true;
//                    ObjectAnimator.ofFloat(mWallHeader,"alpha",0,1.0f).setDuration(200).start();
//                    ObjectAnimator.ofFloat(mWallDesc,"alpha",0,1.0f).setDuration(200).start();
//                }
//                return super.onSingleTapConfirmed(e);
//            }
//        });
//
//        mWallVPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                tapGestureDetector.onTouchEvent(event);
//                return false;
//            }
//        });
//    }
//
//    //按钮的点击事件
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.mWallLeftBack:
//                onBackPressed();
//                break;
//        }
//    }
//
//
//    public class WallPagerAdapter extends PagerAdapter {
//        private List<View> views = new ArrayList<View>();
//
//        public WallPagerAdapter(List<View> views) {
//            this.views = views;
//        }
//
//        @Override
//        public boolean isViewFromObject(View arg0, Object arg1) {
//            return arg0 == arg1;
//        }
//
//        @Override
//        public int getCount() {
//            return views.size();
//        }
//
//        @Override
//        public void destroyItem(View container, int position, Object object) {
//            ((ViewPager) container).removeView(views.get(position));
//        }
//
//        @Override
//        public Object instantiateItem(View container, int position) {
//            ((ViewPager) container).addView(views.get(position));
//            return views.get(position);
//        }
//
//    }
//}
