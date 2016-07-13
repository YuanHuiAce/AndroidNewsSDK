package com.news.yazhidao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.news.yazhidao.database.AlbumSubItemDao;
import com.news.yazhidao.database.DiggerAlbumDao;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.StringCallback;
import com.news.yazhidao.net.request.CreateDiggerAlbumRequest;
import com.news.yazhidao.net.request.DigNewsRequest;
import com.news.yazhidao.net.request.FetchAlbumSubItemsRequest;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fengjigang on 15/8/11.
 * 监听网络状态变化
 */
public class NetworkStateChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            if(DeviceInfoUtil.isNetworkConnected(context)){
                //查询数据库,看是否有新建的专辑没有上传成功,如果有则上传
                // FIXME: 15/11/23 以后上传到服务器
//                reUploadAlbum(context);
//                reUPloadAlbumSubItem(context);
            }
        }
    }

    /**
     * 刷新挖掘数据
     * @param pContext
     * @param pSubItems
     */
    private void refreshAlbumSubItems(Context pContext, ArrayList<AlbumSubItem> pSubItems) {
        final AlbumSubItemDao pDao = new AlbumSubItemDao(pContext);
        for (AlbumSubItem item: pSubItems){
            final DiggerAlbum album = item.getDiggerAlbum();
            String album_id = album.getAlbum_id();
            FetchAlbumSubItemsRequest.fetchAlbumSubItems(pContext, album_id, true, new JsonCallback<ArrayList<AlbumSubItem>>() {
                        @Override
                        public int retryCount() {
                            return 3;
                        }
                        @Override
                        public void success(ArrayList<AlbumSubItem> subItems) {
                            //存数据库
                            if (!TextUtil.isListEmpty(subItems)) {
                                for (AlbumSubItem item : subItems) {
                                    item.setDiggerAlbum(album);
                                    Logger.e("jigang","----update item ="+item);
                                    pDao.update(item);
                                }
                            }
                        }

                        @Override
                        public void failed(MyAppException exception) {
                            Logger.e("jigang","----refreshAlbumSubItems fail "+exception.getMessage());
                        }
                    }
            );
        }

        }
    /**
     * 重新上传挖掘内容
     * @param pContext
     */
    private void reUPloadAlbumSubItem(final Context pContext) {
        final AlbumSubItemDao dao =new AlbumSubItemDao(pContext);
        final ArrayList<AlbumSubItem> subItems = dao.queryNotUpload();
        for (int i=0;i < subItems.size(); i++){
            final AlbumSubItem albumSubItem = subItems.get(i);
            final int index = i;
            DigNewsRequest.digNews(pContext, albumSubItem.getDiggerAlbum().getAlbum_id(), albumSubItem.getSearch_key(), albumSubItem.getSearch_url(), new StringCallback() {
                @Override
                public int retryCount() {
                    return 3;
                }

                @Override
                public void success(String result) {
                    if (!TextUtils.isEmpty(result)) {

                        if (index == subItems.size()-1){
                            //通知AlbumListAty 刷新数据
//                            Intent intent = new Intent(AlbumListAty.ACTION_REFRESH_DATA);
//                            pContext.getApplicationContext().sendBroadcast(intent);
//                            refreshAlbumSubItems(pContext,subItems);
                            Logger.e("jigang","---通知刷新数据了");
                        }
                        Logger.e("jigang", "---重新向服务器请求挖掘成功!" + result);
                        albumSubItem.setIs_uploaded(AlbumSubItem.UPLOAD_DONE);
                        dao.update(albumSubItem);
                    }
                }

                @Override
                public void failed(MyAppException exception) {
                    Logger.e("jigang", "---重新向服务器请求挖掘失败!" + exception.getMessage());
                }
            });
        }
    }

    /**
     * 重新上传新建专辑列表
     * @param pContext
     */
    public void reUploadAlbum(Context pContext){
        final DiggerAlbumDao dao = new DiggerAlbumDao(pContext);
        ArrayList<DiggerAlbum> albums = dao.queryNotUpload();
        for(final DiggerAlbum pDiggerAlbum: albums){
            /**把新创建好的专辑存入数据库*/
            CreateDiggerAlbumRequest.createDiggerAlbum(pContext, pDiggerAlbum, new StringCallback() {
                @Override
                public int retryCount() {
                    return 3;
                }

                @Override
                public void success(String result) {
                    String albumId = null;
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            JSONObject jsonObj = new JSONObject(result);
                            albumId = jsonObj.optString(CreateDiggerAlbumRequest.ALBUM_ID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!TextUtils.isEmpty(albumId)) {
                            pDiggerAlbum.setIs_uploaded(DiggerAlbum.UPLOAD_DONE);
                            dao.update(pDiggerAlbum);
                            Logger.e("jigang", "---重新上传新建专辑成功!");
                        } else {
                            Logger.e("jigang", "---重新上传新建专辑失败!");
                        }

                    }
                }

                @Override
                public void failed(MyAppException exception) {
                    Logger.e("jigang", "---重新上传新建专辑失败," + exception.getMessage());
                }
            });
        }

    }
}
