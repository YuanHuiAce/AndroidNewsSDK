package com.news.yazhidao.widget.InputBar;//package com.news.yazhidao.widget.InputBar;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Handler;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.TextView;
//
//import com.news.yazhidao.R;
//import com.news.yazhidao.listener.UserLoginListener;
//import com.news.yazhidao.pages.LoginModeFgt;
//import com.news.yazhidao.utils.AMRAudioRecorder;
//import com.news.yazhidao.utils.FileUtils;
//import com.news.yazhidao.utils.ToastUtil;
//import com.news.yazhidao.utils.manager.SharedPreManager;
//import com.news.yazhidao.widget.TextViewExtend;
//
//import java.io.File;
//import java.util.Timer;
//
//import cn.sharesdk.framework.PlatformDb;
//
//
///**
// * Created by h.yuan on 2015/3/23.
// */
//public class InputBar extends FrameLayout {
//
//
//    private View mRootView;
//    private TextViewExtend mSwitchButton;
//    private TextViewExtend mRecordButton;
//    private EditText mTextField;
//    private InputBarType m_eCurType = InputBarType.eText;
//    private AMRAudioRecorder mRecorder;
//    private TextViewExtend mSendButton;
//    private String mFileName;
//    private boolean mKeypadIsShown = false;
//    private InputBarDelegate mDelegate;
//    private Context mContext;
//    private boolean isLong = false;// 是否滑动到取消录音的距离
//    private Context mActivity;
//    public static final int MAX_TIME = 60;// 最长录音时间
//    public static final int MIN_TIME = 1;// 最短录音时间
//    public static final int RECORD_NO = 1000; // 不在录音
//    public static final int RECORD_ING = 1100; // 正在录音
//    private static final int RECORD_ED = 12; // 完成录音
//    private int mRecordState = 0; // 录音的状态
//    private double mRecordVolume;// 麦克风获取的音量值
//
//    private Timer mCheckRecordTimer;
//    private float mfCurDuration;
//    private Handler mHandler;
//
//    public void setActivityAndHandler(Context activity, Handler handler) {
//        mActivity = activity;
//        mHandler = handler;
//    }
//
//    public void setRecordState(int recordState) {
//        mRecordState = recordState;
//    }
//
//    public float getCurDuration() {
//        return mfCurDuration;
//    }
//
//    public boolean isLong() {
//        return isLong;
//    }
//
//    public int getRecordState() {
//        return mRecordState;
//    }
//
//    public double getRecordVolume() {
//        return mRecordVolume;
//    }
//
//    public InputBar(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        mContext = context;
//        mRootView = LayoutInflater.from(context).inflate(R.layout.input_bar_view, null, false);
//        addView(mRootView);
//        findViews(mRootView);
//    }
//
//    public void setDelegate(InputBarDelegate delegate) {
//        mDelegate = delegate;
//    }
//
//    public String getmFileName() {
//        return mFileName;
//    }
//
//    public void setmFileName(String argFileName) {
//        this.mFileName = argFileName;
//    }
//
//    private void findViews(View rootView) {
//        mSwitchButton = (TextViewExtend) rootView.findViewById(R.id.switch_button);
//        mRecordButton = (TextViewExtend) rootView.findViewById(R.id.record_button);
//        mTextField = (EditText) rootView.findViewById(R.id.text_field);
//        mSendButton = (TextViewExtend) rootView.findViewById(R.id.send_button);
//        mTextField.setCursorVisible(false);//失去光标
//        mTextField.setImeOptions(EditorInfo.IME_ACTION_SEND);
//        mRecordButton.setVisibility(View.GONE);
//        mSendButton.setOnClickListener(
//                new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        inputFinished();
//                    }
//                }
//        );
//        mSwitchButton.setOnClickListener(mSwitchClickEvent);
//        mRecordButton.setOnTouchListener(mRecordTouchEvent);
//        mTextField.setOnEditorActionListener(mTextFieldListener);
//        mTextField.setOnFocusChangeListener(mTextFieldFocusChangeListener);
//        this.mFileName = System.currentTimeMillis() + "";
//    }
//
//    public void startReply() {
//        Log.i("---", "startReply");
////        mReplyData = argData;
////        mTextField.setHint("回复" + mReplyData.getM_strNickname() + ":");
////        mRecordButton.setText("按住回复" + mReplyData.getM_strNickname());
//
//        if (m_eCurType == InputBarType.eText) {
//            Log.i("---", "startReply Text");
//            mTextField.beginBatchEdit();
//        }
//    }
//
//    OnClickListener mHideKeyPadEvent = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (mKeypadIsShown) {
//                removeTextFieldFirstRespond();
//                resumeToNormal();
//            }
//        }
//    };
//
//    OnClickListener mSwitchClickEvent = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (m_eCurType == InputBarType.eRecord) {
//                mSendButton.setVisibility(VISIBLE);
//                mSendButton.setClickable(true);
//                mSwitchButton.setBackgroundResource(R.drawable.input_type_record);
//                m_eCurType = InputBarType.eText;
//                mRecordButton.setVisibility(View.GONE);
//                mTextField.setVisibility(View.VISIBLE);
//                stopRecord();
//            } else if (m_eCurType == InputBarType.eText) {
//                mSendButton.setVisibility(INVISIBLE);
//                mSendButton.setClickable(false);
//                mSwitchButton.setBackgroundResource(R.drawable.input_type_keyboard);
//                m_eCurType = InputBarType.eRecord;
//                mRecordButton.setVisibility(View.VISIBLE);
//                mTextField.setVisibility(View.GONE);
//                InputMethodManager inputManager = (InputMethodManager)
//                        mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputManager.hideSoftInputFromWindow(InputBar.this.getWindowToken(), 0);
//            }
//        }
//    };
//    Thread thread;
//    OnTouchListener mRecordTouchEvent = new OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    //登录
//                    if (SharedPreManager.getUser(mContext) == null) {
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
//                        loginModeFgt.show(((FragmentActivity)mContext).getSupportFragmentManager(),"loginModeFgt");
//                        removeTextFieldFirstRespond();
//                        return false;
//                    }
//
//                    if (thread != null) {
//                        thread.interrupt();
//                        thread = null;
//                    }
//
//                    mfCurDuration = 0;
//                    isLong = false;
//                    startRecord();
//                    thread = new Thread() {
//                        @Override
//                        public void run() {
//                            try {
//                                while (true) {
//                                    Log.i("tag", "tag111111111");
//                                    mfCurDuration += 0.1;
//                                    mRecordState = RECORD_ING;
//                                    mRecordVolume = mRecorder.getAmplitude();
//                                    mHandler.sendEmptyMessage(RECORD_ING);
//                                    if (mfCurDuration > 59.5)//如果大于30秒
//                                    {
//                                        mHandler.sendEmptyMessage(RECORD_NO);
//                                    }
//                                    thread.sleep(100);
//                                }
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    };
//                    thread.start();
////                    TimerTask task = new TimerTask() {
////                        public void run() {
////                            mfCurDuration += 0.05;
////                            mRecordState = RECORD_ING;
////                            mRecordVolume = mRecorder.getAmplitude();
////                            mHandler.sendEmptyMessage(RECORD_ING);
////                            if (mfCurDuration > 29.5)//如果大于30秒
////                            {
////                                mHandler.sendEmptyMessage(RECORD_NO);
////                            }
////                        }
////                    };
////                    mCheckRecordTimer = new Timer(true);
////                    mCheckRecordTimer.schedule(task, 0, 50);
//                    //延时100ms后执行，100ms执行一次
//                    break;
//
//                case MotionEvent.ACTION_MOVE: {
//                    if (event.getY() < -20) {
//                        isLong = true;
//                        mDelegate.cancelVoiceTipsType1();
//                    } else {
//                        isLong = false;
//                        mDelegate.cancelVoiceTipsType2();
//                    }
//                    return true;
//                }
//                case MotionEvent.ACTION_UP:
//                    mRecordVolume = 0;
//                    if (event.getY() < 0 || event.getX() < 0) {
//                        cancalRecord();
//                    } else {
//                        finishRecord();
//                    }
//                    break;
//
//                case MotionEvent.ACTION_CANCEL:
//                    cancalRecord();
//                    break;
//            }
//
//
//            return true;
//        }
//    };
//
//
//    OnFocusChangeListener mTextFieldFocusChangeListener = new OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            if (hasFocus) {
//                //登录
//                if (SharedPreManager.getUser(mContext) == null) {
//                    LoginModeFgt loginModeFgt = new LoginModeFgt(mContext, new UserLoginListener() {
//                        @Override
//                        public void userLogin(String platform, PlatformDb platformDb) {
//
//                        }
//
//                        @Override
//                        public void userLogout() {
//
//                        }
//                    },null);
//                    loginModeFgt.show(((FragmentActivity) mContext).getSupportFragmentManager(), "loginModeFgt");
//                    removeTextFieldFirstRespond();
//                    return;
//                }
//                mKeypadIsShown = true;
//                mTextField.setCursorVisible(true);//显示光标
//            }
//            if (!hasFocus) {
//                mKeypadIsShown = false;
//                mTextField.setCursorVisible(false);//失去光标
//            }
//        }
//    };
//
//    TextView.OnEditorActionListener mTextFieldListener = new TextView.OnEditorActionListener() {
//        @Override
//        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//            stopRecord();
//            if (v.getText() == null || v.getText().equals("")) {
//                return true;
//            }
//            if (actionId == EditorInfo.IME_ACTION_SEND ||
//                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                inputFinished();
//            }
//            return true;
//        }
//    };
//
//    private void startRecord() {
//        if (mRecorder != null) {
//            mRecorder.stopRecorder();
//            mRecorder = null;
//        }
//        mRecorder = new AMRAudioRecorder(mActivity, mFileName, null);
//        mRecorder.startRecorder();
//
//        if (mDelegate != null)
//            mDelegate.recordDidBegin(this);
//    }
//
//    private void stopRecord() {
//        if (mRecorder != null)
//            mRecorder.stopRecorder();
//    }
//
//    public void finishRecord() {
//        if (thread != null) {
//            thread.interrupt();
//            thread = null;
//        }
//        if (mCheckRecordTimer != null)
//            mCheckRecordTimer.cancel();
//        if (mRecorder != null)
//            mRecorder.stopRecorder();
//        if (mRecordState == RECORD_ED)
//            return;
//        mRecordState = RECORD_ED;
//        String filePath = FileUtils.getSaveDir(mContext) + File.separator + mFileName + ".amr";
//        MediaPlayer mp = MediaPlayer.create(mActivity, Uri.parse(filePath));
//        int duration = mp.getDuration();
//        mp.release();
//        if (duration < 2000) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//            builder.setTitle("啊哦");
//            builder.setMessage("说话时间太短了");
//            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            builder.create().show();
//            cancalRecord();
//
//            return;
//        }
//        if (mDelegate != null)
//            mDelegate.submitThisMessage(m_eCurType, filePath, duration);
//    }
//
//    private void cancalRecord() {
//        if (mRecorder != null)
//            mRecorder.stopRecorder();
//
//        if (mCheckRecordTimer != null)
//            mCheckRecordTimer.cancel(); //退出计时器
//
//        if (thread != null) {
//            thread.interrupt();
//            thread = null;
//        }
//        if (mDelegate != null)
//            mDelegate.recordDidCancel(this);
//
//    }
//
//    public void resumeToNormal() {
//        mTextField.setText("");
//    }
//
//    private void removeTextFieldFirstRespond() {
//        InputMethodManager inputManager = (InputMethodManager)
//                mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.hideSoftInputFromWindow(this.getWindowToken(), 0);
//        mTextField.clearFocus();
//        mKeypadIsShown = false;
//    }
//
//    private void inputFinished() {
//        String strPureText = mTextField.getText().toString().replace(" ", "").replace("\n", "");
//        if (strPureText.isEmpty()) {
//            ToastUtil.toastLong("还没写呢");
//            return;
//        }
//        removeTextFieldFirstRespond();
////        String message = mTextField.getText().toString();
//        if (mDelegate != null)
//            mDelegate.submitThisMessage(m_eCurType, strPureText, -1);
//        mTextField.setText("");
//    }
//
//}
