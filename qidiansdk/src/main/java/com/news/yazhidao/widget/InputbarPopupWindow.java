package com.news.yazhidao.widget;//package com.news.yazhidao.widget;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//
//import com.news.yazhidao.R;
//import com.news.yazhidao.entity.NewsDetailAdd;
//import com.news.yazhidao.entity.User;
//import com.news.yazhidao.listener.DisplayImageListener;
//import com.news.yazhidao.listener.UploadCommentListener;
//import com.news.yazhidao.net.request.UploadCommentRequest;
//import com.news.yazhidao.utils.Logger;
//import com.news.yazhidao.utils.ToastUtil;
//import com.news.yazhidao.utils.image.ImageManager;
//import com.news.yazhidao.utils.manager.SharedPreManager;
//import com.news.yazhidao.widget.InputBar.InputBar;
//import com.news.yazhidao.widget.InputBar.InputBarDelegate;
//import com.news.yazhidao.widget.InputBar.InputBarType;
//
//import java.util.ArrayList;
//
//
///**
// * Created by h.yuan on 2015/3/23.
// */
//public class InputbarPopupWindow extends PopupWindow implements InputBarDelegate, Handler.Callback {
//
//    private int PARA_FLAG = 0;
//    private int ARTICLE_FLAG = 1;
//
//    private ImageView mivClose;
//    private View mMenuView;
//    private Activity m_pContext;
//    //    private Context m_pContext;
//    private ListView mlvComment;
//    private DateAdapter mCommentAdapter;
//    private InputBar mInputBar;
//    private RelativeLayout mrlRecord;
//    private Handler mHandler;
//
//    private double mRecordVolume;// 麦克风获取的音量值
//    private TextViewExtend mtvVoiceTips, mtvVoiceTimes;
//    private ImageView mivRecord;
//    private String mSourceUrl;
//    private int comment_flag;
//    private ArrayList<NewsDetailAdd.Point> marrPoints;
//    private IUpdateCommentCount mIUpdateCommentCount;
//    private int miCount, mParagraphIndex = 0;
//    private NewsDetailAdd.Point newPoint;
//    private RelativeLayout rl_inputbar_content;
//    private boolean praiseFlag;
//
//    public InputbarPopupWindow(Activity context, ArrayList<NewsDetailAdd.Point> points, String sourceUrl, IUpdateCommentCount updateCommentCount, int flag) {
//        super(context);
//        m_pContext = context;
//        marrPoints = points;
//        mSourceUrl = sourceUrl;
//        comment_flag = flag;
//        if (marrPoints != null && marrPoints.size() > 0) {
//            if(marrPoints.get(0) != null && marrPoints.get(0).paragraphIndex != null) {
//                mParagraphIndex = Integer.valueOf(marrPoints.get(0).paragraphIndex);
//            }
//        } else{
//            mParagraphIndex = 0;
//        }
//        mIUpdateCommentCount = updateCommentCount;
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mMenuView = inflater.inflate(R.layout.ll_comment_inputbar, null);
//        mCommentAdapter = new DateAdapter(m_pContext);
//        mHandler = new Handler(this);
//        findHeadPortraitImageViews();
//        loadData();
//    }
//
//    public InputbarPopupWindow(Activity context, ArrayList<NewsDetailAdd.Point> points, String sourceUrl, IUpdateCommentCount updateCommentCount, int flag, int paraindex) {
//        super(context);
//        m_pContext = context;
//        marrPoints = points;
//        mSourceUrl = sourceUrl;
//        comment_flag = flag;
//        if (marrPoints != null && marrPoints.size() > 0) {
//            mParagraphIndex = Integer.valueOf(marrPoints.get(0).paragraphIndex);
//        } else{
//            mParagraphIndex = 0;
//        }
//
//        this.mParagraphIndex = paraindex;
//        mIUpdateCommentCount = updateCommentCount;
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mMenuView = inflater.inflate(R.layout.ll_comment_inputbar, null);
//        mCommentAdapter = new DateAdapter(m_pContext);
//        mHandler = new Handler(this);
//        findHeadPortraitImageViews();
//        loadData();
//    }
//
//    private void findHeadPortraitImageViews() {
//        //录音动画
//        mrlRecord = (RelativeLayout) mMenuView.findViewById(R.id.voice_record_layout_wins);
//        mtvVoiceTips = (TextViewExtend) mMenuView.findViewById(R.id.tv_voice_tips);
//        mtvVoiceTimes = (TextViewExtend) mMenuView.findViewById(R.id.voice_record_time);
//        mivRecord = (ImageView) mMenuView.findViewById(R.id.iv_record);
//        //输入框
//        mInputBar = (InputBar) mMenuView.findViewById(R.id.input_bar_view);
//        rl_inputbar_content = (RelativeLayout) mMenuView.findViewById(R.id.rl_inputbar_content);
//        rl_inputbar_content.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        mInputBar.setActivityAndHandler(m_pContext, mHandler);
//        mInputBar.setDelegate(this);
//        //设置SelectPicPopupWindow的View
//        this.setContentView(mMenuView);
//        //设置SelectPicPopupWindow弹出窗体的宽
//        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        //设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        //设置SelectPicPopupWindow弹出窗体可点击
//        this.setFocusable(true);
//        //设置SelectPicPopupWindow弹出窗体动画效果
////        this.setAnimationStyle(R.style.DialogAnimation);
//        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0x11000000);
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
//    }
//
//
//    @Override
//    public void dismiss() {
//        mIUpdateCommentCount.updateCommentCount(miCount, mParagraphIndex, newPoint,comment_flag,praiseFlag);
//        super.dismiss();
//    }
//
//    private void loadData() {
//
//    }
//
//    @Override
//    public void submitThisMessage(InputBarType argType, String argContent, int speechDuration) {
//        mrlRecord.setVisibility(View.INVISIBLE);
//        newPoint = new NewsDetailAdd.Point();
//        String type;
//        if (argType == InputBarType.eRecord) {
//            type = UploadCommentRequest.SPEECH_DOC;
//            newPoint.srcText = argContent;
//        } else {
//            type = UploadCommentRequest.TEXT_DOC;
//            newPoint.srcText = argContent;
//        }
//        User user = SharedPreManager.getUser(m_pContext);
//        newPoint.userIcon = user.getUserIcon();
//        newPoint.userName = user.getUserName();
//        newPoint.type = type;
//        marrPoints.add(newPoint);
//        mCommentAdapter.setData(marrPoints);
//        mCommentAdapter.notifyDataSetChanged();
//        Logger.i("jigang", type + "----url==" + argContent);
//        newPoint.srcTextTime = speechDuration / 1000;
//
//        dismiss();
//        UploadCommentRequest.uploadComment(m_pContext, mSourceUrl, argContent, "0", type, speechDuration, new UploadCommentListener() {
//
//            @Override
//            public void success(NewsDetailAdd.Point result) {
//
//            }
//
//            @Override
//            public void failed() {
//
//            }
//        });
//        Log.i("tag", argContent);
//    }
//
//    @Override
//    public void recordDidBegin(InputBar argView) {
//        mtvVoiceTips.setText("手指上滑,取消发送");
//        mtvVoiceTips.setTextColor(Color.WHITE);
//        mrlRecord.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void recordDidCancel(InputBar argView) {
//        mrlRecord.setVisibility(View.INVISIBLE);
//    }
//
//    @Override
//    public void cancelVoiceTipsType1() {
//        mtvVoiceTips.setText("松开手指，取消发送");
//        mtvVoiceTips.setTextColor(Color.RED);
//    }
//
//    @Override
//    public void cancelVoiceTipsType2() {
//        mtvVoiceTips.setText("手指上滑,取消发送");
//        mtvVoiceTips.setTextColor(Color.WHITE);
//    }
//
//    @Override
//    public boolean handleMessage(Message msg) {
//        switch (msg.what) {
//            case InputBar.RECORD_NO:// 不在录音
//                if (mInputBar.getRecordState() == InputBar.RECORD_ING) {
//                    // 停止录音
//                    mInputBar.finishRecord();
//                    // 初始化录音音量
//                    mRecordVolume = 0;
//                    // 话筒隐藏
//                    mrlRecord.setVisibility(View.INVISIBLE);
//                    // 录音达到最大时长，结束录音并发送语音
//                    ToastUtil.toastShort("录音时间不能超过60秒");
//                }
//
//                break;
//            case InputBar.RECORD_ING:// 正在录音
//                // 显示录音时间
//                mtvVoiceTimes.setText((int) mInputBar.getCurDuration() + "/" + InputBar.MAX_TIME + "″");
//                // 音量大小的动画
//                mRecordVolume = mInputBar.getRecordVolume();
//                if (mInputBar.isLong()) {
//                    mivRecord.setBackgroundResource(R.drawable.voice_cancle);
//                } else if (mRecordVolume < 500.0) {
//                    mivRecord.setBackgroundResource(R.drawable.voice_1);
//                } else if (mRecordVolume >= 500.0 && mRecordVolume < 2000) {
//                    mivRecord.setBackgroundResource(R.drawable.voice_2);
//                } else if (mRecordVolume >= 2000.0 && mRecordVolume < 8000) {
//                    mivRecord.setBackgroundResource(R.drawable.voice_3);
//                } else if (mRecordVolume >= 8000.0) {
//                    mivRecord.setBackgroundResource(R.drawable.voice_4);
//                }
//                break;
//        }
//        return false;
//    }
//
//    class DateAdapter extends BaseAdapter {
//
//        Context mContext;
//        ArrayList<NewsDetailAdd.Point> marrPoint;
//
//        DateAdapter(Context context) {
//            mContext = context;
//        }
//
//        public void setData(ArrayList<NewsDetailAdd.Point> arrPoint) {
//            if (arrPoint != null)
//                marrPoint = arrPoint;
//        }
//
//        @Override
//        public int getCount() {
//            return marrPoint == null ? 0 : marrPoint.size();
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
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_comment1, null, false);
//                holder.tvContent = (TextViewExtend) convertView.findViewById(R.id.tv_comment_content);
////                holder.mSpeechView = (SpeechView) convertView.findViewById(R.id.mSpeechView);
//                holder.ivHeadIcon = (RoundedImageView) convertView.findViewById(R.id.iv_user_icon);
//                holder.ivHeadIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                holder.tvName = (TextViewExtend) convertView.findViewById(R.id.tv_user_name);
//                holder.ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
//                holder.tvPraiseCount = (TextViewExtend) convertView.findViewById(R.id.tv_praise_count);
//                convertView.setTag(holder);
//            } else {
//                holder = (Holder) convertView.getTag();
//            }
//            NewsDetailAdd.Point point = marrPoint.get(position);
//            if (point.userIcon != null && !point.userIcon.equals(""))
//                ImageManager.getInstance(mContext).DisplayImage(point.userIcon, holder.ivHeadIcon, false,new DisplayImageListener() {
//                    @Override
//                    public void success(int width,int height) {
//
//                    }
//
//                    @Override
//                    public void failed() {
//
//                    }
//                });
//            else
//                holder.ivHeadIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_para));
//            holder.tvName.setText(point.userName);
//            holder.tvPraiseCount.setText(point.up);
//            holder.ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
//            if (point.type.equals("text_paragraph")) {
//                holder.tvContent.setText(point.srcText);
//                holder.tvContent.setVisibility(View.VISIBLE);
//                holder.mSpeechView.setVisibility(View.GONE);
//            } else {
//                Logger.i("jigang", point.srcTextTime + "--" + point.srcText);
//                holder.mSpeechView.setUrl(point.srcText, false);
//                holder.mSpeechView.setDuration(point.srcTextTime);
//                holder.mSpeechView.setVisibility(View.VISIBLE);
//                holder.tvContent.setVisibility(View.GONE);
//            }
//            holder.ivPraise.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//            return convertView;
//        }
//    }
//
//
//    class Holder {
//        RoundedImageView ivHeadIcon;
//        TextViewExtend tvName;
//        TextViewExtend tvContent;
//        TextViewExtend tvPraiseCount;
//        ImageView ivPraise;
//        SpeechView mSpeechView;
//    }
//
//    public interface IUpdateCommentCount {
//        void updateCommentCount(int count, int paragraphIndex, NewsDetailAdd.Point newPoint, int flag, boolean isPraiseFlag);
//    }
//}
