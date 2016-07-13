package com.news.yazhidao.widget;//package com.news.yazhidao.widget;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.net.Uri;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.FragmentActivity;
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
//import com.facebook.drawee.view.SimpleDraweeView;
//import com.news.yazhidao.R;
//import com.news.yazhidao.entity.NewsDetailAdd;
//import com.news.yazhidao.entity.User;
//import com.news.yazhidao.listener.PraiseListener;
//import com.news.yazhidao.listener.UploadCommentListener;
//import com.news.yazhidao.listener.UserLoginListener;
//import com.news.yazhidao.net.request.PraiseRequest;
//import com.news.yazhidao.net.request.UploadCommentRequest;
//import com.news.yazhidao.pages.LoginModeFgt;
//import com.news.yazhidao.pages.NewsDetailAty2;
//import com.news.yazhidao.utils.DeviceInfoUtil;
//import com.news.yazhidao.utils.Logger;
//import com.news.yazhidao.utils.TextUtil;
//import com.news.yazhidao.utils.ToastUtil;
//import com.news.yazhidao.utils.manager.MediaPlayerManager;
//import com.news.yazhidao.utils.manager.SharedPreManager;
//import com.news.yazhidao.widget.InputBar.InputBar;
//import com.news.yazhidao.widget.InputBar.InputBarDelegate;
//import com.news.yazhidao.widget.InputBar.InputBarType;
//
//import java.util.ArrayList;
//
//import cn.sharesdk.framework.PlatformDb;
//
//;
//
//
///**
// * Created by h.yuan on 2015/3/23.
// */
//public class CommentPopupWindow extends PopupWindow implements InputBarDelegate, Handler.Callback {
//
//    private int mParaindex;
//    private ImageView mivClose;
//    private View mMenuView;
//    private Context m_pContext;
//    private ListView mlvComment;
//    private DateAdapter mCommentAdapter;
//    private InputBar mInputBar;
//    private RelativeLayout mrlRecord;
//    private Handler mHandler;
//    private double mRecordVolume;// 麦克风获取的音量值
//    private TextViewExtend mtvTitle, mtvVoiceTips, mtvVoiceTimes;
//    private ImageView mivRecord;
//    private ArrayList<NewsDetailAdd.Point> marrPoints;
//    private IUpdateCommentCount mIUpdateCommentCount;
//    private IUpdatePraiseCount mIUpdatePraiseCount;
//    private int mParagraphIndex;
//    private String sourceUrl;
//    private ArrayList<NewsDetailAdd.Point> marrPoint;
//    private NewsDetailAdd.Point point;
//    private RelativeLayout rl_popup;
//    private boolean praiseFlag = false;
//    private int praiseCount;
//    private SharePopupWindow.ShareDismiss mShareDismiss;
//    private View mAddComment;
//
//    /**
//     * 评论界面
//     *
//     * @param context
//     * @param points
//     * @param sourceUrl
//     * @param updateCommentCount
//     * @param paraindex
//     * @param updatePraiseCount
//     */
//    public CommentPopupWindow(Context context, ArrayList<NewsDetailAdd.Point> points, String sourceUrl, IUpdateCommentCount updateCommentCount, int paraindex, IUpdatePraiseCount updatePraiseCount, SharePopupWindow.ShareDismiss shareDismiss) {
//        super(context);
//        m_pContext = context;
//        mShareDismiss = shareDismiss;
//        if (points == null) {
//            marrPoints = new ArrayList<>();
//        } else {
//            marrPoints = new ArrayList<>(points);
//        }
//        this.sourceUrl = sourceUrl;
//        mParaindex = paraindex;
//        if (paraindex == -1) {
//            if (!TextUtil.isListEmpty(marrPoints)) {
//                mParagraphIndex = Integer.valueOf(marrPoints.get(0).paragraphIndex);
//            } else {
//                mParagraphIndex = 0;
//            }
//        } else {
//            mParagraphIndex = paraindex;
//        }
//        mIUpdateCommentCount = updateCommentCount;
//        mIUpdatePraiseCount = updatePraiseCount;
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mMenuView = inflater.inflate(R.layout.popup_window_comment, null);
//        mCommentAdapter = new DateAdapter(m_pContext);
//        mHandler = new Handler(this);
//        findHeadPortraitImageViews();
//        loadData();
//    }
//
//    private void findHeadPortraitImageViews() {
//        mAddComment = mMenuView.findViewById(R.id.mAddComment);
//        mAddComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                User user = SharedPreManager.getUser(m_pContext);
//                if (user == null) {
//                    LoginModeFgt loginModeFgt = new LoginModeFgt(m_pContext, new UserLoginListener() {
//                        @Override
//                        public void userLogin(String platform, PlatformDb platformDb) {
////                            UserCommentDialog commentDialog = new UserCommentDialog(((NewsDetailAty2) m_pContext), new UserCommentDialog.IRefreshCommentPage() {
////                                @Override
////                                public void refreshComment(NewsDetailAdd.Point point) {
////                                    marrPoints.add(0,point);
////                                    mCommentAdapter.setData(marrPoints);
////                                }
//                            });
//                            commentDialog.show(((NewsDetailAty2)m_pContext).getSupportFragmentManager(), "UserCommentDialog");
//                        }
//
//                        @Override
//                        public void userLogout() {
//
//                        }
//                    }, null);
//                    loginModeFgt.show(((FragmentActivity) m_pContext).getSupportFragmentManager(), "loginModeFgt");
//                } else {
//                    UserCommentDialog commentDialog = new UserCommentDialog(((NewsDetailAty2) m_pContext), new UserCommentDialog.IRefreshCommentPage() {
//                        @Override
//                        public void refreshComment(NewsDetailAdd.Point point) {
//                            marrPoints.add(0,point);
//                            mCommentAdapter.setData(marrPoints);
//                        }
//                    });
//                    commentDialog.show(((NewsDetailAty2)m_pContext).getSupportFragmentManager(), "UserCommentDialog");
//                }
//            }
//        });
//        mtvTitle = (TextViewExtend) mMenuView.findViewById(R.id.title_textView);
//        //录音动画
//        mrlRecord = (RelativeLayout) mMenuView.findViewById(R.id.voice_record_layout_wins);
//        mtvVoiceTips = (TextViewExtend) mMenuView.findViewById(R.id.tv_voice_tips);
//        mtvVoiceTimes = (TextViewExtend) mMenuView.findViewById(R.id.voice_record_time);
//        mivRecord = (ImageView) mMenuView.findViewById(R.id.iv_record);
//        //输入框
//        mInputBar = (InputBar) mMenuView.findViewById(R.id.input_bar_view);
//        mInputBar.setActivityAndHandler(m_pContext, mHandler);
//        mInputBar.setDelegate(this);
//        mivClose = (ImageView) mMenuView.findViewById(R.id.close_imageView);
//        mlvComment = (ListView) mMenuView.findViewById(R.id.comment_list_view);
//        mlvComment.setAdapter(mCommentAdapter);
//        mCommentAdapter.setData(marrPoints);
//        mCommentAdapter.notifyDataSetChanged();
//        //设置SelectPicPopupWindow的View
//        this.setContentView(mMenuView);
//        //设置SelectPicPopupWindow弹出窗体的宽
//        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        //设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
//        //设置SelectPicPopupWindow弹出窗体可点击
//        this.setFocusable(true);
//        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.popupWindowAnimation);
//        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(Color.parseColor("#50b5eb"));
//        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
//        mivClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//    }
//
//    @Override
//    public void dismiss() {
//        super.dismiss();
//        mShareDismiss.shareDismiss();
//        //退出评论页面时，关闭正在播放的语音评论
//        MediaPlayerManager.getInstance().stop();
//    }
//
//    private void loadData() {
//
//    }
//
//    @Override
//    public void submitThisMessage(InputBarType argType, String argContent, int speechDuration) {
//        mrlRecord.setVisibility(View.INVISIBLE);
//        if (marrPoints != null && marrPoints.size() > 0) {
//            NewsDetailAdd.Point point = marrPoints.get(0);
//        }
//        final NewsDetailAdd.Point newPoint = new NewsDetailAdd.Point();
//        String type;
//        if (argType == InputBarType.eRecord) {
//            type = UploadCommentRequest.SPEECH_PARAGRAPH;
//            if (mParaindex == -1) {
//                type = UploadCommentRequest.SPEECH_DOC;
//            }
//            newPoint.srcText = argContent;
//            newPoint.srcTextTime = speechDuration / 1000;
//        } else {
//            type = UploadCommentRequest.TEXT_PARAGRAPH;
//            if (mParaindex == -1) {
//                type = UploadCommentRequest.TEXT_DOC;
//            }
//            newPoint.srcText = argContent;
//        }
//        User user = SharedPreManager.getUser(m_pContext);
//        newPoint.userIcon = user.getUserIcon();
//        newPoint.userName = user.getUserName();
//        newPoint.type = type;
//        newPoint.sourceUrl = sourceUrl;
//        marrPoints.add(newPoint);
//        point = newPoint;
//        Logger.i("jigang", type + "----url==" + argContent + "-------duration===" + speechDuration);
//        UploadCommentRequest.uploadComment(m_pContext, sourceUrl, argContent, mParagraphIndex + "", type, speechDuration, new UploadCommentListener() {
//
//            @Override
//            public void success(NewsDetailAdd.Point result) {
//                if (result != null) {
//                    result.up = "0";
//                    result.down = "0";
//                    result.paragraphIndex = mParagraphIndex + "";
//                    //刷新评论界面
//                    mCommentAdapter.setData(marrPoints);
//                    //通知刷新外面新闻展示界面
//                    if (mIUpdateCommentCount != null) {
//                        mIUpdateCommentCount.updateCommentCount(result);
//                    }
//                }
//                Logger.e("jigang", "+++++++comment==" + result.paragraphIndex + ",,,,type=" + point.type);
//            }
//
//            @Override
//            public void failed() {
//                Logger.e("jigang", "+++++++comment fail==");
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
//                    ToastUtil.toastShort("录音时间不能超过30秒");
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
//
//
//        DateAdapter(Context context) {
//            mContext = context;
//        }
//
//        public void setData(ArrayList<NewsDetailAdd.Point> arrPoint) {
//            marrPoint = arrPoint;
//            notifyDataSetChanged();
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
//                holder.mSpeechView = (SpeechView) convertView.findViewById(R.id.mSpeechView);
//                holder.ivHeadIcon = (SimpleDraweeView) convertView.findViewById(R.id.iv_user_icon);
//                holder.tvName = (TextViewExtend) convertView.findViewById(R.id.tv_user_name);
//                holder.ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
//                holder.tvPraiseCount = (TextViewExtend) convertView.findViewById(R.id.tv_praise_count);
//                convertView.setTag(holder);
//            } else {
//                holder = (Holder) convertView.getTag();
//            }
//            final NewsDetailAdd.Point point = marrPoint.get(position);
//            if (point.userIcon != null && !point.userIcon.equals(""))
//                holder.ivHeadIcon.setImageURI(Uri.parse(point.userIcon));
//            holder.tvName.setText(point.userName);
//            if (point.up != null) {
//                holder.tvPraiseCount.setText(point.up);
//            } else {
//                holder.tvPraiseCount.setText("0");
//            }
//
//            if (position == 0) {
//                praiseCount = Integer.parseInt(holder.tvPraiseCount.getText().toString());
//            }
//
//            holder.ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
//
//
//            if ("1".equals(point.isPraiseFlag)) {
//                holder.ivPraise.setImageResource(R.drawable.bg_praised);
//            } else {
//                holder.ivPraise.setImageResource(R.drawable.bg_normal_praise);
//            }
//
//
//            if (point.type.equals("text_paragraph")) {
//                holder.tvContent.setText(point.srcText);
//                holder.tvContent.setVisibility(View.VISIBLE);
//                holder.mSpeechView.setVisibility(View.GONE);
//            } else if (point.type.equals("text_doc")) {
//                holder.tvContent.setText(point.srcText);
//                holder.tvContent.setVisibility(View.VISIBLE);
//                holder.mSpeechView.setVisibility(View.GONE);
//            } else {
//                Logger.i("jigang", point.srcTextTime + "--adapter--" + point.srcText);
//                holder.mSpeechView.setUrl(point.srcText, false);
//                holder.mSpeechView.setDuration(point.srcTextTime);
//                holder.mSpeechView.setVisibility(View.VISIBLE);
//                holder.tvContent.setVisibility(View.GONE);
//            }
//            holder.ivPraise.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    User user = SharedPreManager.getUser(mContext);
//                    if (user == null) {
//                        LoginModeFgt loginModeFgt = new LoginModeFgt(mContext, new UserLoginListener() {
//                            @Override
//                            public void userLogin(String platform, PlatformDb platformDb) {
//
//                            }
//
//                            @Override
//                            public void userLogout() {
//
//                            }
//                        },null);
//                        loginModeFgt.show(((FragmentActivity) mContext).getSupportFragmentManager(), "loginModeFgt");
//                    } else {
//                        if ("1".equals(point.isPraiseFlag)) {
//                            ToastUtil.toastLong("您已经点过赞了");
//                        } else {
//                            holder.ivPraise.setImageResource(R.drawable.bg_praised);
//                            point.isPraiseFlag = "1";
//                            int count = 0;
//                            if (holder.tvPraiseCount != null && holder.tvPraiseCount.getText() != null && !"".equals(holder.tvPraiseCount.getText())) {
//                                count = Integer.parseInt(holder.tvPraiseCount.getText().toString());
//                            }
//                            holder.tvPraiseCount.setText(count + 1 + "");
//
//                            if (position == 0) {
//                                praiseCount = praiseCount + 1;
//                            }
//
//                            if (point.up != null) {
//                                point.up = count + 1 + "";
//                            } else {
//                                point.up = "1";
//                            }
//
//                            String uuid = DeviceInfoUtil.getUUID();
//
//                            final NewsDetailAdd.Point point_item = marrPoint.get(position);
//                            if (user != null) {
//                                PraiseRequest.Praise(mContext, user.getUserId(), user.getPlatformType(), uuid, sourceUrl, point_item.commentId, new PraiseListener() {
//                                    @Override
//                                    public void success() {
//                                        if (mIUpdatePraiseCount != null) {
//                                            mIUpdatePraiseCount.updataPraise();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void failed() {
//                                        Logger.e("jigang","praise fail~~");
//                                    }
//                                });
//                            }
//                        }
//                    }
//                }
//            });
//            return convertView;
//        }
//    }
//
//
//    class Holder {
//        SimpleDraweeView ivHeadIcon;
//        TextViewExtend tvName;
//        TextViewExtend tvContent;
//        TextViewExtend tvPraiseCount;
//        ImageView ivPraise;
//        SpeechView mSpeechView;
//    }
//
//    public interface IUpdateCommentCount {
//        void updateCommentCount(NewsDetailAdd.Point point);
//    }
//
//
//    public interface IUpdatePraiseCount {
//        void updataPraise();
//    }
//}
