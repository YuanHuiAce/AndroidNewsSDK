package com.news.yazhidao.widget;//package com.news.yazhidao.widget;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.graphics.drawable.ColorDrawable;
//import android.os.IBinder;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.BaseAdapter;
//import android.widget.EditText;
//import android.widget.GridView;
//import android.widget.HorizontalScrollView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.news.yazhidao.R;
//import com.news.yazhidao.database.AlbumSubItemDao;
//import com.news.yazhidao.database.DiggerAlbumDao;
//import com.news.yazhidao.entity.Album;
//import com.news.yazhidao.entity.AlbumSubItem;
//import com.news.yazhidao.entity.DiggerAlbum;
//import com.news.yazhidao.pages.LengjingFgt;
//import com.news.yazhidao.utils.DeviceInfoUtil;
//import com.news.yazhidao.utils.Logger;
//import com.news.yazhidao.utils.TextUtil;
//import com.news.yazhidao.utils.ToastUtil;
//
//import java.util.ArrayList;
//
//
///**
// * Created by ariesy on 2015/7/16.
// */
//public class DiggerPopupWindow extends PopupWindow implements View.OnClickListener {
//
//    private static final String TAG = "DiggerPopupWindow";
//    private final boolean isShowUrlTextView;
//    private Activity m_pContext;
//    private LengjingFgt mLengJingFgt;
//    private View mMenuView;
//    private int itemCount;
//    private AlbumAdapter adapter;
//
//    private TextView tv_cancel;
//    private TextView tv_source_url;
//    private TextView tv_confirm;
//    private EditText et_content;
//    private GridView gv_album;
//    private HorizontalScrollView album_scollView;
//    private LinearLayout ll_digger_source;
//    private int position;
//    private LinearLayout album_item_layout;
//    private ArrayList<Album> albumList;
//    private int viewcount = 0;
//    private int width;
//    private int height;
//    private InputMethodManager imm;
//    /**
//     * 是否显示剪切中的数据
//     */
//    private boolean isShowClipboardContent = true;
//    private DiggerAlbum mDiggerAlbum;
//
//    public DiggerPopupWindow(LengjingFgt lengjingFgt, Activity context, String itemCount, ArrayList<Album> list, int position, boolean isShowClipboardContent, boolean isShowUrlTextView) {
//        super(context);
//        m_pContext = context;
//        this.mLengJingFgt = lengjingFgt;
//        this.position = position;
//        this.isShowClipboardContent = isShowClipboardContent;
//        this.isShowUrlTextView = isShowUrlTextView;
//        if (albumList != null) {
//            albumList.clear();
//        }
//
//        if (itemCount != null) {
//            this.itemCount = Integer.parseInt(itemCount);
//        }
//
//        imm = (InputMethodManager) m_pContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        albumList = new ArrayList<Album>();
//
//        if (list != null && list.size() > 0) {
//            for (int i = 0; i < list.size(); i++) {
//                albumList.add(list.get(i));
//            }
//        }
//
//        width = DeviceInfoUtil.getScreenWidth();
//        height = DeviceInfoUtil.getScreenHeight();
//
//        findHeadPortraitImageViews();
//
//        if (isShowClipboardContent) {
//            showClipboardDialog(context);
//        }
//        if (!isShowUrlTextView) {
//            //隐藏显示url的textview
//            ll_digger_source.setVisibility(View.GONE);
//        }
//    }
//
//    private void findHeadPortraitImageViews() {
//
//        viewcount = 0;
//        mMenuView = View.inflate(m_pContext, R.layout.popup_window_add_digger, null);
//        mMenuView.setFocusableInTouchMode(true);
//        mMenuView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                    dismiss();
//                    return true;
//
//                }
//                return false;
//            }
//
//        });
//
//        ll_digger_source = (LinearLayout) mMenuView.findViewById(R.id.ll_digger_source);
//        if (position == 2) {
//            ll_digger_source.setVisibility(View.GONE);
//        }
//
//        tv_cancel = (TextView) mMenuView.findViewById(R.id.tv_cancel);
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//
//
//        tv_source_url = (TextView) mMenuView.findViewById(R.id.tv_source_url);
//        tv_confirm = (TextView) mMenuView.findViewById(R.id.tv_confirm);
//        tv_confirm.setOnClickListener(this);
//        et_content = (EditText) mMenuView.findViewById(R.id.et_content);
//        album_scollView = (HorizontalScrollView) mMenuView.findViewById(R.id.album_scollView);
//        album_item_layout = (LinearLayout) mMenuView.findViewById(R.id.album_item_layout);
//        for (int i = 0; i < albumList.size(); i++) {
//            RelativeLayout layout = (RelativeLayout) View.inflate(m_pContext, R.layout.item_gridview_album, null);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (width * 0.47), (int) (height * 0.32));
//
//            layout.setLayoutParams(params);
//            ImageView ivBgIcon = (ImageView) layout.findViewById(R.id.iv_bg_icon);
//            String img = albumList.get(i).getId();
//            if(img != null && !img.equals("img")) {
//                ivBgIcon.setBackgroundResource(Integer.valueOf(img));
//            }
//            LetterSpacingTextView tvName = (LetterSpacingTextView) layout.findViewById(R.id.tv_name);
//            tvName.setTextSize(16);
//            final ImageView iv_selected = (ImageView) layout.findViewById(R.id.iv_selected);
//            final RelativeLayout rl_album = (RelativeLayout) layout.findViewById(R.id.rl_album);
//            rl_album.setTag(viewcount);
//
//            rl_album.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int tag = (int) rl_album.getTag();
//                    Album album = albumList.get(tag);
//                    hideKeyboard(et_content);
//                    iv_selected.setVisibility(View.VISIBLE);
//                    album.setSelected(true);
//
//                    for (int i = 0; i < albumList.size(); i++) {
//                        if (i != tag) {
//                            RelativeLayout layout = (RelativeLayout) album_item_layout.getChildAt(i);
//                            if (layout != null) {
//                                ImageView iv_selected = (ImageView) layout.findViewById(R.id.iv_selected);
//                                iv_selected.setVisibility(View.INVISIBLE);
//                                albumList.get(i).setSelected(false);
//                            }
//                        }
//                    }
//                }
//            });
//            Album album = albumList.get(i);
//
//            if (albumList != null && albumList.size() > 0) {
//                tvName.setText(album.getAlbum());
//            }
//
//            if (album.isSelected()) {
//                iv_selected.setVisibility(View.VISIBLE);
//            } else {
//                iv_selected.setVisibility(View.INVISIBLE);
//            }
//            layout.setVisibility(View.VISIBLE);
//            album_item_layout.addView(layout);
//            viewcount++;
//        }
//        RelativeLayout layout_add = (RelativeLayout) View.inflate(m_pContext, R.layout.item_gridview_album, null);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (width * 0.47), (int) (height * 0.32));//4635
//        layout_add.setLayoutParams(params);
//
//        final RelativeLayout rl_album = (RelativeLayout) layout_add.findViewById(R.id.rl_album);
//        final RelativeLayout rl_add_album = (RelativeLayout) layout_add.findViewById(R.id.rl_add_album);
//        rl_album.setVisibility(View.GONE);
//        rl_add_album.setVisibility(View.VISIBLE);
//
//        rl_add_album.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AddAlbumPopupWindow window = new AddAlbumPopupWindow(m_pContext, new AddAlbumPopupWindow.AddAlbumListener() {
//
//                    @Override
//                    public void add(Album album, DiggerAlbum diggerAlbum) {
//                        if (album != null) {
//                            //添加新专辑的时候,要默认新专辑为选中,所以要把老数据全部置为false
//                            for (Album item : albumList) {
//                                item.setSelected(false);
//                            }
//                            albumList.add(album);
//                            mDiggerAlbum = diggerAlbum;
//                            RelativeLayout layout = (RelativeLayout) View.inflate(m_pContext, R.layout.item_gridview_album, null);
//                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (width * 0.47), (int) (height * 0.32));
//
//                            layout.setLayoutParams(params);
//                            ImageView ivBgIcon = (ImageView) layout.findViewById(R.id.iv_bg_icon);
//                            ivBgIcon.setBackgroundResource(Integer.parseInt(album.getId()));
//                            LetterSpacingTextView tvName = (LetterSpacingTextView) layout.findViewById(R.id.tv_name);
//                            tvName.setTextSize(16);
//                            final ImageView iv_selected = (ImageView) layout.findViewById(R.id.iv_selected);
//                            final RelativeLayout rl_album = (RelativeLayout) layout.findViewById(R.id.rl_album);
//                            rl_album.setTag(viewcount);
//                            for (int i = 0; i < albumList.size(); i++) {
//                                if (i != viewcount) {
//                                    RelativeLayout layout_temp = (RelativeLayout) album_item_layout.getChildAt(i);
//                                    if (layout_temp != null) {
//                                        ImageView iv_selected_temp = (ImageView) layout_temp.findViewById(R.id.iv_selected);
//                                        iv_selected_temp.setVisibility(View.GONE);
//                                        albumList.get(i).setSelected(false);
//                                    }
//
//                                }
//                            }
//
//                            rl_album.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    int tag = (int) rl_album.getTag();
//                                    Album album = albumList.get(tag);
//
//                                    iv_selected.setVisibility(View.VISIBLE);
//                                    album.setSelected(true);
//
//                                    for (int i = 0; i < albumList.size(); i++) {
//                                        if (i != tag) {
//                                            albumList.get(i).setSelected(false);
//                                            RelativeLayout layout = (RelativeLayout) album_item_layout.getChildAt(i);
//                                            if (layout != null) {
//                                                ImageView iv_selected = (ImageView) layout.findViewById(R.id.iv_selected);
//                                                iv_selected.setVisibility(View.INVISIBLE);
//                                                albumList.get(i).setSelected(false);
//                                            }
//                                        }
//                                    }
//                                }
//                            });
//
//                            tvName.setText(album.getAlbum());
//
//                            if (album.isSelected()) {
//                                iv_selected.setVisibility(View.VISIBLE);
//                            } else {
//                                iv_selected.setVisibility(View.INVISIBLE);
//                            }
//
//                            imm.hideSoftInputFromWindow(mMenuView.getWindowToken(), 0);
//
//                            album_item_layout.addView(layout, viewcount);
//                            albumList.add(album);
//                            viewcount++;
//                        }
//                    }
//                });
//                window.setFocusable(true);
//                window.showAtLocation(m_pContext.getWindow().getDecorView(), Gravity.CENTER
//                        | Gravity.CENTER, 0, 0);
//            }
//        });
//        layout_add.setVisibility(View.VISIBLE);
//        album_item_layout.addView(layout_add);
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
//        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
//    }
//
//    @Override
//    public void dismiss() {
//        super.dismiss();
//    }
//
//
//    public void setDigNewsTitleAndUrl(String title, String url) {
//        et_content.setText(title);
//        et_content.setSelection(title.length());
//        tv_source_url.setText(url);
//    }
//
//    private void showClipboardDialog(final Context pContext) {
//        ClipboardManager cbm = (ClipboardManager) pContext.getSystemService(Context.CLIPBOARD_SERVICE);
//        final ClipData primaryClip = cbm.getPrimaryClip();
//        if (primaryClip != null && primaryClip.getItemCount() != 0 && !TextUtil.isEmptyString(primaryClip.getItemAt(0).getText().toString())) {
//            AlertDialog.Builder clearBuilder = new AlertDialog.Builder(m_pContext);
//            clearBuilder.setMessage("是否要使用剪切板中的数据进行挖掘");
//            clearBuilder.setTitle("温馨提示");
//            clearBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    et_content.setText(primaryClip.getItemAt(0).getText());
//            }});
//            clearBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            clearBuilder.create().show();
//        }
//
//    }
//
//    long currentTimeMillis = System.currentTimeMillis();
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.tv_confirm:
//                if (System.currentTimeMillis() - currentTimeMillis <= 1500) {
//                    currentTimeMillis = System.currentTimeMillis();
//                    return;
//                }
//                currentTimeMillis = System.currentTimeMillis();
//                final String inputTitle = et_content.getText().toString();
//                final String inputUrl = tv_source_url.getText().toString();
//                if (TextUtil.isEmptyString(inputTitle)) {
//                    ToastUtil.toastShort("亲,挖掘内容不能为空!");
//                } else {
//                    //判断用户选择的是哪一个专辑
//                    int index = 0;
//                    for (; index < albumList.size(); index++) {
//                        if (albumList.get(index).isSelected()) {
//                            break;
//                        }
//                    }
//                    //通知外面的LengJingFgt 数据发生了变化
//                    //TODO 开始挖掘
//                    final int finalIndex = index;
//                    final Album album = albumList.get(finalIndex);
//                    /**修改数据库,随后开始挖掘*/
//
//                    //如果mDiggerAlbum 为null,则说明用户选择的是老专辑,否则是新建专辑
//                    if (mDiggerAlbum == null) {
//                        DiggerAlbum diggerAlbum = mLengJingFgt.getDiggerAlbums().get(finalIndex);
//                        mDiggerAlbum = diggerAlbum;
//                    }
//                    /**(1).修改专辑数据*/
//                    final DiggerAlbumDao diggerAlbumDao = new DiggerAlbumDao(m_pContext);
//                    final AlbumSubItemDao albumSubItemDao = new AlbumSubItemDao(m_pContext);
//                    mDiggerAlbum.setAlbum_news_count(((albumSubItemDao.queryByAlbumId(mDiggerAlbum.getAlbum_id()).size() + 1) + ""));
//                    diggerAlbumDao.update(mDiggerAlbum);
//                    /**(2).修改挖掘新闻数据*/
//                    final AlbumSubItem albumSubItem = new AlbumSubItem(inputTitle, inputUrl);
//                    albumSubItem.setDiggerAlbum(mDiggerAlbum);
//                    AlbumSubItem existItem = albumSubItemDao.queryByTitleAndUrl(inputTitle, inputUrl);
//                    if (existItem == null) {
//                        Logger.e("jigang","create time ==" + albumSubItem.getCreateTime());
//                        albumSubItemDao.insert(albumSubItem);
//                    } else {
//                        ToastUtil.toastShort("您已挖掘过该新闻!");
//                    }
//                    mLengJingFgt.updateAlbumList(finalIndex, mDiggerAlbum);
////                    /**(3).开始向服务器请求挖掘数据*/
////                    DigNewsRequest.digNews(m_pContext, album.getAlbumId(), inputTitle, inputUrl, new StringCallback() {
////                        @Override
////                        public int retryCount() {
////                            return 3;
////                        }
////
////                        @Override
////                        public void success(String result) {
////                            if (!TextUtils.isEmpty(result)) {
////                                Logger.e("jigang", "---向服务器请求挖掘成功!" + result);
////                                albumSubItem.setIs_uploaded(albumSubItem.UPLOAD_DONE);
////                                albumSubItemDao.update(albumSubItem);
////                            }
////                        }
////
////                        @Override
////                        public void failed(MyAppException exception) {
////                            Logger.e("jigang", "---向服务器请求挖掘失败!" + exception.getMessage());
////                        }
////                    });
//                    DiggerPopupWindow.this.dismiss();
//                }
//                break;
//        }
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
//    class AlbumAdapter extends BaseAdapter {
//        Context mContext;
//
//        public AlbumAdapter(Context context) {
//            mContext = context;
//        }
//
//        @Override
//        public int getCount() {
//            return albumList.size() + 1;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            final Holder holder;
//            if (convertView == null) {
//                holder = new Holder();
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview_album, null, false);
//                holder.ivBgIcon = (ImageView) convertView.findViewById(R.id.iv_bg_icon);
//                holder.tvName = (LetterSpacingTextView) convertView.findViewById(R.id.tv_name);
//                holder.tvName.setFontSpacing(5);
//                holder.tvName.setTextSize(16);
//                holder.iv_selected = (ImageView) convertView.findViewById(R.id.iv_selected);
//                holder.rl_album = (RelativeLayout) convertView.findViewById(R.id.rl_album);
//                holder.rl_add_album = (RelativeLayout) convertView.findViewById(R.id.rl_add_album);
//                holder.iv_add_album = (ImageView) convertView.findViewById(R.id.iv_add_album);
//                convertView.setTag(holder);
//            } else {
//                holder = (Holder) convertView.getTag();
//            }
//            holder.ivBgIcon.setBackgroundResource(Integer.valueOf(albumList.get(position).getId()));
//            holder.rl_album.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Album album = albumList.get(position);
//
//                    boolean isSelected = album.isSelected();
//                    if (isSelected) {
//                        holder.iv_selected.setVisibility(View.INVISIBLE);
//                        album.setSelected(false);
//                    } else {
//                        holder.iv_selected.setVisibility(View.VISIBLE);
//                        album.setSelected(true);
//                    }
//
//                    for (int i = 0; i < albumList.size(); i++) {
//                        if (i != position) {
//                            albumList.get(i).setSelected(false);
//                        }
//                    }
//                }
//            });
//
//            holder.rl_add_album.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AddAlbumPopupWindow window = new AddAlbumPopupWindow(m_pContext, new AddAlbumPopupWindow.AddAlbumListener() {
//
//                        @Override
//                        public void add(Album album, DiggerAlbum diggerAlbum) {
//                            if (album != null) {
//                                //添加新专辑的时候,要默认新专辑为选中,所以要把老数据全部置为false
//                                for (Album item : albumList) {
//                                    item.setSelected(false);
//                                }
//                                albumList.add(album);
//                                mDiggerAlbum = diggerAlbum;
//                                adapter.notifyDataSetChanged();
//                            }
//                        }
//                    });
//                    window.setFocusable(true);
//                    window.showAtLocation(m_pContext.getWindow().getDecorView(), Gravity.CENTER
//                            | Gravity.CENTER, 0, 0);
//                }
//            });
//            if (position == albumList.size()) {
//
//                holder.rl_album.setVisibility(View.GONE);
//                holder.rl_add_album.setVisibility(View.VISIBLE);
//
//            } else {
//
//                holder.rl_album.setVisibility(View.VISIBLE);
//                holder.rl_add_album.setVisibility(View.GONE);
//
//                Album album = albumList.get(position);
//
//                if (albumList != null && albumList.size() > 0) {
//                    holder.tvName.setText(album.getAlbum());
//                }
//
//                if (album.isSelected()) {
//                    holder.iv_selected.setVisibility(View.VISIBLE);
//                } else {
//                    holder.iv_selected.setVisibility(View.INVISIBLE);
//                }
//            }
//            return convertView;
//        }
//    }
//
//    class Holder {
//        ImageView ivBgIcon;
//        LetterSpacingTextView tvName;
//        ImageView iv_selected;
//        RelativeLayout rl_album;
//        RelativeLayout rl_add_album;
//        ImageView iv_add_album;
//    }
//}
