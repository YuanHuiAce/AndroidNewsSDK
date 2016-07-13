package com.news.yazhidao.widget;//package com.news.yazhidao.widget;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.IBinder;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.HorizontalScrollView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.news.yazhidao.R;
//import com.news.yazhidao.database.DiggerAlbumDao;
//import com.news.yazhidao.entity.Album;
//import com.news.yazhidao.entity.BgAlbum;
//import com.news.yazhidao.entity.DiggerAlbum;
//import com.news.yazhidao.entity.User;
//import com.news.yazhidao.utils.DateUtil;
//import com.news.yazhidao.utils.TextUtil;
//import com.news.yazhidao.utils.ToastUtil;
//import com.news.yazhidao.utils.manager.SharedPreManager;
//
//import java.util.ArrayList;
//
//
///**
// * Created by ariesy on 2015/7/16.
// */
//public class AddAlbumPopupWindow extends PopupWindow {
//
//    private static final String TAG = "AddAlbumPopupWindow";
//    private Activity m_pContext;
//    private View mMenuView;
//    private ArrayList<Album> albumList;
//    private ArrayList<BgAlbum> ids;
//    private AddAlbumListener listener;
//    private Album album;
//    private DiggerAlbum mDiggerAlbum;
//    private TextView tv_cancel;
//    private TextView tv_add_album;
//    private TextView tv_new;
//    private EditText et_name;
//    private EditText et_des;
//    private HorizontalScrollView bg_album_scollView;
//    private LinearLayout bg_album_item_layout;
//    private long mFirstClickStart;
//    //判断是如何dismiss钓popupwindow ，如果点击确定，flag = true 其他为fasle
//    private boolean flag = false;
//
//    public AddAlbumPopupWindow(Activity context, AddAlbumListener listener) {
//        super(context);
//        this.listener = listener;
//        m_pContext = context;
//        loadData();
//        findHeadPortraitImageViews();
//    }
//
//    private void findHeadPortraitImageViews() {
//
//        mMenuView = View.inflate(m_pContext, R.layout.rl_add_album, null);
//        mMenuView.setFocusableInTouchMode(true);
//        mMenuView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                    dismiss();
//                    flag = false;
//                    return true;
//
//                }
//                return false;
//            }
//
//        });
//
//        tv_cancel = (TextView) mMenuView.findViewById(R.id.tv_cancel);
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideKeyboard(v);
//                flag = false;
//                dismiss();
//            }
//        });
//        tv_add_album = (TextView) mMenuView.findViewById(R.id.tv_add_album);
//        tv_new = (TextView) mMenuView.findViewById(R.id.tv_new);
//        tv_new.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideKeyboard(v);
//                String inputTitle = et_name.getText().toString();
//                if (TextUtil.isEmptyString(inputTitle)) {
//                    ToastUtil.toastShort("专辑名称不能为空!");
//                } else {
//                    album.setAlbum(inputTitle);
//                    album.setDescription(et_des.getText().toString());
//                    album.setSelected(true);
//                    m_pContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//                    /**防止多次点击*/
//                    if (System.currentTimeMillis() - mFirstClickStart <= 2000) {
//                        mFirstClickStart = System.currentTimeMillis();
//                        return;
//                    }
//                    mFirstClickStart = System.currentTimeMillis();
//                    User user = SharedPreManager.getUser(m_pContext);
//                    /**先存到本地数据库,随后发送新建专辑的数据到服务器*/
//                    String albumId = TextUtil.getDatabaseId();
//                    final DiggerAlbum diggerAlbum = new DiggerAlbum(albumId, DateUtil.getDate(), album.getDescription(), user.getUserId(), inputTitle, "0", album.getId(),DiggerAlbum.UPLOAD_NOT_DONE);
//                    album.setAlbumId(albumId);
//                    diggerAlbum.setAlbum_id(albumId);
//                    mDiggerAlbum = diggerAlbum;
//                    /**(1).把新创建好的专辑存入数据库*/
//                    final DiggerAlbumDao diggerAlbumDao = new DiggerAlbumDao(m_pContext);
//                    ArrayList<DiggerAlbum> diggerAlbums = diggerAlbumDao.existedDiggerAlbum(diggerAlbum);
//                    if (!TextUtil.isListEmpty(diggerAlbums)){
//                        ToastUtil.toastShort("亲,您已经创建过该专辑!");
//                        return;
//                    }
//                    diggerAlbumDao.insert(diggerAlbum);
//                    ToastUtil.toastShort("创建专辑成功!");
//                    flag = true;
//
//                    /**(2).通知棱镜fragment 界面刷新列表数据*/
//                    Intent intent = new Intent(LengjingFgt.ACTION_USER_REFRESH_ALBUM);
//                    m_pContext.sendBroadcast(intent);
//
////                    /**(3).把新创建好的专辑上传到服务器*/
////                    CreateDiggerAlbumRequest.createDiggerAlbum(m_pContext, diggerAlbum, new StringCallback() {
////                        @Override
////                        public int retryCount() {
////                            return 3;
////                        }
////
////                        @Override
////                        public void success(String result) {
////                            String albumId = null;
////                            if (!TextUtil.isEmptyString(albumId)) {
////                                try {
////                                    JSONObject jsonObj = new JSONObject(result);
////                                    albumId = jsonObj.optString(CreateDiggerAlbumRequest.ALBUM_ID);
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
////                                Logger.e("jigang","---upload album "+diggerAlbum);
////                                if (!TextUtil.isEmptyString(albumId)) {
////                                    //TODO 上传专辑数据到服务器成功处理
////                                    Logger.e("jigang","---上传新建专辑成功");
////                                    diggerAlbum.setIs_uploaded(DiggerAlbum.UPLOAD_DONE);
////                                    diggerAlbumDao.update(diggerAlbum);
////                                } else {
////                                    Logger.e("jigang","---上传新建专辑失败");
////                                }
////
////                            }
////                        }
////
////                        @Override
////                        public void failed(MyAppException exception) {
////                            Logger.e("jigang","---上传新建专辑失败,"+exception.getMessage());
////                        }
////                    });
//                    dismiss();
//                }
//
//            }
//        });
//
//        et_name = (EditText) mMenuView.findViewById(R.id.et_name);
//        et_des = (EditText) mMenuView.findViewById(R.id.et_des);
//
//        bg_album_scollView = (HorizontalScrollView) mMenuView.findViewById(R.id.bg_album_scollView);
//        bg_album_item_layout = (LinearLayout) mMenuView.findViewById(R.id.bg_album_item_layout);
//
//        for (int i = 0; i < ids.size(); i++) {
//            RelativeLayout layout = (RelativeLayout) View.inflate(m_pContext, R.layout.item_gridview_album2, null);
//            ImageView ivBgIcon = (ImageView) layout.findViewById(R.id.iv_bg_icon);
//            ivBgIcon.setBackgroundResource(Integer.parseInt(ids.get(i).getId()));
//            final ImageView iv_selected = (ImageView) layout.findViewById(R.id.iv_selected);
//
//            if (i == 0) {
//                iv_selected.setVisibility(View.VISIBLE);
//            }
//
//            if (ids.get(i).isSelected()) {
//                iv_selected.setVisibility(View.VISIBLE);
//            } else {
//                iv_selected.setVisibility(View.GONE);
//            }
//
//            final RelativeLayout rl_album = (RelativeLayout) layout.findViewById(R.id.rl_album);
//            rl_album.setTag(i);
//
//            rl_album.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int tag = (int) rl_album.getTag();
//                    /**当选择专辑背景时,隐藏软键盘*/
//                    hideKeyboard(et_name);
//                    hideKeyboard(et_des);
//
//                    RelativeLayout layout = (RelativeLayout) bg_album_item_layout.getChildAt(tag);
//                    ImageView iv_selected = (ImageView) layout.findViewById(R.id.iv_selected);
//                    BgAlbum ba = ids.get(tag);
//                    ba.setSelected(true);
//                    iv_selected.setVisibility(View.VISIBLE);
//                    album.setId(ba.getId());
//
//                    for (int i = 0; i < ids.size(); i++) {
//                        if (i != tag) {
//                            RelativeLayout rl_aa = (RelativeLayout) bg_album_item_layout.getChildAt(i);
//                            final ImageView iv_aa = (ImageView) rl_aa.findViewById(R.id.iv_selected);
//                            iv_aa.setVisibility(View.GONE);
//                            ids.get(i).setSelected(false);
//                        }
//                    }
//
//                }
//            });
//            bg_album_item_layout.addView(layout, i);
//        }
//
//
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
////        //实例化一个ColorDrawable颜色为半透明
////        ColorDrawable dw = new ColorDrawable(0xffffff);
////        //设置SelectPicPopupWindow弹出窗体的背景
////        this.setBackgroundDrawable(dw);
//    }
//
//    /**
//     * 获取InputMethodManager，隐藏软键盘
//     *
//     * @param view
//     */
//    private void hideKeyboard(View view) {
//        IBinder token = view.getWindowToken();
//        if (token != null) {
//            InputMethodManager im = (InputMethodManager) m_pContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }
//
//    @Override
//    public void dismiss() {
//        String inputTitle = et_name.getText().toString();
//        if (album != null && inputTitle != null && !"".equals(inputTitle) && flag) {
//            if (listener != null) {
//                listener.add(album, mDiggerAlbum);
//            }
//        }
//        super.dismiss();
//
//    }
//
//    private void loadData() {
//        ids = new ArrayList<BgAlbum>();
//
//        for (int i = 0; i < 8; i++) {
//            BgAlbum ba = new BgAlbum();
//            ba.setSelected(i == 0);
//            setId(ba, i);
//            ids.add(ba);
//        }
//
//        album = new Album();
//        album.setId(String.valueOf(R.drawable.bg_album1));
//    }
//
//    private void setId(BgAlbum ba, int i) {
//
//        switch (i) {
//            case 0:
//                ba.setId(String.valueOf(R.drawable.bg_album1));
//                break;
//
//            case 1:
//                ba.setId(String.valueOf(R.drawable.bg_album2));
//                break;
//
//            case 2:
//                ba.setId(String.valueOf(R.drawable.bg_album3));
//                break;
//
//            case 3:
//                ba.setId(String.valueOf(R.drawable.bg_album4));
//                break;
//
//            case 4:
//                ba.setId(String.valueOf(R.drawable.bg_album5));
//                break;
//
//            case 5:
//                ba.setId(String.valueOf(R.drawable.bg_album6));
//                break;
//
//            case 6:
//                ba.setId(String.valueOf(R.drawable.bg_album7));
//                break;
//
//            case 7:
//                ba.setId(String.valueOf(R.drawable.bg_album8));
//                break;
//        }
//
//    }
//
//    public interface AddAlbumListener {
//        void add(Album album, DiggerAlbum diggerAlbum);
//    }
//}
