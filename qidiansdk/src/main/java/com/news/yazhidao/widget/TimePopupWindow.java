package com.news.yazhidao.widget;//package com.news.yazhidao.widget;
//
//import android.animation.ObjectAnimator;
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.ColorDrawable;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Interpolator;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//
//import com.news.yazhidao.R;
//import com.news.yazhidao.entity.TimeFeed;
//import com.news.yazhidao.utils.FastBlur;
//import com.news.yazhidao.utils.ImageUtils;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//
//
////m_ppopupWindow = new TSHeadPortraitPopupWindow(m_pActivity, m_pUserData.getM_strPhotoUrl());
////        m_ppopupWindow.setAnimationStyle(R.style.AnimationAlpha);
////        m_ppopupWindow.showAtLocation(m_pActivity.getWindow().getDecorView(), Gravity.CENTER
////        | Gravity.CENTER, 0, 0);
//
///**
// * Created by h.yuan on 2015/3/23.
// */
//public class TimePopupWindow extends PopupWindow implements Handler.Callback {
//
//    private TextViewExtend mtvHour, mtvMin, mtvSec;
//    private ImageView mivBg, mivStatus, mivClose;
//    private View mMenuView;
//    private Activity m_pContext;
//    private Context mContext;
//    private Handler mHandler;
//    private RoundedProgressBar mrpbTime;
//    private HorizontalListView mhlvDate;
//    private DateAdapter mDateAdapter;
//    private long mlCurrentTime, mlTotalTime;
//    private float mAnimationProgress, miCurrentProgress;
//    private TimeFeed mCurrentTimeFeed;
//    private IUpdateUI mUpdateUI;
//    private String mCurrentDate, mCurrentType, mStrSelectedDate;
//
//    public TimePopupWindow(Activity context, Bitmap bitmap, TimeFeed timeFeed, Long updateTime, Long totalTime, IUpdateUI updateUI) {
//        super(context);
//        m_pContext = context;
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mMenuView = inflater.inflate(R.layout.popup_window_time, null);
//        mHandler = new Handler(this);
//        mCurrentTimeFeed = timeFeed;
//        mlCurrentTime = updateTime;
//        mlTotalTime = totalTime;
//        mUpdateUI = updateUI;
//        mDateAdapter = new DateAdapter(m_pContext);
//        findHeadPortraitImageViews(bitmap);
//        loadData();
//    }
//
//    private void findHeadPortraitImageViews(Bitmap bitmap) {
//        mrpbTime = (RoundedProgressBar) mMenuView.findViewById(R.id.progress_circle);
//        mivBg = (ImageView) mMenuView.findViewById(R.id.bg_imageView);
////        blur(bitmap, mivBg);
//        mivBg.setBackgroundResource(R.drawable.time_bg);
//        mivClose = (ImageView) mMenuView.findViewById(R.id.close_imageView);
//        mivStatus = (ImageView) mMenuView.findViewById(R.id.iv_date_status);
//        mtvHour = (TextViewExtend) mMenuView.findViewById(R.id.tv_hour_num);
//        mtvMin = (TextViewExtend) mMenuView.findViewById(R.id.tv_min_num);
//        mtvSec = (TextViewExtend) mMenuView.findViewById(R.id.tv_sec_num);
//        mhlvDate = (HorizontalListView) mMenuView.findViewById(R.id.lv_date);
//        mDateAdapter.notifyDataSetChanged();
//        mhlvDate.setAdapter(mDateAdapter);
//        //设置SelectPicPopupWindow的View
//        this.setContentView(mMenuView);
//        //设置SelectPicPopupWindow弹出窗体的宽
//        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        //设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
//        //设置SelectPicPopupWindow弹出窗体可点击
//        this.setFocusable(true);
//        //设置SelectPicPopupWindow弹出窗体动画效果
////        this.setAnimationStyle(R.style.DialogAnimation);
//        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
//        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
////        mMenuView.setOnTouchListener(new OnTouchListener() {
////
////            public boolean onTouch(View v, MotionEvent event) {
////                if (event.getAction() == MotionEvent.ACTION_UP) {
////                    dismiss();
////                }
////                return true;
////            }
////        });
//        mivClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//    }
//
//
//    private void loadData() {
//        String type = "";
//        if (mCurrentTimeFeed != null)
//            type = mCurrentTimeFeed.getNext_update_type();
//        if (type != null && type.equals("0"))
//            mivStatus.setBackgroundResource(R.drawable.bg_date_sun);
//        else
//            mivStatus.setBackgroundResource(R.drawable.bg_date_moon);
//        if (mlTotalTime != 0) {
//            miCurrentProgress = (int) ((mlTotalTime - mlCurrentTime) * 100 / mlTotalTime);
//            mHandler.sendEmptyMessage(0);
//            mDateAdapter.setData(mCurrentTimeFeed);
//
//            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mrpbTime, "translationY", 0);
//            objectAnimator.setDuration(1000);
//            objectAnimator.setInterpolator(new AccelerateInterpolator());
//            objectAnimator.start();
//        }
//    }
//
//    public interface IUpdateUI {
//        void refreshUI(String date, String type);
//    }
//
//    private void blur(Bitmap bkg, View view) {
//        long startMs = System.currentTimeMillis();
//        float scaleFactor = 15;
//        float radius = 1;
//        int width = m_pContext.getWindowManager().getDefaultDisplay().getWidth();
//        int height = m_pContext.getWindowManager().getDefaultDisplay()
//                .getHeight();
//        Bitmap overlay = Bitmap.createBitmap((int) (width / scaleFactor),
//                (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(overlay);
//        canvas.translate(-2 / scaleFactor, 0);
//        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
//        Paint paint = new Paint();
//        paint.setFlags(Paint.ANTI_ALIAS_FLAG);//消除锯齿
//        if (canvas != null && bkg != null && paint != null) {
//            canvas.drawBitmap(bkg, 0, 0, paint);
//            canvas.drawColor(new Color().parseColor("#99FFFFFF"));
//        }
//
//        overlay = FastBlur.doBlur(overlay, (int) radius, true);
//        overlay = ImageUtils.getRoundedCornerBitmap(m_pContext, overlay, 1, false, false, false, true);
//        view.setBackgroundDrawable(new BitmapDrawable(m_pContext.getResources(), overlay));
//
//        if (overlay != null) {
//            overlay = null;
//        }
//        Log.e("xxxx", System.currentTimeMillis() - startMs + "ms");
//    }
//
//    public class AccelerateInterpolator implements Interpolator {
//        private final float mFactor;
//        private final double mDoubleFactor;
//
//        public AccelerateInterpolator() {
//            mFactor = 2.0f;
//            mDoubleFactor = 50.0f;
//        }
//
//        @Override
//        public float getInterpolation(float input) {
//            mAnimationProgress = input * miCurrentProgress;
//            if (input == 1.0f) {
//                mAnimationProgress = miCurrentProgress;
//            }
//            mHandler.sendEmptyMessage((int) mAnimationProgress);
//            if (mFactor == 1.0f) {
//                return input * input;
//            } else {
//                return (float) Math.pow(input, mDoubleFactor);
//            }
//        }
//    }
//
//    @Override
//    public boolean handleMessage(Message msg) {
//        if (mAnimationProgress < miCurrentProgress) {
//            mrpbTime.setProgress((int) mAnimationProgress);
//            int random = (int) Math.round(Math.random() * (60 - 10) + 10);
//            int random1 = (int) Math.round(Math.random() * (60 - 10) + 10);
//            int random2 = (int) Math.round(Math.random() * (60 - 10) + 10);
//            mtvHour.setText("" + random);
//            mtvMin.setText("" + random1);
//            mtvSec.setText("" + random2);
//        } else {
//            final long time = mlCurrentTime - 1000;
//            new CountDownTimer(time, 1000) {
//                public void onTick(long millisUntilFinished) {
//                    int ss = 1000;
//                    int mi = ss * 60;
//                    int hh = mi * 60;
//                    int dd = hh * 24;
//                    miCurrentProgress += (12 * 60 * 60 * 10 * 1.0f) / mlTotalTime;
//                    mrpbTime.setProgress((int) miCurrentProgress);
//                    long day = millisUntilFinished / dd;
//                    long hour = (millisUntilFinished - day * dd) / hh;
//                    long minute = (millisUntilFinished - day * dd - hour * hh) / mi;
//                    long second = (millisUntilFinished - day * dd - hour * hh - minute * mi) / ss;
//                    long milliSecond = millisUntilFinished - day * dd - hour * hh - minute * mi - second * ss;
//
//                    String strDay = day < 10 ? "0" + day : "" + day; //天
//                    String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
//                    String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
//                    String strSecond = second < 10 ? "0" + second : "" + second;//秒
//                    String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
//                    strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;
//
//                    mtvHour.setText("" + strHour);
//                    mtvMin.setText("" + strMinute);
//                    mtvSec.setText("" + strSecond);
//                }
//
//                public void onFinish() {
//                    mrpbTime.setProgress(100);
//                    dismiss();
//                }
//            }.start();
//        }
//        return false;
//    }
//
//    public void setDateAndType(String currentDate, String currentType) {
//        mCurrentDate = currentDate;
//        mCurrentType = currentType;
//        if (mCurrentTimeFeed == null) {
//            mCurrentTimeFeed = new TimeFeed();
//            if (mCurrentType != null && mCurrentType.equals("0"))
//                mCurrentTimeFeed.setNext_update_type("1");
//            else
//                mCurrentTimeFeed.setNext_update_type("0");
//            mCurrentTimeFeed.setNext_upate_time("24894000");
//            mCurrentTimeFeed.setNext_update_freq("43200000");
//        }
//        if (Integer.valueOf(mCurrentTimeFeed.getNext_update_type()) == 0 && mCurrentType.equals("1")) {
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//            try {
//                Date date = df.parse(mCurrentDate);
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(date);
//                calendar.add(Calendar.DAY_OF_MONTH, +1);
//                date = calendar.getTime();
//                mStrSelectedDate = df.format(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        } else {
//            mStrSelectedDate = mCurrentDate;
//        }
//        if (mCurrentTimeFeed.getHistory_date() != null) {
//            ArrayList<String> arrTimeList = mCurrentTimeFeed.getHistory_date();
//            Log.i("tag", "i===" + mStrSelectedDate);
//            for (int i = 0; i < arrTimeList.size(); i++) {
//                if (mStrSelectedDate != null && mStrSelectedDate.equals(arrTimeList.get(i))) {
//                    final int j = i;
//                    mhlvDate.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mhlvDate.scrollTo(width * j);
//                        }
//                    }, 100);
//                }
//            }
//        }
//    }
//
//    int width;
//
//    class DateAdapter extends BaseAdapter {
//
//        Context mContext;
//        ArrayList<String> marrStrHistoryDate;
//
//        DateAdapter(Context context) {
//            mContext = context;
//        }
//
//        public void setData(TimeFeed timeFeed) {
//            if (timeFeed != null)
//                marrStrHistoryDate = timeFeed.getHistory_date();
//        }
//
//        @Override
//        public int getCount() {
//            return marrStrHistoryDate == null ? 0 : marrStrHistoryDate.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return position;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            final Holder holder;
//            if (convertView == null) {
//                holder = new Holder();
//                if (Integer.valueOf(mCurrentTimeFeed.getNext_update_type()) == 0)
//                    convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_date1, null, false);
//                else
//                    convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_date2, null, false);
//                holder.tvDate = (TextViewExtend) convertView.findViewById(R.id.tv_date);
//                holder.ivMorning = (ImageView) convertView.findViewById(R.id.iv_date_morning);
//                holder.ivNight = (ImageView) convertView.findViewById(R.id.iv_date_night);
//                convertView.setTag(holder);
//            } else {
//                holder = (Holder) convertView.getTag();
//            }
//            convertView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            convertView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            width = convertView.getMeasuredWidth();
//            if (mStrSelectedDate != null && mStrSelectedDate.equals(marrStrHistoryDate.get(position))) {
//                if (mCurrentType.equals("0"))
//                    holder.ivMorning.setPressed(true);
//                else
//                    holder.ivNight.setPressed(true);
//            } else {
//                holder.ivMorning.setPressed(false);
//                holder.ivNight.setPressed(false);
//            }
//            final String strCurrentDate = marrStrHistoryDate.get(position);
//            holder.tvDate.setText(strCurrentDate.substring(5));
//            holder.ivMorning.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i("tag", strCurrentDate);
//                    mUpdateUI.refreshUI(strCurrentDate, "0");
//                    dismiss();
//                }
//            });
//            holder.ivNight.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mUpdateUI != null && Integer.valueOf(mCurrentTimeFeed.getNext_update_type()) == 0) {
//                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                        try {
//                            Date date = df.parse(strCurrentDate);
//                            Calendar calendar = Calendar.getInstance();
//                            calendar.setTime(date);
//                            calendar.add(Calendar.DAY_OF_MONTH, -1);
//                            date = calendar.getTime();
//                            String strDate = df.format(date);
//                            Log.i("tag", strDate);
//                            mUpdateUI.refreshUI(strDate, "1");
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Log.i("tag", strCurrentDate);
//                        mUpdateUI.refreshUI(strCurrentDate, "1");
//                    }
//                    dismiss();
//                }
//            });
//            return convertView;
//        }
//    }
//
//
//    class Holder {
//        TextViewExtend tvDate;
//        ImageView ivMorning;
//        ImageView ivNight;
//    }
//}
